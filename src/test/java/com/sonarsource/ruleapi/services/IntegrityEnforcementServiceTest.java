/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.SupportedCodingStandard;
import com.sonarsource.ruleapi.externalspecifications.misra.MisraC2004;
import com.sonarsource.ruleapi.externalspecifications.specifications.Cert;
import com.sonarsource.ruleapi.externalspecifications.specifications.Cwe;
import com.sonarsource.ruleapi.externalspecifications.specifications.OwaspTopTen;
import com.sonarsource.ruleapi.externalspecifications.specifications.SansTop25;
import com.sonarsource.ruleapi.utilities.Language;
import org.junit.Test;

import java.util.*;

import static org.fest.assertions.Assertions.assertThat;


public class IntegrityEnforcementServiceTest {

  IntegrityEnforcementService enforcer = new IntegrityEnforcementService();


  @Test
  public void testDoDropTargetedForIrrlevant() {

    Rule rule = new Rule("Java");
    rule.getIrrelevantLanguages().add("C#");
    rule.getIrrelevantLanguages().add("PHP");
    rule.getTargetedLanguages().add("C#");
    rule.getTargetedLanguages().add("Java");

    assertThat(enforcer.doDropTargetedForIrrelevant(rule)).isNotEmpty();

    List<String> targeted = rule.getTargetedLanguages();
    assertThat("C#").isNotIn(targeted);
    assertThat(targeted).hasSize(1).contains("Java");

    assertThat(rule.getIrrelevantLanguages()).hasSize(2);

  }

  @Test
  public void testDropEmptyMapEntries(){
    Map <Rule, Map<String,Object>> map = new HashMap<>();
    map.put(new Rule(""), new HashMap<String, Object>());

    Map<String, Object> child = new HashMap<>();
    child.put("blah", new Object());
    map.put(new Rule("Java"), child);

    assertThat(map.size()).isEqualTo(2);
    enforcer.dropEmptyMapEntries(map);
    assertThat(map.size()).isEqualTo(1);
  }

  @Test
  public void testMoveReferencesToNewRules() {

    String cwe = "CWE-945";
    String findbugs = "BH_BLAH_BLAH";
    String misra = "1.1.1";


    Rule oldRule = new Rule("");
    oldRule.getCwe().add(cwe);
    oldRule.getFindbugs().add(findbugs);
    List<String> misra4 = oldRule.getMisraC04();
    misra4.add(misra);
    misra4.add("1.2.3");
    misra4.add("1.2.1");

    Map<String, Object> oldRuleUpdates = new HashMap<>();

    Rule newRule = new Rule("");
    newRule.getCwe().add("CWE-345");
    newRule.getMisraC04().add(misra);
    Map<Rule, Map<String,Object>> newRules = new HashMap<>();
    newRules.put(newRule, new HashMap<String, Object>());

    assertThat(newRule.getCwe()).hasSize(1);
    assertThat(newRule.getFindbugs()).hasSize(0);
    assertThat(oldRule.getCwe()).hasSize(1);
    assertThat(oldRule.getFindbugs()).hasSize(1);

    enforcer.moveReferencesToNewRules(oldRule, oldRuleUpdates, newRules,
            SupportedCodingStandard.FINDBUGS.getCodingStandard());

    assertThat(newRule.getCwe()).hasSize(1);
    assertThat(newRule.getFindbugs()).hasSize(1);
    assertThat(oldRule.getCwe()).hasSize(1);
    assertThat(oldRule.getFindbugs()).hasSize(0);


    enforcer.moveReferencesToNewRules(oldRule, oldRuleUpdates, newRules,
            SupportedCodingStandard.CWE.getCodingStandard());

    assertThat(newRule.getCwe()).hasSize(2);
    assertThat(newRule.getFindbugs()).hasSize(1);
    assertThat(oldRule.getCwe()).hasSize(0);
    assertThat(oldRule.getFindbugs()).hasSize(0);


    assertThat(oldRule.getCert()).hasSize(0);
    assertThat(newRule.getCert()).hasSize(0);

    enforcer.moveReferencesToNewRules(oldRule, oldRuleUpdates, newRules,
            SupportedCodingStandard.CERT.getCodingStandard());

    assertThat(oldRule.getCert()).hasSize(0);
    assertThat(newRule.getCert()).hasSize(0);

    assertThat(oldRule.getMisraC04()).hasSize(3);
    assertThat(newRule.getMisraC04()).hasSize(1);

    enforcer.moveReferencesToNewRules(oldRule, oldRuleUpdates, newRules,
            SupportedCodingStandard.MISRA_C_2004.getCodingStandard());

    assertThat(oldRule.getMisraC04()).hasSize(0);
    assertThat(newRule.getMisraC04()).hasSize(3);

  }



