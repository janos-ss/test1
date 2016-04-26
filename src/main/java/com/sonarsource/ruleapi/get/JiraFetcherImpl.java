/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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
  private static final String BASE_QUERY = "project=RSPEC AND resolution = Unresolved AND issuetype = Specification AND ";

  private static final String ENCODING = "UTF-8";
  private static final String ISSUES = "issues";

  private Map<String, JSONObject> rspecJsonCacheByKey = null;

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
      return Fetcher.getJsonFromUrl(BASE_URL + ISSUE + issueKey + "?expand=names" + FIELDS);
    }
  }

  private JSONObject getIssueByLegacyKey(String key) {
    if (rspecJsonCacheByKey != null) {
      return getRuleByLegacyKeyFromCache(key);
    }

    String query = "\"Legacy Key\"~\"" + key + "\"";
    try {
      String searchStr = URLEncoder.encode(BASE_QUERY + query, ENCODING).replaceAll("\\+", "%20");

      JSONObject sr = Fetcher.getJsonFromUrl(BASE_URL + SEARCH + searchStr);
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
    return "Specification".equals(JiraHelper.getJsonFieldValue(issue, "issuetype"));
  }

  private static boolean legacyKeyFieldMatches(JSONObject issue, String key) {
    List<String> legacyKeys = JiraHelper.getCustomFieldValueAsList(issue, "Legacy Key");
    return legacyKeys.contains(key);
  }

  @Override
  public List<JSONObject> fetchIssuesBySearch(String search) {
    ensureRspecsByKeyCachePopulated();

    try {

      String searchStr = BASE_QUERY + "(" + search + ")";
      searchStr = URLEncoder.encode(searchStr, ENCODING).replaceAll("\\+", "%20");

      JSONObject sr = Fetcher.getJsonFromUrl(BASE_URL + SEARCH + searchStr);
      propagateNames(sr);
      return (List<JSONObject>) sr.get(ISSUES);

    } catch (UnsupportedEncodingException e) {
      throw new RuleException(e);
    }
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

    int startAt = 0;
    JSONObject page;
    while ((page = fetchRspecPage(startAt)) != null) {
      propagateNames(page);

      JSONArray issues = (JSONArray) page.get(ISSUES);
      for (Object issueObject : issues) {
        JSONObject issue = (JSONObject) issueObject;
        builder.put((String) issue.get("key"), issue);
      }

      startAt += issues.size();
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

  /**
   * Fetches a RSPEC issues page. Returns <code>null</code> if no issue is found.
   */
  @CheckForNull
  private static JSONObject fetchRspecPage(int startAt) {
    JSONObject page = Fetcher.getJsonFromUrl(BASE_URL
      + SEARCH
      + "project%3DRSPEC"
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
