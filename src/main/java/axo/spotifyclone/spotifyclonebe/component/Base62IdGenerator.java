package axo.spotifyclone.spotifyclonebe.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;

//@Component
public class Base62IdGenerator {

    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final SecureRandom random = new SecureRandom();

    private final Integer idLength;

    public Base62IdGenerator(@Value("${public-id-length}") Integer idLength){
        this.idLength = idLength;
    }

    public String generate() {
        char[] id = new char[this.idLength];

        for (int i = 0; i < this.idLength; i++) {
            id[i] = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
        }

        return new String(id);
    }

}
