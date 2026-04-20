# Render Deployment Script (PowerShell)
# Usage: .\deploy.ps1

$RENDER_API_KEY = "rnd_eap3gzWcjfXzbfnXr2dxJ89gceJJ"
$GITHUB_REPO = "anshulraturi2009/social-engine"
$SERVICE_NAME = "social-engine"

Write-Host "Starting Render Deployment..." -ForegroundColor Green

# Create Web Service
Write-Host "Creating Web Service..." -ForegroundColor Yellow

$body = @{
    type = "web_service"
    name = $SERVICE_NAME
    repoUrl = "https://github.com/$GITHUB_REPO"
    branch = "main"
    buildCommand = "mvn clean package -DskipTests"
    startCommand = "java -jar target/social-engine-0.0.1-SNAPSHOT.jar"
    envVars = @(
        @{ key = "PORT"; value = "10000" },
        @{ key = "SPRING_JPA_HIBERNATE_DDL_AUTO"; value = "update" },
        @{ key = "SPRING_JPA_SHOW_SQL"; value = "false" },
        @{ key = "SPRING_JPA_HIBERNATE_DIALECT"; value = "org.hibernate.dialect.PostgreSQLDialect" }
    )
    plan = "free"
    region = "oregon"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "https://api.render.com/v1/services" `
        -Method Post `
        -Headers @{
            "Authorization" = "Bearer $RENDER_API_KEY"
            "Content-Type" = "application/json"
        } `
        -Body $body

    Write-Host "Service Creation Response:" -ForegroundColor Green
    Write-Host $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
}
catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nCheck your Render dashboard for deployment status" -ForegroundColor Cyan
Write-Host "Dashboard: https://dashboard.render.com" -ForegroundColor Blue
