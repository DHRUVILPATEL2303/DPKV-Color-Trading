package ws

import (
	"context"
	"log"
	"net/http"

	enginepb "ws-server-go/ws-server-go/Color-Trading/backend/ws-server-go/proto/enginepb"
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

func HandleWS(hub *Hub, client *grpcclient.EngineClient) http.HandlerFunc {

	return func(w http.ResponseWriter, r *http.Request) {

		conn, err := upgrader.Upgrade(w, r, nil)
		if err != nil {
			log.Println("Upgrade error:", err)
			return
		}

		hub.AddClient(conn)

		defer func() {
			hub.RemoveClient(conn)
			conn.Close()
		}()

		for {
			var msg Message

			err := conn.ReadJSON(&msg)
			if err != nil {
				log.Println("Read error:", err)
				return
			}

			if msg.Type == "PLACE_BET" {

				resp, err := client.Client.RequestPlaceBet(context.Background(), &enginepb.PlaceBetRequest{
					UserId: int32(msg.UserID),
					Amount: int64(msg.Amount),
					Color:  msg.Color,
				})

				if err != nil {
					conn.WriteJSON(map[string]string{
						"error": err.Error(),
					})
					continue
				}

				conn.WriteJSON(resp)
			}
		}
	}
}
