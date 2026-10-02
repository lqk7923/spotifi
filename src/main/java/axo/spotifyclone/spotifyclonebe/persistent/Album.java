package axo.spotifyclone.spotifyclonebe.persistent;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.springframework.data.annotation.CreatedDate;


import java.sql.Time;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class Album {
    @Id
    @Generated @ColumnDefault("uuidv7()")
    private UUID albumId;

    @Column(nullable = false)
    private String albumAuthor;

    @CreatedDate
    @Column(nullable = false)
    @ColumnDefault("now()")
    private Time createdDay;

    @Column(nullable = false)
    private String albumTitle;
}
