package axo.spotifyclone.spotifyclonebe.exceptions;

public class CustomException extends RuntimeException{
    public CustomException(String msg){
        super(msg);
    }
    public CustomException(String msg, Throwable t){
        super(msg, t);
    }
}
