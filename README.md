# Social Engine API

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Redis via `StringRedisTemplate`
- Maven
- Render.com for deployment

## Architecture Overview

This service exposes a small social interaction API where posts and comments are stored in PostgreSQL, while Redis handles fast-moving counters and operational guardrails. The `PostController` delegates to transactional service classes for persistence, `ViralityService` manages Redis score updates, `GuardrailService` enforces bot interaction caps before any comment write reaches PostgreSQL, and `NotificationSweeper` batches delayed push notifications every five minutes.

The write flow for bot comments is intentionally ordered as: validate request, reserve Redis guardrails, persist the comment in PostgreSQL, increment Redis virality, and enqueue or emit notifications. This keeps the horizontal cap race-safe and ensures database writes only happen after the required Redis checks pass.

## How to Run Locally

### Prerequisites

- Java 17
- Maven
- Docker

### Commands

1. Set the required environment variables for Docker Compose and the Spring Boot application.
2. Run `docker-compose up -d`
3. Run `mvn spring-boot:run`

### Set these env vars locally

- `LOCAL_POSTGRES_PASSWORD`
- `DATABASE_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD` if your Redis instance requires authentication

## API Endpoints with curl examples

Set a base URL in your shell before running the examples:

```bash
BASE_URL="<your-service-base-url>"
```

Create a post:

```bash
curl -X POST "$BASE_URL/api/posts" \
  -H "Content-Type: application/json" \
  -d '{
    "authorId": 1,
    "authorType": "USER",
    "content": "hello world"
  }'
```

Create a human comment:

```bash
curl -X POST "$BASE_URL/api/posts/1/comments" \
  -H "Content-Type: application/json" \
  -d '{
    "authorId": 2,
    "authorType": "USER",
    "content": "nice post",
    "depthLevel": 1
  }'
```

Create a bot comment:

```bash
curl -X POST "$BASE_URL/api/posts/1/comments" \
  -H "Content-Type: application/json" \
  -d '{
    "authorId": 1,
    "authorType": "BOT",
    "content": "nice post",
    "depthLevel": 1,
    "botId": 1,
    "humanId": 1
  }'
```

Like a post:

```bash
curl -X POST "$BASE_URL/api/posts/1/like" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1
  }'
```

Trigger a vertical cap violation:

```bash
curl -X POST "$BASE_URL/api/posts/1/comments" \
  -H "Content-Type: application/json" \
  -d '{
    "authorId": 1,
    "authorType": "BOT",
    "content": "too deep",
    "depthLevel": 25,
    "botId": 1,
    "humanId": 1
  }'
```

## Redis Guardrails Explanation

### Atomic horizontal cap

The horizontal cap uses Redis `INCR` first and only evaluates the returned count after the increment. This avoids race conditions that happen with a `GET` followed by a separate `SET` or conditional write. If the incremented value crosses `100`, the service immediately decrements the same key and throws a `429 TOO_MANY_REQUESTS` guardrail exception.

### TTL-based cooldown system

Bot-to-human interaction cooldowns use `SETNX` semantics through `setIfAbsent` with a `600` second TTL. If the key already exists, the request is rejected. Notification cooldowns use a separate `900` second TTL to suppress repeated pushes to the same user while still preserving pending activity in Redis lists.

### Notification batching logic

When a bot interacts with a human post and the notification cooldown is active, the bot name is appended to `user:{humanId}:pending_notifs`. The scheduled sweeper scans for those lists every five minutes, reads the first bot name and total count, logs a summarized message, and deletes the pending list to keep Redis clean.

## Render Deployment Guide

1. Create a new Web Service for this repository.
2. Add a managed PostgreSQL service in Render.
3. Add a managed Redis service in Render.
4. Set the environment variables from the table below in the Render dashboard.
5. Ensure `DATABASE_URL` is provided as a JDBC-compatible PostgreSQL datasource value for Spring Boot.
6. Deploy the service and verify the health of the web service logs plus Redis connectivity.

## Environment Variables Reference

| Variable | Required | Description |
| --- | --- | --- |
| `PORT` | No | HTTP port for the Spring Boot app. Defaults to `8080`. |
| `DATABASE_URL` | Yes | PostgreSQL datasource URL consumed by Spring Boot. |
| `DB_USERNAME` | Yes | PostgreSQL username. |
| `DB_PASSWORD` | Yes | PostgreSQL password. |
| `DB_DRIVER_CLASS_NAME` | No | JDBC driver class name override. |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | No | Hibernate schema management mode. |
| `SPRING_JPA_SHOW_SQL` | No | Enables or disables SQL logging. |
| `SPRING_JPA_HIBERNATE_DIALECT` | No | Hibernate dialect override. |
| `SPRING_JPA_OPEN_IN_VIEW` | No | Enables or disables Open Session in View. |
| `REDIS_HOST` | Yes | Redis hostname. |
| `REDIS_PORT` | No | Redis port. Defaults to `6379`. |
| `REDIS_PASSWORD` | No | Redis password if the instance requires it. |
| `LOCAL_POSTGRES_DB` | No | Docker Compose PostgreSQL database name. Defaults to `socialengine`. |
| `LOCAL_POSTGRES_USER` | No | Docker Compose PostgreSQL username. Defaults to `postgres`. |
| `LOCAL_POSTGRES_PASSWORD` | Yes for Compose | Docker Compose PostgreSQL password. |
| `LOCAL_POSTGRES_PORT` | No | Host port mapped to PostgreSQL. Defaults to `5432`. |
| `LOCAL_REDIS_PORT` | No | Host port mapped to Redis. Defaults to `6379`. |
