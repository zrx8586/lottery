-- 插入角色数据
INSERT IGNORE INTO roles (role_name, role_desc) VALUES
    ('ADMIN', '系统管理员，拥有所有权限'),
    ('USER', '普通用户，拥有基本权限');

-- 插入活动数据
INSERT IGNORE INTO lottery_activity (activity_name, activity_desc, start_date, end_date, status) VALUES
    ('春节大抽奖', '喜迎新春，好运连连！参与抽奖赢取丰厚奖品！', '2024-02-10 00:00:00', '2024-02-17 23:59:59', 'INACTIVE'),
    ('元宵节灯谜会', '猜灯谜，赢大奖！元宵佳节，好运连连！', '2024-02-24 00:00:00', '2024-02-24 23:59:59', 'INACTIVE'),
    ('端午节龙舟赛', '端午佳节，龙舟竞渡，参与抽奖赢取精美礼品！', '2024-06-10 00:00:00', '2024-06-10 23:59:59', 'INACTIVE'),
    ('中秋节团圆礼', '中秋月圆，阖家欢乐，参与抽奖赢取团圆好礼！', '2024-09-17 00:00:00', '2024-09-17 23:59:59', 'INACTIVE'),
    ('国庆节欢乐购', '欢度国庆，参与抽奖赢取超值好礼！', '2024-10-01 00:00:00', '2024-10-07 23:59:59', 'INACTIVE'),
    ('五一劳动节特惠', '劳动最光荣，参与抽奖赢取劳动节专属好礼！', '2024-05-01 00:00:00', '2024-05-07 23:59:59', 'ACTIVE'),
    ('夏日清凉季', '炎炎夏日，清凉好礼等你来拿！', '2024-07-15 00:00:00', '2024-08-15 23:59:59', 'ACTIVE'),
    ('开学季特惠', '新学期新气象，参与抽奖赢取开学好礼！', '2024-09-01 00:00:00', '2024-09-30 23:59:59', 'ACTIVE'),
    ('双十一狂欢节', '双十一狂欢，超值好礼等你来拿！', '2024-11-11 00:00:00', '2024-11-11 23:59:59', 'INACTIVE'),
    ('双十二年终盛典', '年终盛典，惊喜不断！', '2024-12-12 00:00:00', '2024-12-12 23:59:59', 'INACTIVE'),
    ('元旦跨年活动', '喜迎元旦，跨年狂欢！', '2024-12-31 00:00:00', '2025-01-01 23:59:59', 'INACTIVE'),
    ('情人节特惠', '浪漫情人节，甜蜜好礼！', '2024-02-14 00:00:00', '2024-02-14 23:59:59', 'INACTIVE'),
    ('女神节特惠', '女神节专属福利！', '2024-03-08 00:00:00', '2024-03-08 23:59:59', 'INACTIVE'),
    ('儿童节欢乐购', '六一儿童节，欢乐无限！', '2024-06-01 00:00:00', '2024-06-01 23:59:59', 'INACTIVE'),
    ('七夕情人节', '浪漫七夕，甜蜜好礼！', '2024-08-14 00:00:00', '2024-08-14 23:59:59', 'INACTIVE'),
    ('教师节特惠', '感恩教师节，专属好礼！', '2024-09-10 00:00:00', '2024-09-10 23:59:59', 'INACTIVE'),
    ('重阳节特惠', '重阳敬老，感恩回馈！', '2024-10-14 00:00:00', '2024-10-14 23:59:59', 'INACTIVE'),
    ('万圣节狂欢', '万圣节狂欢，惊喜不断！', '2024-10-31 00:00:00', '2024-10-31 23:59:59', 'INACTIVE'),
    ('感恩节特惠', '感恩回馈，超值好礼！', '2024-11-28 00:00:00', '2024-11-28 23:59:59', 'INACTIVE'),
    ('圣诞节狂欢', '圣诞狂欢，惊喜连连！', '2024-12-25 00:00:00', '2024-12-25 23:59:59', 'INACTIVE'),
    ('新年特惠', '喜迎新年，好运连连！', '2025-01-01 00:00:00', '2025-01-07 23:59:59', 'INACTIVE'),
    ('春节特惠', '新春特惠，好运连连！', '2025-01-29 00:00:00', '2025-02-05 23:59:59', 'INACTIVE'),
    ('元宵节特惠', '元宵佳节，好运连连！', '2025-02-12 00:00:00', '2025-02-12 23:59:59', 'INACTIVE'),
    ('情人节特惠', '浪漫情人节，甜蜜好礼！', '2025-02-14 00:00:00', '2025-02-14 23:59:59', 'INACTIVE'),
    ('女神节特惠', '女神节专属福利！', '2025-03-08 00:00:00', '2025-03-08 23:59:59', 'INACTIVE'),
    ('清明节特惠', '清明时节，感恩回馈！', '2025-04-05 00:00:00', '2025-04-05 23:59:59', 'INACTIVE');

