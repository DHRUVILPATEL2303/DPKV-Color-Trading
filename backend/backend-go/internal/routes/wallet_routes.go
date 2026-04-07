package routes

import (
	"Color-Trading/backend/backend-go/internal/handlers"
	"Color-Trading/backend/backend-go/internal/midddleware"

	"github.com/gin-gonic/gin"
)

func WalletRoutes(router *gin.RouterGroup, h *handlers.WalletHandler) {
	rg := router.Group("/wallet")
	rg.Use(midddleware.AuthMiddleware())
	
	rg.POST("/add", h.AddMoney)
	rg.POST("/deduct", h.DeductMoney)
}
