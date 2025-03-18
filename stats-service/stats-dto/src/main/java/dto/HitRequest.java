package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

    @NotBlank
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\\+([01][0-9]|2[0-3])%3A([0-5][0-9])%3A([0-5][0-9])$",
            message = "Неправильный формат даты. Верный формат: yyyy-MM-dd HH:mm:ss")
    private final String timestamp;
}
