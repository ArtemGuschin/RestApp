package net.artem.restapp.exception;

public class RepositoryException extends RuntimeException {

  public RepositoryException(String message, Throwable cause) {
    super(message, cause);
  }
}
