# ---- Etape 1 : construction du livrable avec Maven ----
FROM maven:3.9-eclipse-temurin-17 AS construction
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Etape 2 : image d'execution minimale ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# curl sert au HEALTHCHECK, l'utilisateur non privilegie est une bonne
# pratique de securite quand un conteneur tourne en production
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/* \
 && groupadd -r mediatheque && useradd -r -g mediatheque mediatheque

COPY --from=construction /build/target/conteneur-mediatheque.jar app.jar
RUN chown -R mediatheque:mediatheque /app
USER mediatheque

EXPOSE 8111

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD curl -fsS http://localhost:8111/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
