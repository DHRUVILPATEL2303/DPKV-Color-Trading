package models

import "time"

type AdminLog struct {
	Id        int32     `json:"id"`
	AdminId   int32     `json:"admin_id"`
	UserId    int32     `json:"user_id"`
	Amount    int64     `json:"amount"`
	Action    string    `json:"action"`
	CreatedAt time.Time `json:"created_at"`
}
