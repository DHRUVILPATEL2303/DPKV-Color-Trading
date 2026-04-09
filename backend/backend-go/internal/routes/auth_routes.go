package routes

import (
	"Color-Trading/backend/backend-go/internal/handlers"

	"github.com/gin-gonic/gin"
)

func AuthRoutes(rg *gin.RouterGroup, h *handlers.UserHandler) {
	auth := rg.Group("/auth")
	auth.POST("/login", h.Login)
	auth.GET("/validateToken", h.ValidateToken)
	auth.POST("/signUp", h.CreateUserAccount)
	auth.POST("/refreshToken", h.RefreshToken)
	auth.GET("/profile", h.GetUserById)
	auth.POST("/logout", h.Logout)
}
