/*
 * Copyright (c) 2009-2011 Ansgar Konermann
 *
 * This file is part of the Maven 3 Drools Plugin.
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
 */
package de.lightful.maven.plugins.drools.knowledgeio;

import org.apache.maven.plugin.logging.Log;

public class InfoLogger implements Logger<InfoLogger> {

  private static final String NEWLINE = System.getProperty("line.separator");
  private StringBuilder stringBuilder = new StringBuilder();
  private Log mavenLog;

  public InfoLogger(Log mavenLog) {
    this.mavenLog = mavenLog;
  }

  public InfoLogger log(String message) {
    stringBuilder.append(message);
    if (message.endsWith(NEWLINE)) {
      return nl();
    }
    else {
      return this;
    }
  }

  public InfoLogger nl() {
    mavenLog.info(stringBuilder.toString());
    stringBuilder.setLength(0);
    return this;
  }
}