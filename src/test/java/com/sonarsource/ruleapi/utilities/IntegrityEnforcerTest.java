/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import java.util.*;

import static org.fest.assertions.Assertions.assertThat;


public class IntegrityEnforcerTest {

  IntegrityEnforcer enforcer = new IntegrityEnforcer();

  @Test
  public void testStripHtml() {

    String html = "<ul><li><a href=\"blah.com\">blah</a></li></ul>";
    assertThat(enforcer.stripHtml(html)).isEqualTo("blah");
  }

  @Test
  public void testIsTagPresent() {

    Rule rule = new Rule("");
    List<String> tags = new ArrayList<String>();
    tags.add("cwe");
    tags.add("yellow");
    rule.setTags(tags);

    assertThat(enforcer.isTagPresent(rule, "cwe")).isTrue();
    assertThat(enforcer.isTagPresent(rule, "misra")).isFalse();
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
  public void testParseCweFromSeeSection() {

    List<String> references = new ArrayList<String>();
    references.add("MITRE, CWE-123 - title");
    references.add("MITRE, 404 - RAH!");

    List<String> refs = enforcer.parseCweFromSeeSection(references);

    assertThat(refs).hasSize(1).contains("CWE-123");
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
  public void testIsCweEntryFormatValid() {

    Rule rule = new Rule("");
    rule.setKey("test");

    List<String> references = new ArrayList<String>();
    references.add("CWE-123");
    references.add("456");

    Map<String, Object> updates = new HashMap<String, Object>();

    enforcer.isCweFieldEntryFormatValid(references, updates, rule);
    List<String> ups = (List<String>) updates.get("CWE");

    assertThat(updates).hasSize(1);
    assertThat(rule.getCwe()).hasSize(2).contains("CWE-123").contains("CWE-456");
    assertThat(rule.getCwe()).isEqualTo(ups);
  }

  @Test
  public void testIsCweEntryFormatValidNot() {

    Rule rule = new Rule("");
    rule.setKey("test");

    List<String> references = new ArrayList<String>();
    references.add("CWE123");
    references.add("456");

    Map<String, Object> updates = new HashMap<String, Object>();

    enforcer.isCweFieldEntryFormatValid(references, updates, rule);
    List<String> ups = (List<String>) updates.get("CWE");

    assertThat(updates).hasSize(0);
  }

  @Test
  public void testGetCweUpdates1() {

    Rule rule = new Rule("");
    boolean tagPresent = false;
    List<String> references = new ArrayList<String>();
    references.add("CWE-123");
    references.add("CWE-456");

    List<String> cweField = new ArrayList<String>();
    cweField.add("789");

    Map<String,Object> updates = enforcer.getCweUpdates(rule, tagPresent, references, cweField);

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

    Map<String,Object> updates = enforcer.getCweUpdates(rule, tagPresent, references, cweField);

    assertThat(updates).isEmpty();
  }

  @Test
  public void testGetCweUpdates3() {
    Rule rule = new Rule("");
    boolean tagPresent = false;
    List<String> references = new ArrayList<String>();
    List<String> cweField = new ArrayList<String>();
    cweField.add("CWE-789");

    Map<String,Object> updates = enforcer.getCweUpdates(rule, tagPresent, references, cweField);

    Map<String, Object> expectedUpdates = new HashMap<String, Object>();

    List<String> tmp = new ArrayList<String>();
    tmp.add("cwe");

    expectedUpdates.put("Labels", tmp);

    assertThat(updates).hasSize(1).isEqualTo(expectedUpdates);

  }

  @Test
  public void testIsKeyNormal () {
    assertThat(enforcer.isKeyNormal("blue")).isFalse();
    assertThat(enforcer.isKeyNormal("RSPEC-1")).isTrue();
  }

  @Test
  public void testDropCovered() {

    String language = "Yellow";

    Rule rule = new Rule("");
    rule.getCoveredLanguages().add(language);
    rule.getOutdatedLanguages().add(language);
    Map<String, Rule> rspecRules = new HashMap<String, Rule>();
    rspecRules.put("key", rule);

    Map<String, Rule> needsUpdating = new HashMap<String, Rule>();

    enforcer.dropCoveredForNonNemoRules(language, rspecRules, needsUpdating);

    assertThat(needsUpdating).hasSize(1);
    assertThat(rule.getCoveredLanguages()).isEmpty();
    assertThat(rule.getOutdatedLanguages()).isEmpty();
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
  public void testLanguagesEnum() {

    assertThat(IntegrityEnforcer.Language.values().length).isEqualTo(17);
    assertThat(IntegrityEnforcer.Language.ABAP.sq).isEqualTo("abap");
    assertThat(IntegrityEnforcer.Language.ABAP.rspec).isEqualTo("ABAP");
    assertThat(IntegrityEnforcer.Language.ABAP.update).isTrue();
  }

}
