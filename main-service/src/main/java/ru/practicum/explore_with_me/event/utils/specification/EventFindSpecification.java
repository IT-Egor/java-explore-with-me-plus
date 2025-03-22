package ru.practicum.explore_with_me.event.utils.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.explore_with_me.event.model.Event;

import java.time.LocalDateTime;
import java.util.Set;

public class EventFindSpecification {

    public static Specification<Event> categoryIn(Set<Long> categories) {
        return ((root, query, criteriaBuilder) -> {
            if (categories == null || categories.isEmpty()) {
                return null;
            }
            return root.get("category").in(categories);
        });
    }

    public static Specification<Event> userIn(Set<Long> users) {
        return ((root, query, criteriaBuilder) -> {
            if (users == null || users.isEmpty()) {
                return null;
            }
            return root.get("initiator").in(users);
        });
    }

    public static Specification<Event> stateIn(Set<String> states) {
        return ((root, query, criteriaBuilder) -> {
            if (states == null || states.isEmpty()) {
                return null;
            }
            return root.get("state").in(states);
        });
    }

    public static Specification<Event> eventDateBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return null;
            }
            return criteriaBuilder.lessThan(root.get("eventDate"), date);
        };
    }

    public static Specification<Event> eventDateAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return null;
            }
            return criteriaBuilder.greaterThan(root.get("eventDate"), date);
        };
    }
}
