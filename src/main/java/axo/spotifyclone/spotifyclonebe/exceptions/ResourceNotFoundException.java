package axo.spotifyclone.spotifyclonebe.exceptions;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String id){
        super(id + " not found");
    }
    public ResourceNotFoundException(UUID id){
        super(id.toString() + " not found");
    }
}
