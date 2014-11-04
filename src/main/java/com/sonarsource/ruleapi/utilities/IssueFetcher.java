/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Retrieves Issue from Jira by key
 */
public class IssueFetcher {

  private static final String RSPEC = "RSPEC-";
  private static final String SEARCH = "project=RSPEC AND ";
  private static final String LEGACY_SEARCH1 = "\"Legacy Key\"~\"";
  private static final String LEGACY_SEARCH2 = "\"";
  private static final URI SERVER_URI = URI.create("http://jira.sonarsource.com/");

  private AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

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
  public Issue fetchIssueByKey(String key) {

    Issue issue = null;

    if (key.matches("S?[0-9]+")) {
      issue = getIssueByKey(RSPEC + key.replaceFirst("S", ""));
    } else if (key.matches(RSPEC+"[0-9]+")) {
      issue = getIssueByKey(key);
    } else {
      String searchStr = SEARCH + LEGACY_SEARCH1 + key + LEGACY_SEARCH2;
      try {
        searchStr = URLEncoder.encode(searchStr, "UTF-8").replaceAll("\\+","%20");
        issue = getIssueByLegacyKey(searchStr);
      } catch (UnsupportedEncodingException e) {
        return null;
      }
    }

    return issue;
  }

  private Issue getIssueByKey(String issueKey) {
    JiraRestClient client = factory.create(SERVER_URI, new AnonymousAuthenticationHandler());
    return client.getIssueClient().getIssue(issueKey).claim();
  }

  private Issue getIssueByLegacyKey(String searchString) {
    JiraRestClient client = factory.create(SERVER_URI, new AnonymousAuthenticationHandler());
    SearchResult sr = client.getSearchClient().searchJql(searchString).claim();

    if (sr.getTotal() == 1) {
      return getIssueByKey(sr.getIssues().iterator().next().getKey());
    } else {
      return null;
    }
  }

  public List<Issue> fetchIssuesBySearch(String search) {
    String searchStr = SEARCH + search;
    try {
      searchStr = URLEncoder.encode(searchStr, "UTF-8").replaceAll("\\+","%20");
      JiraRestClient client = factory.create(SERVER_URI, new AnonymousAuthenticationHandler());
      SearchResult sr = client.getSearchClient().searchJql(searchStr).claim();

      Iterable<BasicIssue> basicIssues = sr.getIssues();

      List<Issue> issues = new ArrayList<Issue>();

      Iterator<BasicIssue> itr = basicIssues.iterator();
      while (itr.hasNext()) {
        BasicIssue bi = itr.next();

        Issue issue = getIssueByKey(bi.getKey());
        if (issue != null) {
          issues.add(issue);
        }
      }
      return issues;

    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

}
