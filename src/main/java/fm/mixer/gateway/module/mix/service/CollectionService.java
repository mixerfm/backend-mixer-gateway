package fm.mixer.gateway.module.mix.service;

import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.mix.api.v1.model.CollectionList;
import fm.mixer.gateway.module.mix.api.v1.model.SingleCollection;
import fm.mixer.gateway.module.mix.mapper.MixMapper;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollectionLike;
import fm.mixer.gateway.module.mix.persistance.repository.CollectionRepository;
import fm.mixer.gateway.module.mix.persistance.repository.MixCollectionRelationRepository;
import fm.mixer.gateway.module.react.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final MixMapper mapper;
    private final CollectionRepository repository;
    private final MixCollectionRelationRepository mixRepository;
    @Qualifier("mixCollectionReactionService")
    private final ReactionService<MixCollection, MixCollectionLike> reactionService;

    public CollectionList getCollectionList(PaginationRequest collectionPagination, PaginationRequest mixPagination) {
        final var collections = repository.findAll(collectionPagination.pageable());
        final var collectionList = mapper.toCollectionList(collections, collectionPagination);

        // This fetch/logic is not optimized, so we might consider it to hardcode a max number of mixes (constant)
        if (Objects.nonNull(mixPagination)) {
            collections.forEach(collection -> {
                final var mixes = mixRepository.findByCollectionIdOrderByPosition(collection.getId(), mixPagination.pageable());

                collectionList.getCollections().stream().filter(c -> c.getIdentifier().equals(collection.getIdentifier()))
                    .findFirst().ifPresent(c -> c.setMixes(mapper.toMixList(mixes, mixPagination)));
            });
        }

        return collectionList;
    }

    public SingleCollection getSingleCollection(String collectionId, PaginationRequest mixPagination, List<String> filter) {
        final var collection = repository.findByIdentifier(collectionId).orElseThrow(ResourceNotFoundException::new);
        final var singleCollection = mapper.toSingleCollection(collection);

        if (Objects.nonNull(mixPagination)) {
            final var mixes = filter.isEmpty() ?
                mixRepository.findByCollectionIdOrderByPosition(collection.getId(), mixPagination.pageable()) :
                mixRepository.findByCollectionIdAndMixTagsNameInOrderByPosition(
                    collection.getId(), filter, mixPagination.pageable()
                );

            singleCollection.setMixes(mapper.toMixList(mixes, mixPagination));
        }

        return singleCollection;
    }

    public List<UserReaction> react(final String collectionId, final UserReaction.TypeEnum reaction) {
        final var collection = repository.findByIdentifier(collectionId).orElseThrow(ResourceNotFoundException::new);

        return reactionService.react(collection, reaction);
    }

    public List<UserReaction> removeReaction(final String collectionId) {
        final var collection = repository.findByIdentifier(collectionId).orElseThrow(ResourceNotFoundException::new);

        return reactionService.removeReaction(collection);
    }
}
