/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.specifications.*;
import org.junit.Test;

import java.util.*;

import static org.fest.assertions.Assertions.assertThat;


public class IntegrityEnforcementServiceTest {

  IntegrityEnforcementService enforcer = new IntegrityEnforcementService();


  @Test
  public void testIsTagPresent() {

    Rule rule = new Rule("");
    List<String> tags = new ArrayList<String>();
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

    Map<String, Object> updates = new HashMap<String, Object>();

    enforcer.addTagIfMissing(rule, updates, "cwe");
    assertThat(rule.getTags()).hasSize(1).contains("cwe");
    assertThat(updates).hasSize(1);

    enforcer.addTagIfMissing(rule, updates, "cwe");
    assertThat(rule.getTags()).hasSize(1).contains("cwe");
    assertThat(updates).hasSize(1);
  }

  @Test
  public void testAddSeeToReferenceField() {

    String fieldName = "CWE";

    Map<String, Object> updates = new HashMap<String, Object>();

    List<String> sees = new ArrayList<String>();
    sees.add("CWE-1");
    sees.add("CWE-2");
    sees.add("CWE-3");

    List<String> referenceField = new ArrayList<String>();
    referenceField.add("CWE-1");

    enforcer.addSeeToReferenceField(sees, referenceField, fieldName, updates);
    List<String> updateValue = (List<String>) updates.get(fieldName);

    assertThat(updates).hasSize(1);
    assertThat(updateValue).hasSize(3).contains("CWE-1").contains("CWE-2").contains("CWE-3");
    assertThat(updateValue).isEqualTo(referenceField);
  }

  @Test
  public void testGetCweUpdates1() {

    Rule rule = new Rule("");
    String refs = "<li><a href='http://blah.com'>MITRE, CWE-123</a> - blah</li>\n" +
            "<li><a href='bleh.com'>MITRE, CWE-456</a> - bleh</li>";
    rule.setReferences(refs);

    List<String> cweField = new ArrayList<String>();
    cweField.add("789");
    rule.setCwe(cweField);

    Map<String,Object> updates = enforcer.getUpdates(rule, new Cwe());

    Map<String, Object> expectedUpdates = new HashMap<String, Object>();
    List<String> tmp = new ArrayList<String>();
    tmp.add("CWE-123");
    tmp.add("CWE-456");
    tmp.add("CWE-789");
    expectedUpdates.put("CWE", tmp);

    tmp = new ArrayList<String>();
    tmp.add("cwe");

    expectedUpdates.put("Labels", tmp);

    Collections.sort((List<String>) updates.get("CWE"));

    assertThat(updates).hasSize(2).isEqualTo(expectedUpdates);
  }

  @Test
  public void testGetCweUpdates2() {

    Rule rule = new Rule("");
    boolean tagPresent = true;
    List<String> references = new ArrayList<String>();

    List<String> cweField = new ArrayList<String>();
    rule.setCwe(cweField);

    Map<String,Object> updates = enforcer.getUpdates(rule, new Cwe());

    assertThat(updates).isEmpty();
  }

  @Test
  public void testGetCweUpdates3() {
    Rule rule = new Rule("");
    rule.getCwe().add("CWE-789");

    Map<String,Object> updates = enforcer.getUpdates(rule, new Cwe());

    Map<String, Object> expectedUpdates = new HashMap<String, Object>();

    List<String> tmp = new ArrayList<String>();
    tmp.add("cwe");

    expectedUpdates.put("Labels", tmp);

    assertThat(updates).hasSize(1).isEqualTo(expectedUpdates);
  }

  @Test
  public void testGetCweUpdatesWithDerivativeTaggableStandard() {

    Rule rule = new Rule("");
    rule.getCwe().add("CWE-89");

    Map<String,Object> updates = enforcer.getUpdates(rule, SansTop25.Category.INSECURE_INTERACTION);

    Map<String, Object> expectedUpdates = new HashMap<String, Object>();

    List<String> tmp = new ArrayList<String>();
    tmp.add("sans-top25-insecure");

    expectedUpdates.put("Labels", tmp);

    assertThat(updates).hasSize(1).isEqualTo(expectedUpdates);
  }


  @Test
  public void testDropCovered() {

    String language = "Yellow";

    Rule rule = new Rule("");
    rule.getCoveredLanguages().add(language);
    Map<String, Rule> rspecRules = new HashMap<String, Rule>();
    rspecRules.put("key", rule);

    Map<String, Rule> needsUpdating = new HashMap<String, Rule>();

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

    Map<String, Rule> needsUpdating = new HashMap<String, Rule>();

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

    Map<String, Rule> needsUpdating = new HashMap<String, Rule>();

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
}
