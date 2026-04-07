CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     email TEXT UNIQUE NOT NULL,
                                     password TEXT NOT NULL,
                                     balance INT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS  idx_user_email ON users(email);