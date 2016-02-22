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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RuleSpec implements CustomerReport {

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

    sb.append("<h2>RSpec coverage by language</h2>\n")
            .append("<table><tr><th></th><th>Stronly typed</th><th>Weakly typed</th><th>Legacy</th></tr>");

    Collections.sort(rules, sort);

    for (Rule rule : rules) {
      sb.append(buildRuleRow(rule));
    }

    sb.append("</table>");
    return sb.toString();
  }

  protected String buildRuleRow(Rule rule) {

    StringBuilder sb = new StringBuilder();

    List<String> strong = new ArrayList<>();
    List<String> weak = new ArrayList<>();
    List<String> legacy = new ArrayList<>();

    String td = "</td><td>";

    for (String lang : rule.getCoveredLanguages()) {
      Language language = Language.fromString(lang);
      if (language != null) {
        switch (Language.fromString(lang).getLanguageType()) {
          case STRONG:
            strong.add(lang);
            break;
          case LOOSE:
            weak.add(lang);
            break;
          case LEGACY:
            legacy.add(lang);
            break;
          default:
            break;
        }
      }
    }

    sb.append("<tr><td>").append(Utilities.getJiraLinkedRuleReference(rule)).append(td)
            .append(Utilities.listToString(strong, true)).append(td)
            .append(Utilities.listToString(weak, true)).append(td)
            .append(Utilities.listToString(legacy, true)).append("</td></tr>");

    return sb.toString();
  }

  private static class InvertedCoveredLangaugesCountRuleSort implements  Comparator<Rule> {
    @Override
    public int compare(Rule o1, Rule o2) {
      // biggest-to-smallest sort desired
      return Integer.compare(o2.getCoveredLanguages().size(), o1.getCoveredLanguages().size());
    }
  }


}
