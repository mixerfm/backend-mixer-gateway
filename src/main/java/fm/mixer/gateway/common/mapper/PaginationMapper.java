package fm.mixer.gateway.common.mapper;

import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.common.model.SortField;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.model.PaginationMetadata;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class PaginationMapper {

    public static Pageable toPageable(Integer limit, Integer page) {
        return PageRequest.of(page - 1, limit);
    }

    public static Map<SortField, Sort.Direction> toSortDirections(List<String> sortList) {
        final var sortDirections = new HashMap<SortField, Sort.Direction>();

        for (final var sort : sortList) {
            final var request = sort.toUpperCase().split(":");

            final var field = SortField.valueOf(request[0]);
            final var direction = request.length > 1 ? Sort.Direction.valueOf(request[1]) : field.getDirection();

            if (sortDirections.containsKey(field)) {
                throw new BadRequestException("sort.field.duplicated.error");
            }

            sortDirections.put(field, direction);
        }

        return sortDirections;
    }

    public static PaginationRequest toPaginationRequest(Integer limit, Integer page, List<String> sortList) {
        return new PaginationRequest(toSortDirections(sortList), toPageable(limit, page));
    }

    public static PaginationMetadata toPaginationMetadata(Page<?> items, PaginationRequest paginationRequest) {
        final var requestedPageNumber = paginationRequest.pageable().getPageNumber() + 1;

        final var paginationMetadata = new PaginationMetadata()
            .totalItems(items.getTotalElements())
            .totalPages(items.getTotalPages());

        // Collection for requested items is empty
        if (items.getTotalElements() == 0) {
            paginationMetadata.setCurrentPage(0);

            return paginationMetadata;
        }
        paginationMetadata.setCurrentPage(requestedPageNumber);

        // Requested page number is out of max value
        if (requestedPageNumber > items.getTotalPages()) {
            return paginationMetadata.previousPage(items.getTotalPages());
        }

        // Set next and previous page
        if (requestedPageNumber - 1 >= 1) {
            paginationMetadata.setPreviousPage(requestedPageNumber - 1);
        }
        if (requestedPageNumber + 1 <= items.getTotalPages()) {
            paginationMetadata.setNextPage(requestedPageNumber + 1);
        }

        return paginationMetadata;
    }
}
