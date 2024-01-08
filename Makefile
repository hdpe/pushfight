repository:=471112958535.dkr.ecr.us-east-1.amazonaws.com

image:
	docker build -t pushfight-api .

push:
	aws ecr get-login-password --region us-east-1 | \
		docker login \
			--username AWS \
			--password-stdin \
			$(repository)
	docker tag pushfight-api $(repository)/pushfight-api
	docker push $(repository)/pushfight-api

deploy:
	aws ecs update-service \
		--cluster cluster1 \
		--service pushfight-api \
		--force-new-deployment

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