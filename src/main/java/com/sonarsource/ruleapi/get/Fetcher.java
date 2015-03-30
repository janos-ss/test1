/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.RuleException;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
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
import java.util.*;
import java.util.logging.Logger;

/**
 * Retrieves Issue from Jira by key
 */
public class Fetcher {

  private static final Logger LOGGER = Logger.getLogger(Fetcher.class.getName());

  public static final String BASE_URL = "http://jira.sonarsource.com/rest/api/latest/";
  public static final String ISSUE = "issue/";

  private static final String RSPEC = "RSPEC-";
  private static final String LEGACY_SEARCH1 = "\"Legacy Key\"~\"";
  private static final String LEGACY_SEARCH2 = "\"";

  private static final String SEARCH = "search?expand=names&maxResults=500&jql=";
  private static final String BASE_QUERY = "project=RSPEC AND resolution = Unresolved AND issuetype = Specification AND ";
  private static final String EXPAND = "?expand=names";

  private static final Map<String, JSONObject> RESPONSE_CACHE = new HashMap<String, JSONObject>();



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
  public JSONObject fetchIssueByKey(String key) {

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

  private JSONObject getIssueByKey(String issueKey) {
    return getJsonFromUrl(BASE_URL + ISSUE + issueKey + EXPAND);
  }

  private JSONObject getIssueByLegacyKey(String searchString) {
    try {
      String searchStr = URLEncoder.encode(BASE_QUERY + searchString, "UTF-8").replaceAll("\\+", "%20");

      JSONObject sr = getJsonFromUrl(BASE_URL + SEARCH + searchStr);
      JSONArray issues = (JSONArray) sr.get("issues");

      if (issues.size() == 1) {
        return getIssueByKey(((JSONObject) issues.get(0)).get("key").toString());
      }
      return null;
    } catch (UnsupportedEncodingException e) {
      throw new RuleException(e);
    }
  }

  public List<JSONObject> fetchIssuesBySearch(String search) {

    try {

      List<JSONObject> issues = new ArrayList<JSONObject>();

      String searchStr = BASE_QUERY + "(" + search + ")";
      searchStr = URLEncoder.encode(searchStr, "UTF-8").replaceAll("\\+", "%20");

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


  public JSONObject fetchRuleFromSonarQube(String instance, String ruleKey) {

    String path = "/api/rules/show?key=";

    JSONObject rawResult = getJsonFromUrl(instance + path + ruleKey);
    return (JSONObject) rawResult.get("rule");
  }

  public JSONObject getJsonFromUrl(String url) {

    return getJsonFromUrl(url, null, null);
  }

  public JSONObject getJsonFromUrl(String url, String login, String password) {

    JSONObject cacheObj = RESPONSE_CACHE.get(url);

    Client client = ClientBuilder.newClient();
    if (login != null && password != null) {
      client.register(HttpAuthenticationFeature.basic(login, password));
    } else if (cacheObj != null) {
      LOGGER.fine("Returning cached obj for:" + url);
      return cacheObj;
    }

    WebTarget webResource = client.target(url);

    Response response = webResource.request().accept("application/json").get(Response.class);

    int status = response.getStatus();
    if (status < 200 || status > 299) {
      throw new RuleException("Failed : HTTP error code: "
              + response.getStatus() + " for " + url);
    }

    String responseStr = response.readEntity(String.class);
    response.close();
    client.close();

    JSONParser parser = new JSONParser();
    try {
      cacheObj = (JSONObject)parser.parse(responseStr);
      RESPONSE_CACHE.put(url, cacheObj);
      return cacheObj;
    } catch (ParseException e) {
      throw new RuleException(e);
    }
  }
}