  @Test
  public void testIsTagPresent() {

    Rule rule = new Rule("");
    List<String> tags = new ArrayList<>();
    tags.add("cwe");
    tags.add("yellow");
    rule.setTags(tags);

    assertThat(enforcer.isTagPresent(rule, new Cwe())).isTrue();
    assertThat(enforcer.isTagPresent(rule, new MisraC2004())).isFalse();
  }

  @Test
  public void testGetSpecificReferences() {

    Rule rule = new Rule("");
    rule.setReferences("<h2>See</h2>\n\n<ul>\n<li><a href=\"http://blah.com\">MITRE, CWE-123</a> - title</li>\n</ul>\n\n");

    assertThat(enforcer.getSpecificReferences(rule, "CWE")).hasSize(1).contains("MITRE, CWE-123 - title");
    assertThat(enforcer.getSpecificReferences(rule, "MISRA")).hasSize(0);
  }

  @Test
  public void testAddTagIfMissing() {

    Rule rule = new Rule("");
    rule.setTags(new ArrayList<String>());

    Map<String, Object> updates = new HashMap<>();

    enforcer.addTagIfMissing(rule, updates, "cwe");
    assertThat(rule.getTags()).hasSize(1).contains("cwe");
    assertThat(updates).hasSize(1);

    updates.clear();

    enforcer.addTagIfMissing(rule, updates, "cwe");
    assertThat(rule.getTags()).hasSize(1).contains("cwe");
    assertThat(updates).hasSize(0);

    updates.clear();
    rule.getTags().clear();
    rule.setStatus(Rule.Status.DEPRECATED);

    enforcer.addTagIfMissing(rule, updates, "cwe");
    assertThat(rule.getTags()).isEmpty();
    assertThat(updates).isEmpty();

  }

  @Test
  public void testAddSeeToReferenceField() {

    String fieldName = "CWE";

    Map<String, Object> updates = new HashMap<>();

    List<String> sees = new ArrayList<>();
    sees.add("CWE-1");
    sees.add("CWE-2");
    sees.add("CWE-3");

    List<String> referenceField = new ArrayList<>();
    referenceField.add("CWE-1");

    enforcer.addSeeToReferenceField(sees, referenceField, fieldName, updates);
    List<String> updateValue = (List<String>) updates.get(fieldName);

    assertThat(updates).hasSize(1);
    assertThat(updateValue).hasSize(3).contains("CWE-1").contains("CWE-2").contains("CWE-3");
    assertThat(updateValue).isEqualTo(referenceField);
  }

  @Test
  public void testGetUpdates(){
    Rule rule = new Rule("");
    rule.getTags().add("cwe");
    rule.getTags().add("owaspa3");

    Map<String,Object> updates = enforcer.getUpdates(rule, new Cwe());

    assertThat(updates).hasSize(0);
    assertThat(rule.getTags()).hasSize(2);

    updates = enforcer.getUpdates(rule, OwaspTopTen.StandardRule.A3);

    assertThat(updates).hasSize(0);
    assertThat(rule.getTags()).hasSize(2);

    updates = enforcer.getUpdates(rule, OwaspTopTen.StandardRule.A6);

    assertThat(updates).hasSize(0);
    assertThat(rule.getTags()).hasSize(2);

    updates = enforcer.getUpdates(rule, new Cert());

    assertThat(updates).hasSize(0);
    assertThat(rule.getTags()).hasSize(2);

  }

