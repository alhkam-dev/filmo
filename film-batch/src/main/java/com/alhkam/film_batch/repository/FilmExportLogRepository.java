package com.alhkam.film_batch.repository;

import com.alhkam.film_batch.domain.FilmExportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmExportLogRepository extends JpaRepository<FilmExportLog, Long> {}
