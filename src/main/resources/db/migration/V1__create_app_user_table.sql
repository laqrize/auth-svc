CREATE TABLE IF NOT EXISTS app_users
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL,
    username         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    type          VARCHAR(255) NOT NULL,
    status        VARCHAR(3)   NOT NULL DEFAULT 'C',
    created_at    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    created_by_id    BIGINT,
    updated_at    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_by_id    BIGINT,
    enabled       BOOLEAN,
    locked        BOOLEAN,
    version       BIGINT,

    CONSTRAINT fk_app_users_created_by FOREIGN KEY (created_by_id)
        REFERENCES app_users(id),

    CONSTRAINT fk_app_users_updated_by
        FOREIGN KEY (updated_by_id)
            REFERENCES app_users (id)

);

CREATE UNIQUE INDEX IF NOT EXISTS uq_app_users_email_active
    ON app_users (email)
    WHERE status = 'A';


-- System user for automated operations
INSERT INTO app_users (id, email, username, password_hash, type, status, enabled, locked)
VALUES (1, 'SYSTEM', 'SYSTEM', 'NO_LOGIN_DISABLED_ACCOUNT', 'ADMIN', 'C', false, true);

-- Reset sequence to start after system user
ALTER SEQUENCE app_users_id_seq RESTART WITH 2;
