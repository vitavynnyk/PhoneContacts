package phoneApp.repo;

import phoneApp.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
    void deleteByContactUid(String id);
}
