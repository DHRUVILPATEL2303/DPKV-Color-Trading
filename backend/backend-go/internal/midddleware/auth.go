package midddleware

import (
	"Color-Trading/backend/backend-go/pkg/jwtutils"
	"net/http"
	"strings"

	"github.com/golang-jwt/jwt/v5"

	"github.com/gin-gonic/gin"
)

func AuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")

		if authHeader == "" {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{
				"error": "token missing",
			})

			c.Abort()
			return
		}
		token := strings.TrimPrefix(authHeader, "Bearer ")
		if token == "" {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{
				"error": "token missing",
			})
		}

		validateToken, err := jwtutils.ValidateToken(token)
		if err != nil || !validateToken.Valid {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token"})
			c.Abort()
			return
		}

		claims := validateToken.Claims.(jwt.MapClaims)

		c.Set("user_id", int(claims["user_id"].(float64)))
		c.Set("email", claims["email"].(string))

		c.Next()

	}
}
