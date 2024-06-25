INSERT INTO "user" (identifier,name,description,date_of_birth,avatar,profile_color,created_at,updated_at,email,phone_number,gender,active) VALUES
    ('uid1','Test User','Test User Description','2000-01-13','userAvatar.jpg', '#001122','2000-01-14 15:59:49.216','2000-06-01 15:59:53.806','info@example.com','+123456789','F',true),
    ('uid2','Test User 2','Test User 2 Description','2000-02-13','userAvatar2.jpg', '#00112A','2000-01-15 15:59:49.216','2000-06-02 15:59:53.806','info2@example.com','+123456789','M',true),
    ('uid3','Test User 3','Test User 3 Description','2000-03-13','userAvatar3.jpg', '#00112B','2000-01-16 15:59:49.216','2000-06-03 15:59:53.806','info3@example.com','+123456789','M',true),
    ('uid4','Test User 4','Test User 4 Description','2000-04-13','userAvatar4.jpg', '#00112C','2000-01-17 15:59:49.216','2000-06-04 15:59:53.806','info4@example.com','+123456789','F',true),
    ('uid5','Test User 5','Test User 5 Description','2000-05-13','userAvatar5.jpg', '#00112D','2000-01-18 15:59:49.216','2000-06-05 15:59:53.806','info5@example.com','+123456789','F',true);

INSERT INTO mix (identifier,name,description,avatar,user_id,visibility,created_at,updated_at,play_count,nsfw) VALUES
    ('mid1','Test Mix','Test Mix Description','mixAvatar.jpg',1,0,'2024-01-15 12:00:54.262','2024-01-17 12:00:54.262',5,false);

INSERT INTO comment (identifier,"content",user_id,mix_id,parent_comment_id,number_of_replies,created_at,updated_at) VALUES
    ('cmid1','Test Comment',1,1,NULL,2,'2024-02-20 06:30:48.601','2024-03-20 06:30:48.601'),
    ('cmid2','Test Comment 2',1,1,1,0,'2024-02-21 06:30:48.601','2024-03-21 06:30:48.601'),
    ('cmid3','Test Comment 3',1,1,1,0,'2024-02-22 06:30:48.601','2024-03-22 06:30:48.601');

INSERT INTO comment_like (user_id,comment_id,liked,updated_at) VALUES
    (1,1,true,'2024-01-20 16:36:16.556'),
    (2,1,false,'2024-01-21 16:36:16.556'),
    (3,1,true,'2024-01-22 16:36:16.556'),
    (4,1,true,'2024-01-23 16:36:16.556');
