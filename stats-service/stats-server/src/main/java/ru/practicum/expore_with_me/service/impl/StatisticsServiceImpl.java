package ru.practicum.expore_with_me.service.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dto.GetResponse;
import dto.HitRequest;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.expore_with_me.dao.HitRepository;
import ru.practicum.expore_with_me.mapper.HitMapper;
import ru.practicum.expore_with_me.model.Hit;
import ru.practicum.expore_with_me.model.QHit;
import ru.practicum.expore_with_me.service.StatisticsService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final HitMapper hitMapper;
    private final HitRepository hitRepository;
    private final EntityManager entityManager;

    @Override
    public void createHit(HitRequest hitRequest) {
        Hit hit = hitMapper.requestToHit(hitRequest);
        hitRepository.save(hit);
        log.info("Hit saved");
    }

    @Override
    public Collection<GetResponse> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        QHit qHit = QHit.hit;

        BooleanExpression whereClause = qHit.timestamp.between(start, end);

        if (uris != null && !uris.isEmpty()) {
            whereClause = whereClause.and(qHit.uri.in(uris));
        }

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<Tuple> query;

        if (unique) {
            query = queryFactory.select(qHit.ip.countDistinct(), qHit.app, qHit.uri);
        } else {
            query = queryFactory.select(qHit.ip.count(), qHit.app, qHit.uri);
        }

        query = query.from(qHit)
                .where(whereClause)
                .groupBy(qHit.app, qHit.uri)
                .orderBy(qHit.ip.count().desc());

        List<Tuple> tuples = query.fetch();

        log.info("Returned list of getResponses");
        return tuples.stream().map(tuple ->
                GetResponse.builder()
                        .hits(tuple.get(0, Long.class).longValue())
                        .app(tuple.get(qHit.app))
                        .uri(tuple.get(qHit.uri))
                        .build()
        ).toList();
    }
}
