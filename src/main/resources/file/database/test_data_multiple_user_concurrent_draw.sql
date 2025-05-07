-- 插入测试用户数据
INSERT INTO users (username, email, phone, password, role_id) VALUES
('test_user1', 'test1@example.com', '13800138001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2),
('test_user2', 'test2@example.com', '13800138002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2),
('test_user3', 'test3@example.com', '13800138003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2),
('test_user4', 'test4@example.com', '13800138004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2),
('test_user5', 'test5@example.com', '13800138005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2),
('test_user6', 'test6@example.com', '13800138006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2),
('test_user7', 'test7@example.com', '13800138007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2),
('test_user8', 'test8@example.com', '13800138008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2),
('test_user9', 'test9@example.com', '13800138009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2),
('test_user10', 'test10@example.com', '13800138010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2);

-- 插入活动数据
INSERT INTO lottery_activity (activity_name, activity_desc, start_date, end_date, status) VALUES
('多人并发测试活动', '多人并发抽奖测试活动', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 'ACTIVE');

-- 插入奖品数据
INSERT INTO lottery_prize (prize_name, prize_desc, prize_image_url, prize_category, prize_value, stock_quantity, is_active) VALUES
('多人并发测试活动 iPhone 15', '最新款iPhone手机', 'https://example.com/iphone.jpg', '电子产品', 6999.00, 100, true),
('多人并发测试活动 AirPods Pro', '无线降噪耳机', 'https://example.com/airpods.jpg', '电子产品', 1999.00, 200, true),
('多人并发测试活动 购物券100元', '100元购物券', 'https://example.com/coupon.jpg', '优惠券', 100.00, 1000, true),
('多人并发测试活动 购物券50元', '50元购物券', 'https://example.com/coupon.jpg', '优惠券', 50.00, 2000, true),
('多人并发测试活动 谢谢参与', '感谢参与', 'https://example.com/thanks.jpg', '其他', 0.00, 999999, true);

-- 插入活动奖品关联数据
INSERT INTO lottery_activity_prize (activity_id, prize_id, probability, quantity, version)
SELECT
    (SELECT activity_id FROM lottery_activity WHERE activity_name = '多人并发测试活动'),
    prize_id,
    CASE
        WHEN prize_name = '多人并发测试活动 iPhone 15' THEN 0.001
        WHEN prize_name = '多人并发测试活动 AirPods Pro' THEN 0.005
        WHEN prize_name = '多人并发测试活动 购物券100元' THEN 0.094
        WHEN prize_name = '多人并发测试活动 购物券50元' THEN 0.2
        WHEN prize_name = '多人并发测试活动 谢谢参与' THEN 0.7
    END,
    CASE
        WHEN prize_name = '多人并发测试活动 iPhone 15' THEN 1
        WHEN prize_name = '多人并发测试活动 AirPods Pro' THEN 5
        WHEN prize_name = '多人并发测试活动 购物券100元' THEN 100
        WHEN prize_name = '多人并发测试活动 购物券50元' THEN 200
        WHEN prize_name = '多人并发测试活动 谢谢参与' THEN 999999
    END,
    0
FROM lottery_prize
WHERE prize_name LIKE '多人并发测试活动%';

-- 插入用户活动参与数据
INSERT INTO lottery_activity_user (activity_id, user_id, lottery_attempts, version)
SELECT
    (SELECT activity_id FROM lottery_activity WHERE activity_name = '多人并发测试活动'),
    user_id,
    100,
    0
FROM users
WHERE username LIKE 'test_user%';
