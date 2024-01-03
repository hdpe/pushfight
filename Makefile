start-deps:
	docker compose \
		-f docker-compose.deps.yml \
		up

start-all:
	 2>/dev/null docker network create pushfight_api; \
	 	test $$? -le 1
	 docker compose \
	 	-f docker-compose.deps.yml \
	 	-f docker-compose.app.yml \
	 	up \
	 	--build