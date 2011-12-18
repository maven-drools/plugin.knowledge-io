package de.lightful.maven.plugins.drools.knowledgeio.impl;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static de.lightful.maven.plugins.drools.knowledgeio.impl.ArrayUtils.slice;
import static org.fest.assertions.Assertions.assertThat;

@Test
public class KnowledgeModuleWriterImplTest {

  private KnowledgeModuleWriterImpl writer;
  private Iterable<KnowledgePackage> knowledgePackages;
  private ByteArrayOutputStream outputStream;

  @BeforeClass
  public void setUpKnowledgePackages() {
    final KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    knowledgeBuilder.add(ResourceFactory.newByteArrayResource(ExampleDroolsCode.THREE_SIMPLE_RULES.getBytes()), ResourceType.DRL);
    assertThat(knowledgeBuilder.hasErrors()).as("Knowledge Builder's hasErrors Flag").isFalse();
    knowledgePackages = knowledgeBuilder.getKnowledgePackages();
  }

  @BeforeMethod
  public void setUp() {
    outputStream = new ByteArrayOutputStream();
    writer = new KnowledgeModuleWriterImpl(outputStream);
  }

  @Test
  public void testWritesFileMagic() throws IOException {
    writer.writeKnowledgePackages(knowledgePackages);
    final byte[] bytes = outputStream.toByteArray();
    assertThat(slice(bytes, 0, KnowledgeModule.FILE_MAGIC.length)).isEqualTo(KnowledgeModule.FILE_MAGIC);
  }

  @Test
  public void testWritesFileFormatVersion() throws IOException {
    writer.writeKnowledgePackages(knowledgePackages);
    final byte[] bytes = outputStream.toByteArray();
    assertThat(slice(bytes, KnowledgeModule.FILE_MAGIC.length, KnowledgeModule.CURRENT_FILE_FORMAT.length)).isEqualTo(KnowledgeModule.CURRENT_FILE_FORMAT);
  }

  @Test
  public void testWritesDroolsCoreRuntimeVersion() throws IOException {
    writer.writeKnowledgePackages(knowledgePackages);
    final byte[] bytes = outputStream.toByteArray();

    final int sizeOfRuntimeVersionLength = Short.SIZE / 8;
    final ByteBuffer buffer = ByteBuffer.wrap(bytes);
    final int skipSize = KnowledgeModule.FILE_MAGIC.length + KnowledgeModule.CURRENT_FILE_FORMAT.length;
    int runtimeVersionLength = buffer.getShort(skipSize);
    final byte[] runtimeVersionChars = slice(bytes, skipSize + sizeOfRuntimeVersionLength, runtimeVersionLength);
    assertThat(new String(runtimeVersionChars, "UTF-8")).isEqualTo(KnowledgePackage.class.getPackage().getImplementationVersion());
  }

  @Test
  public void testWritesActualKnowledgePackages() throws IOException, ClassNotFoundException {
    writer.writeKnowledgePackages(knowledgePackages);
    final byte[] bytes = outputStream.toByteArray();

    final int sizeOfRuntimeVersionLength = Short.SIZE / 8;
    final ByteBuffer buffer = ByteBuffer.wrap(bytes);
    final int fixedSkipLength = KnowledgeModule.FILE_MAGIC.length + KnowledgeModule.CURRENT_FILE_FORMAT.length;
    final int runtimeVersionLength = buffer.getShort(fixedSkipLength);
    final int totalSkipLength = fixedSkipLength + sizeOfRuntimeVersionLength + runtimeVersionLength;

    final byte[] knowledgePackageData = slice(bytes, totalSkipLength, bytes.length - totalSkipLength);
    final Object knowledgeObject = DroolsStreamUtils.streamIn(knowledgePackageData, true);
    assertThat(knowledgeObject).isInstanceOf(Iterable.class);
    Iterable<?> knowledgeIterable = (Iterable<?>) knowledgeObject;
    for (Object shouldBeKnowledgePackage : knowledgeIterable) {
      assertThat(shouldBeKnowledgePackage).isInstanceOf(KnowledgePackage.class);
      KnowledgePackage knowledgePackage = (KnowledgePackage) shouldBeKnowledgePackage;
      assertThat(knowledgePackage.getRules()).hasSize(3);
    }
  }
}
