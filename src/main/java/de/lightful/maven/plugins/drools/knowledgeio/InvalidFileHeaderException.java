package de.lightful.maven.plugins.drools.knowledgeio;

public class InvalidFileHeaderException extends IllegalFileFormatException {

  public InvalidFileHeaderException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidFileHeaderException(String message) {
    super(message);
  }
}
