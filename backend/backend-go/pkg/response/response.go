package response

import "github.com/gin-gonic/gin"

// APIResponse represents a generic structured format for all API responses
type APIResponse struct {
	Success bool        `json:"success"`
	Message string      `json:"message,omitempty"`
	Data    interface{} `json:"data,omitempty"`
	Error   string      `json:"error,omitempty"`
}

// Success sends a generic success response
func Success(c *gin.Context, statusCode int, message string, data interface{}) {
	c.JSON(statusCode, APIResponse{
		Success: true,
		Message: message,
		Data:    data,
	})
}

// Error sends a generic error response
func Error(c *gin.Context, statusCode int, errorMsg string) {
	c.JSON(statusCode, APIResponse{
		Success: false,
		Error:   errorMsg,
	})
}
