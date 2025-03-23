package dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@Setter
@RequiredArgsConstructor
public class HitRequest {
    @NotBlank
    private final String app;

    @NotBlank
    private final String uri;

    @NotBlank
    private final String ip;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();
}
