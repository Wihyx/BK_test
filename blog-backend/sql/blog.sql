-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role`        VARCHAR(20)  DEFAULT 'admin' COMMENT '角色',
    `created_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME    DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(50)  NOT NULL COMMENT '分类名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
    `sort`        INT          DEFAULT 0 COMMENT '排序',
    `created_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
    `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`         VARCHAR(50) NOT NULL COMMENT '标签名称',
    `created_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`      TINYINT     DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 文章表
CREATE TABLE IF NOT EXISTS `article` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`             VARCHAR(200) NOT NULL COMMENT '文章标题',
    `summary`           VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
    `content`           LONGTEXT     COMMENT '文章内容',
    `cover_image`       VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
    `category_id`       BIGINT       DEFAULT NULL COMMENT '分类ID',
    `status`            TINYINT      DEFAULT 0 COMMENT '状态 0-草稿 1-已发布',
    `view_count`        BIGINT       DEFAULT 0 COMMENT '访问量',
    `like_count`        BIGINT       DEFAULT 0 COMMENT '点赞数',
    `is_top`            TINYINT      DEFAULT 0 COMMENT '是否置顶 0-否 1-是',
    `is_comment_enabled` TINYINT     DEFAULT 1 COMMENT '是否开启评论',
    `created_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time`      DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 文章标签关联表
CREATE TABLE IF NOT EXISTS `article_tag` (
    `id`         BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id`     BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`id`),
    KEY `idx_article` (`article_id`),
    KEY `idx_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `article_id`  BIGINT       NOT NULL COMMENT '文章ID',
    `parent_id`   BIGINT       DEFAULT NULL COMMENT '父评论ID',
    `nickname`    VARCHAR(50)  NOT NULL COMMENT '昵称',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `content`     VARCHAR(500) NOT NULL COMMENT '评论内容',
    `status`      TINYINT      DEFAULT 0 COMMENT '状态 0-待审核 1-通过 2-驳回',
    `created_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_article` (`article_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 初始化管理员账号 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `role`) VALUES ('admin', 'admin123', '管理员', 'admin');
