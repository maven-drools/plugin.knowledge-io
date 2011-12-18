package de.lightful.maven.plugins.drools.knowledgeio.internal;

public class DroolsKnowledgeModuleHeader {

  public byte[] magic = new byte[KnowledgeModule.FILE_MAGIC.length];
  public long fileFormatVersion;
  public String droolsRuntimeVersion = "";
}
