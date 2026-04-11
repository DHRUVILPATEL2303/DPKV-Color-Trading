package admin

import (
	"Color-Trading/backend/backend-go/pkg/response"

	"github.com/gin-gonic/gin"
)

var ADMIN_SECRET = "ADMINDPKV@123123"

func AdminOnly(c *gin.Context) {
	secret := c.GetHeader("X-Admin-Secret")

	if secret != ADMIN_SECRET {
		response.Error(c, 403, "unauthorized")
		c.Abort()
		return
	}

	c.Next()
}
