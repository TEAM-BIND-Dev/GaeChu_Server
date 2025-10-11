-- Schema for categories, likes, and like_account (MariaDB/MySQL compatible)
-- Uses IF NOT EXISTS to be idempotent


-- Create categories table
CREATE TABLE IF NOT EXISTS categories
(
    category_id   INT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Create like_account table (집계 테이블)
CREATE TABLE IF NOT EXISTS like_account
(
    reference_id VARCHAR(100) PRIMARY KEY,
    account      BIGINT NOT NULL DEFAULT 0,
    version      INT    NOT NULL DEFAULT 0
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Create likes table
CREATE TABLE IF NOT EXISTS likes
(
    like_id      VARCHAR(255) PRIMARY KEY,
    category_id  INT          NOT NULL,
    reference_id VARCHAR(100) NOT NULL,
    liker_id     VARCHAR(100) NOT NULL,
    liked_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_likes_category_ref_liker (category_id, reference_id, liker_id),
    FOREIGN KEY (category_id) REFERENCES categories (category_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_likes_reference ON likes (reference_id);
CREATE INDEX IF NOT EXISTS idx_likes_liker ON likes (liker_id);
CREATE INDEX IF NOT EXISTS idx_likes_category ON likes (category_id);
