package fm.mixer.gateway.module.user.service;

import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.module.user.mapper.UserNewsletterMapper;
import fm.mixer.gateway.module.user.persistance.entity.UserNewsletter;
import fm.mixer.gateway.module.user.persistance.repository.UserNewsletterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final UserNewsletterMapper mapper;
    private final UserNewsletterRepository repository;

    public UserNewsletter subscribe(String email) {
        final var newsletter = repository.findByEmail(email);
        //noinspection OptionalIsPresent
        if (newsletter.isPresent()) {
            return newsletter.get();
        }

        return repository.save(mapper.toUserNewsletterEntity(email));
    }

    public void unsubscribe(UUID token, String email) {
        repository.delete(
            repository.findByIdentifierAndEmail(token.toString(), email).orElseThrow(ResourceNotFoundException::new)
        );
    }
}
