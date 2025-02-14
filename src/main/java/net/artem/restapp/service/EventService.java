package net.artem.restapp.service;

import lombok.RequiredArgsConstructor;
import net.artem.restapp.model.Event;
import net.artem.restapp.repository.EventRepository;

import java.util.List;

@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    public Event getById(Integer id) {
        return eventRepository.getById(id);
    }

    public List<Event> getAll() {
        return eventRepository.getAll();
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public Event update(Event event) {
        return eventRepository.update(event);
    }

    public void deleteById(Integer id) {
        eventRepository.delete(id);
    }

}
