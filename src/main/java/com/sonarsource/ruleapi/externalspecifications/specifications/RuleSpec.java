/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CustomerReport;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RuleSpec implements CustomerReport {

  private static final int TOTAL_LANGUAGES = Language.LEGACY_LANGUAGES.size() + Language.STRONGLY_TYPED_LANGUAGES.size()
          + Language.LOOSLY_TYPE_LANGUAGES.size();

  private InvertedCoveredLangaugesCountRuleSort sort = new InvertedCoveredLangaugesCountRuleSort();

  @Override
  public String getStandardName() {

    return "RSpec";
  }

  @Override
  public String getHtmlReport(String instance) {

    List<Rule> rules = RuleMaker.getRulesByJql("'Covered languages' is not empty", "");

    return getRuleTable(rules);
  }

  protected String getRuleTable(List<Rule> rules) {

    StringBuilder sb = new StringBuilder();

    sb.append("<style>\n" +
            "td {\n" +
            "  padding-top: 10px;\n" +
            "  border-bottom:1pt solid black;\n" +
            "  border-spacing: 10px;\n" +
            "  border-collapse: separate;\n" +
            "}\n" +
            "\n" +
            ".small { font-size: 75%;}\n" +
            "\n" +
            "</style>");

    sb.append("<h2>Backlog of Most Relevant Missing Rules</h2>\n")
            .append("<table><tr><th colspan='3'>By Language</th></tr>" +
                    "<tr><th class='left'>Stronly typed</th><th class='left'>Weakly typed</th><th class='left'>Legacy</th><th></th></tr>");

    Collections.sort(rules, sort);

    for (Rule rule : rules) {
      sb.append(buildRuleRow(rule));
    }

    sb.append("</table>");
    sb.append(Utilities.getFormattedDateString());
    return sb.toString();
  }

  protected static String buildRuleRow(Rule rule) {

    if (rule.getCoveredLanguages().size() == 1 || rule.getCoveredLanguages().size() >= TOTAL_LANGUAGES){
      return "";
    }

    StringBuilder sb = new StringBuilder();
    String td = "</td><td>";

    List<Language> strong = new ArrayList<>();
    List<Language> weak = new ArrayList<>();
    List<Language> legacy = new ArrayList<>();
    populateCoveredLanguageLists(rule, strong, weak, legacy);

    List<Language> irrelevant = getIrrelevantLanguageList(rule);

    String missingStrong = getMissing(Language.STRONGLY_TYPED_LANGUAGES, strong, irrelevant);
    String missingWeak = getMissing(Language.LOOSLY_TYPE_LANGUAGES, weak, irrelevant);
    String missingLegacy = getMissing(Language.LEGACY_LANGUAGES, legacy, irrelevant);

    if (!missingLegacy.isEmpty() || !missingWeak.isEmpty() || !missingStrong.isEmpty()) {
      sb.append("<tr><td>").append(missingStrong).append(td)
              .append(missingWeak).append(td)
              .append(missingLegacy).append(td)
              .append(Utilities.getJiraLinkedRuleReference(rule))
              .append("<span class='small'>")
              .append(Utilities.setToString(rule.getCoveredLanguages(), true))
              .append("</span></td></tr>");
    }

    return sb.toString();
  }

  protected static List<Language> getIrrelevantLanguageList(Rule rule) {

    List<Language> irrelevant = new ArrayList<>();
    for (String lang : rule.getIrrelevantLanguages()) {
      irrelevant.add(Language.fromString(lang));
    }
    return irrelevant;
  }

  protected static void populateCoveredLanguageLists(Rule rule, List<Language> strong, List<Language> weak, List<Language> legacy) {

    for (String lang : rule.getCoveredLanguages()) {
      Language language = Language.fromString(lang);
      if (language != null) {
        if (Language.STRONGLY_TYPED_LANGUAGES.contains(language)) {
          strong.add(language);
        } else if (Language.LOOSLY_TYPE_LANGUAGES.contains(language)) {
          weak.add(language);
        } else if (Language.LEGACY_LANGUAGES.contains(language)) {
          legacy.add(language);
        }
      }
    }
  }

  protected static String getMissing(Collection<Language> languageListByType, List<Language> covered, List<Language> irrelevant) {

    int coveredCount = 0;

    StringBuilder sb = new StringBuilder();

    for (Language lang : languageListByType) {
      if (covered.contains(lang)) {
        coveredCount++;
      } else if (!irrelevant.contains(lang)) {
        if (sb.length() > 0) {
          sb.append(", ");
        }
        sb.append(lang.getRspec());
      }
    }
    if (coveredCount > 0) {
      return sb.toString();
    }
    return "";
  }


  private static class InvertedCoveredLangaugesCountRuleSort implements  Comparator<Rule> {
    @Override
    public int compare(Rule o1, Rule o2) {
      // biggest-to-smallest sort desired
      return Integer.compare(o2.getCoveredLanguages().size(), o1.getCoveredLanguages().size());
    }
  }

}
