package fm.mixer.gateway.module.search.api;

import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.module.search.api.v1.SearchApiDelegate;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultGroupList;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultList;
import fm.mixer.gateway.module.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController implements SearchApiDelegate {

    private final SearchService service;
    
    @Override
    public ResponseEntity<SearchItemResultGroupList> searchAll(String query, Integer limit, Integer page, List<String> sort) {
        return ResponseEntity.ok(service.searchAll(query, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<SearchItemResultList> searchArtist(String query, Integer limit, Integer page, List<String> sort) {
        return ResponseEntity.ok(service.searchArtist(query, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<SearchItemResultList> searchCollection(String query, Integer limit, Integer page, List<String> sort) {
        return ResponseEntity.ok(service.searchCollection(query, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<SearchItemResultList> searchMix(String query, Integer limit, Integer page, List<String> sort) {
        return ResponseEntity.ok(service.searchMix(query, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<SearchItemResultList> searchTag(String query, Integer limit, Integer page, List<String> sort) {
        return ResponseEntity.ok(service.searchTag(query, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    public ResponseEntity<SearchItemResultList> searchUser(String query, Integer limit, Integer page, List<String> sort) {
        return ResponseEntity.ok(service.searchUser(query, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }
}
