-- 活动表
CREATE TABLE IF NOT EXISTS lottery_activity (
                                                activity_id INT AUTO_INCREMENT PRIMARY KEY,
                                                activity_name VARCHAR(100) NOT NULL,
                                                activity_desc TEXT, -- 活动描述
                                                start_date DATETIME NOT NULL,
                                                end_date DATETIME NOT NULL,
                                                datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 重新设计的 lottery_prize 表
CREATE TABLE IF NOT EXISTS lottery_prize (
                                             prize_id INT AUTO_INCREMENT PRIMARY KEY,
                                             prize_name VARCHAR(100) NOT NULL,
                                             prize_desc TEXT, -- 奖品描述
                                             prize_image_url VARCHAR(255), -- 奖品图片链接
                                             prize_category VARCHAR(50), -- 奖品类别
                                             prize_value DECIMAL(10, 2), -- 奖品市场价值
                                             stock_quantity INT NOT NULL DEFAULT 0, -- 奖品库存数量
                                             is_active BOOLEAN NOT NULL DEFAULT TRUE, -- 奖品是否可用
                                             datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                             datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 活动奖品表
CREATE TABLE IF NOT EXISTS lottery_activity_prize (
                                                      activity_prize_id INT AUTO_INCREMENT PRIMARY KEY,
                                                      activity_id INT NOT NULL,
                                                      prize_id INT NOT NULL,
                                                      probability DOUBLE NOT NULL,
                                                      quantity INT NOT NULL, -- 每场活动的奖品库存
                                                      datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                      datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                      FOREIGN KEY (activity_id) REFERENCES lottery_activity(activity_id),
                                                      FOREIGN KEY (prize_id) REFERENCES lottery_prize(prize_id)
);

-- 用户表
CREATE TABLE IF NOT EXISTS lottery_user (
                                            user_id INT AUTO_INCREMENT PRIMARY KEY,
                                            username VARCHAR(50) NOT NULL,
                                            email VARCHAR(100) NOT NULL UNIQUE,
                                            datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                            datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 抽奖记录表
CREATE TABLE IF NOT EXISTS lottery_record (
                                              record_id INT AUTO_INCREMENT PRIMARY KEY,
                                              user_id INT NOT NULL,
                                              activity_id INT NOT NULL,
                                              prize_id INT NOT NULL,
                                              won_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                              datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                              datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                              FOREIGN KEY (user_id) REFERENCES lottery_user(user_id),
                                              FOREIGN KEY (activity_id) REFERENCES lottery_activity(activity_id),
                                              FOREIGN KEY (prize_id) REFERENCES lottery_prize(prize_id)
);

-- 活动用户表
CREATE TABLE IF NOT EXISTS lottery_activity_user (
                                                     activity_user_id INT AUTO_INCREMENT PRIMARY KEY,
                                                     activity_id INT NOT NULL,
                                                     user_id INT NOT NULL,
                                                     participation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                     lottery_attempts INT NOT NULL, -- 可用抽奖次数
                                                     datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                     datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                     FOREIGN KEY (activity_id) REFERENCES lottery_activity(activity_id),
                                                     FOREIGN KEY (user_id) REFERENCES lottery_user(user_id)
);