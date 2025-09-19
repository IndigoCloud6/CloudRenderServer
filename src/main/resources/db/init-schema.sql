--
-- SQLiteStudio v3.4.4 生成的文件，周一 5月 13 11:56:35 2024
--
-- 所用的文本编码：System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;


-- 表：user
CREATE TABLE IF NOT EXISTS user (
                                    name     TEXT PRIMARY KEY
                                    UNIQUE,
                                    password TEXT NOT NULL
);


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


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
