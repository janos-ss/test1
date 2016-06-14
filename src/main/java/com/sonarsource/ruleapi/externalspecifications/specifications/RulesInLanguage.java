/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.get.Fetcher;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.fest.util.Strings;
import org.json.simple.JSONObject;


public class RulesInLanguage {

  private static final String SPEC = "SonarAnalyzer";
  private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

  private static final Comparator<Rule> RULE_SEVERITY_COMPARATOR = new Comparator<Rule>() {
    @Override
    public int compare(Rule r1, Rule r2) {
      return r1.getSeverity().compareTo(r2.getSeverity());
    }
  };


  public String getHtmlLanguageReport(String instance, Language language) {

    if (language == null || Strings.isNullOrEmpty(instance)) {
      return "";
    }

    List<Rule> rules = RuleMaker.getRulesFromSonarQubeForLanguage(language, instance);
    if (rules.isEmpty()) {
      return "";
    }

    Map<Rule.Type, List<Rule>> typeMap = groupRulesByType(rules);

    StringBuilder sb = new StringBuilder();
    StringBuilder rulesBuilder = new StringBuilder();

    sb.append(String.format(Locale.ENGLISH, HEADER_TEMPLATE, language.getRspec(), SPEC, rules.size()));

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
    sb.append(FOOTER_TEMPLATE);

    return sb.toString();
  }

