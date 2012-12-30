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

import de.lightful.maven.plugins.drools.knowledgeio.InvalidDroolsRuntimeVersionException;
import de.lightful.maven.plugins.drools.knowledgeio.VersionCheckStrategy;
import mockit.NonStrict;
import mockit.NonStrictExpectations;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;

import static de.lightful.maven.plugins.drools.knowledgeio.internal.KnowledgeModuleReaderImplTestData.VALID_FILE_FORMAT_1;
import static de.lightful.maven.plugins.drools.knowledgeio.internal.KnowledgeModuleReaderImplTestData.VALID_MAGIC;
import static java.util.Collections.singleton;
import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.fail;

@Test
public class KnowledgeModuleReaderImpl_dealingWithUnknownDroolsRuntime_Test {

  @NonStrict
  private Package droolsPackage;

  @NonStrict
  private KnowledgePackageImp knowledgePackageImp;

  @BeforeMethod
  public void setUp() {
    new NonStrictExpectations() {{
      droolsPackage.getImplementationTitle();
      result = null;
    }};

    new NonStrictExpectations() {{
      droolsPackage.getImplementationVersion();
      result = null;
    }};

    new NonStrictExpectations() {{
      KnowledgePackageImp.class.getPackage();
      result = droolsPackage;
    }};
  }

  @Test
  public void testCanHandleUnknownDroolsVersionIfVersionsShouldMatch() throws ClassNotFoundException, IOException {
    final byte[] inputBytes = ArrayUtils.concat(VALID_MAGIC, VALID_FILE_FORMAT_1, ArrayUtils.bytes(0x00, 0x06, '5', '.', '1', '.', '1', 'X'));
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(inputBytes));
    reader.setSupportedVersions(singleton(1l));
    try {
      reader.readKnowledgePackages(VersionCheckStrategy.VERSIONS_MUST_MATCH);
      fail("Expected InvalidDroolsRuntimeVersionException to be thrown, but no exception occurred at all.");
    }
    catch (InvalidDroolsRuntimeVersionException e) {
      assertThat(e.getMessage()).contains("null");
      assertThat(e.getMessage()).contains("5.1.1X");
      // caught by intention: this test expects InvalidDroolsRuntimeVersionException to be thrown
    }
    catch (Exception e) {
      fail("Expected InvalidDroolsRuntimeVersionException to be thrown, but actual exception was " + e.getClass().getSimpleName() + ".", e);
    }
  }

  @Test(expectedExceptions = EOFException.class /* reading content after header and verifying versions will lead to EOF */)
  public void testAcceptsUnknownDroolsRuntimeVersion() throws ClassNotFoundException, IOException {
    final byte[] inputBytes = ArrayUtils.concat(VALID_MAGIC, VALID_FILE_FORMAT_1, ArrayUtils.bytes(0x00, 0x06, '5', '.', '1', '.', '1', 'X'));
    KnowledgeModuleReaderImpl reader = new KnowledgeModuleReaderImpl(new ByteArrayInputStream(inputBytes));
    reader.setSupportedVersions(singleton(1l));
    reader.readKnowledgePackages(VersionCheckStrategy.IGNORE_UNKNOWN_RUNTIME_VERSION);
  }
}
