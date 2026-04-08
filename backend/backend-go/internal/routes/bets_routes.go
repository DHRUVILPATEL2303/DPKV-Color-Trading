package routes

import (
	"Color-Trading/backend/backend-go/internal/handlers"
	"Color-Trading/backend/backend-go/internal/midddleware"

	"github.com/gin-gonic/gin"
)

func BetsRoutes(r *gin.RouterGroup, h *handlers.BetsHandler) {
	bets := r.Group("/bets")
	bets.Use(midddleware.AuthMiddleware())
	bets.GET("/history", h.GetAllBetsHistory)

}
