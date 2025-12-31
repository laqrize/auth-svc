package pl.ros.authsvc.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    @Query("SELECT e FROM AppUser e WHERE (e.email = ?1 or e.username= ?2) AND e.status = 'C'")
    List<AppUser> findActualByEmailOrUsername(String email, String username);

    @Query("SELECT e FROM AppUser e WHERE e.email = ?1 AND e.status = 'C'")
    Optional<AppUser> findActualByEmail(String email);

    List<AppUser> findAllByStatusIn(List<String> statusList);

}
