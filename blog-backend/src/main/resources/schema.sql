CREATE TABLE IF NOT EXISTS `user` (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(100) NOT NULL,
    nickname    VARCHAR(50)  DEFAULT NULL,
    avatar      VARCHAR(255) DEFAULT NULL,
    email       VARCHAR(100) DEFAULT NULL,
    role        VARCHAR(20)  DEFAULT 'admin',
    created_time DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME    DEFAULT NULL,
    deleted     TINYINT      DEFAULT 0
);

CREATE TABLE IF NOT EXISTS `category` (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    sort        INT          DEFAULT 0,
    created_time DATETIME    DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0
);

CREATE TABLE IF NOT EXISTS `tag` (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(50) NOT NULL,
    created_time DATETIME    DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0
);

CREATE TABLE IF NOT EXISTS `quote` (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    content      VARCHAR(500) NOT NULL,
    created_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    deleted      TINYINT      DEFAULT 0
);

CREATE TABLE IF NOT EXISTS `profile` (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50)  DEFAULT NULL,
    avatar     VARCHAR(255) DEFAULT NULL,
    bio        VARCHAR(500) DEFAULT NULL,
    tech_stack VARCHAR(500) DEFAULT NULL,
    email      VARCHAR(100) DEFAULT NULL,
    github     VARCHAR(255) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS `feed` (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    url          VARCHAR(500) NOT NULL,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `feed_article` (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    feed_id   BIGINT NOT NULL,
    feed_name VARCHAR(100) DEFAULT NULL,
    title     VARCHAR(500) DEFAULT NULL,
    link      VARCHAR(500) DEFAULT NULL,
    summary   CLOB,
    image     VARCHAR(500) DEFAULT NULL,
    pub_date  VARCHAR(100) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS `article` (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    title              VARCHAR(200) NOT NULL,
    summary            VARCHAR(500) DEFAULT NULL,
    content            CLOB,
    cover_image        VARCHAR(255) DEFAULT NULL,
    category_id        BIGINT       DEFAULT NULL,
    status             TINYINT      DEFAULT 0,
    view_count         BIGINT       DEFAULT 0,
    like_count         BIGINT       DEFAULT 0,
    is_top             TINYINT      DEFAULT 0,
    is_comment_enabled TINYINT      DEFAULT 1,
    card_type          TINYINT      DEFAULT 0,
    created_time       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_time       DATETIME     DEFAULT NULL,
    deleted            TINYINT      DEFAULT 0
);

CREATE TABLE IF NOT EXISTS `article_tag` (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS `comment` (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id  BIGINT       NOT NULL,
    parent_id   BIGINT       DEFAULT NULL,
    nickname    VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) DEFAULT NULL,
    content     VARCHAR(500) NOT NULL,
    status      TINYINT      DEFAULT 0,
    created_time DATETIME    DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0
);
