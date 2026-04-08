package publisher

import (
	"context"
	"encoding/json"

	"github.com/redis/go-redis/v9"
)

type Publisher struct {
	rdb *redis.Client
}

func NewPublisher() *Publisher {
	rdb := redis.NewClient(&redis.Options{
		Addr: "localhost:6379",
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
