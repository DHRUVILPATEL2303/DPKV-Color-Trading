package main

import (
	internalgrpc "Color-Trading/backend/backend-go/internal/grpc"
	"Color-Trading/backend/backend-go/internal/repositories/postgres"
	"Color-Trading/backend/backend-go/internal/routes"
	"Color-Trading/backend/backend-go/internal/services"
	"Color-Trading/backend/backend-go/pkg/database/migrations"

	pb "Color-Trading/backend/backend-go/Color-Trading/backend/backend-go/proto/bettingpb"

	"database/sql"
	"fmt"
	"log"
	"net"
	"os"
	"time"

	_ "github.com/lib/pq"
	"google.golang.org/grpc"
)

func InitPostgres() *sql.DB {
	connStr := os.Getenv("DATABASE_URL")
	if connStr == "" {
		connStr = "postgres://postgres:password@localhost:5432/color_trading?sslmode=disable"
	}

	db, err := sql.Open("postgres", connStr)
	if err != nil {
		log.Fatal("Error Connecting to Postgres:", err)
	}

	db.SetConnMaxLifetime(time.Hour)
	db.SetMaxIdleConns(10)

	if err = db.Ping(); err != nil {
		log.Fatal("Error Pinging Postgres:", err)
	}

	fmt.Println("Connected to Postgres")
	return db
}

func main() {
	db := InitPostgres()

	err := migrations.RunMigrations(db)
	if err != nil {
		log.Fatal("Error Running Migrations:", err)
	}

	userRepo := postgres.NewUserRepository(db)
	walletRepo := postgres.NewWalletRepository(db)
	walletService := services.NewWalletService(walletRepo, &userRepo)

	betsRepo := postgres.NewBetRepository(db)
	betsService := services.NewBetsService(betsRepo)

	roundRepo := postgres.NewRoundsRepository(db)
	roundService := services.NewRoundsService(roundRepo)

	go startGRPCServer(walletService, *betsService, *roundService)

	router := routes.SetUpRouter(db)

	fmt.Println("HTTP Server running on :8080")
	router.Run(":8080")
}
func startGRPCServer(walletService *services.WalletService, service services.BetsService, roundService services.RoundsService) {

	lis, err := net.Listen("tcp", ":8082")
	if err != nil {
		log.Fatal("Error Listening:", err)
	}

	grpcServer := grpc.NewServer()

	bettingServer := internalgrpc.NewBettingServer(*walletService, service, roundService)

	pb.RegisterBettingServiceServer(grpcServer, bettingServer)

	fmt.Println("gRPC Server running on :8082")

	if err := grpcServer.Serve(lis); err != nil {
		log.Fatal("gRPC server failed:", err)
	}
}
