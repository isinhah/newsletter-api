package api.newsletter.web.controller;

import api.newsletter.model.SubscriberStatus;
import api.newsletter.service.SubscriberService;
import api.newsletter.web.dto.SubscriberRegisterDto;
import api.newsletter.web.dto.SubscriberResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {

    private final SubscriberService subscriberService;

    @GetMapping
    public ResponseEntity<Page<SubscriberResponseDto>> getSubscribersByStatus(@RequestParam String status, Pageable pageable) {
        Page<SubscriberResponseDto> subscribers = subscriberService.getSubscribersByStatus(status, pageable);
        return ResponseEntity.ok(subscribers);
    }

    @PostMapping
    public ResponseEntity<SubscriberResponseDto> registerSubscriber(@Valid @RequestBody SubscriberRegisterDto subscriberRegisterDto) {
        SubscriberResponseDto response = subscriberService.registerSubscriber(subscriberRegisterDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/unsubscribe/{token}")
    public ResponseEntity<String> unsubscribeSubscriber(@PathVariable String token) {
        try {
            subscriberService.unsubscribe(token);
            return ResponseEntity.ok("Subscriber unsubscribed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
