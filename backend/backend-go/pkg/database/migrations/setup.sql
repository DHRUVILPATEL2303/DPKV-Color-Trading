
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

ALTER TABLE rounds
    ADD COLUMN IF NOT EXISTS status TEXT DEFAULT 'OPEN';

ALTER TABLE rounds ALTER COLUMN result DROP NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_bets_round'
    ) THEN
ALTER TABLE bets
    ADD CONSTRAINT fk_bets_round
        FOREIGN KEY (round_number) REFERENCES rounds(round_number);
END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_bets_user'
    ) THEN
ALTER TABLE bets
    ADD CONSTRAINT fk_bets_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
END IF;
END$$;


DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'check_amount_positive'
        AND conrelid = 'bets'::regclass
    ) THEN
ALTER TABLE bets
    ADD CONSTRAINT check_amount_positive CHECK (amount > 0);
END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'check_color'
        AND conrelid = 'bets'::regclass
    ) THEN
ALTER TABLE bets
    ADD CONSTRAINT check_color CHECK (color IN ('RED', 'GREEN'));
END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_bets_user ON bets(user_id);
CREATE INDEX IF NOT EXISTS idx_bets_round ON bets(round_number);
CREATE INDEX IF NOT EXISTS idx_rounds_status ON rounds(status);


ALTER TABLE bets DROP CONSTRAINT IF EXISTS unique_user_round;

ALTER TABLE bets ALTER COLUMN result SET DEFAULT 'PENDING';


CREATE TABLE IF NOT EXISTS admin_logs (
                                          id SERIAL PRIMARY KEY,
                                          admin_id INT NOT NULL,
                                          user_id INT NOT NULL,
                                          amount INT NOT NULL,
                                          action TEXT NOT NULL, -- ADD / DEDUCT
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_admin_logs_created_at ON admin_logs(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_rounds_round_number ON rounds(round_number DESC);