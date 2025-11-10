# Docker Deployment Guide

This guide explains how to containerize and deploy the OD Management System with optimized startup times.

## Quick Start

### Build and Run with Docker Compose (Recommended)
```bash
docker-compose up -d
```

The application will be available at `http://localhost`

### Build and Run with Docker
```bash
# Build the image
docker build -t odms-app .

# Run the container
docker run -d -p 80:80 --name odms-app odms-app
```

## Startup Time Optimizations

The following optimizations ensure fast container startup (typically 5-15 seconds):

### 1. **Layered Docker Build**
- Multi-stage build separates build and runtime
- Dependencies extracted into layers for faster JVM startup
- Using Alpine Linux (JRE only) for minimal image size (~200MB vs 500MB+)

### 2. **JVM Tuning**
The container uses optimized JVM flags:
- `-XX:TieredStopAtLevel=1`: Faster compilation, less optimization
- `-XX:+UseSerialGC`: Lightweight garbage collector
- `-Xss256k`: Reduced thread stack size
- `-Dspring.jmx.enabled=false`: Disable JMX overhead
- `-Dspring.backgroundpreinitializer.ignore=true`: Skip background initialization

### 3. **Spring Boot Optimizations**
- Disabled JMX
- Disabled background pre-initializer
- Minimal logging configuration
- H2 in-memory database (fastest startup)
- Lazy initialization where possible

### 4. **Efficient Resource Allocation**
- Memory limits: 512MB max, 256MB reserved
- Optimized for quick cold starts after idle periods

## Configuration

### Environment Variables

Key environment variables you can override:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=

# Email
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# Application
APP_SECURITY_DEV_LOGIN_ENABLED=false
OD_REQUEST_URGENT_REGNO=URGENT-999
```

### Using PostgreSQL (Production)

Uncomment the `postgres` service in `docker-compose.yml` and update environment variables:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/odms_db
  SPRING_DATASOURCE_DRIVER_CLASS_NAME: org.postgresql.Driver
  SPRING_DATASOURCE_USERNAME: odms_user
  SPRING_DATASOURCE_PASSWORD: change_this_password
  SPRING_JPA_DATABASE_PLATFORM: org.hibernate.dialect.PostgreSQLDialect
```

## Monitoring

### Health Check
```bash
curl http://localhost/actuator/health
```

### View Logs
```bash
docker logs -f odms-app
```

### Container Stats
```bash
docker stats odms-app
```

## Deployment Strategies

### For Auto-Scaling Platforms (Render, Railway, etc.)

1. **Build the image:**
   ```bash
   docker build -t your-registry/odms-app:latest .
   docker push your-registry/odms-app:latest
   ```

2. **Set these environment variables** on your platform:
   - `SPRING_PROFILES_ACTIVE=docker`
   - Database credentials
   - Email credentials
   - `PORT` (if platform uses dynamic ports)

3. **Configure auto-scaling** with:
   - Minimum instances: 0 (scales to zero when idle)
   - Maximum instances: 3
   - Health check: `/actuator/health`
   - Startup timeout: 60 seconds

### For Kubernetes

Create a deployment with:
- `livenessProbe` on `/actuator/health`
- `readinessProbe` on `/actuator/health`
- Resource requests/limits as defined in docker-compose.yml
- Horizontal Pod Autoscaler (HPA) for auto-scaling

## Performance Benchmarks

Expected startup times:
- **Cold start (container not cached)**: 8-15 seconds
- **Warm start (container cached)**: 5-10 seconds
- **Hot start (JVM warmed up)**: 3-8 seconds

Memory usage:
- **Initial**: ~200MB
- **Steady state**: ~300-400MB
- **Maximum**: 512MB (configurable)

## Troubleshooting

### Slow Startup
1. Check available memory: `docker stats`
2. Review logs: `docker logs odms-app`
3. Verify database connectivity
4. Increase memory limits if needed

### Connection Refused
1. Wait for full startup (check health endpoint)
2. Verify port mapping: `docker ps`
3. Check firewall rules

### Out of Memory
1. Increase memory limits in docker-compose.yml
2. Review JVM heap settings in Dockerfile
3. Check for memory leaks in application logs

## Building for Production

1. **Update credentials** in docker-compose.yml or use secrets
2. **Set production profile**: `SPRING_PROFILES_ACTIVE=docker`
3. **Enable HTTPS** with reverse proxy (nginx, traefik)
4. **Use PostgreSQL** instead of H2 for data persistence
5. **Implement backup strategy** for database
6. **Monitor with APM tools** (New Relic, Datadog, etc.)

## Cleanup

```bash
# Stop and remove containers
docker-compose down

# Remove images
docker rmi odms-app

# Remove volumes (WARNING: deletes data)
docker-compose down -v
```
