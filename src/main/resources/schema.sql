-- categories
CREATE TABLE IF NOT EXISTS categories
(
    category_id   INT          NOT NULL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- like_account (집계 테이블)
-- 복합 PK: (category_id, reference_id) -> LikeAccount의 EmbeddedId와 일치
CREATE TABLE IF NOT EXISTS like_account
(
    category_id  INT          NOT NULL,
    reference_id VARCHAR(100) NOT NULL,
    account      BIGINT       NOT NULL DEFAULT 0,
    version      INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (category_id, reference_id),
    CONSTRAINT fk_like_account_categories FOREIGN KEY (category_id)
        REFERENCES categories (category_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- likes (개별 좋아요 로그)
CREATE TABLE IF NOT EXISTS likes
(
    like_id      VARCHAR(255) NOT NULL PRIMARY KEY,
    category_id  INT          NOT NULL,
    reference_id VARCHAR(100) NOT NULL,
    liker_id     VARCHAR(100) NOT NULL,
    liked_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_likes_category_ref_liker (category_id, reference_id, liker_id),
    CONSTRAINT fk_likes_categories FOREIGN KEY (category_id)
        REFERENCES categories (category_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    -- 복합 FK: likes(category_id, reference_id) -> like_account(category_id, reference_id)
    CONSTRAINT fk_likes_like_account FOREIGN KEY (category_id, reference_id)
        REFERENCES like_account (category_id, reference_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 인덱스: 조회 패턴에 맞게 추가
CREATE INDEX IF NOT EXISTS idx_likes_reference ON likes (reference_id);
CREATE INDEX IF NOT EXISTS idx_likes_liker ON likes (liker_id);
CREATE INDEX IF NOT EXISTS idx_likes_category ON likes (category_id);
CREATE INDEX IF NOT EXISTS idx_like_account_reference ON like_account (reference_id);
