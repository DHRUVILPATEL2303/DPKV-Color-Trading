package engine

import (
	pb "en/Color-Trading/backend/engine-server/proto/bettingpb"
)

type Engine struct {
	manager *Manager
	store   *BetStore
	api     pb.BettingServiceClient
}

func NewEngine(m *Manager, s *BetStore, api pb.BettingServiceClient) *Engine {
	return &Engine{
		manager: m,
		store:   s,
		api:     api,
	}
}
