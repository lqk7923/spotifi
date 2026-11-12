package axo.spotifyclone.spotifyclonebe;

import axo.spotifyclone.spotifyclonebe.component.PaginationRequestFactory;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.dto.storage.PresignedUrl;
import axo.spotifyclone.spotifyclonebe.exceptions.BadRequestException;
import axo.spotifyclone.spotifyclonebe.exceptions.ResourceNotFoundException;
import axo.spotifyclone.spotifyclonebe.mapper.TrackResponseMapper;
import axo.spotifyclone.spotifyclonebe.service.LikedMusicService;
import axo.spotifyclone.spotifyclonebe.service.MediaUrlService;
import axo.spotifyclone.spotifyclonebe.service.implement.LikedMusicServiceImplement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none", showSql = false)
@ActiveProfiles("test")
@Import({LikedMusicServiceImplement.class, TrackResponseMapper.class, PaginationRequestFactory.class})
@Sql("classpath:search-schema.sql")
@Sql(statements = {
        "CREATE TABLE IF NOT EXISTS liked_music (id UUID DEFAULT RANDOM_UUID() PRIMARY KEY, " +
                "user_id VARCHAR(255) NOT NULL, track_id UUID NOT NULL)",
        "CREATE TABLE IF NOT EXISTS app_user (id VARCHAR(255) PRIMARY KEY, username TEXT, email TEXT)"
})
class LikedMusicServiceTests {
    @Autowired private LikedMusicService likedMusicService;
    @Autowired private JdbcTemplate jdbc;
    @MockitoBean private MediaUrlService mediaUrlService;

    private final Instant expiration = Instant.parse("2026-11-10T00:00:00Z");

    @BeforeEach
    void seedLikes(){
        jdbc.update("INSERT INTO app_user (id) VALUES ('listener'), ('other-user')");
        jdbc.update("INSERT INTO album (album_id, album_title, album_author, album_release_date, album_cover_key) " +
                "VALUES (?, 'Album', 'Artist', DATE '2026-01-01', 'cover')", id(1));
        for(int number = 1; number <= 3; number++){
            jdbc.update("INSERT INTO track (track_id, album_id, track_title, author, track_duration, track_key) " +
                    "VALUES (?, ?, ?, 'Artist', 180000, ?)", id(number), id(1), "Song " + number, "audio-" + number);
            jdbc.update("INSERT INTO liked_music (id, user_id, track_id) VALUES (?, ?, ?)",
                    id(number), number == 3 ? "other-user" : "listener", id(number));
        }
        when(mediaUrlService.generateUrl("cover")).thenReturn(new PresignedUrl("https://example.com/cover", expiration));
    }

    @ParameterizedTest
    @CsvSource({"0,2", "1,1"})
    void pagesContainOnlyRequestedUsersTracksWithMatchingTotals(int page, int trackNumber){
        var result = likedMusicService.listLikedTracks("listener", page, 1);

        assertThat(result.getData()).extracting(TrackDetailsResponse::getTrackId).containsExactly(id(trackNumber));
        assertThat(result.getPagination().getPage()).isEqualTo(page);
        assertThat(result.getPagination().getSize()).isEqualTo(1);
        assertThat(result.getPagination().getTotalElements()).isEqualTo(2);
        assertThat(result.getPagination().getTotalPages()).isEqualTo(2);
        var track = result.getData().getFirst();
        assertThat(track.getTitle()).isEqualTo("Song " + trackNumber);
        assertThat(track.getArtist()).isEqualTo("Artist");
        assertThat(track.getDurationMs()).isEqualTo(180000L);
        assertThat(track.getAlbumTitle()).isEqualTo("Album");
        assertThat(track.getAlbumId()).isEqualTo(id(1));
        assertThat(track.getCoverUrl()).isEqualTo("https://example.com/cover");
        assertThat(track.getCoverExpiresAt()).isEqualTo(expiration);
    }

    @Test
    void pagePastTheEndPreservesTotals(){
        var result = likedMusicService.listLikedTracks("listener", 2, 1);
        assertThat(result.getData()).isEmpty();
        assertThat(result.getPagination().getTotalElements()).isEqualTo(2);
        assertThat(result.getPagination().getTotalPages()).isEqualTo(2);
    }

    @Test
    void userWithNoLikesGetsAnEmptyPage(){
        var result = likedMusicService.listLikedTracks("no-likes", 0, 20);
        assertThat(result.getData()).isEmpty();
        assertThat(result.getPagination().getTotalElements()).isZero();
        assertThat(result.getPagination().getTotalPages()).isZero();
    }

    @ParameterizedTest
    @CsvSource({"-1,20", "0,0", "0,21"})
    void invalidPaginationIsRejected(int page, int size){
        assertThatThrownBy(() -> likedMusicService.listLikedTracks("listener", page, size))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void missingUserIdIsRejected(String userId){
        assertThatThrownBy(() -> likedMusicService.listLikedTracks(userId, 0, 20))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void settingLikeTwiceAddsOneTrackAndRemovingItLeavesOtherUsersLikes(){
        likedMusicService.setTrackLiked("listener", id(3), true);
        likedMusicService.setTrackLiked("listener", id(3), true);

        var liked = likedMusicService.listLikedTracks("listener", 0, 20);
        assertThat(liked.getData()).extracting(TrackDetailsResponse::getTrackId)
                .containsExactlyInAnyOrder(id(1), id(2), id(3));

        likedMusicService.setTrackLiked("listener", id(3), false);
        likedMusicService.setTrackLiked("listener", id(3), false);

        assertThat(likedMusicService.listLikedTracks("listener", 0, 20).getData())
                .extracting(TrackDetailsResponse::getTrackId).containsExactly(id(2), id(1));
        assertThat(likedMusicService.listLikedTracks("other-user", 0, 20).getData())
                .extracting(TrackDetailsResponse::getTrackId).containsExactly(id(3));
    }

    @Test
    void likingAnUnknownTrackReturnsNotFound(){
        assertThatThrownBy(() -> likedMusicService.setTrackLiked("listener", id(4), true))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void unlikingRemovesStaleAndDuplicateLikes(){
        jdbc.update("INSERT INTO liked_music (id, user_id, track_id) VALUES (?, 'listener', ?), (?, 'listener', ?)",
                id(4), id(4), id(5), id(4));

        likedMusicService.setTrackLiked("listener", id(4), false);

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM liked_music WHERE user_id = ? AND track_id = ?",
                Long.class, "listener", id(4))).isZero();
    }

    private UUID id(int number){
        return new UUID(0, number);
    }
}
