# API contract

All property names, error codes, and application-generated messages use English. Track and album IDs are UUID strings. User IDs are Keycloak subject strings. Track durations use milliseconds. Release dates use `YYYY-MM-DD`; URL expiration timestamps use UTC ISO 8601.

## Endpoints

| Method | Path | Response | Access |
| --- | --- | --- | --- |
| GET | `/users/me` | Current user's stored ID, username, and email | Bearer token |
| GET | `/tracks?q=hello&page=0&size=20` | Paginated track metadata with optional search | Public |
| GET | `/tracks/{id}` | Track metadata | Bearer token |
| GET | `/tracks/{id}/playback` | Audio URL and expiration | Bearer token |
| GET | `/tracks/{id}/playback?include=metadata` | Audio URL, expiration, and track metadata | Bearer token |
| GET | `/albums?q=hello&page=0&size=20` | Paginated album metadata with optional search | Bearer token |
| GET | `/albums/{albumId}` | Album metadata | Bearer token |
| GET | `/albums/{albumId}/tracks?page=0&size=20` | Paginated track metadata for an album | Bearer token |

The track and album collection endpoints use `q` to match any part of a title or artist name, ignoring case and surrounding whitespace. Omitting `q` or providing a blank value lists the collection. Characters such as `%` and `_` match literally.

Pages start at 0. The default size is 20, and `size` accepts values from 1 to 20. Invalid pagination values return 400. Tracks and albums are ordered by their respective IDs. A search with no matches or a page beyond the last page returns an empty `data` array.

`/albums/{albumId}/tracks` uses the same `page` and `size` parameters and returns `data` and `pagination`. An existing album with no tracks returns an empty page with `totalElements` and `totalPages` both set to 0. A missing album returns 404.

Every track must belong to an existing album. Track detail and paginated track queries use inner joins to albums, with the same join in the pagination count queries. When the album tracks query counts zero tracks, the service checks whether the album exists. Playback without metadata reads only the track's audio key.

Playback accepts only an omitted/empty `include` or `include=metadata`; other values return 400. Without metadata, playback reads only the track's audio object key. With metadata, one repository query retrieves both the source and metadata.

## Success responses

Authenticated JWT requests insert the current user into `app_user` when its ID is first encountered. The configured Keycloak issuer supplies `sub` as `id`, `preferred_username` as `username`, and `email` as `email`. IDs are stored as strings to support Keycloak's subject format. Username and email can be null when the corresponding claims are absent. Existing users retain the profile saved on first insertion. The insert uses `on conflict do nothing` with the ID primary key so repeated requests for the same ID reuse the existing record.

`GET /users/me` returns the stored profile with `Cache-Control: no-store`.

```json
{
  "data": {
    "id": "743360a4-07d9-4356-b75c-65517b48229a",
    "username": "listener",
    "email": "listener@example.com"
  }
}
```

Enable the Keycloak `profile` and `email` client scopes and include their claims in access tokens. Synchronization runs on requests with validated bearer tokens, including public routes when a token is supplied. Anonymous requests do not create users. This maps users from the single issuer configured for this backend. Synchronization failures return 500 and stop request processing.

A single resource is wrapped in `data`:

```json
{
  "data": {
    "trackId": "743360a4-07d9-4356-b75c-65517b48229a",
    "title": "Example Track",
    "durationMs": 173000,
    "artist": "Example Artist",
    "albumTitle": "Example Album",
    "albumId": "9e6c7ab6-e760-4abb-986b-99bff71802f2",
    "coverUrl": "https://storage.example/cover?signature=...",
    "coverExpiresAt": "2026-10-29T12:00:00Z"
  }
}
```

Paginated responses have `data` and `pagination` at the top level:

```json
{
  "data": [],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 0,
    "totalPages": 0
  }
}
```

Playback without metadata:

```json
{
  "data": {
    "trackId": "743360a4-07d9-4356-b75c-65517b48229a",
    "audioUrl": "https://storage.example/audio?signature=...",
    "expiresAt": "2026-10-29T12:00:00Z"
  }
}
```

With `include=metadata`, the same playback object also contains `metadata`, using the track metadata fields shown above. When metadata is not requested, the property is omitted.

Album metadata contains `albumId`, `title`, `artist`, `coverUrl`, `coverExpiresAt`, `releaseDate`, and `albumType`. The API returns the stored type, which can be `SINGLE` (1–3 tracks), `EP` (4–7 tracks), `LP` (8 or more tracks), or `null` if unset. Tracks are retrieved from the album tracks endpoint. Track metadata uses the track's artist, and album metadata uses the album's artist.

Missing cover keys produce `coverUrl: null` and `coverExpiresAt: null`. An audio source must be present to return a successful playback response.

## Error responses

```json
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "Track 743360a4-07d9-4356-b75c-65517b48229a not found"
}
```

| HTTP status | Code | Meaning |
| --- | --- | --- |
| 400 | `BAD_REQUEST` | Invalid pagination or any MVC error processed by `handleExceptionInternal` |
| 401 | `UNAUTHORIZED` | Authentication is required or the token is invalid |
| 403 | `FORBIDDEN` | Access is denied |
| 404 | `RESOURCE_NOT_FOUND` | The requested track or album does not exist |
| 500 | `INTERNAL_SERVER_ERROR` | An unexpected error occurred; details are logged server-side |

Every MVC error processed by `handleExceptionInternal` returns 400 with code `BAD_REQUEST` and message `Invalid request.`, including errors that Spring originally classified as 405, 415, or 500. Each is logged at WARN level with the request description, original HTTP status, and exception stack trace in the Spring console. Security responses preserve the bearer authentication headers.

Request parameter type mismatches, such as a malformed UUID in `/tracks/{id}` or `page=abc`, return 400 with the message `Invalid request.`

## URL lifetime

Set `MEDIA_URL_EXPIRATION` to an ISO 8601 duration, for example `PT2M` or `PT10M`. The default is 2 minutes, with an allowed range of 1 second to 7 days. The setting applies to newly signed audio and cover URLs.

Fetch a new URL before its expiration. The expiration of a cover URL is independent of the audio URL expiration. Responses containing signed URLs send `Cache-Control: no-store` to prevent HTTP caches from reusing expired links.

## Changes for frontend consumers

- Read single-resource payloads from `response.data` and list items from `response.data` alongside `response.pagination`.
- Replace `trackTitle`/`albumTitle` on their respective resource objects with `title`. Track metadata still has `albumTitle` for its parent album.
- Replace `trackDuration` with `durationMs`, and `author` with `artist`.
- Replace the previous cover URL names with `coverUrl`; read its expiration from `coverExpiresAt`.
- Replace `trackPresignedLink` with `audioUrl` and read `expiresAt`. A playback response has no standalone cover URL; request metadata or reuse existing track metadata.
- Read album details from `/albums/{albumId}` and request each page of tracks from `/albums/{albumId}/tracks`.
- Replace numeric `errorCode` handling with `code` and read the message from `message`. Errors use a single `ApiErrorResponse` object without an `error` wrapper.

This refactor does not change the database schema or add track-number ordering.
