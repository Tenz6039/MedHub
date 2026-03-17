-- ============================================
-- MedHub 智慧购药平台 - 完整数据库脚本
-- ============================================
-- 项目名称：MedHub (智慧购药平台)
-- 项目缩写：MH
-- 数据库名：med_hub
-- 创建日期：2026-03-17
-- 说明：本脚本包含完整的数据库结构、测试数据和优惠券功能
-- 使用方法：直接执行此脚本即可创建完整的数据库
-- ============================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 第一部分：数据库创建和基础表结构
-- ============================================

-- 创建数据库
DROP DATABASE IF EXISTS `med_hub`;
CREATE DATABASE `med_hub` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `med_hub`;

-- ============================================
-- 1. 员工表 (employee)
-- ============================================
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) NOT NULL COMMENT '姓名',
  `username` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `sex` varchar(2) NOT NULL COMMENT '性别',
  `id_number` varchar(18) NOT NULL COMMENT '身份证号',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态 0:禁用，1:启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='员工信息';

-- ============================================
-- 2. 分类表 (category)
-- ============================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` int DEFAULT NULL COMMENT '类型 1 药品分类 2 药品组合分类',
  `name` varchar(32) NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '顺序',
  `status` int DEFAULT NULL COMMENT '分类状态 0:禁用，1:启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='药品及药品组合分类';

-- ============================================
-- 3. 药品表 (medicine)
-- ============================================
DROP TABLE IF EXISTS `medicine`;
CREATE TABLE `medicine` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) NOT NULL COMMENT '药品名称',
  `category_id` bigint NOT NULL COMMENT '药品分类 id',
  `price` decimal(10,2) DEFAULT NULL COMMENT '药品价格',
  `image` varchar(255) DEFAULT NULL COMMENT '图片',
  `description` varchar(255) DEFAULT NULL COMMENT '描述信息',
  `status` int DEFAULT '1' COMMENT '0 下架 1 上架',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_medicine_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='药品';

-- ============================================
-- 4. 药品规格表 (medicine_spec)
-- ============================================
DROP TABLE IF EXISTS `medicine_spec`;
CREATE TABLE `medicine_spec` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `medicine_id` bigint NOT NULL COMMENT '药品 id',
  `name` varchar(32) DEFAULT NULL COMMENT '规格名称',
  `value` varchar(255) DEFAULT NULL COMMENT '规格数据 list',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='药品规格关系表';

-- ============================================
-- 5. 药品组合表 (medicine_combo)
-- ============================================
DROP TABLE IF EXISTS `medicine_combo`;
CREATE TABLE `medicine_combo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` bigint NOT NULL COMMENT '药品组合分类 id',
  `name` varchar(32) NOT NULL COMMENT '药品组合名称',
  `price` decimal(10,2) NOT NULL COMMENT '药品组合价格',
  `status` int DEFAULT '1' COMMENT '售卖状态 0:下架 1:上架',
  `description` varchar(255) DEFAULT NULL COMMENT '描述信息',
  `image` varchar(255) DEFAULT NULL COMMENT '图片',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_medicine_combo_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='药品组合';

-- ============================================
-- 6. 药品组合药品关系表 (medicine_combo_medicine)
-- ============================================
DROP TABLE IF EXISTS `medicine_combo_medicine`;
CREATE TABLE `medicine_combo_medicine` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `combo_id` bigint DEFAULT NULL COMMENT '药品组合 id',
  `medicine_id` bigint DEFAULT NULL COMMENT '药品 id',
  `name` varchar(32) DEFAULT NULL COMMENT '药品名称（冗余字段）',
  `price` decimal(10,2) DEFAULT NULL COMMENT '药品单价（冗余字段）',
  `copies` int DEFAULT NULL COMMENT '药品份数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='药品组合药品关系';

-- ============================================
-- 7. 用户表 (user)
-- ============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `openid` varchar(45) DEFAULT NULL COMMENT '微信用户唯一标识',
  `name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
  `sex` varchar(2) DEFAULT NULL COMMENT '性别',
  `id_number` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息';

-- ============================================
-- 8. 地址表 (address_book)
-- ============================================
DROP TABLE IF EXISTS `address_book`;
CREATE TABLE `address_book` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户 id',
  `consignee` varchar(50) DEFAULT NULL COMMENT '收货人',
  `sex` varchar(2) DEFAULT NULL COMMENT '性别',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `province_code` varchar(12) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '省级区划编号',
  `province_name` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '省级名称',
  `city_code` varchar(12) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '市级区划编号',
  `city_name` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '市级名称',
  `district_code` varchar(12) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '区级区划编号',
  `district_name` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '区级名称',
  `detail` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '详细地址',
  `label` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '标签',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '默认 0 否 1 是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='地址簿';

