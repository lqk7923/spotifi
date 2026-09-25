# Spotify Clone Backend

Java 21, Spring Boot, and PostgreSQL. Run all commands from the project root. Requires JDK 21, Maven, and Docker Compose.

## Quick setup

```bash
docker compose -f compose.db.yml up -d
mvn "-Duser.timezone=UTC" clean package
java -Duser.timezone=UTC -jar target/spotify-clone-be-0.0.1-SNAPSHOT.jar
```

>[NOTE] You can change '-Duser.timezone=UTC' part to fit your location.

The app starts on `http://localhost:8080`. Stop it with `Ctrl+C`.

## Configuration

Default database: `localhost:5432/spotify_clone_db`, user `postgres`, password `postgres`. To use different credentials, set `DB_USER` and `DB_PASSWORD` before starting Docker Compose and the app. `DB_NAME` changes the app's database name; `compose.db.yml` still creates `spotify_clone_db` unless you also change `POSTGRES_DB` there.

```bash
# Run tests (PostgreSQL must be running)
mvn "-Duser.timezone=UTC" test

# Build without running tests
mvn clean package -DskipTests

# Stop PostgreSQL; keep its data volume
docker compose -f compose.db.yml down
```
