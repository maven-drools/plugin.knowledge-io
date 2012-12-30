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

package de.lightful.maven.plugins.drools.knowledgeio;

/**
 * Indicates how to perform the version check between version recorded in DKM file and
 * actual Drools runtime version. Required to deal with non-standard Java runtime environments,
 * like Google App Engine, which does not support using reflection on Drools library classes.
 *
 * @since 0.3.1
 * @see KnowledgeModuleReader#readKnowledgePackages(VersionCheckStrategy)
 */
public enum VersionCheckStrategy {

  /**
   * Check version from DKM against actual runtime version.
   * Throw {@link InvalidDroolsRuntimeVersionException} on mismatch.
   * If actual runtime version is unknown, consider this a mismatch.
   * Use this value in a standard J2SE/J2EE environment.
   **/
  VERSIONS_MUST_MATCH,

  /**
   * Check version from DKM against actual runtime version.
   * If actual runtime version is unknown, continue action anyway.
   * Throw {@link InvalidDroolsRuntimeVersionException} on mismatch.
   * Use this value in Google App Engine.
   */
  IGNORE_UNKNOWN_RUNTIME_VERSION,

}
