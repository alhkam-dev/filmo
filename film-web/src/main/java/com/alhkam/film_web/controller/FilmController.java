package com.alhkam.film_web.controller;

import com.alhkam.film_web.domain.ArtistType;
import com.alhkam.film_web.dto.FilmCreationDTO;
import com.alhkam.film_web.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

  private final ArtistService artistService;

  @GetMapping("/films-create")
  public String showMovieCreateForm(Model model) {
    model.addAttribute("filmCreationDTO", FilmCreationDTO.builder().build());

    loadDropdowns(model);

    return "filmo/films/films-create";
  }

  private void loadDropdowns(Model model) {
    model.addAttribute("directors", artistService.findArtistsByType(ArtistType.DIRECTOR));
    model.addAttribute("actors", artistService.findArtistsByType(ArtistType.ACTOR));
  }
}
