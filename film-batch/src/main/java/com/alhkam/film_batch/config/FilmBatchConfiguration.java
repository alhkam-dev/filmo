package com.alhkam.film_batch.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FilmBatchConfigurationProperties.class)
public class FilmBatchConfiguration {}
