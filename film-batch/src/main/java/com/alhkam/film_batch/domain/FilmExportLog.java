package com.alhkam.film_batch.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "film_export_log")
public class FilmExportLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "job_id", nullable = false)
  private Long jobId;

  @Column(name = "film_id", nullable = false)
  private Long filmId;

  @CreationTimestamp
  @Column(name = "exported_at", nullable = false, updatable = false)
  private LocalDateTime exportedAt;
}
