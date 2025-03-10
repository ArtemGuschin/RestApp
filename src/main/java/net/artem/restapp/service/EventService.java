package net.artem.restapp.service;

import lombok.RequiredArgsConstructor;
import net.artem.restapp.model.Event;
import net.artem.restapp.model.File;
import net.artem.restapp.model.User;
import net.artem.restapp.repository.EventRepository;
import net.artem.restapp.repository.impl.EventRepositoryImpl;
import net.artem.restapp.repository.impl.FileRepositoryImpl;
import net.artem.restapp.repository.impl.UserRepositoryImpl;

import java.util.List;

@RequiredArgsConstructor
public class EventService {

   private final EventRepository eventRepository;
    private final UserService userService;
    private final FileService fileService;

    public EventService() {
        this.userService = new UserService(new UserRepositoryImpl());
        this.fileService = new FileService(new FileRepositoryImpl());
        this.eventRepository = new EventRepositoryImpl();
    }

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


    public Event saveEvent(Event event1, int userId, int fileId) {

        User user = userService.getById(userId);
        File file = fileService.getById(fileId);

        if (user == null || file == null) {
            throw new IllegalArgumentException("User or File not found");
        }
        Event event = new Event();
        event.setUser(user);
        event.setFile(file);
        this.save(event);

        return event;
    }


}
