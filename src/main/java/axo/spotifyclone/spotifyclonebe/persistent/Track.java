package axo.spotifyclone.spotifyclonebe.persistent;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.UUID;

@Table(
        indexes = {
                @Index(name = "idx_track_album_id", columnList = "album_id")
        }
)
@Data
@NoArgsConstructor
@Entity
public class Track {

    @Id
    @Generated @ColumnDefault("uuidv7()")
    private UUID trackId;

    @Column(nullable = false)
    private String trackKey;

    @Column(nullable = false)
    private String trackTitle;

    @Column(nullable = false)
    private Long trackDuration;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private UUID albumId;
}
