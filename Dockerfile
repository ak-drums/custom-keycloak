#Для сборки jar
FROM maven:3.8.3-openjdk-17 AS spi-builder
COPY ./keycloakspi/src 	    /home/app/src
COPY ./keycloakspi/pom.xml  /home/app

RUN mvn verify -f /home/app/pom.xml --fail-never
RUN mvn -f /home/app/pom.xml package

COPY ./air-themes  /home/app-theme/air-themes
WORKDIR /home/app-theme/air-themes/
RUN jar cMf AirTheme.jar .

#Для сборки keycloak вместе с jar
FROM quay.io/keycloak/keycloak:22.0 as keycloak-builder
COPY --from=spi-builder      /home/app/target/keycloakspi-1.0.jar /opt/keycloak/providers/
COPY --from=spi-builder      /home/app-theme/air-themes/AirTheme.jar      /opt/keycloak/providers/
RUN  /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:22.0
COPY --from=keycloak-builder /opt/keycloak/providers/          /opt/keycloak/providers/
COPY --from=keycloak-builder /opt/keycloak/lib/quarkus/        /opt/keycloak/lib/quarkus/
