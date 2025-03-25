package ru.practicum.explore_with_me.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationMergeRequest {
    @NotNull(message = "title is required")
    @NotBlank(message = "title is required")
    @Size(min = 1, max = 50, message = "title length must be between 1 and 50 characters")
    String title;

    @Builder.Default
    Boolean pinned = false;

    Set<Long> events;
}
