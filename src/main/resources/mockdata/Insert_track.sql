INSERT INTO album(album_id, album_author, album_title)
values
    ('00000000-0000-0000-0000-000000000001'::uuid,'looplicator','Industrial Drum'),
    ('00000000-0000-0000-0000-000000000002'::uuid,'josefpres','Piano and Music'),
    ('00000000-0000-0000-0000-000000000003'::uuid,'joanne_pang','Peaceful Harp'),
    ('00000000-0000-0000-0000-000000000004'::uuid,'theojt','Peaceful Ambiance');

Insert into track(storage_name, track_key, track_title, track_duration, author, album_id)
VALUES
('audio-for-spotifi-aka-spotify-clone', 'industrialdrum_by_@looplicator.wav', 'Industrial Drum', 12000, 'looplicator', '00000000-0000-0000-0000-000000000001'::uuid),
('audio-for-spotifi-aka-spotify-clone', 'music_loop_003_by_@josefpres.wav', 'Music Loop 003',142000, 'josefpres', '00000000-0000-0000-0000-000000000002'::uuid),
('audio-for-spotifi-aka-spotify-clone', 'music_loop_004_by_@josefpres.wav', 'Music Loop 004',51000, 'josefpres', '00000000-0000-0000-0000-000000000002'::uuid),
('audio-for-spotifi-aka-spotify-clone', 'piano_213_by_@josefpres.wav', 'Piano 213',130000, 'josefpres', '00000000-0000-0000-0000-000000000002'::uuid),
('audio-for-spotifi-aka-spotify-clone', 'piano_212_by_@josefpres.wav', 'Piano 212',250000, 'josefpres', '00000000-0000-0000-0000-000000000002'::uuid),
('audio-for-spotifi-aka-spotify-clone', 'peaceful_harp_@joanne_pang.wav', 'Peaceful Harp',19000, 'joanne_pang', '00000000-0000-0000-0000-000000000003'::uuid),
('audio-for-spotifi-aka-spotify-clone', 'peaceful_ambiance_@theojt.wav', 'Peaceful Ambiance',56000, 'theojt', '00000000-0000-0000-0000-000000000004'::uuid);

SELECT * FROM TRACK;