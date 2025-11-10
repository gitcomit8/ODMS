#!/bin/bash
# Stop and clean up ODMS Docker containers

echo "🛑 Stopping OD Management System..."

if docker ps -a | grep -q odms-app; then
    docker stop odms-app
    docker rm odms-app
    echo "✅ Container stopped and removed"
else
    echo "ℹ️  No running container found"
fi

# Optional: remove images (uncomment to enable)
# echo "🗑️  Removing Docker image..."
# docker rmi odms-app:latest 2>/dev/null || echo "No image to remove"

echo "✅ Cleanup complete"
