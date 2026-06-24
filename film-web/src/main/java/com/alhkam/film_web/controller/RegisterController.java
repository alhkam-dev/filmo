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

@Controller
@RequiredArgsConstructor
public class RegisterController {

  private final UserService userService;

  @GetMapping("/register")
  public String showRegisterForm(Model model) {
    model.addAttribute("userRegisterDTO", UserRegisterDTO.builder().build());
    return "film/register";
  }

  @PostMapping("/register")
  public String registerUser(
      @Valid @ModelAttribute("userRegisterDTO") UserRegisterDTO userRegisterDTO,
      BindingResult bindingResult,
      Model model) {

    if (bindingResult.hasErrors()) {
      return "film/register";
    }

    if (!userRegisterDTO.password().equals(userRegisterDTO.passwordConfirm())) {
      bindingResult.rejectValue(
          "passwordConfirm",
          "register.validation.passwordConfirm.noMatch",
          "Passwords do not match");
      return "film/register";
    }

    try {
      userService.registerUser(userRegisterDTO);

      model.addAttribute("userRegistrationSuccesMessage", true);
      model.addAttribute("userRegisterDTO", UserRegisterDTO.builder().build());
    } catch (UsernameAlreadyExistsException e) {
      bindingResult.rejectValue("username", "register.error.duplicateUsername", e.getMessage());
      return "film/register";
    } catch (EmailAlreadyExistsException e) {
      bindingResult.rejectValue("email", "register.error.duplicateEmail", e.getMessage());
      return "film/register";
    }

    return "film/register";
  }
}
