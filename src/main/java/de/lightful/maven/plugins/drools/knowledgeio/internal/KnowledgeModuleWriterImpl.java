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
