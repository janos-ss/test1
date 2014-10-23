/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.rule_compare.utilities;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.sonarsource.rule_compare.domain.Rule;

import java.net.URI;

/**
 * Created by ganncamp on 9/21/14.
 */
public class IssueFetcher {

  /*
   * get custom fields names
   * http://jira.sonarsource.com/rest/api/latest/issue/createmeta?projectKeys=RSPEC&issuetypeName=Specification&expand=projects.issuetypes.
   * fields
   */

  // add ISSUE to get by key, add SEARCH to get by jql query

  private static final String RSPEC = "RSPEC-";
  private static final String LEGACY_SEARCH1 = "jql=project%3DRSPEC%20AND%20\"Legacy%20Key\"~\"";
  private static final String LEGACY_SEARCH2 = "\"";
  private static final URI serverUri = URI.create("http://jira.sonarsource.com/");

  private String key = null;
  private String legacyKey = null;

  public IssueFetcher() {
  }

  public Issue fetch(String key) {

    Rule rule = null;
    Issue issue = null;
    AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

    if (key.matches("S[0-9]+"))
    {
      issue = getIssueByKey(factory, RSPEC + key.replaceFirst("S", ""));
    }
    else
    {
      issue = getIssueByLegacyKey(factory, LEGACY_SEARCH1 + key + LEGACY_SEARCH2);
    }

    return issue;
  }

  private Issue getIssueByKey(JiraRestClientFactory factory, String issueKey) {
    JiraRestClient client = factory.create(serverUri, new AnonymousAuthenticationHandler());
    return client.getIssueClient().getIssue(issueKey).claim();
  }

  private Issue getIssueByLegacyKey(JiraRestClientFactory factory, String searchString) {
    Issue issue = null;

    JiraRestClient client = factory.create(serverUri, new AnonymousAuthenticationHandler());
    SearchResult sr = client.getSearchClient().searchJql(legacyKey).claim();

    if (sr.getTotal() == 1)
    {
      return getIssueByKey(factory, sr.getIssues().iterator().next().getKey());
    }
    else {
      return null;
    }
  }

}
