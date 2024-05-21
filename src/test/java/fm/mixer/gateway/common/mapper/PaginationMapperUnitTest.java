package fm.mixer.gateway.common.mapper;

import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.common.model.SortField;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.module.mix.api.v1.model.PaginationMetadata;
import fm.mixer.gateway.test.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
class PaginationMapperUnitTest {

    @Test
    void shouldMapToPageable() {
        // Given
        final var limit = 1;
        final var page = 5;

        // When
        final var pageable = PaginationMapper.toPageable(limit, page);

        // Then
        assertThat(pageable.getPageNumber()).isEqualTo(page - 1);
        assertThat(pageable.getPageSize()).isEqualTo(limit);
    }

    @ParameterizedTest
    @EnumSource(SortField.class)
    void shouldMapToSortDirections(SortField sortField) {
        // Given
        final var sortList = List.of(sortField.name().toLowerCase() + ":asc");

        // When
        final var sortDirections = PaginationMapper.toSortDirections(sortList);

        // Then
        assertThat(sortDirections).containsOnly(entry(sortField, Sort.Direction.ASC));
    }

    @ParameterizedTest
    @EnumSource(SortField.class)
    void shouldMapToDefaultSortDirections(SortField sortField) {
        // Given
        final var sortList = List.of(sortField.name());

        // When
        final var sortDirections = PaginationMapper.toSortDirections(sortList);

        // Then
        assertThat(sortDirections).containsOnly(entry(sortField, sortField.getDirection()));
    }

    @Test
    void shouldThrowExceptionWhenMapToSortDirections() {
        // Given
        final var sortList = List.of(SortField.DATE.name(), SortField.DATE.name() + ":desc");

        // When
        final var exception = catchThrowable(() -> PaginationMapper.toSortDirections(sortList));

        // Then
        assertThat(exception).isInstanceOf(BadRequestException.class);
        assertThat(((BadRequestException )exception).getMessageCode()).isEqualTo("sort.field.duplicated.error");
    }

    @Test
    void shouldMapToPaginationRequest() {
        // Given
        final var limit = 1;
        final var page = 5;
        final var sortList = List.of(SortField.DATE.name());

        // When
        final var pageable = PaginationMapper.toPaginationRequest(limit, page, sortList);

        // Then
        assertThat(pageable.pageable().getPageSize()).isEqualTo(limit);
        assertThat(pageable.pageable().getPageNumber()).isEqualTo(page - 1);
        assertThat(pageable.sort()).containsOnly(entry(SortField.DATE, SortField.DATE.getDirection()));
    }

    @ParameterizedTest
    @MethodSource("providePaginationMetadata")
    void shouldMapToPaginationMetadata(Integer pageNumber, Long totalElements, Integer totalPages, PaginationMetadata expected) {
        // Given
        final var paginationRequest = mock(PaginationRequest.class);
        final var pageable = mock(Pageable.class);
        final var items = mock(Page.class);

        when(paginationRequest.pageable()).thenReturn(pageable);
        when(pageable.getPageNumber()).thenReturn(pageNumber);
        when(items.getTotalElements()).thenReturn(totalElements);
        when(items.getTotalPages()).thenReturn(totalPages);

        // When
        final var paginationMetadata = PaginationMapper.toPaginationMetadata(items, paginationRequest);

        // Then
        assertThat(paginationMetadata).isEqualTo(expected);
    }

    private static Stream<Arguments> providePaginationMetadata() {
        return Stream.of(
            Arguments.of(0, 0L, 0, new PaginationMetadata().currentPage(0).totalPages(0).totalItems(0L)),
            Arguments.of(0, 5L, 5, new PaginationMetadata().currentPage(1).nextPage(2).totalPages(5).totalItems(5L)),
            Arguments.of(1, 5L, 5, new PaginationMetadata().currentPage(2).previousPage(1).nextPage(3).totalPages(5).totalItems(5L)),
            Arguments.of(5, 5L, 5, new PaginationMetadata().currentPage(6).previousPage(5).totalPages(5).totalItems(5L)),
            Arguments.of(6, 5L, 5, new PaginationMetadata().currentPage(7).previousPage(5).totalPages(5).totalItems(5L)) // out of scope case
        );
    }
}