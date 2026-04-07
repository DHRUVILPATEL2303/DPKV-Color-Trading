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


