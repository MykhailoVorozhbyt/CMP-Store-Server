# Graph Report - CMP-Store-Server  (2026-08-09)

## Corpus Check
- 401 files · ~58,553 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 2217 nodes · 3372 edges · 281 communities (199 shown, 82 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 436 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `85eed5d7`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Auth Mappers & Repository Tests
- Validatable Form Fields
- ViewModel Tests & Email Validator
- Desktop Google Sign-In
- Auth & Customer DTOs
- Auth Domain Use Cases
- Android Gradle Convention Plugins
- Repository Implementations
- iOS App Entry Points
- Feature Module Plugins
- Network Error Taxonomy & Fakes
- Ktor Server App & Routes
- ApiResult & Safe API Call
- Coroutine Launchers
- Ktor Auth Data Sources
- Project Docs & CI Pipeline
- RequestState Wrapper
- Customer DAO & Update Use Case
- StoreTheme Dimens & Strings
- Product DAO
- EventBus
- Action Handler Scopes
- Multiplatform Logger
- Google Sign-In Errors
- Auth Session DAO & Tokens
- View Action Handlers
- BaseActionHandleViewModel
- Platform Expect/Actual
- Compose & Core Module Plugins
- String Validators
- Product Data Source Tests
- Product DAO Tests
- Keyed Input Field Changes
- ViewAction Definitions
- Navigation Screen Keys
- String Sanitization Utils
- Authentication View Data
- Customer DAO Tests
- Android Library Gradle Base
- Gradle Source Set Helpers
- UiEvent Emitter & HomeGraph VM
- Custom Drawer UI
- Auth Service Tests
- Gradle Project Extensions
- Notification & Message Events
- Social Sign-In Block
- Store Colors Palette
- Snackbar & HomeGraph Screen
- Field State Validators
- Secure Storage & Session
- Customer Data Source
- Database Factory & Test Support
- Navigation State
- UiEvent Types
- Product Category Use Cases
- HomeGraphMockPreview
- StoreModulePlugin
- MainViewController
- navKey
- SignInResult
- DefaultCustomerRepositoryTest
- .renderAuth
- BottomBar
- Products
- StoreApp
- App
- DistinctFlowImpl
- PreviewTheme
- .invoke
- .apply
- AuthenticationContent
- UiText
- EmailPatternValidator
- FakeAuthDataSource
- .buildInitial
- InputFieldViewData
- Measurement
- AbstractInputFieldValidator
- SignInFailureHandler
- RootNavigator
- .launch
- StoreAndroidAppPlugin
- data/mappers/CustomerMappers.kt
- JwtParserTest
- ProductDataSource
- customer/mappers/CustomerMappers.kt
- FakeCustomerRepository
- IosSecureStorageExt.kt
- ProjectExtensions.kt
- AppStrings
- Currency
- OnCloseBottomSheet
- OnSystemBackClick.kt
- GetCurrentUserIdUseCase
- SocialMediaBlockViewData
- .authCredentialFixture
- SignInResult
- AuthenticationViewAction
- Greeting
- FakeCustomerRepository
- PasswordHasher
- PasswordHasherTest
- CoreDataModulePlugin
- CoreDomainModulePlugin
- .apply
- CoreSecurityModulePlugin
- ComposeMultiplatformConventionPlugin
- KotlinMultiplatformConventionPlugin
- StoreTheme
- getAndroidSdkVersions
- .apply
- FeatureAuthenticationModulePlugin
- SocialMediaViewAction
- BuildTypeName
- JwtParser
- .invoke
- RefreshOutcome
- .invoke
- .invoke
- configureDesktopApplication
- .invoke
- gradlew
- auth/mappers/AuthMappers.kt
- ComposeAppCommonTest
- Module
- GoogleOAuthConfig
- alias
- AuthCredentialTable
- AuthSessionTable
- CartItemTable
- CustomerTable
- PhoneNumberTable
- ProductTable
- DaoModuleTest
- ServiceModuleTest
- SharedCommonTest
- AppDispatchers.kt
- Alpha.kt
- model/CartItemDto.kt
- model/CustomerDto.kt
- PhoneNumberDto.kt
- AuthenticationTags.kt
- ProductCurrency.kt
- ProductMeasurements.kt
- AdaptivePreview.kt
- CoroutineScope
- dto/CustomerDto.kt
- NavKey
- CustomDrawerState
- .apply
- AuthenticationValidator
- dto/CustomerDto.kt
- toDto
- .authorize
- data/mappers/AuthMappers.kt
- .apply
- AuthDto.kt
- LocalCallbackServer
- RefreshOutcome
- HomeGraphViewAction
- StoreColorsPalette
- NotificationType
- AuthenticationViewAction
- .invoke
- UiText
- AuthUserRequest
- EmailDomainValidator
- StatusPages.kt
- PasswordField
- PlatformRepositoryProvider
- Greeting
- AppViewModelTest
- StoreTheme
- AuthenticationContent
- RateLimitGroup
- configureRouting
- FeatureAuthenticationModulePlugin
- desktopApp
- .authCredentialFixture
- AppViewModel
- Resources
- FakeCustomerDataSource
- ScopeProvider
- .apply
- RacingDaos.kt
- Logger
- .invoke
- HomeGraphViewAction
- installAuthentication
- installKoin
- ApiException
- dto/CustomerDto.kt
- AuthRequestDto.kt
- dto/CartItemDto.kt
- CoroutineScope
- CoroutineStart
- Job
- MutableStateFlow
- SharedFlow
- StateFlow
- VD
- DrawableResource
- Composable
- MutableInteractionSource
- MutableState
- R
- AuthRequestDto
- CustomerDto
- AuthRequestDto
- AuthRequestDto
- ApplicationPlugin
- KoinKtorApplication

## God Nodes (most connected - your core abstractions)
1. `NetworkError` - 75 edges
2. `AuthRequest` - 33 edges
3. `ModuleName` - 32 edges
4. `Product` - 31 edges
5. `Customer` - 30 edges
6. `ApiResult` - 29 edges
7. `BaseActionHandleViewModelTest` - 26 edges
8. `ModulePath` - 24 edges
9. `testDaoDatabase()` - 24 edges
10. `BaseActionHandleViewModel` - 23 edges

## Surprising Connections (you probably didn't know these)
- `Gradle convention plugins (build-logic)` --semantically_similar_to--> `gradle-setup composite action`  [INFERRED] [semantically similar]
  CLAUDE.md → .github/actions/gradle-setup/action.yml
- `Build & run instructions (Android/Desktop/Server/iOS)` --semantically_similar_to--> `CI workflow`  [INFERRED] [semantically similar]
  README.md → .github/workflows/ci.yml
- `ManualEmail()` --calls--> `StoreOutlinedButton()`  [INFERRED]
  feature/authentication/presentation/src/commonMain/kotlin/com/feature/authentication/presentation/AuthenticationScreen.kt → core/presentation/src/commonMain/kotlin/com/store/core/presentation/ui/components/button/StoreOutlinedButton.kt
- `GoogleButton()` --calls--> `StoreOutlinedButton()`  [INFERRED]
  feature/authentication/presentation/src/commonMain/kotlin/com/feature/authentication/presentation/social_media/ui/SocialMediaBlock.kt → core/presentation/src/commonMain/kotlin/com/store/core/presentation/ui/components/button/StoreOutlinedButton.kt
- `ManualEmail()` --calls--> `PasswordField()`  [INFERRED]
  feature/authentication/presentation/src/commonMain/kotlin/com/feature/authentication/presentation/AuthenticationScreen.kt → core/presentation/src/commonMain/kotlin/com/store/core/presentation/ui/components/input/StorePasswordField.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **CI pipeline: shared setup action + two parallel test jobs + artifact upload** — _github_workflows_ci_ci_workflow, _github_workflows_ci_test_app, _github_workflows_ci_test_server, _github_actions_gradle_setup_action_gradle_setup, _github_workflows_ci_test_report_artifacts [EXTRACTED 1.00]
- **MVI-style unidirectional flow in BaseActionHandleViewModel** — claude_baseactionhandleviewmodel, claude_viewaction, claude_uievent, claude_koin_di [EXTRACTED 1.00]
- **Customer persistence stack: repository, Ktor server, in-memory storage, Firebase replacement intent** — claude_customerrepositoryimpl, claude_ktor_server, claude_customerstorage, readme_firebase_replacement_rationale [INFERRED 0.85]

## Communities (281 total, 82 thin omitted)

### Community 0 - "Auth Mappers & Repository Tests"
Cohesion: 0.19
Nodes (7): AuthCredentialDao, AuthCredentialDaoImpl, AuthCredential, AuthProvider, FACEBOOK, GOOGLE, MANUAL

### Community 2 - "ViewModel Tests & Email Validator"
Cohesion: 0.17
Nodes (6): BaseActionHandleViewModelTest, TestMessageEventData, TestPageEvent, TestViewAction, TestViewData, BaseViewModelTest

### Community 4 - "Auth & Customer DTOs"
Cohesion: 0.15
Nodes (3): AuthSessionDao, AuthSessionDaoImpl, AuthSessionDto

### Community 5 - "Auth Domain Use Cases"
Cohesion: 0.21
Nodes (5): SessionTokens, AuthSessionService, AuthSessionServiceImpl, AuthProvider, MintedSession

### Community 6 - "Android Gradle Convention Plugins"
Cohesion: 0.06
Nodes (35): Action, ApplicationExtension, configureCompileOptions(), AndroidSdkVersions, getAndroidSdkVersions(), getDefaultVersionName(), getVersionCode(), android() (+27 more)

### Community 7 - "Repository Implementations"
Cohesion: 0.15
Nodes (3): RefreshTokenDao, RefreshTokenDaoImpl, RefreshTokenDto

### Community 8 - "iOS App Entry Points"
Cohesion: 0.07
Nodes (30): Any, App, Bool, Data, Firebase, FirebaseCore, FirebaseMessaging, GoogleSignIn (+22 more)

### Community 9 - "Feature Module Plugins"
Cohesion: 0.09
Nodes (26): FeatureHomeDataModulePlugin, FeatureHomeDomainModulePlugin, FeatureHomeModulePlugin, FeatureHomePresentationModulePlugin, Plugin, ModuleName, APP, ATHLETICA_PLUS_KMP (+18 more)

### Community 10 - "Network Error Taxonomy & Fakes"
Cohesion: 0.08
Nodes (24): EmptyResult, ApiError, NetworkError, ACCOUNT_HAS_NO_PASSWORD, AUTH_PROVIDER_NOT_SUPPORTED, CUSTOMER_NOT_FOUND, EMAIL_REQUIRED, FORBIDDEN (+16 more)

### Community 12 - "ApiResult & Safe API Call"
Cohesion: 0.20
Nodes (12): ApiResult, Error, E, R, T, mapSuccess(), onError(), onSuccess() (+4 more)

### Community 13 - "Coroutine Launchers"
Cohesion: 0.11
Nodes (16): DebouncedSingleJobLaunch, CoroutineContext, CoroutineStart, Job, Launcher, LauncherIO, SingleLauncher, CancelPolicy (+8 more)

### Community 14 - "Ktor Auth Data Sources"
Cohesion: 0.24
Nodes (7): jsonHeaders(), HttpClient, MockEngine, T, respondJson(), testHttpClient(), KtorCustomerDataSourceTest

### Community 15 - "Project Docs & CI Pipeline"
Cohesion: 0.10
Nodes (26): Gradle setup with build caching, gradle-setup composite action, JDK 21 (Temurin) toolchain, App/server test split (allTests -x :server:test), CI workflow, Concurrency group with cancel-in-progress, test-app job (App tests), Test report artifact upload (+18 more)

### Community 16 - "RequestState Wrapper"
Cohesion: 0.06
Nodes (21): InPlace, MutableStateFlow, VD, Scoped, StateProduce, ViewDataInitializer, Error, Idle (+13 more)

### Community 17 - "Customer DAO & Update Use Case"
Cohesion: 0.23
Nodes (4): CustomerDao, CustomerDaoImpl, seedCustomer(), Customer

### Community 18 - "StoreTheme Dimens & Strings"
Cohesion: 0.33
Nodes (8): Modifier, StringResource, StoreOutlinedButton(), StoreOutlinedLoadingPreview(), StoreOutlinedPreview(), StoreOutlinedWithIconLoadingPreview(), StoreOutlinedWithIconPreview(), DrawableResource

### Community 19 - "Product DAO"
Cohesion: 0.19
Nodes (3): AuthUserRequest, AuthenticationUseCasesTest, AuthProvider

### Community 20 - "EventBus"
Cohesion: 0.20
Nodes (13): Channel, Base, CompletionWrap, Consumer, EventBus, Full, E, Flow (+5 more)

### Community 21 - "Action Handler Scopes"
Cohesion: 0.11
Nodes (29): CoroutineScope, ScopeProvider, ActionHandlerContext, CoroutineContext, ActionHandlerChildScopeImpl, ActionHandlerReadOnlyScope, ActionHandlerReadOnlyScopeImpl, ActionHandlerScope (+21 more)

### Community 22 - "Multiplatform Logger"
Cohesion: 0.14
Nodes (12): Logger, d(), e(), i(), Logger, LogLevel, DEBUG, ERROR (+4 more)

### Community 24 - "Auth Session DAO & Tokens"
Cohesion: 0.25
Nodes (9): NotificationType, ERROR, INFO, SUCCESS, Base, info(), invoke(), MessageEventData (+1 more)

### Community 25 - "View Action Handlers"
Cohesion: 0.11
Nodes (30): Composable, internalOnValueChange(), FieldError, InputFieldViewData, KeyboardActions, KeyboardOptions, Modifier, rememberTextFieldValue() (+22 more)

### Community 26 - "BaseActionHandleViewModel"
Cohesion: 0.18
Nodes (10): ClearFocus, HideKeyboard, Navigate, NavigateInclusive, ShowKeyboard, ShowMessage, ShowMessageExtendable, UiEvent (+2 more)

### Community 27 - "Platform Expect/Actual"
Cohesion: 0.18
Nodes (12): AndroidPlatform, getPlatform(), getPlatform(), Platform, PlatformType, ANDROID, IOS, JVM (+4 more)

### Community 28 - "Compose & Core Module Plugins"
Cohesion: 0.14
Nodes (15): AdminPanel, Auth, Cart, Categories, CategorySearch, Checkout, ContactUs, Details (+7 more)

### Community 29 - "String Validators"
Cohesion: 0.12
Nodes (7): DefaultStringValidator, T, Validator, EmailPatternValidator, EmailValidator, MaxLengthValidator, MinLengthValidator

### Community 30 - "Product Data Source Tests"
Cohesion: 0.10
Nodes (21): ApiResult, SessionTokensDto, RefreshRequestDto, SessionTokensDto, ApiResult, T, safeApiCall(), toNetworkError() (+13 more)

### Community 31 - "Product DAO Tests"
Cohesion: 0.17
Nodes (5): AuthDataSource, EmptyResult, KtorAuthDataSource, AuthResponseDto, KtorAuthDataSourceTest

### Community 32 - "Keyed Input Field Changes"
Cohesion: 0.23
Nodes (11): FieldKey, copy(), Field, Impl, InputFieldChanged, invoke(), KeyedField, Revalidate (+3 more)

### Community 33 - "ViewAction Definitions"
Cohesion: 0.18
Nodes (6): OnCloseBottomSheet, Tagged, OnSystemBackClick, ViewAction, AuthenticationViewAction, OnSignInClick

### Community 34 - "Navigation Screen Keys"
Cohesion: 0.13
Nodes (17): koinNavConfigProvider(), NavBackStack, NavKey, rememberKoinNavBackStack(), NavBackStack, NavKey, NavigationState, rememberMap() (+9 more)

### Community 36 - "Authentication View Data"
Cohesion: 0.16
Nodes (7): AuthenticationMockPreview, AuthenticationValidator, AuthenticationInitializer, MutableStateFlow, AuthenticationViewData, ManualBlockViewData, AuthUiTestFixtures

### Community 37 - "Customer DAO Tests"
Cohesion: 0.24
Nodes (4): CustomerDaoTest, customerFixture(), CartItem, PhoneNumber

### Community 38 - "Android Library Gradle Base"
Cohesion: 0.50
Nodes (3): CorePresentationModulePlugin, Plugin, Project

### Community 39 - "Gradle Source Set Helpers"
Cohesion: 0.22
Nodes (5): configureStore(), createHttpClient(), HttpClient, HttpClientFactoryTest, HttpClientEngine

### Community 40 - "UiEvent Emitter & HomeGraph VM"
Cohesion: 0.28
Nodes (8): implementation(), module(), safeAdd(), testImplementation(), kotlinMultiplatformExtension(), sourceSet(), Project, Project

### Community 41 - "Custom Drawer UI"
Cohesion: 0.25
Nodes (7): DrawerItem, Admin, Blog, Contact, Locations, Profile, SignOut

### Community 42 - "Auth Service Tests"
Cohesion: 0.14
Nodes (8): DefaultAuthRepository, ApiResult, AuthRepository, AuthRepository, AuthService, AuthResponse, FakeAuthRepository, CompletableDeferred

### Community 44 - "Notification & Message Events"
Cohesion: 0.17
Nodes (12): FieldError, of(), T, ValidatableField, ValidatableStringField, ValidatableUpdatableField, indicatorLineModifier(), IndicatorLineWrap() (+4 more)

### Community 45 - "Social Sign-In Block"
Cohesion: 0.17
Nodes (9): ViewAction, PlatformGoogleButton(), GoogleButton(), Modifier, MobileGoogleButtonUiContainerFirebase(), PlatformGoogleButton(), SocialMediaBlockContent(), PlatformGoogleButton() (+1 more)

### Community 46 - "Store Colors Palette"
Cohesion: 0.14
Nodes (13): Exception, AuthorizationDenied, DesktopNotSupported, GoogleSignInError, InvalidIdToken, InvalidState, MissingAuthorizationCode, MissingIdToken (+5 more)

### Community 47 - "Snackbar & HomeGraph Screen"
Cohesion: 0.10
Nodes (27): CustomDrawerState, DrawerItem, BottomBar(), BottomBarItem(), BottomBarPreview(), BottomBarDestination, CustomerViewData, Modifier (+19 more)

### Community 48 - "Field State Validators"
Cohesion: 0.22
Nodes (3): SessionCleanupJob, SessionCleanupJobTest, ThrowingAuthSessionDao

### Community 49 - "Secure Storage & Session"
Cohesion: 0.25
Nodes (4): AuthRepository, CustomerRepository, GoogleSignInService, PlatformRepositoryProvider

### Community 50 - "Customer Data Source"
Cohesion: 0.28
Nodes (4): AuthCredential, AuthServiceImpl, AuthProvider, Customer

### Community 51 - "Database Factory & Test Support"
Cohesion: 0.06
Nodes (28): ActionHandlerContext, ActionHandlerScope, BaseActionHandleViewModel, T, CoroutineContext, CoroutineScope, CoroutineStart, AuthenticationViewModel (+20 more)

### Community 52 - "Navigation State"
Cohesion: 0.24
Nodes (3): GoogleTokenResponse, JvmGoogleSignInService, TokenEntity

### Community 53 - "UiEvent Types"
Cohesion: 0.33
Nodes (4): runCatchingCancellable(), EmptyResult, R, Result

### Community 54 - "Product Category Use Cases"
Cohesion: 0.15
Nodes (9): Flow, ReadProductsByCategoryUseCase, ProductCategory, Accessories, Creatine, Gainers, PreWorkout, Protein (+1 more)

### Community 56 - "StoreModulePlugin"
Cohesion: 0.25
Nodes (8): configureDesktopApplication(), macOsExtraKeysRawXml(), AppAthleticaPlusModulePlugin, AppNutriSportModulePlugin, Plugin, Project, StoreModulePlugin, TargetFormat

### Community 57 - "MainViewController"
Cohesion: 0.18
Nodes (7): Module, MainViewController(), initializeKoin(), Module, KoinApplication, AthleticaPlusMainViewController(), NutriSportMainViewController()

### Community 58 - "navKey"
Cohesion: 0.24
Nodes (8): T, NavKeyProviderInstaller, T, navEntry(), navKey(), KoinDefinition, KSerializer, PolymorphicModuleBuilder

### Community 59 - "SignInResult"
Cohesion: 0.18
Nodes (9): alias(), composeExtension(), desktopExtension(), CoreResourcesModulePlugin, Plugin, Project, PluginDependency, Provider (+1 more)

### Community 61 - ".renderAuth"
Cohesion: 0.23
Nodes (3): AuthCallback, LocalOAuthCallbackServer, LocalOAuthCallbackServerTest

### Community 62 - "BottomBar"
Cohesion: 0.15
Nodes (3): DefaultAuthRepositoryTest, AuthProvider, AuthResponseDto

### Community 63 - "Products"
Cohesion: 0.17
Nodes (12): Auth, Authorize, ByCategory, ByIds, Customers, Discounted, Id, Logout (+4 more)

### Community 66 - "DistinctFlowImpl"
Cohesion: 0.42
Nodes (8): DistinctFlowImpl, distinctUntilChangedDebounced(), distinctUntilChangedDebouncedBy(), distinctUntilChangedDebouncedByType(), Flow, FlowCollector, T, KClass

### Community 67 - "PreviewTheme"
Cohesion: 0.20
Nodes (4): Application, StoreApp, AthleticaPlusApp, NutriSportApp

### Community 69 - ".apply"
Cohesion: 0.12
Nodes (8): LocalAuthSessionDataSource, SecureStorage, provideAuthSessionDataSource(), FirebaseLocalAuthSessionDataSource, SecureStorage, provideAuthSessionDataSource(), SecureStorage, provideAuthSessionDataSource()

### Community 70 - "AuthenticationContent"
Cohesion: 0.14
Nodes (14): dimensFor(), Dp, StoreDimens, BaseTheme(), PreviewTheme(), WindowType, Compact, Expanded (+6 more)

### Community 72 - "EmailPatternValidator"
Cohesion: 0.50
Nodes (3): CoreUtilsModulePlugin, Plugin, Project

### Community 73 - "FakeAuthDataSource"
Cohesion: 0.36
Nodes (4): FakeAuthDataSource, ApiResult, AuthResponseDto, EmptyResult

### Community 74 - ".buildInitial"
Cohesion: 0.31
Nodes (4): init(), StoreTypography, AndroidSecureStorage, SecretKey

### Community 76 - "Measurement"
Cohesion: 0.22
Nodes (8): Measurement, GALLON, GRAM, KILOGRAM, LITER, MILLILITER, PORTION, SINGLE_ITEM

### Community 77 - "AbstractInputFieldValidator"
Cohesion: 0.20
Nodes (8): assertFailsWithSuspend(), customerDtoFixture(), decodeJson(), grantAdmin(), CartItemDto, CustomerDto, PhoneNumberDto, T

### Community 78 - "SignInFailureHandler"
Cohesion: 0.28
Nodes (3): SignInFailureHandler, SignInFailureHandlerImpl, FakeSignInFailureHandler

### Community 80 - ".launch"
Cohesion: 0.39
Nodes (5): CoroutineContext, CoroutineStart, Job, Launcher, LauncherIO

### Community 81 - "StoreAndroidAppPlugin"
Cohesion: 0.33
Nodes (5): CustomerDto, CustomerDataSource, ApiResult, EmptyResult, KtorCustomerDataSource

### Community 82 - "data/mappers/CustomerMappers.kt"
Cohesion: 0.50
Nodes (3): Plugin, Project, ServerModulePlugin

### Community 84 - "ProductDataSource"
Cohesion: 0.24
Nodes (7): BlockingFieldValidator, FieldStateValidator, StateFlow, T, FieldValidator, StateFlow, T

### Community 85 - "customer/mappers/CustomerMappers.kt"
Cohesion: 0.22
Nodes (6): DefaultCustomerRepository, ApiResult, Customer, CustomerRepository, EmptyResult, Flow

### Community 87 - "IosSecureStorageExt.kt"
Cohesion: 0.29
Nodes (6): CFDictionaryRef, ByteArray, toByteArray(), toCFDictionary(), toNSData(), NSData

### Community 88 - "ProjectExtensions.kt"
Cohesion: 0.36
Nodes (4): ApplicationConfig, Config, DatabaseFactory, isolatedDatabaseConfig()

### Community 89 - "AppStrings"
Cohesion: 0.18
Nodes (6): AuthResponseDto, RefreshRequestDto, SessionTokensDto, AuthResponseDto, Session, SessionRoutesIntegrationTest

### Community 90 - "Currency"
Cohesion: 0.33
Nodes (6): Currency, EURO, FRANC, UAH, USD, getCurrencyById()

### Community 92 - "OnSystemBackClick.kt"
Cohesion: 0.20
Nodes (6): AuthRepository, CustomerRepository, GoogleSignInService, SecureStorage, PlatformRepositoryProvider, provideAuthSessionDataSource()

### Community 93 - "GetCurrentUserIdUseCase"
Cohesion: 0.16
Nodes (6): org, ProductDao, ProductDaoImpl, dbQuery(), T, Product

### Community 94 - "SocialMediaBlockViewData"
Cohesion: 0.38
Nodes (3): SocialMediaBlockMockPreview, SocialMediaBlockViewData, SocialMediaButtonViewData

### Community 95 - ".authCredentialFixture"
Cohesion: 0.42
Nodes (3): AbstractInputFieldValidator, BlankStringValidator, T

### Community 96 - "SignInResult"
Cohesion: 0.32
Nodes (4): Failure, SignInResult, Success, SignInUseCase

### Community 97 - "AuthenticationViewAction"
Cohesion: 0.32
Nodes (6): InputFieldAction, Done, Impl, invoke(), KeyboardAction, Next

### Community 98 - "Greeting"
Cohesion: 0.22
Nodes (6): createJvmGoogleOAuthHttpClient(), AuthRepository, CustomerRepository, GoogleSignInService, HttpClient, PlatformRepositoryProvider

### Community 99 - "FakeCustomerRepository"
Cohesion: 0.14
Nodes (6): CustomerRepository, EmptyResult, Flow, FakeCustomerRepository, EmptyResult, Flow

### Community 103 - "CoreDomainModulePlugin"
Cohesion: 0.25
Nodes (5): configureAndroidLibraryBase(), androidLibrary(), CoreDomainModulePlugin, Plugin, Project

### Community 104 - ".apply"
Cohesion: 0.20
Nodes (7): configureIOS(), CoreNavigationModulePlugin, Plugin, Project, CoreSecurityModulePlugin, Plugin, Project

### Community 105 - "CoreSecurityModulePlugin"
Cohesion: 0.40
Nodes (5): collectEvents(), collectEventsWithDefaultProcessing(), FlowCollector, SharedFlow, UiEventSource

### Community 106 - "ComposeMultiplatformConventionPlugin"
Cohesion: 0.50
Nodes (3): ComposeMultiplatformConventionPlugin, Plugin, Project

### Community 107 - "KotlinMultiplatformConventionPlugin"
Cohesion: 0.50
Nodes (3): KotlinMultiplatformConventionPlugin, Plugin, Project

### Community 108 - "StoreTheme"
Cohesion: 0.25
Nodes (3): AppNavigator, Modifier, SetupNavGraph()

### Community 109 - "getAndroidSdkVersions"
Cohesion: 0.14
Nodes (10): ApplicationPlugin, CoreNetworkModulePlugin, Plugin, Project, KoinKtorApplication, main(), module(), installForwardedHeaders() (+2 more)

### Community 110 - ".apply"
Cohesion: 0.50
Nodes (3): DiModulePlugin, Plugin, Project

### Community 112 - "SocialMediaViewAction"
Cohesion: 0.40
Nodes (4): OnGoogleClick, OnSignInFailure, OnSignInSuccess, SocialMediaViewAction

### Community 113 - "BuildTypeName"
Cohesion: 0.50
Nodes (3): BuildTypeName, DEBUG, RELEASE

### Community 116 - "RefreshOutcome"
Cohesion: 0.17
Nodes (10): SessionRefreshCall, HttpStatusCode, SessionRefreshCallTest, bareTestClient(), jsonHeaders(), HttpClient, HttpStatusCode, MockEngine (+2 more)

### Community 118 - ".invoke"
Cohesion: 0.50
Nodes (3): Plugin, Project, TestModulePlugin

### Community 121 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 126 - "alias"
Cohesion: 0.40
Nodes (4): BottomBarDestination, Cart, Categories, ProductsOverview

### Community 212 - "NavKey"
Cohesion: 0.50
Nodes (3): AuthenticationFields, Email, Password

### Community 214 - "CustomDrawerState"
Cohesion: 0.40
Nodes (4): CustomDrawerState, Closed, Opened, opposite()

### Community 215 - ".apply"
Cohesion: 0.50
Nodes (3): CoreDataModulePlugin, Plugin, Project

### Community 216 - "AuthenticationValidator"
Cohesion: 0.57
Nodes (3): Abstract, T, SuspendFieldValidator

### Community 218 - "toDto"
Cohesion: 0.40
Nodes (4): AuthResponseDto, SessionTokensDto, toAuthRequest(), toDto()

### Community 222 - ".apply"
Cohesion: 0.23
Nodes (8): Impl, InputFieldConfig, InputFieldViewData, invoke(), InputFieldValidator, EmailFieldValidator, PasswordFieldValidator, ValidatableUpdatableInputField

### Community 224 - "AuthDto.kt"
Cohesion: 0.40
Nodes (4): Compromised, RefreshResult, Rejected, Rotated

### Community 226 - "RefreshOutcome"
Cohesion: 0.40
Nodes (4): RefreshOutcome, REJECT, REVOKE_FAMILY, ROTATE

### Community 227 - "HomeGraphViewAction"
Cohesion: 0.32
Nodes (7): CartItemDto, CustomerDto, PhoneNumberDto, toCartItem(), toCustomer(), toDto(), toPhoneNumber()

### Community 228 - "StoreColorsPalette"
Cohesion: 0.33
Nodes (7): BaseDarkStoreColorsPalette, BaseLightStoreColorsPalette, Color, StoreColorsPalette, StoreThemeProvider, AthleticaPlusStoreThemeProvider, NutriSportStoreThemeProvider

### Community 229 - "NotificationType"
Cohesion: 0.50
Nodes (3): ComposeAppModulePlugin, Plugin, Project

### Community 230 - "AuthenticationViewAction"
Cohesion: 0.32
Nodes (7): CartItemDto, CustomerDto, PhoneNumberDto, toCartItem(), toCustomer(), toDto(), toPhoneNumber()

### Community 232 - "UiText"
Cohesion: 0.22
Nodes (7): Empty, invoke(), StringResource, Raw, Resource, ResourceArgs, UiText

### Community 233 - "AuthUserRequest"
Cohesion: 0.28
Nodes (4): AuthUserRequest, GoogleSignInService, FakeGoogleSignInService, CompletableDeferred

### Community 234 - "EmailDomainValidator"
Cohesion: 0.36
Nodes (3): EmailDomainValidationConfig, EmailDomainValidator, Impl

### Community 235 - "StatusPages.kt"
Cohesion: 0.29
Nodes (4): Bundle, ComponentActivity, MainActivity, App()

### Community 236 - "PasswordField"
Cohesion: 0.36
Nodes (7): FieldError, InputFieldViewData, KeyboardActions, KeyboardOptions, Modifier, PasswordField(), PasswordFieldDisablePreview()

### Community 237 - "PlatformRepositoryProvider"
Cohesion: 0.25
Nodes (4): AuthRepository, CustomerRepository, GoogleSignInService, PlatformRepositoryProvider

### Community 239 - "Greeting"
Cohesion: 0.25
Nodes (3): serverRout(), ApplicationTest, Greeting

### Community 240 - "AppViewModelTest"
Cohesion: 0.38
Nodes (3): AppViewModel, AppViewModelTest, BaseViewModelTest

### Community 241 - "StoreTheme"
Cohesion: 0.29
Nodes (4): AppStrings, StoreTheme, AthleticaPlusStrings, NutriSportStrings

### Community 242 - "AuthenticationContent"
Cohesion: 0.23
Nodes (10): Modifier, StringResource, TextDivider(), TextDividerPrev(), AuthenticationContent(), AuthenticationScreen(), AuthenticationScreenPreview(), AuthenticationViewData (+2 more)

### Community 243 - "RateLimitGroup"
Cohesion: 0.38
Nodes (6): Route, rateLimited(), RateLimitGroup, AUTH_AUTHORIZE, AUTH_SESSION, register()

### Community 244 - "configureRouting"
Cohesion: 0.33
Nodes (4): authRoutes(), customerRoutes(), requireOwnCustomer(), configureRouting()

### Community 245 - "FeatureAuthenticationModulePlugin"
Cohesion: 0.53
Nodes (5): FeatureAuthenticationDataModulePlugin, FeatureAuthenticationDomainModulePlugin, FeatureAuthenticationModulePlugin, FeatureAuthenticationPresentationModulePlugin, Plugin

### Community 247 - "desktopApp"
Cohesion: 0.29
Nodes (4): desktopApp(), Module, main(), main()

### Community 251 - "Resources"
Cohesion: 0.18
Nodes (10): Modifier, StoreSnackbar(), StoreSnackbarHostState, Flag, Font, Icon, Image, Resources (+2 more)

### Community 252 - "FakeCustomerDataSource"
Cohesion: 0.53
Nodes (3): FakeCustomerDataSource, CustomerDto, EmptyResult

### Community 253 - "ScopeProvider"
Cohesion: 0.50
Nodes (3): ApplicationCall, authorizeRateLimitKey(), installRateLimit()

### Community 255 - ".apply"
Cohesion: 0.50
Nodes (3): Plugin, Project, SharedModulePlugin

### Community 257 - "RacingDaos.kt"
Cohesion: 0.50
Nodes (3): RacingCredentialDao, RacingCustomerDao, uniqueViolation()

### Community 260 - "HomeGraphViewAction"
Cohesion: 0.50
Nodes (3): CheckoutClicked, HomeGraphViewAction, SignOutClicked

### Community 262 - "installKoin"
Cohesion: 0.50
Nodes (3): installKoin(), ApplicationPlugin, KoinKtorApplication

## Knowledge Gaps
- **205 isolated node(s):** `SessionTokensDto`, `AuthResponseDto`, `SessionTokensDto`, `ROTATE`, `REJECT` (+200 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **82 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `NetworkError` connect `Network Error Taxonomy & Fakes` to `.invoke`, `ApiResult & Safe API Call`, `Product Data Source Tests`, `Product DAO Tests`, `Auth Service Tests`, `UiEvent Types`, `Product Category Use Cases`, `BottomBar`, `FakeAuthDataSource`, `SignInFailureHandler`, `StoreAndroidAppPlugin`, `customer/mappers/CustomerMappers.kt`, `.authorize`, `FakeCustomerRepository`, `.invoke`, `.invoke`, `FakeCustomerDataSource`, `.invoke`, `.invoke`, `auth/mappers/AuthMappers.kt`, `Module`?**
  _High betweenness centrality (0.189) - this node is a cross-community bridge._
- **Why does `Customer` connect `Customer DAO & Update Use Case` to `RacingDaos.kt`, `FakeCustomerRepository`, `.invoke`, `HomeGraphViewAction`, `AuthenticationViewAction`, `.invoke`, `Customer DAO Tests`, `RequestState Wrapper`?**
  _High betweenness centrality (0.109) - this node is a cross-community bridge._
- **Why does `ActionHandlerScope` connect `Action Handler Scopes` to `RequestState Wrapper`, `Keyed Input Field Changes`, `ProductDataSource`, `AuthenticationValidator`?**
  _High betweenness centrality (0.080) - this node is a cross-community bridge._
- **Are the 15 inferred relationships involving `AuthRequest` (e.g. with `.invoke()` and `.signInUseCase_maps_social_request_and_returns_Success()`) actually correct?**
  _`AuthRequest` has 15 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SessionTokensDto`, `AuthResponseDto`, `SessionTokensDto` to the rest of the system?**
  _205 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Auth & Customer DTOs` be split into smaller, more focused modules?**
  _Cohesion score 0.14736842105263157 - nodes in this community are weakly interconnected._
- **Should `Android Gradle Convention Plugins` be split into smaller, more focused modules?**
  _Cohesion score 0.057692307692307696 - nodes in this community are weakly interconnected._