/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sonarsource.ruleapi.domain.RuleException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.annotation.CheckForNull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class JiraFetcherImpl implements JiraFetcher {

  public static final String BASE_URL = "https://jira.sonarsource.com/rest/api/latest/";
  public static final String ISSUE = "issue/";

  public static final String RSPEC = "RSPEC-";

  private static final String FIELDS = "&fields=*all%2c-comment%2c-assignee%2c-project%2c-reporter%2c-creator%2c-votes%2c-watches%2c-parent";
  private static final String SEARCH = "search?expand=names&maxResults=1000&jql=";
  private static final String BASE_QUERY = "project=RSPEC AND resolution = Unresolved AND issuetype != Language-Specification AND ";

  private static final String ENCODING = "UTF-8";
  private static final String ISSUES = "issues";

  private String baseUrl;

  private Map<String, JSONObject> rspecJsonCacheByKey = null;

  private JiraFetcherImpl() {
    this(getBaseUrl());
  }

  public static JiraFetcherImpl instance() {
    return new JiraFetcherImpl();
  }

  public static String getBaseUrl() {
    return System.getProperty("ruleApi.baseUrl", BASE_URL);
  }

  private JiraFetcherImpl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override
  public JSONObject fetchIssueByKey(String key) {
    JSONObject issue;

    if (key.matches("S?[0-9]+")) {
      issue = getIssueByKey(RSPEC + key.replaceFirst("S", ""));
    } else if (key.matches(RSPEC + "[0-9]+")) {
      issue = getIssueByKey(key);
    } else {
      issue = getIssueByLegacyKey(key);
    }
    return issue;
  }

  private JSONObject getIssueByKey(String issueKey) {
    if (rspecJsonCacheByKey != null) {
      return rspecJsonCacheByKey.get(issueKey);
    } else {
      return Fetcher.getJsonFromUrl(baseUrl + ISSUE + issueKey + "?expand=names" + FIELDS);
    }
  }

  private JSONObject getIssueByLegacyKey(String key) {
    if (rspecJsonCacheByKey != null) {
      return getRuleByLegacyKeyFromCache(key);
    }

    String query = "\"Legacy Key\"~\"" + key + "\"";
    try {
      String searchStr = URLEncoder.encode(BASE_QUERY + query, ENCODING).replaceAll("\\+", "%20");

      JSONObject sr = Fetcher.getJsonFromUrl(baseUrl + SEARCH + searchStr);
      JSONArray issues = (JSONArray) sr.get(ISSUES);

      if (issues.size() == 1) {
        return getIssueByKey(((JSONObject) issues.get(0)).get("key").toString());
      }
      return null;
    } catch (UnsupportedEncodingException e) {
      throw new RuleException(e);
    }
  }

  private JSONObject getRuleByLegacyKeyFromCache(String key) {
    List<String> rspecKeys = Lists.newArrayList();
    for (JSONObject rspec : rspecJsonCacheByKey.values()) {
      if (isSpecification(rspec) && legacyKeyFieldMatches(rspec, key)) {
        rspecKeys.add(rspec.get("key").toString());
      }
    }

    if (rspecKeys.size() > 1) {
      throw new IllegalArgumentException("Legacy Key \"\" can matches several RSPECs: " + Joiner.on(", ").join(rspecKeys));
    }

    return rspecKeys.isEmpty() ? null : getIssueByKey(rspecKeys.get(0));
  }

  private static boolean isSpecification(JSONObject issue) {
    return !("Language-Specification".equals(JiraHelper.getJsonFieldValue(issue, "issuetype")));
  }

  private static boolean legacyKeyFieldMatches(JSONObject issue, String key) {
    List<String> legacyKeys = JiraHelper.getCustomFieldValueAsList(issue, "Legacy Key");
    return legacyKeys.contains(key);
  }

  @Override
  public List<JSONObject> fetchIssuesBySearch(String search) {
    ensureRspecsByKeyCachePopulated();

    return fetchPaginatedRspecs(BASE_QUERY + "(" + search + ")");
  }


  /**
   * Fetches every single RSPEC from Jira in only a few REST calls
   */
  @VisibleForTesting
  protected void ensureRspecsByKeyCachePopulated() {
    if (rspecJsonCacheByKey != null) {
      return;
    }

    ImmutableMap.Builder<String, JSONObject> builder = ImmutableMap.builder();

    JSONArray issues = fetchPaginatedRspecs("project=RSPEC");
    for (Object issueObject : issues) {
      JSONObject issue = (JSONObject) issueObject;
      builder.put((String) issue.get("key"), issue);
    }

    rspecJsonCacheByKey = builder.build();
  }

  private static void propagateNames(JSONObject page) {

    JSONArray issues = (JSONArray) page.get(ISSUES);
    if (issues.isEmpty()) {
      return;
    }

    JSONObject names = (JSONObject) page.get("names");

    for (Object issueObject : issues) {
      JSONObject issue = (JSONObject) issueObject;
      issue.put("names", names);
    }

  }


  private JSONArray fetchPaginatedRspecs(String search) {

    try {
      String searchStr = URLEncoder.encode(search, ENCODING).replaceAll("\\+", "%20");

      JSONArray results = new JSONArray();
      long retrieved = 0;
      long expected = 1;
      JSONObject sr;

      while (retrieved < expected && (sr = fetchRspecPage((int)retrieved, searchStr)) != null) {
        propagateNames(sr);

        results.addAll((JSONArray) sr.get(ISSUES));

        expected = (long) sr.get("total");
        retrieved = results.size();
      }
      return results;

    } catch (UnsupportedEncodingException e) {
      throw new RuleException(e);
    }
  }

  /**
   * Fetches a RSPEC issues page. Returns <code>null</code> if no issue is found.
   *
   * @param startAt the issue number in the result set at which to restart retrieval
   * @param urlEncodedSearch the url-encoded search string to use
   */
  @CheckForNull
  private JSONObject fetchRspecPage(int startAt, String urlEncodedSearch) {
    
    JSONObject page = Fetcher.getJsonFromUrl(baseUrl
      + SEARCH
      + urlEncodedSearch
      + FIELDS
      + "&startAt="
      + startAt);
    Object issuesObject = page.get(ISSUES);
    if (issuesObject == null) {
      return null;
    }
    JSONArray issues = (JSONArray) issuesObject;
    if (issues.isEmpty()) {
      return null;
    }
    return page;
  }
}
