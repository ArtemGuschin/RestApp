package net.artem.restapp.exception;

public class UserNotFoundException extends NotFoundException {
  public UserNotFoundException(Integer id) {
    super("Пользователь с идентификатором " + id + " не найден.");
  }
}
