package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private int reviewId = 0;

    @NotEmpty
    @NotNull
    private String content;

    @NotNull
    private Boolean isPositive;

    @NotNull
    @Positive
    private Integer userId;

    @NotNull
    @Positive
    private Integer filmId;

    @Positive
    private Integer useful;
}
