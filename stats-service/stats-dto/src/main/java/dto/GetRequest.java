package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@RequiredArgsConstructor
public class GetRequest {
    @NotBlank
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\\+([01][0-9]|2[0-3])%3A([0-5][0-9])%3A([0-5][0-9])$",
            message = "Invalid date. Correct format: yyyy-MM-dd HH:mm:ss")
    private final String start;

    @NotBlank
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\\+([01][0-9]|2[0-3])%3A([0-5][0-9])%3A([0-5][0-9])$",
            message = "Invalid date. Correct format: yyyy-MM-dd HH:mm:ss")
    private final String end;

    @NotNull
    private final List<String> uris;

    @Builder.Default
    private final boolean unique = false;
}
