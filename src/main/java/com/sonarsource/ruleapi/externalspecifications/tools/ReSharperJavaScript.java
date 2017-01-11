/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.tools;


import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableExternalTool;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;
import java.util.List;

public class ReSharperJavaScript extends AbstractReportableExternalTool{

  private String toolName = "ReSharper";
  private Language language = Language.JS;

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
    ASSIGNEDVALUEISNEVERUSED("AssignedValueIsNeverUsed", Implementability.IMPLEMENTABLE),
    ASSIGNTOIMPLICITGLOBALINFUNCTIONSCOPE("AssignToImplicitGlobalInFunctionScope", Implementability.IMPLEMENTABLE),
    CONDITIONISALWAYSCONST("ConditionIsAlwaysConst", Implementability.IMPLEMENTABLE),
    DUPLICATINGLOCALDECLARATION("DuplicatingLocalDeclaration", Implementability.IMPLEMENTABLE),
    DUPLICATINGPROPERTYDECLARATION("DuplicatingPropertyDeclaration", Implementability.IMPLEMENTABLE),
    DUPLICATINGSWITCHLABEL("DuplicatingSwitchLabel", Implementability.IMPLEMENTABLE),
    ELIDEDTRAILINGELEMENT("ElidedTrailingElement", Implementability.IMPLEMENTABLE),
    EMPTYOBJECTPROPERTYDECLARATION("EmptyObjectPropertyDeclaration", Implementability.IMPLEMENTABLE),
    ERRORINXMLDOCREFERENCE("ErrorInXmlDocReference", Implementability.IMPLEMENTABLE),
    EXPRESSIONISALWAYSCONST("ExpressionIsAlwaysConst", Implementability.IMPLEMENTABLE),
    INCONSISTENTFUNCTIONRETURNS("InconsistentFunctionReturns", Implementability.IMPLEMENTABLE),
    INCONSISTENTNAMING("InconsistentNaming", Implementability.IMPLEMENTABLE),
    INVOCATIONOFNONFUNCTION("InvocationOfNonFunction", Implementability.IMPLEMENTABLE),
    INVOKEDEXPRESSIONMAYBENONFUNCTION("InvokedExpressionMaybeNonFunction", Implementability.IMPLEMENTABLE),
    JSFUNCTIONCANBECONVERTEDTOLAMBDA("JsFunctionCanBeConvertedToLambda", Implementability.IMPLEMENTABLE),
    JSUNREACHABLECODE("JsUnreachableCode", Implementability.IMPLEMENTABLE),
    JUMPMUSTBEINLOOP("JumpMustBeInLoop", Implementability.IMPLEMENTABLE),
    LABELORSEMICOLONEXPECTED("LabelOrSemicolonExpected", Implementability.IMPLEMENTABLE),
    LVALUEISEXPECTED("LValueIsExpected", Implementability.IMPLEMENTABLE),
    MISUSEOFOWNERFUNCTIONTHIS("MisuseOfOwnerFunctionThis", Implementability.IMPLEMENTABLE),
    MULTIPLEDECLARATIONSINFOREACH("MultipleDeclarationsInForeach", Implementability.IMPLEMENTABLE),
    NOTALLPATHSRETURNVALUE("NotAllPathsReturnValue", Implementability.IMPLEMENTABLE),
    NOTRESOLVED("NotResolved", Implementability.IMPLEMENTABLE),
    PARAMETERVALUEISNOTUSED("ParameterValueIsNotUsed", Implementability.IMPLEMENTABLE),
    POSSIBLYUNASSIGNEDPROPERTY("PossiblyUnassignedProperty", Implementability.IMPLEMENTABLE),
    PROPERTYGETTERCANNOTHAVEPARAMETERS("PropertyGetterCannotHaveParameters", Implementability.IMPLEMENTABLE),
    PROPERTYSETTERMUSTHAVESINGLEPARAMETER("PropertySetterMustHaveSingleParameter", Implementability.IMPLEMENTABLE),
    QUALIFIEDEXPRESSIONISNULL("QualifiedExpressionIsNull", Implementability.IMPLEMENTABLE),
    QUALIFIEDEXPRESSIONMAYBENULL("QualifiedExpressionMaybeNull", Implementability.IMPLEMENTABLE),
    REDUNDANTLOCALFUNCTIONNAME("RedundantLocalFunctionName", Implementability.IMPLEMENTABLE),
    RETURNFROMGLOBALSCOPETWITHVALUE("ReturnFromGlobalScopetWithValue", Implementability.IMPLEMENTABLE),
    STATEMENTISNOTTERMINATED("StatementIsNotTerminated", Implementability.IMPLEMENTABLE),
    THISINGLOBALCONTEXT("ThisInGlobalContext", Implementability.IMPLEMENTABLE),
    THROWMUSTBEFOLLOWEDBYEXPRESSION("ThrowMustBeFollowedByExpression", Implementability.IMPLEMENTABLE),
    UNUSEDLOCALS("UnusedLocals", Implementability.IMPLEMENTABLE),
    USEOFIMPLICITGLOBALINFUNCTIONSCOPE("UseOfImplicitGlobalInFunctionScope", Implementability.IMPLEMENTABLE),
    USINGOFRESERVEDWORD("UsingOfReservedWord", Implementability.IMPLEMENTABLE),
    WRONGEXPRESSIONSTATEMENT("WrongExpressionStatement", Implementability.IMPLEMENTABLE);


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