  private static String iterateRulesInType(String instance, Rule.Type type, List<Rule> typeRules) {

    String td = "</td><td>";
    StringBuilder rulesBuilder = new StringBuilder();

    rulesBuilder.append("<a name='").append(type.toString()).append("'></a>");

    rulesBuilder.append("<h3>").append(type.toString()).append(" Detection Rules</h3>\n")
            .append("<table class=\"table table-striped table-condensed table-hover\">\n")
            .append(" <thead><tr> <th>Rule ID</th> <th>Name</th> <th>Sonar&nbsp;way</th> <th>Tags</th> <th>In Action</th> </tr> </thead>\n")
            .append(" <tbody> \n");

    Collections.sort(typeRules, RULE_SEVERITY_COMPARATOR);
    for (Rule rule : typeRules) {
      Set<String> tags = rule.getTags();
      tags.remove("bug");
      tags.remove("security");

      String severityName = rule.getSeverity().getSeverityName();

      rulesBuilder.append("<tr><td>")
              .append(Utilities.getInstanceLinkedRuleKey(instance, rule, true)).append(td)
              .append("<a title='").append(severityName).append("'>")
              .append("<i class=\"icon-severity-").append(severityName).append("\"></i></a> ")
              .append(rule.getTitle()).append("</td>")
              .append("<td class=\"text-center\">").append(isRuleDefault(rule)?"<a title='Included in Sonar way'><span id=\"checkmark\"></span></a>":"").append(td)
              .append(Utilities.setToString(tags, true)).append(td)
              .append(getInActionLink(rule, instance));

      rulesBuilder.append("</td></tr>\n");
    }
    rulesBuilder.append(" </tbody>\n</table><br/>\n");

    return rulesBuilder.toString();
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


  private static final String HEADER_TEMPLATE = "<!DOCTYPE html>\n" +
          "<html lang=\"en\">\n" +
          "  <head>\n" +
          "    <meta charset=\"utf-8\">\n" +
          "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
          "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
          "\n" +
          "    <title>%2$s for %1$s</title>\n" +
          "\n" +
          "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\">\n" +
          "\n" +
          "    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->\n" +
          "    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->\n" +
          "    <!--[if lt IE 9]>\n" +
          "      <script src=\"https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js\"></script>\n" +
          "      <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>\n" +
          "    <![endif]-->\n" +
          "\n" +
          "<style>\n" +
          ".header-counter {\n" +
          "    font-size: 24px;\n" +
          "}\n" +
          "#checkmark {\n" +
          "    display:inline-block;\n" +
          "    width: 15px;\n" +
          "    height:15px;\n" +
          "    -ms-transform: rotate(35deg); /* IE 9 */\n" +
          "    -webkit-transform: rotate(35deg); /* Chrome, Safari, Opera */\n" +
          "    transform: rotate(35deg);\n" +
          "}\n" +
          "#checkmark:before{\n" +
          "  content:\"\"; position: absolute; width:3px; height:12px; background-color:#85bb43; left:7px; top:2px;\n" +
          "}\n" +
          "#checkmark:after{\n" +
          "  content:\"\"; position: absolute; width:5px; height:3px; background-color:#85bb43; left:4px; top:11px;\n" +
          "}\n" +
          "[class^=icon-severity-] {\n" +
          "  display:inline-block;vertical-align:top;width:16px;height:16px;background-size:14px 14px;background:no-repeat 50%%\n" +
          "}\n" +
          ".icon-severity-Blocker {\n" +
          "  background-image:url(\"data:image/svg+xml;charset=utf-8,%%3Csvg width='14' height='14' xmlns='http://www.w3.org/2000/svg' fill-rule='evenodd' clip-rule='evenodd' stroke-linejoin='round' stroke-miterlimit='1.414'%%3E%%3Cpath d='M7 1c1.09 0 2.092.268 3.012.805a5.96 5.96 0 0 1 2.183 2.183C12.732 4.908 13 5.91 13 7s-.268 2.092-.805 3.012a5.96 5.96 0 0 1-2.183 2.183C9.092 12.732 8.09 13 7 13s-2.092-.268-3.012-.805a5.96 5.96 0 0 1-2.183-2.183C1.268 9.092 1 8.09 1 7s.268-2.092.805-3.012a5.96 5.96 0 0 1 2.183-2.183A5.863 5.863 0 0 1 7 1zm1 9.742V9.258a.258.258 0 0 0-.07-.184A.226.226 0 0 0 7.758 9h-1.5a.247.247 0 0 0-.18.078.247.247 0 0 0-.078.18v1.484c0 .068.026.128.078.18a.247.247 0 0 0 .18.078h1.5a.23.23 0 0 0 .172-.074c.047-.05.07-.11.07-.184zm-.016-2.687l.14-4.852a.15.15 0 0 0-.077-.14A.284.284 0 0 0 7.86 3H6.14a.284.284 0 0 0-.187.063.152.152 0 0 0-.078.14l.133 4.852c0 .052.026.097.078.136.052.04.115.06.187.06H7.72c.072 0 .133-.02.182-.06a.17.17 0 0 0 .082-.13z' fill='%%23D43340' fill-rule='nonzero'/%%3E%%3C/svg%%3E\")\n" +
          "}\n" +
          ".icon-severity-Critical {\n" +
          "  background-image:url(\"data:image/svg+xml;charset=utf-8,%%3Csvg width='14' height='14' xmlns='http://www.w3.org/2000/svg' fill-rule='evenodd' clip-rule='evenodd' stroke-linejoin='round' stroke-miterlimit='1.414'%%3E%%3Cpath d='M11.03 6.992a.47.47 0 0 0-.14-.35L7.353 3.1a.475.475 0 0 0-.352-.14c-.14 0-.25.046-.35.14L3.11 6.64a.474.474 0 0 0-.14.35c0 .14.047.258.14.352l.71.71c.095.094.21.14.353.14a.47.47 0 0 0 .35-.14L6 6.58v3.92c0 .135.05.253.148.352.1.098.217.148.352.148h1c.135 0 .253-.05.352-.148A.48.48 0 0 0 8 10.5V6.578l1.477 1.477c.1.1.216.148.35.148.137 0 .254-.05.353-.148l.71-.71a.48.48 0 0 0 .14-.353zM13 7c0 1.09-.268 2.092-.805 3.012a5.96 5.96 0 0 1-2.183 2.183C9.092 12.732 8.09 13 7 13s-2.092-.268-3.012-.805a5.96 5.96 0 0 1-2.183-2.183C1.268 9.092 1 8.09 1 7s.268-2.092.805-3.012a5.96 5.96 0 0 1 2.183-2.183C4.908 1.268 5.91 1 7 1s2.092.268 3.012.805a5.96 5.96 0 0 1 2.183 2.183C12.732 4.908 13 5.91 13 7z' fill='%%23D43340' fill-rule='nonzero'/%%3E%%3C/svg%%3E\")\n" +
          "}\n" +
          ".icon-severity-Major {\n" +
          "  background-image:url(\"data:image/svg+xml;charset=utf-8,%%3Csvg width='14' height='14' xmlns='http://www.w3.org/2000/svg' fill-rule='evenodd' clip-rule='evenodd' stroke-linejoin='round' stroke-miterlimit='1.414'%%3E%%3Cpath d='M10.102 8.898l.796-.796c.1-.1.15-.217.15-.352a.48.48 0 0 0-.15-.352L7.352 3.852A.481.481 0 0 0 7 3.702a.48.48 0 0 0-.352.15L3.102 7.398c-.1.1-.15.217-.15.352 0 .135.05.253.15.352l.796.796c.1.1.217.15.352.15a.48.48 0 0 0 .352-.15L7 6.5l2.398 2.398c.1.1.217.15.352.15a.48.48 0 0 0 .352-.15zM13 7c0 1.09-.268 2.092-.805 3.012a5.96 5.96 0 0 1-2.183 2.183C9.092 12.732 8.09 13 7 13s-2.092-.268-3.012-.805a5.96 5.96 0 0 1-2.183-2.183C1.268 9.092 1 8.09 1 7s.268-2.092.805-3.012a5.96 5.96 0 0 1 2.183-2.183C4.908 1.268 5.91 1 7 1s2.092.268 3.012.805a5.96 5.96 0 0 1 2.183 2.183C12.732 4.908 13 5.91 13 7z' fill='%%23D43340' fill-rule='nonzero'/%%3E%%3C/svg%%3E\")\n" +
          "}\n" +
          ".icon-severity-Minor {\n" +
          "  background-image:url(\"data:image/svg+xml;charset=utf-8,%%3Csvg width='14' height='14' xmlns='http://www.w3.org/2000/svg' fill-rule='evenodd' clip-rule='evenodd' stroke-linejoin='round' stroke-miterlimit='1.414'%%3E%%3Cpath d='M7.352 10.148l3.546-3.546c.1-.1.15-.217.15-.352a.48.48 0 0 0-.15-.352l-.796-.796a.481.481 0 0 0-.352-.15.48.48 0 0 0-.352.15L7 7.5 4.602 5.102a.481.481 0 0 0-.352-.15.48.48 0 0 0-.352.15l-.796.796c-.1.1-.15.217-.15.352 0 .135.05.253.15.352l3.546 3.546c.1.1.217.15.352.15a.48.48 0 0 0 .352-.15zM13 7c0 1.09-.268 2.092-.805 3.012a5.96 5.96 0 0 1-2.183 2.183C9.092 12.732 8.09 13 7 13s-2.092-.268-3.012-.805a5.96 5.96 0 0 1-2.183-2.183C1.268 9.092 1 8.09 1 7s.268-2.092.805-3.012a5.96 5.96 0 0 1 2.183-2.183C4.908 1.268 5.91 1 7 1s2.092.268 3.012.805a5.96 5.96 0 0 1 2.183 2.183C12.732 4.908 13 5.91 13 7z' fill='%%2387BB43' fill-rule='nonzero'/%%3E%%3C/svg%%3E\")\n" +
          "}\n" +
          ".icon-severity-Info {\n" +
          "  background-image:url(\"data:image/svg+xml;charset=utf-8,%%3Csvg width='14' height='14' xmlns='http://www.w3.org/2000/svg' fill-rule='evenodd' clip-rule='evenodd' stroke-linejoin='round' stroke-miterlimit='1.414'%%3E%%3Cpath d='M11.03 7.008a.478.478 0 0 0-.14-.352l-.71-.71a.475.475 0 0 0-.352-.14.47.47 0 0 0-.35.14L8 7.42V3.5a.486.486 0 0 0-.148-.352A.48.48 0 0 0 7.5 3h-1a.486.486 0 0 0-.352.148A.48.48 0 0 0 6 3.5v3.922L4.523 5.945a.477.477 0 0 0-.35-.148.484.484 0 0 0-.353.148l-.71.71a.48.48 0 0 0-.14.353c0 .14.046.258.14.35l3.538 3.54c.094.094.21.14.352.14.14 0 .258-.046.352-.14l3.54-3.54a.478.478 0 0 0 .14-.35zM13 7c0 1.09-.268 2.092-.805 3.012a5.96 5.96 0 0 1-2.183 2.183C9.092 12.732 8.09 13 7 13s-2.092-.268-3.012-.805a5.96 5.96 0 0 1-2.183-2.183C1.268 9.092 1 8.09 1 7s.268-2.092.805-3.012a5.96 5.96 0 0 1 2.183-2.183C4.908 1.268 5.91 1 7 1s2.092.268 3.012.805a5.96 5.96 0 0 1 2.183 2.183C12.732 4.908 13 5.91 13 7z' fill='%%2387BB43' fill-rule='nonzero'/%%3E%%3C/svg%%3E\")\n" +
          "}\n" +
          "</style>\n" +
          "  </head>\n" +
          "  <body><a name='top' id='top'></a>\n" +
          "\n" +
          "<div style=\"background-color: rgb(25, 25, 25);height:66px;padding-left:6px\">\n" +
          "  <div class='container'>\n" +
          "  <img src=\"http://www.sonarsource.com/wp-content/themes/sonarsource/images/logo.png\">\n" +
          "  </div>" +
          "</div>\n" +
          "\n" +
          "<div class=\"container\">\n" +
          "\n" +
          "<h2>%2$s for %1$s</h2>\n" +
          "<h3>%3$d Rules</h3>\n" +
          "<p>Offering a set of powerful rules, the %2$s for %1$s's is all you need for finding bugs, vulnerabilities, and code smells in your code. " +
          "With the %2$s for %1$s, monitoring your code quality is no longer a daunting task.</p>\n" +
          "\n" +
          "<br>";

  private static final String FOOTER_TEMPLATE = "\n" +
          "<p><a href='#top'>Back to the top</a></p>" +
          "\n" +
          "    <footer class=\"footer\">\n" +
          "      <p class=\"small\">Powered by <a href=\"http://www.sonarsource.com/\">SonarSource SA</a></p>\n" +
          "    </footer>\n" +
          "  </div><!-- container -->" +
          "\n" +
          "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>\n" +
          "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\" integrity=\"sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS\" crossorigin=\"anonymous\"></script>\n" +
          "  </body>\n" +
          "</html>\n";

}
