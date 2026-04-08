package handlers

import (
	"Color-Trading/backend/backend-go/internal/services"
	"net/http"

	"github.com/gin-gonic/gin"
)

type TransactionHandler struct {
	service *services.TransactionService
}

func NewTransactionHandler(service *services.TransactionService) *TransactionHandler {
	return &TransactionHandler{
		service: service,
	}
}

func (s *TransactionHandler) GetAllTransactionHandler(c *gin.Context) {
	value := c.GetInt("user_id")

	transactions, err := s.service.GetAllUserTransactions(int32(value))
	if err != nil {

		c.JSON(http.StatusInternalServerError, gin.H{
			"error": "Error in Fetching Transactions",
		})
	}

	c.JSON(http.StatusOK, transactions)

}
