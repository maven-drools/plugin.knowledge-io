package de.lightful.maven.plugins.drools.knowledgeio;

public class IllegalFileFormatException extends RuntimeException {

  public IllegalFileFormatException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalFileFormatException(String message) {
    super(message);
  }
}
