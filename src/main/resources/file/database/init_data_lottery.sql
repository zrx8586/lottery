-- 插入活动数据
INSERT IGNORE INTO lottery_activity (activity_id, activity_name, activity_desc, start_date, end_date) VALUES
                                                                                                          (1, 'Spring Festival', 'Celebrate the arrival of spring with exciting prizes!', '2023-10-01 00:00:00', '2023-10-10 23:59:59'),
                                                                                                          (2, 'Summer Sale', 'Enjoy the summer with amazing discounts and prizes!', '2023-11-01 00:00:00', '2023-11-15 23:59:59'),
                                                                                                          (3, 'New Year Bash 2025', 'Ring in the new year with fantastic prizes!', '2025-01-01 00:00:00', '2025-12-31 23:59:59'),
                                                                                                          (4, 'Winter Wonderland', 'Celebrate winter with amazing gifts!', '2023-12-01 00:00:00', '2023-12-31 23:59:59'),
                                                                                                          (5, 'Black Friday', 'Exclusive deals and prizes for Black Friday!', '2023-11-24 00:00:00', '2023-11-24 23:59:59'),
                                                                                                          (6, 'Cyber Monday', 'Tech deals and prizes for Cyber Monday!', '2023-11-27 00:00:00', '2023-11-27 23:59:59'),
                                                                                                          (7, 'Valentine\'s Day Special', 'Celebrate love with special prizes!', '2024-02-14 00:00:00', '2024-02-14 23:59:59'),
                                                                                                          (8, 'Easter Egg Hunt', 'Find eggs and win prizes!', '2024-04-01 00:00:00', '2024-04-10 23:59:59'),
                                                                                                          (9, 'Halloween Spooktacular', 'Spooky prizes await!', '2024-10-01 00:00:00', '2024-10-31 23:59:59'),
                                                                                                          (10, 'Thanksgiving Giveaway', 'Celebrate Thanksgiving with prizes!', '2024-11-01 00:00:00', '2024-11-30 23:59:59'),
                                                                                                          (11, 'Back to School', 'Prizes for students and teachers!', '2024-08-01 00:00:00', '2024-08-31 23:59:59'),
                                                                                                          (12, 'Christmas Countdown', 'Daily prizes leading up to Christmas!', '2024-12-01 00:00:00', '2024-12-25 23:59:59'),
                                                                                                          (13, 'New Year\'s Eve Party', 'Celebrate the new year with prizes!', '2024-12-31 00:00:00', '2024-12-31 23:59:59'),
                                                                                                          (14, 'Spring Cleaning', 'Win prizes for cleaning supplies!', '2024-03-01 00:00:00', '2024-03-31 23:59:59'),
                                                                                                          (15, 'Summer Fun', 'Prizes for summer activities!', '2024-06-01 00:00:00', '2024-06-30 23:59:59'),
                                                                                                          (16, 'Autumn Harvest', 'Celebrate the harvest with prizes!', '2024-09-01 00:00:00', '2024-09-30 23:59:59'),
                                                                                                          (17, 'Winter Warmth', 'Prizes to keep you warm this winter!', '2024-12-01 00:00:00', '2024-12-31 23:59:59'),
                                                                                                          (18, 'Tech Expo', 'Win the latest gadgets!', '2024-05-01 00:00:00', '2024-05-31 23:59:59'),
                                                                                                          (19, 'Fitness Challenge', 'Prizes for staying fit!', '2024-07-01 00:00:00', '2024-07-31 23:59:59'),
                                                                                                          (20, 'Music Festival', 'Win tickets and merchandise!', '2024-08-01 00:00:00', '2024-08-31 23:59:59'),
                                                                                                          (21, 'Art Contest', 'Prizes for the best artwork!', '2024-09-01 00:00:00', '2024-09-30 23:59:59'),
                                                                                                          (22, 'Gaming Tournament', 'Win gaming gear and prizes!', '2024-10-01 00:00:00', '2024-10-31 23:59:59'),
                                                                                                          (23, 'Book Fair', 'Prizes for book lovers!', '2024-11-01 00:00:00', '2024-11-30 23:59:59'),
                                                                                                          (24, 'Charity Drive', 'Win prizes for donating!', '2024-12-01 00:00:00', '2024-12-31 23:59:59'),
                                                                                                          (25, 'Eco-Friendly Challenge', 'Prizes for going green!', '2024-04-01 00:00:00', '2024-04-30 23:59:59');

