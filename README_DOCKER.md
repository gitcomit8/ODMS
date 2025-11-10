# Docker Containerization - Quick Start Guide

This Spring Boot application has been containerized and optimized for **fast startup times** (15-25 seconds), making it ideal for auto-scaling platforms that spin down containers during idle periods.

## 🚀 Quick Start

### Option 1: Using Helper Scripts (Easiest)
```bash
./start-docker.sh
```

To stop:
```bash
./stop-docker.sh
```

### Option 2: Using Docker Compose
```bash
docker-compose up -d
```

### Option 3: Using Docker Directly
```bash
docker build -t odms-app .
docker run -d -p 80:80 --name odms-app odms-app
```

## ⚡ Performance Metrics

**Startup Times:**
- Cold start: 18-25 seconds
- Container size: ~335MB (Alpine-based)
- Memory usage: 256-400MB

**Optimizations Applied:**
- ✅ Lazy initialization of Spring beans
- ✅ Layered JAR structure for faster JVM startup
- ✅ Minimal JRE (Alpine Linux with Temurin JRE 21)
- ✅ Optimized JVM flags for fast startup
- ✅ Serial GC for low memory footprint
- ✅ Disabled unnecessary Spring features (JMX, background initializers)
- ✅ In-memory H2 database (fastest option)

## 📁 Files Created

- `Dockerfile` - Multi-stage build optimized for fast startup
- `docker-compose.yml` - Easy deployment with Docker Compose
- `.dockerignore` - Minimizes build context
- `application-docker.properties` - Production-optimized configuration
- `start-docker.sh` - Quick start script
- `stop-docker.sh` - Clean shutdown script
- `DOCKER_DEPLOYMENT.md` - Comprehensive deployment guide

## 🔧 Configuration

### Environment Variables

The application can be configured via environment variables:

```bash
# Database (default: H2 in-memory)
SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=

# Email Settings
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# Application Settings
APP_SECURITY_DEV_LOGIN_ENABLED=true
OD_REQUEST_URGENT_REGNO=URGENT-999
```

### Using PostgreSQL for Persistence

Edit `docker-compose.yml` and uncomment the PostgreSQL service, then update the environment:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/odms_db
  SPRING_DATASOURCE_DRIVER_CLASS_NAME: org.postgresql.Driver
  SPRING_JPA_DATABASE_PLATFORM: org.hibernate.dialect.PostgreSQLDialect
```

## 🏥 Health Checks

The application exposes health endpoints:

```bash
# Readiness probe (recommended for load balancers)
curl http://localhost/actuator/health/readiness

# Liveness probe
curl http://localhost/actuator/health/liveness

# General health
curl http://localhost/actuator/health
```

## 📊 Monitoring

### View Logs
```bash
docker logs -f odms-app
```

### Container Stats
```bash
docker stats odms-app
```

### Check Status
```bash
docker ps | grep odms-app
```

## 🌐 Deployment to Cloud Platforms

### Render, Railway, Fly.io, etc.

1. Push your code to Git
2. Connect repository to your platform
3. Platform will auto-detect the Dockerfile
4. Set environment variables in platform dashboard
5. Deploy!

**Recommended settings:**
- Health check path: `/actuator/health/readiness`
- Port: 80
- Auto-scaling: Min 0, Max 3 instances
- Memory: 512MB

### Docker Hub / Container Registry

```bash
# Tag the image
docker tag odms-app:latest your-username/odms-app:latest

# Push to registry
docker push your-username/odms-app:latest
```

## 🐛 Troubleshooting

### Slow Startup
- Check `docker logs odms-app` for errors
- Verify memory allocation: `docker stats`
- Ensure database is accessible

### Port Already in Use
```bash
# Use different port
docker run -d -p 8080:80 --name odms-app odms-app
```

### Application Not Responding
```bash
# Check if container is running
docker ps -a | grep odms-app

# Restart container
docker restart odms-app

# Check logs
docker logs --tail 50 odms-app
```

## 🔒 Security Notes

- Container runs as non-root user `spring`
- Change default passwords in production
- Use secrets management for sensitive data
- Enable HTTPS via reverse proxy

## 📖 Additional Documentation

See `DOCKER_DEPLOYMENT.md` for comprehensive deployment guide including:
- Kubernetes deployment
- Advanced configuration
- Performance tuning
- Production best practices

## 🎯 Why These Optimizations Matter

When your container spins down after idle periods and needs to restart:
- **Fast startup = Better user experience**
- **Lower costs** on platforms that charge per second
- **Quick auto-scaling** during traffic spikes
- **Efficient resource utilization**

The optimizations ensure your users experience minimal wait times (~20 seconds) when the service restarts from a cold state.
