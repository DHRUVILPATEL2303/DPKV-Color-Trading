package routes

import (
	"Color-Trading/backend/backend-go/internal/admin"
	"Color-Trading/backend/backend-go/internal/handlers"

	"github.com/gin-gonic/gin"
)

func AdminRoutes(r *gin.RouterGroup, handler *handlers.AdminHandler) {

	router := r.Group("/admin")
	router.POST("/add-funds", admin.AdminOnly, handler.AddFunds)
	router.POST("/deduct-funds", admin.AdminOnly, handler.DeductFunds)

}
