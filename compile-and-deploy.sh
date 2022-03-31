docker volume create postgresql-data || echo "Volume already existed"
docker build -t g2-backend_app .  &&
docker-compose down &&
docker-compose up;