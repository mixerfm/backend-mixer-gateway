-- USER
INSERT INTO "user" (identifier,name,description,date_of_birth,avatar,profile_color,created_at,updated_at,email,phone_number,gender,active) VALUES
    ('uid1','Test User','Test User Description','2000-01-13','userAvatar.jpg', '#001122','2000-01-14 15:59:49.216','2000-06-01 15:59:53.806','info@example.com','+123456789','F',true),
    ('uid2','Test User 2','Test User 2 Description','2000-02-13','userAvatar2.jpg', '#00112A','2000-01-15 15:59:49.216','2000-06-02 15:59:53.806','info2@example.com','+123456789','M',true),
    ('uid3','Test User 3','Test User 3 Description','2000-03-13','userAvatar3.jpg', '#00112B','2000-01-16 15:59:49.216','2000-06-03 15:59:53.806','info3@example.com','+123456789','M',true),
    ('uid4','Test User 4','Test User 4 Description','2000-04-13','userAvatar4.jpg', '#00112C','2000-01-17 15:59:49.216','2000-06-04 15:59:53.806','info4@example.com','+123456789','F',true);

-- MIX
INSERT INTO mix (identifier,name,description,avatar,duration,number_of_tracks,user_id,visibility,created_at,updated_at,play_count,nsfw) VALUES
    ('mid1','Test Mix','Test Mix Description','mixAvatar.jpg',150,3,1,0,'2024-01-15 12:00:54.262','2024-01-17 12:00:54.262',5,false),
    ('mid2','Test Mix 2','Test Mix 2 Description','mixAvatar2.jpg',60,2,2,0,'2024-01-16 12:00:54.262','2024-01-18 12:00:54.262',2,true),
    ('mid3','Test Mix 3','Test Mix 3 Description','mixAvatar3.jpg',180,5,3,0,'2024-01-13 12:00:54.262','2024-01-22 12:00:54.262',7,true);

INSERT INTO mix_like (user_id,mix_id,liked,updated_at) VALUES
    (1,1,true,'2024-01-20 16:36:16.556'),
    (2,1,false,'2024-01-21 16:36:16.556'),
    (3,1,true,'2024-01-22 16:36:16.556'),
    (4,1,true,'2024-01-23 16:36:16.556');

-- COLLECTION
INSERT INTO mix_collection (identifier,name,description,avatar,user_id,visibility,created_at,updated_at) VALUES
    ('cid1','Test Collection 1','Test Collection Description','collectionAvatar.jpg',1,0,'2024-06-03 20:46:22.722','2024-06-06 20:46:30.626'),
    ('cid2','Test Collection 2','Test Collection 2 Description','collectionAvatar2.jpg',1,1,'2024-06-04 20:46:22.722','2024-06-05 20:46:30.626');

INSERT INTO mix_collection_like (user_id,mix_collection_id,liked,updated_at) VALUES
     (1,1,true,'2024-01-20 16:36:16.556'),
     (2,1,false,'2024-01-21 16:36:16.556'),
     (3,1,true,'2024-01-22 16:36:16.556'),
     (4,1,true,'2024-01-23 16:36:16.556');

INSERT INTO mix_collection_relation (collection_id,mix_id,"position") VALUES
    (1,1,0);

-- COMMENTS
INSERT INTO comment (identifier,"content",user_id,mix_id,parent_comment_id,number_of_replies,created_at,updated_at) VALUES
    ('cmid1','Test Comment',1,1,NULL,2,'2024-02-20 06:30:48.601','2024-03-20 06:30:48.601');

INSERT INTO comment_like (user_id,comment_id,liked,updated_at) VALUES
    (1,1,true,'2024-01-20 16:36:16.556'),
    (2,1,false,'2024-01-21 16:36:16.556'),
    (3,1,true,'2024-01-22 16:36:16.556'),
    (4,1,true,'2024-01-23 16:36:16.556');

-- TAGS
INSERT INTO mix_tag (name) VALUES
    ('Rock'),
    ('Pop'),
    ('Happy');

INSERT INTO mix_tag_relation (tag_id,mix_id,"position") VALUES
    (1,1,0),
    (2,1,1),
    (3,1,2);

INSERT INTO mix_collection_tag_relation (tag_id,collection_id,"position") VALUES
    (1,1,0),
    (2,1,1);

-- ARTIST
INSERT INTO user_artist (identifier,name,"type",avatar,user_id,created_at,updated_at) VALUES
    ('uaid1','User Artist','BAND','userArtist.png',NULL,'2024-06-10 14:41:07.304','2024-06-10 14:40:15.922');

INSERT INTO user_artist_mix_relation (artist_id,mix_id,"position") VALUES
    (1,1,0);

-- ALBUM
INSERT INTO mix_album (identifier,name,release_date) VALUES
    ('aid1','Album 1','1992-12-05');

-- TRACK
INSERT INTO mix_track (identifier,name,duration,stream_url,created_at,updated_at,play_count,artist_id,album_id) VALUES
    ('tid1','Track 1',180,'track.mp3','2024-05-24 10:40:00.436','2024-05-24 10:40:05.202',0,1,1),
    ('tid2','Track 2',180,'track1.mp3','2024-05-26 10:40:00.436','2024-05-23 10:40:05.202',0,1,1),
    ('tid3','Track 3',120,'track3.mp3','2024-05-16 10:40:00.436','2024-05-17 10:40:05.202',0,1,1),
    ('tid4','Track 4',130,'track4.mp3','2024-05-18 10:40:00.436','2024-05-21 10:40:05.202',0,1,1),
    ('tid5','Track 5',140,'track5.mp3','2024-05-19 10:40:00.436','2024-05-22 10:40:05.202',0,1,1);

INSERT INTO mix_track_like (user_id,mix_track_id,liked,recommend,updated_at) VALUES
     (1,1,true,true,'2024-01-20 16:36:16.556'),
     (1,3,null,true,'2024-08-20 16:36:16.556'),
     (2,1,false,false,'2024-01-21 16:36:16.556'),
     (3,1,true,false,'2024-01-22 16:36:16.556'),
     (4,1,true,null,'2024-01-23 16:36:16.556');

INSERT INTO mix_track_relation (mix_id,track_id,"position") VALUES
    (1,1,0),
    (3,3,0),
    (3,4,1),
    (3,5,2);

-- SESSION
INSERT INTO public.play_session (user_id,mix_id,track_id,duration,tracks,shuffle,created_at,updated_at) VALUES
     (1,1,1,0,'1',false,'2024-06-17 13:56:59.422117','2024-06-17 14:57:07.220679');

-- user_follower
-- user_location
-- user_social_network