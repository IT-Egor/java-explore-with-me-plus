package ru.practicum.explore_with_me.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50, message = "title length must be between 1 and 50 characters")
    String title;

    Boolean pinned;
    Set<Long> events;
}
