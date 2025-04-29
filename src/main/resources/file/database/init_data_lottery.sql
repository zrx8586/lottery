-- 插入活动数据
INSERT IGNORE INTO lottery_activity (activity_name, activity_desc, start_date, end_date, status) VALUES
    ('春节大抽奖', '喜迎新春，好运连连！参与抽奖赢取丰厚奖品！', '2024-02-10 00:00:00', '2024-02-17 23:59:59', 'ENDED'),
    ('元宵节灯谜会', '猜灯谜，赢大奖！元宵佳节，好运连连！', '2024-02-24 00:00:00', '2024-02-24 23:59:59', 'ENDED'),
    ('端午节龙舟赛', '端午佳节，龙舟竞渡，参与抽奖赢取精美礼品！', '2024-06-10 00:00:00', '2024-06-10 23:59:59', 'PENDING'),
    ('中秋节团圆礼', '中秋月圆，阖家欢乐，参与抽奖赢取团圆好礼！', '2024-09-17 00:00:00', '2024-09-17 23:59:59', 'PENDING'),
    ('国庆节欢乐购', '欢度国庆，参与抽奖赢取超值好礼！', '2024-10-01 00:00:00', '2024-10-07 23:59:59', 'PENDING');

-- 插入奖品数据
INSERT IGNORE INTO lottery_prize (prize_name, prize_desc, prize_image_url, prize_category, prize_value, stock_quantity, is_active) VALUES
    ('华为Mate60 Pro', '最新款华为旗舰手机，搭载麒麟9000S芯片', 'https://example.com/images/huawei-mate60.jpg', '电子产品', 6999.00, 10, TRUE),
    ('茅台飞天53度', '贵州茅台酒，500ml装', 'https://example.com/images/maotai.jpg', '酒水', 1499.00, 20, TRUE),
    ('故宫文创礼盒', '故宫博物院官方授权文创产品', 'https://example.com/images/forbidden-city.jpg', '文创', 299.00, 50, TRUE),
    ('大闸蟹礼券', '阳澄湖大闸蟹，8只装', 'https://example.com/images/crab.jpg', '食品', 888.00, 30, TRUE),
    ('茶具套装', '景德镇青花瓷茶具，6件套', 'https://example.com/images/tea-set.jpg', '家居', 599.00, 15, TRUE),
    ('谢谢参与', '感谢您的参与', 'https://example.com/images/thanks.jpg', '其他', 0.00, 999999, TRUE);

