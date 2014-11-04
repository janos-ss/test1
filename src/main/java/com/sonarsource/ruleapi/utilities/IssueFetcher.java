/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.net.URI;

/**
 * Retrieves Issue from Jira by key
 */
public class IssueFetcher {

  private static final String RSPEC = "RSPEC-";
  private static final String LEGACY_SEARCH1 = "project%3DRSPEC%20AND%20\"Legacy%20Key\"~\"";
  private static final String LEGACY_SEARCH2 = "\"";
  private static final URI SERVER_URI = URI.create("http://jira.sonarsource.com/");

  public IssueFetcher() {
  }

  /**
   * Retrieves Jira Issue by Jira id (RSPEC-###)
   * or implementation id (S###) or legacy key.
   *
   * If no match is found, or if multiple results
   * are found for a legacy key search,
   * null is returned.
   *
   * @param key the key to search by.
   * @return Populated Issue retrieved from Jira or null
   */
  public Issue fetch(String key) {

    Issue issue = null;
    AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

    if (key.matches("S?[0-9]+")) {
      issue = getIssueByKey(factory, RSPEC + key.replaceFirst("S", ""));
    } else if (key.matches(RSPEC+"[0-9]+")) {
      issue = getIssueByKey(factory, key);
    } else {
      issue = getIssueByLegacyKey(factory, LEGACY_SEARCH1 + key + LEGACY_SEARCH2);
    }

    return issue;
  }

  private Issue getIssueByKey(JiraRestClientFactory factory, String issueKey) {
    JiraRestClient client = factory.create(SERVER_URI, new AnonymousAuthenticationHandler());
    return client.getIssueClient().getIssue(issueKey).claim();
  }

  private Issue getIssueByLegacyKey(JiraRestClientFactory factory, String searchString) {
    JiraRestClient client = factory.create(SERVER_URI, new AnonymousAuthenticationHandler());
    SearchResult sr = client.getSearchClient().searchJql(searchString).claim();

    if (sr.getTotal() == 1) {
      return getIssueByKey(factory, sr.getIssues().iterator().next().getKey());
    } else {
      return null;
    }
  }

}
