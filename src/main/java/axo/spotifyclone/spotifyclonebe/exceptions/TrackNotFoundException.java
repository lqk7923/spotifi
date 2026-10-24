package axo.spotifyclone.spotifyclonebe.exceptions;

import java.util.UUID;

public class TrackNotFoundException extends RuntimeException{
    public TrackNotFoundException(String id){
        super(id + " not found");
    }
    public TrackNotFoundException(UUID id){
        super(id.toString() + " not found");
    }
}
