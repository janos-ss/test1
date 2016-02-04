/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;


/**
 * Not every standard has fixed id's. The CERT standards (C, C++, Java)
 * are contained in a wiki. That gets edited. So sometimes the titles of
 * CERT rules get changed. Sometimes the id's do too.
 *
 * A cleanup report should be a list of id's and/or titles that have
 * changed in the source but not in RSpec.
 */
public interface CleanupReport {

  String generateCleanupReport();

}
