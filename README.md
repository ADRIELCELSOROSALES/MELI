# AppChallengeMELI

Aplicacion Android nativa que consume la API publica de Mercado Libre para buscar y visualizar productos. Desarrollada como challenge tecnico, siguiendo las mejores practicas de desarrollo Android moderno.

## Screenshots

| Busqueda | Resultados | Detalle |
|----------|------------|---------|
| Pantalla inicial con barra de busqueda | Lista de productos con scroll infinito | Carrusel de imagenes, precio, atributos y descripcion |

## Arquitectura

El proyecto sigue **MVVM + Clean Architecture** organizado en las siguientes capas:

```
app/src/main/java/com/example/appchallengemeli/
├── core/                  # Tipos base (Result, UiState, AppException, AppConfig)
├── data/
│   ├── remote/
│   │   ├── api/           # Interfaz Retrofit (MeliApi)
│   │   ├── dto/           # Data Transfer Objects (respuestas de la API)
│   │   ├── interceptor/   # AuthInterceptor (inyeccion del token)
│   │   └── mapper/        # Mappers DTO → Domain
│   └── repository/        # Implementacion del repositorio
├── di/                    # Modulos Hilt (NetworkModule, RepositoryModule)
├── domain/
│   ├── model/             # Modelos de dominio (Product, ProductDetail, SearchResult)
│   ├── repository/        # Interfaz del repositorio (contrato)
│   └── usecase/           # Casos de uso (SearchProductsUseCase, GetProductDetailUseCase)
└── ui/
    ├── common/            # Componentes reutilizables (ErrorState, LoadingIndicator, EmptyState)
    ├── detail/            # Pantalla de detalle (Screen + ViewModel)
    ├── navigation/        # Grafo de navegacion
    ├── search/            # Pantalla de busqueda (Screen + ViewModel)
    └── theme/             # Tema Material 3 con colores de Mercado Libre
```

### Por que esta arquitectura?

- **Separacion de responsabilidades**: cada capa tiene un rol claro. La UI no conoce Retrofit, el dominio no conoce la implementacion de datos.
- **Testeable**: los ViewModels dependen de Use Cases (interfaces), lo que permite mockear facilmente en tests.
- **Escalable**: agregar nuevas features (favoritos, historial, etc.) no requiere modificar las existentes.
- **`sealed interface` para Result y UiState**: permite pattern matching exhaustivo en Kotlin, eliminando la posibilidad de estados no manejados.

### Flujo de datos

```
UI (Compose) → ViewModel → UseCase → Repository → API (Retrofit)
                   ↑                      ↓
              StateFlow              Result<T>
```

El ViewModel expone `StateFlow<UiState<T>>` que la UI recolecta con `collectAsStateWithLifecycle()`, garantizando que la recoleccion se detiene cuando la UI no es visible (lifecycle-aware).

## Stack tecnologico

| Tecnologia | Version | Justificacion |
|-----------|---------|---------------|
| **Kotlin** | 2.0.0 | Lenguaje oficial para Android. Soporte de coroutines, null safety, extension functions. |
| **Jetpack Compose** | BOM 2024.12.01 | Toolkit de UI declarativo recomendado por Google. Menos boilerplate que Views/XML. |
| **Material 3** | (via BOM) | Sistema de diseno actualizado de Google. Soporte nativo para temas dinamicos y dark mode. |
| **Hilt** | 2.51.1 | Inyeccion de dependencias oficial de Android sobre Dagger. Reduce boilerplate con anotaciones. |
| **Retrofit** | 2.11.0 | Cliente HTTP type-safe. Estandar de la industria para consumir REST APIs en Android. |
| **OkHttp** | 4.12.0 | Cliente HTTP subyacente. Permite interceptors para auth y logging. |
| **Coil** | 2.7.0 | Carga de imagenes para Compose. Mas liviano que Glide, coroutine-first. |
| **Navigation Compose** | 2.8.5 | Navegacion oficial para Compose con soporte de argumentos tipados y transiciones. |
| **Coroutines + Flow** | 1.8.1 | Programacion asincrona estructurada. StateFlow para estado reactivo en ViewModels. |
| **KSP** | 2.0.0-1.0.24 | Procesador de anotaciones mas rapido que KAPT, usado por Hilt. |

### Configuracion del proyecto

- `compileSdk = 35` / `targetSdk = 35` / `minSdk = 24`
- AGP 8.8.0
- Version Catalog (`libs.versions.toml`) para gestion centralizada de dependencias

## Features

- **Busqueda de productos**: barra de busqueda con accion de teclado y boton. Boton para limpiar la query.
- **Paginacion infinita**: scroll infinito que carga mas resultados automaticamente al llegar al final de la lista.
- **Pull-to-refresh**: permite refrescar los resultados deslizando hacia abajo.
- **Detalle de producto**: carrusel de imagenes con `HorizontalPager`, precio, condicion, cantidad vendida, stock, garantia, atributos y descripcion.
- **Manejo de errores**: estados diferenciados para sin conexion, timeout, token expirado, errores HTTP y errores desconocidos. Todos con boton de reintentar.
- **Estado vacio**: feedback visual cuando una busqueda no devuelve resultados.
- **Dark mode**: soporte completo con colores adaptados al tema de Mercado Libre.
- **Transiciones animadas**: slide + fade entre pantallas de busqueda y detalle.
- **Placeholders de imagen**: indicador de carga y estado de error en todas las imagenes.

