package de.lightful.maven.plugins.drools.knowledgeio;

public class InvalidFileMagicException extends InvalidFileHeaderException {

  public InvalidFileMagicException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidFileMagicException(String message) {
    super(message);
  }
}
