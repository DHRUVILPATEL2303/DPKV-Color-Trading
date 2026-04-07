package engine

import "sync"

type Bet struct {
	UserID int
	Color  string
	Amount int
}

type BetStore struct {
	bets []Bet
	mu   sync.Mutex
}

func NewBetStore() *BetStore {
	return &BetStore{}
}

func (b *BetStore) AddBet(bet Bet) {
	b.mu.Lock()
	defer b.mu.Unlock()

	b.bets = append(b.bets, bet)
}

func (b *BetStore) GetBets() []Bet {
	b.mu.Lock()
	defer b.mu.Unlock()

	return b.bets
}

func (b *BetStore) Clear() {
	b.mu.Lock()
	defer b.mu.Unlock()

	b.bets = []Bet{}
}
