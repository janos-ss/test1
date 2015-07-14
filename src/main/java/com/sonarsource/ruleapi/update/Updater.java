/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.update;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.simple.JSONObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class Updater {

  private static final String BASE_URL = "http://jira.sonarsource.com/rest/api/2/issue/";

  public boolean putIssueUpdate(String login, String password, String ruleKey, JSONObject request){

    Client client = ClientBuilder.newClient().register(HttpAuthenticationFeature.basic(login, password));

    WebTarget target = client.target(BASE_URL + ruleKey);

    Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(request.toJSONString(), MediaType.APPLICATION_JSON_TYPE));

    return readAndCloseResponse(client, response);
  }

  public boolean postIssueUpdate(String login, String password, String apiTarget, JSONObject request) {
    Client client = ClientBuilder.newClient().register(HttpAuthenticationFeature.basic(login, password));

    WebTarget target = client.target(BASE_URL + apiTarget);

    Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(request.toJSONString(), MediaType.APPLICATION_JSON_TYPE));

    return readAndCloseResponse(client, response);

  }

  protected boolean readAndCloseResponse(Client client, Response response) {

    response.close();
    client.close();

    if (response.getStatus() == 204) {
      return true;
    }

    return false;
  }
}
