
INSERT INTO album(album_id, album_author, album_title, album_cover_key, album_release_date, album_type)
values
    ('9e6c7ab6-e760-4abb-986b-99bff71802f2'::uuid,'looplicator','Industrial Drum','track_cover/industrial_drums.jpg', date '2026-01-02', 'SINGLE'),
    ('cc32a6e3-e401-4800-9c09-01a71d9f83da'::uuid,'josefpres','Piano and Music','track_cover/piano_and_music.jpg',date '2026-10-24', 'EP'),
    ('43840c7c-2fa0-4252-b254-ef0e0e767cc4'::uuid,'joanne_pang','Peaceful Harp','track_cover/peaceful_harp.jpg',date '2026-5-10', 'SINGLE'),
    ('0588e9e9-2b5a-4071-a254-2a5c7b41208d'::uuid,'theojt','Peaceful Ambiance','track_cover/peaceful_ambiance.jpg', date '2026-07-25', 'SINGLE'),
    ('743360a4-07d9-4356-b75c-65517b48229a'::uuid,'youngrich89','Calm Night','track_cover/calm_night_x_hal_jordan.jpg', date '2026-10-05', 'SINGLE'),
    ('4c051112-0da8-4951-b1cf-27b8e67edd57'::uuid,'seth makes sounds','Summer Has No Tomorrow','track_cover/summer_has_no_tomorrow.jpg', date '2026-10-14', 'SINGLE'),
    ('baef7ba0-8a01-4e6e-8444-efe3d276e263'::uuid,'fredericfont','Puckr Music','track_cover/video_game_music_seamless.jpg', date '2026-10-14', 'SINGLE'),
    ('c07112d5-334b-41c3-95a7-6df3da5d9b46'::uuid,'x1shi','Seamless Game Music','track_cover/video_game_music_seamless.jpg', date '2026-10-14', 'SINGLE');


    Insert into track(track_key, track_title, track_duration, author, album_id)
VALUES
( 'track_file/calm_night_x_hal_jordan_@youngrich89.wav','Calm Night x Hal Jordan', 173000, 'youngrich89', '743360a4-07d9-4356-b75c-65517b48229a'::uuid),
( 'track_file/industrialdrum_by_@looplicator.wav', 'Industrial Drum', 12000, 'looplicator', '9e6c7ab6-e760-4abb-986b-99bff71802f2'::uuid),
( 'track_file/music_loop_003_by_@josefpres.wav', 'Music Loop 003', 82000, 'josefpres', 'cc32a6e3-e401-4800-9c09-01a71d9f83da'::uuid),
( 'track_file/music_loop_004_by_@josefpres.wav', 'Music Loop 004',51000, 'josefpres', 'cc32a6e3-e401-4800-9c09-01a71d9f83da'::uuid),
( 'track_file/piano_213_by_@josefpres.wav', 'Piano 213',130000, 'josefpres', 'cc32a6e3-e401-4800-9c09-01a71d9f83da'::uuid),
( 'track_file/piano_212_by_@josefpres.wav', 'Piano 212',130000, 'josefpres', 'cc32a6e3-e401-4800-9c09-01a71d9f83da'::uuid),
( 'track_file/peaceful_harp_@joanne_pang.wav', 'Peaceful Harp',19000, 'joanne_pang', '43840c7c-2fa0-4252-b254-ef0e0e767cc4'::uuid),
( 'track_file/peaceful_ambiance_@theojt.wav', 'Peaceful Ambiance',56000, 'theojt', '0588e9e9-2b5a-4071-a254-2a5c7b41208d'::uuid),
( 'track_file/darksummer_@seth_makes_sounds.wav', 'Dark Summer',118000, 'seth makes sounds', '4c051112-0da8-4951-b1cf-27b8e67edd57'::uuid),
( 'track_file/edgy_game_music_@seth_makes_sounds.wav', 'Edgy Game',61000, 'seth makes sounds', '4c051112-0da8-4951-b1cf-27b8e67edd57'::uuid),
( 'track_file/no_tomorrow_@seth_makes_sounds.wav', 'No Tomorrow',93000, 'seth makes sounds', '4c051112-0da8-4951-b1cf-27b8e67edd57'::uuid),
( 'track_file/puckr_music_level_1_@fredericfont.wav', 'Puckr Music Level 1',73000, 'fredericfont', 'baef7ba0-8a01-4e6e-8444-efe3d276e263'::uuid),
( 'track_file/video_game_music_seamless_@x1shi.wav', 'Video Game Music Seamless',113000, 'x1shi', 'c07112d5-334b-41c3-95a7-6df3da5d9b46'::uuid);

-- SELECT * FROM TRACK;
select t1_0.track_id,t1_0.track_title,t1_0.track_duration,a1_0.album_author,a1_0.album_title,t1_0.album_id,a1_0.album_cover_key,a1_0.album_release_date from track t1_0 join album a1_0 on t1_0.album_id=a1_0.album_id
