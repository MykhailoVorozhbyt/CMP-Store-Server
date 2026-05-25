package org.cmp.store.presentation

import com.feature.authentication.domain.usecases.GetCurrentUserIdUseCase
import com.store.test.BaseViewModelTest
import com.store.test.fakes.FakeCustomerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import com.store.core.presentation.navigation.Screen
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Lives in `jvmTest` on purpose: [AppViewModel]'s init reads `getPlatform().isMobile`, and the
 * mobile branch initializes the Google/Firebase auth provider. On the JVM `isMobile` is false, so
 * the branch under test (auth state -> Auth vs HomeGraph) runs in isolation and deterministically.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest : BaseViewModelTest() {

    @Test
    fun start_destination_is_HomeGraph_when_current_user_id_present() = runVmTest {
        val viewModel = buildViewModel(currentUserId = "user-123")

        advanceUntilIdle()

        val state = viewModel.viewDataState.value
        assertTrue(state.appReady, "appReady should be flipped on once init completes")
        assertIs<Screen.HomeGraph>(state.startDestination)
    }

    @Test
    fun start_destination_is_Auth_when_current_user_id_absent() = runVmTest {
        val viewModel = buildViewModel(currentUserId = null)

        advanceUntilIdle()

        val state = viewModel.viewDataState.value
        assertTrue(state.appReady, "appReady should be flipped on once init completes")
        assertEquals(Screen.Auth, state.startDestination)
    }

    private fun buildViewModel(currentUserId: String?): AppViewModel {
        val customerRepository = FakeCustomerRepository()
        customerRepository.currentUserId = currentUserId
        return AppViewModel(
            getCurrentUserIdUseCase = GetCurrentUserIdUseCase(customerRepository),
            dispatchers = dispatchers,
        )
    }
}
