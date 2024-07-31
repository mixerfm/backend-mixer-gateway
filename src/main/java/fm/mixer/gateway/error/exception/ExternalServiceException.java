package fm.mixer.gateway.error.exception;

public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException() {
        super();
    }
}
