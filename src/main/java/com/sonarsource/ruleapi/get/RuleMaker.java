/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.google.common.base.Strings;
import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Given a key and a language, retrieves the relevant Issue
 * from Jira (if possible), and populates a new Rule with
 * the details.
 *
 * Given a key, langauge and SQ instance URL, retrieves the
 * relevant Issue from the SonarQube instance.
 *
 * Rule population from Jira includes the retrieval
 * and overlay of values from any language-specific subtask,
 * as well as proper handling of labeled language-specific
 * variants such as {code} blocks and parameter variations.
 */
public class RuleMaker {

  private static final String DESCRIPTION = "description";

  private static final Fetcher FETCHER = new Fetcher();

  protected static Map<String, Rule> jiraRuleCache = new HashMap<>();


  private RuleMaker() {
  }

  public static List<Rule> getRulesFromSonarQubeForLanguage(Language language, String instance) {

    if (Language.CSH.equals(language)) {
      File rules = new File("rules.xml");
      File sqale = new File("sqale.xml");
      if (! (rules.exists() && sqale.exists())) {
        FETCHER.fetchRuleDataFromRedmond("tv.sonar@outlook.com", "RmY6wzrFC6mjgBYCdr5nEpbp");
      }
      return getRulesFromXml(language, rules, sqale);
    }

    return RuleMaker.getRulesFromSonarQubeByQuery(instance, "repositories=" + language.getSq());
  }

  public static Rule getRuleFromSonarQubeByKey(String sonarQubeInstance, String ruleKey, Language language) {

    Fetcher fetcher = new Fetcher();

    String extendedKey = language.getSq() + ":" + ruleKey;

    JSONObject jsonRule = fetcher.fetchRuleFromSonarQube(sonarQubeInstance, extendedKey);
    return  populateFieldsFromSonarQube(jsonRule);
  }

  public static Rule getCachedRuleByKey(String key, String language) {

    String normalKey = Utilities.normalizeKey(key);
    Rule rule = jiraRuleCache.get(normalKey);
    if (rule == null) {
      rule = getRuleByKey(normalKey, language);
    }
    return rule;
  }

  protected static List<Rule> getRulesFromXml(Language language, File ruleFile, File sqaleFile) {

    List<Rule> rules = new ArrayList<>();

    SAXReader reader = new SAXReader();
    try {
      Document sqaleXml = reader.read(sqaleFile.toURI().toURL());
      Element sqaleRoot = sqaleXml.getRootElement();

      Document rulesXml = reader.read(ruleFile.toURI().toURL());
      Element rulesRoot = rulesXml.getRootElement();

      for ( Iterator i = rulesRoot.elementIterator( "rule" ); i.hasNext(); ) {
        Element xRule = (Element)i.next();
        rules.add(populateFieldsFromXml(xRule, sqaleRoot, language));
      }

    } catch (DocumentException e) {
      throw new RuleException(e);
    } catch (MalformedURLException e) {
      throw new RuleException(e);
    }

    return rules;
  }

  /**
   * Given a rule key and a language (e.g. Java, ABAP, etc), fetches
   * Issue from Jira and creates from it a Rule. Rule population includes
   * translation of markdown to HTML so that return value can be compared
   * directly with Implementation.
   *
   * @param key rule key - legacy key, S### or RSPEC-###
   * @param language language of rule. Used to distinguish among language-specific options
   * @return rule fetched from Jira
   */
  public static Rule getRuleByKey(String key, String language) {

    Rule rule = new Rule(language);
    JSONObject jsonRule = FETCHER.fetchIssueByKey(key);
    fleshOutRule(FETCHER, rule, jsonRule);

    jiraRuleCache.put(rule.getKey(), rule);
    return rule;
  }

  public static List<Rule> getRulesFromSonarQubeByQuery(String instance, String query) {

    List<Rule> rules = new ArrayList<Rule>();

    Fetcher fetcher = new Fetcher();
    List<JSONObject> jsonRules = fetcher.fetchRulesFromSonarQube(instance, query);

    for (JSONObject jsonRule : jsonRules) {
      rules.add(populateFieldsFromSonarQube(jsonRule));
    }

    return rules;
  }

