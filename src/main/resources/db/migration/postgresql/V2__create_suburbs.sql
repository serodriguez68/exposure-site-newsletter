CREATE TABLE suburbs
(
    id        UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    post_code VARCHAR(255) NOT NULL,
    name      VARCHAR(255) NOT NULL
);