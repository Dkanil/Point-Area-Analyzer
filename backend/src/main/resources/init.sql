CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS points
(
    id          BIGSERIAL PRIMARY KEY,
    x           DOUBLE PRECISION NOT NULL,
    y           DOUBLE PRECISION NOT NULL,
    r           DOUBLE PRECISION NOT NULL,
    is_hit      BOOLEAN          NOT NULL,
    username    VARCHAR(255)     NOT NULL,
    "timestamp" BIGINT           NOT NULL,
    CONSTRAINT fk_points_user
        FOREIGN KEY (username) REFERENCES users (username)
            ON DELETE CASCADE
);
