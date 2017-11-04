all:
	./mvnw package
	./mvnw install dockerfile:build
