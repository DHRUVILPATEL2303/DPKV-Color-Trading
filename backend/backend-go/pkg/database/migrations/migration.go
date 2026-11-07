package migrations

import (
	"database/sql"
	_ "embed"
	"fmt"
	"os"
)

//go:embed setup.sql
var embeddedSQL string

func RunMigrations(db *sql.DB) error {
	sqlContent := embeddedSQL
	if sqlContent == "" {
		file, err := os.ReadFile("pkg/database/migrations/setup.sql")
		if err != nil {
			return err
		}
		sqlContent = string(file)
	}

	_, err := db.Exec(sqlContent)
	if err != nil {
		return err
	}

	fmt.Println(" Database migrations complete")
	return nil
}
