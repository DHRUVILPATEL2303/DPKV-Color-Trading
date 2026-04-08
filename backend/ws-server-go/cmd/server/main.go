package main

import (
	"log"
	"net/http"
	"ws-server-go/ws-server-go/internal/grpc"
	"ws-server-go/ws-server-go/internal/subscriber"
	"ws-server-go/ws-server-go/internal/ws"
)

func main() {

	client := grpc.NewEngineClient()
	hub := ws.NewHub()

	go subscriber.StartSubscriber(hub)

	http.HandleFunc("/ws", ws.HandleWS(hub, client))

	log.Println("WebSocket server running on :8090")

	err := http.ListenAndServe(":8090", nil)
	if err != nil {
		log.Fatal(err)
	}
}
