package com.store.feature.profile

import app.cash.turbine.test
import com.store.component.customer.model.CustomerError
import com.store.component.customer.model.CustomerProfileRequest
import com.store.component.customer.usecase.GetCachedCustomerUseCase
import com.store.component.customer.usecase.ReadCustomerUseCase
import com.store.component.customer.usecase.UpdateCustomerUseCase
import com.store.core.domain.ApiResult
import com.store.core.domain.DataError
import com.store.core.presentation.core.NotificationType
import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.core.presentation.ui.base.UiEvent
import com.store.core.presentation.utils.ScreenState
import com.store.feature.profile.validator.ProfileValidator
import com.store.feature.profile.view_data.Country
import com.store.feature.profile.view_data.ProfileFields
import com.store.feature.profile.view_data.ProfileInitializer
import com.store.feature.profile.view_data.ProfileViewAction
import com.store.test.BaseViewModelTest
import com.store.test.fakes.FakeCustomerRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest : BaseViewModelTest() {

    private val repository = FakeCustomerRepository()

    @Test
    fun initial_state_is_loading() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()

        assertEquals(ScreenState.Loading, viewModel.viewDataState.value.screenState)
        assertFalse(viewModel.viewDataState.value.updateEnabled)
    }

    @Test
    fun load_success_fills_form_and_enables_update() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.viewDataState.value
        assertEquals(ScreenState.Success, state.screenState)
        assertEquals("Stefan", state.form.firstName.input)
        assertEquals("customer@mail.com", state.form.email.input)
        assertEquals("11000", state.form.postalCode.input)
        assertEquals("123456789", state.form.phone.number.input)
        assertEquals(Country.Ukraine, state.form.phone.country)
        assertTrue(state.updateEnabled)
    }

    @Test
    fun load_with_empty_fields_keeps_update_enabled_without_errors() = runVmTest {
        repository.customerResult = ApiResult.Success(
            customer.copy(city = null, postalCode = null, address = null, phoneNumber = null)
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.viewDataState.value
        assertEquals(Country.Default, state.form.phone.country)
        assertTrue(state.updateEnabled)
        assertFalse(state.form.city.error.hasError)
    }

    @Test
    fun load_not_found_shows_error_state() = runVmTest {
        repository.customerResult = ApiResult.Error(CustomerError.NotFound)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertEquals(ScreenState.Error, viewModel.viewDataState.value.screenState)
    }

    @Test
    fun load_no_internet_result_shows_no_internet_state() = runVmTest {
        repository.customerResult = ApiResult.NoInternet
        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertEquals(ScreenState.NoInternetConnection, viewModel.viewDataState.value.screenState)
    }

    @Test
    fun cached_customer_stays_visible_when_refresh_fails() = runVmTest {
        repository.cachedCustomer.value = customer
        repository.customerResult = ApiResult.NoInternet
        val viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.viewDataState.value
        assertEquals(ScreenState.Success, state.screenState)
        assertEquals("Stefan", state.form.firstName.input)
        assertEquals(1, repository.readCount)
    }

    @Test
    fun fresh_customer_replaces_cached_one() = runVmTest {
        repository.cachedCustomer.value = customer.copy(firstName = "Cached")
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertEquals("Stefan", viewModel.viewDataState.value.form.firstName.input)
    }

    @Test
    fun update_with_too_short_first_name_shows_error_and_skips_request() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onViewAction(InputFieldChanged("St", ProfileFields.FirstName))
        advanceUntilIdle()
        assertTrue(viewModel.viewDataState.value.updateEnabled)

        viewModel.onViewAction(ProfileViewAction.UpdateClicked)
        advanceUntilIdle()

        val state = viewModel.viewDataState.value
        assertTrue(state.form.firstName.error.hasError)
        assertNull(repository.lastProfileRequest)
        assertFalse(state.isUpdating)
    }

    @Test
    fun update_sends_only_profile_fields() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onViewAction(InputFieldChanged("Mykhailo", ProfileFields.FirstName))
        advanceUntilIdle()
        viewModel.onViewAction(ProfileViewAction.UpdateClicked)
        advanceUntilIdle()

        assertEquals(
            CustomerProfileRequest(
                firstName = "Mykhailo",
                lastName = customer.lastName,
                city = customer.city,
                postalCode = 11000,
                address = customer.address,
                phoneNumber = PhoneNumber(dialCode = 380, number = "123456789"),
            ),
            repository.lastProfileRequest,
        )
        assertNull(repository.lastUpdatedCustomer)
        assertFalse(viewModel.viewDataState.value.isUpdating)
    }

    @Test
    fun update_success_shows_success_message() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()
        advanceUntilIdle()
        changeFirstName(viewModel)

        viewModel.uiEvents.test {
            viewModel.onViewAction(ProfileViewAction.UpdateClicked)
            advanceUntilIdle()

            val event = awaitItem()
            assertIs<UiEvent.ShowMessage>(event)
            assertEquals(NotificationType.SUCCESS, event.data.type)
            assertEquals(FakeProfileMessageHandler.UPDATE_SUCCESS, event.data.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun update_error_shows_error_message() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        repository.updateResult = ApiResult.Error(CustomerError.Common(DataError.Server))
        val viewModel = buildViewModel()
        advanceUntilIdle()
        changeFirstName(viewModel)

        viewModel.uiEvents.test {
            viewModel.onViewAction(ProfileViewAction.UpdateClicked)
            advanceUntilIdle()

            val event = awaitItem()
            assertIs<UiEvent.ShowMessage>(event)
            assertEquals(NotificationType.ERROR, event.data.type)
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(viewModel.viewDataState.value.isUpdating)
    }

    @Test
    fun update_no_internet_shows_no_internet_message() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        repository.updateResult = ApiResult.NoInternet
        val viewModel = buildViewModel()
        advanceUntilIdle()
        changeFirstName(viewModel)

        viewModel.uiEvents.test {
            viewModel.onViewAction(ProfileViewAction.UpdateClicked)
            advanceUntilIdle()

            val event = awaitItem()
            assertIs<UiEvent.ShowMessage>(event)
            assertEquals(NotificationType.ERROR, event.data.type)
            assertEquals(FakeProfileMessageHandler.NO_INTERNET, event.data.message)
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(viewModel.viewDataState.value.isUpdating)
    }

    @Test
    fun update_with_empty_required_field_shows_required_error() = runVmTest {
        repository.customerResult = ApiResult.Success(customer.copy(city = null))
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onViewAction(ProfileViewAction.UpdateClicked)
        advanceUntilIdle()

        val form = viewModel.viewDataState.value.form
        assertTrue(form.city.error.hasError)
        assertFalse(form.firstName.error.hasError)
        assertNull(repository.lastProfileRequest)
    }

    @Test
    fun update_click_while_request_is_running_sends_one_request() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        repository.updateGate = CompletableDeferred()
        val viewModel = buildViewModel()
        advanceUntilIdle()
        changeFirstName(viewModel)

        viewModel.onViewAction(ProfileViewAction.UpdateClicked)
        advanceUntilIdle()
        assertTrue(viewModel.viewDataState.value.isUpdating)
        assertFalse(viewModel.viewDataState.value.updateEnabled)

        viewModel.onViewAction(ProfileViewAction.CountryPickerDismissed)
        viewModel.onViewAction(ProfileViewAction.UpdateClicked)
        advanceUntilIdle()
        assertEquals(1, repository.profileRequestCount)

        repository.updateGate?.complete(Unit)
        advanceUntilIdle()
        assertFalse(viewModel.viewDataState.value.isUpdating)
        assertEquals(1, repository.profileRequestCount)
    }

    @Test
    fun country_picker_confirm_changes_dial_code() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onViewAction(ProfileViewAction.CountryPickerClicked)
        advanceUntilIdle()
        assertEquals(Country.Ukraine, viewModel.viewDataState.value.countryPicker?.selected)

        viewModel.onViewAction(ProfileViewAction.CountryHighlighted(Country.Usa))
        advanceUntilIdle()
        viewModel.onViewAction(ProfileViewAction.CountryConfirmed)
        advanceUntilIdle()

        val state = viewModel.viewDataState.value
        assertEquals(Country.Usa, state.form.phone.country)
        assertNull(state.countryPicker)
    }

    @Test
    fun country_picker_dismiss_keeps_previous_country() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onViewAction(ProfileViewAction.CountryPickerClicked)
        advanceUntilIdle()
        viewModel.onViewAction(ProfileViewAction.CountryHighlighted(Country.Usa))
        advanceUntilIdle()
        viewModel.onViewAction(ProfileViewAction.CountryPickerDismissed)
        advanceUntilIdle()

        val state = viewModel.viewDataState.value
        assertEquals(Country.Ukraine, state.form.phone.country)
        assertNull(state.countryPicker)
    }

    @Test
    fun country_search_without_match_disables_confirm() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onViewAction(ProfileViewAction.CountryPickerClicked)
        advanceUntilIdle()
        viewModel.onViewAction(InputFieldChanged("0001", ProfileFields.DialCodeSearch))
        advanceUntilIdle()

        val picker = viewModel.viewDataState.value.countryPicker!!
        assertTrue(picker.countries.isEmpty())
        assertFalse(picker.confirmEnabled)
    }

    @Test
    fun country_search_filters_by_dial_code_prefix() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onViewAction(ProfileViewAction.CountryPickerClicked)
        advanceUntilIdle()
        viewModel.onViewAction(InputFieldChanged("38", ProfileFields.DialCodeSearch))
        advanceUntilIdle()

        assertEquals(listOf(Country.Ukraine), viewModel.viewDataState.value.countryPicker?.countries)
    }

    @Test
    fun update_without_changes_shows_info_and_skips_request() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.uiEvents.test {
            viewModel.onViewAction(ProfileViewAction.UpdateClicked)
            advanceUntilIdle()

            val event = awaitItem()
            assertIs<UiEvent.ShowMessage>(event)
            assertEquals(NotificationType.INFO, event.data.type)
            assertEquals(FakeProfileMessageHandler.NOTHING_CHANGED, event.data.message)
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(repository.lastProfileRequest)
        assertFalse(viewModel.viewDataState.value.isUpdating)
    }

    @Test
    fun update_after_successful_save_without_new_changes_skips_request() = runVmTest {
        repository.customerResult = ApiResult.Success(customer)
        val viewModel = buildViewModel()
        advanceUntilIdle()
        changeFirstName(viewModel)

        viewModel.onViewAction(ProfileViewAction.UpdateClicked)
        advanceUntilIdle()
        viewModel.onViewAction(ProfileViewAction.CountryPickerDismissed)
        viewModel.onViewAction(ProfileViewAction.UpdateClicked)
        advanceUntilIdle()

        assertEquals(1, repository.profileRequestCount)
    }

    private fun TestScope.changeFirstName(viewModel: ProfileViewModel) {
        viewModel.onViewAction(InputFieldChanged("Mykhailo", ProfileFields.FirstName))
        advanceUntilIdle()
    }

    private fun buildViewModel(): ProfileViewModel {
        val validator = ProfileValidator()
        val messageHandler = FakeProfileMessageHandler()
        return ProfileViewModel(
            initializer = ProfileInitializer(ReadCustomerUseCase(repository), GetCachedCustomerUseCase(repository), validator),
            updateCustomerUseCase = UpdateCustomerUseCase(repository),
            validator = validator,
            messageHandler = messageHandler,
            dispatchers = dispatchers,
        )
    }

    private companion object {
        val customer = Customer(
            id = "customer-1",
            firstName = "Stefan",
            lastName = "Jovanovic",
            email = "customer@mail.com",
            city = "Kyiv",
            postalCode = 11000,
            address = "Khreshchatyk 11",
            phoneNumber = PhoneNumber(dialCode = 380, number = "123456789"),
            cart = listOf(CartItem(id = "item-1", productId = "product-1", quantity = 2)),
        )
    }
}