-- 插入活动奖品数据
INSERT IGNORE INTO lottery_activity_prize (activity_id, prize_id, probability, quantity)
SELECT 
    a.activity_id,
    p.prize_id,
    CASE 
        WHEN a.activity_name = '春节大抽奖' AND p.prize_name = '华为Mate60 Pro' THEN 0.01
        WHEN a.activity_name = '春节大抽奖' AND p.prize_name = '茅台飞天53度' THEN 0.02
        WHEN a.activity_name = '春节大抽奖' AND p.prize_name = '故宫文创礼盒' THEN 0.05
        WHEN a.activity_name = '春节大抽奖' AND p.prize_name = '谢谢参与' THEN 0.92
        WHEN a.activity_name = '元宵节灯谜会' AND p.prize_name = '大闸蟹礼券' THEN 0.03
        WHEN a.activity_name = '元宵节灯谜会' AND p.prize_name = '茶具套装' THEN 0.05
        WHEN a.activity_name = '元宵节灯谜会' AND p.prize_name = '故宫文创礼盒' THEN 0.10
        WHEN a.activity_name = '元宵节灯谜会' AND p.prize_name = '谢谢参与' THEN 0.82
        WHEN a.activity_name = '端午节龙舟赛' AND p.prize_name = '华为Mate60 Pro' THEN 0.01
        WHEN a.activity_name = '端午节龙舟赛' AND p.prize_name = '茅台飞天53度' THEN 0.02
        WHEN a.activity_name = '端午节龙舟赛' AND p.prize_name = '大闸蟹礼券' THEN 0.05
        WHEN a.activity_name = '端午节龙舟赛' AND p.prize_name = '谢谢参与' THEN 0.92
        WHEN a.activity_name = '中秋节团圆礼' AND p.prize_name = '茅台飞天53度' THEN 0.02
        WHEN a.activity_name = '中秋节团圆礼' AND p.prize_name = '故宫文创礼盒' THEN 0.05
        WHEN a.activity_name = '中秋节团圆礼' AND p.prize_name = '茶具套装' THEN 0.08
        WHEN a.activity_name = '中秋节团圆礼' AND p.prize_name = '谢谢参与' THEN 0.85
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '华为Mate60 Pro' THEN 0.01
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '茅台飞天53度' THEN 0.02
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '故宫文创礼盒' THEN 0.05
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '大闸蟹礼券' THEN 0.05
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '茶具套装' THEN 0.05
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '谢谢参与' THEN 0.82
    END as probability,
    CASE 
        WHEN a.activity_name = '春节大抽奖' AND p.prize_name = '华为Mate60 Pro' THEN 2
        WHEN a.activity_name = '春节大抽奖' AND p.prize_name = '茅台飞天53度' THEN 5
        WHEN a.activity_name = '春节大抽奖' AND p.prize_name = '故宫文创礼盒' THEN 10
        WHEN a.activity_name = '春节大抽奖' AND p.prize_name = '谢谢参与' THEN 999999
        WHEN a.activity_name = '元宵节灯谜会' AND p.prize_name = '大闸蟹礼券' THEN 8
        WHEN a.activity_name = '元宵节灯谜会' AND p.prize_name = '茶具套装' THEN 15
        WHEN a.activity_name = '元宵节灯谜会' AND p.prize_name = '故宫文创礼盒' THEN 20
        WHEN a.activity_name = '元宵节灯谜会' AND p.prize_name = '谢谢参与' THEN 999999
        WHEN a.activity_name = '端午节龙舟赛' AND p.prize_name = '华为Mate60 Pro' THEN 1
        WHEN a.activity_name = '端午节龙舟赛' AND p.prize_name = '茅台飞天53度' THEN 3
        WHEN a.activity_name = '端午节龙舟赛' AND p.prize_name = '大闸蟹礼券' THEN 10
        WHEN a.activity_name = '端午节龙舟赛' AND p.prize_name = '谢谢参与' THEN 999999
        WHEN a.activity_name = '中秋节团圆礼' AND p.prize_name = '茅台飞天53度' THEN 5
        WHEN a.activity_name = '中秋节团圆礼' AND p.prize_name = '故宫文创礼盒' THEN 10
        WHEN a.activity_name = '中秋节团圆礼' AND p.prize_name = '茶具套装' THEN 15
        WHEN a.activity_name = '中秋节团圆礼' AND p.prize_name = '谢谢参与' THEN 999999
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '华为Mate60 Pro' THEN 2
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '茅台飞天53度' THEN 5
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '故宫文创礼盒' THEN 10
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '大闸蟹礼券' THEN 10
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '茶具套装' THEN 10
        WHEN a.activity_name = '国庆节欢乐购' AND p.prize_name = '谢谢参与' THEN 999999
    END as quantity
FROM lottery_activity a
CROSS JOIN lottery_prize p
WHERE (
    (a.activity_name = '春节大抽奖' AND p.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '故宫文创礼盒', '谢谢参与')) OR
    (a.activity_name = '元宵节灯谜会' AND p.prize_name IN ('大闸蟹礼券', '茶具套装', '故宫文创礼盒', '谢谢参与')) OR
    (a.activity_name = '端午节龙舟赛' AND p.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '大闸蟹礼券', '谢谢参与')) OR
    (a.activity_name = '中秋节团圆礼' AND p.prize_name IN ('茅台飞天53度', '故宫文创礼盒', '茶具套装', '谢谢参与')) OR
    (a.activity_name = '国庆节欢乐购' AND p.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '故宫文创礼盒', '大闸蟹礼券', '茶具套装', '谢谢参与'))
);

