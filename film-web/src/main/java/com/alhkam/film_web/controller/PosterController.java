package com.alhkam.film_web.controller;

import com.alhkam.film_web.domain.Poster;
import com.alhkam.film_web.service.PosterService;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PosterController {

  private final PosterService posterService;

  @Value("${filmo.posters.upload-dir}")
  private String uploadDir;

  @GetMapping("/filmo/posters/{resourceId}")
  @ResponseBody
  public ResponseEntity<Resource> getPosterByResourceId(@PathVariable UUID resourceId) {
    try {
      Poster posterMetadata = posterService.getMetadataByResourceId(resourceId);

      Path rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();
      Path filePath = rootPath.resolve(resourceId.toString());
      Resource resource = new UrlResource(filePath.toUri());

      if (resource.exists() && resource.isReadable()) {
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(posterMetadata.getContentType()))
            .body(resource);
      } else {
        Resource defaultPoster = new ClassPathResource("static/images/no-poster.webp");
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(defaultPoster);
      }
    } catch (Exception e) {
      log.error("Error trying to get poster with id: {}. {}", resourceId, e.getMessage());

      Resource defaultPoster = new ClassPathResource("static/images/no-poster.webp");
      return ResponseEntity.ok()
              .contentType(MediaType.IMAGE_PNG)
              .body(defaultPoster);
    }
  }
}
