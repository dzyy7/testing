# AGENTS.md - PSP POS Kotlin EDC Mobile

Android Kotlin POS/EDC payment app using MVVM + Hilt DI.
**Package**: `id.co.psplauncher` | **Min SDK**: 24 | **Target SDK**: 34 | **JVM**: Java 21

---

## Build Commands

```bash
./gradlew build                          # Build project
./gradlew clean build                    # Clean build
./gradlew lint                           # Lint check
./gradlew test                           # All unit tests
./gradlew test --tests "id.co.psplauncher.ExampleUnitTest.addition_isCorrect"  # Single test
./gradlew test --tests "id.co.psplauncher.ExampleUnitTest"                    # All tests in class
./gradlew connectedAndroidTest           # Instrumented tests (device required)
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=id.co.psplauncher.ExampleInstrumentedTest#useAppContext  # Single instrumented test
./gradlew assembleDebug                  # Debug APK
./gradlew assembleRelease                # Release APK
```

---

## Architecture

**MVVM**: View (Activities/Fragments + ViewBinding) → ViewModel (`@HiltViewModel`, LiveData) → Repository (extends `BaseRepository`, `safeApiCall`) → API (Retrofit suspend functions)

**DI**: Hilt - `@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`, `@Inject`, `@Module`, `@InstallIn(SingletonComponent::class)`

**Storage**: DataStore Preferences (`UserPreferences` class)

---

## Package Structure

```
id.co.psplauncher/
├── data/local/           # UserPreferences, SharedPreferences, CartManager
├── data/network/         # BaseRepository, Resource, RemoteDataSource, AuthInterceptor
│   ├── auth/             # AuthApi, AuthRepository
│   ├── chip/             # ChipApi, ChipRepository
│   ├── dashboard/        # DashboardApi, DashboardRepository
│   ├── request/          # Request data classes
│   ├── response/         # Response data classes
│   └── model/            # Domain models
├── di/                   # AppModule, Hilt modules
├── ui/                   # Activities, Fragments, ViewModels, adapters
└── Utils.kt              # Extension functions, error handling
```

---

## Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Activity | `XxxActivity` | `LoginActivity` |
| Fragment | `XxxFragment` or `FragmentXxx` | `FragmentPos`, `FragmentCart` |
| ViewModel | `XxxViewModel` | `LoginViewModel`, `FragmentPosViewModel` |
| Repository | `XxxRepository` | `AuthRepository` |
| API Interface | `XxxApi` | `AuthApi` |
| Adapter | `XxxAdapter` | `ProductAdapter` |
| Request/Response | `XxxRequest`, `XxxResponse` | `LoginRequest`, `LoginResponse` |
| Layout (Activity) | `activity_xxx.xml` | `activity_login_page.xml` |
| Layout (Fragment) | `fragment_xxx.xml` | `fragment_pos.xml` |
| Layout (Item) | `item_xxx.xml` | `item_product_grid.xml` |

**LiveData naming**: Private `_propertyName`, public `propertyName`

---

## Import Order

1. Android SDK (`android.*`)
2. AndroidX (`androidx.*`)
3. Third-party (`com.*`, `dagger.*`, `retrofit2.*`, etc.)
4. Project (`id.co.psplauncher.*`)
5. Kotlin (`kotlin.*`, `kotlinx.*`)
6. Java (`java.*`, `javax.*`)

Alphabetize within each group.

---

## Code Patterns

### ViewModel
```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val repository: ExampleRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _data = MutableLiveData<Resource<ExampleResponse>>()
    val data: LiveData<Resource<ExampleResponse>> = _data

    fun fetchData() = viewModelScope.launch {
        _data.value = repository.fetchData()
    }
}
```

### Fragment with ViewBinding
```kotlin
@AndroidEntryPoint
class ExampleFragment : Fragment() {
    private var _binding: FragmentExampleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExampleViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExampleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

### Repository
```kotlin
class ExampleRepository @Inject constructor(
    private val api: ExampleApi,
    private val userPreferences: UserPreferences
) : BaseRepository() {
    suspend fun fetchData() = safeApiCall({ api.getData() }, userPreferences)
}
```

### API Interface
```kotlin
interface ExampleApi {
    @Headers("Content-Type: application/json")
    @POST("endpoint/path")
    suspend fun postData(@Body request: ExampleRequest): Response<ExampleResponse>
}
```

### Observing Resource
```kotlin
viewModel.data.observe(this) {
    when (it) {
        is Resource.Success -> { /* Handle it.value */ }
        is Resource.Loading -> { /* Show loading */ }
        is Resource.Failure -> handleApiError(binding.root, it)
    }
}
```

---

## Error Handling

Use `Utils.handleApiError(view, failure)` - handles network errors, 401 unauthorized, 400 bad request, and generic errors with Snackbar.

---

## Formatting

- Official Kotlin code style (`kotlin.code.style=official`)
- 4-space indentation
- Max line length: 100 characters
- Opening brace on same line
- Blank line before first and after last member in class body

---

## Key Dependencies

Hilt 2.51.1 | Retrofit 2.9.0 | OkHttp 4.9.1 | Coroutines 1.7.3 | Glide 5.0.5 | DataStore 1.0.0 | Chucker 3.5.2 (debug)
