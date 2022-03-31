docker volume create postgresql-data;

docker run --name g2-backend_db \
    -p 5432:5432 \
    -e POSTGRES_USER=g2 \
    -e POSTGRES_PASSWORD=g2 \
    -e POSTGRES_DB=g2-db \
    --mount source=postgresql-data,target=/var/lib/postgresql/data \
    -d postgres:9.6-alpine;
