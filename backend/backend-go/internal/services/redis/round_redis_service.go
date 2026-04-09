package redis

import "Color-Trading/backend/backend-go/internal/repositories/redis"

type RoundRedisService struct {
	repo *redis.RoundRepository
}

func NewRoundRedisService(repo *redis.RoundRepository) *RoundRedisService {
	return &RoundRedisService{
		repo: repo,
	}
}

func (r *RoundRedisService) GetRoundHistoryFromRedis() ([]redis.RoundHistory, error) {
	rounds, err := r.repo.GetLastRounds()
	if err != nil {
		return nil, err
	}
	return rounds, nil

}
