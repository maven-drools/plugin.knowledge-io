package de.lightful.maven.plugins.drools.knowledgeio.impl;

import de.lightful.maven.plugins.drools.knowledgeio.KnowledgeModuleWriter;
import org.drools.definition.KnowledgePackage;

import java.io.IOException;
import java.io.OutputStream;

public class KnowledgeModuleWriterImpl implements KnowledgeModuleWriter {

  private OutputStream outputStream;

  public KnowledgeModuleWriterImpl(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public void writeKnowledgePackages(Iterable<KnowledgePackage> knowledgePackages) throws IOException {
    throw new UnsupportedOperationException();
  }
}
