package subscriber

import (
	"context"
	_ "log"
	"ws-server-go/ws-server-go/internal/ws"

	"github.com/redis/go-redis/v9"
)

func StartSubscriber(hub *ws.Hub) {

	rdb := redis.NewClient(&redis.Options{
		Addr: "localhost:6379",
	})

	sub := rdb.Subscribe(context.Background(), "game_updates")

	ch := sub.Channel()

	for msg := range ch {
		hub.BroadcastMessage(msg.Payload)
	}
}
