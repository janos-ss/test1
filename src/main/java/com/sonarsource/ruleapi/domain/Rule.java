/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Rule {

  public enum Severity {
    INFO, MINOR, MAJOR, CRITICAL, BLOCKER
  }

  private String language = null;
  private String key = null;
  private String status = null;

  private Severity severity = null;
  private Boolean defaultActive = null;
  private boolean template = false;
  private List<String> legacyKeys = null;

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
  private String sqaleRemediationFunction = null;
  private String sqaleConstantCostOrLinearThreshold = null;
  private String sqaleLinearArg = null;
  private String sqaleLinearFactor = null;
  private String sqaleLinearOffset = null;

  private List<Parameter> parameterList = new ArrayList<Parameter>();
  private List<String> tags = new ArrayList<String>();

  private List<String> externalKeys;
  private List<String> externalRepositories;
  private List<String> findbugs;
  private List<String> pmd;
  private List<String> checkstyle;
  private List<String> misraC12;
  private List<String> misraC04;
  private List<String> misraCpp;
  private List<String> findSecBugs;
  private List<String> cert;
  private List<String> owasp;
  private List<String> phpFig;
  private List<String> cwe;


  public Rule(String language) {
    this.language = language;
  }

  public void merge(Rule subRule) {
    if (subRule.title == null) {
      return;
    }

    mergeTitle(subRule);

    if (subRule.message != null) {
      this.message = subRule.message;
    }

    if (subRule.defaultActive != null) {
      this.defaultActive = subRule.defaultActive;
    }
    if (subRule.severity != null) {
      this.severity = subRule.severity;
    }
    if (Strings.isNullOrEmpty(message)) {
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
    if (!Strings.isNullOrEmpty(subRule.description)) {
      this.description = subRule.description;
    }
    if (!Strings.isNullOrEmpty(subRule.nonCompliant)) {
      this.nonCompliant = subRule.nonCompliant;
    }
    if (!Strings.isNullOrEmpty(subRule.compliant)) {
      this.compliant = subRule.compliant;
    }
    if (!Strings.isNullOrEmpty(subRule.exceptions)) {
      this.exceptions = subRule.exceptions;
    }
    if (!Strings.isNullOrEmpty(subRule.references)) {
      this.references = subRule.references;
    }
  }

  public String getHtmlDescription() {
    return description + nonCompliant + compliant + exceptions + references;
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

  /**
   * Set the full rule description (description, noncompliant, compliant &etc.)
   *
   * For most purposes, the setDescription method in RuleMaker should be used instead.
   *
   * {@link com.sonarsource.ruleapi.RuleMaker#setDescription(Rule, String)}
   *
   * @param fullDescription the full rule description
   */
  public void setFullDescription(String fullDescription) {
    this.fullDescription = fullDescription;
  }

  public String getSqaleRemediationFunction() {
    return sqaleRemediationFunction;
  }

  public void setSqaleRemediationFunction(String sqaleRemediationFunction) {
    this.sqaleRemediationFunction = sqaleRemediationFunction;
  }

  public String getSqaleConstantCostOrLinearThreshold() {
    return sqaleConstantCostOrLinearThreshold;
  }

  public void setSqaleConstantCostOrLinearThreshold(String sqaleConstantCostOrLinearThreshold) {
    this.sqaleConstantCostOrLinearThreshold = sqaleConstantCostOrLinearThreshold;
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

  public List<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = new ArrayList<String>(tags);
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

  public List<String> getLegacyKeys() {
    return legacyKeys;
  }

  public void setLegacyKeys(List<String> legacyKeys) {
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

  public void setTags(List<String> tags) {

    this.tags = tags;
  }

  public List<String> getExternalKeys() {

    return externalKeys;
  }

  public void setExternalKeys(List<String> externalKeys) {

    this.externalKeys = externalKeys;
  }

  public List<String> getExternalRepositories() {

    return externalRepositories;
  }

  public void setExternalRepositories(List<String> externalRepositories) {

    this.externalRepositories = externalRepositories;
  }

  public List<String> getFindbugs() {

    return findbugs;
  }

  public void setFindbugs(List<String> findbugs) {

    this.findbugs = findbugs;
  }

  public List<String> getPmd() {

    return pmd;
  }

  public void setPmd(List<String> pmd) {

    this.pmd = pmd;
  }

  public List<String> getCheckstyle() {

    return checkstyle;
  }

  public void setCheckstyle(List<String> checkstyle) {

    this.checkstyle = checkstyle;
  }

  public List<String> getMisraC12() {

    return misraC12;
  }

  public void setMisraC12(List<String> misraC12) {

    this.misraC12 = misraC12;
  }

  public List<String> getMisraC04() {

    return misraC04;
  }

  public void setMisraC04(List<String> misraC04) {

    this.misraC04 = misraC04;
  }

  public List<String> getMisraCpp() {

    return misraCpp;
  }

  public void setMisraCpp(List<String> misraCpp) {

    this.misraCpp = misraCpp;
  }

  public List<String> getFindSecBugs() {

    return findSecBugs;
  }

  public void setFindSecBugs(List<String> findSecBugs) {

    this.findSecBugs = findSecBugs;
  }

  public List<String> getCert() {

    return cert;
  }

  public void setCert(List<String> cert) {

    this.cert = cert;
  }

  public List<String> getOwasp() {

    return owasp;
  }

  public void setOwasp(List<String> owasp) {

    this.owasp = owasp;
  }

  public List<String> getPhpFig() {

    return phpFig;
  }

  public void setPhpFig(List<String> phpFig) {

    this.phpFig = phpFig;
  }

  public List<String> getCwe() {

    return cwe;
  }

  public void setCwe(List<String> cwe) {

    this.cwe = cwe;
  }
}
