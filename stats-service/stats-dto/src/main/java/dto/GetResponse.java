package dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
@RequiredArgsConstructor
public class GetResponse {
    @NotBlank
    private final String app;

    @NotBlank
    private final String uri;

    private final long hits;
}
