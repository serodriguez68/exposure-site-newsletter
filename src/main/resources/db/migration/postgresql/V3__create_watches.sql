CREATE TABLE watches
(
    id            UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    created_at    TIMESTAMP with time zone NOT NULL,
    subscriber_id UUID,
    suburb_id     UUID
);

ALTER TABLE watches
    ADD CONSTRAINT FK_WATCH_ON_SUBSCRIBER FOREIGN KEY (subscriber_id) REFERENCES subscribers (id);

ALTER TABLE watches
    ADD CONSTRAINT FK_WATCH_ON_SUBURB FOREIGN KEY (suburb_id) REFERENCES suburbs (id);