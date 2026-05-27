# Tenvia - Leaderboard Service

This microservice keeps track of the top scores for `tenvia-app`. 

## Overview
- **RabbitMQ** - listens to a RabbitMQ queue for score update events which are sent by `tenvia-app` when a game session is finished
- **Storage** - high score data is currently stored in an in-memory H2 database (in development)

## Configuration
The service configs for RabbitMQ and H2 are stored in `application.properties`.

## Endpoints

| Method | Name           | Description                 |
|--------|----------------|-----------------------------|
| `GET`  | `/leaderboard` | Retrieves the top 25 scores |

## Running

**Required:** Docker

This service depends on RabbitMQ and the shared `tenvia-common` module so it is recommended to run the build script provided.

It will build all the modules and bring up the services locally (using docker compose)
```bash
# Run from project root directory
./build-local.sh
```

To stop, run:
```
docker compose down
```