/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sonarsource.ruleapi.domain.RuleException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.CheckForNull;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import jersey.repackaged.com.google.common.base.Splitter;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Retrieves Issue from Jira by key
 */
public class Fetcher {

  public static final String BASE_URL = "https://jira.sonarsource.com/rest/api/latest/";
  public static final String ISSUE = "issue/";

  public static final String RSPEC = "RSPEC-";

  private static final String SEARCH = "search?fields=key&maxResults=1000&jql=";
  private static final String BASE_QUERY = "project=RSPEC AND resolution = Unresolved AND issuetype = Specification AND ";

  private static final String ENCODING = "UTF-8";

  private Map<String, JSONObject> rspecsByKey = null;

  public Fetcher(){
    System.setProperty("jsse.enableSNIExtension", "false");
  }

  /**
   * Retrieves Jira Issue by Jira id (RSPEC-###)
   * or implementation id (S###) or legacy key.
   *
   * If no match is found, <code>null</code> is returned.
   * Will throw a <code>RuleException</code> when several matches are found for a legacy key search.
   *
   * The first invocation of this method will prefetch all RSPECs from Jira, and therefore be slow.
   * However, all subsequent accesses will return prefetched data.
   *
   * @param key the key to search by.
   * @return Populated Issue retrieved from Jira or null
   */
  public JSONObject fetchIssueByKey(String key) {
    JSONObject issue;

    if (key.matches("S?[0-9]+")) {
      issue = getIssueByKey(RSPEC + key.replaceFirst("S", ""));
    } else if (key.matches(RSPEC+"[0-9]+")) {
      issue = getIssueByKey(key);
    } else {
      issue = getIssueByLegacyKey(key);
    }
    return issue;
  }

  private JSONObject getIssueByKey(String issueKey) {
    ensureRspecsByKeyPopulated();

    JSONObject json = rspecsByKey.get(issueKey);
    if (json == null) {
      throw new RuleException("RSPEC not found: " + issueKey);
    }
    return json;
  }

  private JSONObject getIssueByLegacyKey(String key) {
    ensureRspecsByKeyPopulated();

    List<String> rspecKeys = Lists.newArrayList();
    for (JSONObject rspec: rspecsByKey.values()) {
      // See https://github.com/SonarSource/sonar-rule-api/blob/1.8/src/main/java/com/sonarsource/ruleapi/get/Fetcher.java#L43
      if (isUnresolved(rspec) && isSpecification(rspec) && legacyKeyFieldMatches(rspec, key)) {
        rspecKeys.add(rspec.get("key").toString());
      }
    }

    if (rspecKeys.size() > 1) {
      throw new IllegalArgumentException("Legacy Key \"\" can matches several RSPECs: " + Joiner.on(", ").join(rspecKeys));
    }

    return rspecKeys.isEmpty() ? null : getIssueByKey(rspecKeys.get(0));
  }

  private static boolean isUnresolved(JSONObject issue) {
    JSONObject fields = (JSONObject)issue.get("fields");
    return fields.get("resolution") == null;
  }

  // TODO: Perhaps legacy keys should be dropped from subtasks?
  private static boolean isSpecification(JSONObject issue) {
    JSONObject fields = (JSONObject)issue.get("fields");
    JSONObject issuetype = (JSONObject)fields.get("issuetype");
    return "Specification".equals(issuetype.get("name"));
  }

  /**
   * Reimplementation of Jira's <code>~</code> operator
   */
  private static boolean legacyKeyFieldMatches(JSONObject issue, String key) {
    String legacyKeyCustomFieldName = getInternalCustomFieldName(issue, "Legacy Key");

    JSONObject fields = (JSONObject)issue.get("fields");
    String legacyKeyFieldValue = (String)fields.get(legacyKeyCustomFieldName);

    return legacyKeyFieldMatches(legacyKeyFieldValue, key);
  }

  private static boolean legacyKeyFieldMatches(String legacyKeyFieldValue, String key) {
    if (legacyKeyFieldValue == null || legacyKeyFieldValue.isEmpty()) {
      return false;
    }

    String pattern = "(?i).*\\b" + Pattern.quote(key) + "\\b.*";
    for (String candidate: Splitter.on(',').split(legacyKeyFieldValue)) {
      if (candidate.matches(pattern)) {
        return true;
      }
    }

    return false;
  }

  private static String getInternalCustomFieldName(JSONObject issue, String customFieldName) {
    JSONObject names = (JSONObject)issue.get("names");
    for (Object nameObject: names.entrySet()) {
      Map.Entry<String, Object> name = (Map.Entry<String, Object>)nameObject;
      if (customFieldName.equals(name.getValue())) {
        return name.getKey();
      }
    }
    throw new IllegalArgumentException("No custom field named \"" + customFieldName + "\" found in: " + issue);
  }

