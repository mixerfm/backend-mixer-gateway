package fm.mixer.gateway.module.react.config;

import fm.mixer.gateway.module.community.persistance.entity.Comment;
import fm.mixer.gateway.module.community.persistance.entity.CommentLike;
import fm.mixer.gateway.module.community.persistance.repository.CommentLikeRepository;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.MixLike;
import fm.mixer.gateway.module.mix.persistance.repository.MixLikeRepository;
import fm.mixer.gateway.module.react.model.ResourceType;
import fm.mixer.gateway.module.react.service.ReactionService;
import fm.mixer.gateway.module.react.service.ReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ReactionServiceFactoryConfig {

    @Bean
    @Primary
    public ReactionService<Mix, MixLike> mixReactionService(MixLikeRepository repository, ReportService reportService) {
        return new ReactionService<>(repository, reportService, ResourceType.MIX);
    }

    @Bean
    public ReactionService<Comment, CommentLike> commentReactionService(CommentLikeRepository repository, ReportService reportService) {
        return new ReactionService<>(repository, reportService, ResourceType.COMMENT);
    }
}
