/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.domain.ReportAndBadge;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.BadgableMultiLanguage;
import com.sonarsource.ruleapi.get.Fetcher;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.services.ReportService;
import com.sonarsource.ruleapi.services.badge.BadgeGenerator;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.MarkdownConverter;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.fest.util.Strings;
import org.json.simple.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


public class RulesInLanguage implements BadgableMultiLanguage {

  private static final Logger LOGGER = Logger.getLogger(RulesInLanguage.class.getName());

  private static final String TITLE_AND_INTRO = "<h2>Sonar%1$s</h2>\n" +
          "<h3>%2$d Rules</h3>\n" +
          "<p>Offering a set of powerful rules, Sonar%1$s is all you need for finding bugs, vulnerabilities, and code smells in your code. " +
          "With Sonar%1$s, monitoring your code quality is no longer a daunting task.</p>\n" +
          "\n" +
          "<br>";

  private static final String SPEC = "Sonar";
  private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

  private Language language = null;
  private List<Rule> rules = null;

  private static final Comparator<Rule> RULE_SEVERITY_COMPARATOR = (Rule r1, Rule r2)->
          r1.getSeverity().compareTo(r2.getSeverity());


  @Override
  public void setLanguage(Language language) {
    this.language = language;
    rules = null;
  }

  @Override
  public String getStandardName() {

    return "Rules";
  }

  @Override
  public String getBadgeValue(String instance) {
    fetchRules(instance);

    if (rules == null) {
      return "";
    }
    return Integer.toString(rules.size());
  }

  private void fetchRules(String instance) {
    if (language != null && ! Strings.isNullOrEmpty(instance) && rules == null) {
      rules = RuleMaker.getRulesFromSonarQubeForLanguage(language, instance);
    }
  }

  @Override
  public ReportAndBadge getHtmlLanguageReport(String instance, Language language) {

    setLanguage(language);

    ReportAndBadge reportAndBadge = new ReportAndBadge();
    reportAndBadge.setReport(generateReport(instance));

    if (rules != null) {
      BadgeGenerator badger = new BadgeGenerator();
      reportAndBadge.setBadge(badger.getBadge(getStandardName(), Integer.toString(rules.size())));
    }

    return reportAndBadge;
  }

  protected String generateReport(String instance) {

    fetchRules(instance);
    if (rules == null || rules.isEmpty()) {
      return "";
    }

    LOGGER.info("Getting Rules in Language report for " + language.getRspec());

    Map<Rule.Type, List<Rule>> typeMap = groupRulesByType(rules);

    StringBuilder sb = new StringBuilder();
    StringBuilder rulesBuilder = new StringBuilder();

    sb.append(String.format(Locale.ENGLISH, ReportService.HEADER_TEMPLATE, language.getRspec(), SPEC));
    sb.append(String.format(Locale.ENGLISH, TITLE_AND_INTRO, language.getRspec(), rules.size()));

    sb.append("<div class=\"row\">");
    for (Rule.Type type : Rule.Type.values()) {
      List<Rule> typeRules = typeMap.get(type);

      if (typeRules != null) {

        sb.append("<div class=\"col-md-4\"><p class=\"text-center header-counter\">")
                .append(type.toString()).append("</br>")
                .append("<a href='#").append(type.toString()).append("'>")
                .append(typeRules.size()).append("</a></p></div>\n");

        rulesBuilder.append(iterateRulesInType(instance, type, typeRules));
      }
    }
    sb.append("</div>");

    sb.append(rulesBuilder.toString());
    sb.append(ReportService.FOOTER_TEMPLATE);

    return sb.toString();
  }

  protected static String iterateRulesInType(String instance, Rule.Type type, List<Rule> typeRules) {

    StringBuilder rulesBuilder = new StringBuilder();

    rulesBuilder.append("<a name='").append(type.toString()).append("'></a>");

    rulesBuilder.append("<h3>").append(type.toString()).append(" Detection Rules</h3>\n")
            .append(ReportService.TABLE_OPEN)
            .append(" <thead><tr> <th>Rule ID</th> <th>Name</th> <th>Sonar&nbsp;way</th> <th>Tags</th> <th>In Action</th> </tr> </thead>\n")
            .append(" <tbody> \n");

    Collections.sort(typeRules, RULE_SEVERITY_COMPARATOR);
    for (Rule rule : typeRules) {

      rulesBuilder.append(getRuleRow(rule, instance, getInActionLink(rule, instance)));
    }
    rulesBuilder.append(" </tbody>\n</table>\n");

    return rulesBuilder.toString();
  }

  protected static String getRuleRow(Rule rule, String instance, String inActionLink) {
    String td = "</td><td>";

    StringBuilder sb = new StringBuilder();

    Set<String> tags = rule.getTags();
    tags.remove("bug");
    tags.remove("security");

    String severityName = rule.getSeverity().getSeverityName();

    sb.append("<tr><td>")
            .append(Utilities.getInstanceLinkedRuleKey(instance, rule, true)).append(td)
            .append("<a title='").append(severityName).append("'>")
            .append("<i class=\"icon-severity-").append(severityName).append("\"></i></a> ")
            .append(MarkdownConverter.handleEntities(rule.getTitle())).append("</td>")
            .append("<td class=\"text-center\">").append(isRuleDefault(rule)?"<a title='Included in Sonar way'><span id=\"checkmark\"></span></a>":"").append(td)
            .append(Utilities.setToString(tags, true)).append(td)
            .append(inActionLink);

    sb.append("</td></tr>\n");

    return sb.toString();
  }

  private static String getInActionLink(Rule rule, String instance) {

    String ruleKey = Utilities.getDeployedKey(rule);

    // get count: https://sonarqube.com/api/issues/search?rule=squid%3S1154&ps=1
    JSONObject response = Fetcher.getJsonFromUrl(String.format("%s/api/issues/search?ps=1&rules=%s:%s",instance,rule.getRepo(),ruleKey));
    long total = (long) response.get("total");

    if (total > 0) {
      String tot = NUMBER_FORMAT.format(total);
      // link to list: https://sonarqube.com/issues/search#resolved=false|rules=squid%3AS1191
      return String.format("~<a href='%s/issues/search#resolved=false|rules=%s:%s' target='issues'>%s issue%s</a>",instance, rule.getRepo(), ruleKey, tot, total>1?"s":"");
    }

    return "";
  }

  protected static boolean isRuleDefault(Rule rule) {

    for (Profile profile : rule.getDefaultProfiles()) {
      if ("Sonar way".equalsIgnoreCase(profile.getName())){
        return true;
      }
    }
    return false;
  }

  protected static Map<Rule.Type, List<Rule>> groupRulesByType(List<Rule> rules) {

    Map<Rule.Type, List<Rule>> typeMap = new EnumMap<>(Rule.Type.class);

    for (Rule rule : rules) {
      List<Rule> typeList = typeMap.get(rule.getType());
      if (typeList == null) {
        typeList = new ArrayList<>();
        typeMap.put(rule.getType(), typeList);
      }
      typeList.add(rule);
    }
    return typeMap;
  }

}
