#!/bin/bash

./mvnw clean install -DskipTests=true
docker compose build
docker compose up