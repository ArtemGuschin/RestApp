package net.artem.restapp.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.artem.restapp.model.File;
import net.artem.restapp.repository.impl.FileRepositoryImpl;
import net.artem.restapp.service.FileService;


import java.io.IOException;
import java.util.List;


@WebServlet("/files/*")
public class FileController extends HttpServlet {
    private FileService fileService = new FileService(new FileRepositoryImpl());
    private ObjectMapper objectMapper;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    public FileController() {

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

            File file = objectMapper.readValue(req.getInputStream(), File.class);


            fileService.save(file);


            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(file));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid data");
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
            file.setId(id); // Устанавливаем ID из URL


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