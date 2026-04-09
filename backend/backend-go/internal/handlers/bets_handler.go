package handlers

import (
	"Color-Trading/backend/backend-go/internal/services"
	"Color-Trading/backend/backend-go/pkg/response"
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
		log.Println("GetAllBetsHistory error:", err)
		response.Error(c, http.StatusInternalServerError, err.Error())
		return
	}

	response.Success(c, http.StatusOK, "bets history fetched", history)
}
