package fm.mixer.gateway.error.service;

import fm.mixer.gateway.error.mapper.ErrorMapper;
import fm.mixer.gateway.model.Error;
import fm.mixer.gateway.model.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorResponseService {

    private final ErrorMapper errorMapper;
    private final MessageSource messageSource;

    public ResponseEntity<Error> createError(final ErrorType type) {
        return createError(type, null);
    }

    public ResponseEntity<Error> createError(final ErrorType type, @Nullable final Object[] parameters) {
        final var messageCode = String.join(".", ErrorType.class.getName(), type.name());
        final var message = getErrorMessage(messageCode, parameters);

        return ResponseEntity.status(errorMapper.toHttpStatus(type)).body(errorMapper.toError(type, message));
    }

    public String getErrorMessage(final String messageName, @Nullable final Object[] parameters) {
        try {
            return messageSource.getMessage(messageName, parameters, LocaleContextHolder.getLocale());
        }
        catch (NoSuchMessageException ignored) {
            return messageName;
        }
    }
}
