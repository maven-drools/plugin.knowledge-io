package de.lightful.maven.plugins.drools.knowledgeio;

import org.drools.definition.KnowledgePackage;

import java.io.IOException;
import java.util.Collection;

public interface KnowledgeModuleReader {

  Collection<KnowledgePackage> readKnowledgePackages() throws IOException, ClassNotFoundException;
}
