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
    model.addAttribute("user", UserRegisterDTO.builder().build());
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(
      @Valid @ModelAttribute("user") UserRegisterDTO userRegisterDTO,
      BindingResult bindingResult,
      Model model) {

    if (bindingResult.hasErrors()) {
      return "register";
    }

    if (!userRegisterDTO.password().equals(userRegisterDTO.passwordConfirm())) {
      bindingResult.rejectValue(
          "passwordConfirm", "error.passwordConfirm", "Passwords do not match");
      return "register";
    }

    try {
      userService.registerUser(userRegisterDTO);

      model.addAttribute("userRegistrationSuccesMessage", true);
      model.addAttribute("user", UserRegisterDTO.builder().build());
    } catch (UsernameAlreadyExistsException e) {
      bindingResult.rejectValue("username", "error.duplicateUsername", e.getMessage());
      return "register";
    } catch (EmailAlreadyExistsException e) {
      bindingResult.rejectValue("email", "error.duplicateEmail", e.getMessage());
      return "register";
    }

    return "register";
  }
}
