package fm.mixer.gateway.common.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

public record PaginationRequest(Map<SortField, Sort.Direction> sort, Pageable pageable) {
}
