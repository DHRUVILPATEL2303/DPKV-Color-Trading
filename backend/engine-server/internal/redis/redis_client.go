package redis

import (
	"context"
	"os"
	"strings"

	"github.com/redis/go-redis/v9"
)

var ctx = context.Background()

func RedisClient() *redis.Client {
	addr := os.Getenv("REDIS_URL")
	if addr == "" {
		addr = "localhost:6379"
	}
	if strings.HasPrefix(addr, "redis://") || strings.HasPrefix(addr, "rediss://") {
		if opt, err := redis.ParseURL(addr); err == nil {
			return redis.NewClient(opt)
		}
	}
	return redis.NewClient(&redis.Options{
		Addr: addr,
	})
}
