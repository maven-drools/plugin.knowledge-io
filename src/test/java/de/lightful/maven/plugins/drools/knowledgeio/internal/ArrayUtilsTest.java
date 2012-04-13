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

import org.fest.assertions.Assertions;
import org.testng.annotations.Test;

@Test
public class ArrayUtilsTest {

  @Test
  public void testConcatGeneratesSingleByteSequence() {
    Assertions.assertThat(ArrayUtils.bytes(1)).isEqualTo(new byte[] {1});
  }

  @Test
  public void testConcatGeneratesMultiByteSequence() {
    Assertions.assertThat(ArrayUtils.bytes(1, 2, 3, 4, 5)).isEqualTo(new byte[] {1, 2, 3, 4, 5});
  }

  @Test
  public void testConcatTwoThreeByteSequences() {
    Assertions.assertThat(ArrayUtils.concat(new byte[] {1, 2, 3}, new byte[] {4, 5, 6})).isEqualTo(new byte[] {1, 2, 3, 4, 5, 6});
  }

  @Test
  public void testCanConcatThreeTwoByteSequences() {
    Assertions.assertThat(ArrayUtils.concat(new byte[] {1, 2}, new byte[] {3, 4}, new byte[] {5, 6})).isEqualTo(new byte[] {1, 2, 3, 4, 5, 6});
  }

  @Test
  public void testConcatCanAppendEmptySequence() {
    Assertions.assertThat(ArrayUtils.concat(new byte[] {1, 2, 3}, new byte[] {})).isEqualTo(new byte[] {1, 2, 3});
  }

  @Test
  public void testConcatCanPrependEmptySequence() {
    Assertions.assertThat(ArrayUtils.concat(new byte[] {}, new byte[] {1, 2, 3})).isEqualTo(new byte[] {1, 2, 3});
  }

  @Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
  public void testSliceThrowsExceptionIfSourceArrayTooSmall() {
    ArrayUtils.slice(new byte[] {}, 0, 1);
  }

  @Test
  public void testSliceCanExtractFirstByteOfArray() {
    Assertions.assertThat(ArrayUtils.slice(new byte[] {1, 2, 3, 4}, 0, 1)).isEqualTo(new byte[] {1});
  }

  public void testSliceRespectsOffset() {
    Assertions.assertThat(ArrayUtils.slice(new byte[] {1, 2, 3, 4}, 2, 1)).isEqualTo(new byte[] {3});
  }

  public void testSliceRespectsLength() {
    Assertions.assertThat(ArrayUtils.slice(new byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 3, 4)).isEqualTo(new byte[] {4, 5, 6, 7});
  }
}
