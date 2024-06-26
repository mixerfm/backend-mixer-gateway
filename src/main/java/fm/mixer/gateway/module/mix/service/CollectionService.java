package fm.mixer.gateway.module.mix.service;

import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.module.mix.api.v1.model.CollectionList;
import fm.mixer.gateway.module.mix.api.v1.model.SingleCollection;
import fm.mixer.gateway.module.mix.mapper.MixMapper;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import fm.mixer.gateway.module.mix.persistance.repository.CollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final MixMapper mapper;
    private final CollectionRepository repository;

    public CollectionList getCollectionList(PaginationRequest collectionPagination, PaginationRequest mixPagination) {
        return mapper.mapToCollectionList(fetchMixCollections(Objects.nonNull(mixPagination), collectionPagination.pageable()), collectionPagination);
    }

    private Page<MixCollection> fetchMixCollections(final boolean fetchMixes, Pageable pageable) {
        if (!fetchMixes) {
            final var collectionList = repository.findAllWithoutMixes(pageable);

            collectionList.stream().forEach(collection -> collection.setMixes(List.of()));

            return collectionList;
        }

        return repository.findAllWithMixes(pageable);
    }

    public SingleCollection getSingleCollection(String collectionId, PaginationRequest mixPagination, List<String> filter) {
        return mapper.mapToSingleCollection(repository.findFilteredByIdentifier(collectionId, filter));
    }

    public void react(String collectionId, boolean like) {
        // TODO implement
    }

    public void removeReaction(String collectionId) {
        // TODO implement
    }
}
