package axo.spotifyclone.spotifyclonebe;

import axo.spotifyclone.spotifyclonebe.component.PaginationRequestFactory;
import axo.spotifyclone.spotifyclonebe.component.SearchQueryFactory;
import axo.spotifyclone.spotifyclonebe.controller.AlbumController;
import axo.spotifyclone.spotifyclonebe.controller.TrackController;
import axo.spotifyclone.spotifyclonebe.dto.storage.PresignedUrl;
import axo.spotifyclone.spotifyclonebe.exceptions.GlobalExceptionHandler;
import axo.spotifyclone.spotifyclonebe.mapper.TrackResponseMapper;
import axo.spotifyclone.spotifyclonebe.service.AlbumService;
import axo.spotifyclone.spotifyclonebe.service.MediaUrlService;
import axo.spotifyclone.spotifyclonebe.service.TrackService;
import axo.spotifyclone.spotifyclonebe.service.implement.AlbumServiceImplement;
import axo.spotifyclone.spotifyclonebe.service.implement.TrackServiceImplement;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none", showSql = false)
@ActiveProfiles("test")
@Import({TrackServiceImplement.class, AlbumServiceImplement.class, TrackResponseMapper.class,
        PaginationRequestFactory.class, SearchQueryFactory.class})
@Sql("classpath:search-schema.sql")
class SearchApiTests {
    @Autowired private TrackService trackService;
    @Autowired private AlbumService albumService;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private EntityManagerFactory entityManagerFactory;
    @MockitoBean private MediaUrlService mediaUrlService;

    private MockMvc mvc;