  /**
   * Fetches every single RSPEC from Jira in only a few REST calls
   */
  public void ensureRspecsByKeyPopulated() {
    if (rspecsByKey != null) {
      return;
    }

    ImmutableMap.Builder<String, JSONObject> builder = ImmutableMap.builder();

    int startAt = 0;
    JSONObject page;
    while ((page = fetchRspecPage(startAt)) != null) {
      if (!page.containsKey("names")) {
        throw new IllegalStateException("expected names to be expanded");
      }
      JSONObject names = (JSONObject)page.get("names");

      JSONArray issues = (JSONArray)page.get("issues");
      for (Object issueObject: issues) {
        JSONObject issue = (JSONObject)issueObject;
        String key = (String)issue.get("key");

        // Propagate "names" down into each issue
        issue.put("names", names);

        builder.put(key, issue);
      }

      startAt += issues.size();
    }

    rspecsByKey = builder.build();
  }

  /**
   * Fetches a RSPEC issues page. Returns <code>null</code> if no issue is found.
   */
  @CheckForNull
  private JSONObject fetchRspecPage(int startAt) {
    JSONObject page = getJsonFromUrl(BASE_URL + "search?jql=project%3DRSPEC&expand=names&startAt=" + startAt + "&maxResults=500");
    Object issuesObject = page.get("issues");
    if (issuesObject == null) {
      return null;
    }
    JSONArray issues = (JSONArray)issuesObject;
    if (issues.isEmpty()) {
      return null;
    }
    return page;
  }

  public List<JSONObject> fetchIssueKeysBySearch(String search) {

    try {

      String searchStr = BASE_QUERY + "(" + search + ")";
      searchStr = URLEncoder.encode(searchStr, ENCODING).replaceAll("\\+", "%20");

      JSONObject sr = getJsonFromUrl(BASE_URL + SEARCH + searchStr);
      return (JSONArray) sr.get("issues");

    } catch (UnsupportedEncodingException e) {
      throw new RuleException(e);
    }
  }

  /**
   * Retrieves raw JSON rules from a running SonarQube instance by query.
   *
   * @param instance base SonarQube instance address. E.G. http://nemo.sonarqube.org
   * @param search query to execute in query string format (with '&amp;' separating parameters)
   *               E. G. repositories=c
   * @return list of retrieved JSON rules
   */
  public List<JSONObject> fetchRulesFromSonarQube(String instance, String search) {

    String baseUrl = instance + "/api/rules/search?ps=500&" + search;

    JSONObject rawResult = getJsonFromUrl(baseUrl);
    JSONArray rules = (JSONArray)rawResult.get("rules");

    while ((Long) rawResult.get("total") > rules.size()) {
      long page = (Long) rawResult.get("p");
      page++;

      rawResult = getJsonFromUrl(baseUrl + "&p=" + page);
      rules.addAll((JSONArray) rawResult.get("rules"));

    }

    return rules;
  }

  public List<JSONObject> fetchProfilesFromSonarQube(String instance) {
    String url = instance + "/api/rules/app";

    JSONObject rawResult = getJsonFromUrl(url);
    return (JSONArray) rawResult.get("qualityprofiles");
  }


  public JSONObject fetchRuleFromSonarQube(String instance, String ruleKey) {

    String path = "/api/rules/show?key=";

    JSONObject rawResult = getJsonFromUrl(instance + path + ruleKey);
    return (JSONObject) rawResult.get("rule");
  }

  public JSONObject getJsonFromUrl(String url) {

    return getJsonFromUrl(url, null, null);
  }

  public JSONObject getJsonFromUrl(String url, String login, String password) {

    Client client = getClient(login, password);

    WebTarget webResource = client.target(url);

    Response response = webResource.request().accept("application/json").get(Response.class);

    checkStatus(url, client, response);

    String responseStr = response.readEntity(String.class);
    response.close();
    client.close();

    JSONParser parser = new JSONParser();
    try {
      return (JSONObject)parser.parse(responseStr);
    } catch (ParseException e) {
      throw new RuleException(e);
    }
  }

  public boolean isUrlGood(String urlString) {
    try {
      URL u = new URL(urlString);
      HttpURLConnection huc = (HttpURLConnection) u.openConnection();
      huc.setRequestMethod("GET");
      huc.setInstanceFollowRedirects(true);
      huc.connect();
      int code = huc.getResponseCode();

      return code >= 200 && code <=299;
    } catch (MalformedURLException e) {
      throw new RuleException(e);
    } catch (UnknownHostException e) {
      throw new RuleException(e);
    } catch (ProtocolException e) {
      throw new RuleException(e);
    } catch (IOException e) {
      throw new RuleException(e);
    }
  }

  protected void checkStatus(String url, Client client, Response response) {

    int status = response.getStatus();
    if (status < 200 || status > 299) {
      response.close();
      client.close();
      throw new RuleException("Failed : HTTP error code: "
              + response.getStatus() + " for " + url);
    }
  }

  protected Client getClient(String login, String password) {

    Client client = ClientBuilder.newClient();
    if (login != null && password != null) {
      client.register(HttpAuthenticationFeature.basic(login, password));
    }
    return client;
  }
}
