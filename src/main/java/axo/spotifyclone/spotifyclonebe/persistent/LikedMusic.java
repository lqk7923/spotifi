package axo.spotifyclone.spotifyclonebe.persistent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.util.UUID;

@Entity
@Table(indexes = {
        @Index(name = "idx_liked_music_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
public class LikedMusic {
    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String userId;

    @Column(nullable = false)
    private UUID trackId;
}
