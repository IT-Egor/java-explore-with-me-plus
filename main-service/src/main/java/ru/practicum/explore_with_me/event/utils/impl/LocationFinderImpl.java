package ru.practicum.explore_with_me.event.utils.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.event.dao.LocationRepository;
import ru.practicum.explore_with_me.event.model.Location;
import ru.practicum.explore_with_me.event.utils.LocationFinder;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationFinderImpl implements LocationFinder {
    private final LocationRepository locationRepository;

    @Override
    public Location findById(Float lat, Float lon) {
        log.info("Searching location with lat = {} and lon = {}", lat, lon);
        return locationRepository
                .findByLatAndLon(lat, lon)
                .orElseGet(() -> locationRepository.save(Location.builder().lat(lat).lon(lon).build()));
    }
}