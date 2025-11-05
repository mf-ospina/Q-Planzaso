# 🎉 Q-Planzaso: Tu App de Eventos y Planes

**Q-Planzaso** es una aplicación móvil nativa para Android que permite a los usuarios descubrir, crear y compartir eventos en su área.  
Desde conciertos y festivales hasta reuniones comunitarias, Q-Planzaso conecta a las personas a través de experiencias compartidas.  
Su objetivo es centralizar los mejores planes y actividades locales en una sola aplicación intuitiva y moderna.

---

## Características Principales

- **Exploración de Eventos:** Pantalla de inicio dinámica con carrusel, categorías y listado general.  
- **Búsqueda y Filtrado:** Búsqueda en tiempo real por nombre o descripción.  
- **Navegación por Categorías:** Filtra por *Música*, *Deportes*, *Arte*, *Comedia* y más.  
- **Creación de Eventos:**  
  - Nombre, descripción y categoría.  
  - Selección de ubicación mediante dirección (geocodificación).  
  - Subida de imágenes desde la galería.  
  - Definición de precio y fechas.  
- **Resumen y Confirmación:** Pantalla de vista previa antes de publicar.  
- **Detalles del Evento:** Muestra información completa usando `Gson` para pasar objetos entre pantallas.  
- **Diseño Moderno:** Construido 100% con Jetpack Compose y Material Design 3.

---

## Arquitectura del Proyecto

El proyecto implementa el patrón **MVVM (Model - View - ViewModel)**, lo que permite una separación clara entre la lógica de negocio y la interfaz de usuario.

- **Model:**  
  Define las clases de datos (`Evento`, `Categoria`) y la capa de acceso a datos (repositorios que interactúan con Firebase Firestore y Storage).

- **ViewModel:**  
  Maneja la lógica de presentación y los estados de la UI.  
  Usa `StateFlow` para mantener actualizada la interfaz en tiempo real cuando cambian los datos.

- **View (Compose UI):**  
  Pantallas construidas con funciones `@Composable`, totalmente declarativas y reactivas.

---

## Stack Tecnológico y Arquitectura

