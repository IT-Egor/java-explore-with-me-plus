package ru.practicum.explore_with_me.request.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explore_with_me.category.model.RequestStatus;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@Table(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;

    RequestStatus status;
}

