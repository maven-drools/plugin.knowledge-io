package de.lightful.maven.plugins.drools.knowledgeio;

/**
 * Indicates that a Knowledge Module was compiled for a Drools version which does not match the one currently on the classpath.
 * This means your project which is using the {@link de.lightful.maven.plugins.drools.knowledgeio.internal.KnowledgeModuleReaderImpl}
 * cannot use the compiled knowledge module content.
 */
public class InvalidDroolsRuntimeVersionException extends InvalidFileHeaderException {

  public InvalidDroolsRuntimeVersionException(String message) {
    super(message);
  }

  public InvalidDroolsRuntimeVersionException(String message, Throwable cause) {
    super(message, cause);
  }
}
