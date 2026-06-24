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
    String metodoHttp = request.getMethod();

    String errorTipo = "Error HTTP";
    String motivoDetalle = "";

    // Si el servidor nos dice que es un 404
    if (status != null && status.toString().equals("404")) {
      errorTipo = "Error HTTP 404 - Not Found";
      motivoDetalle = messageSource.getMessage("error.motivo.404", null, locale);
    }
    // Si es un error 500 u otro código donde SÍ ha explotado el código Java
    else if (throwable != null) {
      errorTipo = "Error HTTP " + (status != null ? status.toString() : "500");
      motivoDetalle = throwable.getMessage();
    }
    // Cualquier otro error genérico
    else {
      errorTipo = "Error HTTP " + (status != null ? status.toString() : "Indeterminado");
      motivoDetalle = messageSource.getMessage("error.motivo.desconocido", null, locale);
    }

    String forwardUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
    String urlCompleta;

    if (forwardUri != null) {
      String esquema = request.getScheme();
      String servidor = request.getServerName();
      int puerto = request.getServerPort();

      // Si es puerto 80 o 443 estándar, no hace falta pintarlo, pero para desarrollo con 8080 sí
      String puertoStr = (puerto == 80 || puerto == 443) ? "" : ":" + puerto;

      urlCompleta = esquema + "://" + servidor + puertoStr + forwardUri;
    } else {
      // Salvavidas por si no viene del forward de Tomcat
      urlCompleta = request.getRequestURL().toString();
    }

    ErrorDTO errorDto = ErrorDTO.builder()
            .url(urlCompleta)
            .exception(errorTipo)
            .motivo(motivoDetalle)
            .metodo(metodoHttp)
            .build();

    model.addAttribute("error", errorDto);

    return "filmo/error";
  }
}
