package de.lightful.maven.plugins.drools.knowledgeio.impl;

public interface KnowledgeModule {

  public static final byte[] FILE_MAGIC = new byte[] {'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00};
  public static final byte[] CURRENT_FILE_FORMAT = new byte[] {0, 0, 0, 0, 0, 0, 0, 1};
}
