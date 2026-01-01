#!/bin/bash
set +e
docker stop image-share-backend || true
docker rm image-share-backend || true
echo "Container 'image-share-backend' has been stopped and removed."