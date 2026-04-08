package postgres

import (
	"Color-Trading/backend/backend-go/internal/models"
	"database/sql"
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
}

func (b *BetRepository) SaveBet(userId int32, round int64, amount int64, color string) error {
	_, err := b.db.Exec("INSERT INTO bets (user_id,round_number,amount,color) VALUES ($1,$2,$3,$4)", userId, round, amount, color)
	if err != nil {
		return err
	}

	return nil
}
func (b *BetRepository) GetAllBetsHistory(userId int32) ([]*models.Bet, error) {

	rows, err := b.db.Query(`
		SELECT round_number, amount, color, created_at
		FROM bets
		WHERE user_id = $1
		ORDER BY created_at DESC
		LIMIT 20
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
