#!/bin/bash

# Exit on any error
set -e

echo "----------------------------------------"
echo "Starting GCP VM Setup for Color Trading"
echo "----------------------------------------"

# Update system
echo "[1/5] Updating system packages..."
sudo apt-get update && sudo apt-get upgrade -y

# Add swap file (Essential for 2GB RAM instances)
echo "[2/5] Creating 2GB swap file..."
if [ ! -f /swapfile ]; then
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/apt/fstab
else
    echo "Swap file already exists."
fi

# Install dependencies
echo "[3/5] Installing system dependencies..."
sudo apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    git \
    htop \
    ufw

# Install Docker
echo "[4/5] Installing Docker..."
if ! command -v docker &> /dev/null; then
    sudo mkdir -p /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    
    echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
      $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    
    sudo apt-get update
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
else
    echo "Docker is already installed."
fi

# Set up Docker permissions
echo "[5/5] Configuring Docker permissions..."
if ! getent group docker > /dev/null; then
    sudo groupadd docker
fi
sudo usermod -aG docker $USER

# Finalize
echo "----------------------------------------"
echo "Setup Complete!"
echo "Please log out and log back in for Docker permissions to take effect."
echo "----------------------------------------"
