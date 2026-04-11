package postgres

import (
	"database/sql"
	"errors"
)

type AdminRepository struct {
	db *sql.DB
}

func NewAdminRepository(db *sql.DB) *AdminRepository {
	return &AdminRepository{
		db: db,
	}
}

type IAdminRepository interface {
	AddFunds(userID int32, amount int64, adminId int64) error
	DeductFunds(userID int32, amount int64, adminId int64) error
}

func (r *AdminRepository) AddFunds(userID int32, amount int64, adminId int64) error {
	tx, err := r.db.Begin()
	if err != nil {
		return err
	}

	var balance int64
	err = tx.QueryRow(`
		SELECT balance FROM users WHERE id=$1 FOR UPDATE
	`, userID).Scan(&balance)

	if err != nil {
		tx.Rollback()
		return err
	}

	_, err = tx.Exec(`
		UPDATE users SET balance = balance + $1 WHERE id=$2
	`, amount, userID)

	if err != nil {
		tx.Rollback()
		return err
	}

	_, err = tx.Exec(`
		INSERT INTO transactions (user_id, amount, type)
		VALUES ($1, $2, 'CREDIT_ADMIN')
	`, userID, amount)

	if err != nil {
		tx.Rollback()
		return err
	}

	_, err = tx.Exec(`
		INSERT INTO admin_logs (admin_id, user_id, amount, action)
		VALUES ($1, $2, $3, 'ADD')
	`, adminId, userID, amount)

	if err != nil {
		tx.Rollback()
		return err
	}

	return tx.Commit()
}
func (r *AdminRepository) DeductFunds(userID int32, amount int64, adminId int64) error {
	tx, err := r.db.Begin()
	if err != nil {
		return err
	}

	var balance int64
	err = tx.QueryRow(`
		SELECT balance FROM users WHERE id=$1 FOR UPDATE
	`, userID).Scan(&balance)

	if err != nil {
		tx.Rollback()
		return err
	}

	if balance < amount {
		tx.Rollback()
		return errors.New("insufficient balance")
	}

	_, err = tx.Exec(`
		UPDATE users SET balance = balance - $1 WHERE id=$2
	`, amount, userID)

	if err != nil {
		tx.Rollback()
		return err
	}

	_, err = tx.Exec(`
		INSERT INTO transactions (user_id, amount, type)
		VALUES ($1, $2, 'DEBIT_ADMIN')
	`, userID, amount)

	if err != nil {
		tx.Rollback()
		return err
	}

	_, err = tx.Exec(`
		INSERT INTO admin_logs (admin_id, user_id, amount, action)
		VALUES ($1, $2, $3, 'DEDUCT')
	`, adminId, userID, amount)

	if err != nil {
		tx.Rollback()
		return err
	}

	return tx.Commit()
}
