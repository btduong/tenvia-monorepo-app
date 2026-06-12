#!/bin/bash
set -e

# Redirect output to a log file for debugging
exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1

echo "Starting Tenvia user data script..."

# Update and install dependencies
apt-get update
apt-get install -y ca-certificates curl gnupg git

# Install Docker
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null

apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Ensure docker service is running
systemctl enable docker
systemctl start docker

# Clone the public repository
echo "Cloning the repository..."
git clone https://github.com/btduong/tenvia-monorepo-app.git /opt/tenvia-monorepo-app
cd /opt/tenvia-monorepo-app

# Build and start the containers
echo "Building and starting Docker containers..."
docker compose up -d --build

# Install Caddy for automatic HTTPS
echo "Installing Caddy..."
apt-get install -y debian-keyring debian-archive-keyring apt-transport-https
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | tee /etc/apt/sources.list.d/caddy-stable.list
apt-get update
apt-get install -y caddy

# Configure Caddy
echo "Configuring Caddyfile..."
cat << 'EOF' > /etc/caddy/Caddyfile
api.${domain_name} {
    reverse_proxy localhost:8080
}

leaderboard.${domain_name} {
    reverse_proxy localhost:8081
}
EOF

systemctl reload caddy

echo "Tenvia user data script completed successfully."
