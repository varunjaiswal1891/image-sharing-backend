#!/bin/bash

AWS_REGION=ap-south-1
AWS_ACCOUNT_ID=123456789012
IMAGE_NAME=image-app

aws ecr get-login-password --region $AWS_REGION \
| docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

docker pull $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$IMAGE_NAME:latest

docker run -d \
  --name image-app \
  -p 8080:8080 \
  --restart always \
  $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$IMAGE_NAME:latest
echo "Container 'image-app' has been started."