package fm.mixer.gateway.common.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

public record PaginationRequest(Map<SortField, Sort.Direction> sort, Pageable pageable) {

    public Pageable toPageable(Map<SortField, String> columnMapping) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(toOrders(columnMapping)));
    }

    private List<Sort.Order> toOrders(Map<SortField, String> columnMapping) {
        return sort.entrySet().stream()
            .filter(entry -> columnMapping.containsKey(entry.getKey())) // Resource does not define that sort algorithm
            .map((entry) -> switch (entry.getValue()) {
                case DESC -> Sort.Order.desc(columnMapping.get(entry.getKey()));
                case ASC -> Sort.Order.asc(columnMapping.get(entry.getKey()));
            }).toList();
    }
}
