#!/bin/bash
echo "=== Fresh build without cache ==="

# Останавливаем и удаляем контейнеры
docker-compose down

# Удаляем образ
docker rmi dorlova-nail-app:latest 2>/dev/null || true

# Собираем без кеша
docker-compose build --no-cache

# Запускаем
docker-compose up