-- 插入用户数据
INSERT IGNORE INTO lottery_user (username, email, lottery_attempts) VALUES
    ('alice', 'alice@example.com', 5),
    ('bob', 'bob@example.com', 3),
    ('charlie', 'charlie@example.com', 2),
    ('david', 'david@example.com', 4),
    ('eve', 'eve@example.com', 1),
    ('frank', 'frank@example.com', 3),
    ('grace', 'grace@example.com', 2),
    ('hank', 'hank@example.com', 5),
    ('irene', 'irene@example.com', 1),
    ('jack', 'jack@example.com', 4),
    ('kate', 'kate@example.com', 3),
    ('leo', 'leo@example.com', 2),
    ('mike', 'mike@example.com', 5),
    ('nina', 'nina@example.com', 1),
    ('oliver', 'oliver@example.com', 4),
    ('paul', 'paul@example.com', 3),
    ('quinn', 'quinn@example.com', 2),
    ('rachel', 'rachel@example.com', 5),
    ('steve', 'steve@example.com', 1),
    ('tina', 'tina@example.com', 4),
    ('ursula', 'ursula@example.com', 3),
    ('victor', 'victor@example.com', 2),
    ('wendy', 'wendy@example.com', 5),
    ('xander', 'xander@example.com', 1),
    ('yara', 'yara@example.com', 4);

-- 插入抽奖记录数据
INSERT IGNORE INTO lottery_record (user_id, activity_id, prize_id, won_at)
SELECT 
    u.user_id,
    a.activity_id,
    p.prize_id,
    CASE 
        WHEN u.username = 'alice' THEN '2023-10-01 10:00:00'
        WHEN u.username = 'bob' THEN '2023-10-02 11:00:00'
        WHEN u.username = 'charlie' THEN '2023-11-03 12:00:00'
        WHEN u.username = 'david' THEN '2023-12-01 13:00:00'
        WHEN u.username = 'eve' THEN '2023-12-02 14:00:00'
        WHEN u.username = 'frank' THEN '2023-12-03 15:00:00'
        WHEN u.username = 'grace' THEN '2023-12-04 16:00:00'
        WHEN u.username = 'hank' THEN '2023-12-05 17:00:00'
        WHEN u.username = 'irene' THEN '2023-12-06 18:00:00'
        WHEN u.username = 'jack' THEN '2023-12-07 19:00:00'
        WHEN u.username = 'kate' THEN '2023-12-08 20:00:00'
        WHEN u.username = 'leo' THEN '2023-12-09 21:00:00'
        WHEN u.username = 'mike' THEN '2023-12-10 22:00:00'
        WHEN u.username = 'nina' THEN '2023-12-11 23:00:00'
        WHEN u.username = 'oliver' THEN '2023-12-12 00:00:00'
        WHEN u.username = 'paul' THEN '2023-12-13 01:00:00'
        WHEN u.username = 'quinn' THEN '2023-12-14 02:00:00'
        WHEN u.username = 'rachel' THEN '2023-12-15 03:00:00'
        WHEN u.username = 'steve' THEN '2023-12-16 04:00:00'
        WHEN u.username = 'tina' THEN '2023-12-17 05:00:00'
        WHEN u.username = 'ursula' THEN '2023-12-18 06:00:00'
        WHEN u.username = 'victor' THEN '2023-12-19 07:00:00'
        WHEN u.username = 'wendy' THEN '2023-12-20 08:00:00'
        WHEN u.username = 'xander' THEN '2023-12-21 09:00:00'
        WHEN u.username = 'yara' THEN '2023-12-22 10:00:00'
    END as won_at