## Manejo de errores

Se definio un `sealed class AppException` que clasifica los errores posibles:

| Excepcion | Causa | Feedback al usuario |
|-----------|-------|---------------------|
| `Network` | `IOException` (sin conexion) | "No hay conexion a internet" + icono WifiOff |
| `Timeout` | `SocketTimeoutException` | "Tiempo de espera agotado" |
| `TokenExpired` | HTTP 401 | "Token expirado. Reemplaza el token en AppConfig y recompila." |
| `Api` | HTTP 403, 500, etc. | Mensaje parseado del body de la respuesta |
| `Unknown` | Cualquier otra excepcion | Mensaje generico con la causa |

Todos los errores se capturan en `safeApiCall()` dentro del repositorio, y cada pantalla muestra un `ErrorState` con boton de reintentar.

## Testing

El proyecto cuenta con **36 tests unitarios** que cubren todas las capas:

```
app/src/test/
├── MainDispatcherRule.kt                          # Regla JUnit para tests con coroutines
├── data/
│   ├── remote/mapper/ProductMapperTest.kt         # 7 tests - Mapeo de DTOs a dominio
│   └── repository/ProductRepositoryImplTest.kt    # 6 tests - Repositorio, errores HTTP, safeApiCall
├── domain/usecase/
│   ├── SearchProductsUseCaseTest.kt               # 3 tests - Delegacion al repositorio
│   └── GetProductDetailUseCaseTest.kt             # 2 tests - Delegacion al repositorio
└── ui/
    ├── common/PriceFormatterTest.kt               # 6 tests - Formateo de precios por moneda
    ├── detail/DetailViewModelTest.kt              # 3 tests - Carga, error, retry
    └── search/SearchViewModelTest.kt              # 9 tests - Busqueda, paginacion, empty, error, reset
```

### Estrategia de testing

- **Mappers**: se testean con datos reales y con campos `null` para validar defaults.
- **Repositorio**: se mockea `MeliApi` con MockK y se verifican los distintos tipos de error (IOException, HttpException 401/403/500).
- **Use Cases**: se verifica la correcta delegacion al repositorio con los parametros esperados.
- **ViewModels**: se usa `MainDispatcherRule` con `UnconfinedTestDispatcher` (para verificar estado final) o `StandardTestDispatcher` (para observar estados intermedios como Loading).
- **PriceFormatter**: se validan formatos para ARS, USD, BRL y monedas desconocidas.

### Ejecutar tests

```bash
./gradlew testDebugUnitTest
```

## Seguridad

- **Token de acceso**: no se commitea al repositorio. Se usa un placeholder en `AppConfig.kt` que debe ser reemplazado localmente.
- **HTTPS**: todas las URLs de thumbnails se convierten de HTTP a HTTPS en el mapper.
- **Logging**: `HttpLoggingInterceptor` esta condicionado a `BuildConfig.DEBUG`, evitando logs en builds de release.
- **Permisos**: la app solo solicita `INTERNET`, el minimo necesario.

## Decisiones de diseno

### Por que no use UseCases al principio y despues los agregue?

Inicialmente el proyecto era lo suficientemente simple como para que los ViewModels llamaran directamente al repositorio. Al crecer la complejidad (paginacion, multiples endpoints en el detalle), los Use Cases se volvieron necesarios para:
- Mantener los ViewModels enfocados en logica de presentacion
- Facilitar el testing (mockear un UseCase es mas limpio que mockear un Repository completo)
- Preparar la app para crecer (e.g., un UseCase podria orquestar cache + network)

### Por que Coil y no Glide?

Coil fue disenado para Kotlin y Compose desde el inicio. Es mas liviano (~1500 metodos vs ~4500 de Glide), usa coroutines nativamente y tiene integracion directa con Compose (`SubcomposeAsyncImage`).

### Por que StateFlow y no LiveData?

`StateFlow` es una API de Kotlin pura (no requiere dependencia de Android), es mas predecible (siempre tiene un valor inicial), y se integra mejor con coroutines. Ademas, `collectAsStateWithLifecycle()` de Compose proporciona la misma seguridad lifecycle-aware que LiveData.

### Por que sealed interface y no sealed class para Result/UiState?

`sealed interface` permite que las implementaciones sean `data class`, `data object` o incluso hereden de otra clase. Es mas flexible y es la recomendacion actual de Kotlin.

## Requisitos para compilar

1. **Android Studio** Ladybug (2024.2.1) o superior
2. **JDK 11** o superior
3. **Token de MercadoLibre**: reemplazar `YOUR_ACCESS_TOKEN_HERE` en `app/src/main/java/.../core/AppConfig.kt`

```kotlin
object AppConfig {
    const val ACCESS_TOKEN = "TU_TOKEN_AQUI"
}
```

> El token de la API de Mercado Libre expira cada 6 horas. Podes obtener uno desde [developers.mercadolibre.com.ar](https://developers.mercadolibre.com.ar/).

## Compilar y ejecutar

```bash
# Clonar el repositorio
git clone https://github.com/ADRIELCELSOROSALES/AppChallengeMELI.git

# Abrir en Android Studio y configurar el token en AppConfig.kt

# Compilar desde terminal (opcional)
./gradlew assembleDebug

# Ejecutar tests
./gradlew testDebugUnitTest
```