-- 插入奖品数据
INSERT IGNORE INTO lottery_prize (prize_name, prize_desc, prize_image_url, prize_category, prize_value, stock_quantity, is_active) 
SELECT 
    prize_info.prize_name,
    prize_info.prize_desc,
    prize_info.prize_image_url,
    prize_info.prize_category,
    prize_info.prize_value,
    prize_info.stock_quantity,
    CASE 
        WHEN EXISTS (
            SELECT 1 
            FROM lottery_activity a 
            WHERE a.status = 'ACTIVE' 
            AND (
                (a.activity_name = '五一劳动节特惠' AND prize_info.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '故宫文创礼盒', '大闸蟹礼券', '茶具套装', '谢谢参与')) OR
                (a.activity_name = '夏日清凉季' AND prize_info.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '故宫文创礼盒', '大闸蟹礼券', '茶具套装', '谢谢参与')) OR
                (a.activity_name = '开学季特惠' AND prize_info.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '故宫文创礼盒', '大闸蟹礼券', '茶具套装', '谢谢参与'))
            )
        ) THEN TRUE 
        ELSE FALSE 
    END as is_active
FROM (
    SELECT '华为Mate60 Pro' as prize_name, '最新款华为旗舰手机，搭载麒麟9000S芯片' as prize_desc, '/assets/images/prizes/phones/huawei-mate60-pro.png' as prize_image_url, '手机' as prize_category, 6999.00 as prize_value, 10 as stock_quantity UNION ALL
    SELECT '茅台飞天53度', '贵州茅台酒，500ml装', '/assets/images/prizes/wine/moutai-feitian.png', '酒水', 1499.00, 20 UNION ALL
    SELECT '故宫文创礼盒', '故宫博物院官方授权文创产品', '/assets/images/prizes/cultural/forbidden-city-gift.png', '文创', 299.00, 50 UNION ALL
    SELECT '大闸蟹礼券', '阳澄湖大闸蟹，8只装', '/assets/images/prizes/food/crab_gift.jpg', '食品', 888.00, 30 UNION ALL
    SELECT '茶具套装', '景德镇青花瓷茶具，6件套', '/assets/images/prizes/household/tea_set.jpg', '家居', 599.00, 15 UNION ALL
    SELECT '谢谢参与', '感谢您的参与', '/assets/images/prizes/others/thanks.jpg', '其他', 0.00, 999999 UNION ALL
    SELECT 'iPhone 15 Pro', '苹果最新款旗舰手机', '/assets/images/prizes/phones/iphone-15-pro.png', '手机', 7999.00, 5 UNION ALL
    SELECT '小米14 Ultra', '小米最新款旗舰手机', '/assets/images/prizes/phones/xiaomi-14-ultra.png', '手机', 5999.00, 8 UNION ALL
    SELECT '三星S24 Ultra', '三星最新款旗舰手机', '/assets/images/prizes/phones/samsung-s24-ultra.png', '手机', 8999.00, 6 UNION ALL
    SELECT 'OPPO Find X7', 'OPPO最新款旗舰手机', '/assets/images/prizes/phones/oppo-find-x7.png', '手机', 4999.00, 10 UNION ALL
    SELECT 'vivo X100 Pro', 'vivo最新款旗舰手机', '/assets/images/prizes/phones/vivo-x100-pro.png', '手机', 5999.00, 8 UNION ALL
    SELECT '荣耀Magic6 Pro', '荣耀最新款旗舰手机', '/assets/images/prizes/phones/honor-magic6.png', '手机', 4999.00, 10 UNION ALL
    SELECT '联想拯救者Y9000P', '联想游戏本', '/assets/images/prizes/electronics/lenovo_y9000p.jpg', '电子产品', 9999.00, 5 UNION ALL
    SELECT '戴尔XPS 15', '戴尔高端笔记本', '/assets/images/prizes/electronics/dell_xps15.jpg', '电子产品', 12999.00, 4 UNION ALL
    SELECT '苹果MacBook Pro', '苹果高端笔记本', '/assets/images/prizes/electronics/macbook_pro.jpg', '电子产品', 14999.00, 3 UNION ALL
    SELECT '华为MateBook X Pro', '华为高端笔记本', '/assets/images/prizes/electronics/huawei_matebook.jpg', '电子产品', 9999.00, 5 UNION ALL
    SELECT '小米笔记本Pro', '小米高端笔记本', '/assets/images/prizes/electronics/xiaomi_notebook.jpg', '电子产品', 6999.00, 8 UNION ALL
    SELECT '荣耀MagicBook Pro', '荣耀高端笔记本', '/assets/images/prizes/electronics/honor_magicbook.jpg', '电子产品', 5999.00, 10 UNION ALL
    SELECT '联想小新Pro', '联想轻薄本', '/assets/images/prizes/electronics/lenovo_xiaoxin.jpg', '电子产品', 4999.00, 12 UNION ALL
    SELECT '戴尔灵越15', '戴尔轻薄本', '/assets/images/prizes/electronics/dell_inspiron.jpg', '电子产品', 5999.00, 8 UNION ALL
    SELECT '苹果iPad Pro', '苹果平板电脑', '/assets/images/prizes/electronics/ipad_pro.jpg', '电子产品', 6999.00, 6 UNION ALL
    SELECT '华为MatePad Pro', '华为平板电脑', '/assets/images/prizes/electronics/huawei_matepad.jpg', '电子产品', 4999.00, 8 UNION ALL
    SELECT '小米平板6 Pro', '小米平板电脑', '/assets/images/prizes/electronics/xiaomi_pad6.jpg', '电子产品', 2999.00, 10 UNION ALL
    SELECT '荣耀平板V8 Pro', '荣耀平板电脑', '/assets/images/prizes/electronics/honor_pad_v8.jpg', '电子产品', 2999.00, 10 UNION ALL
    SELECT '联想小新Pad Pro', '联想平板电脑', '/assets/images/prizes/electronics/lenovo_pad.jpg', '电子产品', 2999.00, 10 UNION ALL
    SELECT '戴尔XPS 13', '戴尔轻薄本', '/assets/images/prizes/electronics/dell_xps13.jpg', '电子产品', 9999.00, 5
) prize_info;

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
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '华为Mate60 Pro' THEN 0.02
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '茅台飞天53度' THEN 0.03
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '故宫文创礼盒' THEN 0.05
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '大闸蟹礼券' THEN 0.05
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '茶具套装' THEN 0.05
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '谢谢参与' THEN 0.80
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '华为Mate60 Pro' THEN 0.01
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '茅台飞天53度' THEN 0.02
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '故宫文创礼盒' THEN 0.05
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '大闸蟹礼券' THEN 0.05
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '茶具套装' THEN 0.05
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '谢谢参与' THEN 0.82
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '华为Mate60 Pro' THEN 0.01
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '茅台飞天53度' THEN 0.02
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '故宫文创礼盒' THEN 0.05
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '大闸蟹礼券' THEN 0.05
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '茶具套装' THEN 0.05
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '谢谢参与' THEN 0.82
        ELSE 0.00
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
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '华为Mate60 Pro' THEN 3
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '茅台飞天53度' THEN 5
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '故宫文创礼盒' THEN 10
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '大闸蟹礼券' THEN 10
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '茶具套装' THEN 10
        WHEN a.activity_name = '五一劳动节特惠' AND p.prize_name = '谢谢参与' THEN 999999
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '华为Mate60 Pro' THEN 2
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '茅台飞天53度' THEN 5
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '故宫文创礼盒' THEN 10
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '大闸蟹礼券' THEN 10
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '茶具套装' THEN 10
        WHEN a.activity_name = '夏日清凉季' AND p.prize_name = '谢谢参与' THEN 999999
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '华为Mate60 Pro' THEN 2
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '茅台飞天53度' THEN 5
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '故宫文创礼盒' THEN 10
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '大闸蟹礼券' THEN 10
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '茶具套装' THEN 10
        WHEN a.activity_name = '开学季特惠' AND p.prize_name = '谢谢参与' THEN 999999
        ELSE 0
    END as quantity
