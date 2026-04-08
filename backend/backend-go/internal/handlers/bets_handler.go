package handlers

import (
	"Color-Trading/backend/backend-go/internal/services"
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
)

type BetsHandler struct {
	s *services.BetsService
}

func NewBetsHandler(s *services.BetsService) *BetsHandler {
	return &BetsHandler{s: s}
}

func (h *BetsHandler) GetAllBetsHistory(c *gin.Context) {
	userId := c.GetInt("user_id")

	history, err := h.s.GetAllBetsHistory(int32(userId))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"message": err.Error(),
		})
		log.Println("GetAllBetsHistory error:", err)
		return
	}

	c.JSON(http.StatusOK, history)
}
