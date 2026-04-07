package engine

func CalculateResult(bets []Bet) string {

	totals := map[string]int{}

	for _, bet := range bets {
		totals[bet.Color] += bet.Amount
	}

	minColor := ""
	minValue := int(^uint(0) >> 1)

	for color, val := range totals {
		if val < minValue {
			minValue = val
			minColor = color
		}
	}

	return minColor
}
