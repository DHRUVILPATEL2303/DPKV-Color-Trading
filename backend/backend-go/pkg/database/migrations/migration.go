package migrations

import (
	"database/sql"
	"fmt"
	"os"
)

func RunMigrations(db *sql.DB) error {
	file, err := os.ReadFile("pkg/database/migrations/setup.sql")
	if err != nil {
		return err
	}

	_, err = db.Exec(string(file))
	if err != nil {
		return err
	}

	fmt.Println(" Database migrations complete")
	return nil

}
