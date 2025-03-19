package ru.practicum.expore_with_me.service;

import dto.GetRequest;
import dto.GetResponse;
import dto.HitRequest;

import java.util.Collection;

public interface StatisticsService {
    void createHit(HitRequest hitRequest);

    Collection<GetResponse> getStatistics(GetRequest getRequest);
}
