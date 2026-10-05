
INSERT INTO album(album_id, album_author, album_title, album_cover_key, album_release_date)
values
    ('00000000-0000-0000-0000-000000000001'::uuid,'looplicator','Industrial Drum','track_cover/industrial_drums.jpg', date '2026-01-02'),
    ('00000000-0000-0000-0000-000000000002'::uuid,'josefpres','Piano and Music','track_cover/piano_and_music.jpg',date '2026-10-24'),
    ('00000000-0000-0000-0000-000000000003'::uuid,'joanne_pang','Peaceful Harp','track_cover/peaceful_harp.jpg',date '2026-5-10'),
    ('00000000-0000-0000-0000-000000000004'::uuid,'theojt','Peaceful Ambiance','track_cover/peaceful_ambiance.jpg', date '2026-07-25');

Insert into track(track_key, track_title, track_duration, author, album_id)
VALUES
( 'track_file/industrialdrum_by_@looplicator.wav', 'Industrial Drum', 12000, 'looplicator', '00000000-0000-0000-0000-000000000001'::uuid),
( 'track_file/music_loop_003_by_@josefpres.wav', 'Music Loop 003',142000, 'josefpres', '00000000-0000-0000-0000-000000000002'::uuid),
( 'track_file/music_loop_004_by_@josefpres.wav', 'Music Loop 004',51000, 'josefpres', '00000000-0000-0000-0000-000000000002'::uuid),
( 'track_file/piano_213_by_@josefpres.wav', 'Piano 213',130000, 'josefpres', '00000000-0000-0000-0000-000000000002'::uuid),
( 'track_file/piano_212_by_@josefpres.wav', 'Piano 212',250000, 'josefpres', '00000000-0000-0000-0000-000000000002'::uuid),
( 'track_file/peaceful_harp_@joanne_pang.wav', 'Peaceful Harp',19000, 'joanne_pang', '00000000-0000-0000-0000-000000000003'::uuid),
( 'track_file/peaceful_ambiance_@theojt.wav', 'Peaceful Ambiance',56000, 'theojt', '00000000-0000-0000-0000-000000000004'::uuid);

-- SELECT * FROM TRACK;