FROM lottery_user u
CROSS JOIN lottery_activity a
CROSS JOIN lottery_prize p
WHERE (
    (u.username = 'alice' AND a.activity_name = '春节大抽奖' AND p.prize_name = '华为Mate60 Pro') OR
    (u.username = 'bob' AND a.activity_name = '春节大抽奖' AND p.prize_name = '茅台飞天53度') OR
    (u.username = 'charlie' AND a.activity_name = '元宵节灯谜会' AND p.prize_name = '故宫文创礼盒') OR
    (u.username = 'david' AND a.activity_name = '端午节龙舟赛' AND p.prize_name = '大闸蟹礼券') OR
    (u.username = 'eve' AND a.activity_name = '中秋节团圆礼' AND p.prize_name = '茶具套装') OR
    (u.username = 'frank' AND a.activity_name = '国庆节欢乐购' AND p.prize_name = '谢谢参与') OR
    (u.username = 'grace' AND a.activity_name = '春节大抽奖' AND p.prize_name = '华为Mate60 Pro') OR
    (u.username = 'hank' AND a.activity_name = '元宵节灯谜会' AND p.prize_name = '茅台飞天53度') OR
    (u.username = 'irene' AND a.activity_name = '端午节龙舟赛' AND p.prize_name = '故宫文创礼盒') OR
    (u.username = 'jack' AND a.activity_name = '中秋节团圆礼' AND p.prize_name = '大闸蟹礼券') OR
    (u.username = 'kate' AND a.activity_name = '国庆节欢乐购' AND p.prize_name = '茶具套装') OR
    (u.username = 'leo' AND a.activity_name = '春节大抽奖' AND p.prize_name = '谢谢参与') OR
    (u.username = 'mike' AND a.activity_name = '元宵节灯谜会' AND p.prize_name = '华为Mate60 Pro') OR
    (u.username = 'nina' AND a.activity_name = '端午节龙舟赛' AND p.prize_name = '茅台飞天53度') OR
    (u.username = 'oliver' AND a.activity_name = '中秋节团圆礼' AND p.prize_name = '故宫文创礼盒') OR
    (u.username = 'paul' AND a.activity_name = '国庆节欢乐购' AND p.prize_name = '大闸蟹礼券') OR
    (u.username = 'quinn' AND a.activity_name = '春节大抽奖' AND p.prize_name = '茶具套装') OR
    (u.username = 'rachel' AND a.activity_name = '元宵节灯谜会' AND p.prize_name = '谢谢参与') OR
    (u.username = 'steve' AND a.activity_name = '端午节龙舟赛' AND p.prize_name = '华为Mate60 Pro') OR
    (u.username = 'tina' AND a.activity_name = '中秋节团圆礼' AND p.prize_name = '茅台飞天53度') OR
    (u.username = 'ursula' AND a.activity_name = '国庆节欢乐购' AND p.prize_name = '故宫文创礼盒') OR
    (u.username = 'victor' AND a.activity_name = '春节大抽奖' AND p.prize_name = '大闸蟹礼券') OR
    (u.username = 'wendy' AND a.activity_name = '元宵节灯谜会' AND p.prize_name = '茶具套装') OR
    (u.username = 'xander' AND a.activity_name = '端午节龙舟赛' AND p.prize_name = '谢谢参与') OR
    (u.username = 'yara' AND a.activity_name = '中秋节团圆礼' AND p.prize_name = '华为Mate60 Pro')
);

