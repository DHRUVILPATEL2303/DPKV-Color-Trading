#!/bin/bash

<<<<<<< HEAD
=======
# Exit on any error
>>>>>>> master
set -e

echo "----------------------------------------"
echo "Starting GCP VM Setup for Color Trading"
echo "----------------------------------------"

<<<<<<< HEAD
echo "[1/5] Updating package lists..."
sudo apt-get update

# ✅ FIXED swap
=======
# Update system (Lightweight)
echo "[1/5] Updating package lists..."
sudo apt-get update
# We skip 'upgrade -y' here because it's too heavy for e2-small (e.g. Google Cloud SDK).

# Add swap file (Essential for 2GB RAM instances)
>>>>>>> master
echo "[2/5] Creating 2GB swap file..."
if [ ! -f /swapfile ]; then
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
<<<<<<< HEAD
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
=======
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/apt/fstab
>>>>>>> master
else
    echo "Swap file already exists."
fi

<<<<<<< HEAD
=======
# Install dependencies
>>>>>>> master
echo "[3/5] Installing system dependencies..."
sudo apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    git \
    htop \
    ufw

<<<<<<< HEAD
=======
# Install Docker
>>>>>>> master
echo "[4/5] Installing Docker..."
if ! command -v docker &> /dev/null; then
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    rm get-docker.sh
else
<<<<<<< HEAD
    echo "Docker already installed."
fi

# ✅ START DOCKER
sudo systemctl start docker
sudo systemctl enable docker

echo "[5/5] Configuring Docker permissions..."
sudo usermod -aG docker $USER

# ✅ FIREWALL CONFIG (IMPORTANT)
echo "[+] Configuring firewall..."
sudo ufw allow 22
sudo ufw allow 8085
sudo ufw allow 80
sudo ufw --force enable

echo "----------------------------------------"
echo "Setup Complete!"
echo "Please logout & login again."
=======
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
>>>>>>> master
echo "----------------------------------------"
