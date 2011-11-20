package de.lightful.maven.plugins.drools.knowledgeio;

public class InvalidFileFormatVersionException extends InvalidFileHeaderException {

  public InvalidFileFormatVersionException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidFileFormatVersionException(String message) {
    super(message);
  }
}
