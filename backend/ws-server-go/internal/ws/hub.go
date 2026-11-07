package ws

import (
	"log"
	"sync"

	"github.com/gorilla/websocket"
)

type Hub struct {
	clients map[*websocket.Conn]bool
	mu      sync.Mutex
}

func NewHub() *Hub {
	return &Hub{
		clients: make(map[*websocket.Conn]bool),
	}
}

func (h *Hub) AddClient(conn *websocket.Conn) {
	h.mu.Lock()
	defer h.mu.Unlock()
	h.clients[conn] = true

}
func (h *Hub) RemoveClient(conn *websocket.Conn) {
	h.mu.Lock()
	defer h.mu.Unlock()
	delete(h.clients, conn)

}
func (h *Hub) BroadcastMessage(message any) {
	h.mu.Lock()
	defer h.mu.Unlock()
	for client := range h.clients {
		var err error
		if str, ok := message.(string); ok {
			err = client.WriteMessage(websocket.TextMessage, []byte(str))
		} else if b, ok := message.([]byte); ok {
			err = client.WriteMessage(websocket.TextMessage, b)
		} else {
			err = client.WriteJSON(message)
		}
		if err != nil {
			client.Close()
			delete(h.clients, client)
			log.Println("Error writing message:", err)
		}
	}
}
