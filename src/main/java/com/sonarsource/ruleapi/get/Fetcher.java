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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Retrieves Issue from Jira by key
 */
public class Fetcher {

  public static final String BASE_URL = "http://jira.sonarsource.com/rest/api/latest/";
  public static final String REDMOND = "https://sonarqubefordotnet.visualstudio.com/DefaultCollection/SonarQubeTfsIntegration/_apis/build/builds";
  public static final String ISSUE = "issue/";

  private static final String RSPEC = "RSPEC-";
  private static final String LEGACY_SEARCH1 = "\"Legacy Key\"~\"";
  private static final String LEGACY_SEARCH2 = "\"";

  private static final String SEARCH = "search?fields=key&maxResults=1000&jql=";
  private static final String BASE_QUERY = "project=RSPEC AND resolution = Unresolved AND issuetype = Specification AND ";
  private static final String EXPAND = "?expand=names";

  private static final String ENCODING = "UTF-8";

  private JSONObject names = null;


  public Fetcher(){
    System.setProperty("jsse.enableSNIExtension", "false");
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
    if (names == null) {
      JSONObject tmp = getJsonFromUrl(BASE_URL + ISSUE + issueKey + EXPAND);
      names = (JSONObject) tmp.get("names");
      return tmp;
    } else {
      JSONObject tmp = getJsonFromUrl(BASE_URL + ISSUE + issueKey);
      tmp.put("names", names);
      return tmp;
    }
  }

  private JSONObject getIssueByLegacyKey(String searchString) {
    try {
      String searchStr = URLEncoder.encode(BASE_QUERY + searchString, ENCODING).replaceAll("\\+", "%20");

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

  public void fetchRuleDataFromRedmond(String login, String password) {
    try {
      String query = URLEncoder.encode("definition=CI - C# Code Analysis", ENCODING);

      JSONObject buildList = getJsonFromUrl(REDMOND + "?" + query, login, password);
      long buildId = getBuildId(buildList);

      String buildUrl = REDMOND + "/" + buildId + "/artifacts/rule-descriptors?$format=zip";

      getFilesFromUrl(buildUrl, login, password);

    } catch (UnsupportedEncodingException e) {
      throw new RuleException(e);
    }
  }

  protected long getBuildId(JSONObject buildList) {

    ArrayList<JSONObject> builds = (JSONArray) buildList.get("value");

    long buildId = -1;
    for (JSONObject build : builds) {

      long buildDefId = (long) ((JSONObject) build.get("definition")).get("id");
      if (buildDefId == 31 && "completed".equals(build.get("status")) && "succeeded".equals(build.get("result"))) {
        buildId = (long) build.get("id");
        break;
      }
    }
    return buildId;
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

  protected void getFilesFromUrl(String url, String login, String password) {

    Client client = getClient(login, password);

    WebTarget webResource = client.target(url);

    Response response = webResource.request().accept("application/zip").get(Response.class);

    checkStatus(url, client, response);

    FileOutputStream output = null;
    try(InputStream is = response.readEntity(InputStream.class); ZipInputStream zin = new ZipInputStream(is)) {

      byte[] buffer = new byte[2048];
      ZipEntry entry;
      while((entry = zin.getNextEntry())!=null) {
        if (entry.isDirectory()) {
          continue;
        }

        String outpath = entry.getName();

        File file = new File(outpath);
        output = new FileOutputStream(file.getName());

        int len = 0;
        while ((len = zin.read(buffer)) > 0) {
          output.write(buffer, 0, len);
        }
        output.close();

      }
    } catch (IOException e) {
      throw new RuleException(e);

    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          // intentionally blank
        }
      }

      response.close();
      client.close();
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
