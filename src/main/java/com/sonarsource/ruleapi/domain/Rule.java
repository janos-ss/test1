/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Rule {

  public enum Status {
    BETA("beta"), READY("ready"), DEPRECATED("deprecated"), SUPERSEDED("superseded");

    protected final String statusName;

    Status(String statusName) {
      this.statusName = statusName;
    }

    public static Status fromString(String string) {
      for (Status s : Status.values()) {
        if (s.name().equalsIgnoreCase(string)) {
          return s;
        }
      }
      return Status.READY;
    }

    public String getStatusName() {
      return this.statusName;
    }
  }

  public enum Severity {
    BLOCKER("Blocker"), CRITICAL("Critical"), MAJOR("Major"), MINOR("Minor"), INFO("Info");

    protected final String severityName;

    Severity(String severityName) {
      this.severityName = severityName;
    }

    public String getSeverityName() {
      return this.severityName;
    }
  }

  public enum RemediationFunction {
    CONSTANT_ISSUE("Constant/Issue"),
    LINEAR("Linear"),
    LINEAR_OFFSET("Linear with offset");

    protected final String functionName;

    RemediationFunction(String functionName) {
      this.functionName = functionName;
    }

    public String getFunctionName() {
      return functionName;
    }
  }

  public enum Type {
    BUG("Bug"),
    VULNERABILITY("Vulnerability"),
    CODE_SMELL("Code Smell");

    protected final String typeName;

    Type(String typeName) {
      this.typeName = typeName;
    }

    @Override
    public String toString() {
      return this.typeName;
    }

    public static Type fromString(String value){
      if (Strings.isNullOrEmpty(value)) {
        return Type.CODE_SMELL;
      }

      for (Type type : Type.values()) {
        if (type.name().equals(value) || value.startsWith(type.typeName)) {
          return type;
        }
      }
      return Type.CODE_SMELL;
    }
  }


  private final String language;
  private String key = null;
  private String lookupKey = null;
  private String repo = null;
  private Status status = null;
  private Type type = Type.CODE_SMELL;

  private List<String> replacementLinks = new ArrayList<>();

  private Severity severity = null;
  private boolean template = false;
  private List<String> legacyKeys = new ArrayList<>();

  private Set<Profile> defaultProfiles = new HashSet<>();

  private String title = null;
  private String message = null;
  private String fullDescription = null;
  private String description = "";
  private String nonCompliant = "";
  private String compliant = "";
  private String exceptions = "";
  private String references = "";
  private String deprecation = "";

  private RemediationFunction remediationFunction = null;
  private String constantCostOrLinearThreshold = null;
  private String linearArgDesc = null;
  private String linearFactor = null;
  private String linearOffset = null;

  private List<Parameter> parameterList = new ArrayList<>();
  private Set<String> tags = new HashSet<>();

  private Set<String> targetedLanguages = new HashSet<>();
  private Set<String> coveredLanguages = new HashSet<>();
  private Set<String> irrelevantLanguages = new HashSet<>();

  private List<String> cwe = new ArrayList<>();
  private List<String> cert = new ArrayList<>();
  private List<String> esLint = new ArrayList<>();
  private List<String> cppCheck = new ArrayList<>();
  private List<String> misraC12 = new ArrayList<>();
  private List<String> misraC04 = new ArrayList<>();
  private List<String> misraCpp = new ArrayList<>();
  private List<String> findbugs = new ArrayList<>();
  private List<String> fbContrib = new ArrayList<>();
  private List<String> findSecBugs = new ArrayList<>();
  private List<String> owasp = new ArrayList<>();
  private List<String> pmd = new ArrayList<>();
  private List<String> checkstyle = new ArrayList<>();
  private List<String> phpFig = new ArrayList<>();
  private List<String> resharper = new ArrayList<>();
  private List<String> pylint = new ArrayList<>();
  private List<String> fxCop = new ArrayList<>();
  private List<String> pcLint = new ArrayList<>();
  private List<String> msftRoslyn = new ArrayList<>();

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

    if (subRule.severity != null) {
      this.severity = subRule.severity;
    }
    if (Strings.isNullOrEmpty(message)) {
      this.message = subRule.message;
    }
    if (subRule.parameterList != null && !subRule.parameterList.isEmpty()) {
      this.parameterList = subRule.parameterList;
    }
    mergeDescriptionPieces(subRule);
    mergeRemediationPieces(subRule);
    mergeDefaultProfiles(subRule);

    tags.addAll(subRule.getTags());
  }

  protected void mergeDefaultProfiles(Rule subRule) {
    Set<Profile> subProfiles = subRule.getDefaultProfiles();
    if (!subProfiles.isEmpty()) {
      for (Profile p : subProfiles) {
        if ("override none".equals(p.getLowerCaseName())) {
          this.getDefaultProfiles().clear();
          return;
        }
      }

      this.setDefaultProfiles(new HashSet<>(subProfiles));
    }
  }

  protected void mergeRemediationPieces(Rule subRule) {

    if (subRule.remediationFunction != null) {
      this.remediationFunction = subRule.remediationFunction;
    }

    if (!Strings.isNullOrEmpty(subRule.constantCostOrLinearThreshold)) {
      this.constantCostOrLinearThreshold = subRule.constantCostOrLinearThreshold;
    }

    if (!Strings.isNullOrEmpty(subRule.linearArgDesc)) {
      this.linearArgDesc = subRule.linearArgDesc;
    }

    if (!Strings.isNullOrEmpty(subRule.linearFactor)) {
      this.linearFactor = subRule.linearFactor;
    }

    if (!Strings.isNullOrEmpty(subRule.linearOffset)) {
      this.linearOffset = subRule.linearOffset;
    }
  }

  protected void mergeTitle(Rule subRule) {

    String subTitle = subRule.title;
    int pos = subTitle.indexOf(' ');
    if (pos == -1) {
      return;
    }

    subTitle = subTitle.substring(pos).trim();
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
    return description + nonCompliant + compliant + exceptions + references + deprecation;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getLookupKey() {
    return lookupKey;
  }

  public void setLookupKey(String lookupKey) {
    this.lookupKey = lookupKey;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Type getType() {

    return type;
  }

  public void setType(Type type) {

    this.type = type;
  }

  public String getFullDescription() {
    return fullDescription;
  }

  /**
   * Set the full rule description (description, noncompliant, compliant &etc.)
   *
   * For most purposes, the setDescription method in RuleMaker should be used instead.
   *
   * @param fullDescription the full rule description
   */
  public void setFullDescription(String fullDescription) {
    this.fullDescription = fullDescription;
  }

  public RemediationFunction getRemediationFunction() {
    return remediationFunction;
  }

  public void setRemediationFunction(RemediationFunction remediationFunction) {
    this.remediationFunction = remediationFunction;
  }

  public String getConstantCostOrLinearThreshold() {
    return constantCostOrLinearThreshold;
  }

  public void setConstantCostOrLinearThreshold(String constantCostOrLinearThreshold) {
    this.constantCostOrLinearThreshold = constantCostOrLinearThreshold;
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
    this.tags = new HashSet<>(tags);
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
    this.severity = severity;
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

  public String getLanguage() {
    return language;
  }

  public String getLinearArgDesc() {

    return linearArgDesc;
  }

  public void setLinearArgDesc(String linearArgDesc) {

    this.linearArgDesc = linearArgDesc;
  }

  public String getLinearFactor() {

    return linearFactor;
  }

  public void setLinearFactor(String linearFactor) {

    this.linearFactor = linearFactor;
  }

  public String getLinearOffset() {

    return linearOffset;
  }

  public void setLinearOffset(String linearOffset) {

    this.linearOffset = linearOffset;
  }

  public boolean isTemplate() {

    return template;
  }

  public void setTemplate(boolean isTemplate) {

    this.template = isTemplate;
  }

  public void setTags(List<String> tags) {

    this.tags = new HashSet<>(tags);
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

  public Set<String> getTargetedLanguages() {

    return targetedLanguages;
  }

  public void setTargetedLanguages(Set<String> targetedLanguages) {

    this.targetedLanguages = targetedLanguages;
  }

  public Set<String> getCoveredLanguages() {

    return coveredLanguages;
  }

  public void setCoveredLanguages(Set<String> coveredLanguages) {

    this.coveredLanguages = coveredLanguages;
  }

  public Set<String> getIrrelevantLanguages() {

    return irrelevantLanguages;
  }

  public void setIrrelevantLanguages(Set<String> irrelevantLanguages) {

    this.irrelevantLanguages = irrelevantLanguages;
  }

  public List<String> getReplacementLinks() {

    return replacementLinks;
  }

  public void setReplacementLinks(List<String> replacementLinks) {

    this.replacementLinks = replacementLinks;
  }

  public List<String> getPylint() {

    return pylint;
  }

  public void setPylint(List<String> pylint) {

    this.pylint = pylint;
  }

  public String getDeprecation() {

    return deprecation;
  }

  public void setDeprecation(String deprecation) {

    this.deprecation = deprecation;
  }

  public List<String> getFbContrib() {

    return fbContrib;
  }

  public void setFbContrib(List<String> fbContrib) {

    this.fbContrib = fbContrib;
  }

  public List<String> getResharper() {

    return resharper;
  }

  public void setResharper(List<String> resharper) {

    this.resharper = resharper;
  }

  public List<String> getCppCheck() {

    return cppCheck;
  }

  public void setCppCheck(List<String> cppCheck) {

    this.cppCheck = cppCheck;
  }

  public Set<Profile> getDefaultProfiles() {

    return defaultProfiles;
  }

  public void setDefaultProfiles(Set<Profile> defaultProfiles) {

    this.defaultProfiles = defaultProfiles;
  }

  public String getRepo() {

    return repo;
  }

  public void setRepo(String repo) {

    this.repo = repo;
  }

  public void setFxCop(List<String> ids) {
    fxCop = ids;
  }

  public List<String> getFxCop() {
    return fxCop;
  }

  public List<String> getPcLint() {

    return pcLint;
  }

  public void setPcLint(List<String> pcLint) {

    this.pcLint = pcLint;
  }

  public List<String> getMsftRoslyn() {

    return msftRoslyn;
  }

  public void setMsftRoslyn(List<String> msftRoslyn) {

    this.msftRoslyn = msftRoslyn;
  }

  public List<String> getEsLint() {
    return esLint;
  }

  public void setEsLint(List<String> esLint) {
    this.esLint = esLint;
  }

}
