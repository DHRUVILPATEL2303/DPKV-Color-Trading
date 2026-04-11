package handlers

import (
	"Color-Trading/backend/backend-go/internal/services"
	"Color-Trading/backend/backend-go/pkg/response"
	"net/http"

	"github.com/gin-gonic/gin"
)

type AdminHandler struct {
	service services.AdminService
}

func NewAdminHandler(service services.AdminService) *AdminHandler {
	return &AdminHandler{service: service}
}

type FundRequest struct {
	UserID int32 `json:"user_id" binding:"required"`
	Amount int64 `json:"amount" binding:"required"`
}

func (h *AdminHandler) AddFunds(c *gin.Context) {
	var req FundRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		response.Error(c, http.StatusBadRequest, err.Error())
		return
	}

	adminID := int64(1)

	err := h.service.AddFunds(req.UserID, req.Amount, adminID)
	if err != nil {
		response.Error(c, http.StatusBadRequest, err.Error())
		return
	}

	response.Success(
		c,
		http.StatusOK,
		"funds added successfully",
		gin.H{
			"user_id": req.UserID,
			"amount":  req.Amount,
		},
	)
}

func (h *AdminHandler) DeductFunds(c *gin.Context) {
	var req FundRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		response.Error(c, http.StatusBadRequest, err.Error())
		return
	}

	adminID := int64(1)

	err := h.service.DeductFunds(req.UserID, req.Amount, adminID)
	if err != nil {
		response.Error(c, http.StatusBadRequest, err.Error())
		return
	}

	response.Success(
		c,
		http.StatusOK,
		"funds deducted successfully",
		gin.H{
			"user_id": req.UserID,
			"amount":  req.Amount,
		},
	)
}
