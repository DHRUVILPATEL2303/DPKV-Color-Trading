package handlers

import (
	"Color-Trading/backend/backend-go/internal/services/redis"
	"Color-Trading/backend/backend-go/pkg/response"
	"net/http"

	"github.com/gin-gonic/gin"
)

type RoundHandler struct {
	service *redis.RoundRedisService
}

func NewRoundHandler(service *redis.RoundRedisService) *RoundHandler {
	return &RoundHandler{
		service: service,
	}
}
func (h *RoundHandler) GetLast10RoundHistory(c *gin.Context) {
	fromRedis, err := h.service.GetRoundHistoryFromRedis()
	if err != nil {
		response.Error(c, http.StatusInternalServerError, err.Error())
		return
	}

	response.Success(c, http.StatusOK, "bets history fetched", fromRedis)
}
