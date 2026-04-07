package main

import (
	"log"
	"net/http"
	"ws-server-go/ws-server-go/internal/grpc"
	"ws-server-go/ws-server-go/internal/ws"
)

func main() {

	client := grpc.NewClientGrpc()

	http.HandleFunc("/ws", ws.HandleWS(client))

	log.Println("WebSocket server running on :8090")

	err := http.ListenAndServe(":8090", nil)
	if err != nil {
		log.Fatal(err)
	}
}
