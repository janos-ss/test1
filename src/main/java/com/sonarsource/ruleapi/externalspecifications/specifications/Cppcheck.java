/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.List;


public class Cppcheck extends AbstractReportableExternalTool {

  private String toolName = "CPPCheck";
  private Language language = Language.C;


  @Override
  public Language getLanguage() {

    return null;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return CppCheckRule.values();
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

    return rule.getCppCheck();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setCppCheck(ids);
  }

  public enum CppCheckRule implements CodingStandardRule {
    ASSIGNMENTADDRESSTOINTEGER("AssignmentAddressToInteger", Implementability.IMPLEMENTABLE),
    ASSIGNMENTINTEGERTOADDRESS("AssignmentIntegerToAddress", Implementability.IMPLEMENTABLE),
    CASTADDRESSTOINTEGERATRETURN("CastAddressToIntegerAtReturn", Implementability.IMPLEMENTABLE),
    CASTINTEGERTOADDRESSATRETURN("CastIntegerToAddressAtReturn", Implementability.IMPLEMENTABLE),
    CONFIGURATIONNOTCHECKED("ConfigurationNotChecked", Implementability.IMPLEMENTABLE),
    IOWITHOUTPOSITIONING("IOWithoutPositioning", Implementability.IMPLEMENTABLE),
    STLMISSINGCOMPARISON("StlMissingComparison", Implementability.IMPLEMENTABLE),
    ARGUMENTSIZE("argumentSize", Implementability.IMPLEMENTABLE),
    ARITHOPERATIONSONVOIDPOINTER("arithOperationsOnVoidPointer", Implementability.IMPLEMENTABLE),
    ARRAYINDEXOUTOFBOUNDS("arrayIndexOutOfBounds", Implementability.IMPLEMENTABLE),
    ARRAYINDEXOUTOFBOUNDSCOND("arrayIndexOutOfBoundsCond", Implementability.IMPLEMENTABLE),
    ARRAYINDEXTHENCHECK("arrayIndexThenCheck", Implementability.IMPLEMENTABLE),
    ASSERTWITHSIDEEFFECT("assertWithSideEffect", Implementability.IMPLEMENTABLE),
    ASSIGNBOOLTOPOINTER("assignBoolToPointer", Implementability.IMPLEMENTABLE),
    ASSIGNIFERROR("assignIfError", Implementability.IMPLEMENTABLE),
    ASSIGNMENTINASSERT("assignmentInAssert", Implementability.IMPLEMENTABLE),
    AUTOVARIABLES("autoVariables", Implementability.IMPLEMENTABLE),
    AUTOVARINVALIDDEALLOCATION("autovarInvalidDeallocation", Implementability.IMPLEMENTABLE),
    BITWISEONBOOLEAN("bitwiseOnBoolean", Implementability.IMPLEMENTABLE),
    BOOSTFOREACHERROR("boostForeachError", Implementability.IMPLEMENTABLE),
    BUFFERACCESSOUTOFBOUNDS("bufferAccessOutOfBounds", Implementability.IMPLEMENTABLE),
    BUFFERNOTZEROTERMINATED("bufferNotZeroTerminated", Implementability.IMPLEMENTABLE),
    CATCHEXCEPTIONBYVALUE("catchExceptionByValue", Implementability.IMPLEMENTABLE),
    CHARARRAYINDEX("charArrayIndex", Implementability.IMPLEMENTABLE),
    CHARBITOP("charBitOp", Implementability.IMPLEMENTABLE),
    CHARLITERALWITHCHARPTRCOMPARE("charLiteralWithCharPtrCompare", Implementability.IMPLEMENTABLE),
    CHECKCASTINTTOCHARANDBACK("checkCastIntToCharAndBack", Implementability.IMPLEMENTABLE),
    CLARIFYCALCULATION("clarifyCalculation", Implementability.IMPLEMENTABLE),
    CLARIFYCONDITION("clarifyCondition", Implementability.IMPLEMENTABLE),
    CLARIFYSTATEMENT("clarifyStatement", Implementability.IMPLEMENTABLE),
    COMMASEPARATEDRETURN("commaSeparatedReturn", Implementability.IMPLEMENTABLE),
    COMPAREBOOLEXPRESSIONWITHINT("compareBoolExpressionWithInt", Implementability.IMPLEMENTABLE),
    COMPARISONERROR("comparisonError", Implementability.IMPLEMENTABLE),
    COMPARISONFUNCTIONISALWAYSTRUEORFALSE("comparisonFunctionIsAlwaysTrueOrFalse", Implementability.IMPLEMENTABLE),
    COMPARISONOFBOOLWITHBOOLERROR("comparisonOfBoolWithBoolError", Implementability.IMPLEMENTABLE),
    COMPARISONOFBOOLWITHINT("comparisonOfBoolWithInt", Implementability.IMPLEMENTABLE),
    COMPARISONOFFUNCRETURNINGBOOLERROR("comparisonOfFuncReturningBoolError", Implementability.IMPLEMENTABLE),
    COMPARISONOFTWOFUNCSRETURNINGBOOLERROR("comparisonOfTwoFuncsReturningBoolError", Implementability.IMPLEMENTABLE),
    CONSTSTATEMENT("constStatement", Implementability.IMPLEMENTABLE),
    COPYCTORPOINTERCOPYING("copyCtorPointerCopying", Implementability.IMPLEMENTABLE),
    COUTCERRMISUSAGE("coutCerrMisusage", Implementability.IMPLEMENTABLE),
    CPPCHECKERROR("cppcheckError", Implementability.IMPLEMENTABLE),
    CSTYLECAST("cstyleCast", Implementability.IMPLEMENTABLE),
    DANGEROUSUSAGESTRTOL("dangerousUsageStrtol", Implementability.IMPLEMENTABLE),
    DEADPOINTER("deadpointer", Implementability.IMPLEMENTABLE),
    DEALLOCDEALLOC("deallocDealloc", Implementability.IMPLEMENTABLE),
    DEALLOCRET("deallocret", Implementability.IMPLEMENTABLE),
    DEALLOCUSE("deallocuse", Implementability.IMPLEMENTABLE),
    DEREFINVALIDITERATOR("derefInvalidIterator", Implementability.IMPLEMENTABLE),
    DIVIDESIZEOF("divideSizeof", Implementability.IMPLEMENTABLE),
    DOUBLEFREE("doubleFree", Implementability.IMPLEMENTABLE),
    DUPLINHERITEDMEMBER("duplInheritedMember", Implementability.IMPLEMENTABLE),
    DUPLICATEBRANCH("duplicateBranch", Implementability.IMPLEMENTABLE),
    DUPLICATEBREAK("duplicateBreak", Implementability.IMPLEMENTABLE),
    DUPLICATEEXPRESSION("duplicateExpression", Implementability.IMPLEMENTABLE),
    DUPLICATEIF("duplicateIf", Implementability.IMPLEMENTABLE),
    ERASE("erase", Implementability.IMPLEMENTABLE),
    ERASEDEREFERENCE("eraseDereference", Implementability.IMPLEMENTABLE),
    EXCEPTDEALLOCTHROW("exceptDeallocThrow", Implementability.IMPLEMENTABLE),
    EXCEPTRETHROWCOPY("exceptRethrowCopy", Implementability.IMPLEMENTABLE),
    EXCEPTTHROWINATTRIBUTENOTHROWFUNCTION("exceptThrowInAttributeNoThrowFunction", Implementability.IMPLEMENTABLE),
    EXCEPTTHROWINDECLSPECNOTHROWFUNCTION("exceptThrowInDeclspecNoThrowFunction", Implementability.IMPLEMENTABLE),
    EXCEPTTHROWINDESTRUCTOR("exceptThrowInDestructor", Implementability.IMPLEMENTABLE),
    EXCEPTTHROWINNOTHROWFUNCTION("exceptThrowInNoThrowFunction", Implementability.IMPLEMENTABLE),
    EXCEPTTHROWINNOEXECPTFUNCTION("exceptThrowInNoexecptFunction", Implementability.IMPLEMENTABLE),
    FFLUSHONINPUTSTREAM("fflushOnInputStream", Implementability.IMPLEMENTABLE),
    FUNCTIONCONST("functionConst", Implementability.IMPLEMENTABLE),
    FUNCTIONSTATIC("functionStatic", Implementability.IMPLEMENTABLE),
    IGNOREDRETURNVALUE("ignoredReturnValue", Implementability.IMPLEMENTABLE),
    INCOMPLETEARRAYFILL("incompleteArrayFill", Implementability.IMPLEMENTABLE),
    INCORRECTLOGICOPERATOR("incorrectLogicOperator", Implementability.IMPLEMENTABLE),
    INCORRECTSTRINGBOOLEANERROR("incorrectStringBooleanError", Implementability.IMPLEMENTABLE),
    INCORRECTSTRINGCOMPARE("incorrectStringCompare", Implementability.IMPLEMENTABLE),
    INCREMENTBOOLEAN("incrementboolean", Implementability.IMPLEMENTABLE),
    INITIALIZERLIST("initializerList", Implementability.IMPLEMENTABLE),
    INSECURECMDLINEARGS("insecureCmdLineArgs", Implementability.IMPLEMENTABLE),
    INTEGEROVERFLOW("integerOverflow", Implementability.IMPLEMENTABLE),
    INVALIDFUNCTIONARG("invalidFunctionArg", Implementability.IMPLEMENTABLE),
    INVALIDFUNCTIONARGBOOL("invalidFunctionArgBool", Implementability.IMPLEMENTABLE),
    INVALIDITERATOR1("invalidIterator1", Implementability.IMPLEMENTABLE),
    INVALIDITERATOR2("invalidIterator2", Implementability.IMPLEMENTABLE),
    INVALIDPOINTER("invalidPointer", Implementability.IMPLEMENTABLE),
    INVALIDPOINTERCAST("invalidPointerCast", Implementability.IMPLEMENTABLE),
    INVALIDPRINTFARGTYPE_FLOAT("invalidPrintfArgType_float", Implementability.IMPLEMENTABLE),
    INVALIDPRINTFARGTYPE_INT("invalidPrintfArgType_int", Implementability.IMPLEMENTABLE),
    INVALIDPRINTFARGTYPE_N("invalidPrintfArgType_n", Implementability.IMPLEMENTABLE),
    INVALIDPRINTFARGTYPE_P("invalidPrintfArgType_p", Implementability.IMPLEMENTABLE),
    INVALIDPRINTFARGTYPE_S("invalidPrintfArgType_s", Implementability.IMPLEMENTABLE),
    INVALIDPRINTFARGTYPE_SINT("invalidPrintfArgType_sint", Implementability.IMPLEMENTABLE),
    INVALIDPRINTFARGTYPE_UINT("invalidPrintfArgType_uint", Implementability.IMPLEMENTABLE),
    INVALIDSCANFARGTYPE("invalidScanfArgType", Implementability.IMPLEMENTABLE),
    INVALIDSCANFARGTYPE_FLOAT("invalidScanfArgType_float", Implementability.IMPLEMENTABLE),
    INVALIDSCANFARGTYPE_INT("invalidScanfArgType_int", Implementability.IMPLEMENTABLE),
    INVALIDSCANFARGTYPE_S("invalidScanfArgType_s", Implementability.IMPLEMENTABLE),
    INVALIDSCANFFORMATWIDTH("invalidScanfFormatWidth", Implementability.IMPLEMENTABLE),
    INVALIDSCANF("invalidscanf", Implementability.IMPLEMENTABLE),
    INVALIDSCANF_LIBC("invalidscanf_libc", Implementability.IMPLEMENTABLE),
    ITERATORS("iterators", Implementability.IMPLEMENTABLE),
    LEAKNOVARFUNCTIONCALL("leakNoVarFunctionCall", Implementability.IMPLEMENTABLE),
    LEAKRETURNVALNOTUSED("leakReturnValNotUsed", Implementability.IMPLEMENTABLE),
    LITERALWITHCHARPTRCOMPARE("literalWithCharPtrCompare", Implementability.IMPLEMENTABLE),
    MALLOCONCLASSERROR("mallocOnClassError", Implementability.IMPLEMENTABLE),
    MALLOCONCLASSWARNING("mallocOnClassWarning", Implementability.IMPLEMENTABLE),
    MEMLEAK("memleak", Implementability.IMPLEMENTABLE),
    MEMLEAKONREALLOC("memleakOnRealloc", Implementability.IMPLEMENTABLE),
    MEMSETCLASS("memsetClass", Implementability.IMPLEMENTABLE),
    MEMSETCLASSFLOAT("memsetClassFloat", Implementability.IMPLEMENTABLE),
    MEMSETCLASSREFERENCE("memsetClassReference", Implementability.IMPLEMENTABLE),
    MEMSETFLOAT("memsetFloat", Implementability.IMPLEMENTABLE),
    MEMSETVALUEOUTOFRANGE("memsetValueOutOfRange", Implementability.IMPLEMENTABLE),
    MEMSETZEROBYTES("memsetZeroBytes", Implementability.IMPLEMENTABLE),
    MISMATCHALLOCDEALLOC("mismatchAllocDealloc", Implementability.IMPLEMENTABLE),
    MISMATCHSIZE("mismatchSize", Implementability.IMPLEMENTABLE),
    MISMATCHINGBITAND("mismatchingBitAnd", Implementability.IMPLEMENTABLE),
    MISMATCHINGCONTAINERS("mismatchingContainers", Implementability.IMPLEMENTABLE),
    MISSINGINCLUDE("missingInclude", Implementability.REJECTED),
    MISSINGINCLUDESYSTEM("missingIncludeSystem", Implementability.REJECTED),
    MISSINGSCANFFORMATWIDTH("missingScanfFormatWidth", Implementability.IMPLEMENTABLE),
    MODULOALWAYSTRUEFALSE("moduloAlwaysTrueFalse", Implementability.IMPLEMENTABLE),
    MULTICONDITION("multiCondition", Implementability.IMPLEMENTABLE),
    MULTIPLYSIZEOF("multiplySizeof", Implementability.IMPLEMENTABLE),
    NANINARITHMETICEXPRESSION("nanInArithmeticExpression", Implementability.IMPLEMENTABLE),
    NEGATIVEINDEX("negativeIndex", Implementability.IMPLEMENTABLE),
    NEGATIVEMEMORYALLOCATIONSIZE("negativeMemoryAllocationSize", Implementability.IMPLEMENTABLE),
    NOCONSTRUCTOR("noConstructor", Implementability.IMPLEMENTABLE),
    NOCOPYCONSTRUCTOR("noCopyConstructor", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSASCTIME("nonreentrantFunctionsasctime", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSCRYPT("nonreentrantFunctionscrypt", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSCTERMID("nonreentrantFunctionsctermid", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSCTIME("nonreentrantFunctionsctime", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSECVT("nonreentrantFunctionsecvt", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSFCVT("nonreentrantFunctionsfcvt", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSFGETGRENT("nonreentrantFunctionsfgetgrent", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSFGETPWENT("nonreentrantFunctionsfgetpwent", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSFGETSPENT("nonreentrantFunctionsfgetspent", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGCVT("nonreentrantFunctionsgcvt", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETGRENT("nonreentrantFunctionsgetgrent", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETGRGID("nonreentrantFunctionsgetgrgid", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETGRNAM("nonreentrantFunctionsgetgrnam", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETHOSTBYADDR("nonreentrantFunctionsgethostbyaddr", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETHOSTBYNAME("nonreentrantFunctionsgethostbyname", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETHOSTBYNAME2("nonreentrantFunctionsgethostbyname2", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETHOSTENT("nonreentrantFunctionsgethostent", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETLOGIN("nonreentrantFunctionsgetlogin", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETNETBYADDR("nonreentrantFunctionsgetnetbyaddr", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETNETBYNAME("nonreentrantFunctionsgetnetbyname", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETNETGRENT("nonreentrantFunctionsgetnetgrent", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETPROTOBYNAME("nonreentrantFunctionsgetprotobyname", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETPWENT("nonreentrantFunctionsgetpwent", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETPWNAM("nonreentrantFunctionsgetpwnam", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETPWUID("nonreentrantFunctionsgetpwuid", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETRPCBYNAME("nonreentrantFunctionsgetrpcbyname", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETRPCBYNUMBER("nonreentrantFunctionsgetrpcbynumber", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETRPCENT("nonreentrantFunctionsgetrpcent", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETSERVBYNAME("nonreentrantFunctionsgetservbyname", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETSERVBYPORT("nonreentrantFunctionsgetservbyport", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETSERVENT("nonreentrantFunctionsgetservent", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETSPENT("nonreentrantFunctionsgetspent", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGETSPNAM("nonreentrantFunctionsgetspnam", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSGMTIME("nonreentrantFunctionsgmtime", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSLOCALTIME("nonreentrantFunctionslocaltime", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSRAND("nonreentrantFunctionsrand", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSREADDIR("nonreentrantFunctionsreaddir", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSSTRTOK("nonreentrantFunctionsstrtok", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSTEMPNAM("nonreentrantFunctionstempnam", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSTMPNAM("nonreentrantFunctionstmpnam", Implementability.IMPLEMENTABLE),
    NONREENTRANTFUNCTIONSTTYNAME("nonreentrantFunctionsttyname", Implementability.IMPLEMENTABLE),
    NULLPOINTER("nullPointer", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSASCTIME("obsoleteFunctionsasctime", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSASCTIME_R("obsoleteFunctionsasctime_r", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSBCMP("obsoleteFunctionsbcmp", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSBCOPY("obsoleteFunctionsbcopy", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSBSD_SIGNAL("obsoleteFunctionsbsd_signal", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSBZERO("obsoleteFunctionsbzero", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSCTIME("obsoleteFunctionsctime", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSCTIME_R("obsoleteFunctionsctime_r", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSECVT("obsoleteFunctionsecvt", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSFCVT("obsoleteFunctionsfcvt", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSFTIME("obsoleteFunctionsftime", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSGCVT("obsoleteFunctionsgcvt", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSGETCONTEXT("obsoleteFunctionsgetcontext", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSGETHOSTBYADDR("obsoleteFunctionsgethostbyaddr", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSGETHOSTBYNAME("obsoleteFunctionsgethostbyname", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSGETWD("obsoleteFunctionsgetwd", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSINDEX("obsoleteFunctionsindex", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSMAKECONTEXT("obsoleteFunctionsmakecontext", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSPTHREAD_ATTR_GETSTACKADDR("obsoleteFunctionspthread_attr_getstackaddr", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSPTHREAD_ATTR_SETSTACKADDR("obsoleteFunctionspthread_attr_setstackaddr", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSRAND_R("obsoleteFunctionsrand_r", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSRINDEX("obsoleteFunctionsrindex", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSSCALBLN("obsoleteFunctionsscalbln", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSSWAPCONTEXT("obsoleteFunctionsswapcontext", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSTMPNAM("obsoleteFunctionstmpnam", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSTMPNAM_R("obsoleteFunctionstmpnam_r", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSUALARM("obsoleteFunctionsualarm", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSUSLEEP("obsoleteFunctionsusleep", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSUTIME("obsoleteFunctionsutime", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSVFORK("obsoleteFunctionsvfork", Implementability.IMPLEMENTABLE),
    OBSOLETEFUNCTIONSWCSWCS("obsoleteFunctionswcswcs", Implementability.IMPLEMENTABLE),
    OPERATOREQ("operatorEq", Implementability.IMPLEMENTABLE),
    OPERATOREQRETREFTHIS("operatorEqRetRefThis", Implementability.IMPLEMENTABLE),
    OPERATOREQTOSELF("operatorEqToSelf", Implementability.IMPLEMENTABLE),
    OPERATOREQVARERROR("operatorEqVarError", Implementability.IMPLEMENTABLE),
    OPPOSITEINNERCONDITION("oppositeInnerCondition", Implementability.IMPLEMENTABLE),
    OUTOFBOUNDS("outOfBounds", Implementability.IMPLEMENTABLE),
    PASSEDBYVALUE("passedByValue", Implementability.IMPLEMENTABLE),
    POINTERARITHBOOL("pointerArithBool", Implementability.IMPLEMENTABLE),
    POINTERLESSTHANZERO("pointerLessThanZero", Implementability.IMPLEMENTABLE),
    POINTEROUTOFBOUNDS("pointerOutOfBounds", Implementability.IMPLEMENTABLE),
    POINTERPOSITIVE("pointerPositive", Implementability.IMPLEMENTABLE),
    POINTERSIZE("pointerSize", Implementability.IMPLEMENTABLE),
    POSSIBLEBUFFERACCESSOUTOFBOUNDS("possibleBufferAccessOutOfBounds", Implementability.IMPLEMENTABLE),
    POSSIBLEREADLINKBUFFEROVERRUN("possibleReadlinkBufferOverrun", Implementability.IMPLEMENTABLE),
    POSTFIXOPERATOR("postfixOperator", Implementability.IMPLEMENTABLE),
    PREPROCESSORERRORDIRECTIVE("preprocessorErrorDirective", Implementability.IMPLEMENTABLE),
    PUBLICALLOCATIONERROR("publicAllocationError", Implementability.IMPLEMENTABLE),
    READWRITEONLYFILE("readWriteOnlyFile", Implementability.IMPLEMENTABLE),
    READEMPTYCONTAINER("reademptycontainer", Implementability.IMPLEMENTABLE),
    REDUNDANTASSIGNINSWITCH("redundantAssignInSwitch", Implementability.IMPLEMENTABLE),
    REDUNDANTASSIGNMENT("redundantAssignment", Implementability.IMPLEMENTABLE),
    REDUNDANTCONDITION("redundantCondition", Implementability.IMPLEMENTABLE),
    REDUNDANTCOPY("redundantCopy", Implementability.IMPLEMENTABLE),
    REDUNDANTCOPYINSWITCH("redundantCopyInSwitch", Implementability.IMPLEMENTABLE),
    REDUNDANTCOPYLOCALCONST("redundantCopyLocalConst", Implementability.IMPLEMENTABLE),
    REDUNDANTIFREMOVE("redundantIfRemove", Implementability.IMPLEMENTABLE),
    REDUNDANTOPERATIONINSWITCH("redundantOperationInSwitch", Implementability.IMPLEMENTABLE),
    RESOURCELEAK("resourceLeak", Implementability.IMPLEMENTABLE),
    RETURNADDRESSOFAUTOVARIABLE("returnAddressOfAutoVariable", Implementability.IMPLEMENTABLE),
    RETURNADDRESSOFFUNCTIONPARAMETER("returnAddressOfFunctionParameter", Implementability.IMPLEMENTABLE),
    RETURNAUTOCSTR("returnAutocstr", Implementability.IMPLEMENTABLE),
    RETURNLOCALVARIABLE("returnLocalVariable", Implementability.IMPLEMENTABLE),
    RETURNREFERENCE("returnReference", Implementability.IMPLEMENTABLE),
    RETURNTEMPPOINTER("returnTempPointer", Implementability.IMPLEMENTABLE),
    RETURNTEMPREFERENCE("returnTempReference", Implementability.IMPLEMENTABLE),
    SECONDALWAYSTRUEFALSEWHENFIRSTTRUE("secondAlwaysTrueFalseWhenFirstTrue", Implementability.IMPLEMENTABLE),
    SEEKONAPPENDEDFILE("seekOnAppendedFile", Implementability.IMPLEMENTABLE),
    SELFASSIGNMENT("selfAssignment", Implementability.IMPLEMENTABLE),
    SELFINITIALIZATION("selfInitialization", Implementability.IMPLEMENTABLE),
    SHIFTNEGATIVE("shiftNegative", Implementability.IMPLEMENTABLE),
    SHIFTTOOMANYBITS("shiftTooManyBits", Implementability.IMPLEMENTABLE),
    SIGNCONVERSION("signConversion", Implementability.IMPLEMENTABLE),
    SIZEOFCALCULATION("sizeofCalculation", Implementability.IMPLEMENTABLE),
    SIZEOFDEREFERENCEDVOIDPOINTER("sizeofDereferencedVoidPointer", Implementability.IMPLEMENTABLE),
    SIZEOFDIVISIONMEMFUNC("sizeofDivisionMemfunc", Implementability.IMPLEMENTABLE),
    SIZEOFVOID("sizeofVoid", Implementability.IMPLEMENTABLE),
    SIZEOFSIZEOF("sizeofsizeof", Implementability.IMPLEMENTABLE),
    SIZEOFWITHNUMERICPARAMETER("sizeofwithnumericparameter", Implementability.IMPLEMENTABLE),
    SIZEOFWITHSILENTARRAYPOINTER("sizeofwithsilentarraypointer", Implementability.IMPLEMENTABLE),
    SPRINTFOVERLAPPINGDATA("sprintfOverlappingData", Implementability.IMPLEMENTABLE),
    STATICSTRINGCOMPARE("staticStringCompare", Implementability.IMPLEMENTABLE),
    STLBOUNDARIES("stlBoundaries", Implementability.IMPLEMENTABLE),
    STLBOUNDRIES("stlBoundries", Implementability.IMPLEMENTABLE),
    STLIFFIND("stlIfFind", Implementability.IMPLEMENTABLE),
    STLIFSTRFIND("stlIfStrFind", Implementability.IMPLEMENTABLE),
    STLOUTOFBOUNDS("stlOutOfBounds", Implementability.IMPLEMENTABLE),
    STLSIZE("stlSize", Implementability.IMPLEMENTABLE),
    STLCSTR("stlcstr", Implementability.IMPLEMENTABLE),
    STLCSTRPARAM("stlcstrParam", Implementability.IMPLEMENTABLE),
    STLCSTRRETURN("stlcstrReturn", Implementability.IMPLEMENTABLE),
    STRPLUSCHAR("strPlusChar", Implementability.IMPLEMENTABLE),
    STRINGCOMPARE("stringCompare", Implementability.IMPLEMENTABLE),
    SUSPICIOUSCASE("suspiciousCase", Implementability.IMPLEMENTABLE),
    SUSPICIOUSEQUALITYCOMPARISON("suspiciousEqualityComparison", Implementability.IMPLEMENTABLE),
    SUSPICIOUSSEMICOLON("suspiciousSemicolon", Implementability.IMPLEMENTABLE),
    SWITCHCASEFALLTHROUGH("switchCaseFallThrough", Implementability.IMPLEMENTABLE),
    SYNTAXERROR("syntaxError", Implementability.IMPLEMENTABLE),
    TERMINATESTRNCPY("terminateStrncpy", Implementability.IMPLEMENTABLE),
    THISSUBTRACTION("thisSubtraction", Implementability.IMPLEMENTABLE),
    TOOBIGSLEEPTIME("tooBigSleepTime", Implementability.IMPLEMENTABLE),
    TOOMANYCONFIGS("toomanyconfigs", Implementability.REJECTED),
    UDIVERROR("udivError", Implementability.IMPLEMENTABLE),
    UNASSIGNEDVARIABLE("unassignedVariable", Implementability.IMPLEMENTABLE),
    UNHANDLEDEXCEPTIONSPECIFICATION("unhandledExceptionSpecification", Implementability.IMPLEMENTABLE),
    UNINITMEMBERVAR("uninitMemberVar", Implementability.IMPLEMENTABLE),
    UNINITSTRUCTMEMBER("uninitStructMember", Implementability.IMPLEMENTABLE),
    UNINITDATA("uninitdata", Implementability.IMPLEMENTABLE),
    UNINITSTRING("uninitstring", Implementability.IMPLEMENTABLE),
    UNINITVAR("uninitvar", Implementability.IMPLEMENTABLE),
    UNNECESSARYFORWARDDECLARATION("unnecessaryForwardDeclaration", Implementability.IMPLEMENTABLE),
    UNNECESSARYQUALIFICATION("unnecessaryQualification", Implementability.IMPLEMENTABLE),
    UNPRECISEMATHCALL("unpreciseMathCall", Implementability.IMPLEMENTABLE),
    UNREACHABLECODE("unreachableCode", Implementability.IMPLEMENTABLE),
    UNREADVARIABLE("unreadVariable", Implementability.IMPLEMENTABLE),
    UNSAFECLASSCANLEAK("unsafeClassCanLeak", Implementability.IMPLEMENTABLE),
    UNSIGNEDLESSTHANZERO("unsignedLessThanZero", Implementability.IMPLEMENTABLE),
    UNSIGNEDPOSITIVE("unsignedPositive", Implementability.IMPLEMENTABLE),
    UNUSEDALLOCATEDMEMORY("unusedAllocatedMemory", Implementability.IMPLEMENTABLE),
    UNUSEDFUNCTION("unusedFunction", Implementability.IMPLEMENTABLE),
    UNUSEDPRIVATEFUNCTION("unusedPrivateFunction", Implementability.IMPLEMENTABLE),
    UNUSEDSCOPEDOBJECT("unusedScopedObject", Implementability.IMPLEMENTABLE),
    UNUSEDSTRUCTMEMBER("unusedStructMember", Implementability.IMPLEMENTABLE),
    UNUSEDVARIABLE("unusedVariable", Implementability.IMPLEMENTABLE),
    USEAUTOPOINTERARRAY("useAutoPointerArray", Implementability.IMPLEMENTABLE),
    USEAUTOPOINTERCONTAINER("useAutoPointerContainer", Implementability.IMPLEMENTABLE),
    USEAUTOPOINTERCOPY("useAutoPointerCopy", Implementability.IMPLEMENTABLE),
    USECLOSEDFILE("useClosedFile", Implementability.IMPLEMENTABLE),
    USEINITIALIZATIONLIST("useInitializationList", Implementability.IMPLEMENTABLE),
    USELESSASSIGNMENTARG("uselessAssignmentArg", Implementability.IMPLEMENTABLE),
    USELESSASSIGNMENTPTRARG("uselessAssignmentPtrArg", Implementability.IMPLEMENTABLE),
    USELESSCALLSCOMPARE("uselessCallsCompare", Implementability.IMPLEMENTABLE),
    USELESSCALLSEMPTY("uselessCallsEmpty", Implementability.IMPLEMENTABLE),
    USELESSCALLSREMOVE("uselessCallsRemove", Implementability.IMPLEMENTABLE),
    USELESSCALLSSUBSTR("uselessCallsSubstr", Implementability.IMPLEMENTABLE),
    USELESSCALLSSWAP("uselessCallsSwap", Implementability.IMPLEMENTABLE),
    VA_END_MISSING("va_end_missing", Implementability.IMPLEMENTABLE),
    VA_LIST_USEDBEFORESTARTED("va_list_usedBeforeStarted", Implementability.IMPLEMENTABLE),
    VA_START_REFERENCEPASSED("va_start_referencePassed", Implementability.IMPLEMENTABLE),
    VA_START_SUBSEQUENTCALLS("va_start_subsequentCalls", Implementability.IMPLEMENTABLE),
    VA_START_WRONGPARAMETER("va_start_wrongParameter", Implementability.IMPLEMENTABLE),
    VARFUNCNULLUB("varFuncNullUB", Implementability.IMPLEMENTABLE),
    VARIABLEHIDINGENUM("variableHidingEnum", Implementability.IMPLEMENTABLE),
    VARIABLEHIDINGTYPEDEF("variableHidingTypedef", Implementability.IMPLEMENTABLE),
    VARIABLESCOPE("variableScope", Implementability.IMPLEMENTABLE),
    VIRTUALDESTRUCTOR("virtualDestructor", Implementability.IMPLEMENTABLE),
    WRITEOUTSIDEBUFFERSIZE("writeOutsideBufferSize", Implementability.IMPLEMENTABLE),
    WRITEREADONLYFILE("writeReadOnlyFile", Implementability.IMPLEMENTABLE),
    WRONGPIPEPARAMETERSIZE("wrongPipeParameterSize", Implementability.IMPLEMENTABLE),
    WRONGPRINTFSCANFARGNUM("wrongPrintfScanfArgNum", Implementability.IMPLEMENTABLE),
    WRONGPRINTFSCANFARGS("wrongPrintfScanfArgs", Implementability.IMPLEMENTABLE),
    WRONGPRINTFSCANFPARAMETERPOSITIONERROR("wrongPrintfScanfParameterPositionError", Implementability.IMPLEMENTABLE),
    WRONGCCTYPECALL("wrongcctypecall", Implementability.IMPLEMENTABLE),
    WRONGMATHCALL("wrongmathcall", Implementability.IMPLEMENTABLE),
    ZERODIV("zerodiv", Implementability.IMPLEMENTABLE),
    ZERODIVCOND("zerodivcond", Implementability.IMPLEMENTABLE);


    private String title;
    private Implementability implementability;

    CppCheckRule(String title, Implementability implementability) {
      this.title = title;
      this.implementability = implementability;
    }

    @Override
    public String getCodingStandardRuleId() {

      return title;
    }

    @Override
    public Implementability getImplementability() {

      return implementability;
    }
  }
}
