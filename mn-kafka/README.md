To run a kafka docker container use:

docker run --rm -p 2181:2181 -p 3030:3030 -p 8081-8083:8081-8083 -p 9581-9585:9581-9585 -p 9092:9092 -e ADV_HOST=127.0.0.1 lensesio/fast-data-dev:latest

to get the lensios.io interface go to

localhost:3030

To build the graalvm container:

./gradlew clean assemble

docker build . -t mn-kafka

docker network create -d overlay testservice

docker stack deploy -c app.stack.yml mn-kafka-stack

docker service logs -f mn-kafka-stack_mn-kafka

docker stats (for checking memory consumption)

## Micronaut 3.3.0 Documentation

- [User Guide](https://docs.micronaut.io/3.3.0/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.3.0/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.3.0/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)## Feature kafka documentation

- [Micronaut Kafka Messaging documentation](https://micronaut-projects.github.io/micronaut-kafka/latest/guide/index.html)

## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)

