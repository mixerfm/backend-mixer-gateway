package fm.mixer.gateway.module.app.mapper;

import fm.mixer.gateway.module.app.api.v1.model.ClientType;
import fm.mixer.gateway.module.app.api.v1.model.ClientVersion;
import fm.mixer.gateway.module.app.api.v1.model.Feature;
import fm.mixer.gateway.module.app.api.v1.model.FeatureList;
import fm.mixer.gateway.module.app.api.v1.model.FeatureType;
import fm.mixer.gateway.module.app.api.v1.model.ServerApiType;
import fm.mixer.gateway.module.app.api.v1.model.ServerVersion;
import fm.mixer.gateway.module.app.api.v1.model.VersionList;
import fm.mixer.gateway.module.app.config.VersionsConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper
public interface ApplicationMapper {

    default VersionList toVersionList(VersionsConfig config) {
        final var versionList = new VersionList();

        versionList.setClients(
            config.getClients().entrySet().stream()
                .map((version) -> toClientVersion(version.getValue(), version.getKey()))
                .toList()
        );
        versionList.setApis(
            config.getApis().entrySet().stream()
                .map((version) -> toServerVersion(version.getValue(), version.getKey()))
                .toList()
        );

        return versionList;

    }

    @Mapping(target = "deprecationDate", ignore = true)
    @Mapping(target = "notice", ignore = true)
    ClientVersion toClientVersion(VersionsConfig.VersionConfig config, ClientType type);

    @Mapping(target = "deprecationDate", ignore = true)
    @Mapping(target = "notice", ignore = true)
    ServerVersion toServerVersion(VersionsConfig.VersionConfig config, ServerApiType type);

    Feature toFeature(FeatureType type, Boolean available);

    default FeatureList toFeatureList(Map<FeatureType, Boolean> features) {
        return new FeatureList().features(
            features.entrySet().stream().map((feature) -> toFeature(feature.getKey(), feature.getValue())).toList()
        );
    }
}
