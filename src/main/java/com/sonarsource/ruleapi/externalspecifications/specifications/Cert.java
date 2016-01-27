/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;

import com.sonarsource.ruleapi.get.Fetcher;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class Cert extends AbstractMultiLanguageStandard implements TaggableStandard{

  private static final Logger LOGGER = Logger.getLogger(Cert.class.getName());

  private static final String TAG = "cert";

  private static final String REFERENCE_NAME = "CERT";

  private static final String REFERENCE_PATTERN = "[A-Z]{3}\\d\\d-[A-Za-z]+.";

  private static final Fetcher FETCHER = new Fetcher();

  private static final CertType CPP = new CertType(Language.CPP, "146440541");
  private static final CertType C = new CertType(Language.C, "158237133");
  private static final CertType JAVA = new CertType(Language.JAVA, "158237393");

  private CertType currentCertLanguage = null;

  @Override
  public boolean isTagShared() {

    return false;
  }

  @Override
  public String getTag() {

    return TAG;
  }

  @Override
  public String getSeeSectionSearchString() {

    return REFERENCE_NAME;
  }

  @Override
  public String getReferencePattern() {

    return REFERENCE_PATTERN;
  }

  @Override
  public String getStandardName() {

    return REFERENCE_NAME;
  }

  @Override
  public String getRSpecReferenceFieldName() {

    return REFERENCE_NAME;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {

    return rule.getCert();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {

    rule.setCert(ids);
  }

  @Override
  public boolean doesReferenceNeedUpdating(String reference, List<String> replacements, String ruleKey) {
    if (!reference.matches(REFERENCE_PATTERN)) {
      LOGGER.info("Unrecognized CERT reference pattern " + reference + " in " + ruleKey);
    }

    replacements.add(reference);

    return false;
  }


  @Override
  protected String generateReport(String instance, Map<String, List<Rule>> standardRules) {

    if (currentCertLanguage == null || standardRules.isEmpty()) {
      return null;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<h2>").append(currentCertLanguage.language.getRspec()).append(" coverage of ")
            .append(getStandardName()).append("</h2>\n");

    CertRule[] certRules = (CertRule[]) currentCertLanguage.getCodingStandardRules();

    StringBuilder covered = new StringBuilder();
    covered.append("<h3>Covered</h3><table>\n");

    StringBuilder uncovered = new StringBuilder();
    uncovered.append("<h3>Uncovered</h3><table>\n");


    for (CertRule certRule : certRules) {
      String certId = certRule.getId();
      List<Rule> implRules = standardRules.get(certId);
      if (implRules == null) {
        implRules = standardRules.get(certId.substring(0, certId.length() - 1));
      }

      if (implRules != null) {
        covered.append("<tr><td><a href='").append(certRule.getUrl())
                .append("' target='_blank'>").append(certRule.getId()).append("</a>")
                .append(certRule.getTitle()).append("</td>\n<td>");
        for (Rule rule : implRules) {
          covered.append(Utilities.getNemoLinkedRuleReference(instance, rule));
        }
        covered.append("</td></tr>\n");
      } else {
        uncovered.append("<tr><td><a href='")
                .append(certRule.getUrl())
                .append("' target='_blank'>").append(certRule.getId()).append("</a>")
                .append(certRule.getTitle()).append("</td></tr>\n");
      }
    }

    covered.append("</table>");
    uncovered.append("</table>");

    sb.append(covered).append(uncovered);

    return sb.toString();
  }


  @Override
  public void setLanguage(Language language) {

    switch (language) {
      case C:
        currentCertLanguage = C;
        break;
      case CPP:
        currentCertLanguage = CPP;
        break;
      case JAVA:
        currentCertLanguage = JAVA;
        break;
      default:
        currentCertLanguage = null;
    }
  }

  @Override
  public String getReport(String instance) {

    if (getLanguage() == null) {
      return null;
    }

    Map<String, List<Rule>> certRules = initCoverage(instance);
    return generateReport(instance, certRules);
  }

  @Override
  public String getSummaryReport(String instance) {

    return getReport(instance);
  }

  @Override
  public Language getLanguage() {

    if (currentCertLanguage == null) {
      return null;
    }
    return currentCertLanguage.getLanguage();
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return currentCertLanguage.getCodingStandardRules();
  }


  public static class CertType {

    private Language language;
    private String wikiPageId;
    private final List<CertRule> rules = new ArrayList<>();

    CertType(Language language, String wikiPageId) {
      this.language = language;
      this.wikiPageId = wikiPageId;
    }

    public Language getLanguage() {
      return this.language;
    }

    public CodingStandardRule[] getCodingStandardRules(){
      if (rules.isEmpty()) {

        String baseUrl = "https://www.securecoding.cert.org/confluence";
        String url = "%s/rest/api/content/%s/child/page?expand=title&limit=200";

        List<String> ids = new ArrayList<>();
        ids.add(wikiPageId);

        while (!ids.isEmpty()){
          JSONObject jsonObject = FETCHER.getJsonFromUrl(String.format(url, baseUrl, ids.remove(0)));
          List<JSONObject> results = (JSONArray) jsonObject.get("results");

          for (JSONObject obj : results) {
            extractRulesFromChildPages(baseUrl, ids, obj);
          }
        }
      }

      return rules.toArray(new CertRule[rules.size()]);
    }

    protected void extractRulesFromChildPages(String baseUrl, List<String> ids, JSONObject obj) {

      String title = (String) obj.get("title");
      String ruleId = title.split(" ")[0];
      String pageId = (String) obj.get("id");

      if (ruleId.matches(Cert.REFERENCE_PATTERN)) {
        String tiny = (String) ((JSONObject) obj.get("_links")).get("tinyui");
        rules.add(new CertRule(ruleId, title, baseUrl + tiny));

      } else {
        ids.add(pageId);
      }
    }

    public String getStandardName() {
      return Cert.REFERENCE_NAME + " " + language.getSq();
    }
  }

  public static class CertRule implements CodingStandardRule {

    private String id;
    private String title;
    private String url;

    public CertRule(String id, String title, String url) {
      this.id = id;
      this.title = title;
      this.url = url;
    }

    @Override
    public String getCodingStandardRuleId() {

      return id;
    }

    public String getId() {

      return id;
    }

    public String getTitle() {

      return title;
    }

    public String getUrl() {

      return url;
    }

    @Override
    public Implementability getImplementability() {

      return Implementability.IMPLEMENTABLE;
    }
  }
}
