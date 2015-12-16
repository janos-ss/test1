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
import com.sonarsource.ruleapi.externalspecifications.tools.ReSharper.ReSharperRule;
import com.sonarsource.ruleapi.utilities.Language;
import java.util.ArrayList;
import java.util.List;

public class ReSharperWarnings extends AbstractReportableExternalTool {

  private String toolName = "ReSharper Warnings";
  private Language language = Language.CSH;

  private String reportName = toolName + " " + language.getRspec();

  private static final String[] IDs = new String[] {
    "AccessToDisposedClosure",
    "AccessToForEachVariableInClosure",
    "AccessToModifiedClosure",
    "AccessToStaticMemberViaDerivedType",
    "AnnotationConflictInHierarchy",
    "AnnotationRedundancyAtValueType",
    "AnnotationRedundancyInHierarchy",
    "ArrangeStaticMemberQualifier",
    "ArrangeThisQualifier",
    "AssignmentInConditionalExpression",
    "AssignNullToNotNullAttribute",
    "BaseMemberHasParams",
    "BaseMethodCallWithDefaultParameter",
    "BaseObjectEqualsIsObjectEquals",
    "BaseObjectGetHashCodeCallInGetHashCode",
    "BitwiseOperatorOnEnumWithoutFlags",
    "CannotApplyEqualityOperatorToType",
    "CheckNamespace",
    "ClassCannotBeInstantiated",
    "CollectionNeverQueried.Global",
    "CollectionNeverQueried.Local",
    "CollectionNeverUpdated.Global",
    "CollectionNeverUpdated.Local",
    "CompareOfFloatsByEqualityOperator",
    "ConditionalTernaryEqualBranch",
    "ConditionIsAlwaysTrueOrFalse",
    "ConstantConditionalAccessQualifier",
    "ConstantNullCoalescingCondition",
    "ConstructorInitializerLoop",
    "ContractAnnotationNotParsed",
    "CoVariantArrayConversion",
    "DefaultValueAttributeForOptionalParameter",
    "DelegateSubtraction",
    "DynamicShiftRightOpIsNotInt",
    "EmptyConstructor",
    "EmptyDestructor",
    "EmptyEmbeddedStatement",
    "EmptyForStatement",
    "EmptyGeneralCatchClause",
    "EmptyNamespace",
    "EmptyStatement",
    "EnumerableSumInExplicitUncheckedContext",
    "EnumUnderlyingTypeIsInt",
    "EqualExpressionComparison",
    "EventNeverInvoked",
    "EventUnsubscriptionViaAnonymousDelegate",
    "ExplicitCallerInfoArgument",
    "ExpressionIsAlwaysNull",
    "FormatStringProblem",
    "ForStatementConditionIsTrue",
    "FunctionNeverReturns",
    "FunctionRecursiveOnAllPaths",
    "GCSuppressFinalizeForTypeWithoutDestructor",
    "HeuristicUnreachableCode",
    "ImpureMethodCallOnReadonlyValueField",
    "InconsistentlySynchronizedField",
    "InconsistentNaming",
    "InterpolatedStringExpressionIsNotIFormattable",
    "IsExpressionAlwaysFalse",
    "IsExpressionAlwaysTrue",
    "IteratorMethodResultIsIgnored",
    "LocalizableElement",
    "LocalVariableHidesMember",
    "LongLiteralEndingLowerL",
    "LoopVariableIsNeverChangedInsideLoop",
    "MeaninglessDefaultParameterValue",
    "MemberHidesStaticFromOuterClass",
    "MemberInitializerValueIgnored",
    "MethodOverloadWithOptionalParameter",
    "MultipleNullableAttributesUsage",
    "MultipleOrderBy",
    "MustUseReturnValue",
    "NonReadonlyMemberInGetHashCode",
    "NotAccessedField.Local",
    "NotAccessedVariable",
    "NotNullMemberIsNotInitialized",
    "NotResolvedInText",
    "ObjectCreationAsStatement",
    "OneWayOperationContractWithReturnType",
    "OperationContractWithoutServiceContract",
    "OperatorIsCanBeUsed",
    "OptionalParameterHierarchyMismatch",
    "OptionalParameterRefOut",
    "ParameterHidesMember",
    "PartialMethodParameterNameMismatch",
    "PartialMethodWithSinglePart",
    "PartialTypeWithSinglePart",
    "PolymorphicFieldLikeEventInvocation",
    "PossibleAssignmentToReadonlyField",
    "PossibleInfiniteInheritance",
    "PossibleIntendedRethrow",
    "PossibleInterfaceMemberAmbiguity",
    "PossibleInvalidCastException",
    "PossibleInvalidCastExceptionInForeachLoop",
    "PossibleInvalidOperationException",
    "PossibleLossOfFraction",
    "PossibleMistakenArgument",
    "PossibleMistakenCallToGetType.1",
    "PossibleMistakenCallToGetType.2",
    "PossibleMultipleEnumeration",
    "PossibleMultipleWriteAccessInDoubleCheckLocking",
    "PossibleNullReferenceException",
    "PossibleUnintendedReferenceComparison",
    "PossiblyMistakenUseOfParamsMethod",
    "PrivateFieldCanBeConvertedToLocalVariable",
    "PureAttributeOnVoidMethod",
    "ReadAccessInDoubleCheckLocking",
    "RedundantAnonymousTypePropertyName",
    "RedundantArgumentDefaultValue",
    "RedundantAssignment",
    "RedundantBaseConstructorCall",
    "RedundantBaseQualifier",
    "RedundantBoolCompare",
    "RedundantCaseLabel",
    "RedundantCast",
    "RedundantCast.0",
    "RedundantCatchClause",
    "RedundantCheckBeforeAssignment",
    "RedundantDefaultMemberInitializer",
    "RedundantDelegateCreation",
    "RedundantEmptyDefaultSwitchBranch",
    "RedundantEmptyFinallyBlock",
    "RedundantEmptyObjectOrCollectionInitializer",
    "RedundantEnumerableCastCall",
    "RedundantExplicitArrayCreation",
    "RedundantExplicitArraySize",
    "RedundantExplicitNullableCreation",
    "RedundantExtendsListEntry",
    "RedundantJumpStatement",
    "RedundantLambdaParameterType",
    "RedundantLambdaSignatureParentheses",
    "RedundantLogicalConditionalExpressionOperand",
    "RedundantNameQualifier",
    "RedundantOverflowCheckingContext",
    "RedundantOverridenMember",
    "RedundantParams",
    "RedundantStringFormatCall",
    "RedundantStringToCharArrayCall",
    "RedundantTernaryExpression",
    "RedundantToStringCall",
    "RedundantTypeArgumentsOfMethod",
    "RedundantUnsafeContext",
    "RedundantUsingDirective",
    "ReferenceEqualsWithValueType",
    "RequiredBaseTypesConflict",
    "RequiredBaseTypesDirectConflict",
    "RequiredBaseTypesIsNotInherited",
    "ReturnValueOfPureMethodIsNotUsed",
    "SealedMemberInSealedClass",
    "SeviceContractWithoutOperations",
    "SpecifyACultureInStringConversionExplicitly",
    "StaticMemberInGenericType",
    "StaticMemberInitializerReferesToMemberBelow",
    "StringCompareIsCultureSpecific.1",
    "StringCompareIsCultureSpecific.2",
    "StringCompareIsCultureSpecific.3",
    "StringCompareIsCultureSpecific.4",
    "StringCompareIsCultureSpecific.5",
    "StringCompareIsCultureSpecific.6",
    "StringCompareToIsCultureSpecific",
    "StringIndexOfIsCultureSpecific.1",
    "StringIndexOfIsCultureSpecific.2",
    "StringIndexOfIsCultureSpecific.3",
    "StringLastIndexOfIsCultureSpecific.1",
    "StringLastIndexOfIsCultureSpecific.2",
    "StringLastIndexOfIsCultureSpecific.3",
    "SuspiciousTypeConversion.Global",
    "ThreadStaticAtInstanceField",
    "ThreadStaticFieldHasInitializer",
    "TryCastAndCheckForNull.0",
    "TryCastAndCheckForNull.1",
    "UnassignedField.Local",
    "UnassignedGetOnlyAutoProperty",
    "UnassignedReadonlyField",
    "UnreachableCode",
    "UnsupportedRequiredBaseType",
    "UnusedAnonymousMethodSignature",
    "UnusedAutoPropertyAccessor.Global",
    "UnusedAutoPropertyAccessor.Local",
    "UnusedLabel",
    "UnusedMember.Local",
    "UnusedMemberHiearchy.Local",
    "UnusedMemberInSuper.Local",
    "UnusedMethodReturnValue.Local",
    "UnusedParameter.Local",
    "UnusedTypeParameter",
    "UnusedVariable",
    "ValueParameterNotUsed",
    "VirtualMemberCallInContructor",
    "VoidMethodWithMustUseReturnValueAttribute"
  };

  @Override
  public Language getLanguage() {
    return language;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {
    List<CodingStandardRule> builder = new ArrayList<>();

    for (String id: IDs) {
      builder.add(new ReSharperCodingRule(id));
    }

    return builder.toArray(new CodingStandardRule[builder.size()]);
  }

  @Override
  public String getStandardName() {
    return reportName;
  }

  @Override
  public String getRSpecReferenceFieldName() {
    return "ReSharper";
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {
    return rule.getResharper();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setResharper(ids);
  }

  private static class ReSharperCodingRule implements CodingStandardRule {

    private final String id;
    private final Implementability implementability;

    public ReSharperCodingRule(String id) {
      this.id = id;
      this.implementability = implementability(id);
    }

    private static final Implementability implementability(String id) {
      for (ReSharperRule rule: ReSharperRule.values()) {
        if (rule.getCodingStandardRuleId().equals(id)) {
          return rule.getImplementability();
        }
      }

      return Implementability.IMPLEMENTABLE;
    }

    @Override
    public String getCodingStandardRuleId() {
      return id;
    }

    @Override
    public Implementability getImplementability() {
      return implementability;
    }

  }

}
