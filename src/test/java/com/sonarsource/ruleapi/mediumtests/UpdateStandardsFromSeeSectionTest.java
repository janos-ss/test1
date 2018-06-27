/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.mediumtests;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.specifications.SansTop25;
import com.sonarsource.ruleapi.get.JiraFetcher;
import com.sonarsource.ruleapi.get.JiraFetcherImpl;
import com.sonarsource.ruleapi.get.JiraHelper;
import com.sonarsource.ruleapi.services.IntegrityEnforcementService;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.Parameter.param;

public class UpdateStandardsFromSeeSectionTest {

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
    HttpRequest oneIssueRequest = HttpRequest
      .request()
      .withPath("/issue/RSPEC-2576");

    mockServerClient.when(
      oneIssueRequest
    ).respond(
      HttpResponse
        .response()
        .withBody(IOUtils.toString(getClass().getResourceAsStream("/rspec-json/RSPEC-2576.json"), StandardCharsets.UTF_8), StandardCharsets.UTF_8)
    );
    mockServerClient.when(
      HttpRequest
        .request()
        .withPath("/search")
    ).respond(
      HttpResponse
        .response()
        .withBody(IOUtils.toString(getClass().getResourceAsStream("/rspec-json/search.json"), StandardCharsets.UTF_8), StandardCharsets.UTF_8)
    );
    mockServerClient.when(
      HttpRequest
        .request()
        .withPath("/issue/RSPEC-2576/editmeta")
    ).respond(
      HttpResponse
        .response()
        .withBody(IOUtils.toString(getClass().getResourceAsStream("/rspec-json/RSPEC-2576-editmeta.json"), StandardCharsets.UTF_8), StandardCharsets.UTF_8)
    );

    JiraFetcher fetcher = JiraFetcherImpl.instance();
    JSONObject ruleJson = fetcher.fetchIssueByKey("RSPEC-2576");
    Rule rule = new Rule("");
    JiraHelper.populateFields(rule, ruleJson);
    assertThat(rule.getCwe()).hasSize(5);
    assertThat(rule.getSansTop25()).isEmpty();

    mockServerClient.verify(oneIssueRequest);

    IntegrityEnforcementService integrityService = new IntegrityEnforcementService("", "");
    integrityService.enforceTagReferenceIntegrity(SansTop25.Category.INSECURE_INTERACTION);

    // Initial search
    mockServerClient.verify(HttpRequest.request().withPath("/search").withQueryStringParameters(
      param("expand", "names"),
      param("jql", "project=RSPEC"),
      param("maxResults", "1000"),
      param("fields", "*all,-comment,-assignee,-project,-reporter,-creator,-votes,-watches,-parent"),
      param("startAt", "0")
    ));
    // Search for "SANS Top 25" rules
    mockServerClient.verify(HttpRequest.request().withPath("/search").withQueryStringParameters(
      param("expand", "names"),
      param("jql", "project=RSPEC " +
        "AND resolution = Unresolved " +
        "AND issuetype != Language-Specification " +
        "AND (('SANS Top 25' is not EMPTY OR description ~ 'SANS Top 25' OR labels = sans-top25-insecure))"),
      param("maxResults", "1000"),
      param("fields", "*all,-comment,-assignee,-project,-reporter,-creator,-votes,-watches,-parent"),
      param("startAt", "0")
    ));
    // Look for editmeta
    mockServerClient.verify(HttpRequest
      .request()
      .withPath("/issue/RSPEC-2576/editmeta"));
    // Update of "SANS Top 25" field
    mockServerClient.verify(HttpRequest
      .request()
      .withPath("/issue/RSPEC-2576")
      .withMethod("PUT")
      .withBody("{\"fields\":{\"customfield_10252\":\"Insecure Interaction Between Components\"}}")
    );
  }

  @Test
  public void shouldNotUpdateSansTop25FieldUsingSeeSectionIfAlreadyThere() throws Exception {
    HttpRequest oneIssueRequest = HttpRequest
      .request()
      .withPath("/issue/RSPEC-2576");

    mockServerClient.when(
      oneIssueRequest
    ).respond(
      HttpResponse
        .response()
        .withBody(IOUtils.toString(getClass().getResourceAsStream("/rspec-json/RSPEC-2576-with-SANS.json"), StandardCharsets.UTF_8), StandardCharsets.UTF_8)
    );
    mockServerClient.when(
      HttpRequest
        .request()
        .withPath("/search")
    ).respond(
      HttpResponse
        .response()
        .withBody(IOUtils.toString(getClass().getResourceAsStream("/rspec-json/search-with-SANS.json"), StandardCharsets.UTF_8), StandardCharsets.UTF_8)
    );

    JiraFetcher fetcher = JiraFetcherImpl.instance();
    JSONObject ruleJson = fetcher.fetchIssueByKey("RSPEC-2576");
    Rule rule = new Rule("");
    JiraHelper.populateFields(rule, ruleJson);
    assertThat(rule.getCwe()).hasSize(5);
    assertThat(rule.getSansTop25()).hasSize(1);

    mockServerClient.verify(oneIssueRequest);

    IntegrityEnforcementService integrityService = new IntegrityEnforcementService("", "");
    integrityService.enforceTagReferenceIntegrity(SansTop25.Category.INSECURE_INTERACTION);

    // Initial search
    mockServerClient.verify(HttpRequest.request().withPath("/search").withQueryStringParameters(
      param("expand", "names"),
      param("jql", "project=RSPEC"),
      param("maxResults", "1000"),
      param("fields", "*all,-comment,-assignee,-project,-reporter,-creator,-votes,-watches,-parent"),
      param("startAt", "0")
    ));
    // Search for "SANS Top 25" rules
    mockServerClient.verify(HttpRequest.request().withPath("/search").withQueryStringParameters(
      param("expand", "names"),
      param("jql", "project=RSPEC " +
        "AND resolution = Unresolved " +
        "AND issuetype != Language-Specification " +
        "AND (('SANS Top 25' is not EMPTY OR description ~ 'SANS Top 25' OR labels = sans-top25-insecure))"),
      param("maxResults", "1000"),
      param("fields", "*all,-comment,-assignee,-project,-reporter,-creator,-votes,-watches,-parent"),
      param("startAt", "0")
    ));
    // No lookout for editmeta
    mockServerClient.verify(HttpRequest
      .request()
      .withPath("/issue/RSPEC-2576/editmeta"),
      VerificationTimes.exactly(0));
    // No update of "SANS Top 25" field
    mockServerClient.verify(HttpRequest
      .request()
      .withPath("/issue/RSPEC-2576")
      .withMethod("PUT")
      .withBody("{\"fields\":{\"customfield_10252\":\"Insecure Interaction Between Components\"}}"),
      VerificationTimes.exactly(0)
    );
  }

}
