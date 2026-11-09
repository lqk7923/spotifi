package axo.spotifyclone.spotifyclonebe.component;

import org.springframework.stereotype.Component;

@Component
public class SearchQueryFactory {
    public String createPattern(String query){
        if(query == null || query.isBlank()){
            return "";
        }
        String escapedQuery = query.strip()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + escapedQuery + "%";
    }
}
