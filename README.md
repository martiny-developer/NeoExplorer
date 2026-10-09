# 🚀 NeoExplorer

**Sistema de monitoreo espacial** que consume tres APIs REST de la NASA para explorar imágenes astronómicas, fotografías satelitales de la Tierra y asteroides cercanos en tiempo real.

## 📌 Descripción

NeoExplorer es un portal interactivo conectado en tiempo real a las APIs abiertas de la NASA. Permite:

- Ver la imagen astronómica del día y buscar la de cualquier fecha.
- Consultar fotografías recientes de la Tierra completa tomadas desde el espacio.
- Monitorear asteroides cercanos a la Tierra con sus métricas y su nivel de peligrosidad.

El backend procesa las respuestas JSON de la NASA y las expone a una interfaz dinámica con temática espacial.

---

## ✨ Funcionalidades

### 🌌 APOD – Astronomy Picture of the Day
- Muestra la fotografía o imagen astronómica del día con título y explicación.
- Buscador por fecha para consultar imágenes de días anteriores.

### 🌍 EPIC – Earth Polychromatic Imaging Camera
- Imágenes de la Tierra completa capturadas por el satélite DSCOVR.
- Galería con la fecha de captura de cada fotografía.

### ☄️ NeoWs – Near Earth Object Web Service
- Lista de asteroides que se aproximan a la Tierra.
- Por cada asteroide: nombre, diámetro estimado, velocidad y distancia de aproximación.
- Indicador de **peligroso** según la clasificación de la NASA.

---

## 🛠️ Tecnologías

| Capa | Tecnología |
|------|------------|
| Backend | Java, Spring Boot |
| Consumo de APIs | REST (JSON) |
| Frontend | HTML, CSS, JavaScript |
| Fuente de datos | [NASA Open APIs](https://api.nasa.gov/) |
| Control de versiones | Git / GitHub |

> Ajusta esta tabla si usas otras librerías (Thymeleaf, Bootstrap, Maven/Gradle, etc.).

---

## 📋 Requisitos previos

- Java 17 o superior
- Maven (o el wrapper `mvnw` incluido)
- API key de la NASA (gratuita): <https://api.nasa.gov/>

---

## ⚙️ Instalación y ejecución

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/<tu-usuario>/neoexplorer.git
   cd neoexplorer
   ```

2. **Configurar la API key** en `src/main/resources/application.properties`:
   ```properties
   nasa.api.key=TU_API_KEY
   ```
   > También puedes usar una variable de entorno (`NASA_API_KEY`) para no subir la clave al repositorio.

3. **Ejecutar la aplicación**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Abrir en el navegador**: <http://localhost:8080>

---

## 🔌 APIs de la NASA utilizadas

| API | Propósito | Documentación |
|-----|-----------|---------------|
| APOD | Imagen astronómica del día | <https://api.nasa.gov/> |
| EPIC | Imágenes de la Tierra desde el espacio | <https://epic.gsfc.nasa.gov/about/api> |
| NeoWs | Asteroides cercanos a la Tierra | <https://api.nasa.gov/> |

---