  @Test
  public void testGetCweUpdates1() {

    Rule rule = new Rule("");
    String refs = "<li><a href='http://blah.com'>MITRE, CWE-123</a> - blah</li>\n" +
            "<li><a href='bleh.com'>MITRE, CWE-456</a> - bleh</li>";
    rule.setReferences(refs);

    List<String> cweField = new ArrayList<>();
    cweField.add("789");
    rule.setCwe(cweField);

    Map<String,Object> updates = enforcer.getUpdates(rule, new Cwe());

    Map<String, Object> expectedUpdates = new HashMap<>();
    List<String> tmp = new ArrayList<>();
    tmp.add("CWE-123");
    tmp.add("CWE-456");
    tmp.add("CWE-789");
    expectedUpdates.put("CWE", tmp);

    tmp = new ArrayList<>();
    tmp.add("cwe");

    expectedUpdates.put("Labels", tmp);

    Collections.sort((List<String>) updates.get("CWE"));

    assertThat(updates).hasSize(2).isEqualTo(expectedUpdates);
  }

  @Test
  public void testGetCweUpdates2() {

    Rule rule = new Rule("");
    boolean tagPresent = true;
    List<String> references = new ArrayList<>();

    List<String> cweField = new ArrayList<>();
    rule.setCwe(cweField);

    Map<String,Object> updates = enforcer.getUpdates(rule, new Cwe());

    assertThat(updates).isEmpty();
  }

  @Test
  public void testGetCweUpdates3() {
    Rule rule = new Rule("");
    rule.getCwe().add("CWE-789");

    Map<String,Object> updates = enforcer.getUpdates(rule, new Cwe());

    Map<String, Object> expectedUpdates = new HashMap<>();

    List<String> tmp = new ArrayList<>();
    tmp.add("cwe");

    expectedUpdates.put("Labels", tmp);

    assertThat(updates).hasSize(1).isEqualTo(expectedUpdates);
  }

  @Test
  public void testGetCweUpdatesWithDerivativeTaggableStandard() {

    Rule rule = new Rule("");
    rule.getCwe().add("CWE-89");

    Map<String,Object> updates = enforcer.getUpdates(rule, SansTop25.Category.INSECURE_INTERACTION);

    Map<String, Object> expectedUpdates = new HashMap<>();

    List<String> tmp = new ArrayList<>();
    tmp.add("sans-top25-insecure");

    expectedUpdates.put("Labels", tmp);

    assertThat(updates).hasSize(1).isEqualTo(expectedUpdates);
  }

  @Test
  public void testAddMissingReference() {

    Rule rule = new Rule("");

    rule.setReferences("<h2>See</h2>\n" +
            "\n" +
            "<ul>\n" +
            "<li> <a href=\"https://www.owasp.org/index.php/Top_10_2013-A3-Cross-Site_Scripting_(XSS)\">OWASP Top Ten 2013 Category A3</a> - Cross Site Scripting (XSS)\n" +
            "</li><li> <a href=\"https://www.owasp.org/index.php/Top_10_2013-A6-Sensitive_Data_Exposure\">OWASP Top Ten 2013 Category A6</a> - Sensitive Data Exposure\n" +
            "</li><li> <a href=\"https://www.owasp.org/index.php/Top_10_2013-A8-Cross-Site_Request_Forgery_(CSRF)\">OWASP Top Ten 2013 Category A8</a> - Cross-Site Request Forgery (CSRF)\n" +
            "</li></ul>\n");

    List<String> expectedReferenceUpdate = new ArrayList<>();
    expectedReferenceUpdate.add("A3");
    expectedReferenceUpdate.add("A6");
    expectedReferenceUpdate.add("A8");

    List<String> expectedLabelUpdate = new ArrayList<>();
    expectedLabelUpdate.add("owasp-a3");

    Map<String,Object> updates = enforcer.getUpdates(rule, OwaspTopTen.StandardRule.A3);
    assertThat(updates).isNotEmpty();
    assertThat(updates.get("Labels")).isEqualTo(expectedLabelUpdate);
    assertThat(updates.get("OWASP")).isEqualTo(expectedReferenceUpdate);

  }

