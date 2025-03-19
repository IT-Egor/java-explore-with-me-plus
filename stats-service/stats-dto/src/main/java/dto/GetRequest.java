package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@RequiredArgsConstructor
public class GetRequest {
    @NotBlank
    private final LocalDateTime start;

    @NotNull
    private final LocalDateTime end;

    @NotNull
    private final List<String> uris;

    @Builder.Default
    private final boolean unique = false;
}
