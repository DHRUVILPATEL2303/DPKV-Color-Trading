package main

import (
	"Color-Trading/backend/backend-go/internal/routes"
	"Color-Trading/backend/backend-go/pkg/database/migrations"
	"database/sql"
	"fmt"
	"log"
	"time"

	_ "github.com/lib/pq"
)

func InitPostgres() *sql.DB {
	connStr := "postgres://postgres:password@localhost:5432/color_trading?sslmode=disable"

	db, err := sql.Open("postgres", connStr)
	if err != nil {
		log.Fatal("Error Connecting to Postgres:", err)
	}

	db.SetConnMaxLifetime(time.Hour)
	db.SetMaxIdleConns(10)

	if err = db.Ping(); err != nil {
		log.Fatal("Error Pinging Postgres:", err)
	}

	fmt.Println(" Connected to Postgres")
	return db
}

func main() {
	db := InitPostgres()
	err := migrations.RunMigrations(db)
	if err != nil {
		log.Fatal("Error Running Migrations:", err)
	}

	router := routes.SetUpRouter(db)

	fmt.Println(" Server running on :8080")

	router.Run(":8080")
}
