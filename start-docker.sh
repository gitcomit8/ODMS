#!/bin/bash
# Quick start script for ODMS Docker deployment

set -e

echo "🚀 Starting OD Management System with Docker..."
echo ""

# Build the image
echo "📦 Building Docker image..."
docker build -t odms-app:latest -q .

# Stop any existing container
if docker ps -a | grep -q odms-app; then
    echo "🛑 Stopping existing container..."
    docker rm -f odms-app
fi

# Start the container
echo "▶️  Starting container..."
docker run -d \
    --name odms-app \
    -p 80:80 \
    -e SPRING_PROFILES_ACTIVE=docker \
    --restart unless-stopped \
    odms-app:latest

echo ""
echo "⏳ Waiting for application to start (typically 15-25 seconds)..."

# Wait for startup
for i in {1..60}; do
    if curl -s http://localhost:80/actuator/health/readiness | grep -q "UP"; then
        echo ""
        echo "✅ Application is ready!"
        echo "🌐 Access the application at: http://localhost"
        echo "📊 Health check: http://localhost/actuator/health"
        echo ""
        echo "📝 View logs: docker logs -f odms-app"
        echo "🛑 Stop app: docker stop odms-app"
        exit 0
    fi
    echo -n "."
    sleep 1
done

echo ""
echo "⚠️  Application is taking longer than expected to start."
echo "Check logs with: docker logs odms-app"
exit 1
