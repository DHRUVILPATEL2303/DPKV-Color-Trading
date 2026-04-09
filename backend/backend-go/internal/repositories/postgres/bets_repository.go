package postgres

import (
	"Color-Trading/backend/backend-go/internal/models"
	"database/sql"
	"errors"
)

type BetRepository struct {
	db *sql.DB
}

func NewBetRepository(db *sql.DB) *BetRepository {
	return &BetRepository{db: db}

}

type IBetRepository interface {
	SaveBet(userId int32, round int64, amount int64, color string) error
	GetAllBetsHistory(userId int32) ([]*models.Bet, error)
	UpdateBet(round int64, winColor string) error
	PlaceBet(userId int32, round int64, amount int64, color string) error
}

func (b *BetRepository) SaveBet(userId int32, round int64, amount int64, color string) error {
	_, err := b.db.Exec("INSERT INTO bets (user_id,round_number,amount,color,result) VALUES ($1,$2,$3,$4,$5)", userId, round, amount, color, "PENDING")
	if err != nil {
		return err
	}

	return nil
}
func (b *BetRepository) GetAllBetsHistory(userId int32) ([]*models.Bet, error) {

	rows, err := b.db.Query(`
		SELECT round_number, amount, color, result,created_at
		FROM bets
		WHERE user_id = $1
		ORDER BY created_at DESC
	
	`, userId)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var bets []*models.Bet

	for rows.Next() {

		var bet models.Bet

		err := rows.Scan(
			&bet.RoundNumber,
			&bet.Amount,
			&bet.Color,
			&bet.Result,
			&bet.CreatedAt,
		)
		if err != nil {
			return nil, err
		}

		bets = append(bets, &bet)
	}

	if err := rows.Err(); err != nil {
		return nil, err
	}

	return bets, nil
}

func (b *BetRepository) UpdateResult(round int, winningColor string) error {

	_, err := b.db.Exec(`
	UPDATE bets
	SET result = 'WIN'
	WHERE round_number = $1 AND color = $2
	`, round, winningColor)

	if err != nil {
		return err
	}

	_, err = b.db.Exec(`
	UPDATE bets
	SET result = 'LOSS'
	WHERE round_number = $1 AND color != $2
	`, round, winningColor)

	return err
}
func (s *BetRepository) PlaceBet(userID int32, round int64, amount int64, color string) error {
	tx, err := s.db.Begin()
	if err != nil {
		return err
	}

	var balance int
	err = tx.QueryRow("SELECT balance FROM users WHERE id=$1", userID).Scan(&balance)
	if err != nil {
		tx.Rollback()
		return err
	}

	if int(balance) < int(amount) {
		tx.Rollback()
		return errors.New("insufficient balance")
	}

	_, err = tx.Exec(`
	INSERT INTO bets (user_id, round_number, amount, color)
	VALUES ($1,$2,$3,$4)
	`, userID, round, amount, color)

	if err != nil {
		tx.Rollback()
		return err
	}

	_, err = tx.Exec(`
	UPDATE users
	SET balance = balance - $1
	WHERE id = $2
	`, amount, userID)

	if err != nil {
		tx.Rollback()
		return err
	}

	_, err = tx.Exec(`
	INSERT INTO transactions (user_id, amount, type)
	VALUES ($1,$2,'DEBIT')
	`, userID, amount)

	if err != nil {
		tx.Rollback()
		return err
	}

	return tx.Commit()
}
