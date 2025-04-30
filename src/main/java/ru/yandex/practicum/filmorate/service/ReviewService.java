package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.ReviewDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;

    @Autowired
    public ReviewService(ReviewDbStorage reviewDbStorage) {
        this.reviewDbStorage = reviewDbStorage;
    }

    public Review create(Review review) {
        log.info("Добавление нового отзыва: {}", review);
        return reviewDbStorage.create(review);
    }

    public Review update(Review review) {
        log.info("Обновление отзыва с ID: {}", review.getReviewId());
        return reviewDbStorage.update(review);
    }

    public void delete(long id) {
        log.info("Удаление отзыва с ID: {}", id);
        reviewDbStorage.delete(id);
    }

    public Review getById(int id) {
        log.info("Получение отзыва с ID: {}", id);
        return reviewDbStorage.findById(id);
    }

    public List<Review> getAllReviews() {
        log.info("Получение всех отзывов");
        return reviewDbStorage.findAll();
    }

    public List<Review> getReviewsByFilmId(int filmId, int count) {
        log.info("Получение {} отзывов для фильма с ID: {}", count, filmId);
        if (filmId <= 0) {
            throw new ValidationException("ID фильма должен быть положительным");
        }
        if (count <= 0) {
            throw new ValidationException("Количество отзывов должно быть положительным");
        }
        return reviewDbStorage.findByFilmId(filmId, count);
    }

    public Review addLike(int reviewId, int userId) {
        log.info("Добавление лайка отзыву {} от пользователя {}", reviewId, userId);
        validateIds(reviewId, userId);
        reviewDbStorage.addLike(reviewId, userId);
        return getById(reviewId);
    }

    public void addDislike(int reviewId, int userId) {
        log.info("Добавление дизлайка отзыву {} от пользователя {}", reviewId, userId);
        validateIds(reviewId, userId);
        reviewDbStorage.addDislike(reviewId, userId);
    }

    public void removeLike(int reviewId, int userId) {
        log.info("Удаление лайка/дизлайка отзыву {} от пользователя {}", reviewId, userId);
        validateIds(reviewId, userId);
        reviewDbStorage.removeLike(reviewId, userId);
    }

    private void validateIds(int reviewId, int userId) {
        if (reviewId <= 0 || userId <= 0) {
            throw new ValidationException("ID должны быть положительными числами");
        }
    }
}