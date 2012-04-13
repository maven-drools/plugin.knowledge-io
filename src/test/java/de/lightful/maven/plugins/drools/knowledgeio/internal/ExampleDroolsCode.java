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