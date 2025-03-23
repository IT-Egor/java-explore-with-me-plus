package ru.practicum.client;

import dto.GetResponse;
import dto.HitRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.exception.StatsClientException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class StatsClient {

    private static final RestClient restClient = RestClient.create();
    private static final String STATS_SERVER_URI = "http://localhost:9090";

    public static void hit(HitRequest hitRequest) {
        String currentUri = UriComponentsBuilder.fromHttpUrl(STATS_SERVER_URI).path("/hit").toUriString();
        log.info("Post request to server uri = {}", currentUri);

        restClient.post()
                .uri(currentUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new StatsClientException(response.getStatusCode().value(), response.getBody().toString());
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new StatsClientException(response.getStatusCode().value(), response.getBody().toString());
                }))
                .toBodilessEntity();
    }

    public static List<GetResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String currentUri = UriComponentsBuilder.fromHttpUrl(STATS_SERVER_URI)
                .path("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .toUriString();
        log.info("Get request to server uri = {}", currentUri);

        return restClient.get()
                .uri(currentUri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    log.error("Server returned status 4xx to request {}", currentUri);
                    throw new StatsClientException(response.getStatusCode().value(), response.getBody().toString());
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    log.error("Server returned status 5xx to request {}", currentUri);
                    throw new StatsClientException(response.getStatusCode().value(), response.getBody().toString());
                }))
                .body(new ParameterizedTypeReference<>() {
                });
    }
}