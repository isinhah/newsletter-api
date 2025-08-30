package api.newsletter.repository;

import api.newsletter.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    Subscriber findByEmail(String email);
    Subscriber findByVerificationToken(String token);
}