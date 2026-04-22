package levelup42.trivia.domain.exception;

public class InvalidGoogleTokenException extends DomainLogicException {
    public InvalidGoogleTokenException(String message) {
        super(message);
    }
}
