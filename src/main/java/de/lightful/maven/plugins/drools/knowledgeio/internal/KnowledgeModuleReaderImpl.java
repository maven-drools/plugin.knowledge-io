/*******************************************************************************
 * Copyright (c) 2009-2011 Ansgar Konermann
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

import de.lightful.maven.plugins.drools.knowledgeio.*;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

import static org.fest.assertions.Assertions.assertThat;

public class KnowledgeModuleReaderImpl implements KnowledgeModuleReader {

  private Set<Long> supportedVersions = Collections.singleton(1l);

  private InputStream inputStream;
  private ClassLoader classLoader;
  private DroolsKnowledgeModuleHeader header = null;

  public KnowledgeModuleReaderImpl(InputStream inputStream) {
    this(inputStream, Thread.currentThread().getContextClassLoader());
  }

  public KnowledgeModuleReaderImpl(InputStream inputStream, ClassLoader classLoader) {
    this.inputStream = inputStream;
    this.classLoader = classLoader;
  }

  public Collection<KnowledgePackage> readKnowledgePackages() throws IOException, ClassNotFoundException {
    if (this.header == null) {
      this.header = readHeader();
    }
    ensureHeaderIsValid(header);
    ensureDroolsRuntimeMatches(header.droolsRuntimeVersion);
    return readContent();
  }

  DroolsKnowledgeModuleHeader getFileHeader() {
    return header;
  }

  void setSupportedVersions(Set<Long> supportedVersions) {
    this.supportedVersions = supportedVersions;
  }

  private void ensureDroolsRuntimeMatches(String expectedRuntimeVersion) {
    final Package droolsCorePackage = KnowledgePackageImp.class.getPackage();
    final String implementationTitle = droolsCorePackage.getImplementationTitle();
    final String implementationVersion = droolsCorePackage.getImplementationVersion();
    if (!implementationVersion.equals(expectedRuntimeVersion)) {
      throw new InvalidDroolsRuntimeVersionException(
          "Drools runtime versions must match: you're trying to load a Drools Knowledge Module compiled " +
          "for Drools " + expectedRuntimeVersion + ", but there's '" + implementationTitle + "' version " +
          implementationVersion + " on the classpath.");
    }
  }

  private DroolsKnowledgeModuleHeader readHeader() throws IOException {
    try {
      DroolsKnowledgeModuleHeader header = new DroolsKnowledgeModuleHeader();
      header.magic = readFileMagic();
      header.fileFormatVersion = readFileFormatVersion();
      header.droolsRuntimeVersion = readDroolsRuntimeVersion();
      return header;
    }
    catch (IOException ioe) {
      throw new IllegalFileFormatException("Cannot read file header from input stream.", ioe);
    }
  }

  private String readDroolsRuntimeVersion() throws IOException {
    final int lengthOfShort = Short.SIZE / 8;
    final byte[] lengthBuffer = new byte[lengthOfShort];
    final int lengthReadForStringLength = inputStream.read(lengthBuffer, 0, lengthOfShort);
    if (lengthReadForStringLength != lengthOfShort) {
      throw new IllegalFileFormatException("Cannot read drools version from input stream; reading string length failed (unable to read " + lengthOfShort + " bytes, only got " + lengthReadForStringLength + ").");
    }
    ByteBuffer lengthOfStringBuffer = ByteBuffer.wrap(lengthBuffer);
    final short lengthOfVersionString = lengthOfStringBuffer.getShort();
    final byte[] versionStringBytes = new byte[lengthOfVersionString];
    final int lengthReadForVersionString = inputStream.read(versionStringBytes, 0, lengthOfVersionString);
    if (lengthReadForVersionString != lengthOfVersionString) {
      throw new IllegalFileFormatException("Cannot read drools version from input stream; reading version string failed (unable to read  " + lengthOfVersionString + " bytes, only got " + lengthReadForVersionString + ").");
    }
    return new String(versionStringBytes, "UTF-8");
  }

  private byte[] readFileMagic() throws IOException {
    byte[] magic = new byte[KnowledgeModule.FILE_MAGIC.length];
    final int lengthRead = inputStream.read(magic, 0, KnowledgeModule.FILE_MAGIC.length);
    if (lengthRead != KnowledgeModule.FILE_MAGIC.length) {
      throw new IllegalFileFormatException("Cannot read file magic from input stream (unable to read " + KnowledgeModule.FILE_MAGIC.length + " bytes, only got " + lengthRead + ").");
    }
    return magic;
  }

  private long readFileFormatVersion() throws IOException {
    final int longSize = Long.SIZE / 8;
    byte[] versionBytes = new byte[longSize];
    final int lengthRead = inputStream.read(versionBytes, 0, longSize);
    if (longSize != lengthRead) {
      throw new IllegalFileFormatException("Cannot read file format version from input stream (unable to read " + longSize + " bytes, only got " + lengthRead + ").");
    }
    return ByteBuffer.wrap(versionBytes).getLong();
  }

  private void ensureHeaderIsValid(DroolsKnowledgeModuleHeader header) {
    if (!Arrays.equals(KnowledgeModule.FILE_MAGIC, header.magic)) {
      throw new InvalidFileMagicException("Unexpected file magic in header: " + new String(header.magic));
    }
    if (!isSupportedVersion(header.fileFormatVersion)) {
      throw new InvalidFileFormatVersionException("Unsupported version of file format: " + header.fileFormatVersion);
    }
    if (header.droolsRuntimeVersion.length() == 0) {
      throw new IllegalFileFormatException("Illegal drools runtime version in file header: must not be empty.");
    }
  }

  private Collection<KnowledgePackage> readContent() throws IOException, ClassNotFoundException {
    Object streamedInObject = DroolsStreamUtils.streamIn(inputStream, classLoader, true);
    assertThat(streamedInObject).as("object read from stream").isNotNull().isInstanceOf(Collection.class);

    Collection loadedObjects = Collection.class.cast(streamedInObject);
    ensureLoadedObjectsAreKnowledgePackages(loadedObjects);
    Collection<KnowledgePackage> knowledgePackages = convertCollectionItemsToKnowledgePackages(loadedObjects);
    return knowledgePackages;
  }

  private boolean isSupportedVersion(long fileFormatVersion) {
    return supportedVersions.contains(fileFormatVersion);
  }

  private void ensureLoadedObjectsAreKnowledgePackages(Collection loadedObjects) {
    int i = 1;
    for (Object loadedObject : loadedObjects) {
      assertThat(loadedObject)
          .as("object #" + i + " from read collection (" + loadedObject.toString() + ")")
          .isInstanceOf(KnowledgePackage.class);
      i++;
    }
  }

  @SuppressWarnings("unchecked")
  private Collection<KnowledgePackage> convertCollectionItemsToKnowledgePackages(Collection loadedObjects) {
    Collection<KnowledgePackage> knowledgePackages = new ArrayList<KnowledgePackage>();
    knowledgePackages.addAll(loadedObjects);
    return knowledgePackages;
  }
}
