package redis

import (
	"context"
	"encoding/json"

	"github.com/redis/go-redis/v9"
)

type RoundRepository struct {
	rdb *redis.Client
}

func NewRoundRepository(rdb *redis.Client) *RoundRepository {
	return &RoundRepository{rdb: rdb}
}

type RoundHistory struct {
	Round  int    `json:"round"`
	Result string `json:"result"`
}

func (r *RoundRepository) GetLastRounds() ([]RoundHistory, error) {
	ctx := context.Background()

	values, err := r.rdb.LRange(ctx, "recent_rounds", 0, 9).Result()
	if err != nil {
		return nil, err
	}

	var history []RoundHistory

	for _, v := range values {
		var item RoundHistory
		if err := json.Unmarshal([]byte(v), &item); err == nil {
			history = append(history, item)
		}
	}

	return history, nil
}
