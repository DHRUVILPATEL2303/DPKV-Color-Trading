CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     email TEXT UNIQUE NOT NULL,
                                     password TEXT NOT NULL,
                                     balance INT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS  idx_user_email ON users(email);


CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              id SERIAL PRIMARY KEY,
                                              user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token TEXT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_refresh_user ON refresh_tokens(user_id);




CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY ,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount INT NOT NULL,
    type TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE INDEX IF NOT EXISTS idx_user_id ON transactions(user_id);


CREATE TABLE IF NOT EXISTS rounds(
    id SERIAL PRIMARY KEY,
    round_number INT NOT NULL UNIQUE,
    result TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()

);


CREATE TABLE IF NOT EXISTS bets (
                                    id SERIAL PRIMARY KEY,
                                    user_id INT,
                                    round_number INT,
                                    amount INT,
                                    color TEXT,
                                    created_at TIMESTAMP DEFAULT NOW()
    );

ALTER TABLE bets ADD COLUMN IF NOT EXISTS result TEXT;

UPDATE bets SET result = 'PENDING' WHERE result IS NULL;
