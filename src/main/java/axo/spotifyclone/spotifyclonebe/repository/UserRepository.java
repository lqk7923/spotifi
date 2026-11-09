package axo.spotifyclone.spotifyclonebe.repository;

import axo.spotifyclone.spotifyclonebe.persistent.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<AppUser, String> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            insert into app_user (id, username, email)
            values (:id, :username, :email)
            on conflict do nothing
            """, nativeQuery = true)
    void insertIfAbsent(@Param("id") String id, @Param("username") String username, @Param("email") String email);
}
