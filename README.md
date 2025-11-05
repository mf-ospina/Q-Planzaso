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

## Configuración del Proyecto

### Prerrequisitos

- Android Studio Iguana | 2023.2.1 o superior  
- JDK 17 o superior  
- Dispositivo físico o emulador con Android API 26+

---

##  Instalación y Ejecución

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/mf-ospina/Q-Planzaso.git

2. **Abrir en Android Studio**
  File → Open → Selecciona la carpeta del proyecto.

3. **Sincronizar dependencias**
  Android Studio descargará las librerías necesarias desde Gradle.

4. **Ejecutar la app**
  Selecciona un emulador o dispositivo físico.
  Presiona > Run.
