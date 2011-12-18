package de.lightful.maven.plugins.drools.knowledgeio.impl;

public class ExampleDroolsCode {

  public static final String THREE_SIMPLE_RULES =
      "package org.example;\n" +
      "dialect \"java\"\n" +
      "\n" +
      "rule \"one\"\n" +
      "  when\n" +
      "  then\n" +
      "    System.out.println(\"Hello\");\n" +
      "end\n" +
      "\n" +
      "rule \"two\"\n" +
      "  when\n" +
      "  then\n" +
      "    System.out.println(\"Hello\");\n" +
      "end\n" +
      "\n" +
      "rule \"three\"\n" +
      "  when\n" +
      "  then\n" +
      "    System.out.println(\"Hello\");\n" +
      "end\n";
}