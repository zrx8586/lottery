-- 赛季表
CREATE TABLE IF NOT EXISTS season (
                                      season_id INT AUTO_INCREMENT PRIMARY KEY,
                                      season_name VARCHAR(100) NOT NULL,
                                      start_date DATETIME NOT NULL,
                                      end_date DATETIME NOT NULL,
                                      datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 用户表
CREATE TABLE IF NOT EXISTS game_user (
                                         user_id INT AUTO_INCREMENT PRIMARY KEY,
                                         username VARCHAR(50) NOT NULL,
                                         email VARCHAR(100) NOT NULL UNIQUE,
                                         datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                         datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 赛季排行榜记录表
CREATE TABLE IF NOT EXISTS season_leaderboard_record (
                                                         record_id INT AUTO_INCREMENT PRIMARY KEY,
                                                         season_id INT NOT NULL,
                                                         user_id INT NOT NULL,
                                                         score DOUBLE NOT NULL,
                                                         datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                         datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                         FOREIGN KEY (season_id) REFERENCES season(season_id),
                                                         FOREIGN KEY (user_id) REFERENCES game_user(user_id)
);