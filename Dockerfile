FROM registry.access.redhat.com/ubi8/openjdk-11 AS builder
WORKDIR /work
COPY . .
USER 0
RUN CURRENT_ENV=mas ./mvnw clean install -Dmaven.javadoc.skip=true --no-transfer-progress -DtrimStackTrace=false -DskipTests=true

FROM registry.access.redhat.com/ubi8/openjdk-11:latest
USER 185
WORKDIR /work/

COPY --from=builder /work/api/target/*-runner.jar /deployments/app.jar
COPY --from=builder /work/api/target/lib/ /deployments/lib/

EXPOSE 8080

ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/app.jar"