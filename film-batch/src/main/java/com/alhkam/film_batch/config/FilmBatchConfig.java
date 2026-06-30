package com.alhkam.film_batch.config;

import com.alhkam.film_batch.dto.FilmBatchDTO;
import com.alhkam.film_batch.listener.FilmExportLogListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.file.Path;

@Configuration
@RequiredArgsConstructor
public class FilmBatchConfig {

  private final DataSource dataSource;
  private final FilmBatchConfigurationProperties batchProperties;
  private final FilmExportLogListener filmExportLogListener;

  @Bean
  public JdbcCursorItemReader<FilmBatchDTO> filmReader() {
    String sql =
        """
            SELECT f.id, f.title, f.release_year AS releaseYear
            FROM films f
            LEFT JOIN film_export_log fel ON f.id = fel.film_id
            WHERE fel.film_id IS NULL
        """;

    return new JdbcCursorItemReaderBuilder<FilmBatchDTO>()
        .name("filmMariaDbReader")
        .dataSource(dataSource)
        .sql(sql)
        .rowMapper(new BeanPropertyRowMapper<>(FilmBatchDTO.class))
        .build();
  }

  @Bean
  public FlatFileItemWriter<FilmBatchDTO> filmCsvWriter() {
    Path path = Path.of(batchProperties.getOutputPath(), "films-exports.csv");

    BeanWrapperFieldExtractor<FilmBatchDTO> fieldExtractor = new BeanWrapperFieldExtractor<>();
    fieldExtractor.setNames(new String[] {"id", "title", "releaseYear"});

    DelimitedLineAggregator<FilmBatchDTO> lineAggregator = new DelimitedLineAggregator<>();
    lineAggregator.setDelimiter(",");
    lineAggregator.setFieldExtractor(fieldExtractor);

    return new FlatFileItemWriterBuilder<FilmBatchDTO>()
        .name("filmCsvWriter")
        .resource(new FileSystemResource(path.toFile()))
        .lineAggregator(lineAggregator)
        .headerCallback(writer -> writer.write("filmId,title,releaseYear"))
        .append(true)
        .build();
  }

  @Bean
  public Step exportFilmsSteps(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("exportFilmStep", jobRepository)
        .<FilmBatchDTO, FilmBatchDTO>chunk(10)
        .reader(filmReader())
        .writer(filmCsvWriter())
        .listener(filmExportLogListener)
        .transactionManager(transactionManager)
        .build();
  }

  @Bean
  public Job exportFilmsJob(JobRepository jobRepository, Step exporFilmsStep) {
    return new JobBuilder("exportFilmsJob", jobRepository).start(exporFilmsStep).build();
  }
}
