package de.lightful.maven.plugins.drools.knowledgeio;

import de.lightful.maven.plugins.drools.knowledgeio.internal.KnowledgeModuleReaderImpl;
import de.lightful.maven.plugins.drools.knowledgeio.internal.KnowledgeModuleWriterImpl;

import java.io.InputStream;
import java.io.OutputStream;

public class KnowledgeIoFactory {

  public KnowledgeModuleReader createKnowledgeModuleReader(InputStream inputStream, ClassLoader classLoader) {
    return new KnowledgeModuleReaderImpl(inputStream, classLoader);
  }

  public KnowledgeModuleWriter createKnowledgeModuleWriter(OutputStream outputStream) {
    return new KnowledgeModuleWriterImpl(outputStream);
  }
}
