/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.tools;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableExternalTool;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.List;


public class FindSecBugs extends AbstractReportableExternalTool {

  private String standardName = "FindSecBugs";
  private String rspecFieldName = "FindSecBugs";
  private Language language = Language.JAVA;


  public enum StandardRule implements CodingStandardRule {
    PREDICTABLE_RANDOM(Implementability.IMPLEMENTABLE),
    SERVLET_PARAMETER(Implementability.IMPLEMENTABLE),
    SERVLET_CONTENT_TYPE(Implementability.IMPLEMENTABLE),
    SERVLET_SERVER_NAME(Implementability.IMPLEMENTABLE),
    SERVLET_SESSION_ID(Implementability.IMPLEMENTABLE),
    SERVLET_QUERY_STRING(Implementability.IMPLEMENTABLE),
    SERVLET_HEADER(Implementability.IMPLEMENTABLE),
    SERVLET_HEADER_REFERER(Implementability.IMPLEMENTABLE),
    SERVLET_HEADER_USER_AGENT(Implementability.IMPLEMENTABLE),
    COOKIE_USAGE(Implementability.IMPLEMENTABLE),
    PATH_TRAVERSAL_IN(Implementability.IMPLEMENTABLE),
    PATH_TRAVERSAL_OUT(Implementability.IMPLEMENTABLE),
    COMMAND_INJECTION(Implementability.IMPLEMENTABLE),
    WEAK_FILENAMEUTILS(Implementability.REJECTED),
    WEAK_TRUST_MANAGER(Implementability.IMPLEMENTABLE),
    WEAK_HOSTNAME_VERIFIER(Implementability.IMPLEMENTABLE),
    JAXWS_ENDPOINT(Implementability.IMPLEMENTABLE),
    JAXRS_ENDPOINT(Implementability.IMPLEMENTABLE),
    TAPESTRY_ENDPOINT(Implementability.IMPLEMENTABLE),
    WICKET_ENDPOINT(Implementability.IMPLEMENTABLE),
    WEAK_MESSAGE_DIGEST_MD5(Implementability.IMPLEMENTABLE),
    WEAK_MESSAGE_DIGEST_SHA1(Implementability.IMPLEMENTABLE),
    DEFAULT_HTTP_CLIENT(Implementability.IMPLEMENTABLE),
    SSL_CONTEXT(Implementability.IMPLEMENTABLE),
    CUSTOM_MESSAGE_DIGEST(Implementability.IMPLEMENTABLE),
    FILE_UPLOAD_FILENAME(Implementability.IMPLEMENTABLE),
    REDOS(Implementability.IMPLEMENTABLE),
    XXE_XMLSTREAMREADER(Implementability.IMPLEMENTABLE),
    XXE_SAXPARSER(Implementability.IMPLEMENTABLE),
    XXE_XMLREADER(Implementability.IMPLEMENTABLE),
    XXE_DOCUMENT(Implementability.IMPLEMENTABLE),
    XXE_DTD_TRANSFORM_FACTORY(Implementability.IMPLEMENTABLE),
    XXE_XSLT_TRANSFORM_FACTORY(Implementability.IMPLEMENTABLE),
    XPATH_INJECTION(Implementability.IMPLEMENTABLE),
    STRUTS1_ENDPOINT(Implementability.IMPLEMENTABLE),
    STRUTS2_ENDPOINT(Implementability.IMPLEMENTABLE),
    SPRING_ENDPOINT(Implementability.IMPLEMENTABLE),
    SPRING_CSRF_PROTECTION_DISABLED(Implementability.IMPLEMENTABLE),
    SPRING_CSRF_UNRESTRICTED_REQUEST_MAPPING(Implementability.IMPLEMENTABLE),
    CUSTOM_INJECTION(Implementability.IMPLEMENTABLE),
    SQL_INJECTION(Implementability.IMPLEMENTABLE),
    SQL_INJECTION_TURBINE(Implementability.IMPLEMENTABLE),
    SQL_INJECTION_HIBERNATE(Implementability.IMPLEMENTABLE),
    SQL_INJECTION_JDO(Implementability.IMPLEMENTABLE),
    SQL_INJECTION_JPA(Implementability.IMPLEMENTABLE),
    SQL_INJECTION_SPRING_JDBC(Implementability.IMPLEMENTABLE),
    SQL_INJECTION_JDBC(Implementability.IMPLEMENTABLE),
    SQL_INJECTION_ANDROID(Implementability.IMPLEMENTABLE),
    LDAP_INJECTION(Implementability.IMPLEMENTABLE),
    SCRIPT_ENGINE_INJECTION(Implementability.IMPLEMENTABLE),
    SPEL_INJECTION(Implementability.IMPLEMENTABLE),
    EL_INJECTION(Implementability.IMPLEMENTABLE),
    SEAM_LOG_INJECTION(Implementability.IMPLEMENTABLE),
    OGNL_INJECTION(Implementability.IMPLEMENTABLE),
    HTTP_RESPONSE_SPLITTING(Implementability.IMPLEMENTABLE),
    CRLF_INJECTION_LOGS(Implementability.IMPLEMENTABLE),
    EXTERNAL_CONFIG_CONTROL(Implementability.IMPLEMENTABLE),
    BAD_HEXA_CONVERSION(Implementability.IMPLEMENTABLE),
    HAZELCAST_SYMMETRIC_ENCRYPTION(Implementability.IMPLEMENTABLE),
    NULL_CIPHER(Implementability.IMPLEMENTABLE),
    UNENCRYPTED_SOCKET(Implementability.IMPLEMENTABLE),
    UNENCRYPTED_SERVER_SOCKET(Implementability.IMPLEMENTABLE),
    DES_USAGE(Implementability.IMPLEMENTABLE),
    TDES_USAGE(Implementability.IMPLEMENTABLE),
    RSA_NO_PADDING(Implementability.IMPLEMENTABLE),
    HARD_CODE_PASSWORD(Implementability.IMPLEMENTABLE),
    HARD_CODE_KEY(Implementability.IMPLEMENTABLE),
    UNSAFE_HASH_EQUALS(Implementability.IMPLEMENTABLE),
    STRUTS_FORM_VALIDATION(Implementability.IMPLEMENTABLE),
    XSS_REQUEST_WRAPPER(Implementability.IMPLEMENTABLE),
    BLOWFISH_KEY_SIZE(Implementability.IMPLEMENTABLE),
    RSA_KEY_SIZE(Implementability.IMPLEMENTABLE),
    UNVALIDATED_REDIRECT(Implementability.IMPLEMENTABLE),
    PLAY_UNVALIDATED_REDIRECT(Implementability.IMPLEMENTABLE),
    SPRING_UNVALIDATED_REDIRECT(Implementability.IMPLEMENTABLE),
    JSP_INCLUDE(Implementability.IMPLEMENTABLE),
    JSP_SPRING_EVAL(Implementability.IMPLEMENTABLE),
    JSP_JSTL_OUT(Implementability.IMPLEMENTABLE),
    XSS_JSP_PRINT(Implementability.IMPLEMENTABLE),
    XSS_SERVLET(Implementability.IMPLEMENTABLE),
    XML_DECODER(Implementability.IMPLEMENTABLE),
    STATIC_IV(Implementability.IMPLEMENTABLE),
    ECB_MODE(Implementability.IMPLEMENTABLE),
    PADDING_ORACLE(Implementability.IMPLEMENTABLE),
    CIPHER_INTEGRITY(Implementability.IMPLEMENTABLE),
    ESAPI_ENCRYPTOR(Implementability.IMPLEMENTABLE),
    ANDROID_EXTERNAL_FILE_ACCESS(Implementability.IMPLEMENTABLE),
    ANDROID_BROADCAST(Implementability.IMPLEMENTABLE),
    ANDROID_WORLD_WRITABLE(Implementability.IMPLEMENTABLE),
    ANDROID_GEOLOCATION(Implementability.IMPLEMENTABLE),
    ANDROID_WEB_VIEW_JAVASCRIPT(Implementability.IMPLEMENTABLE),
    ANDROID_WEB_VIEW_JAVASCRIPT_INTERFACE(Implementability.IMPLEMENTABLE),
    INSECURE_COOKIE(Implementability.IMPLEMENTABLE),
    HTTPONLY_COOKIE(Implementability.IMPLEMENTABLE),
    OBJECT_DESERIALIZATION(Implementability.IMPLEMENTABLE),
    JACKSON_UNSAFE_DESERIALIZATION(Implementability.IMPLEMENTABLE),
    DESERIALIZATION_GADGET(Implementability.IMPLEMENTABLE),
    TRUST_BOUNDARY_VIOLATION(Implementability.IMPLEMENTABLE),
    JSP_XSLT(Implementability.IMPLEMENTABLE),
    MALICIOUS_XSLT(Implementability.IMPLEMENTABLE),
    URLCONNECTION_SSRF_FD(Implementability.IMPLEMENTABLE),
    TEMPLATE_INJECTION_VELOCITY(Implementability.IMPLEMENTABLE),
    TEMPLATE_INJECTION_FREEMARKER(Implementability.IMPLEMENTABLE),
    PERMISSIVE_CORS(Implementability.IMPLEMENTABLE),
    LDAP_ANONYMOUS(Implementability.IMPLEMENTABLE),
    LDAP_ENTRY_POISONING(Implementability.IMPLEMENTABLE),
    COOKIE_PERSISTENT(Implementability.IMPLEMENTABLE),
    URL_REWRITING(Implementability.IMPLEMENTABLE),
    INSECURE_SMTP_SSL(Implementability.IMPLEMENTABLE),
    AWS_QUERY_INJECTION(Implementability.IMPLEMENTABLE),
    BEAN_PROPERTY_INJECTION(Implementability.IMPLEMENTABLE),
    STRUTS_FILE_DISCLOSURE(Implementability.IMPLEMENTABLE),
    SPRING_FILE_DISCLOSURE(Implementability.IMPLEMENTABLE),
    REQUESTDISPATCHER_FILE_DISCLOSURE(Implementability.IMPLEMENTABLE),
    FORMAT_STRING_MANIPULATION(Implementability.IMPLEMENTABLE),
    HTTP_PARAMETER_POLLUTION(Implementability.IMPLEMENTABLE);

    private Implementability ability;

    StandardRule(Implementability ability) {

      this.ability = ability;
    }

    @Override
    public Implementability getImplementability() {

      return this.ability;
    }

    @Override
    public String getCodingStandardRuleId() {

      return this.name();
    }
  }

  @Override
  public String getStandardName() {
    return standardName;
  }

  @Override
  public Language getLanguage() {
    return language;
  }

  @Override
  public String getRSpecReferenceFieldName() {
    return rspecFieldName;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {
    return rule.getFindSecBugs();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setFindSecBugs(ids);
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return StandardRule.values();
  }
}
