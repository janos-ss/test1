/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.externalspecifications.CleanupReport;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;

import com.sonarsource.ruleapi.get.Fetcher;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.ComparisonUtilities;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class Cert extends AbstractMultiLanguageStandard implements TaggableStandard, CleanupReport {

  private static final Logger LOGGER = Logger.getLogger(Cert.class.getName());

  private static final String TAG = "cert";

  private static final String REFERENCE_NAME = "CERT";

  private static final String REFERENCE_PATTERN = "[A-Z]{3}\\d\\d-[A-Za-z]+.";

  private static final Fetcher FETCHER = new Fetcher();

  private static final CertType CPP = new CertType(Language.CPP, "146440541");
  private static final CertType C = new CertType(Language.C, "158237133");
  private static final CertType JAVA = new CertType(Language.JAVA, "158237393");
  public static final String RESULTS = "results";

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
  public String generateCleanupReport() {
    List<Rule> rules = RuleMaker.getRulesByJql(getStandardName() + " is not empty", "");

    return getCleanupReportBody(rules);
  }

  protected String getCleanupReportBody(List<Rule> rules) {

    StringBuilder sb = new StringBuilder();

    for (Rule rule : rules) {
      List<String> seeSectionReferences = getSpecificReferences(rule);
      for (String ref : seeSectionReferences) {
        String title=ref.replace(getStandardName(), "").trim().replaceAll("^, ","").replace(" - ",". ")
                .replace(".. ",". ");

        checkReference(sb, rule.getKey(), ref, title);
      }
    }
    return sb.toString();
  }

  protected List<String> getSpecificReferences(Rule rule) {

    List<String> referencesFound = new ArrayList<>();

    String[] referenceLines = rule.getReferences().split("\n");
    for (String line : referenceLines) {
      line = ComparisonUtilities.stripHtml(line);
      if (line.toLowerCase().contains("see also")) {
        break;
      }
      if (line.contains(REFERENCE_NAME)) {
        referencesFound.add(line);
      }
    }
    return referencesFound;
  }

  protected void checkReference(StringBuilder sb, String ruleKey, String ref, String title) {

    String baseUrl = "https://www.securecoding.cert.org/confluence/rest/api/content?title=";
    String newline = "\n";

    try {
      String encodedTitle = URLEncoder.encode(title, "UTF-8");

      JSONObject json = FETCHER.getJsonFromUrl(baseUrl + encodedTitle);
      JSONArray results = (JSONArray) json.get(RESULTS);
      if (results.isEmpty()) {
        sb.append("Not found: ").append(ruleKey).append(" : ").append(ref).append(newline);
        sb.append(baseUrl).append(encodedTitle).append(newline);
      }
    } catch (UnsupportedEncodingException e) {
      sb.append("Bad Uri: ").append(ruleKey).append(" : ").append(ref).append(newline);
      sb.append(e.getMessage()).append(newline);
    }
  }


  @Override
  protected String generateReport(String instance) {

    if (currentCertLanguage == null) {
      return null;
    }

    CertRule[] certRules = (CertRule[]) currentCertLanguage.getCodingStandardRules();

    return getReportBody(instance, certRules);
  }

  protected String getReportBody(String instance, CertRule[] certRules) {

    if (currentCertLanguage == null || getRulesCoverage() == null) {
      return null;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<h2>").append(currentCertLanguage.language.getRspec()).append(" coverage of ")
            .append(getStandardName()).append("</h2>\n");

    StringBuilder covered = new StringBuilder();
    covered.append("<h3>Covered</h3><table>\n");

    StringBuilder uncovered = new StringBuilder();
    uncovered.append("<h3>Uncovered</h3><table>\n");

    Map<String, CodingStandardRuleCoverage> map = this.getRulesCoverage();

    for (CertRule certRule : certRules) {
      String certId = certRule.getId();

      CodingStandardRuleCoverage csrc = map.get(certId);

      List<Rule> implRules = csrc.getImplementedBy();

      String title = certRule.getTitle().replaceFirst(certId, "");

      if (!implRules.isEmpty()) {
        covered.append("<tr><td><a href='").append(certRule.getUrl())
                .append("' target='_blank'>").append(certRule.getId()).append("</a>")
                .append(title).append("</td>\n<td>");
        for (Rule rule : implRules) {
          covered.append(Utilities.getNemoLinkedRuleReference(instance, rule));
        }
        covered.append("</td></tr>\n");
      } else {
        uncovered.append("<tr><td><a href='")
                .append(certRule.getUrl())
                .append("' target='_blank'>").append(certRule.getId()).append("</a>")
                .append(title).append("</td><td>");
        for (Rule rule : csrc.getSpecifiedBy()) {
          uncovered.append(Utilities.getJiraLinkedRuleReference(rule));
        }
        uncovered.append("</td></tr>\n");
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

    initCoverageResults(instance);

    return generateReport(instance);
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

    if (currentCertLanguage == null) {
      return new CodingStandardRule[0];
    }

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
        String url = "%s/rest/api/content/%s/child/page?expand=title,metadata.labels&limit=200";
        List<String> ids = new ArrayList<>();
        ids.add(wikiPageId);

        while (!ids.isEmpty()){
          JSONObject jsonObject = FETCHER.getJsonFromUrl(String.format(url, baseUrl, ids.remove(0)));
          List<JSONObject> results = (JSONArray) jsonObject.get(RESULTS);
          extractRulesFromChildPages(baseUrl, ids, results);
        }
      }

      return rules.toArray(new CertRule[rules.size()]);
    }

    protected void extractRulesFromChildPages(String baseUrl, List<String> ids, List<JSONObject> results) {

      for (JSONObject obj : results) {
        String title = (String) obj.get("title");
        String ruleId = title.split(" ")[0];
        String pageId = (String) obj.get("id");

        List<String> labels = new ArrayList<>();
        List<JSONObject> jLabels =  (JSONArray)((JSONObject) ((JSONObject) obj.get("metadata")).get("labels")).get(RESULTS);
        if (jLabels != null) {
          for (JSONObject label : jLabels) {
            labels.add((String) label.get("name"));
          }
        }

        if (!labels.contains("deprecated")) {
          if (ruleId.matches(Cert.REFERENCE_PATTERN)) {
            String tiny = (String) ((JSONObject) obj.get("_links")).get("tinyui");
            rules.add(new CertRule(ruleId, title, baseUrl + tiny, labels));

          } else {
            ids.add(pageId);
          }
        }
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
    private List<String> labels;

    public CertRule(String id, String title, String url, List<String> labels) {
      this.id = id;
      this.title = title;
      this.url = url;
      this.labels = labels;
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

    public List<String> getLabels() {
      return this.labels;
    }

    @Override
    public Implementability getImplementability() {

      return Implementability.IMPLEMENTABLE;
    }
  }
}
