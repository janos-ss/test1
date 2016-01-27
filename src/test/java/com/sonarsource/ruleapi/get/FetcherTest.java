/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.RuleException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class FetcherTest {

  private Fetcher fetcher = new Fetcher();


  @Test(expected = RuleException.class)
  public void testIsUrlGood() {
    fetcher.isUrlGood("http://localhost:1");
  }

  // This is an integration test that relies on real data from RSPEC JIRA's project
  @Test
  public void testActualFetches() {

    // pre-cache fetch
    JSONObject byKeyFromJira = fetcher.fetchIssueByKey("S1234");
    JSONObject byLegacyKeyFromJira = fetcher.fetchIssueByKey("Union");

    assertThat(byKeyFromJira.get("key")).isEqualTo("RSPEC-1234");
    assertThat(byLegacyKeyFromJira.get("key")).isEqualTo("RSPEC-953"); // PC-Lint

    // populate cache
    fetcher.ensureRspecsByKeyCachePopulated();

    JSONObject byKeyFromCache = fetcher.fetchIssueByKey("S1234");
    JSONObject byLegacyKeyFromCache = fetcher.fetchIssueByKey("Union");

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

  @Test
  public void testGetClient() {

    assertThat(fetcher.getClient(null, null)).isNotNull();
    assertThat(fetcher.getClient("yellow", "red")).isNotNull();
    assertThat(fetcher.getClient("yellow", null)).isNotNull();
    assertThat(fetcher.getClient(null, "red")).isNotNull();
  }

  @Test(expected=RuleException.class)
  public void testCheckStatusValueLow(){

    Response response = new TestResponse();
    Client client = fetcher.getClient(null, null);

    fetcher.checkStatus("", client, response);
  }

  @Test(expected=RuleException.class)
  public void testCheckStatusValueHigh(){

    TestResponse response = new TestResponse();
    response.setStatus(500);
    Client client = fetcher.getClient(null, null);

    fetcher.checkStatus("", client, response);
  }

  @Test
  public void testCheckStatusJustRight(){

    TestResponse response = new TestResponse();
    response.setStatus(200);
    Client client = fetcher.getClient(null, null);

    try {
      fetcher.checkStatus("", client, response);
    } catch (Exception e) {
      Assert.fail();
    }

  }

  private class TestResponse extends Response {

    private int status = 0;

    public void setStatus(int status) {
      this.status = status;
    }

    @Override
    public int getStatus() {

      return status;
    }

    @Override
    public StatusType getStatusInfo() {

      return null;
    }

    @Override
    public Object getEntity() {

      return null;
    }

    @Override
    public <T> T readEntity(Class<T> entityType) {

      return null;
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType) {

      return null;
    }

    @Override
    public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {

      return null;
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {

      return null;
    }

    @Override
    public boolean hasEntity() {

      return false;
    }

    @Override
    public boolean bufferEntity() {

      return false;
    }

    @Override
    public void close() {

    }

    @Override
    public MediaType getMediaType() {

      return null;
    }

    @Override
    public Locale getLanguage() {

      return null;
    }

    @Override
    public int getLength() {

      return 0;
    }

    @Override
    public Set<String> getAllowedMethods() {

      return null;
    }

    @Override
    public Map<String, NewCookie> getCookies() {

      return null;
    }

    @Override
    public EntityTag getEntityTag() {

      return null;
    }

    @Override
    public Date getDate() {

      return null;
    }

    @Override
    public Date getLastModified() {

      return null;
    }

    @Override
    public URI getLocation() {

      return null;
    }

    @Override
    public Set<Link> getLinks() {

      return null;
    }

    @Override
    public boolean hasLink(String relation) {

      return false;
    }

    @Override
    public Link getLink(String relation) {

      return null;
    }

    @Override
    public Link.Builder getLinkBuilder(String relation) {

      return null;
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {

      return null;
    }

    @Override
    public MultivaluedMap<String, String> getStringHeaders() {

      return null;
    }

    @Override
    public String getHeaderString(String name) {

      return null;
    }
  }
}