  public static List<Rule> getCachedRulesByJql(String query, String language) {
    List<Rule> rules = new ArrayList<Rule>();

    List<JSONObject> issues = FETCHER.fetchIssueKeysBySearch(query);

    for (JSONObject issueKey : issues) {
      rules.add(getCachedRuleByKey(issueKey.get("key").toString(), language));
    }
    return rules;
  }

  /**
   * Retrieve a list of open rules based on a JQL snippet.
   * @param query the jql to use
   * @param language the language sought. Not used in the query but used to populate rule members
   * @return a list of retrieved rules
   */
  public static List<Rule> getRulesByJql(String query, String language) {
    List<Rule> rules = new ArrayList<Rule>();

    List<JSONObject> issues = FETCHER.fetchIssueKeysBySearch(query);

    for (JSONObject issueKey : issues) {
      rules.add(getRuleByKey(issueKey.get("key").toString(), language));
    }

    return rules;
  }

  protected static void fleshOutRule(Fetcher fetcher, Rule rule, JSONObject jsonRule) {
    if (jsonRule != null) {
      populateFields(rule, jsonRule);

      JSONObject fields = (JSONObject)jsonRule.get("fields");
      JSONArray subtasks = (JSONArray) fields.get("subtasks");

      JSONObject subIssue = getSubtask(fetcher, rule.getLanguage(), subtasks);
      if (subIssue != null) {

        Rule subRule = new Rule(rule.getLanguage());
        populateFields(subRule, subIssue);
        rule.merge(subRule);
      }
    }
  }

  protected static Rule populateFieldsFromSonarQube(JSONObject jsonRule) {
    Rule rule = new Rule((String) jsonRule.get("langName"));

    String rawKey = ((String) jsonRule.get("key")).split(":")[1];
    rule.setKey(Utilities.normalizeKey(rawKey));
    rule.setRepo((String) jsonRule.get("repo"));

    rule.setLegacyKeys(new ArrayList<String>());
    rule.getLegacyKeys().add(rawKey);

    rule.setStatus(Rule.Status.valueOf((String) jsonRule.get("status")));
    rule.setSeverity(Rule.Severity.valueOf((String) jsonRule.get("severity")));

    rule.setTitle((String) jsonRule.get("name"));
    setDescription(rule, (String) jsonRule.get("htmlDesc"), false);

    rule.setSqaleCharac((String) jsonRule.get("defaultDebtChar"));
    setSubcharacteristic(rule, (String) jsonRule.get("defaultDebtSubChar"));
    setRemediationFunction(rule, (String) jsonRule.get("defaultDebtRemFnType"));
    SonarQubeHelper.setSqaleConstantValueFromSqInstance(rule, (String) jsonRule.get("defaultDebtRemFnOffset"));
    rule.setSqaleLinearFactor((String) jsonRule.get("defaultDebtRemFnCoeff"));
    rule.setSqaleLinearArgDesc((String) jsonRule.get("effortToFixDescription"));

    rule.setTags(new ArrayList<String>((JSONArray) jsonRule.get("sysTags")));

    rule.setTemplate((Boolean) jsonRule.get("isTemplate"));

    JSONArray jsonParams = (JSONArray) jsonRule.get("params");
    for (JSONObject obj : (List<JSONObject>)jsonParams) {
      Parameter param = new Parameter();
      param.setKey((String) obj.get("key"));
      param.setDescription((String) obj.get("htmlDesc"));

      String tmp = (String) obj.get("defaultValue");
      if (tmp != null) {
        param.setDefaultVal(tmp);
      }
      param.setType((String) obj.get("type"));
      rule.getParameterList().add(param);
    }

    return rule;
  }

