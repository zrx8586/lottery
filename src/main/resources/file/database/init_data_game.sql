-- 插入赛季数据
INSERT IGNORE INTO season (season_id, season_name, start_date, end_date) VALUES
                                                                             (1, 'Spring Season', '2023-03-01 00:00:00', '2023-05-31 23:59:59'),
                                                                             (2, 'Summer Season', '2023-06-01 00:00:00', '2023-08-31 23:59:59');

-- 插入用户数据
INSERT IGNORE INTO game_user (user_id, username, email) VALUES
                                                            (1, 'alice', 'alice@example.com'),
                                                            (2, 'bob', 'bob@example.com'),
                                                            (3, 'charlie', 'charlie@example.com');

-- 插入赛季排行榜记录数据
INSERT IGNORE INTO season_leaderboard_record (record_id, season_id, user_id, score) VALUES
                                                                                        (1, 1, 1, 1500.0), -- Alice's score in Spring Season
                                                                                        (2, 1, 2, 1200.0), -- Bob's score in Spring Season
                                                                                        (3, 2, 3, 1800.0); -- Charlie's score in Summer Season