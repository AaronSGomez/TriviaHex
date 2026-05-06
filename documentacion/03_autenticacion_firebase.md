# Guia de autenticacion Google + Firebase (Web y Android)

Este documento resume los cambios aplicados para tener login con Google en Flutter (web y Android) y validacion de token en backend Spring Boot.

Objetivo:
- Web: autenticar con Google via Firebase Auth (popup) y enviar Firebase ID token al backend.
- Android: autenticar con Google API, enlazar en Firebase Auth y enviar Firebase ID token al backend.
- Backend: validar Firebase ID token (y mantener fallback a Google ID token legado) para crear/actualizar usuario y emitir JWT propio.

## 1) Arquitectura final

Flujo general:
1. Cliente Flutter obtiene credencial Google.
2. Cliente inicia sesion en Firebase Auth.
3. Cliente obtiene Firebase ID token (`user.getIdToken(true)`).
4. Cliente envia token a `POST /api/auth/google`.
5. Backend valida token, crea/actualiza usuario local, y devuelve JWT propio de la app.

Notas:
- El backend NO usa el JWT de Firebase como sesion interna.
- El backend emite su propio JWT para autorizacion de la API.

## 2) Cambios en Flutter (Web)

### 2.1 Dependencias
Agregar en `pubspec.yaml` del frontend:
- `firebase_auth`
- `google_sign_in`
- `google_sign_in_web`

### 2.2 Login web correcto
En web, NO usar `google_sign_in.authenticate()` como flujo principal.
Usar Firebase web popup:

```dart
final userCredential = await FirebaseAuth.instance.signInWithPopup(
  GoogleAuthProvider(),
);

final user = userCredential.user;
if (user == null) {
  throw Exception('No se pudo obtener usuario Firebase');
}

final firebaseIdToken = await user.getIdToken(true);
if (firebaseIdToken == null || firebaseIdToken.isEmpty) {
  throw Exception('Firebase no devolvio token valido');
}

// Enviar firebaseIdToken al backend
```

### 2.3 CSP para despliegue web
Si usas cabeceras CSP (ejemplo Vercel), permitir al menos:
- `script-src`: `https://apis.google.com`, `https://accounts.google.com`, `https://www.gstatic.com`
- `frame-src`: `https://accounts.google.com`, `https://*.firebaseapp.com`
- `connect-src`: `https://*.googleapis.com`, `https://www.gstatic.com`

Sin esto, Google/Firebase puede fallar por bloqueo de scripts/iframes.

## 3) Cambios en Flutter (Android)

### 3.1 Login Android recomendado
En Android, usar Google Sign-In + Firebase Auth:

```dart
final googleUser = await GoogleSignIn.instance.authenticate(
  scopeHint: const ['email', 'profile'],
);
if (googleUser == null) {
  throw Exception('Login cancelado');
}

final googleAuth = await googleUser.authentication;
final idToken = googleAuth.idToken;
if (idToken == null || idToken.isEmpty) {
  throw Exception('Google no devolvio idToken');
}

final credential = GoogleAuthProvider.credential(idToken: idToken);
final userCredential = await FirebaseAuth.instance.signInWithCredential(credential);
final firebaseUser = userCredential.user;
if (firebaseUser == null) {
  throw Exception('No se pudo obtener usuario Firebase');
}

final firebaseIdToken = await firebaseUser.getIdToken(true);
if (firebaseIdToken == null || firebaseIdToken.isEmpty) {
  throw Exception('Firebase no devolvio token valido');
}

// Enviar firebaseIdToken al backend
```

### 3.2 Configuracion minima Android
- Proyecto Firebase vinculado a la app Android.
- `google-services.json` en `android/app/`.
- SHA-1/SHA-256 de firma registradas en Firebase Console.
- Google Sign-In habilitado en Firebase Authentication.

## 4) Cambios en Spring Boot (backend)

### 4.1 Dependencias Maven
En `pom.xml`:

```xml
<dependency>
  <groupId>com.google.firebase</groupId>
  <artifactId>firebase-admin</artifactId>
  <version>9.2.0</version>
</dependency>

<dependency>
  <groupId>io.grpc</groupId>
  <artifactId>grpc-context</artifactId>
  <version>1.63.0</version>
</dependency>
```

