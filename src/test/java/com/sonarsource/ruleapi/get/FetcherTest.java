/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.RuleException;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FetcherTest {

  @Test
  public void testGetClient() {

    assertThat(Fetcher.getClient(null, null)).isNotNull();
    assertThat(Fetcher.getClient("yellow", "red")).isNotNull();
    assertThat(Fetcher.getClient("yellow", null)).isNotNull();
    assertThat(Fetcher.getClient(null, "red")).isNotNull();
  }

  @Test(expected=RuleException.class)
  public void testCheckStatusValueLow(){

    Response response = new TestResponse();
    Client client = Fetcher.getClient(null, null);

    Fetcher.checkStatus("", client, response);
  }

  @Test(expected=RuleException.class)
  public void testCheckStatusValueHigh(){

    TestResponse response = new TestResponse();
    response.setStatus(500);
    Client client = Fetcher.getClient(null, null);

    Fetcher.checkStatus("", client, response);
  }

  @Test
  public void testCheckStatusJustRight(){

    TestResponse response = new TestResponse();
    response.setStatus(200);
    Client client = Fetcher.getClient(null, null);

    try {
      Fetcher.checkStatus("", client, response);
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
