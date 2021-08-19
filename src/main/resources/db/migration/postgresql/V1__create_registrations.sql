CREATE TABLE IF NOT EXISTS registrations
(
    id    SERIAL primary key,
    email varchar(64) not null
);