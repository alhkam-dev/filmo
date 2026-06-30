package com.alhkam.film_batch.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "filmo.batch")
public class FilmBatchConfigurationProperties {

    private final String outputPath;
}
