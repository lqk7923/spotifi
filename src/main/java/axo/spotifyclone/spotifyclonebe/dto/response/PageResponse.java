package axo.spotifyclone.spotifyclonebe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> data;
    private PaginationResponse pagination;

    public static <T> PageResponse<T> from(Page<T> page){
        return new PageResponse<>(page.getContent(), new PaginationResponse(
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }
}
