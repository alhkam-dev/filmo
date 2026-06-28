package com.alhkam.film_api.rest.service.impl;

import com.alhkam.film_api.persistence.entity.RatingEntity;
import com.alhkam.film_api.persistence.repository.RatingRepository;
import com.alhkam.film_api.rest.dto.RatingAverageResponseDTO;
import com.alhkam.film_api.rest.dto.RatingDetailsDTO;
import com.alhkam.film_api.rest.dto.RatingRequestDTO;
import com.alhkam.film_api.rest.dto.RatingResponseDTO;
import com.alhkam.film_api.rest.exceptions.RatingAlreadyExistsException;
import com.alhkam.film_api.rest.exceptions.RatingNotFoundException;
import com.alhkam.film_api.rest.service.RatingRestService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingRestServiceImpl implements RatingRestService {

  private final RatingRepository ratingRepository;
  private final ModelMapper modelMapper;

  @Override
  @Transactional
  public RatingResponseDTO saveRating(RatingRequestDTO ratingRequestDTO) {

    if (ratingRepository.existsByUserIdAndFilmId(
        ratingRequestDTO.getUserId(), ratingRequestDTO.getFilmId())) {
      throw new RatingAlreadyExistsException(
          ratingRequestDTO.getUserId(), ratingRequestDTO.getFilmId());
    }

    RatingEntity rating = modelMapper.map(ratingRequestDTO, RatingEntity.class);

    RatingEntity savedRating = ratingRepository.save(rating);

    return modelMapper.map(savedRating, RatingResponseDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public RatingDetailsDTO getRatingAndCreationTime(Long filmId, Long userId) {
    RatingEntity rating =
        ratingRepository
            .findByFilmIdAndUserId(filmId, userId)
            .orElseThrow(() -> new RatingNotFoundException(filmId, userId));

    return modelMapper.map(rating, RatingDetailsDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public RatingAverageResponseDTO getFilmAverageAndCount(Long filmId) {
    List<RatingEntity> ratings = ratingRepository.findByFilmId(filmId);

    if (ratings.isEmpty()) {
      return new RatingAverageResponseDTO(0.0, 0L);
    }

    double sumOfScores = 0.0;

    for (RatingEntity entity : ratings) {
      sumOfScores += entity.getScore();
    }

    double average = sumOfScores / ratings.size();

    BigDecimal bd = new BigDecimal(Double.toString(average));
    bd = bd.setScale(2, RoundingMode.CEILING);
    double adjustedAverage = bd.doubleValue();

    return new RatingAverageResponseDTO(adjustedAverage, ratings.size());
  }
}