FROM lottery_activity a
CROSS JOIN lottery_prize p
WHERE (
    (a.activity_name = '春节大抽奖' AND p.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '故宫文创礼盒', '谢谢参与')) OR
    (a.activity_name = '元宵节灯谜会' AND p.prize_name IN ('大闸蟹礼券', '茶具套装', '故宫文创礼盒', '谢谢参与')) OR
    (a.activity_name = '端午节龙舟赛' AND p.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '大闸蟹礼券', '谢谢参与')) OR
    (a.activity_name = '中秋节团圆礼' AND p.prize_name IN ('茅台飞天53度', '故宫文创礼盒', '茶具套装', '谢谢参与')) OR
    (a.activity_name = '国庆节欢乐购' AND p.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '故宫文创礼盒', '大闸蟹礼券', '茶具套装', '谢谢参与')) OR
    (a.activity_name = '五一劳动节特惠' AND p.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '故宫文创礼盒', '大闸蟹礼券', '茶具套装', '谢谢参与')) OR
    (a.activity_name = '夏日清凉季' AND p.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '故宫文创礼盒', '大闸蟹礼券', '茶具套装', '谢谢参与')) OR
    (a.activity_name = '开学季特惠' AND p.prize_name IN ('华为Mate60 Pro', '茅台飞天53度', '故宫文创礼盒', '大闸蟹礼券', '茶具套装', '谢谢参与'))
);

