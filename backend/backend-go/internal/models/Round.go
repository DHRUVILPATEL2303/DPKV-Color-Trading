package models

import "time"

type Round struct {
	RoundNumber int64     `json:"round_number"`
	Result      string    `json:"result"`
	Status      string    `json:"status"`
	CreatedAt   time.Time `json:"created_at"`
}
