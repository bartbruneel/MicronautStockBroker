FROM ghcr.io/graalvm/native-image:latest as graalvm

COPY . /home/app/mn-kafka
WORKDIR /home/app/mn-kafka

RUN native-image --no-server -cp build/libs/mn-kafka-*-all.jar

FROM frolvlad/alpine-glibc
RUN apk update && apk add libstdc++
COPY --from=graalvm /home/app/mn-kafka/mn-kafka /app/mn-kafka
ENTRYPOINT ["/app/mn-kafka", "-Xmx68m"]