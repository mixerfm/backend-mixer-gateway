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

        var exception = cause;
        if (cause instanceof ExecutionException) {
            // FeignException is wrapped in ExecutionException
            exception = cause.getCause();
        }

        if (retryableExceptions.stream().anyMatch(e -> e.equals(cause.getClass()))) {
            if (cause.getCause() instanceof FeignException.TooManyRequests) {
                throw new TooManyRequestsException(this.getClass().getSimpleName());
            }
            else {
                throw new ServiceUnavailableException(this.getClass().getSimpleName());
            }
        }

        if (exception instanceof FeignException.NotFound) {
            throw new ResourceNotFoundException("default.not_found");
        }

        throw new ExternalServiceException(this.getClass().getSimpleName());
    }
}
