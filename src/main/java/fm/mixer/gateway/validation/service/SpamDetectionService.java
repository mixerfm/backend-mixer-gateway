package fm.mixer.gateway.validation.service;

import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.validation.model.SpamVariablePath;
import fm.mixer.gateway.validation.persistance.repository.DisallowedWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpamDetectionService {

    private static final String ERROR_MESSAGE = "%s.contains.spam.error";

    private final DisallowedWordRepository repository;

    public void checkContentForSpam(SpamVariablePath path, String content) {
        if (repository.findInContent(content)) {
            throw new BadRequestException(String.format(ERROR_MESSAGE, path.getPath()));
        }
    }
}
