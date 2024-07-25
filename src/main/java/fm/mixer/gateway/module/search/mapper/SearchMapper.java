package fm.mixer.gateway.module.search.mapper;

import fm.mixer.gateway.common.mapper.PaginatedMapping;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.model.PaginationMetadata;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import fm.mixer.gateway.module.mix.persistance.entity.MixTag;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResult;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultGroup;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultGroupList;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultGroupType;
import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultList;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserArtist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.Comparator;
import java.util.Map;

@Mapper(imports = {PaginationMapper.class})
public interface SearchMapper {

    @Mapping(target = "displayName", source = "name")
    @Mapping(target = "avatarUrl", source = "avatar")
    @Mapping(target = "popularityCount", ignore = true)
    SearchItemResult toSearchItemResult(UserArtist artist);

    @PaginatedMapping
    @Mapping(target = "items", source = "items.content")
    SearchItemResultList toSearchItemResultListFromArtist(Page<UserArtist> items, PaginationRequest paginationRequest);

    @Mapping(target = "displayName", source = "name")
    @Mapping(target = "avatarUrl", source = "avatar")
    @Mapping(target = "popularityCount", ignore = true)
    SearchItemResult toSearchItemResult(MixCollection collection);

    @PaginatedMapping
    @Mapping(target = "items", source = "items.content")
    SearchItemResultList toSearchItemResultListFromCollection(Page<MixCollection> items, PaginationRequest paginationRequest);

    @Mapping(target = "displayName", source = "name")
    @Mapping(target = "avatarUrl", source = "avatar")
    @Mapping(target = "popularityCount", ignore = true)
    SearchItemResult toSearchItemResult(Mix mix);

    @PaginatedMapping
    @Mapping(target = "items", source = "items.content")
    SearchItemResultList toSearchItemResultListFromMix(Page<Mix> items, PaginationRequest paginationRequest);

    @Mapping(target = "identifier", source = "name")
    @Mapping(target = "displayName", source = "name")
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "popularityCount", ignore = true)
    SearchItemResult toSearchItemResult(MixTag tag);

    @PaginatedMapping
    @Mapping(target = "items", source = "items.content")
    SearchItemResultList toSearchItemResultListFromTag(Page<MixTag> items, PaginationRequest paginationRequest);

    @Mapping(target = "displayName", source = "name")
    @Mapping(target = "avatarUrl", source = "avatar")
    @Mapping(target = "popularityCount", ignore = true)
    SearchItemResult toSearchItemResult(User user);

    @PaginatedMapping
    @Mapping(target = "items", source = "items.content")
    SearchItemResultList toSearchItemResultListFromUser(Page<User> items, PaginationRequest paginationRequest);

    @Mapping(target = "items", source = "items.items")
    @Mapping(target = "type", source = "type")
    SearchItemResultGroup toSearchItemResultGroup(SearchItemResultList items, SearchItemResultGroupType type);

    default SearchItemResultGroupList toSearchItemResultGroupList(Map<SearchItemResultGroupType, SearchItemResultList> results) {
        final var groupedResults = new SearchItemResultGroupList();

        groupedResults.setGroups(
            results.entrySet().stream()
                .map((result) -> toSearchItemResultGroup(result.getValue(), result.getKey()))
                .toList()
        );
        groupedResults.setMetadata(
            results.values().stream()
                .map(SearchItemResultList::getMetadata)
                .max(Comparator.comparing(PaginationMetadata::getTotalItems))
                .orElse(null)
        );

        return groupedResults;
    }
}
