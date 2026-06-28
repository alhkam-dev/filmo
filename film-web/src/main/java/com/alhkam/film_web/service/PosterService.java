package com.alhkam.film_web.service;

import com.alhkam.film_web.domain.Poster;
import java.util.UUID;

public interface PosterService {

  Poster getMetadataByResourceId(UUID resourceId);
}
