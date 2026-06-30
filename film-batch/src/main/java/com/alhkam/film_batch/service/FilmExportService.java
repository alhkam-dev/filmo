package com.alhkam.film_batch.service;

import com.alhkam.film_batch.domain.FilmExportLog;
import com.alhkam.film_batch.dto.FilmBatchDTO;
import com.alhkam.film_batch.repository.FilmExportLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmExportService {

  private final FilmExportLogRepository filmExportLogRepository;

  @Transactional
  public void saveExportedFilms(List<FilmBatchDTO> filmBatchDTOList, Long jobId) {
    log.info(
        "Servicio de notificación: procesando registro de {} películas del job {}",
        filmBatchDTOList.size(),
        jobId);

    for (FilmBatchDTO film : filmBatchDTOList) {
      log.info("Registros de exportación para la película con ID: {}", film.getId());

      FilmExportLog filmLog = FilmExportLog.builder().jobId(jobId).filmId(film.getId()).build();

      filmExportLogRepository.save(filmLog);
    }
  }
}
