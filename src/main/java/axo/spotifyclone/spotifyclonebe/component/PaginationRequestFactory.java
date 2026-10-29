package axo.spotifyclone.spotifyclonebe.component;

import axo.spotifyclone.spotifyclonebe.exceptions.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class PaginationRequestFactory {
    public PageRequest create(int page, int size){
        if(size < 1 || size > 20){
            throw new BadRequestException("Size must be between 1 and 20.");
        }
        return PageRequest.of(page, size);
    }
}
