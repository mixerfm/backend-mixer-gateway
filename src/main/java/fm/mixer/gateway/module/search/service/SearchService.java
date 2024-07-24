package fm.mixer.gateway.module.search.service;

import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultGroupList;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultGroupType;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultList;
import fm.mixer.gateway.module.search.mapper.SearchMapper;
import fm.mixer.gateway.module.search.persistance.repository.SearchArtistRepository;
import fm.mixer.gateway.module.search.persistance.repository.SearchCollectionRepository;
import fm.mixer.gateway.module.search.persistance.repository.SearchMixRepository;
import fm.mixer.gateway.module.search.persistance.repository.SearchTagRepository;
import fm.mixer.gateway.module.search.persistance.repository.SearchUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchMapper mapper;
    private final SearchMixRepository mixRepository;
    private final SearchTagRepository tagRepository;
    private final SearchUserRepository userRepository;
    private final SearchArtistRepository artistRepository;
    private final SearchCollectionRepository collectionRepository;

    public SearchItemResultGroupList searchAll(String query, PaginationRequest paginationRequest) {
        return mapper.toSearchItemResultGroupList(new TreeMap<>(Map.of(
            SearchItemResultGroupType.ARTIST, searchArtist(query, paginationRequest),
            SearchItemResultGroupType.COLLECTION, searchCollection(query, paginationRequest),
            SearchItemResultGroupType.MIX, searchMix(query, paginationRequest),
            SearchItemResultGroupType.TAG, searchTag(query, paginationRequest),
            SearchItemResultGroupType.USER, searchUser(query, paginationRequest)
        )));
    }

    public SearchItemResultList searchArtist(String query, PaginationRequest paginationRequest) {
        return mapper.toSearchItemResultListFromArtist(
            artistRepository.search(query, paginationRequest.pageable()),
            paginationRequest
        );
    }

    public SearchItemResultList searchCollection(String query, PaginationRequest paginationRequest) {
        return mapper.toSearchItemResultListFromCollection(
            collectionRepository.search(query, paginationRequest.pageable()),
            paginationRequest
        );
    }

    public SearchItemResultList searchMix(String query, PaginationRequest paginationRequest) {
        return mapper.toSearchItemResultListFromMix(
            mixRepository.search(query, paginationRequest.pageable()),
            paginationRequest
        );
    }

    public SearchItemResultList searchTag(String query, PaginationRequest paginationRequest) {
        return mapper.toSearchItemResultListFromTag(
            tagRepository.search(query, paginationRequest.pageable()),
            paginationRequest
        );
    }

    public SearchItemResultList searchUser(String query, PaginationRequest paginationRequest) {
        return mapper.toSearchItemResultListFromUser(
            userRepository.search(query, paginationRequest.pageable()),
            paginationRequest
        );
    }
}