-- 插入活动用户数据
INSERT IGNORE INTO lottery_activity_user (activity_id, user_id, participation_date, lottery_attempts)
SELECT 
    a.activity_id,
    u.user_id,
    CASE 
        WHEN u.username = 'alice' THEN '2023-10-01 09:00:00'
        WHEN u.username = 'bob' THEN '2023-10-01 10:00:00'
        WHEN u.username = 'charlie' THEN '2023-11-01 11:00:00'
        WHEN u.username = 'david' THEN '2023-12-01 12:00:00'
        WHEN u.username = 'eve' THEN '2023-12-02 13:00:00'
        WHEN u.username = 'frank' THEN '2023-12-03 14:00:00'
        WHEN u.username = 'grace' THEN '2023-12-04 15:00:00'
        WHEN u.username = 'hank' THEN '2023-12-05 16:00:00'
        WHEN u.username = 'irene' THEN '2023-12-06 17:00:00'
        WHEN u.username = 'jack' THEN '2023-12-07 18:00:00'
        WHEN u.username = 'kate' THEN '2023-12-08 19:00:00'
        WHEN u.username = 'leo' THEN '2023-12-09 20:00:00'
        WHEN u.username = 'mike' THEN '2023-12-10 21:00:00'
        WHEN u.username = 'nina' THEN '2023-12-11 22:00:00'
        WHEN u.username = 'oliver' THEN '2023-12-12 23:00:00'
        WHEN u.username = 'paul' THEN '2023-12-13 00:00:00'
        WHEN u.username = 'quinn' THEN '2023-12-14 01:00:00'
        WHEN u.username = 'rachel' THEN '2023-12-15 02:00:00'
        WHEN u.username = 'steve' THEN '2023-12-16 03:00:00'
        WHEN u.username = 'tina' THEN '2023-12-17 04:00:00'
        WHEN u.username = 'ursula' THEN '2023-12-18 05:00:00'
        WHEN u.username = 'victor' THEN '2023-12-19 06:00:00'
        WHEN u.username = 'wendy' THEN '2023-12-20 07:00:00'
        WHEN u.username = 'xander' THEN '2023-12-21 08:00:00'
        WHEN u.username = 'yara' THEN '2023-12-22 09:00:00'
    END as participation_date,
    CASE 
        WHEN u.username IN ('alice', 'bob', 'eve', 'frank', 'grace', 'leo', 'quinn', 'victor') THEN 1
        WHEN u.username IN ('david', 'jack', 'paul', 'ursula', 'wendy') THEN 2
        WHEN u.username IN ('charlie', 'irene', 'oliver', 'steve', 'yara') THEN 3
        WHEN u.username IN ('hank', 'mike', 'tina', 'xander') THEN 4
        WHEN u.username IN ('nina', 'rachel') THEN 5
    END as lottery_attempts
FROM lottery_user u
CROSS JOIN lottery_activity a
WHERE (
    (u.username = 'alice' AND a.activity_name = '春节大抽奖') OR
    (u.username = 'bob' AND a.activity_name = '春节大抽奖') OR
    (u.username = 'charlie' AND a.activity_name = '元宵节灯谜会') OR
    (u.username = 'david' AND a.activity_name = '端午节龙舟赛') OR
    (u.username = 'eve' AND a.activity_name = '中秋节团圆礼') OR
    (u.username = 'frank' AND a.activity_name = '国庆节欢乐购') OR
    (u.username = 'grace' AND a.activity_name = '春节大抽奖') OR
    (u.username = 'hank' AND a.activity_name = '元宵节灯谜会') OR
    (u.username = 'irene' AND a.activity_name = '端午节龙舟赛') OR
    (u.username = 'jack' AND a.activity_name = '中秋节团圆礼') OR
    (u.username = 'kate' AND a.activity_name = '国庆节欢乐购') OR
    (u.username = 'leo' AND a.activity_name = '春节大抽奖') OR
    (u.username = 'mike' AND a.activity_name = '元宵节灯谜会') OR
    (u.username = 'nina' AND a.activity_name = '端午节龙舟赛') OR
    (u.username = 'oliver' AND a.activity_name = '中秋节团圆礼') OR
    (u.username = 'paul' AND a.activity_name = '国庆节欢乐购') OR
    (u.username = 'quinn' AND a.activity_name = '春节大抽奖') OR
    (u.username = 'rachel' AND a.activity_name = '元宵节灯谜会') OR
    (u.username = 'steve' AND a.activity_name = '端午节龙舟赛') OR
    (u.username = 'tina' AND a.activity_name = '中秋节团圆礼') OR
    (u.username = 'ursula' AND a.activity_name = '国庆节欢乐购') OR
    (u.username = 'victor' AND a.activity_name = '春节大抽奖') OR
    (u.username = 'wendy' AND a.activity_name = '元宵节灯谜会') OR
    (u.username = 'xander' AND a.activity_name = '端午节龙舟赛') OR
    (u.username = 'yara' AND a.activity_name = '中秋节团圆礼')
);