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

public class KnowledgeModuleReaderImplTestData {

  public static final byte[] VALID_MAGIC = ArrayUtils.bytes('D', 'R', 'L', 'K', 'M', 'O', 'D', 0x00);
  public static final byte[] FILE_FORMAT_TOO_SHORT_7 = ArrayUtils.bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02);
  public static final byte[] FILE_FORMAT_TOO_SHORT_6 = ArrayUtils.bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x01);
  public static final byte[] VALID_FILE_FORMAT_1 = ArrayUtils.bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01);
  public static final byte[] VALID_FILE_FORMAT_2 = ArrayUtils.bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02);
  public static final byte[] DUMMY_DROOLS_VERSION = ArrayUtils.bytes(0x00, 0x01, 'X');
  public static final byte[] DROOLS_5_1_1 = ArrayUtils.bytes(0, 5, '5', '.', '1', '.', '1');
  public static final byte[] DROOLS_5_2_0_FINAL = ArrayUtils.bytes(0, 11, '5', '.', '2', '.', '0', '.', 'F', 'i', 'n', 'a', 'l');
}
