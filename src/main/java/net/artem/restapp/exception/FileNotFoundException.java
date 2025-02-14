package net.artem.restapp.exception;

public class FileNotFoundException extends NotFoundException {

  public FileNotFoundException(Integer id) {
    super("Файл с идентификатором " + id + " не найден.");
  }
}
