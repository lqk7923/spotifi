package axo.spotifyclone.spotifyclonebe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class SpotifyCloneBeApplication {

    public static void main(String[] args) {
        //uncomment this or add -Duser.timezone="Asia/Ho_Chi_Minh" to VM arguments
        //TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SpringApplication.run(SpotifyCloneBeApplication.class, args);
    }

}
