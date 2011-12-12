package de.lightful.maven.plugins.drools.knowledgeio.impl;

import de.lightful.maven.plugins.drools.knowledgeio.IllegalFileFormatException;
import de.lightful.maven.plugins.drools.knowledgeio.InvalidDroolsRuntimeVersionException;
import de.lightful.maven.plugins.drools.knowledgeio.InvalidFileFormatVersionException;
import de.lightful.maven.plugins.drools.knowledgeio.InvalidFileMagicException;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.fest.assertions.Fail;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;

import static de.lightful.maven.plugins.drools.knowledgeio.impl.ArrayUtils.bytes;
import static de.lightful.maven.plugins.drools.knowledgeio.impl.ArrayUtils.concat;
import static java.util.Collections.singleton;
import static org.fest.assertions.Assertions.assertThat;

@Test
public class KnowledgeModuleReaderImplTest {

  public static final byte[] VALID_MAGIC = bytes('D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00);
  public static final byte[] FILE_FORMAT_TOO_SHORT_7 = bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02);
  public static final byte[] FILE_FORMAT_TOO_SHORT_6 = bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x01);
  public static final byte[] VALID_FILE_FORMAT_1 = bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01);
  public static final byte[] VALID_FILE_FORMAT_2 = bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02);
  public static final byte[] DUMMY_DROOLS_VERSION = bytes(0x00, 0x01, 'X');
  public static final byte[] DROOLS_5_1_1 = bytes(0, 5, '5', '.', '1', '.', '1');

  @Test
  public void testFileFormatTooShort() {
    assertThat(FILE_FORMAT_TOO_SHORT_6.length).isEqualTo(6);
  }

  @Test
  public void testFileFormatNotSupported() {
    assertThat(FILE_FORMAT_TOO_SHORT_7.length).isEqualTo(7);
  }

  @Test(dataProvider = "getModuleWithIncompleteMagic")
  public void testReadFailsForIncompleteMagic(byte[] invalidInput) throws ClassNotFoundException, IOException {
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(invalidInput));
    try {
      reader.readKnowledgePackages();
      Fail.fail("Exception expected");
    }
    catch (IllegalFileFormatException e) {
      assertThat(e.getMessage()).contains("Cannot read file magic from input stream");
    }
    catch (Exception e) {
      Fail.fail("Expected IllegalFileFormatException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @DataProvider
  private Object[][] getModuleWithIncompleteMagic() {
    return new Object[][] {
        {bytes()},
        {bytes('A', 'B', 'C')},
        {bytes('D', 'R', 'L')},
        {bytes('D', 'R', 'L', 'K', 'M', 'O', 'D')},
    };
  }

  @Test(dataProvider = "getModuleWithInvalidFileFormatVersion")
  public void testReadFailsForInvalidFileFormatVersion(byte[] invalidInput) throws ClassNotFoundException, IOException {
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(invalidInput));
    try {
      reader.readKnowledgePackages();
      Fail.fail("Exception expected");
    }
    catch (IllegalFileFormatException e) {
      assertThat(e.getMessage()).contains("Cannot read file format version from input stream");
    }
    catch (Exception e) {
      Fail.fail("Expected IllegalFileFormatException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @DataProvider
  private Object[][] getModuleWithInvalidFileFormatVersion() {
    return new Object[][] {
        {concat(VALID_MAGIC)},
        {concat(VALID_MAGIC, FILE_FORMAT_TOO_SHORT_6)},
        {concat(VALID_MAGIC, FILE_FORMAT_TOO_SHORT_7)},
    };
  }

  @Test(dataProvider = "getModuleWithInvalidDroolsRuntimeVersion")
  public void testReadFailsForInvalidDroolsRuntimeVersion(byte[] invalidInput) throws ClassNotFoundException, IOException {
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(invalidInput));
    try {
      reader.readKnowledgePackages();
      Fail.fail("Exception expected");
    }
    catch (IllegalFileFormatException e) {
      assertThat(e.getMessage()).contains("Cannot read drools version from input stream;");
    }
    catch (Exception e) {
      Fail.fail("Expected IllegalFileFormatException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @DataProvider
  private Object[][] getModuleWithInvalidDroolsRuntimeVersion() {
    return new Object[][] {
        /* no length => invalid */
        {concat(VALID_MAGIC, VALID_FILE_FORMAT_1, bytes())},
        /* length = 1; data = [] => invalid */
        {concat(VALID_MAGIC, VALID_FILE_FORMAT_1, bytes(0x00, 0x01))},
        /* length = 10; data length = 9 => invalid */
        {concat(VALID_MAGIC, VALID_FILE_FORMAT_1, bytes(0x00, 0x0a, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'))},
    };
  }

  @Test(dataProvider = "getModuleForFileFormatConversionTest")
  public void testReadsFileFormatVersion(byte[] inputBytes, long expectedVersion) throws ClassNotFoundException, IOException {
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(inputBytes));
    reader.setSupportedVersions(singleton(expectedVersion));
    try {
      reader.readKnowledgePackages();
      Fail.fail("Exception expected");
    }
    catch (InvalidDroolsRuntimeVersionException e) {
      assertVersionIsConvertedCorrectly(expectedVersion, reader.getFileHeader());
    }
    catch (Exception e) {
      Fail.fail("Expected InvalidDroolsRuntimeVersionException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  private void assertVersionIsConvertedCorrectly(long expectedVersion, DroolsKnowledgeModuleHeader fileHeader) {
    assertThat(fileHeader).as("File Header").isNotNull();
    assertThat(fileHeader.fileFormatVersion).as("File Format Version").isEqualTo(expectedVersion);
  }

  @DataProvider
  private Object[][] getModuleForFileFormatConversionTest() {
    return new Object[][] {
        {concat(VALID_MAGIC, bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01), DUMMY_DROOLS_VERSION), 1l},
        {concat(VALID_MAGIC, bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00), DUMMY_DROOLS_VERSION), 256l},
        {concat(VALID_MAGIC, bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00), DUMMY_DROOLS_VERSION), 65536l},
        {concat(VALID_MAGIC, bytes(0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00), DUMMY_DROOLS_VERSION), 16777216l},
        {concat(VALID_MAGIC, bytes(0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00), DUMMY_DROOLS_VERSION), 4294967296l},
        {concat(VALID_MAGIC, bytes(0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00), DUMMY_DROOLS_VERSION), 1099511627776l},
        {concat(VALID_MAGIC, bytes(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00), DUMMY_DROOLS_VERSION), 281474976710656l},
        {concat(VALID_MAGIC, bytes(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00), DUMMY_DROOLS_VERSION), 72057594037927936l},
    };
  }

  @Test
  public void testReadsDroolsRuntimeVersion() throws ClassNotFoundException, IOException {
    final byte[] inputBytes = concat(VALID_MAGIC, VALID_FILE_FORMAT_1, bytes(0x00, 0x05, '5', '.', '1', '.', '1', 'X'));
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(inputBytes));
    reader.setSupportedVersions(singleton(1l));
    try {
      reader.readKnowledgePackages();
      Fail.fail("Exception expected");
    }
    catch (EOFException e) {
      final DroolsKnowledgeModuleHeader fileHeader = reader.getFileHeader();
      assertThat(fileHeader.droolsRuntimeVersion).isEqualTo(KnowledgePackageImp.class.getPackage().getImplementationVersion());
    }
    catch (Exception e) {
      Fail.fail("Expected EOFException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @Test
  public void testRejectsMismatchingDroolsRuntimeVersion() throws ClassNotFoundException, IOException {
    final byte[] inputBytes = concat(VALID_MAGIC, VALID_FILE_FORMAT_1, bytes(0x00, 0x06, '5', '.', '1', '.', '1', 'X'));
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(inputBytes));
    reader.setSupportedVersions(singleton(1l));
    try {
      reader.readKnowledgePackages();
      Fail.fail("Expected InvalidDroolsRuntimeVersionException to be thrown, but no exception occurred at all.");
    }
    catch (InvalidDroolsRuntimeVersionException e) {
      assertThat(e.getMessage()).contains("5.1.1X");
      // caught by intention: this test expects InvalidDroolsRuntimeVersionException to be thrown
    }
    catch (Exception e) {
      Fail.fail("Expected InvalidDroolsRuntimeVersionException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @Test
  public void testRejectsInvalidFileFormatVersion() throws ClassNotFoundException, IOException {
    final byte[] inputBytes = concat(VALID_MAGIC, VALID_FILE_FORMAT_2, bytes(0x00, 0x05, '5', '.', '1', '.', '1'));
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(inputBytes));
    reader.setSupportedVersions(singleton(1l));
    try {
      reader.readKnowledgePackages();
      Fail.fail("Exception expected");
    }
    catch (InvalidFileFormatVersionException e) {
      assertThat(e.getMessage()).contains("format: 2");
    }
    catch (Exception e) {
      Fail.fail("Expected InvalidFileFormatVersionException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @Test(dataProvider = "getModuleWithInvalidMagic")
  public void testRejectsInvalidFileMagic(byte[] invalidData) throws ClassNotFoundException, IOException {
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(invalidData));
    reader.setSupportedVersions(singleton(1l));
    try {
      reader.readKnowledgePackages();
      Fail.fail("Exception expected");
    }
    catch (InvalidFileMagicException e) {
      assertThat(e.getMessage().contains(new String(invalidData)));
    }
    catch (Exception e) {
      Fail.fail("Expected InvalidFileMagicException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @DataProvider
  private Object[][] getModuleWithInvalidMagic() {
    return new Object[][] {
        {concat(bytes('D', 'R', 'L', 'K', 'M', 'O', 'D', 0x01), VALID_FILE_FORMAT_1, DROOLS_5_1_1)},
        {concat(bytes('H', 'E', 'L', 'L', 'O', 'M', 'E', 0x00), VALID_FILE_FORMAT_1, DROOLS_5_1_1)},
        {concat(bytes('D', 'R', 'L', 'K', 'M', 'O', 'E', 0x00), VALID_FILE_FORMAT_1, DROOLS_5_1_1)},
    };
  }

  @Test
  public void testReadsFileContent() {
    Fail.fail("Unimplemented test: write a test which makes sure that the implementation actually reads the knowledge packages from the input stream.");
  }
}
