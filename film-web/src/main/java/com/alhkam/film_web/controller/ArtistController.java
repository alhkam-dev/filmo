package com.alhkam.film_web.controller;

import com.alhkam.film_web.dto.ArtistCreationDTO;
import com.alhkam.film_web.exception.ArtistAlreadyExistsException;
import com.alhkam.film_web.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/filmo/artists")
@RequiredArgsConstructor
public class ArtistController {

  private final ArtistService artistService;

  @GetMapping("/artists-create")
  public String showArtistCreateForm(Model model) {
    model.addAttribute("artistCreationDTO", ArtistCreationDTO.builder().build());
    return "filmo/artists/artists-create";
  }

  @PostMapping("/artists-create")
  public String createArtist(
      @Valid @ModelAttribute("artistCreationDTO") ArtistCreationDTO artistCreationDTO,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      return "filmo/artists/artists-create";
    }

    try {
      artistService.createArtist(artistCreationDTO);

      redirectAttributes.addFlashAttribute("artistCreationSuccessMessage", true);

      return "redirect:/filmo/artists/artists-create";

    } catch (ArtistAlreadyExistsException e) {
      model.addAttribute("artistAlreadyExistsError", true);
      return "filmo/artists/artists-create";
    }
  }
}
