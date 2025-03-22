package ru.practicum.explore_with_me.event.utils;

import ru.practicum.explore_with_me.event.model.Location;

public interface LocationFinder {
    Location findById(Float lat, Float lon);
}