package subscriber

import (
	"context"
	_ "log"
	"os"
	"ws-server-go/ws-server-go/internal/ws"

	"github.com/redis/go-redis/v9"
)

func StartSubscriber(hub *ws.Hub) {

	addr := os.Getenv("REDIS_URL")
	if addr == "" {
		addr = "localhost:6379"
	}
	rdb := redis.NewClient(&redis.Options{
		Addr: addr,
	})

	sub := rdb.Subscribe(context.Background(), "game_updates")

	ch := sub.Channel()

	for msg := range ch {
		hub.BroadcastMessage(msg.Payload)
	}
}
