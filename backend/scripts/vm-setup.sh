#!/bin/bash

# Exit on any error
set -e

echo "----------------------------------------"
echo "Starting GCP VM Setup for Color Trading"
echo "----------------------------------------"

# Update system (Lightweight)
echo "[1/5] Updating package lists..."
sudo apt-get update

# Add swap file (Essential for low RAM instances)
echo "[2/5] Creating 2GB swap file..."
if [ ! -f /swapfile ]; then
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
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
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    rm get-docker.sh
else
    echo "Docker is already installed."
fi

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker

# Configure Docker permissions
echo "[5/5] Configuring Docker permissions..."
if ! getent group docker > /dev/null; then
    sudo groupadd docker
fi
sudo usermod -aG docker $USER

# Firewall config (important)
echo "[+] Configuring firewall..."
sudo ufw allow 22
sudo ufw allow 80
sudo ufw allow 8085
sudo ufw --force enable

echo "----------------------------------------"
echo "Setup Complete!"
echo "Please log out and log back in."
echo "----------------------------------------"
