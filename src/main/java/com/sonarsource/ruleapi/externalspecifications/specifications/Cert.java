/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CleanupReport;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.get.Fetcher;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.services.ReportService;
import com.sonarsource.ruleapi.utilities.ComparisonUtilities;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class Cert extends AbstractMultiLanguageStandard implements TaggableStandard, CleanupReport {

  public static final String RESULTS = "results";

  private static final Logger LOGGER = Logger.getLogger(Cert.class.getName());

  private static final String TAG = "cert";
  private static final String REFERENCE_NAME = "CERT";
  private static final String REFERENCE_PATTERN = "[A-Z]{3}\\d\\d-[A-Za-z]+.";
  private static final String TITLE_AND_INTRO = "<h2>%1$s Coverage of CERT %2$s Standard</h2>\n" +
          "<p>The following table lists the CERT %2$s standard items %1$s is able to detect, " +
          "and for each of them, the rules providing this coverage.</p>";

  private static final CertType CPP = new CertType(Language.CPP, new String [] {"146440541", "146440543"});
  private static final CertType C = new CertType(Language.C, new String [] {"158237133", "158237251"});
  private static final CertType JAVA = new CertType(Language.JAVA, new String [] {"158237393","158237397"});


  private CertType currentCertLanguage = null;


  @Override
  protected List<Language> getAllLanguages(){
    return Arrays.asList(Language.C, Language.CPP, Language.JAVA);
  }

  @Override
  public String getNameIfStandardApplies(Rule rule) {
    Language ruleLang = Language.fromString(rule.getLanguage());
    if (getAllLanguages().contains(ruleLang)) {

      String suffix = ruleLang.name();
      if (ruleLang == Language.JAVA) {
        suffix = "J";
      }

      for (String id : getRspecReferenceFieldValues(rule)) {
        // strip trailing '.'
        id = id.replace(".", "");
        if (id.endsWith(suffix)) {
          return getStandardName();
        }
      }
    }

    return null;
  }



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

      JSONObject json = Fetcher.getJsonFromUrl(baseUrl + encodedTitle);
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

    sb.append(String.format(ReportService.HEADER_TEMPLATE, currentCertLanguage.getLanguage().getReportName(), REFERENCE_NAME))
            .append(String.format(TITLE_AND_INTRO, currentCertLanguage.getLanguage().getReportName(), currentCertLanguage.getLanguage().getRspec()))
            .append(ReportService.TABLE_OPEN)
            .append("<thead><tr><th>CERT ID</th><th>CERT Title</th><th>Implementing Rules</th></tr></thead>")
            .append("<tbody>");


    Map<String, CodingStandardRuleCoverage> map = this.getRulesCoverage();

    for (CertRule certRule : certRules) {
      String certId = certRule.getId();

      CodingStandardRuleCoverage csrc = map.get(certId);
      List<Rule> implRules = csrc.getImplementedBy();

      String title = certRule.getTitle().replaceFirst(certId, "");

      if (!implRules.isEmpty()) {
        sb.append("<tr><td><a href='").append(certRule.getUrl())
                .append("' target='_blank'>").append(certRule.getId()).append("</a></td><td>")
                .append(title).append("</td>\n<td>");
        for (Rule rule : implRules) {
          sb.append(Utilities.getNemoLinkedRuleReference(instance, rule));
        }
        sb.append("</td></tr>\n");
      }
    }

    sb.append("</tbody></table>");

    sb.append(ReportService.FOOTER_TEMPLATE);


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
    private List<String> wikiPageId = new ArrayList<>();
    private final List<CertRule> rules = new ArrayList<>();

    CertType(Language language, String[] wikiPageId) {
      this.language = language;
      this.wikiPageId = new ArrayList<>(Arrays.asList(wikiPageId));
    }

    public Language getLanguage() {
      return this.language;
    }

    public CodingStandardRule[] getCodingStandardRules(){
      if (rules.isEmpty()) {

        String baseUrl = "https://www.securecoding.cert.org/confluence";
        String url = "%s/rest/api/content/%s/child/page?expand=title,metadata.labels&limit=200";
        List<String> ids = new ArrayList<>();
        ids.addAll(wikiPageId);

        while (!ids.isEmpty()){
          JSONObject jsonObject = Fetcher.getJsonFromUrl(String.format(url, baseUrl, ids.remove(0)));
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