Importante:
- `grpc-context` evita el error de runtime `NoClassDefFoundError: io/grpc/Context` al verificar tokens Firebase.

### 4.2 Inicializacion Firebase Admin
Crear configuracion de arranque (ejemplo `FirebaseConfig`) que:
- lea `GOOGLE_APPLICATION_CREDENTIALS` o propiedad equivalente,
- cargue el JSON de service account,
- ejecute `FirebaseApp.initializeApp(...)` una sola vez,
- registre logs claros si la ruta no existe o apunta a directorio.

### 4.3 Servicio de verificacion Firebase token
Crear `FirebaseTokenVerifierService`:
- `FirebaseAuth.getInstance().verifyIdToken(idToken)`
- extraer `email`, `name`, `uid`
- retornar `Optional.empty()` en token invalido
- capturar `FirebaseAuthException` y tambien excepciones runtime para no provocar 500 innecesarios

### 4.4 Servicio de auth
En `AuthService.loginWithGoogle(String idToken)`:
1. Intentar validar como Firebase token.
2. Si valida, usar `email/name` para crear o actualizar usuario local.
3. Si no valida, fallback a verificacion Google token legado (compatibilidad).
4. Emitir JWT propio de la app y devolver `AuthenticatedPlayer`.

## 5) Docker y despliegue backend

En `docker-compose.yml` del backend:

```yaml
environment:
  - GOOGLE_APPLICATION_CREDENTIALS=/app/firebase.json
volumes:
  - ./app/firebase.json:/app/firebase.json:ro
```

Requisitos:
- En el servidor debe existir archivo real: `./app/firebase.json` (no directorio).
- Reiniciar con build:

```bash
docker compose down
docker compose up -d --build
```

## 6) Endpoint y contrato

Endpoint usado por cliente:
- `POST /api/auth/google`

Payload minimo:

```json
{
  "idToken": "<FIREBASE_ID_TOKEN>"
}
```

Respuesta esperada:
- JWT propio backend
- datos de usuario local (id, name, mail)

## 7) Checklist de verificacion

1. Frontend web:
- Login Google abre popup
- `Step 2/3/4` en logs cliente
- No errores CSP bloqueando Google/Firebase

2. Backend:
- Log de inicio: `firebase_initialized_successfully`
- `POST /api/auth/google` responde 200
- Sin `NoClassDefFoundError: io/grpc/Context`

3. Datos:
- Usuario visible en Firebase Authentication
- Usuario creado/actualizado en BD de la app
- JWT backend funcional para endpoints protegidos

## 8) Errores comunes y solucion rapida

1. `authenticate is not supported on the web`
- Causa: usar `google_sign_in.authenticate()` en web.
- Solucion: usar `FirebaseAuth.signInWithPopup(GoogleAuthProvider())`.

2. `NoClassDefFoundError: io/grpc/Context`
- Causa: falta dependencia runtime.
- Solucion: agregar `io.grpc:grpc-context` en `pom.xml` y reconstruir jar.

3. `firebase.json (Is a directory)`
- Causa: bind mount mal definido.
- Solucion: montar archivo real `./app/firebase.json:/app/firebase.json:ro`.

4. Error CSP con `apis.google.com` o `firebaseapp.com`
- Causa: politicas CSP incompletas.
- Solucion: ampliar `script-src`, `frame-src`, `connect-src` para dominios Google/Firebase.

## 9) Recomendaciones de seguridad

- No subir `firebase.json` al repositorio.
- Rotar credenciales si alguna clave privada se expuso.
- Usar variable de entorno/secreto del servidor para la ruta o para inyectar el JSON en runtime.
- Mantener reglas de CORS y CSP estrictas, solo con dominios necesarios.

---

Si quieres replicarlo en otro proyecto, copia este orden:
1) Firebase proyecto y credenciales,
2) Flutter login (web popup + android google/fb),
3) Backend verificador Firebase + fallback Google,
4) Docker mount de credenciales,
5) test end-to-end con usuario nuevo.
