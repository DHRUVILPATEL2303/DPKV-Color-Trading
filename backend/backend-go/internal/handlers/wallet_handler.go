package handlers

import "C"
import (
	"Color-Trading/backend/backend-go/internal/services"
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
)

type WalletHandler struct {
	service *services.WalletService
}

func NewWalletHandler(service *services.WalletService) *WalletHandler {
	return &WalletHandler{
		service: service,
	}
}

func (w *WalletHandler) AddMoney(c *gin.Context) {
	userId := c.GetInt("user_id")

	fmt.Println("USER ID:", userId)
	var req struct {
		Amount int `json:"amount" binding:"required"`
	}

	err := c.ShouldBindJSON(&req)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid request"})
		return
	}

	err = w.service.AddAmount(req.Amount, userId)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "money added successfully"})

}
func (w *WalletHandler) DeductMoney(c *gin.Context) {
	userID := c.GetInt("user_id")
	var req struct {
		Amount int `json:"amount" binding:"required"`
	}

	err := c.ShouldBindJSON(&req)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid request"})
		return
	}

	err = w.service.DeductAmount(req.Amount, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
	}
	c.JSON(http.StatusOK, gin.H{"message": "money deducted successfully"})
}
