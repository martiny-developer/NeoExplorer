package com.project.neoexplorer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class TranslationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Traduce texto de inglés a español.
     * Si algo falla (cuota agotada, red, respuesta inválida), lanza TranslationException
     * en vez de devolver el texto sin traducir, para que Spring NO cachee un resultado fallido.
     */
    @Cacheable(value = "traducciones", key = "#textoOriginal")
    public String traducirAlEspanol(String textoOriginal) {
        if (textoOriginal == null || textoOriginal.isBlank()) {
            return textoOriginal;
        }

        // 1. Limpieza inicial de saltos de línea molestos
        String textoLimpio = textoOriginal.replaceAll("[\\r\\n]+", " ").trim();

        // Si es menor a 450 caracteres, se traduce directamente
        if (textoLimpio.length() <= 450) {
            return traducirBloque(textoLimpio);
        }

        // 2. Fragmentación por oraciones si supera el límite de MyMemory
        List<String> trozos = dividirEnTrozos(textoLimpio, 450);
        StringBuilder resultadoFinal = new StringBuilder();

        for (String trozo : trozos) {
            resultadoFinal.append(traducirBloque(trozo)).append(" ");
        }

        return resultadoFinal.toString().trim();
    }

    private String traducirBloque(String texto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("q", texto);
        map.add("langpair", "en|es");
        // Un email de contacto (válido o no, MyMemory solo valida el formato) sube la cuota
        // anónima de 5.000 a 50.000 palabras/día por IP, según la documentación de MyMemory.
        map.add("de", "neoexplorer@example.com");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        String url = "https://api.mymemory.translated.net/get";

        String response;
        try {
            response = restTemplate.postForObject(url, request, String.class);
        } catch (Exception e) {
            throw new TranslationException("No se pudo contactar a MyMemory: " + e.getMessage(), e);
        }

        if (response == null) {
            throw new TranslationException("Respuesta vacía de MyMemory");
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.has("responseData") && jsonNode.get("responseData").has("translatedText")) {
                String resultado = jsonNode.get("responseData").get("translatedText").asText();

                boolean esValido = resultado != null && !resultado.isBlank()
                        && !resultado.contains("MYMEMORY WARNING")
                        && !resultado.contains("INVALID LANGUAGE PAIR")
                        && !resultado.contains("QUERY LENGTH LIMIT EXCEEDED");

                if (esValido) {
                    return resultado;
                }
            }
        } catch (Exception e) {
            throw new TranslationException("Error al procesar respuesta de MyMemory", e);
        }

        throw new TranslationException("MyMemory no devolvió una traducción válida (posible límite de cuota)");
    }

    private List<String> dividirEnTrozos(String texto, int maxLongitud) {
        List<String> trozos = new ArrayList<>();
        String[] oraciones = texto.split("(?<=\\.)\\s+"); // Separa por puntos seguidos de espacio

        StringBuilder acumulador = new StringBuilder();

        for (String oracion : oraciones) {
            if (acumulador.length() + oracion.length() + 1 <= maxLongitud) {
                acumulador.append(oracion).append(" ");
            } else {
                if (acumulador.length() > 0) {
                    trozos.add(acumulador.toString().trim());
                    acumulador.setLength(0);
                }

                // Si una sola oración supera el límite, la corta por caracteres
                if (oracion.length() > maxLongitud) {
                    int inicio = 0;
                    while (inicio < oracion.length()) {
                        int fin = Math.min(inicio + maxLongitud, oracion.length());
                        trozos.add(oracion.substring(inicio, fin));
                        inicio = fin;
                    }
                } else {
                    acumulador.append(oracion).append(" ");
                }
            }
        }

        if (acumulador.length() > 0) {
            trozos.add(acumulador.toString().trim());
        }

        return trozos;
    }
}