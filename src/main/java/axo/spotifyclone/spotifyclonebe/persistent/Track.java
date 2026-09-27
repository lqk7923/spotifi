package axo.spotifyclone.spotifyclonebe.persistent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class Track {

    @Id
    @Generated @ColumnDefault("uuidv7()")
    private UUID trackId;

    @CreatedDate
    @Generated @ColumnDefault("now()")
    private Date releaseDate;

    @Column(nullable = false)
    private String storageName;
}
