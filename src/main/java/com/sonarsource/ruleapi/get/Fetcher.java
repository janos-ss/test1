/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.RuleException;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * Retrieve data from a URL.
 */
public class Fetcher {

  private Fetcher() {
    // Utility class to get json from a url
  }


  public static JSONObject getJsonFromUrl(String url) {

    return getJsonFromUrl(url, null, null);
  }

  public static JSONObject getJsonFromUrl(String url, String login, String password) {

    Client client = getClient(login, password);

    WebTarget webResource = client.target(url);

    Response response = webResource.request().accept(MediaType.APPLICATION_JSON).get(Response.class);

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

  protected static void checkStatus(String url, Client client, Response response) {

    int status = response.getStatus();
    if (status < 200 || status > 299) {
      response.close();
      client.close();
      throw new RuleException("Failed : HTTP error code: "
              + response.getStatus() + " for " + url);
    }
  }

  protected static Client getClient(String login, String password) {
    try {
      System.setProperty("jsse.enableSNIExtension", "false");
      SSLContext sslcontext = SSLContext.getInstance( "TLS" );
      sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
          // This is useless for Nemo usage
        }
        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
          // No verification for the time being there, however we should check the server certificate
        }
        @Override
        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }

      }}, new java.security.SecureRandom());

      Client client = ClientBuilder.newBuilder().sslContext(sslcontext)
              .hostnameVerifier((s1, s2) -> s1.equalsIgnoreCase(s2.getPeerHost()))
              .build();

      if (login != null && password != null) {
        client.register(HttpAuthenticationFeature.basic(login, password));
      }

      return client;

    } catch (KeyManagementException| NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
