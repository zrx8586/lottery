-- 清理测试数据（先判断表是否存在）
DROP TABLE IF EXISTS lottery_record;
DROP TABLE IF EXISTS lottery_activity_user;
DROP TABLE IF EXISTS lottery_activity_prize;
DROP TABLE IF EXISTS lottery_prize;
DROP TABLE IF EXISTS lottery_activity;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
                                     role_id INT AUTO_INCREMENT PRIMARY KEY,
                                     role_name VARCHAR(50) NOT NULL UNIQUE,
    role_desc VARCHAR(200),
    datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
    datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
                                     user_id INT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NULL,
    phone VARCHAR(20) NULL,
    password VARCHAR(100) NOT NULL,
    role_id INT NOT NULL,
    datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
    datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 活动表
CREATE TABLE IF NOT EXISTS lottery_activity (
                                                activity_id INT AUTO_INCREMENT PRIMARY KEY,
                                                activity_name VARCHAR(100) NOT NULL,
    activity_desc TEXT, -- 活动描述
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'INACTIVE', -- 活动状态：上线/下线
    datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
    datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 重新设计的 lottery_prize 表
CREATE TABLE IF NOT EXISTS lottery_prize (
                                             prize_id INT AUTO_INCREMENT PRIMARY KEY,
                                             prize_name VARCHAR(100) NOT NULL,
    prize_desc TEXT, -- 奖品描述
    prize_image_url VARCHAR(255), -- 奖品图片链接
    prize_category VARCHAR(50), -- 奖品类别
    prize_value DECIMAL(10, 2), -- 奖品市场价值
    stock_quantity INT NOT NULL DEFAULT 0, -- 奖品总库存数量
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- 奖品是否可用
    datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
    datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 活动奖品表
CREATE TABLE IF NOT EXISTS lottery_activity_prize (
                                                      activity_prize_id INT AUTO_INCREMENT PRIMARY KEY,
                                                      activity_id INT NOT NULL,
                                                      prize_id INT NOT NULL,
                                                      probability DOUBLE NOT NULL,
                                                      quantity INT NOT NULL, -- 每场活动的奖品库存
                                                      version BIGINT NOT NULL DEFAULT 0, -- 乐观锁版本号
                                                      datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                      datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                      FOREIGN KEY (activity_id) REFERENCES lottery_activity(activity_id),
    FOREIGN KEY (prize_id) REFERENCES lottery_prize(prize_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 抽奖记录表
CREATE TABLE IF NOT EXISTS lottery_record (
                                              record_id INT AUTO_INCREMENT PRIMARY KEY,
                                              user_id INT NOT NULL,
                                              activity_id INT NOT NULL,
                                              prize_id INT NOT NULL,
                                              won_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                              datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                              datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                              FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (activity_id) REFERENCES lottery_activity(activity_id),
    FOREIGN KEY (prize_id) REFERENCES lottery_prize(prize_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 活动用户表
CREATE TABLE IF NOT EXISTS lottery_activity_user (
                                                     activity_user_id INT AUTO_INCREMENT PRIMARY KEY,
                                                     activity_id INT NOT NULL,
                                                     user_id INT NOT NULL,
                                                     participation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                     lottery_attempts INT NOT NULL DEFAULT 0, -- 可用抽奖次数
                                                     version BIGINT NOT NULL DEFAULT 0, -- 乐观锁版本号
                                                     datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                     datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                     FOREIGN KEY (activity_id) REFERENCES lottery_activity(activity_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE KEY unique_user_activity (user_id, activity_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入角色数据
INSERT INTO roles (role_name, role_desc) VALUES
                                             ('ADMIN', '管理员'),
                                             ('USER', '普通用户');

-- 插入用户数据
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
    ('并发测试活动', '多人并发抽奖测试活动', NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), 'ACTIVE');

-- 插入奖品数据
INSERT INTO lottery_prize (prize_name, prize_desc, prize_image_url, prize_category, prize_value, stock_quantity, is_active) VALUES
                                                                                                                                ('iPhone 15', '最新款iPhone手机', 'https://example.com/iphone.jpg', '电子产品', 6999.00, 100, true),
                                                                                                                                ('AirPods Pro', '无线降噪耳机', 'https://example.com/airpods.jpg', '电子产品', 1999.00, 200, true),
                                                                                                                                ('购物券100元', '100元购物券', 'https://example.com/coupon.jpg', '优惠券', 100.00, 1000, true),
                                                                                                                                ('购物券50元', '50元购物券', 'https://example.com/coupon.jpg', '优惠券', 50.00, 2000, true),
                                                                                                                                ('谢谢参与', '感谢参与', 'https://example.com/thanks.jpg', '其他', 0.00, 999999, true);

-- 插入活动奖品关联数据
INSERT INTO lottery_activity_prize (activity_id, prize_id, probability, quantity, version) VALUES
                                                                                               (1, 1, 0.001, 1, 0),    -- iPhone 15
                                                                                               (1, 2, 0.005, 5, 0),    -- AirPods Pro
                                                                                               (1, 3, 0.094, 100, 0),  -- 100元购物券
                                                                                               (1, 4, 0.2, 200, 0),    -- 50元购物券
                                                                                               (1, 5, 0.7, 999999, 0); -- 谢谢参与

-- 插入用户活动参与数据
INSERT INTO lottery_activity_user (activity_id, user_id, lottery_attempts, version) VALUES
                                                                                        (1, 1, 100, 0),  -- test_user1
                                                                                        (1, 2, 100, 0),  -- test_user2
                                                                                        (1, 3, 100, 0),  -- test_user3
                                                                                        (1, 4, 100, 0),  -- test_user4
                                                                                        (1, 5, 100, 0),  -- test_user5
                                                                                        (1, 6, 100, 0),  -- test_user6
                                                                                        (1, 7, 100, 0),  -- test_user7
                                                                                        (1, 8, 100, 0),  -- test_user8
                                                                                        (1, 9, 100, 0),  -- test_user9
                                                                                        (1, 10, 100, 0); -- test_user10
