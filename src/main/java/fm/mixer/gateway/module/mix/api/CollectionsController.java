package fm.mixer.gateway.module.mix.api;

import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.module.mix.api.v1.CollectionsApiDelegate;
import fm.mixer.gateway.module.mix.api.v1.model.CollectionList;
import fm.mixer.gateway.module.mix.api.v1.model.SingleCollection;
import fm.mixer.gateway.module.mix.service.CollectionService;
import fm.mixer.gateway.validation.annotation.OpenApiValidation;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CollectionsController implements CollectionsApiDelegate {

    private final CollectionService service;

    @Override
    @OpenApiValidation
    public ResponseEntity<CollectionList> getCollectionList(List<@Pattern(regexp = "^(date|name|popularity|trend)(:(asc|desc))?$") String> sort, Integer limit, Integer page, Integer mixCount, List<@Pattern(regexp = "^(date|name|popularity|trend)(:(asc|desc))?$") String> mixSort) {
        final var collectionPagination = PaginationMapper.toPaginationRequest(limit, page, sort);
        //TODO final var mixPagination = PaginationMapper.toPaginationRequest(mixCount, 1, mixSort); -- check if 0

        return ResponseEntity.ok(service.getCollectionList(collectionPagination));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<SingleCollection> getSingleCollection(String collectionId, List<@Pattern(regexp = "^(date|name|popularity|trend)(:(asc|desc))?$") String> sort, List<String> filter) {
        return ResponseEntity.ok(service.getSingleCollection(collectionId, PaginationMapper.toSortDirections(sort), filter));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> likeCollection(String collectionId) {
        service.setLikeFlag(collectionId, true);

        return ResponseEntity.noContent().build();
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> dislikeCollection(String collectionId) {
        service.setLikeFlag(collectionId, false);

        return ResponseEntity.noContent().build();
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> reportCollection(String collectionId) {
        service.reportCollection(collectionId);

        return ResponseEntity.noContent().build();
    }
}