-- ============================================
-- 9. 购物车表 (shopping_cart)
-- ============================================
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) DEFAULT NULL COMMENT '药品名称',
  `image` varchar(255) DEFAULT NULL COMMENT '图片',
  `user_id` bigint NOT NULL COMMENT '主键',
  `medicine_id` bigint DEFAULT NULL COMMENT '药品 id',
  `combo_id` bigint DEFAULT NULL COMMENT '药品组合 id',
  `medicine_spec` varchar(50) DEFAULT NULL COMMENT '药品规格',
  `number` int NOT NULL DEFAULT '1' COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='购物车';

-- ============================================
-- 10. 订单表 (orders) - 包含所有字段
-- ============================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `number` varchar(50) DEFAULT NULL COMMENT '订单号',
  `status` int NOT NULL DEFAULT '1' COMMENT '订单状态 1 待付款 2 待接单 3 已接单 4 派送中 5 已完成 6 已取消 7 退款',
  `user_id` bigint NOT NULL COMMENT '下单用户',
  `address_book_id` bigint NOT NULL COMMENT '地址 id',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `checkout_time` datetime DEFAULT NULL COMMENT '结账时间',
  `pay_method` int NOT NULL DEFAULT '1' COMMENT '支付方式 1 微信，2 支付宝',
  `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态 0 未支付 1 已支付 2 退款',
  `coupon_id` bigint(20) DEFAULT NULL COMMENT '使用的用户优惠券 ID',
  `coupon_discount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠券抵扣金额',
  `amount` decimal(10,2) NOT NULL COMMENT '实收金额',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `user_name` varchar(32) DEFAULT NULL COMMENT '用户名称',
  `consignee` varchar(32) DEFAULT NULL COMMENT '收货人',
  `sex` varchar(1) DEFAULT NULL COMMENT '性别 0 女 1 男',
  `cancel_reason` varchar(255) DEFAULT NULL COMMENT '订单取消原因',
  `rejection_reason` varchar(255) DEFAULT NULL COMMENT '订单拒绝原因',
  `cancel_time` datetime DEFAULT NULL COMMENT '订单取消时间',
  `estimated_delivery_time` datetime DEFAULT NULL COMMENT '预计送达时间',
  `delivery_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '配送状态 1 立即送出 0 选择具体时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '送达时间',
  `pack_amount` int DEFAULT NULL COMMENT '打包费',
  `package_number` int DEFAULT NULL COMMENT '药品包装数量',
  `package_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '药品包装数量状态 1 按药量提供 0 选择具体数量',
  `delivery_fee` decimal(10,2) DEFAULT NULL COMMENT '配送费',
  `shop_id` bigint DEFAULT NULL COMMENT '店铺 ID',
  PRIMARY KEY (`id`),
  KEY `idx_coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表';

-- ============================================
-- 11. 订单明细表 (order_detail)
-- ============================================
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) DEFAULT NULL COMMENT '名字',
  `image` varchar(255) DEFAULT NULL COMMENT '图片',
  `order_id` bigint NOT NULL COMMENT '订单 id',
  `medicine_id` bigint DEFAULT NULL COMMENT '药品 id',
  `combo_id` bigint DEFAULT NULL COMMENT '药品组合 id',
  `medicine_spec` varchar(50) DEFAULT NULL COMMENT '药品规格',
  `number` int NOT NULL DEFAULT '1' COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单明细表';

-- ============================================
-- 12. 店铺信息表 (shop)
-- ============================================
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) NOT NULL COMMENT '店铺名称',
  `address` varchar(255) DEFAULT NULL COMMENT '店铺地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `delivery_fee` decimal(10,2) DEFAULT '0.00' COMMENT '配送费',
  `min_delivery_amount` decimal(10,2) DEFAULT '0.00' COMMENT '最低起送金额',
  `open_time` varchar(20) DEFAULT '09:00' COMMENT '营业时间 - 开始',
  `close_time` varchar(20) DEFAULT '21:00' COMMENT '营业时间 - 结束',
  `status` int NOT NULL DEFAULT '1' COMMENT '营业状态 0:休息 1:营业',
  `notice` varchar(500) DEFAULT NULL COMMENT '店铺公告',
  `description` varchar(500) DEFAULT NULL COMMENT '店铺描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_shop_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='店铺信息';

-- ============================================
-- 第二部分：优惠券功能表结构
-- ============================================

-- 13. 优惠券表 (coupon)
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` varchar(100) NOT NULL COMMENT '优惠券名称',
  `type` tinyint(4) NOT NULL COMMENT '优惠券类型：1-满减券，2-打折券',
  `min_amount` decimal(10,2) DEFAULT '0.00' COMMENT '满减条件（元），0 表示无门槛',
  `discount_value` decimal(10,2) NOT NULL COMMENT '优惠金额/折扣率',
  `max_discount_amount` decimal(10,2) DEFAULT NULL COMMENT '最大优惠金额（仅打折券）',
  `description` varchar(500) DEFAULT NULL COMMENT '优惠券描述',
  `total_count` int(11) DEFAULT '-1' COMMENT '发行总量，-1 表示不限量',
  `issued_count` int(11) DEFAULT '0' COMMENT '已领取数量',
  `user_limit` int(11) DEFAULT '1' COMMENT '单用户限领数量，-1 表示不限',
  `start_time` datetime NOT NULL COMMENT '有效期开始时间',
  `end_time` datetime NOT NULL COMMENT '有效期结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人 ID',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人 ID',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_time_range` (`start_time`,`end_time`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券表';

