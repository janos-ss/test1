/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Rule {

  public enum Severity {
    Info, Minor, Major, Critical, Blocker
  }

  private String language = null;
  private String key = null;
  private String status = null;

  private Severity severity = null;
  private Boolean defaultActive = null;
  private boolean template = false;
  private String[] legacyKeys = null;

  private String title = null;
  private String message = null;
  private String fullDescription = null;
  private String description = "";
  private String nonCompliant = "";
  private String compliant = "";
  private String exceptions = "";
  private String references = "";
  private String sqaleCharac = null;
  private String sqaleSubCharac = null;
  private String sqaleRemediation = null;
  private String sqaleCost = null;
  private String sqaleLinearArg = null;
  private String sqaleLinearFactor = null;
  private String sqaleLinearOffset = null;

  private List<Parameter> parameterList = null;
  private Set<String> tags = null;

  public Rule(String language) {
    this.language = language;
  }

  public void merge(Rule subRule) {
    if (subRule.title == null) {
      return;
    }

    mergeTitle(subRule);

    if (subRule.defaultActive != null) {
      this.defaultActive = subRule.defaultActive;
    }
    if (subRule.severity != null) {
      this.severity = subRule.severity;
    }
    if (isNotEmpty(message)) {
      this.message = subRule.message;
    }
    if (subRule.parameterList != null && ! subRule.parameterList.isEmpty()) {
      this.parameterList = subRule.parameterList;
    }
    mergeDescriptionPieces(subRule);
  }

  private void mergeTitle(Rule subRule) {
    String subTitle = subRule.title.replaceFirst(language, "").trim();
    if (subTitle.length() > 0) {
      if (subTitle.startsWith(": ") || subTitle.startsWith("- ")) {
        subTitle = subTitle.substring(2);
      }
      if (subTitle.length() > 0) {
        this.title = subTitle;
      }
    }
  }

  private void mergeDescriptionPieces(Rule subRule) {
    if (isNotEmpty(subRule.description)) {
      this.description = subRule.description;
    }
    if (isNotEmpty(subRule.nonCompliant)) {
      this.nonCompliant = subRule.nonCompliant;
    }
    if (isNotEmpty(subRule.compliant)) {
      this.compliant = subRule.compliant;
    }
    if (isNotEmpty(subRule.exceptions)) {
      this.exceptions = subRule.exceptions;
    }
    if (isNotEmpty(subRule.references)) {
      this.references = subRule.references;
    }
  }

  private boolean isNotEmpty(String candidate) {
    return candidate != null && candidate.length() > 0;
  }

  public String getHtmlDescription() {
    return description + nonCompliant + compliant + exceptions + references;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Rule rule = (Rule) o;

    if (! isTextFunctionallyEquivalent(title,rule.title)) {
      return false;
    }
    if (! isTextFunctionallyEquivalent(description,rule.description)) {
      return false;
    }
    if (template != rule.template) {
      return false;
    }
    if (compliant != null ? !compliant.equals(rule.compliant) : rule.compliant != null) {
      return false;
    }
    if (!defaultActive.equals(rule.defaultActive)) {
      return false;
    }
    if (!isTextFunctionallyEquivalent(exceptions,rule.exceptions)) {
      return false;
    }
    if (nonCompliant != null ? !nonCompliant.equals(rule.nonCompliant) : rule.nonCompliant != null) {
      return false;
    }
    if (parameterList != null ? !parameterList.equals(rule.parameterList) : rule.parameterList != null) {
      return false;
    }
    if (references != null ? !references.equals(rule.references) : rule.references != null) {
      return false;
    }
    if (!severity.equals(rule.severity)) {
      return false;
    }
    if (!sqaleCharac.equals(rule.sqaleCharac)) {
      return false;
    }
    if (sqaleCost != null ? !sqaleCost.equals(rule.sqaleCost) : rule.sqaleCost != null) {
      return false;
    }
    if (sqaleLinearArg != null ? !sqaleLinearArg.equals(rule.sqaleLinearArg) : rule.sqaleLinearArg != null) {
      return false;
    }
    if (sqaleLinearFactor != null ? !sqaleLinearFactor.equals(rule.sqaleLinearFactor) : rule.sqaleLinearFactor != null) {
      return false;
    }
    if (sqaleLinearOffset != null ? !sqaleLinearOffset.equals(rule.sqaleLinearOffset) : rule.sqaleLinearOffset != null) {
      return false;
    }
    if (sqaleRemediation != null ? !sqaleRemediation.equals(rule.sqaleRemediation) : rule.sqaleRemediation != null) {
      return false;
    }
    if (!sqaleSubCharac.equals(rule.sqaleSubCharac)) {
      return false;
    }
    if (tags != null ? !tags.equals(rule.tags) : rule.tags != null) {
      return false;
    }

    return true;
  }

  protected boolean isTextFunctionallyEquivalent(String a, String b){
    if (a == null && b == null) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    if (a.equals(b)) {
      return true;
    }
    return hasEquivalentTokens(a, b);
  }

  private boolean hasEquivalentTokens(String a, String b) {
    if (a.contains("|") || b.contains("|")){
      String [] aTokens = a.split(" ");
      String [] bTokens = b.split(" ");

      for (int i=0; i<aTokens.length && i<bTokens.length; i++) {
        String aTok = aTokens[i];
        String bTok = bTokens[i];
        if (! (aTok.equals(bTok) || aTok.contains(bTok) || bTok.contains(aTok))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {

    int result = severity.hashCode();
    result = 31 * result + defaultActive.hashCode();
    result = 31 * result + (template ? 1 : 0);
    result = 31 * result + title.hashCode();
    result = 31 * result + description.hashCode();
    result = 31 * result + (nonCompliant != null ? nonCompliant.hashCode() : 0);
    result = 31 * result + (compliant != null ? compliant.hashCode() : 0);
    result = 31 * result + (exceptions != null ? exceptions.hashCode() : 0);
    result = 31 * result + (references != null ? references.hashCode() : 0);
    result = 31 * result + sqaleCharac.hashCode();
    result = 31 * result + sqaleSubCharac.hashCode();
    result = 31 * result + (sqaleRemediation != null ? sqaleRemediation.hashCode() : 0);
    result = 31 * result + (sqaleCost != null ? sqaleCost.hashCode() : 0);
    result = 31 * result + (sqaleLinearArg != null ? sqaleLinearArg.hashCode() : 0);
    result = 31 * result + (sqaleLinearFactor != null ? sqaleLinearFactor.hashCode() : 0);
    result = 31 * result + (sqaleLinearOffset != null ? sqaleLinearOffset.hashCode() : 0);
    result = 31 * result + (parameterList != null ? parameterList.hashCode() : 0);
    result = 31 * result + (tags != null ? tags.hashCode() : 0);
    return result;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getFullDescription() {
    return fullDescription;
  }

  public void setFullDescription(String fullDescription) {
    this.fullDescription = fullDescription;
  }

  public String getSqaleRemediation() {
    return sqaleRemediation;
  }

  public void setSqaleRemediation(String sqaleRemediation) {
    this.sqaleRemediation = sqaleRemediation;
  }

  public String getSqaleCost() {
    return sqaleCost;
  }

  public void setSqaleCost(String sqaleCost) {
    this.sqaleCost = sqaleCost;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<Parameter> getParameterList() {
    return parameterList;
  }

  public void setParameterList(List<Parameter> parameterList) {
    this.parameterList = parameterList;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
    this.severity = severity;
  }

  public Boolean getDefaultActive() {
    return defaultActive;
  }

  public void setDefaultActive(Boolean defaultActive) {
    this.defaultActive = defaultActive;
  }

  public String[] getLegacyKeys() {
    return legacyKeys;
  }

  public void setLegacyKeys(String[] legacyKeys) {
    this.legacyKeys = legacyKeys;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getNonCompliant() {
    return nonCompliant;
  }

  public void setNonCompliant(String nonCompliant) {
    this.nonCompliant = nonCompliant;
  }

  public String getCompliant() {
    return compliant;
  }

  public void setCompliant(String compliant) {
    this.compliant = compliant;
  }

  public String getExceptions() {
    return exceptions;
  }

  public void setExceptions(String exceptions) {
    this.exceptions = exceptions;
  }

  public String getReferences() {
    return references;
  }

  public void setReferences(String references) {
    this.references = references;
  }

  public String getSqaleCharac() {
    return sqaleCharac;
  }

  public void setSqaleCharac(String sqaleCharac) {
    this.sqaleCharac = sqaleCharac;
  }

  public String getLanguage() {

    return language;
  }

  public void setLanguage(String language) {

    this.language = language;
  }

  public String getSqaleLinearArg() {

    return sqaleLinearArg;
  }

  public void setSqaleLinearArg(String sqaleLinearArg) {

    this.sqaleLinearArg = sqaleLinearArg;
  }

  public String getSqaleLinearFactor() {

    return sqaleLinearFactor;
  }

  public void setSqaleLinearFactor(String sqaleLinearFactor) {

    this.sqaleLinearFactor = sqaleLinearFactor;
  }

  public String getSqaleLinearOffset() {

    return sqaleLinearOffset;
  }

  public void setSqaleLinearOffset(String sqaleLinearOffset) {

    this.sqaleLinearOffset = sqaleLinearOffset;
  }

  public boolean isTemplate() {

    return template;
  }

  public void setTemplate(boolean isTemplate) {

    this.template = isTemplate;
  }

  public String getSqaleSubCharac() {

    return sqaleSubCharac;
  }

  public void setSqaleSubCharac(String sqaleSubCharac) {

    this.sqaleSubCharac = sqaleSubCharac;
  }
}
