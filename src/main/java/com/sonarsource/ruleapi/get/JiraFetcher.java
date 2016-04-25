/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import org.json.simple.JSONObject;

import java.util.List;

public interface JiraFetcher {

  /**
   * Retrieves Jira Issue by Jira id (RSPEC-###)
   * or implementation id (S###) or legacy key.
   *
   * If no match is found, <code>null</code> is returned.
   * Throws a <code>RuleException</code> when several matches are found for a legacy key search.
   *
   * @param key the key to search by.
   * @return Populated Issue retrieved from Jira or null
   */
  JSONObject fetchIssueByKey(String key);

  List<JSONObject> fetchIssuesBySearch(String search);
}
