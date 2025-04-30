CREATE TABLE IF NOT EXISTS mpa (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS genre (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS directors (
    director_id INT PRIMARY KEY AUTO_INCREMENT,
    director_name VARCHAR(70)
);

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    login VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS film (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    mpa_id INT,
    FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id INT,
    user_id INT,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES film(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INT,
    genre_id INT NOT NULL,
    FOREIGN KEY (film_id) REFERENCES film(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_directors (
film_id INT,
director_id INT NOT NULL,
FOREIGN KEY (film_id) REFERENCES film(id),
FOREIGN KEY (director_id) REFERENCES directors(director_id)
);

CREATE TABLE IF NOT EXISTS user_friends (
    user_id INT,
    friend_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE if not exists reviews
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(700),
    positive boolean,
    userid INT references users (id) on delete cascade,
    filmid INT references film (id) on delete cascade,
    useful INT
);

CREATE TABLE if not exists reviews_like
(
    review_id INT references reviews (id) on delete cascade,
    user_id INT references users (id) on delete cascade,
    is_like boolean
);