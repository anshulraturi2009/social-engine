#!/bin/bash

# Render Deployment Script
# Usage: ./deploy.sh

RENDER_API_KEY="rnd_eap3gzWcjfXzbfnXr2dxJ89gceJJ"
GITHUB_REPO="anshulraturi2009/social-engine"
SERVICE_NAME="social-engine"

echo "🚀 Starting Render Deployment..."

# Create Web Service
echo "📝 Creating Web Service..."

curl -X POST https://api.render.com/v1/services \
  -H "Authorization: Bearer $RENDER_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "web_service",
    "name": "'$SERVICE_NAME'",
    "ownerId": "tea_",
    "repoUrl": "https://github.com/'$GITHUB_REPO'",
    "branch": "main",
    "buildCommand": "mvn clean package -DskipTests",
    "startCommand": "java -jar target/social-engine-0.0.1-SNAPSHOT.jar",
    "envVars": [
      {
        "key": "PORT",
        "value": "10000"
      },
      {
        "key": "SPRING_JPA_HIBERNATE_DDL_AUTO",
        "value": "update"
      },
      {
        "key": "SPRING_JPA_SHOW_SQL",
        "value": "false"
      },
      {
        "key": "SPRING_JPA_HIBERNATE_DIALECT",
        "value": "org.hibernate.dialect.PostgreSQLDialect"
      }
    ],
    "plan": "free",
    "region": "oregon"
  }'

echo -e "\n✅ Deployment script executed!"
echo "📌 Check your Render dashboard for deployment status"
echo "🔗 Dashboard: https://dashboard.render.com"
