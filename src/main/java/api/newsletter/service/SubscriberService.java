package api.newsletter.service;

import api.newsletter.messaging.EmailProducer;
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
    private final EmailProducer emailProducer;

    public Page<SubscriberResponseDto> getSubscribersByStatus(String status, Pageable pageable) {
        if (status == null || status.isBlank()) {
            Page<Subscriber> allSubscribers = subscriberRepository.findAll(pageable);
            return allSubscribers.map(SubscriberMapper.INSTANCE::toDto);
        }

        SubscriberStatus subscriberStatus;
        try {
            subscriberStatus = SubscriberStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }

        Page<Subscriber> subscribers = subscriberRepository.findByStatus(subscriberStatus, pageable);
        return subscribers.map(SubscriberMapper.INSTANCE::toDto);
    }

    public SubscriberResponseDto registerSubscriber(SubscriberRegisterDto registerDto) {
        Subscriber subscriber = subscriberRepository.findByEmail(registerDto.email());

        if (subscriber == null) {
            subscriber = SubscriberMapper.INSTANCE.toEntity(registerDto);
        }

        subscriber.setStatus(SubscriberStatus.PENDING);
        subscriber.setVerified(false);
        subscriber.setVerificationToken(UUID.randomUUID().toString());

        Subscriber savedSubscriber = subscriberRepository.save(subscriber);

        emailProducer.publishVerificationEmail(savedSubscriber);

        return SubscriberMapper.INSTANCE.toDto(savedSubscriber);
    }

    public void verifyToken(String token) {
         Optional<Subscriber> subscriberOptional = subscriberRepository.findByVerificationToken(token);

         if (subscriberOptional.isEmpty()) {
             throw new IllegalArgumentException("Invalid token or it has already been used.");
         }

         Subscriber subscriber = subscriberOptional.get();

         subscriber.setVerified(true);
         subscriber.setStatus(SubscriberStatus.ACTIVE);
         subscriber.setVerificationToken(null);

         subscriberRepository.save(subscriber);
    }

    public void unsubscribe(String token) {
        Optional<Subscriber> subscriberOptional = subscriberRepository.findByVerificationToken(token);

        if (subscriberOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid token.");
        }

        Subscriber subscriber = subscriberOptional.get();

        subscriber.setVerified(false);
        subscriber.setStatus(SubscriberStatus.UNSUBSCRIBED);
        subscriber.setVerificationToken(null);

        subscriberRepository.save(subscriber);
    }
}