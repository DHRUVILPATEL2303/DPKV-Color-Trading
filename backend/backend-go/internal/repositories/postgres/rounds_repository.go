package postgres

import "database/sql"

type RoundsRepository struct {
	db *sql.DB
}

type IRoundsRepository interface {
	GetLastRoundNumber() int
}

func NewRoundsRepository(db *sql.DB) *RoundsRepository {
	return &RoundsRepository{
		db: db,
	}
}
func (r *RoundsRepository) GetLastRoundNumber() int {
	var last int
	err := r.db.QueryRow(`SELECT COALESCE(MAX(round_number), 0) FROM rounds`).Scan(&last)
	if err != nil {
		return 0
	}

	return last
}
