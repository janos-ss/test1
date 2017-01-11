/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import org.json.simple.JSONObject;

import java.util.List;

public interface SQFetcher {
  /**
   * Retrieves raw JSON rules from a running SonarQube instance by query.
   *
   * @param instance base SonarQube instance address. E.G. https://sonarqube.com
   * @param search query to execute in query string format (with '&amp;' separating parameters)
   *               E. G. repositories=c
   * @return list of retrieved JSON rules
   */
  List<JSONObject> fetchRulesFromSonarQube(String instance, String search);

  List<JSONObject> fetchProfilesFromSonarQube(String instance);
}
