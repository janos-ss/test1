/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.misra;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.AbstractMisraSpecification;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRequirableRule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.List;


public class MisraCPP2008 extends AbstractMisraSpecification {

  private static final String NAME = "MISRA C++ 2008";
  private static final String SEE_SECTION_SEARCH_STRING = "MISRA C++:2008,";
  private static final String REFERENCE_PATTERN = "\\d\\d?-\\d\\d?-\\d\\d?";

  private Language language = Language.CPP;

  public enum StandardRule implements CodingStandardRequirableRule {

    MISRACPP2008_0_1_1 ("0-1-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_0_1_2 ("0-1-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_0_1_3 ("0-1-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_0_1_4 ("0-1-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_0_1_5 ("0-1-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_0_1_6 ("0-1-6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_0_1_7 ("0-1-7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_0_1_8 ("0-1-8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_0_1_9 ("0-1-9", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_0_1_10 ("0-1-10", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_0_1_11 ("0-1-11", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_0_1_12 ("0-1-12", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_0_2_1 ("0-2-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_0_3_1 ("0-3-1", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    MISRACPP2008_0_3_2 ("0-3-2", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),

    MISRACPP2008_0_4_1 ("0-4-1", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    MISRACPP2008_0_4_2 ("0-4-2", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    MISRACPP2008_0_4_3 ("0-4-3", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),

    MISRACPP2008_1_0_1 ("1-0-1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    MISRACPP2008_1_0_2 ("1-0-2", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    MISRACPP2008_1_0_3 ("1-0-3", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),

    MISRACPP2008_2_2_1 ("2-2-1", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),

    MISRACPP2008_2_3_1 ("2-3-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_2_5_1 ("2-5-1", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_2_7_1 ("2-7-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_2_7_2 ("2-7-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_2_7_3 ("2-7-3", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_2_10_1 ("2-10-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_2_10_2 ("2-10-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_2_10_3 ("2-10-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_2_10_4 ("2-10-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_2_10_5 ("2-10-5", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_2_10_6 ("2-10-6", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_2_13_1 ("2-13-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_2_13_2 ("2-13-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_2_13_3 ("2-13-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_2_13_4 ("2-13-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_2_13_5 ("2-13-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_3_1_1 ("3-1-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_3_1_2 ("3-1-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_3_1_3 ("3-1-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_3_2_1 ("3-2-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_3_2_2 ("3-2-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_3_2_3 ("3-2-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_3_2_4 ("3-2-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_3_3_1 ("3-3-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_3_3_2 ("3-3-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_3_4_1 ("3-4-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_3_9_1 ("3-9-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_3_9_2 ("3-9-2", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_3_9_3 ("3-9-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_4_5_1 ("4-5-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_4_5_2 ("4-5-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_4_5_3 ("4-5-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_4_10_1 ("4-10-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_4_10_2 ("4-10-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_5_0_1 ("5-0-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_2 ("5-0-2", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_3 ("5-0-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_4 ("5-0-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_5 ("5-0-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_6 ("5-0-6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_7 ("5-0-7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_8 ("5-0-8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_9 ("5-0-9", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_10 ("5-0-10", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_11 ("5-0-11", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_12 ("5-0-12", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_13 ("5-0-13", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_14 ("5-0-14", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_15 ("5-0-15", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_16 ("5-0-16", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_17 ("5-0-17", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_18 ("5-0-18", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_19 ("5-0-19", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_20 ("5-0-20", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_0_21 ("5-0-21", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_5_2_1 ("5-2-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_2_2 ("5-2-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_2_3 ("5-2-3", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_2_4 ("5-2-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_2_5 ("5-2-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_2_6 ("5-2-6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_2_7 ("5-2-7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_2_8 ("5-2-8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_2_9 ("5-2-9", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_2_10 ("5-2-10", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_2_11 ("5-2-11", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_2_12 ("5-2-12", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_5_3_1 ("5-3-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_3_2 ("5-3-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_3_3 ("5-3-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_5_3_4 ("5-3-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_5_8_1 ("5-8-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_5_14_1 ("5-14-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_5_17_1 ("5-17-1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),

    MISRACPP2008_5_18_1 ("5-18-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_5_19_1 ("5-19-1", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_6_2_1 ("6-2-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_2_2 ("6-2-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_2_3 ("6-2-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_6_3_1 ("6-3-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_6_4_1 ("6-4-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_4_2 ("6-4-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_4_3 ("6-4-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_4_4 ("6-4-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_4_5 ("6-4-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_4_6 ("6-4-6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_4_7 ("6-4-7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_4_8 ("6-4-8", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_6_5_1 ("6-5-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_5_2 ("6-5-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_5_3 ("6-5-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_5_4 ("6-5-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_5_5 ("6-5-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_5_6 ("6-5-6", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_6_6_1 ("6-6-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_6_2 ("6-6-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_6_3 ("6-6-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_6_4 ("6-6-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_6_6_5 ("6-6-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_7_1_1 ("7-1-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_7_1_2 ("7-1-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_7_2_1 ("7-2-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_7_3_1 ("7-3-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_7_3_2 ("7-3-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_7_3_3 ("7-3-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_7_3_4 ("7-3-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_7_3_5 ("7-3-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_7_3_6 ("7-3-6", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_7_4_1 ("7-4-1", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    MISRACPP2008_7_4_2 ("7-4-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_7_4_3 ("7-4-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_7_5_1 ("7-5-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_7_5_2 ("7-5-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_7_5_3 ("7-5-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_7_5_4 ("7-5-4", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_8_0_1 ("8-0-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_8_3_1 ("8-3-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_8_4_1 ("8-4-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_8_4_2 ("8-4-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_8_4_3 ("8-4-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_8_4_4 ("8-4-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_8_5_1 ("8-5-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_8_5_2 ("8-5-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_8_5_3 ("8-5-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_9_3_1 ("9-3-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_9_3_2 ("9-3-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_9_3_3 ("9-3-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_9_5_1 ("9-5-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_9_6_1 ("9-6-1", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    MISRACPP2008_9_6_2 ("9-6-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_9_6_3 ("9-6-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_9_6_4 ("9-6-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_10_1_1 ("10-1-1", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_10_1_2 ("10-1-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_10_1_3 ("10-1-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_10_2_1 ("10-2-1", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_10_3_1 ("10-3-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_10_3_2 ("10-3-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_10_3_3 ("10-3-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_11_0_1 ("11-0-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_12_1_1 ("12-1-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_12_1_2 ("12-1-2", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_12_1_3 ("12-1-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_12_8_1 ("12-8-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_12_8_2 ("12-8-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_14_5_1 ("14-5-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_14_5_2 ("14-5-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_14_5_3 ("14-5-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_14_6_1 ("14-6-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_14_6_2 ("14-6-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_14_7_1 ("14-7-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_14_7_2 ("14-7-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_14_7_3 ("14-7-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_14_8_1 ("14-8-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_14_8_2 ("14-8-2", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_15_0_1 ("15-0-1", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    MISRACPP2008_15_0_2 ("15-0-2", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_15_0_3 ("15-0-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_15_1_1 ("15-1-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_15_1_2 ("15-1-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_15_1_3 ("15-1-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_15_3_1 ("15-3-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_15_3_2 ("15-3-2", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_15_3_3 ("15-3-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_15_3_4 ("15-3-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_15_3_5 ("15-3-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_15_3_6 ("15-3-6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_15_3_7 ("15-3-7", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_15_4_1 ("15-4-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_15_5_1 ("15-5-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_15_5_2 ("15-5-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_15_5_3 ("15-5-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_16_0_1 ("16-0-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_0_2 ("16-0-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_0_3 ("16-0-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_0_4 ("16-0-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_0_5 ("16-0-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_0_6 ("16-0-6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_0_7 ("16-0-7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_0_8 ("16-0-8", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_16_1_1 ("16-1-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_1_2 ("16-1-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_16_2_1 ("16-2-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_2_2 ("16-2-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_2_3 ("16-2-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_2_4 ("16-2-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_2_5 ("16-2-5", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_2_6 ("16-2-6", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_16_3_1 ("16-3-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_16_3_2 ("16-3-2", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_16_6_1 ("16-6-1", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),

    MISRACPP2008_17_0_1 ("17-0-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_17_0_2 ("17-0-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_17_0_3 ("17-0-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_17_0_4 ("17-0-4", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    MISRACPP2008_17_0_5 ("17-0-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_18_0_1 ("18-0-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_18_0_2 ("18-0-2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_18_0_3 ("18-0-3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_18_0_4 ("18-0-4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRACPP2008_18_0_5 ("18-0-5", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_18_2_1 ("18-2-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_18_4_1 ("18-4-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_18_7_1 ("18-7-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_19_3_1 ("19-3-1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRACPP2008_27_0_1 ("27-0-1", Boolean.TRUE, Implementability.IMPLEMENTABLE);

    private String name;
    private Boolean isMandatory;
    private Implementability implementability;

    StandardRule(String name, Boolean isMandatory, Implementability implementability) {
      this.name = name;
      this.isMandatory = isMandatory;
      this.implementability = implementability;
    }

    @Override
    public String getCodingStandardRuleId() {
      return name;
    }

    @Override
    public Implementability getImplementability() {
      return implementability;
    }

    @Override
    public boolean isRuleRequired() {
      return isMandatory;
    }
  }

  public MisraCPP2008 () {

    super(StandardRule.values());

  }

  @Override
  public String getSeeSectionSearchString() {

    return SEE_SECTION_SEARCH_STRING;
  }

  @Override
  public String getReferencePattern() {

    return REFERENCE_PATTERN;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {
    return rule.getMisraCpp();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setMisraCpp(ids);
  }

  @Override
  public Language getLanguage() {
    return language;
  }

  @Override
  public String getStandardName() {
    return NAME;
  }

  @Override
  public String getRSpecReferenceFieldName() {
    return NAME;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {
    return StandardRule.values();
  }

}
