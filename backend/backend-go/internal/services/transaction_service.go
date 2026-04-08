package services

import (
	"Color-Trading/backend/backend-go/internal/models"
	"Color-Trading/backend/backend-go/internal/repositories/postgres"
)

type TransactionService struct {
	repo *postgres.TransactionRepository
}

func NewTransactionService(repo *postgres.TransactionRepository) *TransactionService {
	return &TransactionService{
		repo: repo,
	}
}

func (t *TransactionService) GetAllUserTransactions(userId int32) ([]*models.Transaction, error) {
	transactions, err := t.repo.GetAllUserTransactions(userId)
	if err != nil {
		return nil, err
	}
	return transactions, nil

}
