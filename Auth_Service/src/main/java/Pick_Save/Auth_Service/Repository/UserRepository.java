package Pick_Save.Auth_Service.Repository;

import Pick_Save.Auth_Service.Model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationCode(String verificationCode);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
