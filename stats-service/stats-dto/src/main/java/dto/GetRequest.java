package dto;

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
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uris;

    @Builder.Default
    private boolean unique = false;
}
