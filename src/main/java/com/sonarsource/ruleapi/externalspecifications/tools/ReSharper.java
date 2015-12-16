/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.tools;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableExternalTool;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.HasLevel;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.List;


public class ReSharper extends AbstractReportableExternalTool {

  private String toolName = "ReSharper";
  private Language language = Language.CSH;

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

  public enum Activation {
    UNKNOWN, OFF, HINT, WARNING, ERROR;
  };

  public enum ReSharperRule implements CodingStandardRule, HasLevel {
    ACCESSTODISPOSEDCLOSURE("AccessToDisposedClosure", Implementability.IMPLEMENTABLE, Activation.WARNING),
    ACCESSTOFOREACHVARIABLEINCLOSURE("AccessToForEachVariableInClosure", Implementability.IMPLEMENTABLE, Activation.WARNING),
    ACCESSTOMODIFIEDCLOSURE("AccessToModifiedClosure", Implementability.IMPLEMENTABLE, Activation.WARNING),
    ACCESSTOSTATICMEMBERVIADERIVEDTYPE("AccessToStaticMemberViaDerivedType", Implementability.REJECTED, Activation.WARNING),
    ANNOTATIONCONFLICTINHIERARCHY("AnnotationConflictInHierarchy", Implementability.REJECTED, Activation.WARNING),
    ANNOTATIONREDUNDANCEATVALUETYPE("AnnotationRedundanceAtValueType", Implementability.REJECTED, Activation.WARNING),
    ANNOTATIONREDUNDANCEINHIERARCHY("AnnotationRedundanceInHierarchy", Implementability.REJECTED, Activation.WARNING),
    ASSIGNNULLTONOTNULLATTRIBUTE("AssignNullToNotNullAttribute", Implementability.REJECTED),
    BASEMEMBERHASPARAMS("BaseMemberHasParams", Implementability.IMPLEMENTABLE, Activation.WARNING),
    BASEMETHODCALLWITHDEFAULTPARAMETER("BaseMethodCallWithDefaultParameter", Implementability.IMPLEMENTABLE, Activation.WARNING),
    BASEOBJECTEQUALSISOBJECTEQUALS("BaseObjectEqualsIsObjectEquals", Implementability.IMPLEMENTABLE, Activation.WARNING),
    BASEOBJECTGETHASHCODECALLINGETHASHCODE("BaseObjectGetHashCodeCallInGetHashCode", Implementability.IMPLEMENTABLE, Activation.WARNING),
    BITWISEOPERATORONENUMWITHOUTFLAGS("BitwiseOperatorOnEnumWithoutFlags", Implementability.IMPLEMENTABLE, Activation.WARNING),
    CANBEREPLACEDWITHTRYCASTANDCHECKFORNULL("CanBeReplacedWithTryCastAndCheckForNull", Implementability.IMPLEMENTABLE),
    CANNOTAPPLYEQUALITYOPERATORTOTYPE("CannotApplyEqualityOperatorToType", Implementability.REJECTED, Activation.WARNING),
    CHECKFORREFERENCEEQUALITYINSTEAD_1("CheckForReferenceEqualityInstead.1", Implementability.IMPLEMENTABLE),
    CHECKFORREFERENCEEQUALITYINSTEAD_2("CheckForReferenceEqualityInstead.2", Implementability.IMPLEMENTABLE),
    CHECKNAMESPACE("CheckNamespace", Implementability.IMPLEMENTABLE, Activation.WARNING),
    CLASSCANBESEALED_GLOBAL("ClassCanBeSealed.Global", Implementability.IMPLEMENTABLE),
    CLASSCANBESEALED_LOCAL("ClassCanBeSealed.Local", Implementability.IMPLEMENTABLE),
    CLASSCANNOTBEINSTANTIATED("ClassCannotBeInstantiated", Implementability.IMPLEMENTABLE, Activation.WARNING),
    CLASSNEVERINSTANTIATED_GLOBAL("ClassNeverInstantiated.Global", Implementability.IMPLEMENTABLE),
    CLASSNEVERINSTANTIATED_LOCAL("ClassNeverInstantiated.Local", Implementability.IMPLEMENTABLE),
    CLASSWITHVIRTUALMEMBERSNEVERINHERITED_GLOBAL("ClassWithVirtualMembersNeverInherited.Global", Implementability.IMPLEMENTABLE),
    CLASSWITHVIRTUALMEMBERSNEVERINHERITED_LOCAL("ClassWithVirtualMembersNeverInherited.Local", Implementability.IMPLEMENTABLE),
    COMPARENONCONSTRAINEDGENERICWITHNULL("CompareNonConstrainedGenericWithNull", Implementability.IMPLEMENTABLE),
    COMPAREOFFLOATSBYEQUALITYOPERATOR("CompareOfFloatsByEqualityOperator", Implementability.IMPLEMENTABLE, Activation.WARNING),
    CONDITIONALTERNARYEQUALBRANCH("ConditionalTernaryEqualBranch", Implementability.IMPLEMENTABLE, Activation.WARNING),
    CONDITIONISALWAYSTRUEORFALSE("ConditionIsAlwaysTrueOrFalse", Implementability.IMPLEMENTABLE, Activation.WARNING),
    CONSTANTNULLCOALESCINGCONDITION("ConstantNullCoalescingCondition", Implementability.IMPLEMENTABLE, Activation.WARNING),
    CONSTRUCTORINITIALIZERLOOP("ConstructorInitializerLoop", Implementability.REJECTED, Activation.WARNING),
    CONTRACTANNOTATIONNOTPARSED("ContractAnnotationNotParsed", Implementability.REJECTED, Activation.WARNING),
    CONVERTCLOSURETOMETHODGROUP("ConvertClosureToMethodGroup", Implementability.IMPLEMENTABLE),
    CONVERTCONDITIONALTERNARYTONULLCOALESCING("ConvertConditionalTernaryToNullCoalescing", Implementability.IMPLEMENTABLE),
    CONVERTIFDOTOWHILE("ConvertIfDoToWhile", Implementability.IMPLEMENTABLE),
    CONVERTIFSTATEMENTTOCONDITIONALTERNARYEXPRESSION("ConvertIfStatementToConditionalTernaryExpression", Implementability.IMPLEMENTABLE),
    CONVERTIFSTATEMENTTONULLCOALESCINGEXPRESSION("ConvertIfStatementToNullCoalescingExpression", Implementability.IMPLEMENTABLE),
    CONVERTIFSTATEMENTTORETURNSTATEMENT("ConvertIfStatementToReturnStatement", Implementability.IMPLEMENTABLE),
    CONVERTIFSTATEMENTTOSWITCHSTATEMENT("ConvertIfStatementToSwitchStatement", Implementability.IMPLEMENTABLE),
    CONVERTIFTOOREXPRESSION("ConvertIfToOrExpression", Implementability.IMPLEMENTABLE),
    CONVERTNULLABLETOSHORTFORM("ConvertNullableToShortForm", Implementability.IMPLEMENTABLE),
    CONVERTTOAUTOPROPERTY("ConvertToAutoProperty", Implementability.IMPLEMENTABLE),
    CONVERTTOAUTOPROPERTYWITHPRIVATESETTER("ConvertToAutoPropertyWithPrivateSetter", Implementability.IMPLEMENTABLE),
    CONVERTTOCONSTANT_GLOBAL("ConvertToConstant.Global", Implementability.IMPLEMENTABLE),
    CONVERTTOCONSTANT_LOCAL("ConvertToConstant.Local", Implementability.IMPLEMENTABLE),
    CONVERTTOLAMBDAEXPRESSION("ConvertToLambdaExpression", Implementability.IMPLEMENTABLE),
    CONVERTTOSTATICCLASS("ConvertToStaticClass", Implementability.IMPLEMENTABLE),
    COVARIANTARRAYCONVERSION("CoVariantArrayConversion", Implementability.IMPLEMENTABLE, Activation.WARNING),
    DEFAULTVALUEATTRIBUTEFOROPTIONALPARAMETER("DefaultValueAttributeForOptionalParameter", Implementability.IMPLEMENTABLE, Activation.WARNING),
    DELEGATESUBTRACTION("DelegateSubtraction", Implementability.IMPLEMENTABLE, Activation.WARNING),
    DONOTCALLOVERRIDABLEMETHODSINCONSTRUCTOR("DoNotCallOverridableMethodsInConstructor", Implementability.IMPLEMENTABLE),
    DOUBLENEGATIONOPERATOR("DoubleNegationOperator", Implementability.IMPLEMENTABLE),
    DUPLICATERESOURCE("DuplicateResource", Implementability.REJECTED),
    DYNAMICSHIFTRIGHTOPISNOTINT("DynamicShiftRightOpIsNotInt", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EMPTYCONSTRUCTOR("EmptyConstructor", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EMPTYDESTRUCTOR("EmptyDestructor", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EMPTYEMBEDDEDSTATEMENT("EmptyEmbeddedStatement", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EMPTYFORSTATEMENT("EmptyForStatement", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EMPTYGENERALCATCHCLAUSE("EmptyGeneralCatchClause", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EMPTYNAMESPACE("EmptyNamespace", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EMPTYSTATEMENT("EmptyStatement", Implementability.IMPLEMENTABLE, Activation.WARNING),
    ENUMERABLESUMINEXPLICITUNCHECKEDCONTEXT("EnumerableSumInExplicitUncheckedContext", Implementability.IMPLEMENTABLE, Activation.WARNING),
    ENUMUNDERLYINGTYPEISINT("EnumUnderlyingTypeIsInt", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EQUALEXPRESSIONCOMPARISON("EqualExpressionComparison", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EVENTNEVERINVOKED("EventNeverInvoked", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EVENTNEVERINVOKED_GLOBAL("EventNeverInvoked.Global", Implementability.IMPLEMENTABLE),
    EVENTNEVERSUBSCRIBEDTO_GLOBAL("EventNeverSubscribedTo.Global", Implementability.IMPLEMENTABLE),
    EVENTNEVERSUBSCRIBEDTO_LOCAL("EventNeverSubscribedTo.Local", Implementability.IMPLEMENTABLE),
    EVENTUNSUBSCRIPTIONVIAANONYMOUSDELEGATE("EventUnsubscriptionViaAnonymousDelegate", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EXPLICITCALLERINFOARGUMENT("ExplicitCallerInfoArgument", Implementability.IMPLEMENTABLE, Activation.WARNING),
    EXPRESSIONISALWAYSNULL("ExpressionIsAlwaysNull", Implementability.IMPLEMENTABLE, Activation.WARNING),
    FIELDCANBEMADEREADONLY_GLOBAL("FieldCanBeMadeReadOnly.Global", Implementability.IMPLEMENTABLE),
    FIELDCANBEMADEREADONLY_LOCAL("FieldCanBeMadeReadOnly.Local", Implementability.IMPLEMENTABLE),
    FORCANBECONVERTEDTOFOREACH("ForCanBeConvertedToForeach", Implementability.IMPLEMENTABLE),
    FORMATSTRINGPLACEHOLDERSMISMATCH("FormatStringPlaceholdersMismatch", Implementability.IMPLEMENTABLE),
    FORMATSTRINGPROBLEM("FormatStringProblem", Implementability.IMPLEMENTABLE, Activation.WARNING),
    FORSTATEMENTCONDITIONISTRUE("ForStatementConditionIsTrue", Implementability.IMPLEMENTABLE, Activation.WARNING),
    FUNCTIONNEVERRETURNS("FunctionNeverReturns", Implementability.REJECTED, Activation.WARNING),
    FUNCTIONRECURSIVEONALLPATHS("FunctionRecursiveOnAllPaths", Implementability.IMPLEMENTABLE, Activation.WARNING),
    GCSUPPRESSFINALIZEFORTYPEWITHOUTDESTRUCTOR("GCSuppressFinalizeForTypeWithoutDestructor", Implementability.IMPLEMENTABLE, Activation.WARNING),
    HEURISTICALLYUNREACHABLECODE("HeuristicallyUnreachableCode", Implementability.IMPLEMENTABLE),
    HEURISTICUNREACHABLECODE("HeuristicUnreachableCode", Implementability.IMPLEMENTABLE, Activation.WARNING),
    IMPLICITLYCAPTUREDCLOSURE("ImplicitlyCapturedClosure", Implementability.IMPLEMENTABLE),
    IMPUREMETHODCALLONREADONLYVALUEFIELD("ImpureMethodCallOnReadonlyValueField", Implementability.IMPLEMENTABLE, Activation.WARNING),
    INCONSISTENTNAMING("InconsistentNaming", Implementability.IMPLEMENTABLE, Activation.WARNING),
    INTRODUCEOPTIONALPARAMETERS_GLOBAL("IntroduceOptionalParameters.Global", Implementability.IMPLEMENTABLE),
    INTRODUCEOPTIONALPARAMETERS_LOCAL("IntroduceOptionalParameters.Local", Implementability.IMPLEMENTABLE),
    INVALIDATTRIBUTEVALUE("InvalidAttributeValue", Implementability.REJECTED),
    INVALIDTASKELEMENT("InvalidTaskElement", Implementability.REJECTED),
    INVALIDVALUETYPE("InvalidValueType", Implementability.REJECTED),
    INVERTCONDITION_1("InvertCondition.1", Implementability.IMPLEMENTABLE),
    INVERTIF("InvertIf", Implementability.IMPLEMENTABLE),
    INVOCATIONISSKIPPED("InvocationIsSkipped", Implementability.IMPLEMENTABLE),
    INVOKEASEXTENSIONMETHOD("InvokeAsExtensionMethod", Implementability.IMPLEMENTABLE),
    ITERATORMETHODRESULTISIGNORED("IteratorMethodResultIsIgnored", Implementability.IMPLEMENTABLE, Activation.WARNING),
    JOINDECLARATIONANDINITIALIZER("JoinDeclarationAndInitializer", Implementability.IMPLEMENTABLE),
    LOCALIZABLEELEMENT("LocalizableElement", Implementability.IMPLEMENTABLE, Activation.WARNING),
    LOCALVARIABLEHIDESMEMBER("LocalVariableHidesMember", Implementability.IMPLEMENTABLE, Activation.WARNING),
    LONGLITERALENDINGLOWERL("LongLiteralEndingLowerL", Implementability.IMPLEMENTABLE, Activation.WARNING),
    LOOPCANBECONVERTEDTOQUERY("LoopCanBeConvertedToQuery", Implementability.IMPLEMENTABLE),
    LOOPCANBEPARTLYCONVERTEDTOQUERY("LoopCanBePartlyConvertedToQuery", Implementability.IMPLEMENTABLE),
    LOOPVARIABLEISNEVERCHANGEDINSIDELOOP("LoopVariableIsNeverChangedInsideLoop", Implementability.IMPLEMENTABLE, Activation.WARNING),
    MEANINGLESSDEFAULTPARAMETERVALUE("MeaninglessDefaultParameterValue", Implementability.IMPLEMENTABLE, Activation.WARNING),
    MEMBERCANBEINTERNAL("MemberCanBeInternal", Implementability.IMPLEMENTABLE),
    MEMBERCANBEMADESTATIC_GLOBAL("MemberCanBeMadeStatic.Global", Implementability.IMPLEMENTABLE),
    MEMBERCANBEMADESTATIC_LOCAL("MemberCanBeMadeStatic.Local", Implementability.REJECTED),
    MEMBERCANBEPRIVATE_GLOBAL("MemberCanBePrivate.Global", Implementability.IMPLEMENTABLE),
    MEMBERCANBEPRIVATE_LOCAL("MemberCanBePrivate.Local", Implementability.IMPLEMENTABLE),
    MEMBERCANBEPROTECTED_GLOBAL("MemberCanBeProtected.Global", Implementability.IMPLEMENTABLE),
    MEMBERCANBEPROTECTED_LOCAL("MemberCanBeProtected.Local", Implementability.IMPLEMENTABLE),
    MEMBERHIDESSTATICFROMOUTERCLASS("MemberHidesStaticFromOuterClass", Implementability.IMPLEMENTABLE, Activation.WARNING),
    METHODOVERLOADWITHOPTIONALPARAMETER("MethodOverloadWithOptionalParameter", Implementability.IMPLEMENTABLE, Activation.WARNING),
    METHODSUPPORTSCANCELLATION("MethodSupportsCancellation", Implementability.IMPLEMENTABLE),
    MORESPECIFICFOREACHVARIABLETYPEAVAILABLE("MoreSpecificForeachVariableTypeAvailable", Implementability.REJECTED),
    MULTIPLENULLABLEATTRIBUTESUSAGE("MultipleNullableAttributesUsage", Implementability.REJECTED, Activation.WARNING),
    MULTIPLEORDERBY("MultipleOrderBy", Implementability.IMPLEMENTABLE, Activation.WARNING),
    MULTIPLEOUTPUTTAGS("MultipleOutputTags", Implementability.REJECTED),
    NEGATIVEEQUALITYEXPRESSION("NegativeEqualityExpression", Implementability.IMPLEMENTABLE),
    NONREADONLYFIELDINGETHASHCODE("NonReadonlyFieldInGetHashCode", Implementability.IMPLEMENTABLE),
    NONVOLATILEFIELDINDOUBLECHECKLOCKING("NonVolatileFieldInDoubleCheckLocking", Implementability.IMPLEMENTABLE),
    NOTACCESSEDFIELD_GLOBAL("NotAccessedField.Global", Implementability.IMPLEMENTABLE),
    NOTACCESSEDFIELD_LOCAL("NotAccessedField.Local", Implementability.IMPLEMENTABLE, Activation.WARNING),
    NOTACCESSEDVARIABLE("NotAccessedVariable", Implementability.IMPLEMENTABLE, Activation.WARNING),
    NOTACCESSEDVARIABLE_COMPILER("NotAccessedVariable.Compiler", Implementability.IMPLEMENTABLE),
    NOTDECLAREDINPARENTCULTURE("NotDeclaredInParentCulture", Implementability.REJECTED),
    NOTOVERRIDENINSPECIFICCULTURE("NotOverridenInSpecificCulture", Implementability.REJECTED),
    NOTRESOLVEDINTEXT("NotResolvedInText", Implementability.IMPLEMENTABLE, Activation.WARNING),
    OBJECTCREATIONASSTATEMENT("ObjectCreationAsStatement", Implementability.IMPLEMENTABLE, Activation.WARNING),
    OPERATORISCANBEUSED("OperatorIsCanBeUsed", Implementability.IMPLEMENTABLE, Activation.WARNING),
    OPTIONALPARAMETERHIERARCHYMISMATCH("OptionalParameterHierarchyMismatch", Implementability.IMPLEMENTABLE, Activation.WARNING),
    OPTIONALPARAMETERREFOUT("OptionalParameterRefOut", Implementability.IMPLEMENTABLE, Activation.WARNING),
    OUTPUTTAGREQUIRED("OutputTagRequired", Implementability.REJECTED),
    OVERRIDENWITHEMPTYVALUE("OverridenWithEmptyValue", Implementability.REJECTED),
    OVERRIDENWITHSAMEVALUE("OverridenWithSameValue", Implementability.REJECTED),
    PARAMETERHIDESMEMBER("ParameterHidesMember", Implementability.IMPLEMENTABLE, Activation.WARNING),
    PARAMETERTYPECANBEENUMERABLE_GLOBAL("ParameterTypeCanBeEnumerable.Global", Implementability.IMPLEMENTABLE),
    PARAMETERTYPECANBEENUMERABLE_LOCAL("ParameterTypeCanBeEnumerable.Local", Implementability.IMPLEMENTABLE),
    PARTIALMETHODPARAMETERNAMEMISMATCH("PartialMethodParameterNameMismatch", Implementability.IMPLEMENTABLE, Activation.WARNING),
    PARTIALMETHODWITHSINGLEPART("PartialMethodWithSinglePart", Implementability.IMPLEMENTABLE, Activation.WARNING),
    PARTIALTYPEWITHSINGLEPART("PartialTypeWithSinglePart", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POLYMORPHICFIELDLIKEEVENTINVOCATION("PolymorphicFieldLikeEventInvocation", Implementability.IMPLEMENTABLE),
    POSSIBLEASSIGNMENTTOREADONLYFIELD("PossibleAssignmentToReadonlyField", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEINFINITEINHERITANCE("PossibleInfiniteInheritance", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEINTENDEDRETHROW("PossibleIntendedRethrow", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEINTERFACEMEMBERAMBIGUITY("PossibleInterfaceMemberAmbiguity", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEINVALIDCASTEXCEPTION("PossibleInvalidCastException", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEINVALIDCASTEXCEPTIONINFOREACHLOOP("PossibleInvalidCastExceptionInForeachLoop", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEINVALIDOPERATIONEXCEPTION("PossibleInvalidOperationException", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLELOSSOFFRACTION("PossibleLossOfFraction", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEMISTAKENARGUMENT("PossibleMistakenArgument", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEMISTAKENCALLTOGETTYPE_1("PossibleMistakenCallToGetType.1", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEMISTAKENCALLTOGETTYPE_2("PossibleMistakenCallToGetType.2", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEMULTIPLEENUMERATION("PossibleMultipleEnumeration", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEMULTIPLEWRITEACCESSINDOUBLECHECKLOCKING("PossibleMultipleWriteAccessInDoubleCheckLocking", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLENULLREFERENCEEXCEPTION("PossibleNullReferenceException", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLEUNINTENDEDREFERENCECOMPARISON("PossibleUnintendedReferenceComparison", Implementability.IMPLEMENTABLE, Activation.WARNING),
    POSSIBLYMISTAKENUSEOFPARAMSMETHOD("PossiblyMistakenUseOfParamsMethod", Implementability.IMPLEMENTABLE, Activation.WARNING),
    PRIVATEFIELDCANBECONVERTEDTOLOCALVARIABLE("PrivateFieldCanBeConvertedToLocalVariable", Implementability.IMPLEMENTABLE, Activation.WARNING),
    PROPERTYNOTRESOLVED("PropertyNotResolved", Implementability.REJECTED),
    PUBLICCONSTRUCTORINABSTRACTCLASS("PublicConstructorInAbstractClass", Implementability.IMPLEMENTABLE),
    PUREATTRIBUTEONVOIDMETHOD("PureAttributeOnVoidMethod", Implementability.IMPLEMENTABLE, Activation.WARNING),
    READACCESSINDOUBLECHECKLOCKING("ReadAccessInDoubleCheckLocking", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTANONYMOUSTYPEPROPERTYNAME("RedundantAnonymousTypePropertyName", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTARGUMENTDEFAULTVALUE("RedundantArgumentDefaultValue", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTARGUMENTNAME("RedundantArgumentName", Implementability.IMPLEMENTABLE),
    REDUNDANTARGUMENTNAMEFORLITERALEXPRESSION("RedundantArgumentNameForLiteralExpression", Implementability.IMPLEMENTABLE),
    REDUNDANTARRAYCREATIONEXPRESSION("RedundantArrayCreationExpression", Implementability.IMPLEMENTABLE),
    REDUNDANTASSIGNMENT("RedundantAssignment", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTATTRIBUTEPARENTHESES("RedundantAttributeParentheses", Implementability.IMPLEMENTABLE),
    REDUNDANTBASECONSTRUCTORCALL("RedundantBaseConstructorCall", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTBASEQUALIFIER("RedundantBaseQualifier", Implementability.REJECTED, Activation.WARNING),
    REDUNDANTBOOLCOMPARE("RedundantBoolCompare", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTCASELABEL("RedundantCaseLabel", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTCAST("RedundantCast", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTCAST_0("RedundantCast.0", Implementability.REJECTED, Activation.WARNING),
    REDUNDANTCATCHCLAUSE("RedundantCatchClause", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTCHECKBEFOREASSIGNMENT("RedundantCheckBeforeAssignment", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTCOLLECTIONINITIALIZERELEMENTBRACES("RedundantCollectionInitializerElementBraces", Implementability.IMPLEMENTABLE),
    REDUNDANTCOMPARISONWITHNULL("RedundantComparisonWithNull", Implementability.IMPLEMENTABLE),
    REDUNDANTDEFAULTFIELDINITIALIZER("RedundantDefaultFieldInitializer", Implementability.IMPLEMENTABLE),
    REDUNDANTDELEGATECREATION("RedundantDelegateCreation", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTEMPTYDEFAULTSWITCHBRANCH("RedundantEmptyDefaultSwitchBranch", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTEMPTYFINALLYBLOCK("RedundantEmptyFinallyBlock", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTEMPTYOBJECTCREATIONARGUMENTLIST("RedundantEmptyObjectCreationArgumentList", Implementability.IMPLEMENTABLE),
    REDUNDANTEMPTYOBJECTORCOLLECTIONINITIALIZER("RedundantEmptyObjectOrCollectionInitializer", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTENUMERABLECASTCALL("RedundantEnumerableCastCall", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTEXPLICITARRAYCREATION("RedundantExplicitArrayCreation", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTEXPLICITARRAYSIZE("RedundantExplicitArraySize", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTEXPLICITNULLABLECREATION("RedundantExplicitNullableCreation", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTEXTENDSLISTENTRY("RedundantExtendsListEntry", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTIFELSEBLOCK("RedundantIfElseBlock", Implementability.IMPLEMENTABLE),
    REDUNDANTJUMPSTATEMENT("RedundantJumpStatement", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTLAMBDAPARAMETERTYPE("RedundantLambdaParameterType", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTLAMBDASIGNATUREPARENTHESES("RedundantLambdaSignatureParentheses", Implementability.REJECTED, Activation.WARNING),
    REDUNDANTLOGICALCONDITIONALEXPRESSIONOPERAND("RedundantLogicalConditionalExpressionOperand", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTNAMEQUALIFIER("RedundantNameQualifier", Implementability.REJECTED, Activation.WARNING),
    REDUNDANTOVERLOAD_GLOBAL("RedundantOverload.Global", Implementability.IMPLEMENTABLE),
    REDUNDANTOVERLOAD_LOCAL("RedundantOverload.Local", Implementability.IMPLEMENTABLE),
    REDUNDANTOVERRIDENMEMBER("RedundantOverridenMember", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTPARAMS("RedundantParams", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTSTRINGFORMATCALL("RedundantStringFormatCall", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTSTRINGTOCHARARRAYCALL("RedundantStringToCharArrayCall", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTTERNARYEXPRESSION("RedundantTernaryExpression", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTTHISQUALIFIER("RedundantThisQualifier", Implementability.IMPLEMENTABLE),
    REDUNDANTTOSTRINGCALL("RedundantToStringCall", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTTOSTRINGCALLFORVALUETYPE("RedundantToStringCallForValueType", Implementability.IMPLEMENTABLE),
    REDUNDANTUNSAFECONTEXT("RedundantUnsafeContext", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REDUNDANTUSINGDIRECTIVE("RedundantUsingDirective", Implementability.REJECTED, Activation.WARNING),
    REFERENCEEQUALSWITHVALUETYPE("ReferenceEqualsWithValueType", Implementability.IMPLEMENTABLE, Activation.WARNING),
    REMOVECONSTUCTORINVOCATION("RemoveConstuctorInvocation", Implementability.IMPLEMENTABLE),
    REMOVEREDUNDANTORSTATEMENT_FALSE("RemoveRedundantOrStatement.False", Implementability.IMPLEMENTABLE),
    REMOVEREDUNDANTORSTATEMENT_TRUE("RemoveRedundantOrStatement.True", Implementability.IMPLEMENTABLE),
    REMOVETOLIST_1("RemoveToList.1", Implementability.IMPLEMENTABLE),
    REMOVETOLIST_2("RemoveToList.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHFIRSTORDEFAULT_1("ReplaceWithFirstOrDefault.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHFIRSTORDEFAULT_2("ReplaceWithFirstOrDefault.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHFIRSTORDEFAULT_3("ReplaceWithFirstOrDefault.3", Implementability.IMPLEMENTABLE),
    REPLACEWITHFIRSTORDEFAULT_4("ReplaceWithFirstOrDefault.4", Implementability.IMPLEMENTABLE),
    REPLACEWITHLASTORDEFAULT_1("ReplaceWithLastOrDefault.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHLASTORDEFAULT_2("ReplaceWithLastOrDefault.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHLASTORDEFAULT_3("ReplaceWithLastOrDefault.3", Implementability.IMPLEMENTABLE),
    REPLACEWITHLASTORDEFAULT_4("ReplaceWithLastOrDefault.4", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_1("ReplaceWithOfType.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_2("ReplaceWithOfType.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_3("ReplaceWithOfType.3", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_ANY_1("ReplaceWithOfType.Any.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_ANY_2("ReplaceWithOfType.Any.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_COUNT_1("ReplaceWithOfType.Count.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_COUNT_2("ReplaceWithOfType.Count.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_FIRST_1("ReplaceWithOfType.First.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_FIRST_2("ReplaceWithOfType.First.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_FIRSTORDEFAULT_1("ReplaceWithOfType.FirstOrDefault.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_FIRSTORDEFAULT_2("ReplaceWithOfType.FirstOrDefault.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_LAST_1("ReplaceWithOfType.Last.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_LAST_2("ReplaceWithOfType.Last.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_LASTORDEFAULT_1("ReplaceWithOfType.LastOrDefault.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_LASTORDEFAULT_2("ReplaceWithOfType.LastOrDefault.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_LONGCOUNT("ReplaceWithOfType.LongCount", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_SINGLE_1("ReplaceWithOfType.Single.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_SINGLE_2("ReplaceWithOfType.Single.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_SINGLEORDEFAULT_1("ReplaceWithOfType.SingleOrDefault.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_SINGLEORDEFAULT_2("ReplaceWithOfType.SingleOrDefault.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHOFTYPE_WHERE("ReplaceWithOfType.Where", Implementability.IMPLEMENTABLE),
    REPLACEWITHSIMPLEASSIGNMENT_FALSE("ReplaceWithSimpleAssignment.False", Implementability.IMPLEMENTABLE),
    REPLACEWITHSIMPLEASSIGNMENT_TRUE("ReplaceWithSimpleAssignment.True", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLEASSIGNMENT_FALSE("ReplaceWithSingleAssignment.False", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLEASSIGNMENT_TRUE("ReplaceWithSingleAssignment.True", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLECALLTOANY("ReplaceWithSingleCallToAny", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLECALLTOCOUNT("ReplaceWithSingleCallToCount", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLECALLTOFIRST("ReplaceWithSingleCallToFirst", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLECALLTOFIRSTORDEFAULT("ReplaceWithSingleCallToFirstOrDefault", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLECALLTOLAST("ReplaceWithSingleCallToLast", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLECALLTOLASTORDEFAULT("ReplaceWithSingleCallToLastOrDefault", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLECALLTOSINGLE("ReplaceWithSingleCallToSingle", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLECALLTOSINGLEORDEFAULT("ReplaceWithSingleCallToSingleOrDefault", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLEORDEFAULT_1("ReplaceWithSingleOrDefault.1", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLEORDEFAULT_2("ReplaceWithSingleOrDefault.2", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLEORDEFAULT_3("ReplaceWithSingleOrDefault.3", Implementability.IMPLEMENTABLE),
    REPLACEWITHSINGLEORDEFAULT_4("ReplaceWithSingleOrDefault.4", Implementability.IMPLEMENTABLE),
    REPLACEWITHSTRINGISNULLOREMPTY("ReplaceWithStringIsNullOrEmpty", Implementability.IMPLEMENTABLE),
    REQUIREDBASETYPESCONFLICT("RequiredBaseTypesConflict", Implementability.REJECTED, Activation.WARNING),
    REQUIREDBASETYPESDIRECTCONFLICT("RequiredBaseTypesDirectConflict", Implementability.REJECTED, Activation.WARNING),
    REQUIREDBASETYPESISNOTINHERITED("RequiredBaseTypesIsNotInherited", Implementability.REJECTED, Activation.WARNING),
    RESOURCEITEMNOTRESOLVED("ResourceItemNotResolved", Implementability.IMPLEMENTABLE),
    RESOURCENOTRESOLVED("ResourceNotResolved", Implementability.IMPLEMENTABLE),
    RESXNOTRESOLVED("ResxNotResolved", Implementability.REJECTED),
    RETURNTYPECANBEENUMERABLE_GLOBAL("ReturnTypeCanBeEnumerable.Global", Implementability.IMPLEMENTABLE),
    RETURNTYPECANBEENUMERABLE_LOCAL("ReturnTypeCanBeEnumerable.Local", Implementability.IMPLEMENTABLE),
    RETURNVALUEOFPUREMETHODISNOTUSED("ReturnValueOfPureMethodIsNotUsed", Implementability.IMPLEMENTABLE, Activation.WARNING),
    SEALEDMEMBERINSEALEDCLASS("SealedMemberInSealedClass", Implementability.IMPLEMENTABLE, Activation.WARNING),
    SIMILARANONYMOUSTYPENEARBY("SimilarAnonymousTypeNearby", Implementability.IMPLEMENTABLE),
    SIMPLIFYCONDITIONALOPERATOR("SimplifyConditionalOperator", Implementability.IMPLEMENTABLE),
    SIMPLIFYCONDITIONALTERNARYEXPRESSION("SimplifyConditionalTernaryExpression", Implementability.IMPLEMENTABLE),
    SIMPLIFYLINQEXPRESSION("SimplifyLinqExpression", Implementability.IMPLEMENTABLE),
    SPECIFYACULTUREINSTRINGCONVERSIONEXPLICITLY("SpecifyACultureInStringConversionExplicitly", Implementability.IMPLEMENTABLE, Activation.WARNING),
    SPECIFYSTRINGCOMPARISON("SpecifyStringComparison", Implementability.IMPLEMENTABLE),
    STATICFIELDINGENERICTYPE("StaticFieldInGenericType", Implementability.IMPLEMENTABLE),
    STATICFIELDINITIALIZERSREFERESTOFIELDBELOW("StaticFieldInitializersReferesToFieldBelow", Implementability.IMPLEMENTABLE),
    STRINGCOMPAREISCULTURESPECIFIC_1("StringCompareIsCultureSpecific.1", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGCOMPAREISCULTURESPECIFIC_2("StringCompareIsCultureSpecific.2", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGCOMPAREISCULTURESPECIFIC_3("StringCompareIsCultureSpecific.3", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGCOMPAREISCULTURESPECIFIC_4("StringCompareIsCultureSpecific.4", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGCOMPAREISCULTURESPECIFIC_5("StringCompareIsCultureSpecific.5", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGCOMPAREISCULTURESPECIFIC_6("StringCompareIsCultureSpecific.6", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGCOMPARETOISCULTURESPECIFIC("StringCompareToIsCultureSpecific", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGENDSWITHISCULTURESPECIFIC("StringEndsWithIsCultureSpecific", Implementability.IMPLEMENTABLE),
    STRINGINDEXOFISCULTURESPECIFIC_1("StringIndexOfIsCultureSpecific.1", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGINDEXOFISCULTURESPECIFIC_2("StringIndexOfIsCultureSpecific.2", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGINDEXOFISCULTURESPECIFIC_3("StringIndexOfIsCultureSpecific.3", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGLASTINDEXOFISCULTURESPECIFIC_1("StringLastIndexOfIsCultureSpecific.1", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGLASTINDEXOFISCULTURESPECIFIC_2("StringLastIndexOfIsCultureSpecific.2", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGLASTINDEXOFISCULTURESPECIFIC_3("StringLastIndexOfIsCultureSpecific.3", Implementability.IMPLEMENTABLE, Activation.WARNING),
    STRINGSTARTSWITHISCULTURESPECIFIC("StringStartsWithIsCultureSpecific", Implementability.IMPLEMENTABLE),
    SUGGESTBASETYPEFORPARAMETER("SuggestBaseTypeForParameter", Implementability.REJECTED),
    SUGGESTUSEVARKEYWORDEVERYWHERE("SuggestUseVarKeywordEverywhere", Implementability.IMPLEMENTABLE),
    SUGGESTUSEVARKEYWORDEVIDENT("SuggestUseVarKeywordEvident", Implementability.IMPLEMENTABLE),
    SUSPICIOUSTYPECONVERSION_GLOBAL("SuspiciousTypeConversion.Global", Implementability.IMPLEMENTABLE, Activation.WARNING),
    TAILRECURSIVECALL("TailRecursiveCall", Implementability.IMPLEMENTABLE),
    TASKSNOTLOADED("TasksNotLoaded", Implementability.REJECTED),
    THREADSTATICATINSTANCEFIELD("ThreadStaticAtInstanceField", Implementability.IMPLEMENTABLE, Activation.WARNING),
    THREADSTATICFIELDHASINITIALIZER("ThreadStaticFieldHasInitializer", Implementability.IMPLEMENTABLE, Activation.WARNING),
    TOOWIDELOCALVARIABLESCOPE("TooWideLocalVariableScope", Implementability.IMPLEMENTABLE),
    TRYSTATEMENTSCANBEMERGED("TryStatementsCanBeMerged", Implementability.IMPLEMENTABLE),
    TYPEPARAMETERCANBEVARIANT("TypeParameterCanBeVariant", Implementability.IMPLEMENTABLE),
    UKNOWNTASKATTRIBUTE("UknownTaskAttribute", Implementability.REJECTED),
    UNASSIGNEDFIELD_GLOBAL("UnassignedField.Global", Implementability.IMPLEMENTABLE),
    UNASSIGNEDFIELD_LOCAL("UnassignedField.Local", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNASSIGNEDREADONLYFIELD("UnassignedReadonlyField", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNKNOWNITEMGROUP("UnknownItemGroup", Implementability.REJECTED),
    UNKNOWNMETADATA("UnknownMetadata", Implementability.REJECTED),
    UNKNOWNOUTPUTPARAMETER("UnknownOutputParameter", Implementability.REJECTED),
    UNKNOWNPROPERTY("UnknownProperty", Implementability.REJECTED),
    UNKNOWNTARGET("UnknownTarget", Implementability.REJECTED),
    UNKNOWNTASK("UnknownTask", Implementability.REJECTED),
    UNREACHABLECODE("UnreachableCode", Implementability.REJECTED, Activation.WARNING),
    UNSUPPORTEDREQUIREDBASETYPE("UnsupportedRequiredBaseType", Implementability.REJECTED, Activation.WARNING),
    UNUSEDANONYMOUSMETHODSIGNATURE("UnusedAnonymousMethodSignature", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNUSEDAUTOPROPERTYACCESSOR_GLOBAL("UnusedAutoPropertyAccessor.Global", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNUSEDAUTOPROPERTYACCESSOR_LOCAL("UnusedAutoPropertyAccessor.Local", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNUSEDIMPORTCLAUSE("UnusedImportClause", Implementability.REJECTED),
    UNUSEDLABEL("UnusedLabel", Implementability.REJECTED, Activation.WARNING),
    UNUSEDMEMBER_GLOBAL("UnusedMember.Global", Implementability.IMPLEMENTABLE),
    UNUSEDMEMBER_LOCAL("UnusedMember.Local", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNUSEDMEMBERHIEARCHY_GLOBAL("UnusedMemberHiearchy.Global", Implementability.IMPLEMENTABLE),
    UNUSEDMEMBERHIEARCHY_LOCAL("UnusedMemberHiearchy.Local", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNUSEDMEMBERINSUPER_GLOBAL("UnusedMemberInSuper.Global", Implementability.IMPLEMENTABLE),
    UNUSEDMEMBERINSUPER_LOCAL("UnusedMemberInSuper.Local", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNUSEDMETHODRETURNVALUE_GLOBAL("UnusedMethodReturnValue.Global", Implementability.IMPLEMENTABLE),
    UNUSEDMETHODRETURNVALUE_LOCAL("UnusedMethodReturnValue.Local", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNUSEDPARAMETER("UnusedParameter", Implementability.IMPLEMENTABLE),
    UNUSEDPARAMETER_GLOBAL("UnusedParameter.Global", Implementability.IMPLEMENTABLE),
    UNUSEDPARAMETER_LOCAL("UnusedParameter.Local", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNUSEDPROPERTY("UnusedProperty", Implementability.IMPLEMENTABLE),
    UNUSEDTYPEPARAMETER("UnusedTypeParameter", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNUSEDVARIABLE("UnusedVariable", Implementability.IMPLEMENTABLE, Activation.WARNING),
    UNUSEDVARIABLE_COMPILER("UnusedVariable.Compiler", Implementability.REJECTED),
    USAGEOFDEFINITELYUNASSIGNEDVALUE("UsageOfDefinitelyUnassignedValue", Implementability.IMPLEMENTABLE),
    USAGEOFPOSSIBLYUNASSIGNEDVALUE("UsageOfPossiblyUnassignedValue", Implementability.IMPLEMENTABLE),
    USEARRAYCREATIONEXPRESSION_1("UseArrayCreationExpression.1", Implementability.IMPLEMENTABLE),
    USEARRAYCREATIONEXPRESSION_2("UseArrayCreationExpression.2", Implementability.IMPLEMENTABLE),
    USEINDEXEDPROPERTY("UseIndexedProperty", Implementability.IMPLEMENTABLE),
    USEISOPERATOR_1("UseIsOperator.1", Implementability.IMPLEMENTABLE),
    USEISOPERATOR_2("UseIsOperator.2", Implementability.IMPLEMENTABLE),
    USEMETHODANY_0("UseMethodAny.0", Implementability.IMPLEMENTABLE),
    USEMETHODANY_1("UseMethodAny.1", Implementability.IMPLEMENTABLE),
    USEMETHODANY_2("UseMethodAny.2", Implementability.IMPLEMENTABLE),
    USEMETHODANY_3("UseMethodAny.3", Implementability.IMPLEMENTABLE),
    USEMETHODANY_4("UseMethodAny.4", Implementability.IMPLEMENTABLE),
    USEMETHODISINSTANCEOFTYPE("UseMethodIsInstanceOfType", Implementability.IMPLEMENTABLE),
    USEOBJECTORCOLLECTIONINITIALIZER("UseObjectOrCollectionInitializer", Implementability.IMPLEMENTABLE),
    VALUEPARAMETERNOTUSED("ValueParameterNotUsed", Implementability.IMPLEMENTABLE, Activation.WARNING),
    VIRTUALMEMBERNEVEROVERRIDEN_GLOBAL("VirtualMemberNeverOverriden.Global", Implementability.IMPLEMENTABLE),
    VIRTUALMEMBERNEVEROVERRIDEN_LOCAL("VirtualMemberNeverOverriden.Local", Implementability.IMPLEMENTABLE),
    WRONGMETADATAUSE("WrongMetadataUse", Implementability.REJECTED),
    STRUCTURALSEARCH("StructuralSearch", Implementability.IMPLEMENTABLE),
    CODESMELL("CodeSmell", Implementability.IMPLEMENTABLE),
    CSHARPERRORS("CSharpErrors", Implementability.IMPLEMENTABLE);


    private Implementability implementability;
    private Activation activation;
    private String title;

    ReSharperRule(String title, Implementability implementability) {
      this.implementability = implementability;
      this.title = title;
    }

    ReSharperRule(String title, Implementability implementability, Activation activation) {
      this.activation = activation;
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
    @Override
    public String getLevel() {
      if (activation == null || activation == Activation.UNKNOWN) {
        return "";
      }
      return this.activation.name().toLowerCase();
    }

  }

}
