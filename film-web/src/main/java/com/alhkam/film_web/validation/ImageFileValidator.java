package com.alhkam.film_web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {

  @Override
  public boolean isValid(
      MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {

    if (multipartFile == null || multipartFile.isEmpty()) {
      return true; // Devolvemos true para que se encargue de ese tipo de validación el @NotNull
    }

    String contentType = multipartFile.getContentType();

    return contentType != null && contentType.startsWith("image/");
  }
}
