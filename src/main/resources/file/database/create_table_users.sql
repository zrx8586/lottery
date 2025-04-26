CREATE TABLE `users` (
                         `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                         `username` VARCHAR(50) NOT NULL UNIQUE,
                         `password` VARCHAR(255) NOT NULL,
                         `role` VARCHAR(50) DEFAULT 'USER',
                         datachange_createtime DATETIME DEFAULT CURRENT_TIMESTAMP,
                         datachange_lasttime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);