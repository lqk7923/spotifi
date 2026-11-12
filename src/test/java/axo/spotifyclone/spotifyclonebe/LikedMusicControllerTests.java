package axo.spotifyclone.spotifyclonebe;

import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.PaginationResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.service.LikedMusicService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:liked-music-controller;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;"
                + "INIT=CREATE ALIAS IF NOT EXISTS UUIDV7 FOR \"java.util.UUID.randomUUID\"",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.hbm2ddl.halt_on_error=true",
        "r2-storage.account-id=test", "r2-storage.access-key=test",
        "r2-storage.secret-key=test", "r2-storage.bucket-name=test"
})
@ActiveProfiles("test")
@AutoConfigureMockMvc
class LikedMusicControllerTests {
    @Autowired private MockMvc mvc;
    @MockitoBean private JwtDecoder jwtDecoder;
    @MockitoBean private LikedMusicService likedMusicService;

    @Test
    void defaultsUseAuthenticatedUserAndReturnTrackMetadata() throws Exception {
        authenticate();
        var trackId = new UUID(0, 1);
        var track = TrackDetailsResponse.builder().trackId(trackId).title("Liked song").build();
        when(likedMusicService.listLikedTracks("listener", 0, 20)).thenReturn(
                new PageResponse<>(List.of(track), new PaginationResponse(0, 20, 1, 1)));

        mvc.perform(get("/users/me/liked-music").param("userId", "another-user")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.data[0].trackId").value(trackId.toString()))
                .andExpect(jsonPath("$.data[0].title").value("Liked song"))
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.size").value(20))
                .andExpect(jsonPath("$.pagination.totalElements").value(1))
                .andExpect(jsonPath("$.pagination.totalPages").value(1));
        verify(likedMusicService).listLikedTracks("listener", 0, 20);
        verifyNoMoreInteractions(likedMusicService);
    }

    @Test
    void customPaginationIsPassedToService() throws Exception {
        authenticate();
        when(likedMusicService.listLikedTracks("listener", 2, 5)).thenReturn(
                new PageResponse<>(List.of(), new PaginationResponse(2, 5, 6, 2)));
        mvc.perform(get("/users/me/liked-music").param("page", "2").param("size", "5")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.pagination.page").value(2))
                .andExpect(jsonPath("$.pagination.size").value(5))
                .andExpect(jsonPath("$.pagination.totalElements").value(6));
        verify(likedMusicService).listLikedTracks("listener", 2, 5);
    }

    @ParameterizedTest
    @CsvSource({"page,abc", "size,abc"})
    void malformedPaginationReturnsBadRequest(String parameter, String value) throws Exception {
        authenticate();
        mvc.perform(get("/users/me/liked-music").param(parameter, value)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
        verifyNoInteractions(likedMusicService);
    }

    @Test
    void anonymousRequestsAreRejected() throws Exception {
        mvc.perform(get("/users/me/liked-music"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
        verifyNoInteractions(likedMusicService);
    }

    @Test
    void invalidTokensAreRejected() throws Exception {
        when(jwtDecoder.decode("invalid")).thenThrow(new BadJwtException("Invalid token"));
        mvc.perform(get("/users/me/liked-music").header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(likedMusicService);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void patchSetsTrackLikeForAuthenticatedUser(boolean liked) throws Exception {
        authenticate();
        var trackId = new UUID(0, 1);

        mvc.perform(patch("/users/me/liked-music/{trackId}", trackId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"liked\":" + liked + ",\"userId\":\"another-user\"}"))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Cache-Control", "no-store"));

        verify(likedMusicService).setTrackLiked("listener", trackId, liked);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "invalid-uuid | {\"liked\":true}",
            "00000000-0000-0000-0000-000000000001 | {}",
            "00000000-0000-0000-0000-000000000001 | {\"liked\":null}"
    })
    void patchRejectsInvalidInput(String trackId, String body) throws Exception {
        authenticate();

        mvc.perform(patch("/users/me/liked-music/{trackId}", trackId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
        verifyNoInteractions(likedMusicService);
    }

    @Test
    void patchRequiresAuthentication() throws Exception {
        mvc.perform(patch("/users/me/liked-music/{trackId}", new UUID(0, 1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"liked\":true}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(likedMusicService);
    }

    private void authenticate(){
        when(jwtDecoder.decode("token")).thenReturn(Jwt.withTokenValue("token")
                .header("alg", "RS256").subject("listener").build());
    }
}
