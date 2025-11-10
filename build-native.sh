#!/bin/bash
# Build GraalVM Native Image for ultra-fast startup

set -e

echo "╔════════════════════════════════════════════════════════════╗"
echo "║       Building GraalVM Native Image Docker Container       ║"
echo "║         Expected startup time: 2-5 seconds                 ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

echo "📦 Building native image (this will take 5-10 minutes)..."
echo "   This is a one-time build. Runtime startup will be 2-5 seconds."
echo ""

START_TIME=$(date +%s)

# Build the native image
docker build -f Dockerfile.native -t odms-app:native . || {
    echo ""
    echo "❌ Build failed. Common issues:"
    echo "   1. Not enough memory (needs ~8GB for native build)"
    echo "   2. Docker resource limits too low"
    echo "   3. Network issues downloading GraalVM"
    exit 1
}

END_TIME=$(date +%s)
BUILD_DURATION=$((END_TIME - START_TIME))
BUILD_MINUTES=$((BUILD_DURATION / 60))
BUILD_SECONDS=$((BUILD_DURATION % 60))

echo ""
echo "✅ Native image built successfully!"
echo "   Build time: ${BUILD_MINUTES}m ${BUILD_SECONDS}s"
echo ""

# Get image size
IMAGE_SIZE=$(docker images odms-app:native --format "{{.Size}}")
echo "📊 Image size: ${IMAGE_SIZE}"
echo ""

echo "🚀 Testing native image startup time..."
docker run -d --rm -p 8091:80 -e SPRING_PROFILES_ACTIVE=native --name odms-native-test odms-app:native > /dev/null

STARTUP_START=$(date +%s)
for i in {1..30}; do
    if docker logs odms-native-test 2>&1 | grep -q "Started OdManagementSystemApplication"; then
        STARTUP_END=$(date +%s)
        STARTUP_TIME=$((STARTUP_END - STARTUP_START))
        echo "✅ Native app started in ${STARTUP_TIME} seconds!"
        
        # Test health endpoint
        sleep 1
        if curl -s http://localhost:8091/actuator/health/readiness | grep -q "UP"; then
            echo "✅ Health check passed!"
        fi
        
        docker rm -f odms-native-test > /dev/null
        echo ""
        echo "╔════════════════════════════════════════════════════════════╗"
        echo "║                  BUILD COMPLETE!                           ║"
        echo "╠════════════════════════════════════════════════════════════╣"
        echo "║  Startup time: ${STARTUP_TIME} seconds (vs 16-25 sec JVM)              ║"
        echo "║  Image size:   ${IMAGE_SIZE}                                    ║"
        echo "║                                                            ║"
        echo "║  To run: ./start-native.sh                                 ║"
        echo "║  or: docker run -p 80:80 odms-app:native                   ║"
        echo "╚════════════════════════════════════════════════════════════╝"
        exit 0
    fi
    sleep 1
done

echo "⚠️  Startup test timed out, but image built successfully"
docker rm -f odms-native-test > /dev/null 2>&1
echo "Try running manually: docker run -p 80:80 odms-app:native"
