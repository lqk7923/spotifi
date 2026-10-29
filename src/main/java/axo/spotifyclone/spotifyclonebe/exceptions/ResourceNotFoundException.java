package axo.spotifyclone.spotifyclonebe.exceptions;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }

    public ResourceNotFoundException(UUID id){
        super(id.toString() + " not found");
    }

    public ResourceNotFoundException(String resource, UUID id){
        super(resource + " " + id + " not found");
    }
}
