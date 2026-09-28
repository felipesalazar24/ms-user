package cl.sanos_y_salvos.ms_base.api.repository;

import cl.sanos_y_salvos.ms_base.api.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
