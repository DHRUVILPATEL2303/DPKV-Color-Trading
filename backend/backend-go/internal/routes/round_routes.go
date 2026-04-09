package routes

import (
	"Color-Trading/backend/backend-go/internal/handlers"
	"Color-Trading/backend/backend-go/internal/midddleware"

	"github.com/gin-gonic/gin"
)

func RoundRoutes(r *gin.RouterGroup, h handlers.RoundHandler) {
	rg := r.Group("/round")
	rg.Use(midddleware.AuthMiddleware())
	rg.GET("last10", h.GetLast10RoundHistory)
}
