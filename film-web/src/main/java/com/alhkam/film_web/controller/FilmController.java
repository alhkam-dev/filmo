package com.alhkam.film_web.controller;

import com.alhkam.film_web.domain.ArtistType;
import com.alhkam.film_web.dto.FilmDetailsDTO;
import com.alhkam.film_web.dto.FilmFormDTO;
import com.alhkam.film_web.dto.FilmPreviewDTO;
import com.alhkam.film_web.dto.RatingFormDTO;
import com.alhkam.film_web.security.FilmoUserDetailsService;
import com.alhkam.film_web.service.ArtistService;
import com.alhkam.film_web.service.FilmService;
import com.alhkam.film_web.service.RatingService;
import com.alhkam.film_web.validation.OnCreate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("filmo/films")
@RequiredArgsConstructor
public class FilmController {

  private final ArtistService artistService;
  private final FilmService filmService;
  private final RatingService ratingService;

  @GetMapping("/films-create")
  public String showCreateForm(Model model) {
    model.addAttribute("filmFormDTO", FilmFormDTO.builder().build());
    model.addAttribute("isEdit", false);

    loadDropdowns(model);

    return "filmo/films/films-form";
  }

  @PostMapping("/films-create")
  public String createFilm(
      @Validated({Default.class, OnCreate.class}) @ModelAttribute("filmFormDTO")
          FilmFormDTO filmFormDTO,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (filmFormDTO.poster() == null || filmFormDTO.poster().isEmpty()) {
      bindingResult.rejectValue("poster", "film.validation.poster.notNull", "Poster is mandatory");
    }

    if (bindingResult.hasErrors()) {
      loadDropdowns(model);

      return "filmo/films/films-form";
    }

    try {
      filmService.saveFilm(filmFormDTO);
      redirectAttributes.addFlashAttribute("filmCreationSuccessMessage", true);

      return "redirect:/filmo/films/films-create";
    } catch (RuntimeException e) {
      model.addAttribute("filmCreationError", true);
      loadDropdowns(model);

      return "filmo/films/films-form";
    }
  }

  @GetMapping("/films-edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    try {
      FilmFormDTO filmFormDTO = filmService.findById(id);

      model.addAttribute("filmFormDTO", filmFormDTO);
      model.addAttribute("isEdit", true);

      loadDropdowns(model);

      return "filmo/films/films-form";
    } catch (EntityNotFoundException e) {
      return "redirect:/filmo/films/films-search?error=notFound";
    }
  }

  @PostMapping("/films-edit/{id}")
  public String updateFilm(
      @PathVariable Long id,
      @Validated(Default.class) @ModelAttribute("filmFormDTO") FilmFormDTO filmFormDTO,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (filmFormDTO.poster() != null && !filmFormDTO.poster().isEmpty()) {
      String contentType = filmFormDTO.poster().getContentType();
      if (contentType == null || !contentType.startsWith("image/")) {
        bindingResult.rejectValue("poster", "film.validation.poster.validImage");
      }
    }

    if (bindingResult.hasErrors()) {
      model.addAttribute("isEdit", true);
      loadDropdowns(model);
      return "filmo/films/films-form";
    }

    try {
      filmService.updateFilm(id, filmFormDTO);
      redirectAttributes.addFlashAttribute("filmUpdateSuccessMessage", true);
      return "redirect:/filmo/films/films-search";
    } catch (RuntimeException e) {
      model.addAttribute("filmEditError", true);
      model.addAttribute("isEdit", true);
      loadDropdowns(model);
      return "filmo/films/films-form";
    }
  }

  @GetMapping("/films-search")
  public String searchFilms(
      @RequestParam(value = "searchText", required = false) String searchText, Model model) {
    model.addAttribute("currentSearch", searchText);

    List<FilmPreviewDTO> filmsFound = filmService.searchFilmsByTitle(searchText);
    model.addAttribute("filmsFound", filmsFound);

    return "filmo/films/films-search";
  }

  @GetMapping("/films-details/{id}")
  public String showFilmDetails(@PathVariable Long id, Model model) {
    try {
      FilmDetailsDTO filmDetailsDTO = filmService.getFilmDetails(id);
      model.addAttribute("filmDetailsDTO", filmDetailsDTO);

      var auth = SecurityContextHolder.getContext().getAuthentication();
      Integer userRatingScore = null;

      if (auth != null && auth.isAuthenticated()) {
        if (auth.getPrincipal()
            instanceof FilmoUserDetailsService.CustomUserDetails customUserDetails) {
          Long userId = customUserDetails.getId();

          userRatingScore = ratingService.getUserRatingForFilm(userId, id);
        }
      }

      model.addAttribute("userRatingScore", userRatingScore);

      if (userRatingScore == null) {
        model.addAttribute("ratingFormDTO", RatingFormDTO.builder().build());
      }

      return "filmo/films/films-details";
    } catch (EntityNotFoundException e) {
      return "redirect:/filmo/films/films-search?error=notFound";
    }
  }

  private void loadDropdowns(Model model) {
    model.addAttribute("directors", artistService.findArtistsByType(ArtistType.DIRECTOR));
    model.addAttribute("actors", artistService.findArtistsByType(ArtistType.ACTOR));
  }
}
