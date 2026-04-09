package models

import "time"

type Transaction struct {
	Id        int32     `json:"id"`
	Amount    int64     `json:"amount"`
	Type      string    `json:"type"`
	CreatedAt time.Time `json:"created_at"`
}
