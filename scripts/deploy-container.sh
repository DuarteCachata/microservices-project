#!/bin/bash

IMAGE_NAME="duartecachata/product-service:1.0"
CONTAINER_NAME="product-service"
HOST_PORT=8080
CONTAINER_PORT=8082

echo "Deploying container..."

# Stop and remove existing container
docker stop $CONTAINER_NAME 2>/dev/null
docker rm $CONTAINER_NAME 2>/dev/null

# Pull image
docker pull $IMAGE_NAME

# Run container
docker run -d \
  --name $CONTAINER_NAME \
  -p $HOST_PORT:$CONTAINER_PORT \
  --restart unless-stopped \
  $IMAGE_NAME

# Wait a few seconds
sleep 5

# Check status
if docker ps | grep -q $CONTAINER_NAME; then
  echo "Container deployed successfully"
  docker ps
  docker logs --tail 20 $CONTAINER_NAME
else
  echo "Container failed to start"
  docker logs $CONTAINER_NAME
fi