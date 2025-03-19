package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GetRequest {
    @NotBlank
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private List<String> uris;

    @Builder.Default
    private boolean unique = false;
}
