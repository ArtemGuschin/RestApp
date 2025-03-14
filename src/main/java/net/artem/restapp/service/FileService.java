package net.artem.restapp.service;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import net.artem.restapp.exception.UserNotFoundException;
import net.artem.restapp.model.Event;
import net.artem.restapp.model.File;
import net.artem.restapp.model.User;
import net.artem.restapp.repository.FileRepository;

import net.artem.restapp.repository.UserRepository;
import net.artem.restapp.repository.impl.FileRepositoryImpl;
import net.artem.restapp.repository.impl.UserRepositoryImpl;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    UserService userService;
    FileService fileService;
    EventService eventService;
    UserRepository userRepository;

    public FileService() {
        this.userService = new UserService(new UserRepositoryImpl());
        this.fileService = new FileService(new FileRepositoryImpl());
        this.eventService = new EventService();
        this.fileRepository = new FileRepositoryImpl();
        this.userRepository = new UserRepositoryImpl();


    }

    public File getById(Integer id) {
        return fileRepository.getById(id);
    }

    public List<File> getAll() {
        return fileRepository.getAll();
    }

    public File save(File file) {
        return fileRepository.save(file);
    }

    public File update(File file) {
        return fileRepository.update(file);
    }

    public void deleteById(Integer id) {
        fileRepository.delete(id);
    }


    public File uploadFile(InputStream fileContent, String fileName, Integer userId) throws IOException, UserNotFoundException {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        String uploadDir = "uploads/";
        Files.createDirectories(Paths.get(uploadDir));
        String filePath = uploadDir + fileName;

        Files.copy(fileContent, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        File file = new File();
        file.setName(fileName);
        file.setFilePath(filePath);
        file = fileRepository.save(file);

        Event event = new Event();
        event.setUser(user);
        event.setFile(file);
        eventService.save(event);

        return file;
    }

//    public File uploadFile(Integer userId, Part filePart) throws IOException {
//        User user = userService.getById(userId);
//
//        if (user == null) {
//            throw new UserNotFoundException(userId);
//        }
//
//
//        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
//        byte[] fileData = filePart.getInputStream().readAllBytes();
//
//
//        String uploadDir = "uploads/";
//        Files.createDirectories(Paths.get(uploadDir));
//        String filePath = uploadDir + fileName;
//
//        Files.write(Paths.get(filePath), fileData);
//
//
//        File file = new File();
//        file.setName(fileName);
//        file.setFilePath(filePath);
//        file = fileRepository.save(file);
//
//        Event event = new Event();
//        event.setUser(user);
//        event.setFile(file);
//
//        eventService.save(event);
//
//        return file;
//    }

}
