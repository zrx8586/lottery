-- 插入活动数据
INSERT IGNORE INTO lottery_activity (activity_id, activity_name, activity_desc, start_date, end_date) VALUES
                                                                                                          (1, 'Spring Festival', 'Celebrate the arrival of spring with exciting prizes!', '2023-10-01 00:00:00', '2023-10-10 23:59:59'),
                                                                                                          (2, 'Summer Sale', 'Enjoy the summer with amazing discounts and prizes!', '2023-11-01 00:00:00', '2023-11-15 23:59:59'),
                                                                                                          (3, 'New Year Bash 2025', 'Ring in the new year with fantastic prizes!', '2025-01-01 00:00:00', '2025-12-31 23:59:59'); -- 新活动

-- 插入奖品数据
INSERT IGNORE INTO lottery_prize (prize_id, prize_name, prize_desc) VALUES
                                                                        (1, 'Smartphone', 'A high-end smartphone with the latest features'),
                                                                        (2, 'Laptop', 'A powerful laptop for all your computing needs'),
                                                                        (3, 'Gift Card', 'A gift card worth $50 for shopping'),
                                                                        (4, 'Tablet', 'A versatile tablet for work and play'),
                                                                        (5, 'Headphones', 'Noise-cancelling headphones for immersive sound'),
                                                                        (6, 'No Prize', 'No prize won this time'); -- 新增“没有中奖”奖品

-- 插入活动奖品数据
INSERT IGNORE INTO lottery_activity_prize (activity_prize_id, activity_id, prize_id, probability, quantity) VALUES
                                                                                                                (1, 1, 1, 0.1, 5),  -- Spring Festival: Smartphone with 10% probability and 5 units
                                                                                                                (2, 1, 2, 0.05, 3), -- Spring Festival: Laptop with 5% probability and 3 units
                                                                                                                (3, 1, 3, 0.15, 10), -- Spring Festival: Gift Card with 15% probability and 10 units
                                                                                                                (4, 1, 6, 0.7, 9999999), -- Spring Festival: No Prize with 70% probability
                                                                                                                (5, 2, 1, 0.1, 4), -- Summer Sale: Smartphone with 10% probability and 4 units
                                                                                                                (6, 2, 2, 0.05, 2),  -- Summer Sale: Laptop with 5% probability and 2 units
                                                                                                                (7, 2, 3, 0.15, 15),-- Summer Sale: Gift Card with 15% probability and 15 units
                                                                                                                (8, 2, 6, 0.7, 9999999),  -- Summer Sale: No Prize with 70% probability
                                                                                                                (9, 3, 1, 0.15, 6), -- New Year Bash 2025: Smartphone with 15% probability and 6 units
                                                                                                                (10, 3, 4, 0.1, 8), -- New Year Bash 2025: Tablet with 10% probability and 8 units
                                                                                                                (11, 3, 5, 0.15, 10),-- New Year Bash 2025: Headphones with 15% probability and 10 units
                                                                                                                (12, 3, 6, 0.6, 9999999);  -- New Year Bash 2025: No Prize with 60% probability

-- 插入用户数据
INSERT IGNORE INTO lottery_user (user_id, username, email) VALUES
                                                               (1, 'alice', 'alice@example.com'),
                                                               (2, 'bob', 'bob@example.com'),
                                                               (3, 'charlie', 'charlie@example.com');

-- 插入抽奖记录数据
INSERT IGNORE INTO lottery_record (record_id, user_id, activity_id, prize_id, won_at) VALUES
                                                                                          (1, 1, 1, 1, '2023-10-01 10:00:00'), -- Alice won a Smartphone during Spring Festival
                                                                                          (2, 2, 1, 2, '2023-10-02 11:00:00'), -- Bob won a Laptop during Spring Festival
                                                                                          (3, 3, 2, 3, '2023-11-03 12:00:00'); -- Charlie won a Gift Card during Summer Sale

-- 插入活动用户数据
INSERT IGNORE INTO lottery_activity_user (activity_user_id, activity_id, user_id, participation_date, lottery_attempts) VALUES
                                                                                                                            (1, 1, 1, '2023-10-01 09:00:00', 3), -- Alice participated in Spring Festival with 3 attempts
                                                                                                                            (2, 1, 2, '2023-10-01 10:00:00', 2), -- Bob participated in Spring Festival with 2 attempts
                                                                                                                            (3, 2, 3, '2023-11-01 11:00:00', 5); -- Charlie participated in Summer Sale with 5 attempts