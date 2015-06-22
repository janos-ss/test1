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

  public enum Status {
    BETA, READY, DEPRECATED;

    public static Status fromString(String string) {
      for (Status s : Status.values()) {
        if (s.name().equalsIgnoreCase(string)) {
          return s;
        }
      }
      return Status.READY;
    }
  }

  public enum Severity {
    INFO, MINOR, MAJOR, CRITICAL, BLOCKER
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

  public enum Subcharacteristic {
    // Portability
    COMPILER_RELATED_PORTABILITY("Compiler related portability"),
    HARDWARE_RELATED_PORTABILITY("Hardware related portability"),
    LANGUAGE_RELATED_PORTABILITY("Language related portability"),
    OS_RELATED_PORTABILITY("OS related portability"),
    SOFTWARE_RELATED_PORTABILITY("Software related portability"),
    TIME_ZONE_RELATED_PORTABILITY("Time zone related portability"),

    // Maintainability
    READABILITY("Readability"),
    UNDERSTANDABILITY("Understandability"),

    // Security
    API_ABUSE("API abuse"),
    ERRORS("Errors"),
    INPUT_VALIDATION_AND_REPRESENTATION("Input validation and representation"),
    SECURITY_FEATURES("Security features"),

    // Efficiency
    CPU_EFFICIENCY("Processor use"),
    MEMORY_EFFICIENCY("Memory use"),
    NETWORK_USE("Network use"),

    // Changability
    ARCHITECTURE_CHANGEABILITY("Architecture related changeability"),
    DATA_CHANGEABILITY("Data related changeability"),
    LOGIC_CHANGEABILITY("Logic related changeability"),

    // Reliability
    ARCHITECTURE_RELIABILITY("Architecture related reliability"),
    DATA_RELIABILITY("Data related reliability"),
    EXCEPTION_HANDLING("Exception handling"),
    FAULT_TOLERANCE("Fault tolerance"),
    INSTRUCTION_RELIABILITY("Instruction related reliability"),
    LOGIC_RELIABILITY("Logic related reliability"),
    RESOURCE_RELIABILITY("Resource related reliability"),
    SYNCHRONIZATION_RELIABILITY("Synchronization related reliability"),
    UNIT_TESTS("Unit tests"),

    // Testability
    INTEGRATION_TESTABILITY("Integration level testability"),
    UNIT_TESTABILITY("Unit level testability");

    protected final String rspecName;

    Subcharacteristic(String rspecName) {
      this.rspecName = rspecName;
    }

    public String getRspecName() {
      return this.rspecName;
    }
  }

  public enum Profile {
    SONARQUBE("SonarQube Way"),
    SECURITY("Security Way"),
    DRUPAL("Drupal"),
    PSR2("PSR-2"),
    VS("Visual Studio");

    protected final String profileName;

    Profile(String profileName) {
      this.profileName = profileName;
    }

    public String getProfileName(){
      return profileName;
    }

    public static Profile fromString(String str){
      for (Profile p : Profile.values()){
        if (p.profileName.equalsIgnoreCase(str)) {
          return p;
        }
      }
      return null;
    }

    @Override
    public String toString(){
      return getProfileName();
    }
  }

  private String language = null;
  private String key = null;
  private String repo = null;
  private Status status = null;

  private List<String> deprecationLinks = new ArrayList<String>();

  private Severity severity = null;
  private boolean template = false;
  private List<String> legacyKeys = null;

  private List<Profile> defaultProfiles = new ArrayList<>();

  private String title = null;
  private String message = null;
  private String fullDescription = null;
  private String description = "";
  private String nonCompliant = "";
  private String compliant = "";
  private String exceptions = "";
  private String references = "";
  private String deprecation = "";

  private String sqaleCharac = null;
  private Subcharacteristic sqaleSubCharac = null;
  private RemediationFunction sqaleRemediationFunction = null;
  private String sqaleConstantCostOrLinearThreshold = null;
  private String sqaleLinearArgDesc = null;
  private String sqaleLinearFactor = null;
  private String sqaleLinearOffset = null;

  private List<Parameter> parameterList = new ArrayList<Parameter>();
  private List<String> tags = new ArrayList<String>();

  private List<String> targetedLanguages = new ArrayList<String>();
  private List<String> coveredLanguages = new ArrayList<String>();
  private List<String> irrelevantLanguages = new ArrayList<String>();

  private List<String> cwe = new ArrayList<String>();
  private List<String> cert = new ArrayList<String>();
  private List<String> cppCheck = new ArrayList<>();
  private List<String> misraC12 = new ArrayList<String>();
  private List<String> misraC04 = new ArrayList<String>();
  private List<String> misraCpp = new ArrayList<String>();
  private List<String> findbugs = new ArrayList<String>();
  private List<String> fbContrib = new ArrayList<>();
  private List<String> findSecBugs = new ArrayList<String>();
  private List<String> owasp = new ArrayList<String>();
  private List<String> pmd = new ArrayList<String>();
  private List<String> checkstyle = new ArrayList<String>();
  private List<String> phpFig = new ArrayList<String>();
  private List<String> resharper = new ArrayList<>();
  private List<String> pylint = new ArrayList<String>();

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
    mergeSqalePieces(subRule);
    mergeDefaultProfiles(subRule);

    tags.addAll(subRule.getTags());
  }

  protected void mergeDefaultProfiles(Rule subRule) {
    if (!subRule.getDefaultProfiles().isEmpty()) {
      this.setDefaultProfiles(new ArrayList<Profile>(subRule.getDefaultProfiles()));
    }
  }

  protected void mergeSqalePieces(Rule subRule) {
    if (!Strings.isNullOrEmpty(subRule.sqaleCharac)) {
      this.sqaleCharac = subRule.getSqaleCharac();
    }

    if (subRule.sqaleSubCharac != null) {
      this.sqaleSubCharac = subRule.sqaleSubCharac;
    }

    if (subRule.sqaleRemediationFunction != null) {
      this.sqaleRemediationFunction = subRule.sqaleRemediationFunction;
    }

    if (!Strings.isNullOrEmpty(subRule.sqaleConstantCostOrLinearThreshold)) {
      this.sqaleConstantCostOrLinearThreshold = subRule.sqaleConstantCostOrLinearThreshold;
    }

    if (!Strings.isNullOrEmpty(subRule.sqaleLinearArgDesc)) {
      this.sqaleLinearArgDesc = subRule.sqaleLinearArgDesc;
    }

    if (!Strings.isNullOrEmpty(subRule.sqaleLinearFactor)) {
      this.sqaleLinearFactor = subRule.sqaleLinearFactor;
    }

    if (!Strings.isNullOrEmpty(subRule.sqaleLinearOffset)) {
      this.sqaleLinearOffset = subRule.sqaleLinearOffset;
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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
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
   * @param fullDescription the full rule description
   */
  public void setFullDescription(String fullDescription) {
    this.fullDescription = fullDescription;
  }

  public RemediationFunction getSqaleRemediationFunction() {
    return sqaleRemediationFunction;
  }

  public void setSqaleRemediationFunction(RemediationFunction sqaleRemediationFunction) {
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

  public String getSqaleLinearArgDesc() {

    return sqaleLinearArgDesc;
  }

  public void setSqaleLinearArgDesc(String sqaleLinearArgDesc) {

    this.sqaleLinearArgDesc = sqaleLinearArgDesc;
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

  public Subcharacteristic getSqaleSubCharac() {

    return sqaleSubCharac;
  }

  public void setSqaleSubCharac(Subcharacteristic sqaleSubCharac) {

    this.sqaleSubCharac = sqaleSubCharac;
  }

  public void setTags(List<String> tags) {

    this.tags = tags;
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

  public List<String> getTargetedLanguages() {

    return targetedLanguages;
  }

  public void setTargetedLanguages(List<String> targetedLanguages) {

    this.targetedLanguages = targetedLanguages;
  }

  public List<String> getCoveredLanguages() {

    return coveredLanguages;
  }

  public void setCoveredLanguages(List<String> coveredLanguages) {

    this.coveredLanguages = coveredLanguages;
  }

  public List<String> getIrrelevantLanguages() {

    return irrelevantLanguages;
  }

  public void setIrrelevantLanguages(List<String> irrelevantLanguages) {

    this.irrelevantLanguages = irrelevantLanguages;
  }

  public List<String> getDeprecationLinks() {

    return deprecationLinks;
  }

  public void setDeprecationLinks(List<String> deprecationLinks) {

    this.deprecationLinks = deprecationLinks;
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

  public List<Profile> getDefaultProfiles() {

    return defaultProfiles;
  }

  public void setDefaultProfiles(List<Profile> defaultProfiles) {

    this.defaultProfiles = defaultProfiles;
  }

  public String getRepo() {

    return repo;
  }

  public void setRepo(String repo) {

    this.repo = repo;
  }
}
