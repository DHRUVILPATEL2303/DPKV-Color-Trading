package engine

import (
	"math/rand"
	"time"
)

var rng = rand.New(rand.NewSource(time.Now().UnixNano()))

func CalculateResult(bets []Bet) string {

	if len(bets) == 0 {
		return getRandomColor()
	}

	totals := map[string]int{
		"RED":   0,
		"GREEN": 0,
	}

	for _, bet := range bets {
		totals[bet.Color] += bet.Amount
	}

	if totals["RED"] == totals["GREEN"] {
		return getRandomColor()
	}

	if totals["RED"] < totals["GREEN"] {
		return "RED"
	}
	return "GREEN"
}

func getRandomColor() string {
	if rng.Intn(2) == 0 {
		return "RED"
	}
	return "GREEN"
}
