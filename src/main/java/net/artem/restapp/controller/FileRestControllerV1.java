package net.artem.restapp.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import net.artem.restapp.exception.UserNotFoundException;
import net.artem.restapp.model.File;
import net.artem.restapp.repository.impl.FileRepositoryImpl;
import net.artem.restapp.repository.impl.UserRepositoryImpl;
import net.artem.restapp.service.FileService;
import net.artem.restapp.service.UserService;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@WebServlet("/api/v1/files/*")
public class FileRestControllerV1 extends HttpServlet {
    private FileService fileService = new FileService();
    private ObjectMapper objectMapper;


    public FileRestControllerV1() {

    }

    @Override
    public void init() {
        objectMapper = new ObjectMapper();
        System.out.println("FileServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();


        if (pathInfo == null || pathInfo.equals("/")) {
            List<File> files = fileService.getAll();
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(files));
            return;
        }


        String[] splits = pathInfo.split("/");
        if (splits.length != 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            return;
        }

        try {
            int id = Integer.parseInt(splits[1]);
            File file = fileService.getById(id);
            if (file == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                return;
            }

            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(file));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Integer userId = Integer.parseInt(req.getParameter("userId"));

            Part filePart = req.getPart("file");

            File uploadedFile = fileService.uploadFile(userId, (javax.servlet.http.Part) filePart);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(uploadedFile));
        } catch (UserNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "File upload failed: " + e.getMessage());
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


            File file = objectMapper.readValue(req.getInputStream(), File.class);
            file.setId(id);


            fileService.update(file);


            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(file));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid data");
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


            File file = fileService.getById(id);
            if (file == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                return;
            }

            fileService.deleteById(file.getId());


            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        }
    }

    @Override
    public void destroy() {
        fileService = null;
        objectMapper = null;
        System.out.println("FileServlet destroyed");
    }
}