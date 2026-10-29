package axo.spotifyclone.spotifyclonebe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {
    private ErrorMessage error;
}
