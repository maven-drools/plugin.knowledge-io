/*******************************************************************************
 * Copyright (c) 2009-2012 Ansgar Konermann
 *
 * This file is part of the "Maven 3 Drools Support" Package.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.lightful.maven.plugins.drools.knowledgeio.internal;

import de.lightful.maven.plugins.drools.knowledgeio.IllegalFileFormatException;
import de.lightful.maven.plugins.drools.knowledgeio.InvalidDroolsRuntimeVersionException;
import de.lightful.maven.plugins.drools.knowledgeio.InvalidFileFormatVersionException;
import de.lightful.maven.plugins.drools.knowledgeio.InvalidFileMagicException;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.io.ResourceFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Collection;

import static de.lightful.maven.plugins.drools.knowledgeio.internal.KnowledgeModuleReaderImplTestData.*;
import static java.util.Collections.singleton;
import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.fail;

@Test
public class KnowledgeModuleReaderImplTest {

  @Test
  public void testFileFormatTooShort() {
    assertThat(FILE_FORMAT_TOO_SHORT_6.length).isEqualTo(6);
    assertThat(FILE_FORMAT_TOO_SHORT_7.length).isEqualTo(7);
  }

  @Test(dataProvider = "getModuleWithIncompleteMagic")
  public void testReadFailsForIncompleteMagic(byte[] invalidInput) throws ClassNotFoundException, IOException {
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(invalidInput));
    try {
      reader.readKnowledgePackages();
      fail("Exception expected");
    }
    catch (IllegalFileFormatException e) {
      assertThat(e.getMessage()).contains("Cannot read file magic from input stream");
    }
    catch (Exception e) {
      fail("Expected IllegalFileFormatException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @DataProvider
  private Object[][] getModuleWithIncompleteMagic() {
    return new Object[][] {
        {ArrayUtils.bytes()},
        {ArrayUtils.bytes('A', 'B', 'C')},
        {ArrayUtils.bytes('D', 'R', 'L')},
        {ArrayUtils.bytes('D', 'R', 'L', 'K', 'M', 'O', 'D')},
    };
  }

  @Test(dataProvider = "getModuleWithInvalidFileFormatVersion")
  public void testReadFailsForInvalidFileFormatVersion(byte[] invalidInput) throws ClassNotFoundException, IOException {
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(invalidInput));
    try {
      reader.readKnowledgePackages();
      fail("Exception expected");
    }
    catch (IllegalFileFormatException e) {
      assertThat(e.getMessage()).contains("Cannot read file format version from input stream");
    }
    catch (Exception e) {
      fail("Expected IllegalFileFormatException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @DataProvider
  private Object[][] getModuleWithInvalidFileFormatVersion() {
    return new Object[][] {
        {ArrayUtils.concat(VALID_MAGIC)},
        {ArrayUtils.concat(VALID_MAGIC, FILE_FORMAT_TOO_SHORT_6)},
        {ArrayUtils.concat(VALID_MAGIC, FILE_FORMAT_TOO_SHORT_7)},
    };
  }

  @Test(dataProvider = "getModuleWithInvalidDroolsRuntimeVersion")
  public void testReadFailsForInvalidDroolsRuntimeVersion(byte[] invalidInput) throws ClassNotFoundException, IOException {
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(invalidInput));
    try {
      reader.readKnowledgePackages();
      fail("Exception expected");
    }
    catch (IllegalFileFormatException e) {
      assertThat(e.getMessage()).contains("Cannot read drools version from input stream;");
    }
    catch (Exception e) {
      fail("Expected IllegalFileFormatException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @DataProvider
  private Object[][] getModuleWithInvalidDroolsRuntimeVersion() {
    return new Object[][] {
        /* no length => invalid */
        {ArrayUtils.concat(VALID_MAGIC, VALID_FILE_FORMAT_1, ArrayUtils.bytes())},
        /* length = 1; data = [] => invalid */
        {ArrayUtils.concat(VALID_MAGIC, VALID_FILE_FORMAT_1, ArrayUtils.bytes(0x00, 0x01))},
        /* length = 10; data length = 9 => invalid */
        {ArrayUtils.concat(VALID_MAGIC, VALID_FILE_FORMAT_1, ArrayUtils.bytes(0x00, 0x0a, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'))},
    };
  }

  @Test(dataProvider = "getModuleForFileFormatConversionTest")
  public void testReadsFileFormatVersion(byte[] inputBytes, long expectedVersion) throws ClassNotFoundException, IOException {
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(inputBytes));
    reader.setSupportedVersions(singleton(expectedVersion));
    try {
      reader.readKnowledgePackages();
      fail("Exception expected");
    }
    catch (InvalidDroolsRuntimeVersionException e) {
      assertVersionIsConvertedCorrectly(expectedVersion, reader.getFileHeader());
    }
    catch (Exception e) {
      fail("Expected InvalidDroolsRuntimeVersionException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  private void assertVersionIsConvertedCorrectly(long expectedVersion, DroolsKnowledgeModuleHeader fileHeader) {
    assertThat(fileHeader).as("File Header").isNotNull();
    assertThat(fileHeader.fileFormatVersion).as("File Format Version").isEqualTo(expectedVersion);
  }

  @DataProvider
  private Object[][] getModuleForFileFormatConversionTest() {
    return new Object[][] {
        {ArrayUtils.concat(VALID_MAGIC, ArrayUtils.bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01), DUMMY_DROOLS_VERSION), 1l},
        {ArrayUtils.concat(VALID_MAGIC, ArrayUtils.bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00), DUMMY_DROOLS_VERSION), 256l},
        {ArrayUtils.concat(VALID_MAGIC, ArrayUtils.bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00), DUMMY_DROOLS_VERSION), 65536l},
        {ArrayUtils.concat(VALID_MAGIC, ArrayUtils.bytes(0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00), DUMMY_DROOLS_VERSION), 16777216l},
        {ArrayUtils.concat(VALID_MAGIC, ArrayUtils.bytes(0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00), DUMMY_DROOLS_VERSION), 4294967296l},
        {ArrayUtils.concat(VALID_MAGIC, ArrayUtils.bytes(0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00), DUMMY_DROOLS_VERSION), 1099511627776l},
        {ArrayUtils.concat(VALID_MAGIC, ArrayUtils.bytes(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00), DUMMY_DROOLS_VERSION), 281474976710656l},
        {ArrayUtils.concat(VALID_MAGIC, ArrayUtils.bytes(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00), DUMMY_DROOLS_VERSION), 72057594037927936l},
    };
  }

  @Test
  public void testReadsDroolsRuntimeVersion() throws ClassNotFoundException, IOException {
    final byte[] inputBytes = ArrayUtils.concat(VALID_MAGIC, VALID_FILE_FORMAT_1, ArrayUtils.bytes(0, 11, '5', '.', '2', '.', '0', '.', 'F', 'i', 'n', 'a', 'l', 'X'));
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(inputBytes));
    reader.setSupportedVersions(singleton(1l));
    try {
      reader.readKnowledgePackages();
      fail("Exception expected");
    }
    catch (EOFException e) {
      final DroolsKnowledgeModuleHeader fileHeader = reader.getFileHeader();
      assertThat(fileHeader.droolsRuntimeVersion).isEqualTo(KnowledgePackageImp.class.getPackage().getImplementationVersion());
    }
    catch (Exception e) {
      fail("Expected EOFException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @Test
  public void testRejectsMismatchingDroolsRuntimeVersion() throws ClassNotFoundException, IOException {
    final byte[] inputBytes = ArrayUtils.concat(VALID_MAGIC, VALID_FILE_FORMAT_1, ArrayUtils.bytes(0x00, 0x06, '5', '.', '1', '.', '1', 'X'));
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(inputBytes));
    reader.setSupportedVersions(singleton(1l));
    try {
      reader.readKnowledgePackages();
      fail("Expected InvalidDroolsRuntimeVersionException to be thrown, but no exception occurred at all.");
    }
    catch (InvalidDroolsRuntimeVersionException e) {
      assertThat(e.getMessage()).contains("5.1.1X");
      // caught by intention: this test expects InvalidDroolsRuntimeVersionException to be thrown
    }
    catch (Exception e) {
      fail("Expected InvalidDroolsRuntimeVersionException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @Test
  public void testRejectsInvalidFileFormatVersion() throws ClassNotFoundException, IOException {
    final byte[] inputBytes = ArrayUtils.concat(VALID_MAGIC, VALID_FILE_FORMAT_2, ArrayUtils.bytes(0x00, 0x05, '5', '.', '1', '.', '1'));
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(inputBytes));
    reader.setSupportedVersions(singleton(1l));
    try {
      reader.readKnowledgePackages();
      fail("Exception expected");
    }
    catch (InvalidFileFormatVersionException e) {
      assertThat(e.getMessage()).contains("format: 2");
    }
    catch (Exception e) {
      fail("Expected InvalidFileFormatVersionException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @Test(dataProvider = "getModuleWithInvalidMagic")
  public void testRejectsInvalidFileMagic(byte[] invalidData) throws ClassNotFoundException, IOException {
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(invalidData));
    reader.setSupportedVersions(singleton(1l));
    try {
      reader.readKnowledgePackages();
      fail("Exception expected");
    }
    catch (InvalidFileMagicException e) {
      assertThat(e.getMessage().contains(new String(invalidData)));
    }
    catch (Exception e) {
      fail("Expected InvalidFileMagicException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @DataProvider
  private Object[][] getModuleWithInvalidMagic() {
    return new Object[][] {
        {ArrayUtils.concat(ArrayUtils.bytes('D', 'R', 'L', 'K', 'M', 'O', 'D', 0x01), VALID_FILE_FORMAT_1, DROOLS_5_1_1)},
        {ArrayUtils.concat(ArrayUtils.bytes('H', 'E', 'L', 'L', 'O', 'M', 'E', 0x00), VALID_FILE_FORMAT_1, DROOLS_5_1_1)},
        {ArrayUtils.concat(ArrayUtils.bytes('D', 'R', 'L', 'K', 'M', 'O', 'E', 0x00), VALID_FILE_FORMAT_1, DROOLS_5_1_1)},
    };
  }

  @Test
  public void testReadsFileContent() throws IOException, ClassNotFoundException {
    final KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    knowledgeBuilder.add(ResourceFactory.newByteArrayResource(ExampleDroolsCode.THREE_SIMPLE_RULES.getBytes()), ResourceType.DRL);
    assertThat(knowledgeBuilder.hasErrors()).as("Knowledge Builder's hasErrors Flag").isFalse();
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    DroolsStreamUtils.streamOut(outputStream, knowledgeBuilder.getKnowledgePackages(), true);

    final byte[] input = ArrayUtils.concat(VALID_MAGIC, VALID_FILE_FORMAT_1, DROOLS_5_2_0_FINAL, outputStream.toByteArray());

    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(input));
    final Collection<KnowledgePackage> actualKnowledgePackages = reader.readKnowledgePackages();

    assertThat(actualKnowledgePackages).hasSize(1);
    final Collection<Rule> rules = actualKnowledgePackages.iterator().next().getRules();
    assertThat(rules).hasSize(3);
  }
}
