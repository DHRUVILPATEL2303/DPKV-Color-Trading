# DPKV Color Trading — Backend

A real-time color-trading game backend built with Go, designed around a microservice architecture. Players place bets on colors during 60-second rounds, and the engine automatically resolves results and settles winnings.

---

## Architecture Overview

```
                          ┌─────────────────────────────────────────────────────────┐
                          │                      CLIENT (Android App)                │
                          └────────────────────────────┬────────────────────────────┘
                                                       │
                                              HTTP / WebSocket
                                                       │
                          ┌────────────────────────────▼────────────────────────────┐
                          │                       NGINX  (:8085)                    │
                          │                      API Gateway                        │
                          │     • Rate Limiting: 10 req/s per IP (burst: 20)        │
                          │     • Internal Secret Header Injection                  │
                          │     • Routes /api  → backend-go  (:8080)                │
                          │     • Routes /ws   → ws-server-go (:8090)               │
                          └────────────┬──────────────────────────┬─────────────────┘
                                       │                          │
                              HTTP REST API                  WebSocket
                                       │                          │
               ┌───────────────────────▼──────┐    ┌─────────────▼───────────────────┐
               │         backend-go (:8080)   │    │      ws-server-go (:8090)       │
               │       REST API Server        │    │      WebSocket Server           │
               │                              │    │                                 │
               │  • /api/v1/auth/*            │    │  • JWT auth via query token     │
               │  • /api/v1/wallet/*  (auth)  │    │  • BroadcastMessage to clients  │
               │  • /api/v1/bets/*    (auth)  │    │  • PlaceBet via gRPC →          │
               │  • /api/v1/transactions/*    │    │    engine-server                │
               │                              │    │                                 │
               │  Middleware:                 │    │  Subscribes to Redis:           │
               │  • JWT Auth                  │    │  • "game_updates" channel       │
               │  • Internal Secret Check     │    │                                 │
               │                              │    └───────┬──────────────┬──────────┘
               │  gRPC Server (:8082):        │            │              │
               │  • PlaceBet                  │            │ gRPC         │ Redis Sub
               │  • SaveBet                   │            │              │
               │  • CreditAmount              │            ▼              ▼
               │  • SaveRoundResult           │    ┌───────────────┐ ┌───────────────┐
               │  • UpdateBetResult           │    │ engine-server │ │     Redis     │
               │  • CreateRound               │◄───┤    (:8083)    │ │    (:6379)    │
               │  • UpdateRoundStatus         │gRPC│  Game Engine  ├─► Pub: updates  │
               └───────────────┬──────────────┘    │               │ │ recent_rounds │
                               │                   │  Game Loop    │ │ current_round │
                          PostgreSQL               │  Handles Bets │ └───────────────┘
                           (:5432)                 │  Settles Wins │
                                                   └───────────────┘
```

---

## Microservices

### 1. `backend-go` — REST API Server (`:8080` HTTP, `:8082` gRPC)

The central REST API and data layer. All client HTTP traffic flows through this service.

**REST Endpoints:**

| Method | Route | Auth Required | Description |
|--------|-------|:---:|-------------|
| POST | `/api/v1/auth/signUp` | ✗ | Register a new user |
| POST | `/api/v1/auth/login` | ✗ | Login and receive JWT tokens |
| GET | `/api/v1/auth/validateToken` | ✗ | Validate a JWT token |
| POST | `/api/v1/auth/refreshToken` | ✗ | Refresh an expiring access token |
| POST | `/api/v1/auth/logout` | ✗ | Logout and invalidate session |
| POST | `/api/v1/wallet/add` | ✓ | Add funds to user wallet |
| POST | `/api/v1/wallet/deduct` | ✓ | Deduct funds from user wallet |
| GET | `/api/v1/bets/history` | ✓ | Retrieve user bet history |
| GET | `/api/v1/transactions/*` | ✓ | Retrieve user transaction history |

**gRPC Methods (`:8082`) — Internal only, consumed by engine-server:**
- `PlaceBet` — Deduct stake from wallet atomically
- `SaveBet` — Persist the bet to the database
- `SaveRoundResult` — Persist the winning color for a round
- `UpdateRoundStatus` — Mark a round as CLOSED or COMPLETED
- `UpdateBetResult` — Mark winning/losing bets
- `CreditAmount` — Add winnings to a user's wallet
- `CreateRound` — Create a new game round record

---

### 2. `engine-server` — Game Engine (`:8083` gRPC)

The heart of the game. Runs an autonomous game loop and manages all round logic in-memory for performance.

**Game Loop (60-second cycle):**

```
T=0s   → New round starts. ROUND_START event published to Redis.
T=0-54s → Betting window open. Players can place bets via WebSocket.
T=55s  → Betting closes. BETTING_CLOSED event published. DB updated.
T=60s  → Round ends. Result calculated. RESULT event published to Redis.
         → 20-goroutine worker pool settles all winning bets concurrently.
         → Round result stored in Redis (recent_rounds list, max 10 entries).
T=60s+ → Next round starts immediately.
```

**Bet Settlement (Worker Pool):**

On round end, a pool of 20 goroutines concurrently processes all bets. Each winning bet receives `2x` the stake amount via a gRPC `CreditAmount` call to `backend-go`.

**State Recovery:**

On server restart, the engine queries `backend-go` via gRPC to find the last completed round number so the game resumes from the correct sequence rather than starting from round 1 again.

