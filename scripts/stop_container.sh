#!/bin/bash
set +e
docker stop image-app || true
docker rm image-app || true
echo "Container 'image-app' has been stopped and removed."