-- 插入奖品数据
INSERT IGNORE INTO lottery_prize (prize_id, prize_name, prize_desc, prize_image_url, prize_category, prize_value, stock_quantity, is_active) VALUES
                                                                                                                                                 (1, 'Smartphone', 'A high-end smartphone with the latest features', 'https://example.com/images/smartphone.jpg', 'Electronics', 699.99, 50, TRUE),
                                                                                                                                                 (2, 'Laptop', 'A powerful laptop for all your computing needs', 'https://example.com/images/laptop.jpg', 'Electronics', 999.99, 30, TRUE),
                                                                                                                                                 (3, 'Gift Card', 'A gift card worth $50 for shopping', 'https://example.com/images/giftcard.jpg', 'Gift', 50.00, 100, TRUE),
                                                                                                                                                 (4, 'Tablet', 'A versatile tablet for work and play', 'https://example.com/images/tablet.jpg', 'Electronics', 499.99, 20, TRUE),
                                                                                                                                                 (5, 'Headphones', 'Noise-cancelling headphones for immersive sound', 'https://example.com/images/headphones.jpg', 'Accessories', 199.99, 40, TRUE);

-- 插入活动奖品数据
INSERT IGNORE INTO lottery_activity_prize (activity_prize_id, activity_id, prize_id, probability, quantity) VALUES
                                                                                                                (1, 1, 1, 0.1, 5), (2, 1, 2, 0.05, 3), (3, 1, 3, 0.15, 10), (4, 1, 25, 0.7, 9999999),
                                                                                                                (5, 2, 4, 0.1, 4), (6, 2, 5, 0.05, 2), (7, 2, 6, 0.15, 15), (8, 2, 25, 0.7, 9999999),
                                                                                                                (9, 3, 7, 0.15, 6), (10, 3, 8, 0.1, 8), (11, 3, 9, 0.15, 10), (12, 3, 25, 0.6, 9999999),
                                                                                                                (13, 4, 10, 0.1, 5), (14, 4, 11, 0.05, 3), (15, 4, 12, 0.15, 10), (16, 4, 25, 0.7, 9999999),
                                                                                                                (17, 5, 13, 0.1, 4), (18, 5, 14, 0.05, 2), (19, 5, 15, 0.15, 15), (20, 5, 25, 0.7, 9999999),
                                                                                                                (21, 6, 16, 0.15, 6), (22, 6, 17, 0.1, 8), (23, 6, 18, 0.15, 10), (24, 6, 25, 0.6, 9999999),
                                                                                                                (25, 7, 19, 0.1, 5);

-- 插入用户数据
INSERT IGNORE INTO lottery_user (user_id, username, email) VALUES
                                                               (1, 'alice', 'alice@example.com'), (2, 'bob', 'bob@example.com'), (3, 'charlie', 'charlie@example.com'),
                                                               (4, 'david', 'david@example.com'), (5, 'eve', 'eve@example.com'), (6, 'frank', 'frank@example.com'),
                                                               (7, 'grace', 'grace@example.com'), (8, 'hank', 'hank@example.com'), (9, 'irene', 'irene@example.com'),
                                                               (10, 'jack', 'jack@example.com'), (11, 'kate', 'kate@example.com'), (12, 'leo', 'leo@example.com'),
                                                               (13, 'mike', 'mike@example.com'), (14, 'nina', 'nina@example.com'), (15, 'oliver', 'oliver@example.com'),
                                                               (16, 'paul', 'paul@example.com'), (17, 'quinn', 'quinn@example.com'), (18, 'rachel', 'rachel@example.com'),
                                                               (19, 'steve', 'steve@example.com'), (20, 'tina', 'tina@example.com'), (21, 'ursula', 'ursula@example.com'),
                                                               (22, 'victor', 'victor@example.com'), (23, 'wendy', 'wendy@example.com'), (24, 'xander', 'xander@example.com'),
                                                               (25, 'yara', 'yara@example.com');

