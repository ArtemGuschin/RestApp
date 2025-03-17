package net.artem.restapp.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artem.restapp.dto.FileDTO;
import net.artem.restapp.exception.UserNotFoundException;
import net.artem.restapp.model.File;

import net.artem.restapp.service.FileService;


import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;


@WebServlet("/api/v1/files")
@MultipartConfig
public class FileRestControllerV1 extends HttpServlet {
    private FileService fileService = new FileService();
    private ObjectMapper objectMapper;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Integer userId = Integer.valueOf(request.getHeader("user_id"));

            Part filePart = request.getPart("file"); // "file" — имя поля формы для загрузки файла

            String contentDisposition = filePart.getHeader("content-disposition");
            String[] items = contentDisposition.split(";");
            String fileName = null;
            for (String item : items) {
                if (item.trim().startsWith("filename=")) {
                    fileName = item.substring(item.indexOf("=") + 2, item.length() - 1);
                    break;
                }
            }

            if (fileName == null || fileName.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name is required");
                return;
            }

            InputStream fileContent = filePart.getInputStream();

            File file = fileService.uploadFile(fileContent, fileName, userId);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(file));
        } catch (UserNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File upload failed: " + e.getMessage());
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