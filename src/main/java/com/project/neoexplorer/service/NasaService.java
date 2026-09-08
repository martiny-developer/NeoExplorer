package com.project.neoexplorer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.neoexplorer.dto.APODDTO;
import com.project.neoexplorer.dto.AsteroidDTO;
import com.project.neoexplorer.dto.EpicDTO;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NasaService {

    @Value("${nasa.api.key:DEMO_KEY}")
    private String apiKey;

    @Autowired
    private TranslationService translationService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    private String realizarPeticionHttp(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) NeoExplorer/1.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    /**
     * Intenta traducir un texto; si MyMemory falla (cuota agotada, red, etc.)
     * devuelve el texto original en inglés en vez de propagar el error,
     * para que la sección completa (APOD/EPIC) no se caiga por un solo fallo de traducción.
     */
    private String intentarTraducir(String texto) {
        try {
            return translationService.traducirAlEspanol(texto);
        } catch (TranslationException e) {
            System.err.println("⚠️ Traducción no disponible ahora mismo, se muestra texto en inglés: " + e.getMessage());
            return texto;
        }
    }

    @Cacheable(value = "apod", key = "#fecha != null ? #fecha : 'hoy'")
    public APODDTO obtenerImagenDelDia(String fecha) {
        try {
            String url = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey;
            if (fecha != null && !fecha.trim().isEmpty()) {
                url += "&date=" + fecha;
            }
            String json = realizarPeticionHttp(url);
            JsonNode root = mapper.readTree(json);

            String tituloIngles = root.has("title") ? root.get("title").asText() : "Vista Cósmica APOD";
            String urlImg = root.has("url") ? root.get("url").asText() : "https://images-assets.nasa.gov/image/PIA12348/PIA12348~orig.jpg";
            String explicacionIngles = root.has("explanation") ? root.get("explanation").asText() : "Imagen astronómica del día provista por los telescopios orbitales de la NASA.";

            // Traducción de título y explicación al español (con respaldo si falla)
            String tituloEspanol = intentarTraducir(tituloIngles);
            String explicacionEspanol = intentarTraducir(explicacionIngles);

            return new APODDTO(tituloEspanol, urlImg, explicacionEspanol);
        } catch (Exception e) {
            System.err.println("⚠️ Alerta APOD: " + e.getMessage());
            return new APODDTO("Nebulosa de la Carina", "https://images-assets.nasa.gov/image/PIA12348/PIA12348~orig.jpg", "Fotografía capturada por el Telescopio Espacial mostrando guarderías estelares activas.");
        }
    }

    @Cacheable(value = "asteroides", key = "#fechaInicio + '_' + #fechaFin")
    public List<AsteroidDTO> obtenerAsteroides(String fechaInicio, String fechaFin) {
        List<AsteroidDTO> asteroides = new ArrayList<>();
        try {
            String url = "https://api.nasa.gov/neo/rest/v1/feed?start_date=" + fechaInicio + "&end_date=" + fechaFin + "&api_key=" + apiKey;
            String json = realizarPeticionHttp(url);
            JsonNode root = mapper.readTree(json);

            if (root != null && root.has("near_earth_objects")) {
                JsonNode neo = root.get("near_earth_objects");
                Iterator<String> fechas = neo.fieldNames();

                while (fechas.hasNext()) {
                    String fecha = fechas.next();
                    JsonNode lista = neo.get(fecha);

                    for (JsonNode asteroid : lista) {
                        String nombre = asteroid.path("name").asText("Asteroide NEO");
                        boolean peligroso = asteroid.path("is_potentially_hazardous_asteroid").asBoolean(false);

                        double diametroMinimo = asteroid.path("estimated_diameter").path("meters").path("estimated_diameter_min").asDouble(10.0);
                        double diametroMaximo = asteroid.path("estimated_diameter").path("meters").path("estimated_diameter_max").asDouble(30.0);

                        JsonNode approachArray = asteroid.path("close_approach_data");
                        double velocidad = 25000.0;
                        double distancia = 1500000.0;

                        if (approachArray.isArray() && approachArray.size() > 0) {
                            JsonNode approach = approachArray.get(0);
                            velocidad = approach.path("relative_velocity").path("kilometers_per_hour").asDouble(25000.0);
                            distancia = approach.path("miss_distance").path("kilometers").asDouble(1500000.0);
                        }

                        asteroides.add(new AsteroidDTO(nombre, diametroMinimo, diametroMaximo, velocidad, distancia, peligroso));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Alerta Asteroides NEO: " + e.getMessage());
        }

        if (asteroides.isEmpty()) {
            asteroides.add(new AsteroidDTO("(2026 XK1) Space Rock", 140.5, 310.2, 48210.5, 3210450.0, true));
            asteroides.add(new AsteroidDTO("(2026 DH2) Near Titan", 210.0, 480.0, 62100.8, 1980000.0, true));
            asteroides.add(new AsteroidDTO("(2026 AP4) Cosmos Explorer", 22.1, 49.8, 18900.2, 8540120.0, false));
            asteroides.add(new AsteroidDTO("(2026 BC9) Eros Prime", 85.0, 190.4, 31200.0, 4120800.0, false));
            asteroides.add(new AsteroidDTO("(2026 FG5) Vanguard", 112.3, 250.0, 39500.0, 5200000.0, false));
            asteroides.add(new AsteroidDTO("(2026 HQ9) Orpheus", 95.8, 210.1, 28400.0, 6100000.0, false));
        }

        return asteroides.stream()
                .sorted(Comparator.comparing(AsteroidDTO::isPeligroso).reversed()
                        .thenComparing(Comparator.comparing(AsteroidDTO::getDiametroMaximo).reversed()))
                .limit(6)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "epic", key = "T(java.time.LocalDate).now().toString()")
    public List<EpicDTO> obtenerImagenesTierra() {
        List<EpicDTO> imagenes = new ArrayList<>();
        try {
            // Incluimos la apiKey para evitar bloqueos por rate-limit
            String url = "https://api.nasa.gov/EPIC/api/natural?api_key=" + apiKey;
            String json = realizarPeticionHttp(url);
            JsonNode root = mapper.readTree(json);

            if (root != null && root.isArray() && root.size() > 0) {
                int limite = 0;
                // Recorremos en orden inverso para asegurar las imágenes más recientes (con hora) primero
                for (int i = root.size() - 1; i >= 0; i--) {
                    if (limite >= 4) break;

                    JsonNode item = root.get(i);
                    String fechaCompleta = item.path("date").asText(); // Ej: "2026-09-04 03:58:28"

                    if (fechaCompleta.length() >= 10) {
                        String fechaSolo = fechaCompleta.substring(0, 10);
                        String nombreImagen = item.path("image").asText();
                        String captionIngles = item.path("caption").asText("Vista de la Tierra desde el satélite DSCOVR");
                        String captionEspanol = intentarTraducir(captionIngles);

                        String[] partes = fechaSolo.split("-");
                        String imageUrl = "https://epic.gsfc.nasa.gov/archive/natural/"
                                + partes[0] + "/" + partes[1] + "/" + partes[2]
                                + "/jpg/" + nombreImagen + ".jpg";

                        // Pasamos fechaCompleta para que el HTML pinte tanto la fecha como la hora
                        imagenes.add(new EpicDTO(fechaCompleta, nombreImagen, captionEspanol, imageUrl));
                        limite++;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Alerta EPIC Tierra: " + e.getMessage());
        }

        return imagenes;
    }
}