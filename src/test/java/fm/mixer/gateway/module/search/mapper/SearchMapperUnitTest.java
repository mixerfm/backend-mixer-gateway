package fm.mixer.gateway.module.search.mapper;

import fm.mixer.gateway.model.PaginationMetadata;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultGroupType;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultList;
import fm.mixer.gateway.test.UnitTest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Comparator;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class SearchMapperUnitTest {

    private final SearchMapper mapper = Mappers.getMapper(SearchMapper.class);

    @Test
    void shouldMapToSearchItemResultGroupList() {
        // Given
        final var given = Map.of(
            SearchItemResultGroupType.ARTIST, Instancio.create(SearchItemResultList.class),
            SearchItemResultGroupType.COLLECTION, Instancio.create(SearchItemResultList.class)
        );

        // When
        final var result = mapper.toSearchItemResultGroupList(given);

        // Then
        assertThat(result.getGroups()).hasSize(2).allSatisfy(group -> {
            assertThat(group.getType()).isIn(SearchItemResultGroupType.ARTIST, SearchItemResultGroupType.COLLECTION);
            assertThat(group.getItems()).isEqualTo(given.get(group.getType()).getItems());
        });
        assertThat(result.getMetadata()).isEqualTo(given.values().stream()
            .map(SearchItemResultList::getMetadata)
            .max(Comparator.comparing(PaginationMetadata::getTotalItems)).orElseThrow());
    }
}