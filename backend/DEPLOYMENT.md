# Deployment Guide: GCP VM (e2-small)

This guide provides step-by-step instructions to deploy the Color Trading backend to a GCP e2-small instance.

## 1. Prerequisites (GCP Console)

Before starting, ensure you have a VM instance created and firewall rules configured:

### Create VM Instance
- **Machine type**: `e2-small` (2 vCPU, 2 GB memory)
- **OS**: Ubuntu 22.04 LTS (recommended)
- **Allow HTTP traffic**: Check the box for "Allow HTTP traffic" (this opens port 80).

### Firewall Rule
If not already done, ensure port 80 is open:
1. Go to **VPC Network** > **Firewall**.
2. Create/Allow a rule for `tcp:80` for your instance.

---

## 2. On the VM: Initial Setup

Once you SSH into your VM, run the following commands to initialize the environment:

```bash
# Clone the repository (replace with your repo URL if different)
git clone <YOUR_REPO_URL>
cd backend

# Run the setup script (installs Docker, Compose, and tools)
chmod +x scripts/vm-setup.sh
./scripts/vm-setup.sh

# RE-LOGIN to apply Docker permissions
exit
```
*After `exit`, SSH back into the VM.*

---

## 3. Configuration

Set up your environment variables:

```bash
# Copy the example .env
cp .env.example .env

# Edit the .env file with your production values
# (Passwords, internal secrets, etc.)
nano .env
```

---

## 4. Deploy

Run the deployment script to build and start the services:

```bash
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

---

## 5. Verification & Monitoring

Check if the containers are running:
```bash
docker compose ps
```

View logs for a specific service:
```bash
docker compose logs -f backend-go
```

The API should now be accessible at: `http://<YOUR_VM_IP>/api`

---

## Troubleshooting

- **Memory Management**: This setup automatically creates a **2GB swap file** on the VM and sets **memory limits** for each service in `docker-compose.yml`. This ensures stability on the `e2-small` instance.
- **Firewall**: Ensure port 80 is open in GCP Console if you cannot access the IP.
- **Database**: The setup uses a local Postgres container. All services are connected via the internal Docker network.
