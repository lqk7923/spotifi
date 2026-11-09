CREATE TABLE IF NOT EXISTS album (
    album_id UUID PRIMARY KEY,
    album_author VARCHAR(255) NOT NULL,
    album_release_date DATE NOT NULL,
    album_title VARCHAR(255) NOT NULL,
    album_cover_key VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS track (
    track_id UUID PRIMARY KEY,
    track_key VARCHAR(255) NOT NULL,
    track_title VARCHAR(255) NOT NULL,
    track_duration BIGINT NOT NULL,
    author VARCHAR(255) NOT NULL,
    album_id UUID NOT NULL REFERENCES album(album_id)
);
