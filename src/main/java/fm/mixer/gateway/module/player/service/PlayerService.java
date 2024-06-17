package fm.mixer.gateway.module.player.service;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.module.mix.persistance.repository.MixRepository;
import fm.mixer.gateway.module.mix.persistance.repository.MixTrackLikeRepository;
import fm.mixer.gateway.module.mix.persistance.repository.MixTrackRepository;
import fm.mixer.gateway.module.player.api.v1.model.Session;
import fm.mixer.gateway.module.player.api.v1.model.Track;
import fm.mixer.gateway.module.player.api.v1.model.TrackList;
import fm.mixer.gateway.module.player.api.v1.model.VolumeValue;
import fm.mixer.gateway.module.player.mapper.PlayerMapper;
import fm.mixer.gateway.module.player.persistance.entity.PlaySession;
import fm.mixer.gateway.module.player.persistance.repository.PlaySessionRepository;
import fm.mixer.gateway.module.user.persistance.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerMapper mapper;
    private final PlaySessionRepository repository;

    private final MixRepository mixRepository;
    private final MixTrackRepository trackRepository;
    private final MixTrackLikeRepository likeRepository;

    public Session getPlayerSession() {
        final var user = getCurrentUser();
        final var session = repository.getCurrentActiveSessionForUser(user).orElseThrow(ResourceNotFoundException::new);

        return mapper.toSession(session);
    }

    public TrackList getTrackList(String mixId) {
        final var user = getCurrentUser();
        final var session = repository.findByUserAndMixIdentifier(user, mixId);

        // User did not play this mix
        if (session.isEmpty()) {
            return mapper.toTrackListObject(List.of());
        }

        final var allTracks = Arrays.stream(session.get().getTracks().split(mapper.TRACK_DELIMITER)).map(Long::valueOf).toList();
        final var listenedTracks = trackRepository.findAllById(
            allTracks.subList(0, allTracks.indexOf(session.get().getTrack().getId()) + 1)
        );

        return mapper.toTrackListObject(listenedTracks);
    }

    public Track playTrack(String mixId) {
        final var user = getCurrentUser();
        final var session = repository.findByUserAndMixIdentifier(user, mixId).orElse(new PlaySession());

        // There is an active session
        if (!session.getShuffle() && Objects.nonNull(session.getMix()) && session.getMix().getIdentifier().equals(mixId)) {
            return mapper.toTrack(session.getTrack());
        }

        // User is first time playing this mix or mix must be shuffled
        final var mix = mixRepository.findByIdentifier(mixId).orElseThrow(ResourceNotFoundException::new);

        mapper.toPlaySession(session, user, mix, mix.getTracks().getFirst());

        repository.save(session);

        return mapper.toTrack(session.getTrack());
    }

    public void pauseTrack(String mixId) {
        // TODO implement user tracking service
    }

    public Track skipTrack(String mixId) {
        // TODO implement user tracking service
        return nextTrack(mixId);
    }

    public Track nextTrack(String mixId) {
        final var user = getCurrentUser();
        final var session = getCurrentPlaySession(user, mixId);
        final var tracks = Arrays.stream(session.getTracks().split(mapper.TRACK_DELIMITER)).map(Long::valueOf).toList();

        final var nextIndex = tracks.indexOf(session.getTrack().getId()) + 1;
        // No more tracks in the list
        if (nextIndex >= tracks.size()) {
            session.setShuffle(true);

            repository.save(session);

            throw new ResourceNotFoundException();
        }

        final var track = trackRepository.findById(tracks.get(nextIndex)).orElseThrow(ResourceNotFoundException::new);

        mapper.toPlaySession(session, user, session.getMix(), track);

        repository.save(session);

        return mapper.toTrack(session.getTrack());
    }

    public void changeVolume(String mixId, VolumeValue volumeValue) {
        // TODO implement user tracking service
    }

    public void setLikeFlag(final String mixId, final boolean like) {
        final var user = getCurrentUser();
        final var track = getCurrentPlaySession(user, mixId).getTrack();

        // Find old record and change "liked" flag. If there is no old record create new object with mapper and store it.
        likeRepository.save(
            likeRepository.findByUserAndTrack(user, track).map(likeRecord -> {
                likeRecord.setLiked(like);
                return likeRecord;
            }).orElse(mapper.toMixTrackLike(user, track, like))
        );
    }

    public void setRecommendedFlag(final String mixId, final boolean flag) {
        // TODO implement user tracking service
    }

    public void reportTrack(String mixId) {
        // TODO implement
    }

    private User getCurrentUser() {
        return UserPrincipalUtil.getCurrentActiveUser().orElseThrow(AccessForbiddenException::new);
    }

    private PlaySession getCurrentPlaySession(final User user, final String mixId) {
        return repository.findByUserAndMixIdentifier(user, mixId).orElseThrow(() -> new BadRequestException("no.active.session.error"));
    }
}
