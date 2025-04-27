CREATE TABLE IF NOT EXISTS mpa (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS genre (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
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
    FOREIGN KEY (mpa_id) REFERENCES mpa(id) ON DELETE CASCADE
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

CREATE TABLE IF NOT EXISTS user_friends (
    user_id INT,
    friend_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

create table if not exists user_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    action_timestamp timestamp DEFAULT CURRENT_TIMESTAMP,
    user_id bigint,
    entity_id bigint,
    event_type varchar(10),
    operation varchar(10),
    FOREIGN KEY (user_id) REFERENCES users(id),
);
create index if not exists user_log_i01 on user_log(user_id);