  protected static void populateFields(Rule rule, JSONObject issue) {
    rule.setKey(issue.get("key").toString());
    JiraHelper.setStatus(rule, issue);

    String tmp = JiraHelper.getCustomFieldValue(issue, "Default Severity");
    if (tmp != null) {
      rule.setSeverity(Rule.Severity.valueOf(tmp.toUpperCase()));
    }

    JiraHelper.setDefaultProfiles(rule, issue);

    rule.setLegacyKeys(JiraHelper.getCustomFieldValueAsList(issue, "Legacy Key"));

    rule.setTargetedLanguages(JiraHelper.getCustomFieldStoredAsList(issue, "Targeted languages"));
    rule.setCoveredLanguages(JiraHelper.getCustomFieldStoredAsList(issue, "Covered Languages"));
    rule.setIrrelevantLanguages(JiraHelper.getCustomFieldStoredAsList(issue, "Irrelevant for Languages"));

    rule.setTitle(JiraHelper.getJsonFieldValue(issue, "summary"));
    rule.setMessage(JiraHelper.getCustomFieldValue(issue, "Message"));

    setDescription(rule, JiraHelper.getJsonFieldValue(issue, DESCRIPTION), true);

    JiraHelper.setSqale(rule, issue);

    rule.setTemplate("Yes".equals(JiraHelper.getCustomFieldValue(issue, "Template Rule")));

    rule.setParameterList(JiraHelper.handleParameterList(JiraHelper.getCustomFieldValue(issue, "List of parameters"), rule.getLanguage()));

    rule.setTags(JiraHelper.getListFromJsonFieldValue(issue, "labels"));

    JiraHelper.setReferences(rule, issue);
  }

  protected static Rule populateFieldsFromXml(Element xRule, Element sqaleRoot, Language language) {
    Rule rule = new Rule(language.getRspec());

    rule.setKey(xRule.element("key").getStringValue());
    rule.setTitle(xRule.element("name").getStringValue());
    rule.setSeverity(Rule.Severity.valueOf(xRule.element("severity").getStringValue()));

    rule.setStatus(Rule.Status.fromString(getTextFromElement(xRule, "status")));

    Node sqaleKey = sqaleRoot.selectSingleNode("//rule-key[contains(., '" + rule.getKey() + "')]");
    if (sqaleKey != null) {
      Element sqaleElement = sqaleKey.getParent();
      String sqaleSubChar = sqaleElement.getParent().element("key").getStringValue();
      rule.setSqaleSubCharac(Rule.Subcharacteristic.valueOf(sqaleSubChar));

      Iterator itr = sqaleElement.elementIterator("prop");
      while (itr.hasNext()){
        Element e = (Element) itr.next();
        Element key = e.element("key");
        if ("remediationFunction".equals(key.getStringValue())) {
          setRemediationFunction(rule, e.element("txt").getStringValue());
        } else {
          rule.setSqaleConstantCostOrLinearThreshold(e.element("val").getStringValue());
        }
      }
    }

    for (Iterator itr = xRule.elementIterator("tag"); itr.hasNext();) {
      rule.getTags().add(((Element) itr.next()).getStringValue());
    }

    for (Iterator itr = xRule.elementIterator("param"); itr.hasNext();) {
      Element xParam = (Element) itr.next();
      Parameter parameter = new Parameter();
      parameter.setKey(getTextFromElement(xParam, "key"));
      parameter.setDescription(getTextFromElement(xParam, DESCRIPTION));
      parameter.setDefaultVal(getTextFromElement(xParam, "defaultValue"));
      parameter.setType(getTextFromElement(xParam, "type"));
    }

    rule.setTemplate("MULTIPLE".equals(xRule.element("cardinality").getStringValue()));

    setDescription(rule, getTextFromElement(xRule, DESCRIPTION), false);

    return rule;
  }

  protected static String getTextFromElement(Element parent, String elementName) {
    Element tmp = parent.element(elementName);
    if (tmp != null) {
      return tmp.getStringValue();
    }
    return null;
  }

