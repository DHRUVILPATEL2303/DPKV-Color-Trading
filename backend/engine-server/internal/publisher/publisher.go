package publisher

import (
	"context"
	"encoding/json"
	"os"

	"github.com/redis/go-redis/v9"
)

type Publisher struct {
	rdb *redis.Client
}

func NewPublisher() *Publisher {
	addr := os.Getenv("REDIS_URL")
	if addr == "" {
		addr = "localhost:6379"
	}
	rdb := redis.NewClient(&redis.Options{
		Addr: addr,
	})

	return &Publisher{rdb}

}

func (p *Publisher) Publish(channel string, data any) error {
	jsonData, err := json.Marshal(data)
	if err != nil {
		return err
	}

	p.rdb.Publish(context.Background(), channel, jsonData)

	return nil
}