-- 14. 用户优惠券关联表 (user_coupon)
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券 ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0-未使用，1-已使用，2-已过期，3-已锁定',
  `obtain_time` datetime NOT NULL COMMENT '领取时间',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `order_id` bigint(20) DEFAULT NULL COMMENT '关联订单 ID',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_status` (`status`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户优惠券关联表';

-- 15. 优惠券使用记录表 (coupon_usage_record)
DROP TABLE IF EXISTS `coupon_usage_record`;
CREATE TABLE `coupon_usage_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券 ID',
  `user_coupon_id` bigint(20) NOT NULL COMMENT '用户优惠券 ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单 ID',
  `order_amount` decimal(10,2) NOT NULL COMMENT '订单原金额',
  `discount_amount` decimal(10,2) NOT NULL COMMENT '抵扣金额',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '实际支付金额',
  `use_time` datetime NOT NULL COMMENT '使用时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_use_time` (`use_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券使用记录表';

-- ============================================
-- 第三部分：索引优化
-- ============================================

-- 为订单表创建索引
CREATE INDEX `idx_orders_user_id` ON `orders`(`user_id`);
CREATE INDEX `idx_orders_number` ON `orders`(`number`);
CREATE INDEX `idx_orders_status` ON `orders`(`status`);
CREATE INDEX `idx_orders_order_time` ON `orders`(`order_time`);

-- 为订单明细表创建索引
CREATE INDEX `idx_order_detail_order_id` ON `order_detail`(`order_id`);

-- 为购物车表创建索引
CREATE INDEX `idx_shopping_cart_user_id` ON `shopping_cart`(`user_id`);

-- 为店铺表创建索引
CREATE INDEX `idx_shop_status` ON `shop`(`status`);

-- 为地址表创建索引
CREATE INDEX `idx_address_book_user_id` ON `address_book`(`user_id`);

-- 为药品表创建索引
CREATE INDEX `idx_medicine_category_id` ON `medicine`(`category_id`);
CREATE INDEX `idx_medicine_status` ON `medicine`(`status`);

-- 为药品组合表创建索引
CREATE INDEX `idx_medicine_combo_category_id` ON `medicine_combo`(`category_id`);
CREATE INDEX `idx_medicine_combo_status` ON `medicine_combo`(`status`);

-- 为药品规格表创建索引
CREATE INDEX `idx_medicine_spec_medicine_id` ON `medicine_spec`(`medicine_id`);

-- 为药品组合药品关系表创建索引
CREATE INDEX `idx_medicine_combo_medicine_combo_id` ON `medicine_combo_medicine`(`combo_id`);
CREATE INDEX `idx_medicine_combo_medicine_medicine_id` ON `medicine_combo_medicine`(`medicine_id`);

-- 为分类表创建索引
CREATE INDEX `idx_category_type` ON `category`(`type`);
CREATE INDEX `idx_category_status` ON `category`(`status`);

-- 为用户表创建索引
CREATE INDEX `idx_user_openid` ON `user`(`openid`);

-- ============================================
-- 第四部分：基础数据初始化
-- ============================================

