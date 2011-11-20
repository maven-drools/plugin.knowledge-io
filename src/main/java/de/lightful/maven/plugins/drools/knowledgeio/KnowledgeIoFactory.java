package de.lightful.maven.plugins.drools.knowledgeio;

import de.lightful.maven.plugins.drools.knowledgeio.impl.KnowledgeModuleReaderImpl;
import de.lightful.maven.plugins.drools.knowledgeio.impl.KnowledgeModuleWriterImpl;

import java.io.InputStream;
import java.io.OutputStream;

public class KnowledgeIoFactory {

  public KnowledgeModuleReader createKnowledgeModuleReader(InputStream inputStream, ClassLoader classLoader) {
    return new KnowledgeModuleReaderImpl(inputStream, classLoader);
  }

  public KnowledgeModuleReader createKnowledgeModuleReader(InputStream inputStream) {
    return new KnowledgeModuleReaderImpl(inputStream);
  }

  public KnowledgeModuleWriter createKnowledgeModuleWriter(OutputStream outputStream) {
    return new KnowledgeModuleWriterImpl(outputStream);
  }
}
