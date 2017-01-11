/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class SQFetcherImpl implements SQFetcher {

  @Override
  public List<JSONObject> fetchRulesFromSonarQube(String instance, String search) {

    String baseUrl = instance + "/api/rules/search?ps=500&" + search;

    JSONObject rawResult = Fetcher.getJsonFromUrl(baseUrl);
    JSONArray rules = (JSONArray) rawResult.get("rules");

    while ((Long) rawResult.get("total") > rules.size()) {
      long page = (Long) rawResult.get("p");
      page++;

      rawResult = Fetcher.getJsonFromUrl(baseUrl + "&p=" + page);
      rules.addAll((JSONArray) rawResult.get("rules"));

    }

    return rules;
  }

  @Override
  public List<JSONObject> fetchProfilesFromSonarQube(String instance) {
    String url = instance + "/api/rules/app";

    JSONObject rawResult = Fetcher.getJsonFromUrl(url);
    return (JSONArray) rawResult.get("qualityprofiles");
  }
}
