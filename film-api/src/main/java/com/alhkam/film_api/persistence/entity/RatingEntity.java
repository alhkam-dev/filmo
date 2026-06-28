package com.alhkam.film_api.persistence.entity;

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
@Table(name = "ratings")
public class RatingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime created;

  @Column(nullable = false)
  private Integer score;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "film_id", nullable = false)
  private Long filmId;
}
