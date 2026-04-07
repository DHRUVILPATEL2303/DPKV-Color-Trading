package engine

import (
	"sync"
	"time"
)

type Round struct {
	ID        int
	StartTime time.Time
	IsOpen    bool
}

type Manager struct {
	currentRound *Round
	mu           sync.RWMutex
}

func NewManager() *Manager {
	return &Manager{}
}

func (m *Manager) StartNewRound(id int) {
	m.mu.Lock()
	defer m.mu.Unlock()

	m.currentRound = &Round{
		ID:        id,
		StartTime: time.Now(),
		IsOpen:    true,
	}
}

func (m *Manager) CloseBetting() {
	m.mu.Lock()
	defer m.mu.Unlock()

	if m.currentRound != nil {
		m.currentRound.IsOpen = false
	}
}

func (m *Manager) GetRound() *Round {
	m.mu.RLock()
	defer m.mu.RUnlock()
	return m.currentRound
}
