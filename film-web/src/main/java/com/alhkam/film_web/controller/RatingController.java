package com.alhkam.film_web.controller;

import com.alhkam.film_web.dto.RatingFormDTO;
import com.alhkam.film_web.security.FilmoUserDetailsService;
import com.alhkam.film_web.service.RatingService;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/filmo/ratings")
public class RatingController {

  private final RatingService ratingService;

  @PostMapping("/films/{filmId}")
  public String saveRating(
      @PathVariable Long filmId,
      @Valid @ModelAttribute("ratingFormDTO") RatingFormDTO ratingFormDTO,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      String errorMsg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();

      redirectAttributes.addFlashAttribute("validationError", errorMsg);
      return "redirect:/filmo/films/films-details/" + filmId;
    }

    Object principal =
        Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
            .getPrincipal();

    Long userId;

    if (principal instanceof FilmoUserDetailsService.CustomUserDetails customUserDetails) {
      userId = customUserDetails.getId();
    } else {
      throw new IllegalStateException("The user is not authenticated correctly.");
    }

    try {
      ratingService.saveRating(filmId, ratingFormDTO.getScore(), userId);

      redirectAttributes.addFlashAttribute("ratingSuccessMessage", "film.rating.score.success");
    } catch (IllegalStateException e) {
      redirectAttributes.addFlashAttribute("ratingErrorMessage", "rating.error.alreadyVoted");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("ratingErrorMessage", "film.rating.error.generic");
    }

    return "redirect:/filmo/films/films-details/" + filmId;
  }
}
