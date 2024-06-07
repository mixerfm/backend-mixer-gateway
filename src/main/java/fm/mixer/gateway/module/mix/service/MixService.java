package fm.mixer.gateway.module.mix.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.module.mix.api.v1.model.SingleMix;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixes;
import fm.mixer.gateway.module.mix.mapper.MixMapper;
import fm.mixer.gateway.module.mix.persistance.repository.MixLikeRepository;
import fm.mixer.gateway.module.mix.persistance.repository.MixRepository;
import fm.mixer.gateway.module.play.persistance.repository.PlaySessionHistoryRepository;
import fm.mixer.gateway.module.user.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MixService {

    private final MixMapper mapper;
    private final MixRepository repository;
    private final UserRepository userRepository;
    private final MixLikeRepository likeRepository;
    private final PlaySessionHistoryRepository historyRepository;

    public SingleMix getSingleMix(String mixId) {
        final var mix = repository.findByIdentifier(mixId).orElseThrow(ResourceNotFoundException::new);

        return mapper.toSingleMix(mix);
    }

    public UserLikedMixes getUserLikedMixes(String username, PaginationRequest pagination) {
        final var user = userRepository.findByIdentifierAndActiveIsTrue(username).orElseThrow(ResourceNotFoundException::new);
        final var likedMixes = likeRepository.findByUserAndLikedIsTrue(user, pagination.pageable());

        return mapper.toUserLikedMixes(likedMixes, pagination);
    }

    public UserListenedMixes getUserMixesHistory(String username, PaginationRequest pagination) {
        final var user = userRepository.findByIdentifierAndActiveIsTrue(username).orElseThrow(ResourceNotFoundException::new);
        final var listenedMixes = historyRepository.findByUser(user, pagination.pageable());

        return mapper.toUserListenedMixes(listenedMixes, pagination);
    }

    public UserUploadedMixes getUserUploadedMixes(String username, PaginationRequest pagination) {
        final var user = userRepository.findByIdentifierAndActiveIsTrue(username).orElseThrow(ResourceNotFoundException::new);

        return mapper.toUserUploadedMixes(repository.findAllByUser(user, pagination.pageable()), pagination);
    }

    public void setLikeFlag(final String mixId, final boolean like) {
        final var mix = repository.findByIdentifier(mixId).orElseThrow(ResourceNotFoundException::new);
        final var user = UserPrincipalUtil.getCurrentActiveUser().orElseThrow(AccessForbiddenException::new);

        // Find old record and change "liked" flag. If there is no old record create new object with mapper and store it.
        likeRepository.save(
            likeRepository.findByUserAndMix(user, mix).map(likeRecord -> {
                likeRecord.setLiked(like);
                return likeRecord;
            }).orElse(mapper.toMixLike(user, mix, like))
        );
    }

    public void reportMix(String mixId) {
        // TODO implement
    }
}
