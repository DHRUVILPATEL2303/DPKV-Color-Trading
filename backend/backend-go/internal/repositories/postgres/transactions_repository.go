package postgres

import (
	"Color-Trading/backend/backend-go/internal/models"
	"database/sql"
)

type TransactionRepository struct {
	db *sql.DB
}

type ITransactionRepository interface {
	GetAllUserTransactions(userId int32) ([]*models.Transaction, error)
}

func NewTransactionRepository(db *sql.DB) *TransactionRepository {
	return &TransactionRepository{
		db: db,
	}

}

func (t *TransactionRepository) GetAllUserTransactions(userId int32) ([]*models.Transaction, error) {

	rows, err := t.db.Query(`
		SELECT id, amount, type, created_at 
		FROM transactions 
		WHERE user_id = $1 
		ORDER BY created_at DESC
	`, userId)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var transactions []*models.Transaction

	for rows.Next() {
		var tx models.Transaction

		err := rows.Scan(
			&tx.Id,
			&tx.Amount,
			&tx.Type,
			&tx.CreatedAt,
		)
		if err != nil {
			return nil, err
		}

		transactions = append(transactions, &tx)
	}

	if err := rows.Err(); err != nil {
		return nil, err
	}

	return transactions, nil
}
