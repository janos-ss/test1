/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import org.json.simple.JSONObject;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class FetcherIntegrationTest {

  private final JiraFetcherImpl fetcher = new JiraFetcherImpl();
  @Test
  public void testActualFetches() {

    // pre-cache fetch
    JSONObject byKeyFromJira = fetcher.fetchIssueByKey("S1234");
    JSONObject byLegacyKeyFromJira = fetcher.fetchIssueByKey("Union");
    byLegacyKeyFromJira.remove("names");

    assertThat(byKeyFromJira.get("key")).isEqualTo("RSPEC-1234");
    assertThat(byLegacyKeyFromJira.get("key")).isEqualTo("RSPEC-953"); // PC-Lint

    // populate cache
    fetcher.ensureRspecsByKeyCachePopulated();

    JSONObject byKeyFromCache = fetcher.fetchIssueByKey("S1234");
    JSONObject byLegacyKeyFromCache = fetcher.fetchIssueByKey("Union");
    byLegacyKeyFromCache.remove("names");

    // field value differs by retrieval method (/issue vs /search)
    byKeyFromJira.remove("expand");
    byLegacyKeyFromJira.remove("expand");
    byKeyFromCache.remove("expand");
    byLegacyKeyFromCache.remove("expand");

    assertThat(byKeyFromCache).isEqualTo(byKeyFromJira);
    assertThat(byLegacyKeyFromCache).isEqualTo(byLegacyKeyFromJira); // PC-Lint

    assertThat(fetcher.fetchIssueByKey("SelectWithoutOtherwise").get("key")).isEqualTo("RSPEC-131"); // Should ignore subtask RSPEC-2315

    assertThat(fetcher.fetchIssuesBySearch("summary is empty")).isEmpty();
    assertThat(fetcher.fetchIssuesBySearch("labels = clumsy")).isNotEmpty();
  }
}