-- 插入抽奖记录数据
INSERT IGNORE INTO lottery_record (record_id, user_id, activity_id, prize_id, won_at) VALUES
                                                                                          (1, 1, 1, 1, '2023-10-01 10:00:00'), (2, 2, 1, 2, '2023-10-02 11:00:00'), (3, 3, 2, 3, '2023-11-03 12:00:00'),
                                                                                          (4, 4, 3, 4, '2023-12-01 13:00:00'), (5, 5, 4, 5, '2023-12-02 14:00:00'), (6, 6, 5, 6, '2023-12-03 15:00:00'),
                                                                                          (7, 7, 6, 7, '2023-12-04 16:00:00'), (8, 8, 7, 8, '2023-12-05 17:00:00'), (9, 9, 8, 9, '2023-12-06 18:00:00'),
                                                                                          (10, 10, 9, 10, '2023-12-07 19:00:00'), (11, 11, 10, 11, '2023-12-08 20:00:00'), (12, 12, 11, 12, '2023-12-09 21:00:00'),
                                                                                          (13, 13, 12, 13, '2023-12-10 22:00:00'), (14, 14, 13, 14, '2023-12-11 23:00:00'), (15, 15, 14, 15, '2023-12-12 00:00:00'),
                                                                                          (16, 16, 15, 16, '2023-12-13 01:00:00'), (17, 17, 16, 17, '2023-12-14 02:00:00'), (18, 18, 17, 18, '2023-12-15 03:00:00'),
                                                                                          (19, 19, 18, 19, '2023-12-16 04:00:00'), (20, 20, 19, 20, '2023-12-17 05:00:00'), (21, 21, 20, 21, '2023-12-18 06:00:00'),
                                                                                          (22, 22, 21, 22, '2023-12-19 07:00:00'), (23, 23, 22, 23, '2023-12-20 08:00:00'), (24, 24, 23, 24, '2023-12-21 09:00:00'),
                                                                                          (25, 25, 24, 25, '2023-12-22 10:00:00');

-- 插入活动用户数据
INSERT IGNORE INTO lottery_activity_user (activity_user_id, activity_id, user_id, participation_date, lottery_attempts) VALUES
                                                                                                                            (1, 1, 1, '2023-10-01 09:00:00', 3), (2, 1, 2, '2023-10-01 10:00:00', 2), (3, 2, 3, '2023-11-01 11:00:00', 5),
                                                                                                                            (4, 3, 4, '2023-12-01 12:00:00', 4), (5, 4, 5, '2023-12-02 13:00:00', 3), (6, 5, 6, '2023-12-03 14:00:00', 2),
                                                                                                                            (7, 6, 7, '2023-12-04 15:00:00', 1), (8, 7, 8, '2023-12-05 16:00:00', 5), (9, 8, 9, '2023-12-06 17:00:00', 4),
                                                                                                                            (10, 9, 10, '2023-12-07 18:00:00', 3), (11, 10, 11, '2023-12-08 19:00:00', 2), (12, 11, 12, '2023-12-09 20:00:00', 1),
                                                                                                                            (13, 12, 13, '2023-12-10 21:00:00', 5), (14, 13, 14, '2023-12-11 22:00:00', 4), (15, 14, 15, '2023-12-12 23:00:00', 3),
                                                                                                                            (16, 15, 16, '2023-12-13 00:00:00', 2), (17, 16, 17, '2023-12-14 01:00:00', 1), (18, 17, 18, '2023-12-15 02:00:00', 5),
                                                                                                                            (19, 18, 19, '2023-12-16 03:00:00', 4), (20, 19, 20, '2023-12-17 04:00:00', 3), (21, 20, 21, '2023-12-18 05:00:00', 2),
                                                                                                                            (22, 21, 22, '2023-12-19 06:00:00', 1), (23, 22, 23, '2023-12-20 07:00:00', 5), (24, 23, 24, '2023-12-21 08:00:00', 4),
                                                                                                                            (25, 24, 25, '2023-12-22 09:00:00', 3);