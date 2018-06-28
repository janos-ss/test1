/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.mediumtests;

import com.sonarsource.ruleapi.services.IntegrityEnforcementService;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.nio.charset.StandardCharsets;

import static org.mockserver.model.Parameter.param;

public class CleanUpDeprecatedRulesTest {

  @org.junit.Rule
  public MockServerRule mockServerRule = new MockServerRule(this);

  // This field is populated by mockServerRule
  private MockServerClient mockServerClient;

  private String originalBaseUrl;

  @Before
  public void setupBaseUrl() {
    String baseUrl = String.format("http://localhost:%d/", mockServerRule.getPort());
    originalBaseUrl = System.setProperty("ruleApi.baseUrl", baseUrl);
  }

  @After
  public void restoreBaseUrl() {
    if (originalBaseUrl == null) {
      System.clearProperty("ruleApi.baseUrl");
    } else {
      System.setProperty("ruleApi.baseUrl", originalBaseUrl);
    }
  }

  @Test
  public void shouldUpdateSansTop25FieldUsingSeeSection() throws Exception {
    mockServerClient.when(
      HttpRequest
        .request()
        .withPath("/search")
    ).respond(
      HttpResponse
        .response()
        .withBody(IOUtils.toString(getClass().getResourceAsStream("/rspec-json/search-superseded.json"), StandardCharsets.UTF_8), StandardCharsets.UTF_8)
    );
    mockServerClient.when(
      HttpRequest
        .request()
        .withPath("/issue/RSPEC-2583")
    ).respond(
      HttpResponse
        .response()
        .withBody(IOUtils.toString(getClass().getResourceAsStream("/rspec-json/RSPEC-2583.json"), StandardCharsets.UTF_8), StandardCharsets.UTF_8)
    );
    mockServerClient.when(
      HttpRequest
        .request()
        .withPath("/issue/RSPEC-1768/transitions")
    ).respond(
      HttpResponse
        .response()
        .withBody(IOUtils.toString(getClass().getResourceAsStream("/rspec-json/RSPEC-1768-transitions.json"), StandardCharsets.UTF_8), StandardCharsets.UTF_8)
    );
    mockServerClient.when(
      HttpRequest
        .request()
        .withPath("/issue/RSPEC-1768/editmeta")
    ).respond(
      HttpResponse
        .response()
        .withBody(IOUtils.toString(getClass().getResourceAsStream("/rspec-json/RSPEC-1768-editmeta.json"), StandardCharsets.UTF_8), StandardCharsets.UTF_8)
    );

    IntegrityEnforcementService integrityService = new IntegrityEnforcementService("", "");

    integrityService.cleanUpDeprecatedRules();

    // Initial search
    mockServerClient.verify(HttpRequest.request().withPath("/search").withQueryStringParameters(
      param("expand", "names"),
      param("jql", "project=RSPEC"),
      param("maxResults", "1000"),
      param("fields", "*all,-comment,-assignee,-project,-reporter,-creator,-votes,-watches,-parent"),
      param("startAt", "0")
    ));
    // Search for "DEPRECATED" rules
    mockServerClient.verify(HttpRequest.request().withPath("/search").withQueryStringParameters(
      param("expand", "names"),
      param("jql", "project=RSPEC " +
        "AND resolution = Unresolved " +
        "AND issuetype != Language-Specification " +
        "AND ( issueFunction in hasLinks(\"is deprecated by\") OR status = DEPRECATED)"),
      param("maxResults", "1000"),
      param("fields", "*all,-comment,-assignee,-project,-reporter,-creator,-votes,-watches,-parent"),
      param("startAt", "0")
    ));
    // RSPEC-1768 is deprecated by RSPEC-2583, fetched in the process
    mockServerClient.verify(HttpRequest.request().withPath("/issue/RSPEC-2583"));

    // RSPEC-1768 has a "is deprecated by" (sic) outbound link but has status != DEPRECATED, should be updated
    mockServerClient.verify(HttpRequest.request().withPath("/issue/RSPEC-1768/transitions"));
    mockServerClient.verify(HttpRequest.request()
      .withMethod("POST")
      .withPath("/issue/RSPEC-1768/transitions")
      .withBody("{\"transition\":{\"id\":\"13\"}}")
    );
    mockServerClient.verify(HttpRequest.request().withPath("/issue/RSPEC-1768/editmeta"));
    mockServerClient.verify(HttpRequest.request().withMethod("PUT").withPath("/issue/RSPEC-1768"));
  }
}
