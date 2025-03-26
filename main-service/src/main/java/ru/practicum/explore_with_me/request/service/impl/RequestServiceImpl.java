package ru.practicum.explore_with_me.request.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.category.model.RequestStatus;
import ru.practicum.explore_with_me.error.model.*;
import ru.practicum.explore_with_me.event.dao.EventRepository;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.enums.EventState;
import ru.practicum.explore_with_me.event.utils.EventFinder;
import ru.practicum.explore_with_me.request.dao.RequestRepository;
import ru.practicum.explore_with_me.request.dto.RequestDto;
import ru.practicum.explore_with_me.request.mapper.RequestMapper;
import ru.practicum.explore_with_me.request.model.Request;
import ru.practicum.explore_with_me.request.service.RequestService;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.utils.UserFinder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestServiceImpl implements RequestService {
    final RequestRepository requestRepository;
    final UserFinder userFinder;
    final EventFinder eventFinder;
    final RequestMapper requestMapper;
    final EventRepository eventRepository;


    @Override
    public Collection<RequestDto> getAllUserRequest(Long userId) {
        userFinder.getUserById(userId);
        Set<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(requestMapper::toRequestDto).toList();
    }

    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        Optional<Request> requestOptional = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (requestOptional.isPresent()) {
            throw new DuplicateRequestException("Request can be only one");
        }
        Event event = eventFinder.findById(eventId);
        User user = userFinder.getUserById(userId);
        RequestStatus status = RequestStatus.PENDING;

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotPublishedEventRequestException("Event must be published");
        }

        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new RequestLimitException("No more seats for the event");
        }

        if (event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }

        if (event.getInitiator().getId() == user.getId()) {
            throw new InitiatorRequestException("Initiator can't submit a request for event");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .status(status)
                .build();
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        userFinder.getUserById(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request not found"));
        request.setStatus(RequestStatus.CANCEL);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }
}
