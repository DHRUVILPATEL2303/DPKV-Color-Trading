package postgres

import "database/sql"

type WalletRepository struct {
	db *sql.DB
}

type IWalletRepository interface {
	UpdateBalance(userId int, amount int) error
}

func NewWalletRepository(db *sql.DB) *WalletRepository {
	return &WalletRepository{
		db: db,
	}
}
func (w *WalletRepository) UpdateBalance(userId int, amount int) error {

	tx, err := w.db.Begin()
	if err != nil {
		return err
	}

	res, err := tx.Exec(
		"UPDATE users SET balance = balance + $1 WHERE id=$2",
		amount, userId,
	)
	if err != nil {
		tx.Rollback()
		return err
	}

	rowsAffected, err := res.RowsAffected()
	if err != nil {
		tx.Rollback()
		return err
	}

	if rowsAffected == 0 {
		tx.Rollback()
		return sql.ErrNoRows
	}

	txType := "CREDIT"
	if amount < 0 {
		txType = "DEBIT"
	}

	_, err = tx.Exec(
		"INSERT INTO transactions (user_id, amount, type) VALUES ($1,$2,$3)",
		userId, amount, txType,
	)
	if err != nil {
		tx.Rollback()
		return err
	}

	return tx.Commit()
}
