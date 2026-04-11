#!/bin/bash

set -e

echo "----------------------------------------"
echo "Deploying Color Trading Backend"
echo "----------------------------------------"

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR/.."

# ✅ Check Docker
if ! command -v docker &> /dev/null; then
    echo "Docker not installed!"
    exit 1
fi

# ✅ Check Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "docker-compose not installed!"
    exit 1
fi

# ✅ ENV CHECK
if [ ! -f .env ]; then
    echo "Warning: .env not found"
    
    if [ -f .env.example ]; then
        cp .env.example .env
        echo "Created .env from example. Please edit it!"
    else
        echo "Error: No .env or .env.example found"
        exit 1
    fi
fi

# 🔥 Stop old containers
echo "Stopping old containers..."
docker-compose down

# 🚀 Start new containers
echo "Starting services..."
docker-compose up -d --build

# 🧹 Cleanup
echo "Cleaning old images..."
docker image prune -f

# 📊 Show status
echo "Running containers:"
docker ps

echo "----------------------------------------"
echo "Deployment Successful!"
echo "----------------------------------------"
