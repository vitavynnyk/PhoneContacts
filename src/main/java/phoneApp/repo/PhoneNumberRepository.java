package phoneApp.repo;

import phoneApp.entity.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Long> {
    void deleteByContactUid(String id);
}
