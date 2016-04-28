/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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

    sb.append("<h2>Missing RSpec coverage </h2>\n")
            .append("<table><tr><th colspan='3'>Missing from</th></tr><tr><th>Stronly typed</th><th>Weakly typed</th><th>Legacy</th><th></th></tr>");

    Collections.sort(rules, sort);

    for (Rule rule : rules) {
      sb.append(buildRuleRow(rule));
    }

    sb.append("</table>");
    return sb.toString();
  }

  protected String buildRuleRow(Rule rule) {

    if (rule.getCoveredLanguages().size() == 1 || rule.getCoveredLanguages().size() >= TOTAL_LANGUAGES){
      return "";
    }

    StringBuilder sb = new StringBuilder();
    String td = "</td><td>";

    List<Language> strong = new ArrayList<>();
    List<Language> weak = new ArrayList<>();
    List<Language> legacy = new ArrayList<>();

    populateLanguageLists(rule, strong, weak, legacy);

    List<Language> irrelevant = getIrrelevantLanguageList(rule);

    strong.removeAll(irrelevant);
    weak.removeAll(irrelevant);
    legacy.removeAll(irrelevant);

    if (!strong.isEmpty() || !weak.isEmpty() || !legacy.isEmpty()) {
      sb.append("<tr><td>").append(getMissing(Language.STRONGLY_TYPED_LANGUAGES, strong)).append(td)
              .append(getMissing(Language.LOOSLY_TYPE_LANGUAGES, weak)).append(td)
              .append(getMissing(Language.LEGACY_LANGUAGES, legacy)).append(td)
              .append(Utilities.getJiraLinkedRuleReference(rule))
              .append(Utilities.setToString(rule.getCoveredLanguages(), true))
              .append("</td></tr>");
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

  protected static void populateLanguageLists(Rule rule, List<Language> strong, List<Language> weak, List<Language> legacy) {

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

  protected static String getMissing(Collection<Language> languageListByType, List<Language> covered) {

    int coveredCount = 0;

    StringBuilder sb = new StringBuilder();

    for (Language lang : languageListByType) {
      if (covered.contains(lang)) {
        coveredCount++;
      } else {
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
