package fm.mixer.gateway.common.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SortField {

    DATE(Sort.Direction.DESC), NAME(Sort.Direction.ASC), POPULARITY(Sort.Direction.DESC), TREND(Sort.Direction.DESC);

    private final Sort.Direction direction;
}
