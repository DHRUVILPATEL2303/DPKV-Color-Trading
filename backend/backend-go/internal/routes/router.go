package routes

import (
	"Color-Trading/backend/backend-go/internal/handlers"
	redis3 "Color-Trading/backend/backend-go/internal/redis"
	"Color-Trading/backend/backend-go/internal/repositories/postgres"
	"Color-Trading/backend/backend-go/internal/repositories/redis"
	"Color-Trading/backend/backend-go/internal/services"
	redis4 "Color-Trading/backend/backend-go/internal/services/redis"
	"database/sql"

	"github.com/gin-gonic/gin"
)

func SetUpRouter(db *sql.DB) *gin.Engine {
	r := gin.New()
	r.Use(gin.Logger(), gin.Recovery())
	r.Use(func(c *gin.Context) {
		if c.GetHeader("X-Internal-Secret") != "dpkv123123" {
			c.AbortWithStatusJSON(403, gin.H{"error": "Forbidden"})
			return
		}
		c.Next()
	})

	api := r.Group("/api/v1")

	userRepo := postgres.NewUserRepository(db)
	userService := services.NewUserService(userRepo)
	userHandler := handlers.NewUserHandler(userService)

	walletRepo := postgres.NewWalletRepository(db)
	walletService := services.NewWalletService(walletRepo, &userRepo)
	walletHandler := handlers.NewWalletHandler(walletService)

	betsRepo := postgres.NewBetRepository(db)
	betsService := services.NewBetsService(betsRepo)
	betsHandler := handlers.NewBetsHandler(betsService)

	transactionRepo := postgres.NewTransactionRepository(db)
	transactionService := services.NewTransactionService(transactionRepo)
	transactionHandler := handlers.NewTransactionHandler(transactionService)

	roundRepo := redis.NewRoundRepository(redis3.NewRedisClient())
	roundRedisService := redis4.NewRoundRedisService(roundRepo)
	roundHandler := handlers.NewRoundHandler(roundRedisService)

	AuthRoutes(api, userHandler)
	WalletRoutes(api, walletHandler)
	BetsRoutes(api, betsHandler)
	TransactionRoutes(api, transactionHandler)
	RoundRoutes(api, *roundHandler)

	return r
}
