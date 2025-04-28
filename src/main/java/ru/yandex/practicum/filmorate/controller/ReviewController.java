package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("POST /reviews - Добавление нового отзыва: {}", review);
        return reviewService.create(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("PUT /reviews - Обновление отзыва с ID: {}", review.getReviewId());
        return reviewService.update(review);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable int id) {
        log.info("GET /reviews/{} - Получение отзыва по ID", id);
        return reviewService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        log.info("DELETE /reviews/{} - Удаление отзыва", id);
        reviewService.delete(id);
    }

    @GetMapping
    public List<Review> getAllReviews(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(defaultValue = "10") int count) {
        if (filmId != null) {
            log.info("GET /reviews?filmId={}&count={} - Получение {} отзывов для фильма", filmId, count, count);
            return reviewService.getReviewsByFilmId(filmId, count);
        }
        log.info("GET /reviews - Получение всех отзывов");
        return reviewService.getAllReviews();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT /reviews/{}/like/{} - Добавление лайка отзыву", id, userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT /reviews/{}/dislike/{} - Добавление дизлайка отзыву", id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("DELETE /reviews/{}/like/{} - Удаление лайка/дизлайка", id, userId);
        reviewService.removeLike(id, userId);
    }
}