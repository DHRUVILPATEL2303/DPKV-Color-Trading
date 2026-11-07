package subscriber

import (
	"context"
	"log"
	"os"
	"strings"
	"time"
	"ws-server-go/ws-server-go/internal/ws"

	"github.com/redis/go-redis/v9"
)

func StartSubscriber(hub *ws.Hub) {
	addr := os.Getenv("REDIS_URL")
	if addr == "" {
		addr = "localhost:6379"
	}

	var rdb *redis.Client
	if strings.HasPrefix(addr, "redis://") || strings.HasPrefix(addr, "rediss://") {
		if opt, err := redis.ParseURL(addr); err == nil {
			rdb = redis.NewClient(opt)
		}
	}
	if rdb == nil {
		rdb = redis.NewClient(&redis.Options{
			Addr: addr,
		})
	}

	for {
		ctx := context.Background()
		sub := rdb.Subscribe(ctx, "game_updates")

		_, err := sub.Receive(ctx)
		if err != nil {
			log.Printf("Failed to subscribe to game_updates, retrying in 2s: %v\n", err)
			time.Sleep(2 * time.Second)
			continue
		}

		log.Println("Subscribed to Redis channel: game_updates")
		ch := sub.Channel()

		for msg := range ch {
			hub.BroadcastMessage(msg.Payload)
		}

		log.Println("Redis subscription channel closed, reconnecting in 2s...")
		time.Sleep(2 * time.Second)
	}
}
