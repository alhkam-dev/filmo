package com.alhkam.film_batch.listener;

import com.alhkam.film_batch.domain.FilmExportLog;
import com.alhkam.film_batch.dto.FilmBatchDTO;
import com.alhkam.film_batch.repository.FilmExportLogRepository;
import com.alhkam.film_batch.service.FilmExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.listener.ItemWriteListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmExportLogListener
    implements ItemWriteListener<FilmBatchDTO>, StepExecutionListener {

  private final FilmExportService filmExportService;
  private StepExecution stepExecution;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  @Override
  public void afterWrite(Chunk<? extends FilmBatchDTO> items) {
    Long jobId = stepExecution.getJobExecutionId();

    List<FilmBatchDTO> filmsList =
        items.getItems().stream().map(film -> (FilmBatchDTO) film).toList();

    filmExportService.saveExportedFilms(filmsList, jobId);
  }
}
