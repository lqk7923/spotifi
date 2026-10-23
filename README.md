# Spotify Clone Backend

Java 21, Spring Boot, PostgreSQL, and Cloudflare R2. Run all commands from the project root. Requires JDK 21, Maven, Docker Compose, and R2 credentials.

## Quick setup

Copy `.env.example` to `.env` and fill in the database and R2 settings. If you already have a `.env`, add the missing settings from `.env.example` instead of overwriting it.

```powershell
# PowerShell (new setup only)
Copy-Item .env.example .env
```

```bash
# Bash (new setup only)
cp .env.example .env
```

Set `R2_ACCOUNT_ID`, `R2_ACCESS_KEY`, `R2_SECRET_KEY`, and `R2_BUCKET_NAME` to your actual R2 values, then start PostgreSQL and the app:

```bash
docker compose -f compose.db.yml up -d
mvn "-Duser.timezone=UTC" spring-boot:run
```

The app defaults to the `dev` profile and starts on `http://localhost:8080`. Stop it with `Ctrl+C`. You can change `-Duser.timezone=UTC` to fit your location.

To build and run the JAR:

```bash
mvn clean package -DskipTests
java -Duser.timezone=UTC -jar target/spotify-clone-be-0.0.1-SNAPSHOT.jar
```

## Configuration

Spring automatically imports `.env` from the current working directory as a Java properties file. This works with Maven, `java -jar`, and IDE runs whose working directory is the project root. An absent `.env` is allowed when configuration is supplied through environment variables. OS environment variables and JVM system properties override `.env` values.

Use plain, unquoted `KEY=value` lines, with comments on their own lines. Do not use `export`, shell interpolation, or inline comments. Java properties treat backslashes as escapes; double any literal backslashes in a value. `.env` is ignored by Git and excluded from Docker builds.

| Variable | Purpose | Development default |
| --- | --- | --- |
| `DB_HOST` | PostgreSQL host | `localhost` |
| `DB_PORT` | PostgreSQL port; also the Compose host port | `5432` |
| `DB_NAME` | Database name; also used by Compose | `spotify_clone_db` |
| `DB_USER` | PostgreSQL username; also used by Compose | `postgres` |
| `DB_PASSWORD` | PostgreSQL password; also used by Compose | `postgres` |
| `DB_URL` | Full JDBC URL, overriding host/port/name for the app | Built from the settings above |
| `DB_POOL_SIZE` | Maximum connection pool size in `prod` | `5` in `prod` |
| `R2_ACCOUNT_ID` | Cloudflare account ID | Required |
| `R2_ACCESS_KEY` | R2 S3 Access Key ID | Required |
| `R2_SECRET_KEY` | R2 S3 Secret Access Key | Required |
| `R2_BUCKET_NAME` | R2 bucket name | Required |
| `PORT` | HTTP port | `8080` (`10000` in `prod`) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed browser origins | Empty |

Compose also reads `.env` automatically. Keep its PostgreSQL settings consistent with the app. Changing these variables does not change credentials or databases in an existing PostgreSQL volume.

The existing development JPA setting is `ddl-auto: create`: application-managed tables are recreated on startup. Use a disposable development database. The production setting remains `update`.

The application no longer depends on Vault or uses a Vault token. `vault.hcl`, `compose.vault.yml`, and existing `vault-data/` are retained for optional separate use.

## Production / Docker

Activate `prod` with `SPRING_PROFILES_ACTIVE=prod` or `--spring.profiles.active=prod`. Supply `DB_URL`, `DB_USER`, `DB_PASSWORD`, and all four R2 variables through `.env` or the deployment environment. Use a PostgreSQL JDBC URL, such as `jdbc:postgresql://host:5432/database`.

The Docker image already selects `prod`. Pass configuration at runtime:

```bash
docker build -t spotify-clone-be .
docker run --rm --env-file .env -p 10000:10000 spotify-clone-be
```

For Docker, set `DB_URL` to a database host reachable from the container. If you set `PORT`, update the port mapping accordingly.

## Cloudflare R2 setup

Create an R2 bucket and credentials for it, then put the account ID, S3 Access Key ID, Secret Access Key, and bucket name in `.env`. The app builds the endpoint as `https://<account-id>.r2.cloudflarestorage.com` with region `auto`.

References:

- [Get started with R2](https://developers.cloudflare.com/r2/get-started/)
- [Create an R2 bucket](https://developers.cloudflare.com/r2/buckets/create-buckets/)
- [Create R2 API credentials and find your account ID](https://developers.cloudflare.com/r2/api/tokens/)

## Development commands

The application context test requires a running PostgreSQL instance and configured R2 settings. Vault is not required.

```bash
# Run all tests
mvn "-Duser.timezone=UTC" test

# Run configuration tests without PostgreSQL or R2 access
mvn -Dtest=EnvironmentConfigurationTests test

# Build without running tests
mvn clean package -DskipTests

# Stop PostgreSQL; keep its data volume
docker compose -f compose.db.yml down
```
