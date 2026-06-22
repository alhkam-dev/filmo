package com.alhkam.film_web.domain.film;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "films")
public class Film {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private int releaseYear;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "director_id", nullable = false)
  private Artist director;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "films_actors",
      joinColumns = @JoinColumn(name = "film_id"),
      inverseJoinColumns = @JoinColumn(name = "artist_id"))
  @Builder.Default
  private Set<Artist> actors = new HashSet<>();

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "poster_id", referencedColumnName = "id", nullable = false)
  private Poster poster;
}
