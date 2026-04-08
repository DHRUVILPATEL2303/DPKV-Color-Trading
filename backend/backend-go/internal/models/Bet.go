package models

import "time"

type Bet struct {
	RoundNumber int       `json:"round_number"`
	Amount      int64     `json:"amount"`
	Color       string    `json:"color"`
	Result      string    `json:"result"`
	CreatedAt   time.Time `json:"created_at"`
}
