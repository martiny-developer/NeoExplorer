package com.project.neoexplorer.controller;

import com.project.neoexplorer.dto.APODDTO;
import com.project.neoexplorer.dto.AsteroidDTO;
import com.project.neoexplorer.dto.EpicDTO;
import com.project.neoexplorer.service.NasaService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NasaController {

    private final NasaService nasaService;

    // Ya no es necesario inyectar TranslationService aquí
    public NasaController(NasaService nasaService) {
        this.nasaService = nasaService;
    }

    @GetMapping("/")
    public String inicio(@RequestParam(value = "date", required = false) String date, Model model) {
        String hoy = LocalDate.now().toString();
        String masSieteDias = LocalDate.now().plusDays(7).toString();

        // NasaService ya devuelve la APOD con su título y descripción traducidos al español
        APODDTO apod = nasaService.obtenerImagenDelDia(date);

        List<AsteroidDTO> asteroides = nasaService.obtenerAsteroides(hoy, masSieteDias);
        List<EpicDTO> epic = nasaService.obtenerImagenesTierra();

        model.addAttribute("apod", apod);
        model.addAttribute("fechaSeleccionada", date != null ? date : hoy);
        model.addAttribute("asteroides", asteroides);
        model.addAttribute("epicImagenes", epic);

        return "index";
    }
}