    @ParameterizedTest
    @ValueSource(strings = {"", "adele"})
    void trackQueryCountStaysConstantAsPageSizeGrows(String query) throws Exception {
        // Keep all covers populated to isolate SQL counts from missing-cover handling.
        jdbc.update("UPDATE album SET album_cover_key = ? WHERE album_id = ?", "cover-2", id(2));
        for(int number = 5; number <= 25; number++){
            insertAlbum(number, "Album " + number, "Adele", "cover-" + number);
            insertTrack(number, number, "Song " + number, "Adele");
        }

        var statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        try {
            for(int size : new int[]{1, 20}){
                statistics.clear();
                mvc.perform(get("/tracks").param("q", query).param("size", String.valueOf(size)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data", hasSize(size)))
                        .andExpect(jsonPath("$.pagination.totalElements").value(query.isEmpty() ? 25 : 23));

                assertThat(statistics.getPrepareStatementCount()).as("SQL statements for size %s", size)
                        .isEqualTo(2);
                assertThat(statistics.getEntityFetchCount()).isZero();
            }
        } finally {
            statistics.setStatisticsEnabled(false);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/playback", "/playback?include=metadata"})
    void trackDetailAndPlaybackUseOneQuery(String suffix) throws Exception {
        var statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        try {
            mvc.perform(get("/tracks/" + id(1) + suffix))
                    .andExpect(status().isOk());
            assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
            assertThat(statistics.getEntityFetchCount()).isZero();
        } finally {
            statistics.setStatisticsEnabled(false);
        }
    }

    @BeforeEach
    void setUp(){
        mvc = MockMvcBuilders.standaloneSetup(new TrackController(trackService), new AlbumController(albumService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        when(mediaUrlService.generateUrl(anyString())).thenAnswer(invocation ->
                new PresignedUrl("https://media.example/" + invocation.getArgument(0),
                        Instant.parse("2026-10-29T12:00:00Z")));

        insertAlbum(1, "Hello World", "Adele", "cover-1");
        insertAlbum(2, "25", "ADELE", null);
        insertAlbum(3, "100%_Live!", "Other Artist", "cover-3");
        insertAlbum(4, "Unrelated", "Other Artist", "cover-4");
        insertTrack(1, 1, "Hello Again", "Adele");
        insertTrack(2, 2, "Skyfall", "ADELE");
        insertTrack(3, 3, "100%_Live!", "Other Artist");
        insertTrack(4, 1, "Unrelated", "Other Artist");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/tracks", "/albums"})
    void matchesTitleIgnoringCaseAndSurroundingWhitespace(String path) throws Exception {
        mvc.perform(get(path).param("q", "  hELLo  "))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.pagination.totalElements").value(1))
                .andExpect(jsonPath("$.data[0].coverUrl").value("https://media.example/cover-1"))
                .andExpect(jsonPath("$.data[0].coverExpiresAt").value("2026-10-29T12:00:00Z"))
                .andExpect(jsonPath("$.data[0].audioUrl").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/tracks", "/albums"})
    void artistMatchesHaveAccurateTotalsAndStablePagination(String path) throws Exception {
        mvc.perform(get(path).param("q", "adele").param("size", "1").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title").value(path.startsWith("/tracks") ? "Skyfall" : "25"))
                .andExpect(jsonPath("$.data[0].coverUrl").doesNotExist())
                .andExpect(jsonPath("$.data[0].coverExpiresAt").doesNotExist())
                .andExpect(jsonPath("$.pagination.page").value(1))
                .andExpect(jsonPath("$.pagination.size").value(1))
                .andExpect(jsonPath("$.pagination.totalElements").value(2))
                .andExpect(jsonPath("$.pagination.totalPages").value(2));
    }

    @ParameterizedTest
    @CsvSource({"/tracks,%", "/tracks,_", "/tracks,!", "/tracks,%_Live!",
            "/albums,%", "/albums,_", "/albums,!", "/albums,%_Live!"})
    void searchTreatsWildcardsAndEscapeCharacterLiterally(String path, String query) throws Exception {
        mvc.perform(get(path).param("q", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title").value("100%_Live!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/tracks", "/albums"})
    void missingOrBlankQueryReturnsTheWholeCollection(String path) throws Exception {
        for(String query : new String[]{null, "", "   "}){
            var request = get(path);
            if(query != null) request.param("q", query);
            mvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(4)))
                    .andExpect(jsonPath("$.pagination.page").value(0))
                    .andExpect(jsonPath("$.pagination.size").value(20))
                    .andExpect(jsonPath("$.pagination.totalElements").value(4));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"/tracks", "/albums"})
    void noMatchesReturnAnEmptyPage(String path) throws Exception {
        mvc.perform(get(path).param("q", "not-present"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.pagination.totalElements").value(0))
                .andExpect(jsonPath("$.pagination.totalPages").value(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/tracks", "/albums"})
    void pagePastTheEndKeepsMatchingTotals(String path) throws Exception {
        mvc.perform(get(path).param("q", "adele").param("page", "3").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.pagination.totalElements").value(2));
    }

    @ParameterizedTest
    @CsvSource({"page,-1", "size,0", "size,21", "page,abc", "size,abc"})
    void invalidPaginationReturnsBadRequest(String parameter, String value) throws Exception {
        for(String path : new String[]{"/tracks", "/albums", "/albums/" + id(1) + "/tracks"}){
            for(String query : new String[]{"", "hello"}){
                mvc.perform(get(path).param("q", query).param(parameter, value))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
            }
        }
    }

    @Test
    void albumSearchIncludesAlbumsWithNoTracks() throws Exception {
        mvc.perform(get("/albums").param("q", "Unrelated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].albumId").value(id(4).toString()));
    }

    @ParameterizedTest
    @CsvSource({"0,1,Hello Again", "1,1,Unrelated", "2,0,"})
    void albumTracksArePaginatedWithAlbumScopedTotals(int page, int count, String title) throws Exception {
        var result = mvc.perform(get("/albums/" + id(1) + "/tracks")
                        .param("page", String.valueOf(page)).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.data", hasSize(count)))
                .andExpect(jsonPath("$.pagination.page").value(page))
                .andExpect(jsonPath("$.pagination.size").value(1))
                .andExpect(jsonPath("$.pagination.totalElements").value(2))
                .andExpect(jsonPath("$.pagination.totalPages").value(2));
        if(title != null){
            result.andExpect(jsonPath("$.data[0].title").value(title));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1"})
    void albumTrackPagesDistinguishEmptyAndMissingAlbums(String page) throws Exception {
        mvc.perform(get("/albums/" + id(4) + "/tracks").param("page", page))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.pagination.size").value(20))
                .andExpect(jsonPath("$.pagination.totalElements").value(0))
                .andExpect(jsonPath("$.pagination.totalPages").value(0));
        mvc.perform(get("/albums/" + id(99) + "/tracks").param("page", page))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    private void insertAlbum(int number, String title, String artist, String cover){
        jdbc.update("INSERT INTO album (album_id, album_title, album_author, album_release_date, album_cover_key) " +
                        "VALUES (?, ?, ?, DATE '2026-01-01', ?)", id(number), title, artist, cover);
    }

    private void insertTrack(int number, int albumNumber, String title, String artist){
        jdbc.update("INSERT INTO track (track_id, album_id, track_title, author, track_duration, track_key) " +
                        "VALUES (?, ?, ?, ?, 180000, ?)", id(number), id(albumNumber), title, artist, "audio-" + number);
    }

    private UUID id(int number){
        return new UUID(0, number);
    }
}
