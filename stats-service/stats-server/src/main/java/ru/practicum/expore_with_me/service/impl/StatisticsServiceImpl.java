package ru.practicum.expore_with_me.service.impl;

import dto.GetRequest;
import dto.GetResponse;
import dto.HitRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.expore_with_me.dao.HitRepository;
import ru.practicum.expore_with_me.mapper.HitMapper;
import ru.practicum.expore_with_me.model.Hit;
import ru.practicum.expore_with_me.service.StatisticsService;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final HitMapper hitMapper;
    private final HitRepository hitRepository;

    @Override
    public void createHit(HitRequest hitRequest) {
        Hit hit = hitMapper.requestToHit(hitRequest);
        hitRepository.save(hit);
    }

    @Override
    public Collection<GetResponse> getStatistics(GetRequest getRequest) {
        return List.of();
    }
}
