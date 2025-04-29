-- 插入用户数据
INSERT IGNORE INTO `users` (`username`, `email`, `password`, `role_id`, `datachange_createtime`, `datachange_lasttime`) 
SELECT 
    u.username,
    u.email,
    u.password,
    CASE 
        WHEN u.username = 'admin' THEN (SELECT role_id FROM roles WHERE role_name = 'ADMIN')
        WHEN u.username = 'manager' THEN (SELECT role_id FROM roles WHERE role_name = 'ADMIN')
        ELSE (SELECT role_id FROM roles WHERE role_name = 'USER')
    END as role_id,
    u.datachange_createtime,
    u.datachange_lasttime
FROM (
    SELECT 'admin' as username, 'admin@example.com' as email, '$2a$10$eBz1J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J' as password, '2023-01-01 00:00:00' as datachange_createtime, '2023-01-01 00:00:00' as datachange_lasttime UNION ALL
    SELECT 'user1', 'user1@example.com', '$2a$10$eBz1J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J', '2023-01-02 00:00:00', '2023-01-02 00:00:00' UNION ALL
    SELECT 'user2', 'user2@example.com', '$2a$10$eBz1J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J', '2023-01-03 00:00:00', '2023-01-03 00:00:00' UNION ALL
    SELECT 'manager', 'manager@example.com', '$2a$10$eBz1J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J', '2023-01-04 00:00:00', '2023-01-04 00:00:00' UNION ALL
    SELECT 'guest', 'guest@example.com', '$2a$10$eBz1J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J9F8J8Q9J', '2023-01-05 00:00:00', '2023-01-05 00:00:00'
) u;

