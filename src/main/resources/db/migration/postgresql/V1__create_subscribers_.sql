CREATE TABLE subscribers
(
    id    UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP with time zone NOT NULL
);