package axo.spotifyclone.spotifyclonebe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {
    private String code;
    private String message;
}
