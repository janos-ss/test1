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


public class Pmd extends AbstractReportableExternalTool {

  private String toolName = "PMD";
  private Language langauge = Language.JAVA;

  @Override
  public Language getLanguage() {

    return langauge;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return ToolRule.values();
  }

  @Override
  public String getStandardName() {

    return toolName;
  }

  @Override
  public String getRSpecReferenceFieldName() {

    return toolName;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {

    return rule.getPmd();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setPmd(ids);
  }

  public enum ToolRule implements CodingStandardRule {
    ABSTRACTCLASSWITHOUTABSTRACTMETHOD("AbstractClassWithoutAbstractMethod", Implementability.IMPLEMENTABLE),
    ABSTRACTCLASSWITHOUTANYMETHOD("AbstractClassWithoutAnyMethod", Implementability.IMPLEMENTABLE),
    ABSTRACTNAMING("AbstractNaming", Implementability.IMPLEMENTABLE),
    ACCESSORCLASSGENERATION("AccessorClassGeneration", Implementability.REJECTED),
    ADDEMPTYSTRING("AddEmptyString", Implementability.IMPLEMENTABLE),
    APPENDCHARACTERWITHCHAR("AppendCharacterWithChar", Implementability.IMPLEMENTABLE),
    ARRAYISSTOREDDIRECTLY("ArrayIsStoredDirectly", Implementability.IMPLEMENTABLE),
    ASSIGNMENTINOPERAND("AssignmentInOperand", Implementability.IMPLEMENTABLE),
    ASSIGNMENTTONONFINALSTATIC("AssignmentToNonFinalStatic", Implementability.IMPLEMENTABLE),
    ATLEASTONECONSTRUCTOR("AtLeastOneConstructor", Implementability.IMPLEMENTABLE),
    AVOIDACCESSIBILITYALTERATION("AvoidAccessibilityAlteration", Implementability.IMPLEMENTABLE),
    AVOIDARRAYLOOPS("AvoidArrayLoops", Implementability.IMPLEMENTABLE),
    AVOIDASSERTASIDENTIFIER("AvoidAssertAsIdentifier", Implementability.IMPLEMENTABLE),
    AVOIDBRANCHINGSTATEMENTASLASTINLOOP("AvoidBranchingStatementAsLastInLoop", Implementability.IMPLEMENTABLE),
    AVOIDCALLINGFINALIZE("AvoidCallingFinalize", Implementability.IMPLEMENTABLE),
    AVOIDCATCHINGGENERICEXCEPTION("AvoidCatchingGenericException", Implementability.IMPLEMENTABLE),
    AVOIDCATCHINGNPE("AvoidCatchingNPE", Implementability.IMPLEMENTABLE),
    AVOIDCATCHINGTHROWABLE("AvoidCatchingThrowable", Implementability.IMPLEMENTABLE),
    AVOIDCONSTANTSINTERFACE("AvoidConstantsInterface", Implementability.IMPLEMENTABLE),
    AVOIDDECIMALLITERALSINBIGDECIMALCONSTRUCTOR("AvoidDecimalLiteralsInBigDecimalConstructor", Implementability.IMPLEMENTABLE),
    AVOIDDEEPLYNESTEDIFSTMTS("AvoidDeeplyNestedIfStmts", Implementability.IMPLEMENTABLE),
    AVOIDDOLLARSIGNS("AvoidDollarSigns", Implementability.IMPLEMENTABLE),
    AVOIDDUPLICATELITERALS("AvoidDuplicateLiterals", Implementability.IMPLEMENTABLE),
    AVOIDENUMASIDENTIFIER("AvoidEnumAsIdentifier", Implementability.IMPLEMENTABLE),
    AVOIDFIELDNAMEMATCHINGMETHODNAME("AvoidFieldNameMatchingMethodName", Implementability.IMPLEMENTABLE),
    AVOIDFIELDNAMEMATCHINGTYPENAME("AvoidFieldNameMatchingTypeName", Implementability.IMPLEMENTABLE),
    AVOIDFINALLOCALVARIABLE("AvoidFinalLocalVariable", Implementability.REJECTED),
    AVOIDINSTANCEOFCHECKSINCATCHCLAUSE("AvoidInstanceofChecksInCatchClause", Implementability.IMPLEMENTABLE),
    AVOIDINSTANTIATINGOBJECTSINLOOPS("AvoidInstantiatingObjectsInLoops", Implementability.REJECTED),
    AVOIDLITERALSINIFCONDITION("AvoidLiteralsInIfCondition", Implementability.IMPLEMENTABLE),
    AVOIDLOSINGEXCEPTIONINFORMATION("AvoidLosingExceptionInformation", Implementability.IMPLEMENTABLE),
    AVOIDMULTIPLEUNARYOPERATORS("AvoidMultipleUnaryOperators", Implementability.IMPLEMENTABLE),
    AVOIDPREFIXINGMETHODPARAMETERS("AvoidPrefixingMethodParameters", Implementability.IMPLEMENTABLE),
    AVOIDPRINTSTACKTRACE("AvoidPrintStackTrace", Implementability.IMPLEMENTABLE),
    AVOIDPROTECTEDFIELDINFINALCLASS("AvoidProtectedFieldInFinalClass", Implementability.IMPLEMENTABLE),
    AVOIDPROTECTEDMETHODINFINALCLASSNOTEXTENDING("AvoidProtectedMethodInFinalClassNotExtending", Implementability.IMPLEMENTABLE),
    AVOIDREASSIGNINGPARAMETERS("AvoidReassigningParameters", Implementability.IMPLEMENTABLE),
    AVOIDRETHROWINGEXCEPTION("AvoidRethrowingException", Implementability.IMPLEMENTABLE),
    AVOIDSTRINGBUFFERFIELD("AvoidStringBufferField", Implementability.IMPLEMENTABLE),
    AVOIDSYNCHRONIZEDATMETHODLEVEL("AvoidSynchronizedAtMethodLevel", Implementability.IMPLEMENTABLE),
    AVOIDTHREADGROUP("AvoidThreadGroup", Implementability.IMPLEMENTABLE),
    AVOIDTHROWINGNEWINSTANCEOFSAMEEXCEPTION("AvoidThrowingNewInstanceOfSameException", Implementability.IMPLEMENTABLE),
    AVOIDTHROWINGNULLPOINTEREXCEPTION("AvoidThrowingNullPointerException", Implementability.IMPLEMENTABLE),
    AVOIDTHROWINGRAWEXCEPTIONTYPES("AvoidThrowingRawExceptionTypes", Implementability.IMPLEMENTABLE),
    AVOIDUSINGHARDCODEDIP("AvoidUsingHardCodedIP", Implementability.IMPLEMENTABLE),
    AVOIDUSINGNATIVECODE("AvoidUsingNativeCode", Implementability.IMPLEMENTABLE),
    AVOIDUSINGOCTALVALUES("AvoidUsingOctalValues", Implementability.IMPLEMENTABLE),
    AVOIDUSINGSHORTTYPE("AvoidUsingShortType", Implementability.IMPLEMENTABLE),
    AVOIDUSINGVOLATILE("AvoidUsingVolatile", Implementability.REJECTED),
    BADCOMPARISON("BadComparison", Implementability.IMPLEMENTABLE),
    BEANMEMBERSSHOULDSERIALIZE("BeanMembersShouldSerialize", Implementability.IMPLEMENTABLE),
    BIGINTEGERINSTANTIATION("BigIntegerInstantiation", Implementability.REJECTED),
    BOOLEANGETMETHODNAME("BooleanGetMethodName", Implementability.IMPLEMENTABLE),
    BOOLEANINSTANTIATION("BooleanInstantiation", Implementability.IMPLEMENTABLE),
    BOOLEANINVERSION("BooleanInversion", Implementability.REJECTED),
    BROKENNULLCHECK("BrokenNullCheck", Implementability.IMPLEMENTABLE),
    BYTEINSTANTIATION("ByteInstantiation", Implementability.IMPLEMENTABLE),
    CALLSUPERFIRST("CallSuperFirst", Implementability.IMPLEMENTABLE),
    CALLSUPERINCONSTRUCTOR("CallSuperInConstructor", Implementability.IMPLEMENTABLE),
    CALLSUPERLAST("CallSuperLast", Implementability.IMPLEMENTABLE),
    CHECKRESULTSET("CheckResultSet", Implementability.IMPLEMENTABLE),
    CHECKSKIPRESULT("CheckSkipResult", Implementability.IMPLEMENTABLE),
    CLASSCASTEXCEPTIONWITHTOARRAY("ClassCastExceptionWithToArray", Implementability.IMPLEMENTABLE),
    CLASSNAMINGCONVENTIONS("ClassNamingConventions", Implementability.IMPLEMENTABLE),
    CLASSWITHONLYPRIVATECONSTRUCTORSSHOULDBEFINAL("ClassWithOnlyPrivateConstructorsShouldBeFinal", Implementability.IMPLEMENTABLE),
    CLONEMETHODMUSTIMPLEMENTCLONEABLE("CloneMethodMustImplementCloneable", Implementability.IMPLEMENTABLE),
    CLONEMETHODMUSTIMPLEMENTCLONEABLEWITHTYPERESOLUTION("CloneMethodMustImplementCloneableWithTypeResolution", Implementability.IMPLEMENTABLE),
    CLONETHROWSCLONENOTSUPPORTEDEXCEPTION("CloneThrowsCloneNotSupportedException", Implementability.IMPLEMENTABLE),
    CLOSERESOURCE("CloseResource", Implementability.IMPLEMENTABLE),
    COLLAPSIBLEIFSTATEMENTS("CollapsibleIfStatements", Implementability.IMPLEMENTABLE),
    COMMENTCONTENT("CommentContent", Implementability.REJECTED),
    COMMENTREQUIRED("CommentRequired", Implementability.REJECTED),
    COMMENTSIZE("CommentSize", Implementability.REJECTED),
    COMPAREOBJECTSWITHEQUALS("CompareObjectsWithEquals", Implementability.IMPLEMENTABLE),
    CONFUSINGTERNARY("ConfusingTernary", Implementability.REJECTED),
    CONSECUTIVEAPPENDSSHOULDREUSE("ConsecutiveAppendsShouldReuse", Implementability.REJECTED),
    CONSECUTIVELITERALAPPENDS("ConsecutiveLiteralAppends", Implementability.IMPLEMENTABLE),
    CONSTRUCTORCALLSOVERRIDABLEMETHOD("ConstructorCallsOverridableMethod", Implementability.IMPLEMENTABLE),
    COUPLINGBETWEENOBJECTS("CouplingBetweenObjects", Implementability.IMPLEMENTABLE),
    CYCLOMATICCOMPLEXITY("CyclomaticComplexity", Implementability.IMPLEMENTABLE),
    DATAFLOWANOMALYANALYSIS("DataflowAnomalyAnalysis", Implementability.REJECTED),
    DEFAULTLABELNOTLASTINSWITCHSTMT("DefaultLabelNotLastInSwitchStmt", Implementability.IMPLEMENTABLE),
    DEFAULTPACKAGE("DefaultPackage", Implementability.IMPLEMENTABLE),
    DONOTCALLGARBAGECOLLECTIONEXPLICITLY("DoNotCallGarbageCollectionExplicitly", Implementability.IMPLEMENTABLE),
    DONOTCALLSYSTEMEXIT("DoNotCallSystemExit", Implementability.IMPLEMENTABLE),
    DONOTEXTENDJAVALANGERROR("DoNotExtendJavaLangError", Implementability.IMPLEMENTABLE),
    DONOTHARDCODESDCARD("DoNotHardCodeSDCard", Implementability.IMPLEMENTABLE),
    DONOTTHROWEXCEPTIONINFINALLY("DoNotThrowExceptionInFinally", Implementability.IMPLEMENTABLE),
    DONOTUSETHREADS("DoNotUseThreads", Implementability.IMPLEMENTABLE),
    DONTCALLTHREADRUN("DontCallThreadRun", Implementability.IMPLEMENTABLE),
    DONTIMPORTJAVALANG("DontImportJavaLang", Implementability.IMPLEMENTABLE),
    DONTIMPORTSUN("DontImportSun", Implementability.IMPLEMENTABLE),
    DONTUSEFLOATTYPEFORLOOPINDICES("DontUseFloatTypeForLoopIndices", Implementability.IMPLEMENTABLE),
    DOUBLECHECKEDLOCKING("DoubleCheckedLocking", Implementability.IMPLEMENTABLE),
    DUPLICATEIMPORTS("DuplicateImports", Implementability.IMPLEMENTABLE),
    EMPTYCATCHBLOCK("EmptyCatchBlock", Implementability.IMPLEMENTABLE),
    EMPTYFINALIZER("EmptyFinalizer", Implementability.IMPLEMENTABLE),
    EMPTYFINALLYBLOCK("EmptyFinallyBlock", Implementability.IMPLEMENTABLE),
    EMPTYIFSTMT("EmptyIfStmt", Implementability.IMPLEMENTABLE),
    EMPTYINITIALIZER("EmptyInitializer", Implementability.IMPLEMENTABLE),
    EMPTYMETHODINABSTRACTCLASSSHOULDBEABSTRACT("EmptyMethodInAbstractClassShouldBeAbstract", Implementability.REJECTED),
    EMPTYSTATEMENTBLOCK("EmptyStatementBlock", Implementability.IMPLEMENTABLE),
    EMPTYSTATEMENTNOTINLOOP("EmptyStatementNotInLoop", Implementability.IMPLEMENTABLE),
    EMPTYSTATICINITIALIZER("EmptyStaticInitializer", Implementability.IMPLEMENTABLE),
    EMPTYSWITCHSTATEMENTS("EmptySwitchStatements", Implementability.IMPLEMENTABLE),
    EMPTYSYNCHRONIZEDBLOCK("EmptySynchronizedBlock", Implementability.IMPLEMENTABLE),
    EMPTYTRYBLOCK("EmptyTryBlock", Implementability.IMPLEMENTABLE),
    EMPTYWHILESTMT("EmptyWhileStmt", Implementability.IMPLEMENTABLE),
    EQUALSNULL("EqualsNull", Implementability.IMPLEMENTABLE),
    EXCEPTIONASFLOWCONTROL("ExceptionAsFlowControl", Implementability.IMPLEMENTABLE),
    EXCESSIVECLASSLENGTH("ExcessiveClassLength", Implementability.IMPLEMENTABLE),
    EXCESSIVEIMPORTS("ExcessiveImports", Implementability.IMPLEMENTABLE),
    EXCESSIVEMETHODLENGTH("ExcessiveMethodLength", Implementability.IMPLEMENTABLE),
    EXCESSIVEPARAMETERLIST("ExcessiveParameterList", Implementability.IMPLEMENTABLE),
    EXCESSIVEPUBLICCOUNT("ExcessivePublicCount", Implementability.IMPLEMENTABLE),
    EXTENDSOBJECT("ExtendsObject", Implementability.IMPLEMENTABLE),
    FIELDDECLARATIONSSHOULDBEATSTARTOFCLASS("FieldDeclarationsShouldBeAtStartOfClass", Implementability.IMPLEMENTABLE),
    FINALFIELDCOULDBESTATIC("FinalFieldCouldBeStatic", Implementability.IMPLEMENTABLE),
    FINALIZEDOESNOTCALLSUPERFINALIZE("FinalizeDoesNotCallSuperFinalize", Implementability.IMPLEMENTABLE),
    FINALIZEONLYCALLSSUPERFINALIZE("FinalizeOnlyCallsSuperFinalize", Implementability.IMPLEMENTABLE),
    FINALIZEOVERLOADED("FinalizeOverloaded", Implementability.IMPLEMENTABLE),
    FINALIZESHOULDBEPROTECTED("FinalizeShouldBeProtected", Implementability.IMPLEMENTABLE),
    FORLOOPSHOULDBEWHILELOOP("ForLoopShouldBeWhileLoop", Implementability.IMPLEMENTABLE),
    FORLOOPSMUSTUSEBRACES("ForLoopsMustUseBraces", Implementability.IMPLEMENTABLE),
    GENERICSNAMING("GenericsNaming", Implementability.IMPLEMENTABLE),
    GODCLASS("GodClass", Implementability.REJECTED),
    GUARDDEBUGLOGGING("GuardDebugLogging", Implementability.REJECTED),
    GUARDLOGSTATEMENT("GuardLogStatement", Implementability.REJECTED),
    GUARDLOGSTATEMENTJAVAUTIL("GuardLogStatementJavaUtil", Implementability.REJECTED),
    IDEMPOTENTOPERATIONS("IdempotentOperations", Implementability.IMPLEMENTABLE),
    IFELSESTMTSMUSTUSEBRACES("IfElseStmtsMustUseBraces", Implementability.IMPLEMENTABLE),
    IFSTMTSMUSTUSEBRACES("IfStmtsMustUseBraces", Implementability.IMPLEMENTABLE),
    IMMUTABLEFIELD("ImmutableField", Implementability.IMPLEMENTABLE),
    IMPORTFROMSAMEPACKAGE("ImportFromSamePackage", Implementability.IMPLEMENTABLE),
    INEFFICIENTEMPTYSTRINGCHECK("InefficientEmptyStringCheck", Implementability.REJECTED),
    INEFFICIENTSTRINGBUFFERING("InefficientStringBuffering", Implementability.IMPLEMENTABLE),
    INSTANTIATIONTOGETCLASS("InstantiationToGetClass", Implementability.IMPLEMENTABLE),
    INSUFFICIENTSTRINGBUFFERDECLARATION("InsufficientStringBufferDeclaration", Implementability.REJECTED),
    INTEGERINSTANTIATION("IntegerInstantiation", Implementability.IMPLEMENTABLE),
    JUMBLEDINCREMENTER("JumbledIncrementer", Implementability.IMPLEMENTABLE),
    LAWOFDEMETER("LawOfDemeter", Implementability.REJECTED),
    LOCALHOMENAMINGCONVENTION("LocalHomeNamingConvention", Implementability.REJECTED),
    LOCALINTERFACESESSIONNAMINGCONVENTION("LocalInterfaceSessionNamingConvention", Implementability.REJECTED),
    LOCALVARIABLECOULDBEFINAL("LocalVariableCouldBeFinal", Implementability.REJECTED),
    LOGGERISNOTSTATICFINAL("LoggerIsNotStaticFinal", Implementability.IMPLEMENTABLE),
    LOGICINVERSION("LogicInversion", Implementability.IMPLEMENTABLE),
    LONGINSTANTIATION("LongInstantiation", Implementability.IMPLEMENTABLE),
    LONGVARIABLE("LongVariable", Implementability.IMPLEMENTABLE),
    LOOSECOUPLING("LooseCoupling", Implementability.IMPLEMENTABLE),
    LOOSECOUPLINGWITHTYPERESOLUTION("LooseCouplingWithTypeResolution", Implementability.IMPLEMENTABLE),
    LOOSEPACKAGECOUPLING("LoosePackageCoupling", Implementability.IMPLEMENTABLE),
    MDBANDSESSIONBEANNAMINGCONVENTION("MDBAndSessionBeanNamingConvention", Implementability.REJECTED),
    METHODARGUMENTCOULDBEFINAL("MethodArgumentCouldBeFinal", Implementability.IMPLEMENTABLE),
    METHODNAMINGCONVENTIONS("MethodNamingConventions", Implementability.IMPLEMENTABLE),
    METHODRETURNSINTERNALARRAY("MethodReturnsInternalArray", Implementability.IMPLEMENTABLE),
    METHODWITHSAMENAMEASENCLOSINGCLASS("MethodWithSameNameAsEnclosingClass", Implementability.IMPLEMENTABLE),
    MISLEADINGVARIABLENAME("MisleadingVariableName", Implementability.IMPLEMENTABLE),
    MISPLACEDNULLCHECK("MisplacedNullCheck", Implementability.IMPLEMENTABLE),
    MISSINGBREAKINSWITCH("MissingBreakInSwitch", Implementability.IMPLEMENTABLE),
    MISSINGSERIALVERSIONUID("MissingSerialVersionUID", Implementability.IMPLEMENTABLE),
    MISSINGSTATICMETHODINNONINSTANTIATABLECLASS("MissingStaticMethodInNonInstantiatableClass", Implementability.IMPLEMENTABLE),
    MODIFIEDCYCLOMATICCOMPLEXITY("ModifiedCyclomaticComplexity", Implementability.IMPLEMENTABLE),
    MORETHANONELOGGER("MoreThanOneLogger", Implementability.IMPLEMENTABLE),
    NCSSCONSTRUCTORCOUNT("NcssConstructorCount", Implementability.IMPLEMENTABLE),
    NCSSMETHODCOUNT("NcssMethodCount", Implementability.IMPLEMENTABLE),
    NCSSTYPECOUNT("NcssTypeCount", Implementability.IMPLEMENTABLE),
    NONCASELABELINSWITCHSTATEMENT("NonCaseLabelInSwitchStatement", Implementability.IMPLEMENTABLE),
    NONSTATICINITIALIZER("NonStaticInitializer", Implementability.IMPLEMENTABLE),
    NONTHREADSAFESINGLETON("NonThreadSafeSingleton", Implementability.IMPLEMENTABLE),
    NOPACKAGE("NoPackage", Implementability.IMPLEMENTABLE),
    NPATHCOMPLEXITY("NPathComplexity", Implementability.IMPLEMENTABLE),
    NULLASSIGNMENT("NullAssignment", Implementability.REJECTED),
    ONEDECLARATIONPERLINE("OneDeclarationPerLine", Implementability.IMPLEMENTABLE),
    ONLYONERETURN("OnlyOneReturn", Implementability.IMPLEMENTABLE),
    OPTIMIZABLETOARRAYCALL("OptimizableToArrayCall", Implementability.IMPLEMENTABLE),
    OVERRIDEBOTHEQUALSANDHASHCODE("OverrideBothEqualsAndHashcode", Implementability.IMPLEMENTABLE),
    PACKAGECASE("PackageCase", Implementability.IMPLEMENTABLE),
    POSITIONLITERALSFIRSTINCASEINSENSITIVECOMPARISONS("PositionLiteralsFirstInCaseInsensitiveComparisons", Implementability.IMPLEMENTABLE),
    POSITIONLITERALSFIRSTINCOMPARISONS("PositionLiteralsFirstInComparisons", Implementability.IMPLEMENTABLE),
    PREMATUREDECLARATION("PrematureDeclaration", Implementability.IMPLEMENTABLE),
    PRESERVESTACKTRACE("PreserveStackTrace", Implementability.IMPLEMENTABLE),
    PROPERCLONEIMPLEMENTATION("ProperCloneImplementation", Implementability.IMPLEMENTABLE),
    PROPERLOGGER("ProperLogger", Implementability.IMPLEMENTABLE),
    REDUNDANTFIELDINITIALIZER("RedundantFieldInitializer", Implementability.REJECTED),
    REMOTEINTERFACENAMINGCONVENTION("RemoteInterfaceNamingConvention", Implementability.REJECTED),
    REMOTESESSIONINTERFACENAMINGCONVENTION("RemoteSessionInterfaceNamingConvention", Implementability.REJECTED),
    REPLACEENUMERATIONWITHITERATOR("ReplaceEnumerationWithIterator", Implementability.IMPLEMENTABLE),
    REPLACEHASHTABLEWITHMAP("ReplaceHashtableWithMap", Implementability.IMPLEMENTABLE),
    REPLACEVECTORWITHLIST("ReplaceVectorWithList", Implementability.IMPLEMENTABLE),
    RETURNEMPTYARRAYRATHERTHANNULL("ReturnEmptyArrayRatherThanNull", Implementability.IMPLEMENTABLE),
    RETURNFROMFINALLYBLOCK("ReturnFromFinallyBlock", Implementability.IMPLEMENTABLE),
    SHORTCLASSNAME("ShortClassName", Implementability.IMPLEMENTABLE),
    SHORTINSTANTIATION("ShortInstantiation", Implementability.IMPLEMENTABLE),
    SHORTMETHODNAME("ShortMethodName", Implementability.IMPLEMENTABLE),
    SHORTVARIABLE("ShortVariable", Implementability.IMPLEMENTABLE),
    SIGNATUREDECLARETHROWSEXCEPTION("SignatureDeclareThrowsException", Implementability.IMPLEMENTABLE),
    SIGNATUREDECLARETHROWSEXCEPTIONWITHTYPERESOLUTION("SignatureDeclareThrowsExceptionWithTypeResolution", Implementability.IMPLEMENTABLE),
    SIMPLEDATEFORMATNEEDSLOCALE("SimpleDateFormatNeedsLocale", Implementability.REJECTED),
    SIMPLIFYBOOLEANEXPRESSIONS("SimplifyBooleanExpressions", Implementability.IMPLEMENTABLE),
    SIMPLIFYBOOLEANRETURNS("SimplifyBooleanReturns", Implementability.IMPLEMENTABLE),
    SIMPLIFYCONDITIONAL("SimplifyConditional", Implementability.IMPLEMENTABLE),
    SIMPLIFYSTARTSWITH("SimplifyStartsWith", Implementability.IMPLEMENTABLE),
    SINGULARFIELD("SingularField", Implementability.IMPLEMENTABLE),
    STATICEJBFIELDSHOULDBEFINAL("StaticEJBFieldShouldBeFinal", Implementability.IMPLEMENTABLE),
    STDCYCLOMATICCOMPLEXITY("StdCyclomaticComplexity", Implementability.IMPLEMENTABLE),
    STRINGBUFFERINSTANTIATIONWITHCHAR("StringBufferInstantiationWithChar", Implementability.IMPLEMENTABLE),
    STRINGINSTANTIATION("StringInstantiation", Implementability.IMPLEMENTABLE),
    STRINGTOSTRING("StringToString", Implementability.IMPLEMENTABLE),
    SUSPICIOUSCONSTANTFIELDNAME("SuspiciousConstantFieldName", Implementability.IMPLEMENTABLE),
    SUSPICIOUSEQUALSMETHODNAME("SuspiciousEqualsMethodName", Implementability.IMPLEMENTABLE),
    SUSPICIOUSHASHCODEMETHODNAME("SuspiciousHashcodeMethodName", Implementability.IMPLEMENTABLE),
    SUSPICIOUSOCTALESCAPE("SuspiciousOctalEscape", Implementability.IMPLEMENTABLE),
    SWITCHDENSITY("SwitchDensity", Implementability.IMPLEMENTABLE),
    SWITCHSTMTSSHOULDHAVEDEFAULT("SwitchStmtsShouldHaveDefault", Implementability.IMPLEMENTABLE),
    SYSTEMPRINTLN("SystemPrintln", Implementability.IMPLEMENTABLE),
    TOOFEWBRANCHESFORASWITCHSTATEMENT("TooFewBranchesForASwitchStatement", Implementability.IMPLEMENTABLE),
    TOOMANYFIELDS("TooManyFields", Implementability.IMPLEMENTABLE),
    TOOMANYMETHODS("TooManyMethods", Implementability.IMPLEMENTABLE),
    TOOMANYSTATICIMPORTS("TooManyStaticImports", Implementability.IMPLEMENTABLE),
    UNCOMMENTEDEMPTYCONSTRUCTOR("UncommentedEmptyConstructor", Implementability.IMPLEMENTABLE),
    UNCOMMENTEDEMPTYMETHODBODY("UncommentedEmptyMethodBody", Implementability.IMPLEMENTABLE),
    UNCOMMENTEDEMPTYMETHOD("UncommentedEmptyMethod", Implementability.IMPLEMENTABLE),
    UNCONDITIONALIFSTATEMENT("UnconditionalIfStatement", Implementability.IMPLEMENTABLE),
    UNNECESSARYCASECHANGE("UnnecessaryCaseChange", Implementability.IMPLEMENTABLE),
    UNNECESSARYCONSTRUCTOR("UnnecessaryConstructor", Implementability.IMPLEMENTABLE),
    UNNECESSARYCONVERSIONTEMPORARY("UnnecessaryConversionTemporary", Implementability.IMPLEMENTABLE),
    UNNECESSARYFINALMODIFIER("UnnecessaryFinalModifier", Implementability.IMPLEMENTABLE),
    UNNECESSARYFULLYQUALIFIEDNAME("UnnecessaryFullyQualifiedName", Implementability.IMPLEMENTABLE),
    UNNECESSARYLOCALBEFORERETURN("UnnecessaryLocalBeforeReturn", Implementability.IMPLEMENTABLE),
    UNNECESSARYPARENTHESES("UnnecessaryParentheses", Implementability.IMPLEMENTABLE),
    UNNECESSARYRETURN("UnnecessaryReturn", Implementability.IMPLEMENTABLE),
    UNNECESSARYWRAPPEROBJECTCREATION("UnnecessaryWrapperObjectCreation", Implementability.IMPLEMENTABLE),
    UNSYNCHRONIZEDSTATICDATEFORMATTER("UnsynchronizedStaticDateFormatter", Implementability.IMPLEMENTABLE),
    UNUSEDFORMALPARAMETER("UnusedFormalParameter", Implementability.IMPLEMENTABLE),
    UNUSEDIMPORTS("UnusedImports", Implementability.IMPLEMENTABLE),
    UNUSEDIMPORTSWITHTYPERESOLUTION("UnusedImportsWithTypeResolution", Implementability.IMPLEMENTABLE),
    UNUSEDLOCALVARIABLE("UnusedLocalVariable", Implementability.IMPLEMENTABLE),
    UNUSEDMODIFIER("UnusedModifier", Implementability.IMPLEMENTABLE),
    UNUSEDNULLCHECKINEQUALS("UnusedNullCheckInEquals", Implementability.IMPLEMENTABLE),
    UNUSEDPRIVATEFIELD("UnusedPrivateField", Implementability.IMPLEMENTABLE),
    UNUSEDPRIVATEMETHOD("UnusedPrivateMethod", Implementability.IMPLEMENTABLE),
    USEARRAYLISTINSTEADOFVECTOR("UseArrayListInsteadOfVector", Implementability.IMPLEMENTABLE),
    USEARRAYSASLIST("UseArraysAsList", Implementability.IMPLEMENTABLE),
    USECOLLECTIONISEMPTY("UseCollectionIsEmpty", Implementability.IMPLEMENTABLE),
    USECONCURRENTHASHMAP("UseConcurrentHashMap", Implementability.REJECTED),
    USECORRECTEXCEPTIONLOGGING("UseCorrectExceptionLogging", Implementability.IMPLEMENTABLE),
    USEEQUALSTOCOMPARESTRINGS("UseEqualsToCompareStrings", Implementability.IMPLEMENTABLE),
    USEINDEXOFCHAR("UseIndexOfChar", Implementability.IMPLEMENTABLE),
    USELESSOPERATIONONIMMUTABLE("UselessOperationOnImmutable", Implementability.IMPLEMENTABLE),
    USELESSOVERRIDINGMETHOD("UselessOverridingMethod", Implementability.IMPLEMENTABLE),
    USELESSPARENTHESES("UselessParentheses", Implementability.IMPLEMENTABLE),
    USELESSSTRINGVALUEOF("UselessStringValueOf", Implementability.IMPLEMENTABLE),
    USELOCALEWITHCASECONVERSIONS("UseLocaleWithCaseConversions", Implementability.IMPLEMENTABLE),
    USENOTIFYALLINSTEADOFNOTIFY("UseNotifyAllInsteadOfNotify", Implementability.IMPLEMENTABLE),
    USEOBJECTFORCLEARERAPI("UseObjectForClearerAPI", Implementability.IMPLEMENTABLE),
    USEPROPERCLASSLOADER("UseProperClassLoader", Implementability.IMPLEMENTABLE),
    USESTRINGBUFFERFORSTRINGAPPENDS("UseStringBufferForStringAppends", Implementability.REJECTED),
    USESTRINGBUFFERLENGTH("UseStringBufferLength", Implementability.IMPLEMENTABLE),
    USEUTILITYCLASS("UseUtilityClass", Implementability.IMPLEMENTABLE),
    USEVARARGS("UseVarargs", Implementability.REJECTED),
    VARIABLENAMINGCONVENTIONS("VariableNamingConventions", Implementability.IMPLEMENTABLE),
    WHILELOOPSMUSTUSEBRACES("WhileLoopsMustUseBraces", Implementability.IMPLEMENTABLE),
    XPATHRULE("XPathRule", Implementability.REJECTED);

    private Implementability implementability;
    private String title;

    ToolRule(String title, Implementability implementability) {
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
