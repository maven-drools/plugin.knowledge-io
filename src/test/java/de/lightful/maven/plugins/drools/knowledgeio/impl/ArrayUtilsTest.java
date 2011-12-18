package de.lightful.maven.plugins.drools.knowledgeio.impl;

import org.testng.annotations.Test;

import static de.lightful.maven.plugins.drools.knowledgeio.impl.ArrayUtils.*;
import static org.fest.assertions.Assertions.assertThat;

@Test
public class ArrayUtilsTest {

  @Test
  public void testConcatGeneratesSingleByteSequence() {
    assertThat(bytes(1)).isEqualTo(new byte[] {1});
  }

  @Test
  public void testConcatGeneratesMultiByteSequence() {
    assertThat(bytes(1, 2, 3, 4, 5)).isEqualTo(new byte[] {1, 2, 3, 4, 5});
  }

  @Test
  public void testConcatTwoThreeByteSequences() {
    assertThat(concat(new byte[] {1, 2, 3}, new byte[] {4, 5, 6})).isEqualTo(new byte[] {1, 2, 3, 4, 5, 6});
  }

  @Test
  public void testCanConcatThreeTwoByteSequences() {
    assertThat(concat(new byte[] {1, 2}, new byte[] {3, 4}, new byte[] {5, 6})).isEqualTo(new byte[] {1, 2, 3, 4, 5, 6});
  }

  @Test
  public void testConcatCanAppendEmptySequence() {
    assertThat(concat(new byte[] {1, 2, 3}, new byte[] {})).isEqualTo(new byte[] {1, 2, 3});
  }

  @Test
  public void testConcatCanPrependEmptySequence() {
    assertThat(concat(new byte[] {}, new byte[] {1, 2, 3})).isEqualTo(new byte[] {1, 2, 3});
  }

  @Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
  public void testSliceThrowsExceptionIfSourceArrayTooSmall() {
    slice(new byte[] {}, 0, 1);
  }

  @Test
  public void testSliceCanExtractFirstByteOfArray() {
    assertThat(slice(new byte[] {1, 2, 3, 4}, 0, 1)).isEqualTo(new byte[] {1});
  }

  public void testSliceRespectsOffset() {
    assertThat(slice(new byte[] {1, 2, 3, 4}, 2, 1)).isEqualTo(new byte[] {3});
  }

  public void testSliceRespectsLength() {
    assertThat(slice(new byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 3, 4)).isEqualTo(new byte[] {4, 5, 6, 7});
  }
}
