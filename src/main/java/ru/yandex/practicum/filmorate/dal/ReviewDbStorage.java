package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Slf4j
@Repository
public class ReviewDbStorage extends BaseRepository<Review> {
    private static final String FIND_ALL_QUERY = "SELECT r.*, " +
            "(SELECT COUNT(*) FROM reviews_like WHERE review_id = r.id AND is_like = true) - " +
            "(SELECT COUNT(*) FROM reviews_like WHERE review_id = r.id AND is_like = false) AS useful " +
            "FROM reviews r ORDER BY useful DESC";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT r.*, " +
            "(SELECT COUNT(*) FROM reviews_like WHERE review_id = r.id AND is_like = true) - " +
            "(SELECT COUNT(*) FROM reviews_like WHERE review_id = r.id AND is_like = false) AS useful " +
            "FROM reviews r WHERE r.filmid = ? ORDER BY useful DESC LIMIT ?";
    private static final String FIND_BY_ID = "SELECT r.*, " +
            "(SELECT COUNT(*) FROM reviews_like WHERE review_id = r.id AND is_like = true) - " +
            "(SELECT COUNT(*) FROM reviews_like WHERE review_id = r.id AND is_like = false) AS useful " +
            "FROM reviews r WHERE r.id = ?";
    private static final String INSERT = "INSERT INTO reviews (content,positive,userid,filmid) VALUES (?,?,?,?)";
    private static final String UPDATE_REVIEW_QUERY = "UPDATE reviews SET content = ?, positive = ? WHERE id = ?";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO reviews_like (review_id, user_id, is_like) VALUES (?, ?, true)";
    private static final String INSERT_DISLIKE_QUERY = "INSERT INTO reviews_like (review_id, user_id, is_like) VALUES (?, ?, false)";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE id = ?";

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
        return findOne(FIND_BY_ID, id).orElseThrow(() -> new NotFoundException("Отзыв с id " + id + " не найден"));
    }

    public Review create(Review review) {
        validateReview(review);

        int generatedId = insert(INSERT, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId());

        return findById(generatedId);
    }

    public Review update(Review review) {
        update(UPDATE_REVIEW_QUERY, review.getContent(), review.getIsPositive(), review.getReviewId());

        return findById(review.getReviewId());
    }


    public void delete(long id) {
        int rowsAffected = update(DELETE_REVIEW_QUERY, id);
        if (rowsAffected > 0) {
            log.info("Отзыв с id {} успешно удален", id);
        } else {
            log.warn("Отзыв с id {} не найден и не может быть удален", id);
        }
    }

    public void addLike(long reviewId, long userId) {
        update(INSERT_LIKE_QUERY, reviewId, userId);
        log.info("Добавлен лайк отзыва {} пользователем {}", reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {
        update(INSERT_DISLIKE_QUERY, reviewId, userId);
        log.info("Добавлен дизлайк отзыва {} пользователем {}", reviewId, userId);
    }

    public void removeLike(long reviewId, long userId) {
        // Удаляем и лайки и дизлайки пользователя для этого отзыва
        update("DELETE FROM reviews_like WHERE review_id = ? AND user_id = ?", reviewId, userId);
        log.info("Удален лайк/дизлайк отзыва {} пользователем {}", reviewId, userId);
    }

    private void validateReview(Review review) {
        if (review.getFilmId() < 0 || review.getUserId() < 0) {
            throw new NotFoundException("ID должны быть положительными числами");
        }

        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            throw new ValidationException("Не указаны user_id и/или film_id");
        }
    }


}