  private static JSONObject getSubtask(Fetcher fetcher, String language, JSONArray tasks) {
    if (tasks != null && ! Strings.isNullOrEmpty(language)) {
      for (JSONObject subt : (Iterable<JSONObject>) tasks) {
        if (isLanguageMatch(language, JiraHelper.getJsonFieldValue(subt, "summary").trim())) {
          return fetcher.fetchIssueByKey(subt.get("key").toString());
        }
      }
    }
    return null;
  }

  protected static boolean isLanguageMatch(String language, String candidate) {

    String ucLanguage = language.toUpperCase();
    String ucCandidate = candidate.toUpperCase();

    if (isCFamilyMatch(ucLanguage)) {
      return isCFamilyMatch(ucCandidate);
    }

    if (ucLanguage.equals(ucCandidate)) {
      return true;
    }
    if (!ucCandidate.startsWith(ucLanguage)) {
      return false;
    }
    return ucCandidate.matches(ucLanguage + "\\W.*");
  }

  protected static boolean isCFamilyMatch(String candidate) {
    String[] cFamily = {"C-Family", "C", "CPP", "C++", "Objective-C"};

    if ("C#".equalsIgnoreCase(candidate) || candidate.matches("(?i:C#\\W.*)")) {
      return false;
    }

    for (String option : cFamily) {
      if (option.equalsIgnoreCase(candidate)) {
        return true;
      }
      if (candidate.matches("(?i:" + option + "\\W.*)")) {
        return true;
      }
    }
    return false;
  }

  /**
   * <p>Splits the full rule description into into its constituent parts
   * and processes each, converting markdown to HTML and making the
   * correct choices among language-specific {code} blocks based
   * on the rule's language property.</p>
   *
   * <p>NOTE that this method cannot distinguish among language-specific
   * word choices, e.g. [Function|Method|Procedure], so the full set of
   * choices will remain intact in the resulting text.</p>
   *
   * @param rule the rule to be populated
   * @param fullDescription the text from which to populate it.
   */
  public static void setDescription(Rule rule, String fullDescription, boolean isMarkdown) {

    String fullDesc = fullDescription;
    String markdownH2Match = "h2\\.";
    String htmlH2 = "<h2>";

    if (Rule.Status.DEPRECATED.equals(rule.getStatus())) {
      StringBuilder sb = new StringBuilder();

      for (String key : rule.getDeprecationLinks()) {
        if (sb.length() > 0) {
          sb.append(", ");
        }
        sb.append(Utilities.denormalizeKey(key));
      }

      if (! rule.getDeprecationLinks().isEmpty()) {
        sb.insert(0, "\r\n\r\nh2. Deprecated\r\nThis rule is deprecated, use ");
        sb.append(" instead.");
      } else {
        sb.append("\r\n\r\nh2. Deprecated\r\nThis rule is deprecated, and will eventually be removed.");
      }

      sb.insert(0, fullDesc);

      fullDesc = sb.toString();
    }

    rule.setFullDescription(fullDesc);
    if (fullDesc != null && fullDesc.length() > 0) {
      String[] markdownPieces = fullDesc.split(markdownH2Match);
      String[] htmlPieces = fullDesc.split(htmlH2);

      if (isMarkdown) {
        JiraHelper.handleMarkdown(rule, markdownPieces);
      } else {
        SonarQubeHelper.handleHtml(rule, htmlPieces);
      }
    }
  }

  public static void setSubcharacteristic(Rule rule, String candidate) {
    if (! Strings.isNullOrEmpty(candidate)) {
      for (Rule.Subcharacteristic subchar : Rule.Subcharacteristic.values()) {
        if (subchar.name().equals(candidate) || subchar.getRspecName().equals(candidate)) {
          rule.setSqaleSubCharac(subchar);
          return;
        }
      }
    }
  }

  public static void setRemediationFunction(Rule rule, String candidate) {
    if (! Strings.isNullOrEmpty(candidate)) {
      for (Rule.RemediationFunction fcn: Rule.RemediationFunction.values()) {
        if (fcn.name().equals(candidate) || fcn.getFunctionName().equals(candidate)) {
          rule.setSqaleRemediationFunction(fcn);
          return;
        }
      }
    }
  }
}
