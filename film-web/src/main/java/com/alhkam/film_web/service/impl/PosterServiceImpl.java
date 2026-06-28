package com.alhkam.film_web.service.impl;

import com.alhkam.film_web.domain.Poster;
import com.alhkam.film_web.exception.ResourceNotFoundException;
import com.alhkam.film_web.repository.PosterRepository;
import com.alhkam.film_web.service.PosterService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PosterServiceImpl implements PosterService {

  private final PosterRepository posterRepository;

  @Override
  @Transactional(readOnly = true)
  public Poster getMetadataByResourceId(UUID resourceId) {
    return posterRepository
        .findByResourceId(resourceId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Poster with id: '" + resourceId + "' not found"));
  }
}
