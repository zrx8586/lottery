-- 幂等表
CREATE TABLE IF NOT EXISTS idempotent_record (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
                                   order_no VARCHAR(64) NOT NULL COMMENT '订单号',
                                   idempotency_key VARCHAR(128) NOT NULL UNIQUE COMMENT '幂等键',
                                   request TEXT COMMENT '请求数据',
                                   response TEXT COMMENT '响应数据',
                                   operation VARCHAR(64) NOT NULL COMMENT '操作类型',
                                   status TINYINT NOT NULL COMMENT '状态: 0-处理中, 1-成功, 2-失败',
                                   hit_count INT DEFAULT 0 COMMENT '命中次数',
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据更新时间'
) COMMENT='幂等记录表';