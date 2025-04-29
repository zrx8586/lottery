-- 清空所有表（按依赖关系顺序）
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE lottery_record;
TRUNCATE TABLE lottery_activity_user;
TRUNCATE TABLE lottery_activity_prize;
TRUNCATE TABLE lottery_prize;
TRUNCATE TABLE lottery_activity;
TRUNCATE TABLE lottery_user;

SET FOREIGN_KEY_CHECKS = 1; 