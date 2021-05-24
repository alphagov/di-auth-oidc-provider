FROM gradle:jdk16@sha256:d31e12d105e332ec2ef1f31c20eac6d1467295487ac70e534e3c1d0ae4a0506e AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x :acceptance-tests:test

FROM openjdk:16-slim@sha256:38f6c41a7f4901b734f4a7cfc0daa6a1995b552d7ec9517496788f6cc8090235

ENV PORT 8080
RUN addgroup --system --gid 1001 appgroup && adduser --system --uid 1001 appuser --gid 1001
RUN mkdir /app
RUN apt-get update && apt-get install -y curl
COPY --from=build \
	/home/gradle/src/build/distributions/src.tar \
	/home/gradle/src/oidc-provider.yml \
	/app/

WORKDIR /app
RUN tar -xvf src.tar \
	&& rm src.tar

RUN chown -R appuser:appgroup /app/
USER appuser
EXPOSE $PORT

ENTRYPOINT ["./src/bin/src", "server", "oidc-provider.yml"]
LABEL project="di-auth-oidc-provider"
