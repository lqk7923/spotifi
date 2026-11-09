package axo.spotifyclone.spotifyclonebe.persistent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_user")
@Getter
@NoArgsConstructor
public class AppUser {
    @Id
    @Column(length = 255, nullable = false)
    private String id;

    @Column(columnDefinition = "text")
    private String username;

    @Column(columnDefinition = "text")
    private String email;
}
