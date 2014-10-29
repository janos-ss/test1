/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import com.atlassian.jira.rest.client.domain.BasicStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Rule {

  private String language = null;
  private String key = null;
  private String status = null;

  private String severity = null;
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

    String subTitle = subRule.title.replaceFirst(language, "").trim();
    if (subTitle.length() > 0) {
      if (subTitle.startsWith(": ") || subTitle.startsWith("- ")) {
        subTitle = subTitle.substring(2);
      }
      if (subTitle.length() > 0) {
        this.title = subTitle;
      }
    }

    if (subRule.defaultActive != null) {
      this.defaultActive = subRule.defaultActive;
    }
    if (subRule.severity != null) {
      this.severity = subRule.severity;
    }
    if (subRule.message != null) {
      this.message = subRule.message;
    }
    if (subRule.parameterList != null && ! subRule.parameterList.isEmpty()) {
      this.parameterList = subRule.parameterList;
    }
    mergeDescriptionPieces(subRule);
  }

  private void mergeDescriptionPieces(Rule subRule) {
    if (subRule.description != null && subRule.description.length() > 0) {
      this.description = subRule.description;
    }
    if (subRule.nonCompliant != null && subRule.nonCompliant.length() > 0) {
      this.nonCompliant = subRule.nonCompliant;
    }
    if (subRule.compliant != null && subRule.compliant.length() > 0) {
      this.compliant = subRule.compliant;
    }
    if (subRule.exceptions != null && subRule.exceptions.length() > 0) {
      this.exceptions = subRule.exceptions;
    }
    if (subRule.references != null && subRule.references.length() > 0) {
      this.references = subRule.references;
    }
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

    if (!compliant.equals(rule.compliant)) {
      return false;
    }
    if (!defaultActive.equals(rule.defaultActive)) {
      return false;
    }
    if (!description.equals(rule.description)) {
      return false;
    }
    if (!exceptions.equals(rule.exceptions)) {
      return false;
    }
    if (!Arrays.equals(legacyKeys, rule.legacyKeys)) {
      return false;
    }
    if (!message.equals(rule.message)) {
      return false;
    }
    if (!nonCompliant.equals(rule.nonCompliant)) {
      return false;
    }
    if (parameterList != null ? !parameterList.equals(rule.parameterList) : rule.parameterList != null) {
      return false;
    }
    if (!references.equals(rule.references)) {
      return false;
    }
    if (severity.equals(rule.severity)) {
      return false;
    }
    if (!sqaleCharac.equals(rule.sqaleCharac)) {
      return false;
    }
    if (!sqaleCost.equals(rule.sqaleCost)) {
      return false;
    }
    if (!sqaleRemediation.equals(rule.sqaleRemediation)) {
      return false;
    }
    if (tags != null ? !tags.equals(rule.tags) : rule.tags != null) {
      return false;
    }
    if (!title.equals(rule.title)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = defaultActive.hashCode();
    result = 31 * result + title.hashCode();
    result = 31 * result + message.hashCode();
    result = 31 * result + description.hashCode();
    result = 31 * result + nonCompliant.hashCode();
    result = 31 * result + compliant.hashCode();
    result = 31 * result + exceptions.hashCode();
    result = 31 * result + references.hashCode();
    result = 31 * result + sqaleCharac.hashCode();
    result = 31 * result + sqaleRemediation.hashCode();
    result = 31 * result + sqaleCost.hashCode();
    result = 31 * result + severity.hashCode();
    result = 31 * result + (parameterList != null ? parameterList.hashCode() : 0);
    result = 31 * result + (tags != null ? tags.hashCode() : 0);
    result = 31 * result + (legacyKeys != null ? Arrays.hashCode(legacyKeys) : 0);
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

  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
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
