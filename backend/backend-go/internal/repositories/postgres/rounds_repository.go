package postgres

import "database/sql"

type RoundsRepository struct {
	db *sql.DB
}

type IRoundsRepository interface {
	GetLastRound() (int, string, error)
	CreateRound(round int) error
	UpdateStatus(round int, status string) error
	SaveResult(round int, result string) error
	RefundRound(round int) error
}

func NewRoundsRepository(db *sql.DB) *RoundsRepository {
	return &RoundsRepository{
		db: db,
	}
}
func (r *RoundsRepository) GetLastRound() (int, string, error) {

	var round int
	var status string

	err := r.db.QueryRow(`
		SELECT round_number, status
		FROM rounds
		ORDER BY round_number DESC
		LIMIT 1
	`).Scan(&round, &status)

	if err != nil {
		return 0, "", err
	}

	return round, status, nil
}

func (r *RoundsRepository) CreateRound(round int) error {
	_, err := r.db.Exec(`
	INSERT INTO rounds (round_number, status)
	VALUES ($1, 'OPEN')
	ON CONFLICT (round_number) DO NOTHING
	`, round)

	return err
}
func (r *RoundsRepository) UpdateStatus(round int, status string) error {
	_, err := r.db.Exec(`
	UPDATE rounds
	SET status = $1
	WHERE round_number = $2
	`, status, round)

	return err
}
func (r *RoundsRepository) SaveResult(round int, result string) error {
	_, err := r.db.Exec(`
	UPDATE rounds
	SET result = $1, status = 'COMPLETED'
	WHERE round_number = $2
	`, result, round)

	return err
}
func (r *RoundsRepository) RefundRound(round int) error {

	tx, err := r.db.Begin()
	if err != nil {
		return err
	}

	_, err = tx.Exec(`
	UPDATE users
	SET balance = balance + b.amount
	FROM bets b
	WHERE b.user_id = users.id
	AND b.round_number = $1
	`, round)

	if err != nil {
		tx.Rollback()
		return err
	}

	_, err = tx.Exec(`
	UPDATE bets
	SET result = 'REFUNDED'
	WHERE round_number = $1
	`, round)

	if err != nil {
		tx.Rollback()
		return err
	}

	_, err = tx.Exec(`
	UPDATE rounds
	SET status = 'COMPLETED'
	WHERE round_number = $1
	`, round)

	if err != nil {
		tx.Rollback()
		return err
	}
	_, err = tx.Exec(`
INSERT INTO transactions (user_id, amount, type)
SELECT user_id, amount, 'REFUND'
FROM bets
WHERE round_number = $1
`, round)

	if err != nil {
		tx.Rollback()
		return err
	}

	return tx.Commit()
}
