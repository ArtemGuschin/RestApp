package net.artem.restapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.artem.restapp.model.User;
import net.artem.restapp.repository.impl.UserRepositoryImpl;
import net.artem.restapp.service.UserService;

import java.io.IOException;
import java.util.List;


@WebServlet("/api/v1/users/*")
public class UserControllerV1 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService(new UserRepositoryImpl());
    private ObjectMapper objectMapper = new ObjectMapper();

    public UserControllerV1() {
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<User> users = userService.getAll();
            response.getWriter().write(new ObjectMapper().writeValueAsString(users));
        } else {
            String[] splits = pathInfo.split("/");
            if (splits.length == 2) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            Integer id = Integer.valueOf(splits[1]);
            User user = userService.getById(id);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.getWriter().write(new ObjectMapper().writeValueAsString(user));
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = objectMapper.readValue(request.getInputStream(), User.class);
        userService.save(user);
        response.setStatus(HttpServletResponse.SC_CREATED);

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = objectMapper.readValue(request.getInputStream(), User.class);
        userService.update(user);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String[] splits = pathInfo.split("/");
        if (splits.length == 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        Integer id = Integer.valueOf(splits[1]);
        User user = userService.getById(id);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        userService.deleteById(user.getId());
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }


}
