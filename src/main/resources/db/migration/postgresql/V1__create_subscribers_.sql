CREATE TABLE subscribers
(
    id    UUID         NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT pk_subscribers PRIMARY KEY (id)
);