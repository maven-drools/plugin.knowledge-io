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

public class ArrayUtils {

  public static byte[] concat(byte[]... byteArrays) {
    int totalLength = 0;
    for (byte[] byteArray : byteArrays) {
      totalLength += byteArray.length;
    }

    byte[] destination = new byte[totalLength];

    int destinationOffset = 0;
    for (byte[] sourceArray : byteArrays) {
      System.arraycopy(sourceArray, 0, destination, destinationOffset, sourceArray.length);
      destinationOffset += sourceArray.length;
    }
    return destination;
  }

  public static byte[] bytes(int... byteValues) {
    byte[] result = new byte[byteValues.length];
    for (int i = 0; i < byteValues.length; i++) {
      result[i] = (byte) byteValues[i];
    }
    return result;
  }

  public static byte[] slice(byte[] source, int offset, int length) {
    byte[] result = new byte[length];
    System.arraycopy(source, offset, result, 0, length);
    return result;
  }
}