| Tecnología | Descripción |
|-------------|-------------|
| **Lenguaje** | [Kotlin](https://kotlinlang.org/) |
| **Arquitectura** | MVVM (Model-View-ViewModel) — separa lógica de UI |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) |
| **Navegación** | [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) |
| **Asincronía** | [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) + [StateFlow](https://kotlinlang.org/docs/flow.html) |
| **Backend (BaaS)** | [Firebase](https://firebase.google.com/) con Authentication, Firestore y Storage |
| **Inyección de dependencias** | `viewModel()` de `androidx.lifecycle` |
| **Imágenes** | [Coil](https://coil-kt.github.io/coil/) |
| **Serialización** | [Gson](https://github.com/google/gson) |


---

## Estructura del Proyecto
app/
├── data/

│ ├── model/ # Clases de datos (Evento, Categoria)

│ └── repository/ # Lógica de conexión con Firebase (EventoRepository, CategoriaRepository)
│
├── ui/

│ ├── components/ # Componentes reutilizables (QTopBar, EventCard, etc.)

│ ├── screens/

│ │ ├── home/ # Pantalla principal con categorías, buscador y eventos

│ │ ├── detailEvent/ # Pantalla de detalles de evento

│ │ └── bottomNavigationMod/ # Módulo de navegación y vistas principales

│ └── viewModel/ # ViewModels que gestionan el estado de cada pantalla
│
├── MainActivity.kt # Entrada principal de la aplicación

├── navigation/ # Controlador de rutas (NavController)

└── utils/ # Funciones auxiliares y adaptadores

---
# ⚙️ Configuración del Proyecto

## 🧩 Prerrequisitos

- **Android Studio** Iguana | 2023.2.1 o superior  
- **JDK** 17 o superior  
- **Dispositivo físico o emulador** con Android API 26+  
- **Conexión a Internet** para sincronizar dependencias y servicios de Firebase/Maps  

---

## 🚀 Instalación y Ejecución

1️⃣ Clonar el repositorio


git clone https://github.com/mf-ospina/Q-Planzaso.git
2️⃣ Abrir en Android Studio
Abre Android Studio

Selecciona File → Open...

Elige la carpeta del proyecto Q-Planzaso

3️⃣ Sincronizar dependencias
Android Studio descargará automáticamente todas las librerías necesarias desde Gradle.
Si no lo hace, selecciona File → Sync Project with Gradle Files.

🔧 Configuración inicial (Firebase y Google Maps)
Antes de ejecutar la aplicación, asegúrate de configurar las claves necesarias para Firebase y Google Maps.
Estos archivos no deben subirse al repositorio (ya están ignorados en .gitignore), pero son obligatorios para que la app funcione correctamente.

📁 1. Configurar Firebase
Accede a la Consola de Firebase.

Selecciona el proyecto Q-Planzaso (o crea uno nuevo).

En el menú lateral, ve a:
Configuración del proyecto → Tus apps → Android

Descarga el archivo google-services.json.

Colócalo dentro de esta ruta del proyecto:

bash
Copiar código
app/google-services.json
Verifica que en el archivo build.gradle (app) esté incluida la siguiente línea al final:

gradle
Copiar código
apply plugin: 'com.google.gms.google-services'
En el archivo build.gradle (project), asegúrate de tener este classpath:

gradle
Copiar código
classpath 'com.google.gms:google-services:4.4.2'
🗺️ 2. Configurar Google Maps API Key
Abre el archivo local.properties (ubicado en la raíz del proyecto).

Agrega la siguiente línea (reemplazando la clave por tu propia API Key):

properties
MAPS_API_KEY=TU_API_KEY_DE_GOOGLE_MAPS
Asegúrate de no subir este archivo al repositorio, ya que contiene información sensible.
Por defecto, local.properties ya está incluido en .gitignore.

En el archivo AndroidManifest.xml, verifica que se esté utilizando correctamente:

<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${MAPS_API_KEY}" />
    
✅ Verificación rápida
Después de configurar ambos pasos:

✅ La app debe conectarse correctamente con Firebase (sin errores de inicialización).

🗺️ Los mapas deben cargar correctamente al abrir una pantalla que los use.

🚫 No deben aparecer errores como "API key not valid" o "FirebaseApp initialization error".

▶️ 3️⃣ Ejecutar la aplicación
Conecta un dispositivo físico o inicia un emulador Android.

En Android Studio, selecciona el módulo app.

Presiona ▶ Run o usa el atajo Shift + F10.

Espera a que compile… y ¡listo! 🎉


⚠️ Importante:
Si otro desarrollador clona el proyecto, deberá agregar manualmente su propio
google-services.json y su MAPS_API_KEY en local.properties.

---
## 🔐 Archivos y Servicios Externos

| Servicio / Herramienta     | Archivo o Configuración                         | Ubicación en el Proyecto                          | Descripción / Función Principal                                                                 | ¿Se Sube a Git? |
|-----------------------------|--------------------------------------------------|----------------------------------------------------|--------------------------------------------------------------------------------------------------|-----------------|
| **Firebase (General)**      | `google-services.json`                          | `/app/google-services.json`                        | Archivo de configuración que conecta la app con tu proyecto de Firebase.                        | ❌ **No** — contiene credenciales privadas |
| **Firestore Database**      | Configurado desde Firebase Console              | —                                                  | Base de datos NoSQL en la nube para almacenar eventos, usuarios, favoritos, notificaciones.     | ✅ Configuración remota |
| **Firebase Authentication** | Configurado desde Firebase Console              | —                                                  | Gestiona inicio de sesión (email, Google, etc.) y autenticación segura de usuarios.              | ✅ Configuración remota |
| **Firebase Storage**        | Configurado desde Firebase Console              | —                                                  | Almacena imágenes y recursos multimedia de eventos y usuarios.                                   | ✅ Configuración remota |
| **Firebase Cloud Messaging (opcional)** | Incluido en el proyecto Firebase                | —                                                  | Permite enviar notificaciones push (no usado directamente, pero disponible si se habilita).      | ✅ Configuración remota |
| **Google Maps SDK**         | `local.properties`                              | `/local.properties`                                | Define la variable `MAPS_API_KEY` con tu clave de Google Maps para mostrar mapas en la app.     | ❌ **No** — contiene claves sensibles |
| **Permisos de Maps API Key**| Configuración en Google Cloud Console           | —                                                  | Limita el uso de la clave `MAPS_API_KEY` solo al paquete `com.planapp.qplanzaso`.               | ✅ Configuración remota |
| **Metadata de API Key**     | `AndroidManifest.xml`                           | `/app/src/main/AndroidManifest.xml`                | Vincula la API Key de Google Maps con la app mediante un `<meta-data>` seguro.                   | ✅ **Sí** |
| **Gradle Plugin de Google Services** | `build.gradle (app)` y `build.gradle (project)` | `/app/` y raíz del proyecto                        | Permite la integración de Firebase en la app a través del plugin `com.google.gms.google-services`. | ✅ **Sí** |

---
