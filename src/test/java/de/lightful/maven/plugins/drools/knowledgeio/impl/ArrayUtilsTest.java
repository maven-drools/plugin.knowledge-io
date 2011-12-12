package de.lightful.maven.plugins.drools.knowledgeio.impl;

import org.testng.annotations.Test;

import static de.lightful.maven.plugins.drools.knowledgeio.impl.ArrayUtils.bytes;
import static de.lightful.maven.plugins.drools.knowledgeio.impl.ArrayUtils.concat;
import static org.fest.assertions.Assertions.assertThat;

@Test
public class ArrayUtilsTest {

  @Test
  public void testGeneratesSingleByteSequence() {
    assertThat(bytes(1)).isEqualTo(new byte[] {1});
  }

  @Test
  public void testGeneratesMultiByteSequence() {
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
  public void testCanAppendEmptySequence() {
    assertThat(concat(new byte[] {1, 2, 3}, new byte[] {})).isEqualTo(new byte[] {1, 2, 3});
  }

  @Test
  public void testCanPrependEmptydSequence() {
    assertThat(concat(new byte[] {}, new byte[] {1, 2, 3})).isEqualTo(new byte[] {1, 2, 3});
  }
}
