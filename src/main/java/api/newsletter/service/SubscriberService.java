package api.newsletter.service;

import api.newsletter.model.Subscriber;
import api.newsletter.model.SubscriberStatus;
import api.newsletter.repository.SubscriberRepository;
import api.newsletter.web.dto.SubscriberRegisterDto;
import api.newsletter.web.dto.SubscriberResponseDto;
import api.newsletter.web.mapper.SubscriberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public SubscriberResponseDto registerSubscriber(SubscriberRegisterDto registerDto) {
        Subscriber subscriber = subscriberRepository.findByEmail(registerDto.email());

        if (subscriber == null) {
            subscriber = SubscriberMapper.INSTANCE.toEntity(registerDto);
        }

        subscriber.setStatus(SubscriberStatus.PENDING);
        subscriber.setVerified(false);
        subscriber.setVerificationToken(UUID.randomUUID().toString());

        Subscriber savedSubscriber = subscriberRepository.save(subscriber);

//        sendEmailService.sendVerificationEmail(
//                savedSubscriber.getEmail(),
//                savedSubscriber.getVerificationToken()
//        );

        return SubscriberMapper.INSTANCE.toDto(savedSubscriber);
    }

    public void verifyToken(String token) {
         Optional<Subscriber> subscriberOptional = subscriberRepository.findByVerificationToken(token);

         if (subscriberOptional.isPresent()) {
             throw new IllegalArgumentException("Token already exists or it's invalid.");
         }

         Subscriber subscriber = subscriberOptional.get();

         subscriber.setVerified(true);
         subscriber.setStatus(SubscriberStatus.ACTIVE);

         subscriber.setVerificationToken(null);

         subscriberRepository.save(subscriber);
    }

    public void unsubscribe(String token) {
        Optional<Subscriber> subscriberOptional = subscriberRepository.findByVerificationToken(token);

        if (subscriberOptional.isPresent()) {
            throw new IllegalArgumentException("Token is invalid.");
        }

        Subscriber subscriber = subscriberOptional.get();

        subscriber.setVerified(false);
        subscriber.setStatus(SubscriberStatus.UNSUBSCRIBED);

        subscriber.setVerificationToken(null);

        subscriberRepository.save(subscriber);
    }

    public Page<SubscriberResponseDto> getActiveSubscribers(Pageable pageable) {
        Page<Subscriber> activeSubscribersPage = subscriberRepository.findByStatus(SubscriberStatus.ACTIVE, pageable);
        return activeSubscribersPage.map(SubscriberMapper.INSTANCE::toDto);
    }
}
