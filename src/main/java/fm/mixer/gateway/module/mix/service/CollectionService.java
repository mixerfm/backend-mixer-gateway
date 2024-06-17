package fm.mixer.gateway.module.mix.service;

import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.common.model.SortField;
import fm.mixer.gateway.module.mix.api.v1.model.CollectionList;
import fm.mixer.gateway.module.mix.api.v1.model.SingleCollection;
import fm.mixer.gateway.module.mix.mapper.MixMapper;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import fm.mixer.gateway.module.mix.persistance.repository.CollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final MixMapper mapper;
    private final CollectionRepository repository;

    public CollectionList getCollectionList(PaginationRequest collectionPagination, Integer mixCount) {
        return mapper.mapToCollectionList(fetchMixCollections(mixCount, collectionPagination.pageable()), collectionPagination);
    }

    private Page<MixCollection> fetchMixCollections(Integer mixCount, Pageable pageable) {
        if (Objects.isNull(mixCount) || mixCount < 1) {
            final var collectionList = repository.findAllWithoutMixes(pageable);

            collectionList.stream().forEach(collection -> collection.setMixes(List.of()));

            return collectionList;
        }

        return repository.findAllWithMixes(pageable);
    }

    public SingleCollection getSingleCollection(String collectionId, Map<SortField, Sort.Direction> sort, List<String> filter) {
        return mapper.mapToSingleCollection(repository.findFilteredByIdentifier(collectionId, filter));
    }

    public void setLikeFlag(String collectionId, boolean like) {
        // TODO implement
    }

    public void reportCollection(String collectionId) {
        // TODO implement
    }
}
