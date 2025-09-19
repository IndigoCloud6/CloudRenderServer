--
-- SQLiteStudio v3.4.4 生成的文件，周一 5月 13 11:56:35 2024
--
-- 所用的文本编码：System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;


-- 表：user
CREATE TABLE IF NOT EXISTS user (
                                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                                    username TEXT UNIQUE NOT NULL,
                                    password TEXT NOT NULL,
                                    email    TEXT UNIQUE,
                                    status   INTEGER DEFAULT 1,  -- 1:active, 0:inactive
                                    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
                                    name     TEXT  -- 保持向后兼容
);

-- 表：roles
CREATE TABLE IF NOT EXISTS roles (
                                    id   INTEGER PRIMARY KEY AUTOINCREMENT,
                                    name TEXT UNIQUE NOT NULL,
                                    description TEXT
);

-- 表：user_roles
CREATE TABLE IF NOT EXISTS user_roles (
                                         id      INTEGER PRIMARY KEY AUTOINCREMENT,
                                         user_id INTEGER NOT NULL,
                                         role_id INTEGER NOT NULL,
                                         FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
                                         FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
                                         UNIQUE(user_id, role_id)
);

-- 插入默认角色
INSERT OR IGNORE INTO roles (name, description) VALUES ('ADMIN', '管理员');
INSERT OR IGNORE INTO roles (name, description) VALUES ('USER', '普通用户');

-- 插入默认管理员用户 (密码: admin123) - 使用正确的BCrypt hash
INSERT OR IGNORE INTO user (username, password, email, status, name) 
VALUES ('admin', '$2a$12$OVLcEa.lBttVc6wOEGwDKecq2mqfHjDFgM1BVGYKxvnQ0Lk5RZtNK', 'admin@cloudrenderserver.com', 1, 'admin');


-- 表：instance
CREATE TABLE IF NOT EXISTS instance (
                                        ID           TEXT    PRIMARY KEY
                                            NOT NULL,
                                        ProjectID    TEXT,
                                        RenderConfig TEXT,
                                        State        INTEGER
);


-- 表：project
CREATE TABLE IF NOT EXISTS project (
                                       project_id      TEXT NOT NULL,
                                       project_name    TEXT NOT NULL,
                                       create_date     TEXT NOT NULL,
                                       project_content TEXT NOT NULL,
                                       save_path       TEXT NOT NULL
);


-- 表：system_config
CREATE TABLE IF NOT EXISTS system_config (
                                             ID                      INTEGER UNIQUE ON CONFLICT IGNORE
                                                 PRIMARY KEY,
                                             SignallingServerPort    INTEGER NOT NULL,
                                             RenderClientPath        TEXT    NOT NULL,
                                             MaximumInstanceCount    INTEGER NOT NULL,
                                             CoturnServerPort        INTEGER NOT NULL,
                                             FileSavePath            TEXT    NOT NULL,
                                             AutoRunSignallingServer INTEGER NOT NULL
                                                 DEFAULT (1),
                                             AutoRunCoturnServer     INTEGER NOT NULL
                                                 DEFAULT (1),
                                             CoturnLocalIP           TEXT,
                                             CoturnPublicIP          TEXT
);

INSERT INTO system_config (
    ID,
    SignallingServerPort,
    RenderClientPath,
    MaximumInstanceCount,
    CoturnServerPort,
    FileSavePath,
    AutoRunSignallingServer,
    AutoRunCoturnServer,
    CoturnLocalIP,
    CoturnPublicIP
)
VALUES (
           1,
           8888,
           './CloudRender/',
           3,
           19306,
           'c:/dtproject',
           1,
           1,
           NULL,
           NULL
       );


-- 为默认管理员分配管理员角色
INSERT OR IGNORE INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM user u, roles r WHERE u.username = 'admin' AND r.name = 'ADMIN';


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
