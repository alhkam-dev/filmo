package com.alhkam.film_web.controller;

import com.alhkam.film_web.dto.ErrorDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class CustomErrorController implements ErrorController {

  private final MessageSource messageSource;

  // Spring inyecta automáticamente el gestor de mensajes i18n
  public CustomErrorController(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @GetMapping("/error")
  public String handleError(HttpServletRequest request, Model model) {

    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    Locale locale = LocaleContextHolder.getLocale();
    String methodHttp = request.getMethod();

    String errorType = "Error HTTP";
    String motiveDetails = "";

    // Si el servidor nos dice que es un 404
    if (status != null && status.toString().equals("404")) {
      errorType = "HTTP Error 404 - Not Found";
      motiveDetails = messageSource.getMessage("error.motive.404", null, locale);
    }
    // Si es un error 500 u otro código donde SÍ ha explotado el código Java
    else if (throwable != null) {
      errorType = "HTTP Error " + (status != null ? status.toString() : "500");
      motiveDetails = throwable.getMessage();
    }
    // Cualquier otro error genérico
    else {
      errorType = "HTTP Error " + (status != null ? status.toString() : "Undetermined");
      motiveDetails = messageSource.getMessage("error.motive.unknown", null, locale);
    }

    String forwardUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
    String url;

    if (forwardUri != null) {
      String scheme = request.getScheme();
      String server = request.getServerName();
      int port = request.getServerPort();

      // Si es puerto 80 o 443 estándar, no hace falta pintarlo, pero para desarrollo con 8080 sí
      String puertoStr = (port == 80 || port == 443) ? "" : ":" + port;

      url = scheme + "://" + server + puertoStr + forwardUri;
    } else {
      // Salvavidas por si no viene del forward de Tomcat
      url = request.getRequestURL().toString();
    }

    ErrorDTO errorDto =
        ErrorDTO.builder()
            .url(url)
            .exception(errorType)
            .motive(motiveDetails)
            .method(methodHttp)
            .build();

    model.addAttribute("error", errorDto);

    return "filmo/error";
  }
}
