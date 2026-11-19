# Guía de defensa – Trato Hecho Mobile (15 min, sin IA)

## Antes de entrar (checklist)
1. Encender MySQL: `D:\mysql-tratohecho\start-mysql.bat` (MariaDB de XAMPP con datos en D:). En el lab: XAMPP → Start MySQL, y crear base/usuario (README §4).
2. Microservicio: `cd backend/tratohecho-api` → `gradlew.bat bootRun` (con JDK 17: `set JAVA_HOME=C:\Program Files\Java\jdk-17`). Probar `http://localhost:8080/api/clientes` → `[]`.
3. Emulador Medium_Phone o celular. Celular real: cambiar `URL_MICROSERVICIO` a `http://IP-DEL-PC:8080/`.
4. Abrir en Android Studio: `AppViewModel`, `TratoHechoRepository`, `TratoHechoApi`, `RetrofitInstance`, `ApiCrudTest`, `build.gradle.kts` (app).

## Mapa mental (60 segundos)
App Compose → **AppViewModel** (estado + corrutinas) → **TratoHechoRepository** → (**Retrofit** `TratoHechoApi` → microservicio Spring → MySQL) y (**Room** como copia local).
- Escritura: primero microservicio; si responde bien, se actualiza Room. Si falla, no se guarda y se avisa con un Toast.
- Lectura: Room (Flow) para pintar; `sincronizar()` al abrir la app baja todo del microservicio (upsert + borra lo que ya no existe; conserva la foto local).
- **API externa vs microservicio**: externa = mindicador.cl, no es nuestra, solo lectura (dólar/UF), `IndicadoresApi`. Microservicio = nuestro, CRUD, persiste en MySQL, `TratoHechoApi`.

## Backend (Spring Boot + Kotlin) – `backend/tratohecho-api`
- Capas: `controller` (rutas REST) → `service` (reglas: total, cliente existe) → `repository` (Spring Data JPA) → `model` (entidades `Cliente`, `Cotizacion` + `LineaCotizacion` embebida en tabla `lineas_cotizacion`).
- `dto/Dtos.kt`: validaciones (`@NotBlank`, `@Email`, `@Min`…). `ErrorHandler` devuelve 400 con los campos con error. 404 con `ResponseStatusException`.
- `application.properties`: URL/usuario/clave de MySQL, `ddl-auto=update` crea tablas solo.
- Total = suma(cantidad×precio) × (1−desc%) × (1+IVA%) en `CotizacionService.calcularTotal`.
- Tests: `ApiCrudTest` (MockMvc + H2 en memoria, `src/test/resources/application.properties`). 97 % líneas.

## Qué se mockea en los tests
- App: `TratoHechoRepositoryTest` mockea `TratoHechoApi`, `IndicadoresApi` y los DAO de Room (MockK). `AppViewModelTest` mockea el repositorio y usa `Dispatchers.setMain(UnconfinedTestDispatcher())`. Se mockea para probar solo la lógica propia, sin red ni base de datos. Cobertura: `gradlew :app:cobertura` (86 %).
- Backend: no se mockea; se prueba la pila completa con H2.

## Firma del APK (cómo se generó)
1. `keytool -genkeypair -keystore keystore/tratohecho.jks -alias tratohecho -keyalg RSA -keysize 2048 -validity 10000`
2. En `app/build.gradle.kts`: `signingConfigs { create("release") {storeFile, storePassword, keyAlias, keyPassword} }` y `buildTypes.release.signingConfig = ...`
3. `gradlew :app:assembleRelease` → `app/build/outputs/apk/release/app-release.apk`. Verificación: `apksigner verify --print-certs`.

