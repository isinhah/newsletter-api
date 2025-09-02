package api.newsletter.messaging;

public record VerificationEmailDto(
        String email,
        String verificationToken
) {
}
