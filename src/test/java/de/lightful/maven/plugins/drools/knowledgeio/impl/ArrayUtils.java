package de.lightful.maven.plugins.drools.knowledgeio.impl;

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
}
