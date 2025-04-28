package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class ReviewDbStorage extends BaseRepository<Review> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews ORDER BY useful DESC";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
    private static final String FIND_BY_ID = "SELECT * FROM reviews WHERE id = ?";
    private static final String INSERT = "INSERT INTO reviews (content,positive,userid,filmid,useful) VALUES (?,?,?,?,?)";
    private static final String UPDATE_REVIEW_QUERY = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO review_likes (review_id, user_id, is_positive) VALUES (?, ?, true)";
    private static final String INSERT_DISLIKE_QUERY = "INSERT INTO review_likes (review_id, user_id, is_positive) VALUES (?, ?, false)";
    private static final String UPDATE_USEFUL_QUERY = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE review_id = ?";

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, ReviewRowMapper reviewRowMapper) {
        super(jdbcTemplate, reviewRowMapper);
    }

    public List<Review> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public List<Review> findByFilmId(int filmId, int count) {
        return findMany(FIND_BY_FILM_ID_QUERY, filmId, count);
    }

    public Review findById(int id) {
        return findOne(FIND_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id " + id + " не найден"));
    }

    public Review create(Review review) {
        validateReview(review);

        int generatedId = insert(INSERT,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful());

        return findById(generatedId);
    }

    public Review update(Review review) {
        update(UPDATE_REVIEW_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        return findById(review.getReviewId());
    }

    public void addLike(long reviewId, long userId) {
        update(INSERT_LIKE_QUERY, reviewId, userId);
        updateUseful(reviewId, 1);
    }

    public void addDislike(long reviewId, long userId) {
        update(INSERT_DISLIKE_QUERY, reviewId, userId);
        updateUseful(reviewId, -1);
    }

    public void delete(long id) {
        int rowsAffected = update(DELETE_REVIEW_QUERY, id);
        if (rowsAffected > 0) {
            log.info("Отзыв с id {} успешно удален", id);
        } else {
            log.warn("Отзыв с id {} не найден и не может быть удален", id);
        }
    }

    private void updateUseful(long reviewId, int value) {
        update(UPDATE_USEFUL_QUERY, value, reviewId);
    }

    private void validateReview(Review review) {
        if (review.getFilmId() < 0 || review.getUserId() < 0) {
            throw new ValidationException("ID должны быть положительными числами");
        }

        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            throw new ValidationException("Не указаны user_id и/или film_id");
        }
    }
}