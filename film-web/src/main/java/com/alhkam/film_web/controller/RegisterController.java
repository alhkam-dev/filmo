package com.alhkam.film_web.controller;

import com.alhkam.film_web.dto.UserRegisterDTO;
import com.alhkam.film_web.exception.EmailAlreadyExistsException;
import com.alhkam.film_web.exception.UsernameAlreadyExistsException;
import com.alhkam.film_web.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegisterController {

  private final UserService userService;

  @GetMapping("/register")
  public String showRegisterForm(Model model) {
    model.addAttribute("userRegisterDTO", UserRegisterDTO.builder().build());
    return "filmo/register";
  }

  @PostMapping("/register")
  public String registerUser(
      @Valid @ModelAttribute("userRegisterDTO") UserRegisterDTO userRegisterDTO,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      return "filmo/register";
    }

    if (!userRegisterDTO.password().equals(userRegisterDTO.passwordConfirm())) {
      bindingResult.rejectValue(
          "passwordConfirm",
          "register.validation.passwordConfirm.noMatch",
          "Passwords do not match");
      return "filmo/register";
    }

    try {
      userService.registerUser(userRegisterDTO);

      redirectAttributes.addFlashAttribute("userRegistrationSuccesMessage", true);

      return "redirect:/register";

    } catch (UsernameAlreadyExistsException e) {
      bindingResult.rejectValue("username", "register.error.duplicateUsername", e.getMessage());
      return "filmo/register";
    } catch (EmailAlreadyExistsException e) {
      bindingResult.rejectValue("email", "register.error.duplicateEmail", e.getMessage());
      return "filmo/register";
    }
  }
}
