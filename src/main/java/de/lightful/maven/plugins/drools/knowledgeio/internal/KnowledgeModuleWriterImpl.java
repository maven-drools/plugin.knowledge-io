package de.lightful.maven.plugins.drools.knowledgeio.internal;

import de.lightful.maven.plugins.drools.knowledgeio.KnowledgeModuleWriter;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definition.KnowledgePackage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import static org.fest.assertions.Assertions.assertThat;

public class KnowledgeModuleWriterImpl implements KnowledgeModuleWriter {

  private OutputStream outputStream;

  public KnowledgeModuleWriterImpl(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public void writeKnowledgePackages(Iterable<KnowledgePackage> knowledgePackages) throws IOException {
    writeFileMagic();
    writeFileFormat();
    writeDroolsRuntimeVersion();
    writeKnowledgeData(knowledgePackages);
  }

  private void writeFileMagic() throws IOException {
    outputStream.write(KnowledgeModule.FILE_MAGIC, 0, KnowledgeModule.FILE_MAGIC.length);
  }

  private void writeFileFormat() throws IOException {
    outputStream.write(KnowledgeModule.CURRENT_FILE_FORMAT, 0, KnowledgeModule.CURRENT_FILE_FORMAT.length);
  }

  private void writeDroolsRuntimeVersion() throws IOException {
    final String implementationVersion = KnowledgePackage.class.getPackage().getImplementationVersion();
    assertThat(implementationVersion.length()).as("Length of implementation version").isLessThan(Short.MAX_VALUE);
    final ByteBuffer buffer = ByteBuffer.allocate(Short.SIZE / 8);
    buffer.putShort((short) implementationVersion.length());
    outputStream.write(buffer.array());
    outputStream.write(implementationVersion.getBytes("UTF-8"));
  }

  private void writeKnowledgeData(Iterable<KnowledgePackage> knowledgePackages) throws IOException {
    DroolsStreamUtils.streamOut(outputStream, knowledgePackages, true);
  }
}