---

### 3. `ws-server-go` — WebSocket Server (`:8090`)

Manages persistent WebSocket connections from all connected clients. Serves as the real-time broadcast channel for game state updates.

**Connection Flow:**
1. Client connects to `wss://.../ws?token=<jwt>`
2. Server validates the JWT from the query parameter
3. On success, the connection is registered in the in-memory Hub
4. Client receives live game updates (TIMER ticks, BETTING_CLOSED, RESULT)

**Receiving Client Messages:**

| Message Type | Action |
|---|---|
| `PLACE_BET` | Forwards bet request via gRPC to `engine-server` |

**Redis Subscriber:**

A background goroutine subscribes to the `game_updates` Redis Pub/Sub channel. Every event published by the engine is received here and immediately broadcast to all connected WebSocket clients via the Hub.

---

## API Gateway & Security (Nginx)

Nginx is the **only entry point** exposed to the public. All services behind it are completely isolated.

### Rate Limiting
```nginx
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;
limit_req zone=api_limit burst=20;
```
- Each IP is allowed **10 requests per second**
- Burst up to **20 requests** before throttling kicks in
- Applied only to REST API routes (`/api/*`)

### Internal Secret Header
Nginx injects an `X-Internal-Secret: dpkv123123` header on every proxied request. Both `backend-go` and `ws-server-go` verify this header before processing any incoming request.

```nginx
proxy_set_header X-Internal-Secret dpkv123123;
```

This ensures **no service can be called directly** by bypassing Nginx — any request without the secret header is rejected with `403 Forbidden`.

### Authorization Header Forwarding
```nginx
proxy_set_header Authorization $http_authorization;
```
The client's JWT `Authorization` header is transparently forwarded to `backend-go` for authentication middleware.

---

## gRPC Communication

Services communicate internally over gRPC for high-performance, strongly-typed calls with zero serialization overhead compared to HTTP/JSON.

```
ws-server-go  ──gRPC(:8083)──►  engine-server  (PlaceBet)
engine-server ──gRPC(:8082)──►  backend-go     (PlaceBet, SaveBet, CreditAmount, etc.)
```

Connection addresses are configured via environment variables (`BACKEND_GO_GRPC_URL`, `ENGINE_SERVER_GRPC_URL`) so they work across both local Docker and any cloud environment.

---

## Data Flow: Placing a Bet (End-to-End)

```
1. Client sends  →  WebSocket message { type: "PLACE_BET", amount: 100, color: "red" }
2. ws-server-go  →  Validates JWT from connection context
3. ws-server-go  →  gRPC call to engine-server: RequestPlaceBet(userID, 100, "red")
4. engine-server →  Checks round is active and betting window is open (< 55s)
5. engine-server →  gRPC call to backend-go: PlaceBet(userID, 100, round)
6. backend-go    →  Deducts 100 from user wallet (atomic DB transaction)
7. engine-server →  gRPC call to backend-go: SaveBet(userID, round, 100, "red")
8. backend-go    →  Persists bet record to PostgreSQL
9. engine-server →  Stores bet in in-memory BetStore for fast settlement later
10. ws-server-go ←  Success response returned back to client WebSocket
```

---

## Data Flow: Round Result & Settlement (End-to-End)

```
1.  engine-server →  Round timer hits 60s. Calls CalculateResult()
2.  engine-server →  gRPC: SaveRoundResult(round, winColor, "COMPLETED")
3.  engine-server →  Publishes { type: "RESULT", round_id, result } to Redis channel
4.  engine-server →  Publishes bet result update to backend-go via gRPC
5.  Redis         →  ws-server-go subscriber receives the event
6.  ws-server-go  →  Broadcasts RESULT message to ALL connected WebSocket clients
7.  engine-server →  Spawns 20-goroutine worker pool with all bets from in-memory store
8.  Workers       →  For each winning bet: gRPC CreditAmount(userID, amount*2)
9.  backend-go    →  Credits winning amount to user wallet in PostgreSQL
10. engine-server →  Stores result in Redis (recent_rounds list, capped at 10)
11. engine-server →  Increments roundID → immediately starts next round
```

---

## Infrastructure

| Component | Technology | Port |
|---|---|---|
| API Gateway | Nginx | 8085 (public) |
| REST API | Go + Gin | 8080 (internal) |
| REST→gRPC Bridge | Go + gRPC | 8082 (internal) |
| Game Engine | Go | 8083 (internal) |
| WebSocket Server | Go + Gorilla | 8090 (internal) |
| Database | PostgreSQL | 5432 (internal) |
| Message Broker | Redis (Pub/Sub) | 6379 (internal) |

---

## Running Locally with Docker

```bash
cd backend
docker compose up --build -d
```

The entire stack will be available on `http://localhost:8085`.

- REST API: `http://localhost:8085/api/v1/...`
- WebSocket: `ws://localhost:8085/ws?token=<jwt>`

### Environment Variables

| Variable | Service | Description |
|---|---|---|
| `DATABASE_URL` | backend-go | PostgreSQL connection string |
| `REDIS_URL` | engine-server, ws-server-go | Redis connection address |
| `BACKEND_GO_GRPC_URL` | engine-server, ws-server-go | backend-go gRPC address |
| `ENGINE_SERVER_GRPC_URL` | ws-server-go | engine-server gRPC address |
| `PORT` | all | HTTP port for health checks |
