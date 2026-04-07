package ws

import (
	"context"
	"encoding/json"
	"log"
	"net/http"
	pb "ws-server-go/ws-server-go/Color-Trading/backend/ws-server-go/proto/bettingpb"

	grpcclient "ws-server-go/ws-server-go/internal/grpc"

	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool { return true },
}

type Message struct {
	Type   string `json:"type"`
	UserID int    `json:"user_id"`
	Amount int    `json:"amount"`
	Color  string `json:"color"`
}

func HandleWS(client *grpcclient.Client) http.HandlerFunc {

	return func(w http.ResponseWriter, r *http.Request) {

		conn, err := upgrader.Upgrade(w, r, nil)
		if err != nil {
			log.Println("Upgrade error:", err)
			return
		}
		defer conn.Close()

		for {
			_, msgBytes, err := conn.ReadMessage()
			if err != nil {
				log.Println("Read error:", err)
				return
			}

			var msg Message
			err = json.Unmarshal(msgBytes, &msg)
			if err != nil {
				return
			}

			if msg.Type == "PLACE_BET" {

				resp, err := client.Betting.PlaceBet(context.Background(), &pb.BetRequest{
					UserId: int32(msg.UserID),
					Amount: int64(int32(msg.Amount)),
					Color:  msg.Color,
				})

				if err != nil {
					err := conn.WriteJSON(map[string]string{
						"error": err.Error(),
					})
					if err != nil {
						return
					}
					continue
				}

				err = conn.WriteJSON(resp)
				if err != nil {
					return
				}
			}
		}
	}
}
