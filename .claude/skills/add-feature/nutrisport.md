# Porting a NutriSport screen

NutriSport (Stevdza-San, Compose Multiplatform + Firebase) is the **product spec** for this app: which screens exist,
which fields and actions they have. It is **not** an architecture reference. Take the UI and behaviour below; build
the ViewModel, data flow and errors from scratch by `CLAUDE.md`. Never copy its ViewModels or repositories.

## Translation table

| NutriSport | This project |
|---|---|
| Firebase / Firestore is the source of truth | Ktor server + SQLite |
| `RequestState` + `onSuccess: () -> Unit, onError: (String) -> Unit` callbacks | `suspend fun` → `ApiResult<T, XError>` |
| `Flow<RequestState<…>>` for one-shot reads | `suspend`; `Flow` only for `observeX()` over data that really changes |
| One `:data` module with four repositories | `component/<entity>/{model, domain-api, usecase, data}` |
| `getCurrentUserId()` in every repository | `LocalAuthSessionDataSource` (`core:security`) inside the repository |
| Cart operations inside `CustomerRepository` | Process component `basket` |
| Order saved, then cart cleared **on the client** (`onError = {}` swallows failures) | Server writes order + clears basket in one transaction |
| Cart total summed in three ViewModels (`Double`) | Server computes money totals |
| `AdminRepository` as an entity | Admin is a role: product writes in `component/product`, `is_admin` checked by the server, never writable via the API |
| `PaypalApi` inside the feature, `PAYPAL_AUTH_KEY` on the client | `component/payment`; PayPal credentials only on the server |
| `PreferencesRepository` polling `while (true) { delay(1000) }` for the payment result | No polling: result arrives through the deeplink / server |
| ViewModel with `mutableStateOf` + one `updateX()` per field | `BaseActionHandleViewModel` + sealed `ViewAction` + `InputFieldChanged` |
| `savedStateHandle.get<String>("id")` | Argument in the `Screen` key (`Screen.Details(id)`) |
| `Dispatchers.IO`, `threadSleep(2000)`, `println` | `AppDispatchers` / `launchIo`, nothing, `Logger` |
| Nested modules `feature/home/cart/checkout` | Flat `feature/<name>` |
| `Country` with `DrawableResource`, `ProductCategory` with `Color` in the domain | UI resources stay out of `:shared` (the server depends on it); map to UI in the feature |
| `shared/component/*` (`CustomTextField`, `PrimaryButton`, `InfoCard`, …) | `Store*` composables in `core:presentation` |
| `isFormValid` with `a && b || c && d` | Validator per field (`FieldKey`). The original has a precedence bug — do not port it |

## Screens

Screen key → what the user sees and does. "Needs" = what must exist before the feature can be built.

**Profile** (`Screen.Profile`) — title "My Profile". Form: first name, last name, email (read-only), city,
postal code (number), address, country picker + phone (dial code from country, number). Button "Update", enabled
only when the form is valid; success message "Successfully updated!". Error card "Oops!" when loading fails.
Needs: `ReadCustomerUseCase`, `UpdateCustomerUseCase` — both exist.

**Products overview** (`Screen.ProductsOverview`) — new products in a horizontal list of large cards, then
"Discounted Products" in a vertical list. Empty state "Nothing here" / "Empty product list.", error "Oops!".
Card click → `Screen.Details(id)`. Needs: `ReadNewProductsUseCase`, `ReadDiscountedProductsUseCase` — exist.

**Details** (`Screen.Details(id)`) — title "Details"; image, title, weight ("${weight}g"), price, description;
flavor chips (single select, shown only if the product has flavors); quantity counter. "Add to Cart" enabled when a
flavor is selected (or the product has none); success "Product added to cart.". Needs: `ReadProductByIdUseCase`
(exists) + basket add-item (component `basket`, server route).

**Cart** (`Screen.Cart`) — list of cart items with product image, title, flavor, price, quantity counter and delete.
Quantity change and delete update the server; empty state "Empty Cart" / "Check some of our products.". Needs:
component `basket` (read, update quantity, delete) with server-computed total.

**Categories** (`Screen.Categories`) — five category cards (`ProductCategory`: Protein, Creatine, Pre-Workout,
Gainers, Accessories) → `Screen.CategorySearch(category)`. Category colours are a UI mapping in the feature.

**Category search** (`Screen.CategorySearch(category)`) — products of one category + search field filtering by title
(debounce 500 ms). Needs: `ReadProductsByCategoryUseCase` (exists); search by title on the server is optional.

**Checkout** (`Screen.Checkout`) — title "Checkout", shows the total; same form as Profile (pre-filled from the
customer). Two buttons, enabled only when the form is valid: "Pay with PayPal" and "Pay on Delivery". Needs:
components `order` + `payment`; the total comes from the server, not from the screen key.

**Payment completed** (`Screen.PaymentCompleted`) — "Success!" / "Your purchase is on the way." or "Oops!" with the
error; button "Go back" → home. The order is created by the server, not by this screen.

**Admin panel** (`Screen.AdminPanel`, admin only) — last 10 products + search by title (debounce 500 ms); click →
`Screen.ManageProduct(id)`, FAB → `Screen.ManageProduct()` (new). Needs: admin product routes on the server.

**Manage product** (`Screen.ManageProduct(id?)`, admin only) — photo picker + thumbnail upload (expect/actual
`PhotoPicker`; "Try again" on failed upload), title, description, category dialog, flavors (comma separated),
weight, price, switches "New" / "Popular" / "Discounted". Create or update; "Delete" when editing. Needs: product
write use cases, image upload route, `ManageProduct` `navEntry` (not registered today).
