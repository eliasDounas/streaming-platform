# Docker Setup for Streaming Platform Frontend

This document explains how to run the streaming platform frontend using Docker.

## Prerequisites

- Docker installed on your system
- Docker Compose (usually included with Docker)

## Quick Start

### Production Build

1. **Build and run the production container:**
   ```bash
   docker-compose up --build streaming-frontend
   ```

2. **Access the application:**
   - Open your browser and navigate to `http://localhost:3000`

### Development Build (with Hot Reload)

1. **Run the development container:**
   ```bash
   docker-compose --profile dev up --build streaming-frontend-dev
   ```

2. **Access the development server:**
   - Open your browser and navigate to `http://localhost:3001`

## Configuration

### Environment Variables

Copy the example environment file and customize it:

```bash
cp .env.example .env.local
```

Edit `.env.local` with your API endpoints:

```env
NEXT_PUBLIC_CHANNEL_API_URL=http://your-channel-api:8080/channel-service
NEXT_PUBLIC_STREAM_API_URL=http://your-stream-api:8081/stream-service
```

### Docker Commands

#### Build production image only:
```bash
docker build -t streaming-frontend .
```

#### Run production container:
```bash
docker run -p 3000:3000 \
  -e NEXT_PUBLIC_CHANNEL_API_URL=http://localhost:8080/channel-service \
  -e NEXT_PUBLIC_STREAM_API_URL=http://localhost:8081/stream-service \
  streaming-frontend
```

#### Build development image:
```bash
docker build -f Dockerfile.dev -t streaming-frontend-dev .
```

#### Run development container with volume mounting:
```bash
docker run -p 3000:3000 \
  -v $(pwd):/app \
  -v /app/node_modules \
  -v /app/.next \
  streaming-frontend-dev
```

## Docker Images

### Production Image (`Dockerfile`)
- **Multi-stage build** for optimal size
- **Alpine Linux** base for security and small footprint
- **Non-root user** for security
- **Standalone output** for faster cold starts
- **~50MB final image size**

### Development Image (`Dockerfile.dev`)
- **Hot reload** support
- **Volume mounting** for live code changes
- **All dev dependencies** included
- **Turbopack** enabled for faster builds

## Networking

The docker-compose file creates a custom network `streaming-network` that allows:
- Frontend to communicate with backend services
- Easy service discovery between containers
- Isolated network environment

## Troubleshooting

### Common Issues

1. **Port already in use:**
   ```bash
   # Change the port mapping in docker-compose.yml
   ports:
     - "3001:3000"  # Use 3001 instead of 3000
   ```

2. **API connection issues:**
   - Ensure your API endpoints are accessible from the Docker container
   - Use `host.docker.internal` on Windows/Mac to access localhost from container
   
3. **File permission issues (Linux):**
   ```bash
   sudo chown -R $USER:$USER .
   ```

### Logs

View container logs:
```bash
# Production
docker-compose logs streaming-frontend

# Development
docker-compose logs streaming-frontend-dev
```

### Health Check

Check if the container is running:
```bash
docker-compose ps
```

## Production Deployment

For production deployment, consider:

1. **Environment-specific configuration:**
   - Set proper API URLs
   - Configure SSL/TLS
   - Set up reverse proxy (nginx)

2. **Resource limits:**
   ```yaml
   deploy:
     resources:
       limits:
         memory: 512M
         cpus: '0.5'
   ```

3. **Health checks:**
   ```yaml
   healthcheck:
     test: ["CMD", "curl", "-f", "http://localhost:3000/api/health"]
     interval: 30s
     timeout: 10s
     retries: 3
   ```

4. **Use a container registry:**
   ```bash
   docker tag streaming-frontend your-registry/streaming-frontend:latest
   docker push your-registry/streaming-frontend:latest
   ```
