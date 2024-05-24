package fm.mixer.gateway.config.feign;

import feign.FeignException;
import fm.mixer.gateway.error.exception.ExternalServiceException;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.error.exception.ServiceUnavailableException;
import fm.mixer.gateway.error.exception.TooManyRequestsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class LambdaClientFallbackFactory<T> implements FallbackFactory<T> {

    private static final Set<Class<? extends Exception>> retryableExceptions = Set.of(feign.RetryableException.class, TimeoutException.class, FeignException.ServiceUnavailable.class);

    @Override
    public T create(Throwable cause) {
        log.error(cause.getMessage(), cause);

        // FeignException is wrapped in ExecutionException
        final var exception = cause instanceof ExecutionException ? cause.getCause() : cause;

        if (retryableExceptions.stream().anyMatch(e -> e.equals(cause.getClass()))) {
            if (cause.getCause() instanceof FeignException.TooManyRequests) {
                throw new TooManyRequestsException();
            }

            throw new ServiceUnavailableException();
        }

        if (exception instanceof FeignException.NotFound) {
            throw new ResourceNotFoundException();
        }

        throw new ExternalServiceException();
    }
}
