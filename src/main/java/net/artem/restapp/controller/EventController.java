package net.artem.restapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.artem.restapp.model.Event;

import net.artem.restapp.model.File;
import net.artem.restapp.model.User;
import net.artem.restapp.repository.impl.EventRepositoryImpl;
import net.artem.restapp.repository.impl.FileRepositoryImpl;
import net.artem.restapp.repository.impl.UserRepositoryImpl;
import net.artem.restapp.service.EventService;
import net.artem.restapp.service.FileService;
import net.artem.restapp.service.UserService;
import java.io.IOException;
import java.util.List;  


@WebServlet("/events/*")
public class EventController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final EventService eventService = new EventService(new EventRepositoryImpl());
    private final UserService userService = new UserService(new UserRepositoryImpl());
    private final FileService fileService = new FileService(new FileRepositoryImpl());
    private final ObjectMapper objectMapper = new ObjectMapper();


//    public EventController(EventService eventService, UserService userService, FileService fileService) {
//        this.eventService = eventService;
//        this.userService = userService;
//        this.fileService = fileService;
//    }

    public EventController() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<Event> events = eventService.getAll();
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(events));
            return;
        }
        String[] splits = pathInfo.split("/");
        if (splits.length != 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }
        try {
            int id = Integer.parseInt(splits[1]);
            Event event = eventService.getById(id);

            if (event == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
                return;
            }
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(event));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Event event = objectMapper.readValue(req.getReader(), Event.class);
            eventService.save(event);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(event));

        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }
        String[] splits = pathInfo.split("/");
        if (splits.length != 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }
        try {
            int id = Integer.parseInt(splits[1]);
            Event event = objectMapper.readValue(req.getReader(), Event.class);
            event.setId(id);

            eventService.update(event);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(event));

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }
        String[] splits = pathInfo.split("/");
        if (splits.length != 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }
        try {
            int id = Integer.parseInt(splits[1]);

            Event event = eventService.getById(id);

            if (event == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
                return;
            }
            eventService.deleteById(event.getId());
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        }
    }

    private Event createEvent(int userId, int fileId) {
        User user = userService.getById(userId);
        File file = fileService.getById(fileId);

        if (user == null || file == null) {
            throw new IllegalArgumentException("User or File not found");
        }
        Event event = new Event();
        event.setUser(user);
        event.setFile(file);
        eventService.save(event);

        return event;
    }
}
