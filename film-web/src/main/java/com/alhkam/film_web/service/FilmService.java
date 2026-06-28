package com.alhkam.film_web.service;

import com.alhkam.film_web.dto.FilmDetailsDTO;
import com.alhkam.film_web.dto.FilmFormDTO;
import com.alhkam.film_web.dto.FilmPreviewDTO;

import java.util.List;
import java.util.Optional;

public interface FilmService {

  void saveFilm(FilmFormDTO filmFormDTO);

  List<FilmPreviewDTO> searchFilmsByTitle(String searchText);

  FilmFormDTO findById(Long id);

  void updateFilm(Long id, FilmFormDTO filmFormDTO);

  FilmDetailsDTO getFilmDetails(Long id);
}
