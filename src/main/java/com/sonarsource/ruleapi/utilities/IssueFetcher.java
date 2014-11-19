/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Retrieves Issue from Jira by key
 */
public class IssueFetcher {

  private static final String RSPEC = "RSPEC-";
  private static final String LEGACY_SEARCH1 = "\"Legacy Key\"~\"";
  private static final String LEGACY_SEARCH2 = "\"";

  private static final String BASE_URL = "http://jira.sonarsource.com/rest/api/latest/";
  private static final String SEARCH = "search?expand=names&maxResults=500&jql=";
  private static final String BASE_QUERY = "project=RSPEC AND resolution = Unresolved AND issuetype = Specification AND ";
  private static final String ISSUE = "issue/";
  private static final String EXPAND = "?expand=names";

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
  public JSONObject fetchIssueByKey(String key) throws ParseException, UnsupportedEncodingException {

    JSONObject issue = null;

    if (key.matches("S?[0-9]+")) {
      issue = getIssueByKey(RSPEC + key.replaceFirst("S", ""));
    } else if (key.matches(RSPEC+"[0-9]+")) {
      issue = getIssueByKey(key);
    } else {
      String searchStr = LEGACY_SEARCH1 + key + LEGACY_SEARCH2;
      issue = getIssueByLegacyKey(searchStr);
    }
    return issue;
  }

  private JSONObject getIssueByKey(String issueKey) throws ParseException {
      return getJsonFromUrl(BASE_URL + ISSUE + issueKey + EXPAND);
  }

  private JSONObject getIssueByLegacyKey(String searchString) throws ParseException, UnsupportedEncodingException {
    String searchStr = URLEncoder.encode(BASE_QUERY + searchString, "UTF-8").replaceAll("\\+","%20");

    JSONObject sr = getJsonFromUrl(BASE_URL + SEARCH + searchStr);
    JSONArray issues = (JSONArray) sr.get("issues");

    if (issues.size() == 1) {
      return getIssueByKey(((JSONObject) issues.get(0)).get("key").toString());
    }
    return null;
  }

  public List<JSONObject> fetchIssuesBySearch(String search) throws UnsupportedEncodingException, ParseException {

    List<JSONObject> issues = new ArrayList<JSONObject>();

    String searchStr = BASE_QUERY + search;
    searchStr = URLEncoder.encode(searchStr, "UTF-8").replaceAll("\\+","%20");

    JSONObject sr = getJsonFromUrl(BASE_URL + SEARCH + searchStr);
    JSONArray jIssues = (JSONArray) sr.get("issues");

    Iterator<JSONObject> itr = jIssues.iterator();
    while (itr.hasNext()) {
      JSONObject jobj = itr.next();

      JSONObject issue = getIssueByKey(jobj.get("key").toString());
      if (issue != null) {
        issues.add(issue);
      }
    }

    return issues;
  }

  public JSONObject getJsonFromUrl(String url) throws ParseException {
    Client client = ClientBuilder.newClient();

    WebTarget webResource = client.target(url);

    Response response = webResource.request().accept("application/json").get(Response.class);

    int status = response.getStatus();
    if (status < 200 || status > 299) {
      throw new RuntimeException("Failed : HTTP error code : "
              + response.getStatus());
    }

    String responseStr = response.readEntity(String.class);
    response.close();

    JSONParser parser = new JSONParser();
    return (JSONObject)parser.parse(responseStr);
  }
}