-- 插入默认管理员账号
INSERT INTO `employee` (`id`, `name`, `username`, `password`, `phone`, `sex`, `id_number`, `status`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES (1, '管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '13812312312', '1', '110101199001010047', 1, NOW(), NOW(), 1, 1);

-- 插入药品分类数据
INSERT INTO `category` (`type`, `name`, `sort`, `status`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, '感冒发热', 10, 1, NOW(), NOW(), 1, 1),
(1, '消化系统', 9, 1, NOW(), NOW(), 1, 1),
(1, '心脑血管', 8, 1, NOW(), NOW(), 1, 1),
(1, '呼吸系统', 7, 1, NOW(), NOW(), 1, 1),
(1, '维生素矿物质', 6, 1, NOW(), NOW(), 1, 1),
(1, '外用药', 5, 1, NOW(), NOW(), 1, 1),
(1, '儿童用药', 4, 1, NOW(), NOW(), 1, 1),
(1, '老年用药', 3, 1, NOW(), NOW(), 1, 1);

-- 插入药品组合分类数据
INSERT INTO `category` (`type`, `name`, `sort`, `status`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(2, '家庭常备药', 12, 1, NOW(), NOW(), 1, 1),
(2, '儿童用药套装', 13, 1, NOW(), NOW(), 1, 1),
(2, '老年用药套装', 14, 1, NOW(), NOW(), 1, 1),
(2, '感冒护理套装', 15, 1, NOW(), NOW(), 1, 1),
(2, '营养保健套装', 16, 1, NOW(), NOW(), 1, 1);

-- 插入店铺默认数据
INSERT INTO `shop` (`id`, `name`, `address`, `phone`, `delivery_fee`, `min_delivery_amount`, `open_time`, `close_time`, `status`, `notice`, `description`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES (1, '智慧购药平台', 'XX 市 XX 区 XX 路 XX 号', '400-123-4567', 5.00, 20.00, '09:00', '21:00', 1, '24 小时在线服务，用药请咨询药师', '专业药店，品质保证', NOW(), NOW(), 1, 1);

-- 插入优惠券测试数据
INSERT INTO `coupon` (`name`, `type`, `min_amount`, `discount_value`, `max_discount_amount`, `description`, `total_count`, `user_limit`, `start_time`, `end_time`, `status`, `create_time`, `create_user`) 
VALUES 
('满 30 减 5 优惠券', 1, 30.00, 5.00, NULL, '药品满减优惠，满 30 元可用', 1000, 5, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1, NOW(), 1),
('满 50 减 10 优惠券', 1, 50.00, 10.00, NULL, '药品满减优惠，满 50 元可用', 500, 3, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1, NOW(), 1),
('无门槛减 3 元券', 1, 0.00, 3.00, NULL, '无门槛现金券，直接减 3 元', 2000, 2, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1, NOW(), 1),
('8.5 折优惠券', 2, 0.00, 0.85, 10.00, '全场药品 8.5 折，最高优惠 10 元', 800, 2, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1, NOW(), 1),
('9 折优惠券', 2, 0.00, 0.90, 5.00, '全场药品 9 折，最高优惠 5 元', 1500, 3, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1, NOW(), 1);

-- ============================================
-- 完成提示
-- ============================================
SET FOREIGN_KEY_CHECKS = 1;

SELECT '========================================' AS '';
SELECT 'MedHub 数据库创建完成！' AS message;
SELECT '========================================' AS '';
SELECT '数据库包含以下表：' AS '';
SELECT '1. employee - 员工表' AS table_info;
SELECT '2. category - 分类表' AS table_info;
SELECT '3. medicine - 药品表' AS table_info;
SELECT '4. medicine_spec - 药品规格表' AS table_info;
SELECT '5. medicine_combo - 药品组合表' AS table_info;
SELECT '6. medicine_combo_medicine - 药品组合药品关系表' AS table_info;
SELECT '7. user - 用户表' AS table_info;
SELECT '8. address_book - 地址表' AS table_info;
SELECT '9. shopping_cart - 购物车表' AS table_info;
SELECT '10. orders - 订单表' AS table_info;
SELECT '11. order_detail - 订单明细表' AS table_info;
SELECT '12. shop - 店铺信息表' AS table_info;
SELECT '13. coupon - 优惠券表' AS table_info;
SELECT '14. user_coupon - 用户优惠券关联表' AS table_info;
SELECT '15. coupon_usage_record - 优惠券使用记录表' AS table_info;
SELECT '========================================' AS '';
SELECT '测试数据已初始化，可以开始使用！' AS message;
SELECT '========================================' AS '';
