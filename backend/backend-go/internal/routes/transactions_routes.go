package routes

import (
	"Color-Trading/backend/backend-go/internal/handlers"
	"Color-Trading/backend/backend-go/internal/midddleware"

	"github.com/gin-gonic/gin"
)

func TransactionRoutes(rg *gin.RouterGroup, h *handlers.TransactionHandler) {
	group := rg.Group("/transactions")

	group.Use(midddleware.AuthMiddleware())

	group.GET("/all", h.GetAllTransactionHandler)
}
