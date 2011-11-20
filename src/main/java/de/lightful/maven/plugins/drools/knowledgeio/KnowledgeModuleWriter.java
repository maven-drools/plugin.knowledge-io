package de.lightful.maven.plugins.drools.knowledgeio;

import org.drools.definition.KnowledgePackage;

import java.io.IOException;

public interface KnowledgeModuleWriter {

  void writeKnowledgePackages(Iterable<KnowledgePackage> knowledgePackages) throws IOException;
}