-- 插入用户数据
INSERT IGNORE INTO users (username, email, password, role_id)
SELECT 
    u.username,
    u.email,
    u.password,
    CASE 
        WHEN u.username = 'admin' THEN (SELECT role_id FROM roles WHERE role_name = 'ADMIN')
        ELSE (SELECT role_id FROM roles WHERE role_name = 'USER')
    END as role_id
FROM (
    SELECT 'alice' as username, 'alice@example.com' as email, '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' as password UNION ALL
    SELECT 'bob', 'bob@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'charlie', 'charlie@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'david', 'david@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'eve', 'eve@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'frank', 'frank@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'grace', 'grace@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'hank', 'hank@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'irene', 'irene@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'jack', 'jack@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'kate', 'kate@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'leo', 'leo@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'mike', 'mike@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'nina', 'nina@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'oliver', 'oliver@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'paul', 'paul@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'quinn', 'quinn@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'rachel', 'rachel@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'steve', 'steve@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'tina', 'tina@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'ursula', 'ursula@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'victor', 'victor@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'wendy', 'wendy@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'xander', 'xander@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'yara', 'yara@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z' UNION ALL
    SELECT 'admin', 'admin@example.com', '$2a$10$X7Q8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z'
) u;

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
FROM users u
CROSS JOIN lottery_activity a
WHERE a.status = 'ACTIVE';

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
FROM users u
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