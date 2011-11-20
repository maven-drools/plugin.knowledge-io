package de.lightful.maven.plugins.drools.knowledgeio.impl;

public class DroolsKnowledgeModuleHeader {

  public byte[] magic = new byte[KnowledgeModule.FILE_MAGIC.length];
  public long fileFormatVersion;
  public String droolsRuntimeVersion = "";
}
