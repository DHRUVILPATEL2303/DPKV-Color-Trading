#!/bin/bash

set -e

echo "----------------------------------------"
echo "Deploying Color Trading Backend"
echo "----------------------------------------"

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR/.."

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

# ✅ USE CORRECT COMMAND
echo "Starting services..."
docker-compose up -d --build

# ✅ CLEANUP
echo "Cleaning old images..."
docker image prune -f

echo "----------------------------------------"
echo "Deployment Successful!"
echo "Check status: docker ps"
echo "----------------------------------------"
