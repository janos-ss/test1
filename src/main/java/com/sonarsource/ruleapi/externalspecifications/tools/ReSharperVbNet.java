/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.tools;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableExternalTool;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.List;


public class ReSharperVbNet extends AbstractReportableExternalTool{

  private String toolName = "ReSharper";
  private Language language = Language.VBNET;

  private String reportName = toolName + " " + language.getRspec();

  @Override
  public Language getLanguage() {

    return language;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return ReSharperRule.values();
  }

  @Override
  public String getStandardName() {

    return reportName;
  }

  @Override
  public String getRSpecReferenceFieldName() {

    return toolName;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {

    return rule.getResharper();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setResharper(ids);
  }

  public enum ReSharperRule implements CodingStandardRule {
    INACTIVEPREPROCESSORBRANCH("InactivePreprocessorBranch", Implementability.IMPLEMENTABLE),
    INCONSISTENTNAMING("InconsistentNaming", Implementability.IMPLEMENTABLE),
    NOTASSIGNEDOUTPARAMETER("NotAssignedOutParameter", Implementability.IMPLEMENTABLE),
    POSSIBLEWRITETOME("PossibleWriteToMe", Implementability.IMPLEMENTABLE),
    REDUNDANTARRAYLOWERBOUNDSPECIFICATION("RedundantArrayLowerBoundSpecification", Implementability.IMPLEMENTABLE),
    REDUNDANTCOMMAINARRAYINITIALIZER("RedundantCommaInArrayInitializer", Implementability.REJECTED),
    REDUNDANTEMPTYCASEELSE("RedundantEmptyCaseElse", Implementability.IMPLEMENTABLE),
    REDUNDANTITERATORKEYWORD("RedundantIteratorKeyword", Implementability.IMPLEMENTABLE),
    REDUNDANTMEQUALIFIER("RedundantMeQualifier", Implementability.IMPLEMENTABLE),
    REDUNDANTMYBASEQUALIFIER("RedundantMyBaseQualifier", Implementability.IMPLEMENTABLE),
    REDUNDANTMYCLASSQUALIFIER("RedundantMyClassQualifier", Implementability.IMPLEMENTABLE),
    REDUNDANTQUALIFIER("RedundantQualifier", Implementability.IMPLEMENTABLE),
    REDUNDANTSTRINGTYPE("RedundantStringType", Implementability.IMPLEMENTABLE),
    REDUNDANTTYPEARGUMENTSOFMETHOD("RedundantTypeArgumentsOfMethod", Implementability.IMPLEMENTABLE),
    SIMPLIFYIIF("SimplifyIIf", Implementability.IMPLEMENTABLE),
    UNUSEDIMPORTCLAUSE("UnusedImportClause", Implementability.IMPLEMENTABLE),
    VBCHECKFORREFERENCEEQUALITYINSTEAD_1("VBCheckForReferenceEqualityInstead.1", Implementability.IMPLEMENTABLE),
    VBCHECKFORREFERENCEEQUALITYINSTEAD_2("VBCheckForReferenceEqualityInstead.2", Implementability.IMPLEMENTABLE),
    VBPOSSIBLEMISTAKENARGUMENT("VBPossibleMistakenArgument", Implementability.IMPLEMENTABLE),
    VBPOSSIBLEMISTAKENCALLTOGETTYPE_1("VBPossibleMistakenCallToGetType.1", Implementability.IMPLEMENTABLE),
    VBPOSSIBLEMISTAKENCALLTOGETTYPE_2("VBPossibleMistakenCallToGetType.2", Implementability.IMPLEMENTABLE),
    VBREMOVETOLIST_1("VBRemoveToList.1", Implementability.IMPLEMENTABLE),
    VBREMOVETOLIST_2("VBRemoveToList.2", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHFIRSTORDEFAULT("VBReplaceWithFirstOrDefault", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHLASTORDEFAULT("VBReplaceWithLastOrDefault", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_1("VBReplaceWithOfType.1", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_2("VBReplaceWithOfType.2", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_ANY_1("VBReplaceWithOfType.Any.1", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_ANY_2("VBReplaceWithOfType.Any.2", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_COUNT_1("VBReplaceWithOfType.Count.1", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_COUNT_2("VBReplaceWithOfType.Count.2", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_FIRST_1("VBReplaceWithOfType.First.1", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_FIRST_2("VBReplaceWithOfType.First.2", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_FIRSTORDEFAULT_1("VBReplaceWithOfType.FirstOrDefault.1", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_FIRSTORDEFAULT_2("VBReplaceWithOfType.FirstOrDefault.2", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_LAST_1("VBReplaceWithOfType.Last.1", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_LAST_2("VBReplaceWithOfType.Last.2", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_LASTORDEFAULT_1("VBReplaceWithOfType.LastOrDefault.1", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_LASTORDEFAULT_2("VBReplaceWithOfType.LastOrDefault.2", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_SINGLE_1("VBReplaceWithOfType.Single.1", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_SINGLE_2("VBReplaceWithOfType.Single.2", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_SINGLEORDEFAULT_1("VBReplaceWithOfType.SingleOrDefault.1", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_SINGLEORDEFAULT_2("VBReplaceWithOfType.SingleOrDefault.2", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHOFTYPE_WHERE("VBReplaceWithOfType.Where", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHSINGLEASSIGNMENT_1("VBReplaceWithSingleAssignment.1", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHSINGLEASSIGNMENT_2("VBReplaceWithSingleAssignment.2", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHSINGLECALLTOANY("VBReplaceWithSingleCallToAny", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHSINGLECALLTOCOUNT("VBReplaceWithSingleCallToCount", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHSINGLECALLTOFIRST("VBReplaceWithSingleCallToFirst", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHSINGLECALLTOFIRSTORDEFAULT("VBReplaceWithSingleCallToFirstOrDefault", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHSINGLECALLTOLAST("VBReplaceWithSingleCallToLast", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHSINGLECALLTOLASTORDEFAULT("VBReplaceWithSingleCallToLastOrDefault", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHSINGLECALLTOSINGLE("VBReplaceWithSingleCallToSingle", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHSINGLECALLTOSINGLEORDEFAULT("VBReplaceWithSingleCallToSingleOrDefault", Implementability.IMPLEMENTABLE),
    VBREPLACEWITHSINGLEORDEFAULT("VBReplaceWithSingleOrDefault", Implementability.IMPLEMENTABLE),
    VBSIMPLIFYLINQEXPRESSION_1("VBSimplifyLinqExpression.1", Implementability.IMPLEMENTABLE),
    VBSIMPLIFYLINQEXPRESSION_10("VBSimplifyLinqExpression.10", Implementability.IMPLEMENTABLE),
    VBSIMPLIFYLINQEXPRESSION_2("VBSimplifyLinqExpression.2", Implementability.IMPLEMENTABLE),
    VBSIMPLIFYLINQEXPRESSION_3("VBSimplifyLinqExpression.3", Implementability.IMPLEMENTABLE),
    VBSIMPLIFYLINQEXPRESSION_4("VBSimplifyLinqExpression.4", Implementability.IMPLEMENTABLE),
    VBSIMPLIFYLINQEXPRESSION_5("VBSimplifyLinqExpression.5", Implementability.IMPLEMENTABLE),
    VBSIMPLIFYLINQEXPRESSION_6("VBSimplifyLinqExpression.6", Implementability.IMPLEMENTABLE),
    VBSIMPLIFYLINQEXPRESSION_7("VBSimplifyLinqExpression.7", Implementability.IMPLEMENTABLE),
    VBSIMPLIFYLINQEXPRESSION_8("VBSimplifyLinqExpression.8", Implementability.IMPLEMENTABLE),
    VBSIMPLIFYLINQEXPRESSION_9("VBSimplifyLinqExpression.9", Implementability.IMPLEMENTABLE),
    VBSTRINGCOMPAREISCULTURESPECIFIC_1("VBStringCompareIsCultureSpecific.1", Implementability.IMPLEMENTABLE),
    VBSTRINGCOMPAREISCULTURESPECIFIC_2("VBStringCompareIsCultureSpecific.2", Implementability.IMPLEMENTABLE),
    VBSTRINGCOMPAREISCULTURESPECIFIC_3("VBStringCompareIsCultureSpecific.3", Implementability.IMPLEMENTABLE),
    VBSTRINGCOMPAREISCULTURESPECIFIC_4("VBStringCompareIsCultureSpecific.4", Implementability.IMPLEMENTABLE),
    VBSTRINGCOMPAREISCULTURESPECIFIC_5("VBStringCompareIsCultureSpecific.5", Implementability.IMPLEMENTABLE),
    VBSTRINGCOMPAREISCULTURESPECIFIC_6("VBStringCompareIsCultureSpecific.6", Implementability.IMPLEMENTABLE),
    VBSTRINGCOMPARETOISCULTURESPECIFIC("VBStringCompareToIsCultureSpecific", Implementability.IMPLEMENTABLE),
    VBSTRINGENDSWITHISCULTURESPECIFIC("VBStringEndsWithIsCultureSpecific", Implementability.IMPLEMENTABLE),
    VBSTRINGINDEXOFISCULTURESPECIFIC_1("VBStringIndexOfIsCultureSpecific.1", Implementability.IMPLEMENTABLE),
    VBSTRINGINDEXOFISCULTURESPECIFIC_2("VBStringIndexOfIsCultureSpecific.2", Implementability.IMPLEMENTABLE),
    VBSTRINGINDEXOFISCULTURESPECIFIC_3("VBStringIndexOfIsCultureSpecific.3", Implementability.IMPLEMENTABLE),
    VBSTRINGLASTINDEXOFISCULTURESPECIFIC_1("VBStringLastIndexOfIsCultureSpecific.1", Implementability.IMPLEMENTABLE),
    VBSTRINGLASTINDEXOFISCULTURESPECIFIC_2("VBStringLastIndexOfIsCultureSpecific.2", Implementability.IMPLEMENTABLE),
    VBSTRINGLASTINDEXOFISCULTURESPECIFIC_3("VBStringLastIndexOfIsCultureSpecific.3", Implementability.IMPLEMENTABLE),
    VBSTRINGSTARTSWITHISCULTURESPECIFIC("VBStringStartsWithIsCultureSpecific", Implementability.IMPLEMENTABLE),
    VBUNREACHABLECODE("VbUnreachableCode", Implementability.IMPLEMENTABLE),
    VBUSEARRAYCREATIONEXPRESSION_1("VBUseArrayCreationExpression.1", Implementability.IMPLEMENTABLE),
    VBUSEARRAYCREATIONEXPRESSION_2("VBUseArrayCreationExpression.2", Implementability.IMPLEMENTABLE),
    VBUSEFIRSTINSTEAD("VBUseFirstInstead", Implementability.IMPLEMENTABLE),
    VBUSEMETHODANY_1("VBUseMethodAny.1", Implementability.IMPLEMENTABLE),
    VBUSEMETHODANY_2("VBUseMethodAny.2", Implementability.IMPLEMENTABLE),
    VBUSEMETHODANY_3("VBUseMethodAny.3", Implementability.IMPLEMENTABLE),
    VBUSEMETHODANY_4("VBUseMethodAny.4", Implementability.IMPLEMENTABLE),
    VBUSEMETHODANY_5("VBUseMethodAny.5", Implementability.IMPLEMENTABLE),
    VBUSEMETHODISINSTANCEOFTYPE("VBUseMethodIsInstanceOfType", Implementability.IMPLEMENTABLE),
    VBUSETYPEOFISOPERATOR_1("VBUseTypeOfIsOperator.1", Implementability.IMPLEMENTABLE),
    VBUSETYPEOFISOPERATOR_2("VBUseTypeOfIsOperator.2", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC40000("VBWarnings::BC40000", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC400005("VBWarnings::BC400005", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC40008("VBWarnings::BC40008", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC40056("VBWarnings::BC40056", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC42016("VBWarnings::BC42016", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC42025("VBWarnings::BC42025", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC42104("VBWarnings::BC42104", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC42105("VBWarnings::BC42105", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC42304("VBWarnings::BC42304", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC42309("VBWarnings::BC42309", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC42322("VBWarnings::BC42322", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC42349("VBWarnings::BC42349", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC42353("VBWarnings::BC42353", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC42356("VBWarnings::BC42356", Implementability.IMPLEMENTABLE),
    VBWARNINGS_BC42358("VBWarnings::BC42358", Implementability.IMPLEMENTABLE),
    VBWARNINGS_WME006("VBWarnings::WME006", Implementability.IMPLEMENTABLE),
    VBERRORS("VBErrors", Implementability.IMPLEMENTABLE);


    private Implementability implementability;
    private String title;

    ReSharperRule(String title, Implementability implementability) {
      this.implementability = implementability;
      this.title = title;
    }

    @Override
    public Implementability getImplementability() {
      return this.implementability;
    }
    @Override
    public String getCodingStandardRuleId() {
      return this.title;
    }

  }
}
