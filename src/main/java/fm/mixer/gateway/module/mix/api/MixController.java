package fm.mixer.gateway.module.mix.api;

import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.module.mix.api.v1.MixesApiDelegate;
import fm.mixer.gateway.module.mix.api.v1.model.SingleMix;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixes;
import fm.mixer.gateway.module.mix.service.MixService;
import fm.mixer.gateway.validation.annotation.OpenApiValidation;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MixController implements MixesApiDelegate {

    private final MixService service;

    @Override
    @OpenApiValidation
    public ResponseEntity<SingleMix> getSingleMix(String mixId) {
        return ResponseEntity.ok(service.getSingleMix(mixId));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<UserLikedMixes> getUserLikedMixes(String username, List<@Pattern(regexp = "^(date|name|popularity|trend)(,(asc|desc))?$") String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getUserLikedMixes(username, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<UserListenedMixes> getUserMixesHistory(String username, List<@Pattern(regexp = "^(date|name|popularity|trend)(,(asc|desc))?$") String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getUserMixesHistory(username, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<UserUploadedMixes> getUserUploadedMixes(String username, List<@Pattern(regexp = "^(date|name|popularity|trend)(,(asc|desc))?$") String> sort, Integer limit, Integer page) {
        return ResponseEntity.ok(service.getUserUploadedMixes(username, PaginationMapper.toPaginationRequest(limit, page, sort)));
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> likeMix(String mixId) {
        service.setLikeFlag(mixId, true);

        return ResponseEntity.noContent().build();
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> dislikeMix(String mixId) {
        service.setLikeFlag(mixId, false);

        return ResponseEntity.noContent().build();
    }

    @Override
    @OpenApiValidation
    public ResponseEntity<Void> reportMix(String mixId) {
        service.reportMix(mixId);

        return ResponseEntity.noContent().build();
    }
}
