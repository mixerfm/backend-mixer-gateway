package fm.mixer.gateway.module.search.integration.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(url = "https://musicbrainz.org/ws/2/artist")
public interface MusicBrainzApiClient {

    List<String> search(@RequestParam String query, @RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer offset);
}
