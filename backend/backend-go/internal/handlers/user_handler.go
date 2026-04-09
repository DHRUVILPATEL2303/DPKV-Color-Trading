package handlers

import (
	"Color-Trading/backend/backend-go/internal/services"
	"Color-Trading/backend/backend-go/pkg/jwtutils"
	"Color-Trading/backend/backend-go/pkg/response"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
)

type UserHandler struct {
	service *services.UserService
}

func NewUserHandler(service *services.UserService) *UserHandler {
	return &UserHandler{
		service: service,
	}
}

type AuthRequest struct {
	Email    string `json:"email" binding:"required,email"`
	Password string `json:"password" binding:"required,min=6"`
}

func (handler *UserHandler) GetUserById(c *gin.Context) {

	header := c.GetHeader("Authorization")

	if header == "" {
		response.Error(c, http.StatusUnauthorized, "missing token")
		return
	}

	tokenString := header
	if len(header) > 7 && header[:7] == "Bearer " {
		tokenString = header[7:]
	}

	token, err := jwtutils.ValidateToken(tokenString)
	if err != nil || !token.Valid {
		response.Error(c, http.StatusUnauthorized, "invalid token")
		return
	}

	claims, ok := token.Claims.(jwt.MapClaims)
	if !ok {
		response.Error(c, http.StatusUnauthorized, "invalid claims")
		return
	}

	userIDFloat, ok := claims["user_id"].(float64)
	if !ok {
		response.Error(c, http.StatusUnauthorized, "invalid user id")
		return
	}

	userID := int(userIDFloat)

	user, err := handler.service.FindUserByID(userID)
	if err != nil {
		response.Error(c, http.StatusNotFound, "user not found")
		return
	}

	response.Success(c, http.StatusOK, "user fetched successfully", gin.H{
		"user_id": user.Id,
		"email":   user.Email,
		"balance": user.Balance,
	})
}

func (handler *UserHandler) CreateUserAccount(c *gin.Context) {
	var req AuthRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		response.Error(c, http.StatusBadRequest, "invalid request body")
		return
	}

	err := handler.service.SignUp(req.Email, req.Password)
	if err != nil {
		response.Error(c, http.StatusBadRequest, err.Error())
		return
	}

	response.Success(c, http.StatusCreated, "user created successfully", gin.H{"user": "user created successfully"})
}

func (handler *UserHandler) Login(c *gin.Context) {
	var req AuthRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		response.Error(c, http.StatusBadRequest, "invalid request body")
		return
	}

	user, accessToken, refreshToken, err := handler.service.Login(req.Email, req.Password)
	if err != nil {
		response.Error(c, http.StatusBadRequest, err.Error())
		return
	}

	response.Success(c, http.StatusOK, "login successful", gin.H{"user_id": user.Id, "email": user.Email, "balance": user.Balance, "accessToken": accessToken, "refreshToken": refreshToken})
}
func (handler *UserHandler) ValidateToken(c *gin.Context) {

	authHeader := c.GetHeader("Authorization")

	if authHeader == "" {
		response.Error(c, http.StatusUnauthorized, "missing token")
		return
	}

	tokenString := authHeader
	if len(authHeader) > 7 && authHeader[:7] == "Bearer " {
		tokenString = authHeader[7:]
	}

	token, err := jwtutils.ValidateToken(tokenString)
	if err != nil || !token.Valid {
		response.Error(c, http.StatusUnauthorized, "invalid token")
		return
	}

	claims, ok := token.Claims.(jwt.MapClaims)
	if !ok {
		response.Error(c, http.StatusUnauthorized, "invalid claims")
		return
	}

	response.Success(c, http.StatusOK, "token validated", gin.H{
		"valid":   true,
		"user_id": claims["user_id"],
		"email":   claims["email"],
	})
}

func (handler *UserHandler) RefreshToken(c *gin.Context) {

	var req struct {
		RefreshToken string `json:"refresh_token" binding:"required"`
	}

	if err := c.ShouldBindJSON(&req); err != nil {
		response.Error(c, 400, "invalid request")
		return
	}

	token, err := jwtutils.ValidateToken(req.RefreshToken)
	if err != nil || !token.Valid {
		response.Error(c, 401, "invalid token")
		return
	}

	userID, err := handler.service.FindRefreshToken(req.RefreshToken)
	if err != nil {
		response.Error(c, 401, "token not found")
		return
	}

	user, _ := handler.service.FindUserByID(userID)

	newAccessToken, _ := jwtutils.GenerateToken(userID, user.Email)

	response.Success(c, 200, "token refreshed", gin.H{
		"access_token": newAccessToken,
	})
}

func (handler *UserHandler) Logout(c *gin.Context) {

	var req struct {
		RefreshToken string `json:"refresh_token" binding:"required"`
	}

	if err := c.ShouldBindJSON(&req); err != nil {
		response.Error(c, 400, "invalid request")
		return
	}

	err := handler.service.DeleteRefreshToken(req.RefreshToken)
	if err != nil {
		response.Error(c, 500, "failed to logout")
		return
	}

	response.Success(c, 200, "logged out successfully", gin.H{"message": "logged out successfully"})
}
