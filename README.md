# Spotify Clone Backend

Java 21, Spring Boot, PostgreSQL, HashiCorp Vault, and Cloudflare R2. Run all commands from the project root. Requires JDK 21, Maven, Docker Compose, and a Cloudflare account with R2 enabled.

## Quick setup

```bash
docker compose -f compose.db.yml up -d
docker compose -f compose.vault.yml up -d
```

Complete [Vault setup](#vault-setup) and [Cloudflare R2 setup](#cloudflare-r2-setup) below before building or starting the app. Vault must be unsealed and contain the application keys.

Set the Vault token in the same terminal used to run Maven and Java:

```powershell
# PowerShell: replace the placeholder with your local Vault token
$env:SPRING_CLOUD_VAULT_TOKEN = "<vault-token>"
```

```bash
# Bash
export SPRING_CLOUD_VAULT_TOKEN='<vault-token>'
```

Then build and run:

```bash
mvn "-Duser.timezone=UTC" clean package
java -Duser.timezone=UTC -jar target/spotify-clone-be-0.0.1-SNAPSHOT.jar
```

> [!NOTE]
> You can change `-Duser.timezone=UTC` to fit your location. `.env.example` documents the Vault token variable, but `java -jar` does not automatically load `.env`. Export the variable as above or set it in your IDE's run configuration.

The app starts on `http://localhost:8080`. Stop it with `Ctrl+C`.

## Configuration

Default database: `localhost:5432/spotify_clone_db`, user `postgres`, password `postgres`.

- `compose.db.yml` uses `DB_USER` and `DB_PASSWORD` to initialize PostgreSQL. Set these before the first database startup if you want different credentials.
- The app reads `database.devUser` and `database.devPassword` from Vault, with `postgres` as the fallback for both. Keep these values consistent with the PostgreSQL credentials. Changing Compose variables does not change credentials in an existing database volume.
- To use a different database name, set the optional Vault key `database.name` in `secret/auth-db` and update `POSTGRES_DB` in `compose.db.yml` to match. The app does not currently read `DB_NAME`.

> [!NOTE]
> The current JPA setting is `ddl-auto: create-drop`, so application-managed tables are recreated on startup and dropped on shutdown. Use a disposable development database.

## Vault setup

Setup walkthrough: [How to Secure Secret Data in Spring Boot Using HashiCorp Vault](https://medium.com/@afdulrohmat03/how-to-secure-secret-data-in-spring-boot-using-hashicorp-vault-e1b37c269a15).

Use this repository's `compose.vault.yml`, container name `spotify_clone_vault`, and the paths and key names below when following the walkthrough. The app imports configuration from Vault at `http://localhost:8200` through `spring.config.import: vault://` and uses token authentication. The [local Vault UI](http://localhost:8200) is available at the same address. Supply the token through `SPRING_CLOUD_VAULT_TOKEN` rather than placing it in `application.yaml`.

1. Start Vault with `docker compose -f compose.vault.yml up -d`.
2. Open the Vault UI. For a new `vault-data` directory, initialize Vault and securely save the unseal keys and initial root token. Skip initialization if Vault is already initialized.
3. Unseal Vault using the number of distinct keys required by your configured threshold. The default is 3 of 5 keys. Log in to the UI with the initial root token for setup.
4. If the `secret/` mount does not exist, enable a **KV** secrets engine at `secret` (KV v2 for a new setup).
5. Under that mount, create the `auth-db` and `cre-storage` entries with the exact keys listed below. If they already exist, check their keys instead of recreating them.
6. Set `SPRING_CLOUD_VAULT_TOKEN` to a token authorized to read these entries before running the app or tests. Use the root token only for local testing; use a dedicated application token for shared environments.

### Application keys

The configured Vault backend is `secret`, with `application-name: auth-db,cre-storage`. These are logical KV paths; enter `auth-db` and `cre-storage` as entry names inside the `secret/` mount.

| Vault path | Key | Purpose | App fallback |
| --- | --- | --- | --- |
| `secret/auth-db` | `database.devUser` | PostgreSQL username | `postgres` |
| `secret/auth-db` | `database.devPassword` | PostgreSQL password | `postgres` |
| `secret/cre-storage` | `r2.account-id` | Cloudflare account ID | Required; no fallback |
| `secret/cre-storage` | `r2.access-key` | R2 S3 Access Key ID | Required; no fallback |
| `secret/cre-storage` | `r2.secret-key` | R2 S3 Secret Access Key | Required; no fallback |

Document key names and placeholders only. Keep actual credentials, Vault tokens, and unseal keys out of README and Git. `.env` and `/vault-data/` are already ignored by this repository.

> [!NOTE]
> These 5 application keys are separate from Vault's unseal keys. Vault data persists in `./vault-data`; after a Vault restart, unseal it again before starting the app. The supplied `vault.hcl` disables TLS for local development; enable TLS before using Vault outside that setup.

Reference: [Vault initialization and default key threshold](https://developer.hashicorp.com/vault/docs/commands/operator/init).

## Cloudflare R2 setup

Follow the official Cloudflare guides:

- [Get started with R2](https://developers.cloudflare.com/r2/get-started/)
- [Create an R2 bucket](https://developers.cloudflare.com/r2/buckets/create-buckets/)
- [Create R2 API credentials and find your account ID](https://developers.cloudflare.com/r2/api/tokens/)

Create a bucket for your track objects, then create R2 credentials scoped to that bucket. **Object Read only** is sufficient for the current presigned download flow; choose **Object Read & Write** if those credentials will also upload objects.

Save the credentials in Vault at `secret/cre-storage`:

```json
{
  "r2.account-id": "<cloudflare-account-id>",
  "r2.access-key": "<r2-access-key-id>",
  "r2.secret-key": "<r2-secret-access-key>"
}
```

Use the generated S3 **Access Key ID** and **Secret Access Key** for the last two values. The app builds the S3 endpoint as `https://<account-id>.r2.cloudflarestorage.com` and uses region `auto`. Use your bucket name and an existing object key when requesting a presigned download URL.

## Development commands

PostgreSQL and an unsealed, configured Vault must be running, and `SPRING_CLOUD_VAULT_TOKEN` must be set for tests.

```bash
# Run tests
mvn "-Duser.timezone=UTC" test

# Build without running tests
mvn clean package -DskipTests

# Stop PostgreSQL; keep its data volume
docker compose -f compose.db.yml down

# Stop Vault; keep ./vault-data (unseal again after restarting)
docker compose -f compose.vault.yml down
```
