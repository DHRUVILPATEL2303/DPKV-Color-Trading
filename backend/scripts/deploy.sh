#!/bin/bash

# Exit on any error
set -e

echo "----------------------------------------"
echo "Deploying Color Trading Backend"
echo "----------------------------------------"

# Ensure we are in the project root
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR/.."

# Check for .env file
if [ ! -f .env ]; then
    echo "Warning: .env file not found. Creating from .env.example..."
    if [ -f .env.example ]; then
        cp .env.example .env
        echo "Please edit .env with your production credentials."
    else
        echo "Error: .env.example not found. Please create a .env file."
        exit 1
    fi
fi

# Pull/Build and start containers
echo "Starting services..."
docker compose up -d --build

# Cleanup old images to save disk space
echo "Cleaning up dangling images..."
docker image prune -f

echo "----------------------------------------"
echo "Deployment Successful!"
echo "Services are running in the background."
echo "Check status with: docker compose ps"
echo "----------------------------------------"
