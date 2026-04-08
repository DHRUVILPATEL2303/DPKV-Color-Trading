package ws

import (
	"context"
	"log"
	"net/http"
	"ws-server-go/ws-server-go/internal/auth"

	enginepb "ws-server-go/ws-server-go/Color-Trading/backend/ws-server-go/proto/enginepb"
	grpcclient "ws-server-go/ws-server-go/internal/grpc"

	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

type Message struct {
	Type   string `json:"type"`
	Amount int    `json:"amount"`
	Color  string `json:"color"`
}

func HandleWS(hub *Hub, client *grpcclient.EngineClient) http.HandlerFunc {

	return func(w http.ResponseWriter, r *http.Request) {
		if r.Header.Get("X-Internal-Secret") != "dpkv123123" {
			http.Error(w, "Forbidden", http.StatusForbidden)
			return
		}

		token := r.URL.Query().Get("token")

		userID, _, err := auth.ValidateToken(token)
		if err != nil {
			http.Error(w, "Unauthorized", http.StatusUnauthorized)
			return
		}

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

			if err := conn.ReadJSON(&msg); err != nil {
				log.Println("Read error:", err)
				return
			}

			if msg.Type == "PLACE_BET" {

				resp, err := client.Client.RequestPlaceBet(context.Background(), &enginepb.PlaceBetRequest{
					UserId: int32(userID),
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
