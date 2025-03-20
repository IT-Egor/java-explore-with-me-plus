package ru.practicum.explore_with_me.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CreateUserResponse {
    Long id;
    String email;
    String name;
}
