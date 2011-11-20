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
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;

@Test
public class KnowledgeModuleReaderImplTest {

  @Test(dataProvider = "getModuleWithIncompleteMagic")
  public void testReadFailsForIncompleteMagic(byte[] invalidInput) throws ClassNotFoundException, IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidInput);
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(inputStream);
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
        {new byte[] {}},
        {new byte[] {'A', 'B', 'C'}},
        {new byte[] {'D', 'R', 'L'}},
        {new byte[] {'D', 'R', 'L', 'K', 'M', 'O', 'D'}},
    };
  }

  @Test(dataProvider = "getModuleWithInvalidFileFormatVersion")
  public void testReadFailsForInvalidFileFormatVersion(byte[] invalidInput) throws ClassNotFoundException, IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidInput);
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(inputStream);
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
        {new byte[] {'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00}},
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
        }},
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
        }},
    };
  }

  @Test(dataProvider = "getModuleWithInvalidDroolsRuntimeVersion")
  public void testReadFailsForInvalidDroolsRuntimeVersion(byte[] invalidInput) throws ClassNotFoundException, IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidInput);
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(inputStream);
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
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
            // drools runtime version:

        }},
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
            // drools runtime version: length = 1; data = [] => invalid
            0x00, 0x01
        }},
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
            // drools runtime version: length = 10; data length = 9 => invalid
            0x00, 0x0a, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'
        }},
    };
  }

  @Test(dataProvider = "getModuleForFileFormatConversionTest")
  public void testReadsFileFormatVersion(byte[] inputBytes, long expectedVersion) throws ClassNotFoundException, IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(inputStream);
    reader.setSupportedVersions(Collections.singleton(expectedVersion));
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
  private Object[][] getModuleForFileFormatConversionTest
      () {
    return new Object[][] {
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
            // drools runtime version: length = 1; data length = 1
            0x00, 0x01, 'X'
        }, 1l},
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00,
            // drools runtime version: length = 1; data length = 1
            0x00, 0x01, 'X'
        }, 256l},
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00,
            // drools runtime version: length = 1; data length = 1
            0x00, 0x01, 'X'
        }, 65536l},
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00,
            // drools runtime version: length = 1; data length = 1
            0x00, 0x01, 'X'
        }, 16777216l},
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00,
            // drools runtime version: length = 1; data length = 1
            0x00, 0x01, 'X'
        }, 4294967296l},
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
            // drools runtime version: length = 1; data length = 1
            0x00, 0x01, 'X'
        }, 1099511627776l},
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            // drools runtime version: length = 1; data length = 1
            0x00, 0x01, 'X'
        }, 281474976710656l},
        {new byte[] {
            // magic:
            'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
            // file format version:
            0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            // drools runtime version: length = 1; data length = 1
            0x00, 0x01, 'X'
        }, 72057594037927936l},
    };
  }

  @Test
  public void testReadsDroolsRuntimeVersion() throws ClassNotFoundException, IOException {
    final byte[] inputBytes = new byte[] {
        'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
        // file format version:
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
        // drools runtime version: length = 1; data length = 1
        0x00, 0x05, '5', '.', '1', '.', '1', 'X'
    };
    ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(inputStream);
    reader.setSupportedVersions(Collections.singleton(1l));
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
    final byte[] inputBytes = new byte[] {
        'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
        // file format version:
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
        // drools runtime version: length = 1; data length = 1
        0x00, 0x06, '5', '.', '1', '.', '1', 'X'
    };
    ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(inputStream);
    reader.setSupportedVersions(Collections.singleton(1l));
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
    final byte[] inputBytes = new byte[] {
        'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00,
        // file format version:
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
        // drools runtime version: length = 1; data length = 1
        0x00, 0x05, '5', '.', '1', '.', '1'
    };
    ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(inputStream);
    reader.setSupportedVersions(Collections.singleton(1l));
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
    ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidData);
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(inputStream);
    reader.setSupportedVersions(Collections.singleton(1l));
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
        {new byte[] {'D', 'R', 'L', 'K', 'M', 'O', 'D', 0x01, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, '5', '.', '1', '.', '1'}},
        {new byte[] {'H', 'E', 'L', 'L', 'O', 'M', 'E', 0x00, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, '5', '.', '1', '.', '1'}},
        {new byte[] {'D', 'R', 'L', 'K', 'M', 'O', 'E', 0x00, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, '5', '.', '1', '.', '1'}},
    };
  }

  @Test
  public void testReadsFileContent() {
    Fail.fail("Unimplemented test: write a test which makes sure that the implementation actually reads the knowledge packages from the input stream.");
  }
}
