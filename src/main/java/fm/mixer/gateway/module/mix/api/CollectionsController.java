package fm.mixer.gateway.module.mix.api;

import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.mix.api.v1.CollectionsApiDelegate;
import fm.mixer.gateway.module.mix.api.v1.model.CollectionList;
import fm.mixer.gateway.module.mix.api.v1.model.SingleCollection;
import fm.mixer.gateway.module.mix.service.CollectionService;
import fm.mixer.gateway.module.react.model.ResourceType;
import fm.mixer.gateway.module.react.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class CollectionsController implements CollectionsApiDelegate {

    private final CollectionService service;
    private final ReportService reportService;

    @Override
    public ResponseEntity<CollectionList> getCollectionList(Integer limit, Integer page, List<String> sort, Integer mixCount, List<String> mixSort) {
        final var collectionPagination = PaginationMapper.toPaginationRequest(limit, page, sort);
        // Set mix pagination only if client send mixCount
        final var mixPagination = (Objects.isNull(mixCount) || mixCount == 0) ? null :
            PaginationMapper.toPaginationRequest(mixCount, 1, mixSort);

        return ResponseEntity.ok(service.getCollectionList(collectionPagination, mixPagination));
    }

    @Override
    public ResponseEntity<SingleCollection> getSingleCollection(String collectionId, List<String> filter, Integer limit, Integer page, List<String> sort) {
        final var mixPagination = PaginationMapper.toPaginationRequest(limit, page, sort);

        return ResponseEntity.ok(service.getSingleCollection(collectionId, mixPagination, filter));
    }

    @Override
    public ResponseEntity<List<UserReaction>> react(String collectionId, UserReaction userReaction) {
        if (UserReaction.TypeEnum.REPORT.equals(userReaction.getType())) {
            reportService.report(collectionId, ResourceType.COLLECTIONS);

            return ResponseEntity.ok(List.of());
        }

        checkReactionType(userReaction.getType());
        return ResponseEntity.ok(service.react(collectionId, UserReaction.TypeEnum.LIKE.equals(userReaction.getType())));
    }

    @Override
    public ResponseEntity<List<UserReaction>> removeReaction(String collectionId) {
        return ResponseEntity.ok(service.removeReaction(collectionId));
    }

    private void checkReactionType(UserReaction.TypeEnum type) {
        if (!List.of(UserReaction.TypeEnum.LIKE, UserReaction.TypeEnum.DISLIKE).contains(type)) {
            throw new BadRequestException("reaction.type.not.supported.error");
        }
    }
}
