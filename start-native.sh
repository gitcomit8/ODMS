#!/bin/bash
# Start GraalVM Native Image container

set -e

echo "🚀 Starting OD Management System (Native GraalVM)..."
echo "   Expected startup: 2-5 seconds"
echo ""

# Check if image exists
if ! docker images | grep -q "odms-app.*native"; then
    echo "❌ Native image not found. Build it first with:"
    echo "   ./build-native.sh"
    exit 1
fi

# Stop any existing container
if docker ps -a | grep -q odms-app-native; then
    echo "🛑 Stopping existing container..."
    docker rm -f odms-app-native > /dev/null
fi

# Start the container
echo "▶️  Starting native container..."
docker run -d \
    --name odms-app-native \
    -p 80:80 \
    -e SPRING_PROFILES_ACTIVE=native \
    --restart unless-stopped \
    odms-app:native

echo ""
echo "⏳ Waiting for ultra-fast startup..."

START_TIME=$(date +%s)
for i in {1..15}; do
    if curl -s http://localhost:80/actuator/health/readiness 2>/dev/null | grep -q "UP"; then
        END_TIME=$(date +%s)
        STARTUP_TIME=$((END_TIME - START_TIME))
        echo ""
        echo "✅ Application ready in ${STARTUP_TIME} seconds!"
        echo ""
        echo "🌐 Access: http://localhost"
        echo "📊 Health: http://localhost/actuator/health"
        echo ""
        echo "📝 View logs:  docker logs -f odms-app-native"
        echo "🛑 Stop:       docker stop odms-app-native"
        echo "💾 Memory:     docker stats odms-app-native"
        exit 0
    fi
    echo -n "."
    sleep 1
done

echo ""
echo "⚠️  Taking longer than expected. Check logs:"
echo "   docker logs odms-app-native"
