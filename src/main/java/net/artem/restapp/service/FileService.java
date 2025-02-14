package net.artem.restapp.service;

import lombok.RequiredArgsConstructor;
import net.artem.restapp.model.File;
import net.artem.restapp.repository.FileRepository;

import java.util.List;

@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
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
}