## Demostración en vivo (orden sugerido)
1. Crear cliente en la app → mostrar en MySQL (`select * from clientes`) o `GET /api/clientes`.
2. Crear cotización con 2 líneas → ver el JSON con `total`.
3. Cambiar estado (PATCH) y eliminar (DELETE) desde la app.
4. Si el docente pide insertar/actualizar/eliminar: cliente = crear/ficha/«Eliminar cliente»; cotización = crear/estado/«Eliminar cotizacion». «Actualizar» de cliente = registrar visita (PUT).
5. Correr tests: `gradlew test` (backend) y `gradlew :app:testDebugUnitTest`.

## Ejercicios de modificación en vivo (practicarlos)
1. **Nuevo endpoint** `GET /api/clientes/buscar?nombre=ana`: en `ClienteRepository` añadir `fun findByNombreContainingIgnoreCase(nombre: String): List<Cliente>`; en `ClienteService` `fun buscar(n: String) = repo.findByNombreContainingIgnoreCase(n)`; en `ClienteController` `@GetMapping("/buscar") fun buscar(@RequestParam nombre: String) = service.buscar(nombre)`. Añadir en `TratoHechoApi`: `@GET("api/clientes/buscar") suspend fun buscarClientes(@Query("nombre") nombre: String): List<ClienteDto>`.
2. **Nuevo campo** `direccion` en cliente: `Entidades.kt` (var), `ClienteRequest`, `aEntidad`; en la app `ClienteDto` y `Cliente` (Room: subir `version` y migración, o desinstalar la app).
3. **Corregir/ajustar una validación**: p. ej. teléfono mínimo 8 dígitos con `@field:Size(min = 8)` en `ClienteRequest`; o cantidad máxima con `@field:Max`.
4. **Agregar un test**: en `AppViewModelTest` copiar un caso y cambiar lo esperado; o en `ApiCrudTest` un test que cree un cliente y haga `get` esperando 200.
5. **Cambiar el puerto**: `server.port` en `application.properties` y `URL_MICROSERVICIO` en `RetrofitInstance`.
6. **Cambiar descuento/IVA por defecto**: `CotizacionRequest.impuestoPorcentaje` (backend).

## Preguntas típicas
- *¿Por qué `10.0.2.2`?* Es la IP con la que el emulador ve el localhost del PC. `usesCleartextTraffic=true` en el Manifest permite http.
- *¿Por qué Room si hay MySQL?* Copia local para mostrar rápido y sin conexión; la fuente de verdad es el microservicio.
- *¿Qué es `suspend`?* Función de corrutina: se llama sin bloquear la UI desde `viewModelScope.launch`.
- *¿Qué hace `@Upsert`?* Inserta o actualiza según la clave primaria (sincronización sin borrar cotizaciones por CASCADE).
- *¿Dónde está el JSON ↔ objeto?* `GsonConverterFactory` en Retrofit; en Spring, Jackson (`jackson-module-kotlin`).
- *Git*: un integrante; mostrar commits, rama main y (si se crean) ramas `feature/*`..

## Banco de preguntas para ensayar (responde en voz alta)
1. ¿Qué dependencias usa el backend y para qué? (web = REST, data-jpa = acceso a BD, validation = anotaciones, mysql-connector = driver, h2 = solo tests).
2. ¿Qué pasa si el microservicio está apagado y guardo un cliente? (Retrofit lanza excepción, el repositorio no toca Room, el ViewModel muestra el Toast).
3. ¿Dónde se define la URL base y cómo la cambio para un celular real?
4. ¿Qué diferencia hay entre `@Entity` de Room y `@Entity` de JPA? (misma anotación, distinto framework: local SQLite vs MySQL remoto).
5. ¿Por qué el repositorio escribe primero en remoto y después en Room?
6. ¿Qué hace `ddl-auto=update`? ¿Y `ErrorHandler`?
7. ¿Qué verifica `ApiCrudTest`? ¿Por qué usa H2?
8. ¿Por qué se mockea `TratoHechoApi` en los tests de la app?
9. ¿Cómo se genera el APK firmado y dónde está el .jks?
10. Muéstrame en Git los commits y ramas; ¿qué hay en el Trello?