  @Test
  public void testDropCovered() {

    String language = "Yellow";

    Rule rule = new Rule("");
    rule.getCoveredLanguages().add(language);
    Map<String, Rule> rspecRules = new HashMap<>();
    rspecRules.put("key", rule);

    Map<String, Rule> needsUpdating = new HashMap<>();

    enforcer.dropCoveredForNonNemoRules(language, rspecRules, needsUpdating);

    assertThat(needsUpdating).hasSize(1);
    assertThat(rule.getCoveredLanguages()).isEmpty();
    assertThat(rule.getTargetedLanguages()).hasSize(1);
  }

  @Test
  public void testAddCovered() {
    String language = "Yellow";

    Rule rule = new Rule("");
    rule.getTargetedLanguages().add(language);

    Map<String, Rule> needsUpdating = new HashMap<>();

    enforcer.addCoveredForNemoRules(language,needsUpdating,rule);

    assertThat(rule.getTargetedLanguages()).isEmpty();
    assertThat(rule.getCoveredLanguages()).hasSize(1);
  }

  @Test
  public void testAddCovered2() {
    String language = "Yellow";

    Rule rule = new Rule("");
    rule.getCoveredLanguages().add(language);
    rule.getTargetedLanguages().add(language);

    Map<String, Rule> needsUpdating = new HashMap<>();

    enforcer.addCoveredForNemoRules(language,needsUpdating,rule);

    assertThat(rule.getTargetedLanguages()).isEmpty();
    assertThat(rule.getCoveredLanguages()).hasSize(1);
  }

  @Test
  public void testAddCovered3(){
    String language = "Yellow";

    Rule rule = new Rule("");
    Map<String, Rule> needsUpdating = new HashMap<>();

    enforcer.addCoveredForNemoRules(language,needsUpdating,rule);

    assertThat(rule.getTargetedLanguages()).isEmpty();
    assertThat(rule.getCoveredLanguages()).hasSize(1);

  }


  @Test
  public void testSeeAlsoReferencesIgnored() {

    Rule rule = new Rule("C");
    rule.setReferences("<h2>See</h2>\n" +
            "\n" +
            "<ul>\n" +
            "<li> MISRA C:2004, 20.3</li>\n" +
            "</ul>\n" +
            "<h3>See Also</h3>\n" +
            "\n" +
            "<ul>\n" +
            "<li> MISRA C:2004, 13.3</li>\n" +
            "<li> ISO/IEC 9899:1990</li>\n" +
            "</ul>\n");

    String authority = "MISRA C:2004,";
    List<String> specificReferences = enforcer.getSpecificReferences(rule, authority);

    assertThat(specificReferences).hasSize(1);
    assertThat(specificReferences).contains(" MISRA C:2004, 20.3");
  }

  @Test
  public void testGetDeprecationUpdates() {

    Rule rule = new Rule("");
    Map<String, Object> updates = enforcer.getDeprecationUpdates(rule);

    assertThat(updates).isEmpty();

    rule.getTargetedLanguages().add("Java");
    rule.getDefaultProfiles().add(new Profile("Sonar way"));
    rule.getTags().add("misra");

    updates = enforcer.getDeprecationUpdates(rule);
    assertThat(updates).hasSize(3);
    assertThat(rule.getTargetedLanguages()).isEmpty();
    assertThat(rule.getDefaultProfiles()).isEmpty();
    assertThat(rule.getTags()).isEmpty();
  }

}
