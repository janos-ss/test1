/*
 * Copyright (C) 2014 SonarSource SA
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

  public enum ReSharperRule implements CodingStandardRule {
    ACCESSTODISPOSEDCLOSURE("AccessToDisposedClosure", Implementability.IMPLEMENTABLE),
    ACCESSTOFOREACHVARIABLEINCLOSURE("AccessToForEachVariableInClosure", Implementability.IMPLEMENTABLE),
    ACCESSTOMODIFIEDCLOSURE("AccessToModifiedClosure", Implementability.IMPLEMENTABLE),
    ACCESSTOSTATICMEMBERVIADERIVEDTYPE("AccessToStaticMemberViaDerivedType", Implementability.REJECTED),
    ANNOTATIONCONFLICTINHIERARCHY("AnnotationConflictInHierarchy", Implementability.IMPLEMENTABLE),
    ANNOTATIONREDUNDANCEATVALUETYPE("AnnotationRedundanceAtValueType", Implementability.IMPLEMENTABLE),
    ANNOTATIONREDUNDANCEINHIERARCHY("AnnotationRedundanceInHierarchy", Implementability.IMPLEMENTABLE),
    ASSIGNEDVALUEISNEVERUSED("AssignedValueIsNeverUsed", Implementability.IMPLEMENTABLE),
    ASSIGNNULLTONOTNULLATTRIBUTE("AssignNullToNotNullAttribute", Implementability.IMPLEMENTABLE),
    BASEMEMBERHASPARAMS("BaseMemberHasParams", Implementability.IMPLEMENTABLE),
    BASEMETHODCALLWITHDEFAULTPARAMETER("BaseMethodCallWithDefaultParameter", Implementability.IMPLEMENTABLE),
    BASEOBJECTEQUALSISOBJECTEQUALS("BaseObjectEqualsIsObjectEquals", Implementability.IMPLEMENTABLE),
    BASEOBJECTGETHASHCODECALLINGETHASHCODE("BaseObjectGetHashCodeCallInGetHashCode", Implementability.IMPLEMENTABLE),
    BITWISEOPERATORONENUMWITHOUTFLAGS("BitwiseOperatorOnEnumWithoutFlags", Implementability.IMPLEMENTABLE),
    CANBEREPLACEDWITHTRYCASTANDCHECKFORNULL("CanBeReplacedWithTryCastAndCheckForNull", Implementability.IMPLEMENTABLE),
    CANNOTAPPLYEQUALITYOPERATORTOTYPE("CannotApplyEqualityOperatorToType", Implementability.IMPLEMENTABLE),
    CHECKFORREFERENCEEQUALITYINSTEAD_1("CheckForReferenceEqualityInstead.1", Implementability.IMPLEMENTABLE),
    CHECKFORREFERENCEEQUALITYINSTEAD_2("CheckForReferenceEqualityInstead.2", Implementability.IMPLEMENTABLE),
    CHECKNAMESPACE("CheckNamespace", Implementability.IMPLEMENTABLE),
    CLASSCANBESEALED_GLOBAL("ClassCanBeSealed.Global", Implementability.IMPLEMENTABLE),
    CLASSCANBESEALED_LOCAL("ClassCanBeSealed.Local", Implementability.IMPLEMENTABLE),
    CLASSCANNOTBEINSTANTIATED("ClassCannotBeInstantiated", Implementability.IMPLEMENTABLE),
    CLASSNEVERINSTANTIATED_GLOBAL("ClassNeverInstantiated.Global", Implementability.IMPLEMENTABLE),
    CLASSNEVERINSTANTIATED_LOCAL("ClassNeverInstantiated.Local", Implementability.IMPLEMENTABLE),
    CLASSWITHVIRTUALMEMBERSNEVERINHERITED_GLOBAL("ClassWithVirtualMembersNeverInherited.Global", Implementability.IMPLEMENTABLE),
    CLASSWITHVIRTUALMEMBERSNEVERINHERITED_LOCAL("ClassWithVirtualMembersNeverInherited.Local", Implementability.IMPLEMENTABLE),
    CLEARATTRIBUTEISOBSOLETE("ClearAttributeIsObsolete", Implementability.IMPLEMENTABLE),
    CLEARATTRIBUTEISOBSOLETE_ALL("ClearAttributeIsObsolete.All", Implementability.IMPLEMENTABLE),
    COMPARENONCONSTRAINEDGENERICWITHNULL("CompareNonConstrainedGenericWithNull", Implementability.IMPLEMENTABLE),
    COMPAREOFFLOATSBYEQUALITYOPERATOR("CompareOfFloatsByEqualityOperator", Implementability.IMPLEMENTABLE),
    CONDITIONALTERNARYEQUALBRANCH("ConditionalTernaryEqualBranch", Implementability.IMPLEMENTABLE),
    CONDITIONISALWAYSTRUEORFALSE("ConditionIsAlwaysTrueOrFalse", Implementability.IMPLEMENTABLE),
    CONSTANTNULLCOALESCINGCONDITION("ConstantNullCoalescingCondition", Implementability.IMPLEMENTABLE),
    CONSTRUCTORINITIALIZERLOOP("ConstructorInitializerLoop", Implementability.REJECTED),
    CONTRACTANNOTATIONNOTPARSED("ContractAnnotationNotParsed", Implementability.IMPLEMENTABLE),
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
    COVARIANTARRAYCONVERSION("CoVariantArrayConversion", Implementability.IMPLEMENTABLE),
    DECLARATIONISEMPTY("DeclarationIsEmpty", Implementability.IMPLEMENTABLE),
    DEFAULTVALUEATTRIBUTEFOROPTIONALPARAMETER("DefaultValueAttributeForOptionalParameter", Implementability.IMPLEMENTABLE),
    DELEGATESUBTRACTION("DelegateSubtraction", Implementability.IMPLEMENTABLE),
    DONOTCALLOVERRIDABLEMETHODSINCONSTRUCTOR("DoNotCallOverridableMethodsInConstructor", Implementability.IMPLEMENTABLE),
    DOUBLENEGATIONOPERATOR("DoubleNegationOperator", Implementability.IMPLEMENTABLE),
    DUPLICATERESOURCE("DuplicateResource", Implementability.IMPLEMENTABLE),
    DYNAMICSHIFTRIGHTOPISNOTINT("DynamicShiftRightOpIsNotInt", Implementability.IMPLEMENTABLE),
    EMPTYCONSTRUCTOR("EmptyConstructor", Implementability.IMPLEMENTABLE),
    EMPTYDESTRUCTOR("EmptyDestructor", Implementability.IMPLEMENTABLE),
    EMPTYEMBEDDEDSTATEMENT("EmptyEmbeddedStatement", Implementability.IMPLEMENTABLE),
    EMPTYFORSTATEMENT("EmptyForStatement", Implementability.IMPLEMENTABLE),
    EMPTYGENERALCATCHCLAUSE("EmptyGeneralCatchClause", Implementability.IMPLEMENTABLE),
    EMPTYNAMESPACE("EmptyNamespace", Implementability.IMPLEMENTABLE),
    EMPTYSTATEMENT("EmptyStatement", Implementability.IMPLEMENTABLE),
    ENUMERABLESUMINEXPLICITUNCHECKEDCONTEXT("EnumerableSumInExplicitUncheckedContext", Implementability.IMPLEMENTABLE),
    ENUMUNDERLYINGTYPEISINT("EnumUnderlyingTypeIsInt", Implementability.IMPLEMENTABLE),
    EQUALEXPRESSIONCOMPARISON("EqualExpressionComparison", Implementability.IMPLEMENTABLE),
    EVENTNEVERINVOKED("EventNeverInvoked", Implementability.IMPLEMENTABLE),
    EVENTNEVERINVOKED_GLOBAL("EventNeverInvoked.Global", Implementability.IMPLEMENTABLE),
    EVENTNEVERSUBSCRIBEDTO_GLOBAL("EventNeverSubscribedTo.Global", Implementability.IMPLEMENTABLE),
    EVENTNEVERSUBSCRIBEDTO_LOCAL("EventNeverSubscribedTo.Local", Implementability.IMPLEMENTABLE),
    EVENTUNSUBSCRIPTIONVIAANONYMOUSDELEGATE("EventUnsubscriptionViaAnonymousDelegate", Implementability.IMPLEMENTABLE),
    EXPLICITCALLERINFOARGUMENT("ExplicitCallerInfoArgument", Implementability.IMPLEMENTABLE),
    EXPRESSIONISALWAYSNULL("ExpressionIsAlwaysNull", Implementability.IMPLEMENTABLE),
    FIELDCANBEMADEREADONLY_GLOBAL("FieldCanBeMadeReadOnly.Global", Implementability.IMPLEMENTABLE),
    FIELDCANBEMADEREADONLY_LOCAL("FieldCanBeMadeReadOnly.Local", Implementability.IMPLEMENTABLE),
    FORCANBECONVERTEDTOFOREACH("ForCanBeConvertedToForeach", Implementability.IMPLEMENTABLE),
    FORMATSTRINGPLACEHOLDERSMISMATCH("FormatStringPlaceholdersMismatch", Implementability.IMPLEMENTABLE),
    FORMATSTRINGPROBLEM("FormatStringProblem", Implementability.IMPLEMENTABLE),
    FORSTATEMENTCONDITIONISTRUE("ForStatementConditionIsTrue", Implementability.IMPLEMENTABLE),
    FUNCTIONNEVERRETURNS("FunctionNeverReturns", Implementability.IMPLEMENTABLE),
    FUNCTIONRECURSIVEONALLPATHS("FunctionRecursiveOnAllPaths", Implementability.IMPLEMENTABLE),
    GCSUPPRESSFINALIZEFORTYPEWITHOUTDESTRUCTOR("GCSuppressFinalizeForTypeWithoutDestructor", Implementability.IMPLEMENTABLE),
    HEURISTICALLYUNREACHABLECODE("HeuristicallyUnreachableCode", Implementability.IMPLEMENTABLE),
    HEURISTICUNREACHABLECODE("HeuristicUnreachableCode", Implementability.IMPLEMENTABLE),
    HEXCOLORVALUEWITHALPHA("HexColorValueWithAlpha", Implementability.IMPLEMENTABLE),
    IMPLICITLYCAPTUREDCLOSURE("ImplicitlyCapturedClosure", Implementability.IMPLEMENTABLE),
    IMPUREMETHODCALLONREADONLYVALUEFIELD("ImpureMethodCallOnReadonlyValueField", Implementability.IMPLEMENTABLE),
    INACTIVEPREPROCESSORBRANCH("InactivePreprocessorBranch", Implementability.IMPLEMENTABLE),
    INCONSISTENTFUNCTIONRETURNS("InconsistentFunctionReturns", Implementability.IMPLEMENTABLE),
    INCONSISTENTNAMING("InconsistentNaming", Implementability.IMPLEMENTABLE),
    INTRODUCEOPTIONALPARAMETERS_GLOBAL("IntroduceOptionalParameters.Global", Implementability.IMPLEMENTABLE),
    INTRODUCEOPTIONALPARAMETERS_LOCAL("IntroduceOptionalParameters.Local", Implementability.IMPLEMENTABLE),
    INVALIDATTRIBUTEVALUE("InvalidAttributeValue", Implementability.IMPLEMENTABLE),
    INVALIDTASKELEMENT("InvalidTaskElement", Implementability.IMPLEMENTABLE),
    INVALIDVALUE("InvalidValue", Implementability.IMPLEMENTABLE),
    INVALIDVALUETYPE("InvalidValueType", Implementability.IMPLEMENTABLE),
    INVERTCONDITION_1("InvertCondition.1", Implementability.IMPLEMENTABLE),
    INVERTIF("InvertIf", Implementability.IMPLEMENTABLE),
    INVOCATIONISSKIPPED("InvocationIsSkipped", Implementability.IMPLEMENTABLE),
    INVOKEASEXTENSIONMETHOD("InvokeAsExtensionMethod", Implementability.IMPLEMENTABLE),
    ITERATORMETHODRESULTISIGNORED("IteratorMethodResultIsIgnored", Implementability.IMPLEMENTABLE),
    JOINDECLARATIONANDINITIALIZER("JoinDeclarationAndInitializer", Implementability.IMPLEMENTABLE),
    JUMPMUSTBEINLOOP("JumpMustBeInLoop", Implementability.IMPLEMENTABLE),
    LABELORSEMICOLONEXPECTED("LabelOrSemicolonExpected", Implementability.IMPLEMENTABLE),
    LOCALIZABLEELEMENT("LocalizableElement", Implementability.IMPLEMENTABLE),
    LOCALVARIABLEHIDESMEMBER("LocalVariableHidesMember", Implementability.IMPLEMENTABLE),
    LONGLITERALENDINGLOWERL("LongLiteralEndingLowerL", Implementability.IMPLEMENTABLE),
    LOOPCANBECONVERTEDTOQUERY("LoopCanBeConvertedToQuery", Implementability.IMPLEMENTABLE),
    LOOPCANBEPARTLYCONVERTEDTOQUERY("LoopCanBePartlyConvertedToQuery", Implementability.IMPLEMENTABLE),
    LOOPVARIABLEISNEVERCHANGEDINSIDELOOP("LoopVariableIsNeverChangedInsideLoop", Implementability.IMPLEMENTABLE),
    MEANINGLESSDEFAULTPARAMETERVALUE("MeaninglessDefaultParameterValue", Implementability.IMPLEMENTABLE),
    MEMBERCANBEINTERNAL("MemberCanBeInternal", Implementability.IMPLEMENTABLE),
    MEMBERCANBEMADESTATIC_GLOBAL("MemberCanBeMadeStatic.Global", Implementability.IMPLEMENTABLE),
    MEMBERCANBEMADESTATIC_LOCAL("MemberCanBeMadeStatic.Local", Implementability.REJECTED),
    MEMBERCANBEPRIVATE_GLOBAL("MemberCanBePrivate.Global", Implementability.IMPLEMENTABLE),
    MEMBERCANBEPRIVATE_LOCAL("MemberCanBePrivate.Local", Implementability.IMPLEMENTABLE),
    MEMBERCANBEPROTECTED_GLOBAL("MemberCanBeProtected.Global", Implementability.IMPLEMENTABLE),
    MEMBERCANBEPROTECTED_LOCAL("MemberCanBeProtected.Local", Implementability.IMPLEMENTABLE),
    MEMBERHIDESSTATICFROMOUTERCLASS("MemberHidesStaticFromOuterClass", Implementability.IMPLEMENTABLE),
    METHODOVERLOADWITHOPTIONALPARAMETER("MethodOverloadWithOptionalParameter", Implementability.IMPLEMENTABLE),
    METHODSUPPORTSCANCELLATION("MethodSupportsCancellation", Implementability.IMPLEMENTABLE),
    MORESPECIFICFOREACHVARIABLETYPEAVAILABLE("MoreSpecificForeachVariableTypeAvailable", Implementability.REJECTED),
    MULTIPLENULLABLEATTRIBUTESUSAGE("MultipleNullableAttributesUsage", Implementability.IMPLEMENTABLE),
    MULTIPLEORDERBY("MultipleOrderBy", Implementability.IMPLEMENTABLE),
    MULTIPLEOUTPUTTAGS("MultipleOutputTags", Implementability.IMPLEMENTABLE),
    NEGATIVEEQUALITYEXPRESSION("NegativeEqualityExpression", Implementability.IMPLEMENTABLE),
    NONREADONLYFIELDINGETHASHCODE("NonReadonlyFieldInGetHashCode", Implementability.IMPLEMENTABLE),
    NONVOLATILEFIELDINDOUBLECHECKLOCKING("NonVolatileFieldInDoubleCheckLocking", Implementability.IMPLEMENTABLE),
    NOTACCESSEDFIELD_GLOBAL("NotAccessedField.Global", Implementability.IMPLEMENTABLE),
    NOTACCESSEDFIELD_LOCAL("NotAccessedField.Local", Implementability.IMPLEMENTABLE),
    NOTACCESSEDVARIABLE("NotAccessedVariable", Implementability.IMPLEMENTABLE),
    NOTACCESSEDVARIABLE_COMPILER("NotAccessedVariable.Compiler", Implementability.IMPLEMENTABLE),
    NOTALLPATHSRETURNVALUE("NotAllPathsReturnValue", Implementability.IMPLEMENTABLE),
    NOTASSIGNEDOUTPARAMETER("NotAssignedOutParameter", Implementability.IMPLEMENTABLE),
    NOTDECLAREDINPARENTCULTURE("NotDeclaredInParentCulture", Implementability.IMPLEMENTABLE),
    NOTOVERRIDENINSPECIFICCULTURE("NotOverridenInSpecificCulture", Implementability.IMPLEMENTABLE),
    NOTRESOLVED("NotResolved", Implementability.IMPLEMENTABLE),
    NOTRESOLVEDINTEXT("NotResolvedInText", Implementability.IMPLEMENTABLE),
    OBJECTCREATIONASSTATEMENT("ObjectCreationAsStatement", Implementability.IMPLEMENTABLE),
    OPERATORISCANBEUSED("OperatorIsCanBeUsed", Implementability.IMPLEMENTABLE),
    OPTIONALPARAMETERHIERARCHYMISMATCH("OptionalParameterHierarchyMismatch", Implementability.IMPLEMENTABLE),
    OPTIONALPARAMETERREFOUT("OptionalParameterRefOut", Implementability.IMPLEMENTABLE),
    OTHERTAGSINSIDESCRIPT1("OtherTagsInsideScript1", Implementability.IMPLEMENTABLE),
    OTHERTAGSINSIDESCRIPT2("OtherTagsInsideScript2", Implementability.IMPLEMENTABLE),
    OTHERTAGSINSIDEUNCLOSEDSCRIPT("OtherTagsInsideUnclosedScript", Implementability.IMPLEMENTABLE),
    OUTPUTTAGREQUIRED("OutputTagRequired", Implementability.IMPLEMENTABLE),
    OVERRIDENWITHEMPTYVALUE("OverridenWithEmptyValue", Implementability.IMPLEMENTABLE),
    OVERRIDENWITHSAMEVALUE("OverridenWithSameValue", Implementability.IMPLEMENTABLE),
    PARAMETERHIDESMEMBER("ParameterHidesMember", Implementability.IMPLEMENTABLE),
    PARAMETERTYPECANBEENUMERABLE_GLOBAL("ParameterTypeCanBeEnumerable.Global", Implementability.IMPLEMENTABLE),
    PARAMETERTYPECANBEENUMERABLE_LOCAL("ParameterTypeCanBeEnumerable.Local", Implementability.IMPLEMENTABLE),
    PARTIALMETHODPARAMETERNAMEMISMATCH("PartialMethodParameterNameMismatch", Implementability.IMPLEMENTABLE),
    PARTIALMETHODWITHSINGLEPART("PartialMethodWithSinglePart", Implementability.IMPLEMENTABLE),
    PARTIALTYPEWITHSINGLEPART("PartialTypeWithSinglePart", Implementability.IMPLEMENTABLE),
    PATHNOTRESOLVED("PathNotResolved", Implementability.IMPLEMENTABLE),
    POLYMORPHICFIELDLIKEEVENTINVOCATION("PolymorphicFieldLikeEventInvocation", Implementability.IMPLEMENTABLE),
    POSSIBLEASSIGNMENTTOREADONLYFIELD("PossibleAssignmentToReadonlyField", Implementability.IMPLEMENTABLE),
    POSSIBLEINFINITEINHERITANCE("PossibleInfiniteInheritance", Implementability.IMPLEMENTABLE),
    POSSIBLEINTENDEDRETHROW("PossibleIntendedRethrow", Implementability.IMPLEMENTABLE),
    POSSIBLEINTERFACEMEMBERAMBIGUITY("PossibleInterfaceMemberAmbiguity", Implementability.IMPLEMENTABLE),
    POSSIBLEINVALIDCASTEXCEPTION("PossibleInvalidCastException", Implementability.IMPLEMENTABLE),
    POSSIBLEINVALIDCASTEXCEPTIONINFOREACHLOOP("PossibleInvalidCastExceptionInForeachLoop", Implementability.IMPLEMENTABLE),
    POSSIBLEINVALIDOPERATIONEXCEPTION("PossibleInvalidOperationException", Implementability.IMPLEMENTABLE),
    POSSIBLELOSSOFFRACTION("PossibleLossOfFraction", Implementability.IMPLEMENTABLE),
    POSSIBLEMISTAKENARGUMENT("PossibleMistakenArgument", Implementability.IMPLEMENTABLE),
    POSSIBLEMISTAKENCALLTOGETTYPE_1("PossibleMistakenCallToGetType.1", Implementability.IMPLEMENTABLE),
    POSSIBLEMISTAKENCALLTOGETTYPE_2("PossibleMistakenCallToGetType.2", Implementability.IMPLEMENTABLE),
    POSSIBLEMULTIPLEENUMERATION("PossibleMultipleEnumeration", Implementability.IMPLEMENTABLE),
    POSSIBLEMULTIPLEWRITEACCESSINDOUBLECHECKLOCKING("PossibleMultipleWriteAccessInDoubleCheckLocking", Implementability.IMPLEMENTABLE),
    POSSIBLENULLREFERENCEEXCEPTION("PossibleNullReferenceException", Implementability.IMPLEMENTABLE),
    POSSIBLEUNINTENDEDREFERENCECOMPARISON("PossibleUnintendedReferenceComparison", Implementability.IMPLEMENTABLE),
    POSSIBLYMISTAKENUSEOFPARAMSMETHOD("PossiblyMistakenUseOfParamsMethod", Implementability.IMPLEMENTABLE),
    PRIVATEFIELDCANBECONVERTEDTOLOCALVARIABLE("PrivateFieldCanBeConvertedToLocalVariable", Implementability.IMPLEMENTABLE),
    PROPERTYNOTRESOLVED("PropertyNotResolved", Implementability.IMPLEMENTABLE),
    PUBLICCONSTRUCTORINABSTRACTCLASS("PublicConstructorInAbstractClass", Implementability.IMPLEMENTABLE),
    PUREATTRIBUTEONVOIDMETHOD("PureAttributeOnVoidMethod", Implementability.IMPLEMENTABLE),
    READACCESSINDOUBLECHECKLOCKING("ReadAccessInDoubleCheckLocking", Implementability.IMPLEMENTABLE),
    REDUNDANT("Redundant", Implementability.IMPLEMENTABLE),
    REDUNDANTANONYMOUSTYPEPROPERTYNAME("RedundantAnonymousTypePropertyName", Implementability.IMPLEMENTABLE),
    REDUNDANTARGUMENTDEFAULTVALUE("RedundantArgumentDefaultValue", Implementability.IMPLEMENTABLE),
    REDUNDANTARGUMENTNAME("RedundantArgumentName", Implementability.IMPLEMENTABLE),
    REDUNDANTARGUMENTNAMEFORLITERALEXPRESSION("RedundantArgumentNameForLiteralExpression", Implementability.IMPLEMENTABLE),
    REDUNDANTARRAYCREATIONEXPRESSION("RedundantArrayCreationExpression", Implementability.IMPLEMENTABLE),
    REDUNDANTASSIGNMENT("RedundantAssignment", Implementability.IMPLEMENTABLE),
    REDUNDANTATTRIBUTEPARENTHESES("RedundantAttributeParentheses", Implementability.IMPLEMENTABLE),
    REDUNDANTBASECONSTRUCTORCALL("RedundantBaseConstructorCall", Implementability.IMPLEMENTABLE),
    REDUNDANTBASEQUALIFIER("RedundantBaseQualifier", Implementability.IMPLEMENTABLE),
    REDUNDANTBOOLCOMPARE("RedundantBoolCompare", Implementability.IMPLEMENTABLE),
    REDUNDANTCASELABEL("RedundantCaseLabel", Implementability.IMPLEMENTABLE),
    REDUNDANTCAST("RedundantCast", Implementability.IMPLEMENTABLE),
    REDUNDANTCAST_0("RedundantCast.0", Implementability.IMPLEMENTABLE),
    REDUNDANTCATCHCLAUSE("RedundantCatchClause", Implementability.IMPLEMENTABLE),
    REDUNDANTCHECKBEFOREASSIGNMENT("RedundantCheckBeforeAssignment", Implementability.IMPLEMENTABLE),
    REDUNDANTCOLLECTIONINITIALIZERELEMENTBRACES("RedundantCollectionInitializerElementBraces", Implementability.IMPLEMENTABLE),
    REDUNDANTCOMPARISONWITHNULL("RedundantComparisonWithNull", Implementability.IMPLEMENTABLE),
    REDUNDANTDEFAULTFIELDINITIALIZER("RedundantDefaultFieldInitializer", Implementability.IMPLEMENTABLE),
    REDUNDANTDELEGATECREATION("RedundantDelegateCreation", Implementability.IMPLEMENTABLE),
    REDUNDANTEMPTYCASEELSE("RedundantEmptyCaseElse", Implementability.IMPLEMENTABLE),
    REDUNDANTEMPTYDEFAULTSWITCHBRANCH("RedundantEmptyDefaultSwitchBranch", Implementability.IMPLEMENTABLE),
    REDUNDANTEMPTYFINALLYBLOCK("RedundantEmptyFinallyBlock", Implementability.IMPLEMENTABLE),
    REDUNDANTEMPTYOBJECTCREATIONARGUMENTLIST("RedundantEmptyObjectCreationArgumentList", Implementability.IMPLEMENTABLE),
    REDUNDANTEMPTYOBJECTORCOLLECTIONINITIALIZER("RedundantEmptyObjectOrCollectionInitializer", Implementability.IMPLEMENTABLE),
    REDUNDANTENUMERABLECASTCALL("RedundantEnumerableCastCall", Implementability.IMPLEMENTABLE),
    REDUNDANTEXPLICITARRAYCREATION("RedundantExplicitArrayCreation", Implementability.IMPLEMENTABLE),
    REDUNDANTEXPLICITARRAYSIZE("RedundantExplicitArraySize", Implementability.IMPLEMENTABLE),
    REDUNDANTEXPLICITNULLABLECREATION("RedundantExplicitNullableCreation", Implementability.IMPLEMENTABLE),
    REDUNDANTEXTENDSLISTENTRY("RedundantExtendsListEntry", Implementability.IMPLEMENTABLE),
    REDUNDANTIFELSEBLOCK("RedundantIfElseBlock", Implementability.IMPLEMENTABLE),
    REDUNDANTITERATORKEYWORD("RedundantIteratorKeyword", Implementability.IMPLEMENTABLE),
    REDUNDANTJUMPSTATEMENT("RedundantJumpStatement", Implementability.IMPLEMENTABLE),
    REDUNDANTLAMBDAPARAMETERTYPE("RedundantLambdaParameterType", Implementability.IMPLEMENTABLE),
    REDUNDANTLAMBDASIGNATUREPARENTHESES("RedundantLambdaSignatureParentheses", Implementability.REJECTED),
    REDUNDANTLOCALFUNCTIONNAME("RedundantLocalFunctionName", Implementability.IMPLEMENTABLE),
    REDUNDANTLOGICALCONDITIONALEXPRESSIONOPERAND("RedundantLogicalConditionalExpressionOperand", Implementability.IMPLEMENTABLE),
    REDUNDANTMYBASEQUALIFIER("RedundantMyBaseQualifier", Implementability.IMPLEMENTABLE),
    REDUNDANTNAMEQUALIFIER("RedundantNameQualifier", Implementability.IMPLEMENTABLE),
    REDUNDANTOVERLOAD_GLOBAL("RedundantOverload.Global", Implementability.IMPLEMENTABLE),
    REDUNDANTOVERLOAD_LOCAL("RedundantOverload.Local", Implementability.IMPLEMENTABLE),
    REDUNDANTOVERRIDENMEMBER("RedundantOverridenMember", Implementability.IMPLEMENTABLE),
    REDUNDANTPARAMS("RedundantParams", Implementability.IMPLEMENTABLE),
    REDUNDANTQUALIFIER("RedundantQualifier", Implementability.IMPLEMENTABLE),
    REDUNDANTSTRINGFORMATCALL("RedundantStringFormatCall", Implementability.IMPLEMENTABLE),
    REDUNDANTSTRINGTOCHARARRAYCALL("RedundantStringToCharArrayCall", Implementability.IMPLEMENTABLE),
    REDUNDANTSTRINGTYPE("RedundantStringType", Implementability.IMPLEMENTABLE),
    REDUNDANTTERNARYEXPRESSION("RedundantTernaryExpression", Implementability.IMPLEMENTABLE),
    REDUNDANTTHISQUALIFIER("RedundantThisQualifier", Implementability.IMPLEMENTABLE),
    REDUNDANTTOSTRINGCALL("RedundantToStringCall", Implementability.IMPLEMENTABLE),
    REDUNDANTTOSTRINGCALLFORVALUETYPE("RedundantToStringCallForValueType", Implementability.IMPLEMENTABLE),
    REDUNDANTTYPEARGUMENTSOFMETHOD("RedundantTypeArgumentsOfMethod", Implementability.IMPLEMENTABLE),
    REDUNDANTUNITS("RedundantUnits", Implementability.IMPLEMENTABLE),
    REDUNDANTUNSAFECONTEXT("RedundantUnsafeContext", Implementability.IMPLEMENTABLE),
    REDUNDANTUSINGDIRECTIVE("RedundantUsingDirective", Implementability.REJECTED),
    REFERENCEEQUALSWITHVALUETYPE("ReferenceEqualsWithValueType", Implementability.IMPLEMENTABLE),
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
    REQUIREDBASETYPESCONFLICT("RequiredBaseTypesConflict", Implementability.IMPLEMENTABLE),
    REQUIREDBASETYPESDIRECTCONFLICT("RequiredBaseTypesDirectConflict", Implementability.IMPLEMENTABLE),
    REQUIREDBASETYPESISNOTINHERITED("RequiredBaseTypesIsNotInherited", Implementability.IMPLEMENTABLE),
    RESOURCEITEMNOTRESOLVED("ResourceItemNotResolved", Implementability.IMPLEMENTABLE),
    RESOURCENOTRESOLVED("ResourceNotResolved", Implementability.IMPLEMENTABLE),
    RESXNOTRESOLVED("ResxNotResolved", Implementability.IMPLEMENTABLE),
    RETURNTYPECANBEENUMERABLE_GLOBAL("ReturnTypeCanBeEnumerable.Global", Implementability.IMPLEMENTABLE),
    RETURNTYPECANBEENUMERABLE_LOCAL("ReturnTypeCanBeEnumerable.Local", Implementability.IMPLEMENTABLE),
    RETURNVALUEOFPUREMETHODISNOTUSED("ReturnValueOfPureMethodIsNotUsed", Implementability.IMPLEMENTABLE),
    SEALEDMEMBERINSEALEDCLASS("SealedMemberInSealedClass", Implementability.IMPLEMENTABLE),
    SIMILARANONYMOUSTYPENEARBY("SimilarAnonymousTypeNearby", Implementability.IMPLEMENTABLE),
    SIMPLIFYCONDITIONALOPERATOR("SimplifyConditionalOperator", Implementability.IMPLEMENTABLE),
    SIMPLIFYCONDITIONALTERNARYEXPRESSION("SimplifyConditionalTernaryExpression", Implementability.IMPLEMENTABLE),
    SIMPLIFYIIF("SimplifyIIf", Implementability.IMPLEMENTABLE),
    SIMPLIFYLINQEXPRESSION("SimplifyLinqExpression", Implementability.IMPLEMENTABLE),
    SPECIFYACULTUREINSTRINGCONVERSIONEXPLICITLY("SpecifyACultureInStringConversionExplicitly", Implementability.IMPLEMENTABLE),
    SPECIFYSTRINGCOMPARISON("SpecifyStringComparison", Implementability.IMPLEMENTABLE),
    STATICFIELDINGENERICTYPE("StaticFieldInGenericType", Implementability.IMPLEMENTABLE),
    STATICFIELDINITIALIZERSREFERESTOFIELDBELOW("StaticFieldInitializersReferesToFieldBelow", Implementability.IMPLEMENTABLE),
    STRINGCOMPAREISCULTURESPECIFIC_1("StringCompareIsCultureSpecific.1", Implementability.IMPLEMENTABLE),
    STRINGCOMPAREISCULTURESPECIFIC_2("StringCompareIsCultureSpecific.2", Implementability.IMPLEMENTABLE),
    STRINGCOMPAREISCULTURESPECIFIC_3("StringCompareIsCultureSpecific.3", Implementability.IMPLEMENTABLE),
    STRINGCOMPAREISCULTURESPECIFIC_4("StringCompareIsCultureSpecific.4", Implementability.IMPLEMENTABLE),
    STRINGCOMPAREISCULTURESPECIFIC_5("StringCompareIsCultureSpecific.5", Implementability.IMPLEMENTABLE),
    STRINGCOMPAREISCULTURESPECIFIC_6("StringCompareIsCultureSpecific.6", Implementability.IMPLEMENTABLE),
    STRINGCOMPARETOISCULTURESPECIFIC("StringCompareToIsCultureSpecific", Implementability.IMPLEMENTABLE),
    STRINGENDSWITHISCULTURESPECIFIC("StringEndsWithIsCultureSpecific", Implementability.IMPLEMENTABLE),
    STRINGINDEXOFISCULTURESPECIFIC_1("StringIndexOfIsCultureSpecific.1", Implementability.IMPLEMENTABLE),
    STRINGINDEXOFISCULTURESPECIFIC_2("StringIndexOfIsCultureSpecific.2", Implementability.IMPLEMENTABLE),
    STRINGINDEXOFISCULTURESPECIFIC_3("StringIndexOfIsCultureSpecific.3", Implementability.IMPLEMENTABLE),
    STRINGLASTINDEXOFISCULTURESPECIFIC_1("StringLastIndexOfIsCultureSpecific.1", Implementability.IMPLEMENTABLE),
    STRINGLASTINDEXOFISCULTURESPECIFIC_2("StringLastIndexOfIsCultureSpecific.2", Implementability.IMPLEMENTABLE),
    STRINGLASTINDEXOFISCULTURESPECIFIC_3("StringLastIndexOfIsCultureSpecific.3", Implementability.IMPLEMENTABLE),
    STRINGSTARTSWITHISCULTURESPECIFIC("StringStartsWithIsCultureSpecific", Implementability.IMPLEMENTABLE),
    SUGGESTBASETYPEFORPARAMETER("SuggestBaseTypeForParameter", Implementability.REJECTED),
    SUGGESTUSEVARKEYWORDEVERYWHERE("SuggestUseVarKeywordEverywhere", Implementability.IMPLEMENTABLE),
    SUGGESTUSEVARKEYWORDEVIDENT("SuggestUseVarKeywordEvident", Implementability.IMPLEMENTABLE),
    SUSPICIOUSTYPECONVERSION_GLOBAL("SuspiciousTypeConversion.Global", Implementability.IMPLEMENTABLE),
    TAILRECURSIVECALL("TailRecursiveCall", Implementability.IMPLEMENTABLE),
    TASKSNOTLOADED("TasksNotLoaded", Implementability.IMPLEMENTABLE),
    THREADSTATICATINSTANCEFIELD("ThreadStaticAtInstanceField", Implementability.IMPLEMENTABLE),
    THREADSTATICFIELDHASINITIALIZER("ThreadStaticFieldHasInitializer", Implementability.IMPLEMENTABLE),
    TOOWIDELOCALVARIABLESCOPE("TooWideLocalVariableScope", Implementability.IMPLEMENTABLE),
    TRYSTATEMENTSCANBEMERGED("TryStatementsCanBeMerged", Implementability.IMPLEMENTABLE),
    TYPEPARAMETERCANBEVARIANT("TypeParameterCanBeVariant", Implementability.IMPLEMENTABLE),
    UKNOWNTASKATTRIBUTE("UknownTaskAttribute", Implementability.IMPLEMENTABLE),
    UNASSIGNEDFIELD_COMPILER("UnassignedField.Compiler", Implementability.IMPLEMENTABLE),
    UNASSIGNEDFIELD_GLOBAL("UnassignedField.Global", Implementability.IMPLEMENTABLE),
    UNASSIGNEDFIELD_LOCAL("UnassignedField.Local", Implementability.IMPLEMENTABLE),
    UNASSIGNEDREADONLYFIELD("UnassignedReadonlyField", Implementability.IMPLEMENTABLE),
    UNASSIGNEDREADONLYFIELD_COMPILER("UnassignedReadonlyField.Compiler", Implementability.IMPLEMENTABLE),
    UNEXPECTEDVALUE("UnexpectedValue", Implementability.IMPLEMENTABLE),
    UNKNOWNITEMGROUP("UnknownItemGroup", Implementability.IMPLEMENTABLE),
    UNKNOWNMETADATA("UnknownMetadata", Implementability.IMPLEMENTABLE),
    UNKNOWNOUTPUTPARAMETER("UnknownOutputParameter", Implementability.IMPLEMENTABLE),
    UNKNOWNPROPERTY("UnknownProperty", Implementability.IMPLEMENTABLE),
    UNKNOWNTARGET("UnknownTarget", Implementability.IMPLEMENTABLE),
    UNKNOWNTASK("UnknownTask", Implementability.IMPLEMENTABLE),
    UNREACHABLECODE("UnreachableCode", Implementability.IMPLEMENTABLE),
    UNSUPPORTEDREQUIREDBASETYPE("UnsupportedRequiredBaseType", Implementability.IMPLEMENTABLE),
    UNUSEDANONYMOUSMETHODSIGNATURE("UnusedAnonymousMethodSignature", Implementability.IMPLEMENTABLE),
    UNUSEDAUTOPROPERTYACCESSOR_GLOBAL("UnusedAutoPropertyAccessor.Global", Implementability.IMPLEMENTABLE),
    UNUSEDAUTOPROPERTYACCESSOR_LOCAL("UnusedAutoPropertyAccessor.Local", Implementability.IMPLEMENTABLE),
    UNUSEDFIELD_COMPILER("UnusedField.Compiler", Implementability.IMPLEMENTABLE),
    UNUSEDIMPORTCLAUSE("UnusedImportClause", Implementability.IMPLEMENTABLE),
    UNUSEDLABEL("UnusedLabel", Implementability.IMPLEMENTABLE),
    UNUSEDLOCALS("UnusedLocals", Implementability.IMPLEMENTABLE),
    UNUSEDMEMBER_GLOBAL("UnusedMember.Global", Implementability.IMPLEMENTABLE),
    UNUSEDMEMBER_LOCAL("UnusedMember.Local", Implementability.IMPLEMENTABLE),
    UNUSEDMEMBERHIEARCHY_GLOBAL("UnusedMemberHiearchy.Global", Implementability.IMPLEMENTABLE),
    UNUSEDMEMBERHIEARCHY_LOCAL("UnusedMemberHiearchy.Local", Implementability.IMPLEMENTABLE),
    UNUSEDMEMBERINSUPER_GLOBAL("UnusedMemberInSuper.Global", Implementability.IMPLEMENTABLE),
    UNUSEDMEMBERINSUPER_LOCAL("UnusedMemberInSuper.Local", Implementability.IMPLEMENTABLE),
    UNUSEDMETHODRETURNVALUE_GLOBAL("UnusedMethodReturnValue.Global", Implementability.IMPLEMENTABLE),
    UNUSEDMETHODRETURNVALUE_LOCAL("UnusedMethodReturnValue.Local", Implementability.IMPLEMENTABLE),
    UNUSEDPARAMETER("UnusedParameter", Implementability.IMPLEMENTABLE),
    UNUSEDPARAMETER_GLOBAL("UnusedParameter.Global", Implementability.IMPLEMENTABLE),
    UNUSEDPARAMETER_LOCAL("UnusedParameter.Local", Implementability.IMPLEMENTABLE),
    UNUSEDPROPERTY("UnusedProperty", Implementability.IMPLEMENTABLE),
    UNUSEDTYPEPARAMETER("UnusedTypeParameter", Implementability.IMPLEMENTABLE),
    UNUSEDVARIABLE("UnusedVariable", Implementability.IMPLEMENTABLE),
    UNUSEDVARIABLE_COMPILER("UnusedVariable.Compiler", Implementability.IMPLEMENTABLE),
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
    VALUEPARAMETERNOTUSED("ValueParameterNotUsed", Implementability.IMPLEMENTABLE),
    VIRTUALMEMBERNEVEROVERRIDEN_GLOBAL("VirtualMemberNeverOverriden.Global", Implementability.IMPLEMENTABLE),
    VIRTUALMEMBERNEVEROVERRIDEN_LOCAL("VirtualMemberNeverOverriden.Local", Implementability.IMPLEMENTABLE),
    WEB_IGNOREDPATH("Web.IgnoredPath", Implementability.IMPLEMENTABLE),
    WEB_MAPPEDPATH("Web.MappedPath", Implementability.IMPLEMENTABLE),
    WRONGEXPRESSIONSTATEMENT("WrongExpressionStatement", Implementability.IMPLEMENTABLE),
    WRONGMETADATAUSE("WrongMetadataUse", Implementability.IMPLEMENTABLE),
    STRUCTURALSEARCH("StructuralSearch", Implementability.IMPLEMENTABLE),
    CODESMELL("CodeSmell", Implementability.IMPLEMENTABLE),
    CSHARPERRORS("CSharpErrors", Implementability.IMPLEMENTABLE);


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
