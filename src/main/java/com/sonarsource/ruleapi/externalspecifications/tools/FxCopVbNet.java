package com.sonarsource.ruleapi.externalspecifications.tools;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableExternalTool;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.List;


public class FxCopVbNet extends AbstractReportableExternalTool{

  private String toolName = "FxCop";
  private Language language = Language.VBNET;

  private String reportName = toolName + " " + language.getRspec();

  @Override
  public Language getLanguage() {

    return language;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return FxCopRule.values();
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

    return rule.getFxCop();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setFxCop(ids);
  }

  public enum FxCopRule implements CodingStandardRule {
    ABSTRACTTYPESSHOULDNOTHAVECONSTRUCTORS("AbstractTypesShouldNotHaveConstructors", Implementability.IMPLEMENTABLE),
    APTCAMETHODSSHOULDONLYCALLAPTCAMETHODS("AptcaMethodsShouldOnlyCallAptcaMethods", Implementability.IMPLEMENTABLE),
    APTCATYPESSHOULDONLYEXTENDAPTCABASETYPES("AptcaTypesShouldOnlyExtendAptcaBaseTypes", Implementability.IMPLEMENTABLE),
    ARRAYFIELDSSHOULDNOTBEREADONLY("ArrayFieldsShouldNotBeReadOnly", Implementability.IMPLEMENTABLE),
    ASSEMBLIESSHOULDHAVEVALIDSTRONGNAMES("AssembliesShouldHaveValidStrongNames", Implementability.IMPLEMENTABLE),
    ATTRIBUTESTRINGLITERALSSHOULDPARSECORRECTLY("AttributeStringLiteralsShouldParseCorrectly", Implementability.IMPLEMENTABLE),
    AUTOLAYOUTTYPESSHOULDNOTBECOMVISIBLE("AutoLayoutTypesShouldNotBeComVisible", Implementability.IMPLEMENTABLE),
    AVOIDCALLINGPROBLEMATICMETHODS("AvoidCallingProblematicMethods", Implementability.IMPLEMENTABLE),
    AVOIDDUPLICATEACCELERATORS("AvoidDuplicateAccelerators", Implementability.IMPLEMENTABLE),
    AVOIDEMPTYINTERFACES("AvoidEmptyInterfaces", Implementability.IMPLEMENTABLE),
    AVOIDEXCESSIVECLASSCOUPLING("AvoidExcessiveClassCoupling", Implementability.IMPLEMENTABLE),
    AVOIDEXCESSIVECOMPLEXITY("AvoidExcessiveComplexity", Implementability.IMPLEMENTABLE),
    AVOIDEXCESSIVEINHERITANCE("AvoidExcessiveInheritance", Implementability.IMPLEMENTABLE),
    AVOIDEXCESSIVELOCALS("AvoidExcessiveLocals", Implementability.IMPLEMENTABLE),
    AVOIDEXCESSIVEPARAMETERSONGENERICTYPES("AvoidExcessiveParametersOnGenericTypes", Implementability.IMPLEMENTABLE),
    AVOIDINT64ARGUMENTSFORVB6CLIENTS("AvoidInt64ArgumentsForVB6Clients", Implementability.IMPLEMENTABLE),
    AVOIDNAMESPACESWITHFEWTYPES("AvoidNamespacesWithFewTypes", Implementability.IMPLEMENTABLE),
    AVOIDNONPUBLICFIELDSINCOMVISIBLEVALUETYPES("AvoidNonpublicFieldsInComVisibleValueTypes", Implementability.IMPLEMENTABLE),
    AVOIDOUTPARAMETERS("AvoidOutParameters", Implementability.IMPLEMENTABLE),
    AVOIDOVERLOADSINCOMVISIBLEINTERFACES("AvoidOverloadsInComVisibleInterfaces", Implementability.IMPLEMENTABLE),
    AVOIDSTATICMEMBERSINCOMVISIBLETYPES("AvoidStaticMembersInComVisibleTypes", Implementability.IMPLEMENTABLE),
    AVOIDUNCALLEDPRIVATECODE("AvoidUncalledPrivateCode", Implementability.IMPLEMENTABLE),
    AVOIDUNINSTANTIATEDINTERNALCLASSES("AvoidUninstantiatedInternalClasses", Implementability.IMPLEMENTABLE),
    AVOIDUNMANTAINABLECODE("AvoidUnmantainableCode", Implementability.IMPLEMENTABLE),
    AVOIDUNSEALEDATTRIBUTES("AvoidUnsealedAttributes", Implementability.IMPLEMENTABLE),
    AVOIDUNUSEDPRIVATEFIELDS("AvoidUnusedPrivateFields", Implementability.IMPLEMENTABLE),
    CA2151("CA2151", Implementability.IMPLEMENTABLE),
    CALLBASECLASSMETHODSONISERIALIZABLETYPES("CallBaseClassMethodsOnISerializableTypes", Implementability.IMPLEMENTABLE),
    CALLGCKEEPALIVEWHENUSINGNATIVERESOURCES("CallGCKeepAliveWhenUsingNativeResources", Implementability.IMPLEMENTABLE),
    CALLGCSUPPRESSFINALIZECORRECTLY("CallGCSuppressFinalizeCorrectly", Implementability.IMPLEMENTABLE),
    CALLGETLASTERRORIMMEDIATELYAFTERPINVOKE("CallGetLastErrorImmediatelyAfterPInvoke", Implementability.IMPLEMENTABLE),
    CATCHNONCLSCOMPLIANTEXCEPTIONSINGENERALHANDLERS("CatchNonClsCompliantExceptionsInGeneralHandlers", Implementability.IMPLEMENTABLE),
    COLLECTIONPROPERTIESSHOULDBEREADONLY("CollectionPropertiesShouldBeReadOnly", Implementability.IMPLEMENTABLE),
    COLLECTIONSSHOULDIMPLEMENTGENERICINTERFACE("CollectionsShouldImplementGenericInterface", Implementability.IMPLEMENTABLE),
    COMPOUNDWORDSSHOULDBECASEDCORRECTLY("CompoundWordsShouldBeCasedCorrectly", Implementability.IMPLEMENTABLE),
    COMREGISTRATIONMETHODSSHOULDBEMATCHED("ComRegistrationMethodsShouldBeMatched", Implementability.IMPLEMENTABLE),
    COMREGISTRATIONMETHODSSHOULDNOTBEVISIBLE("ComRegistrationMethodsShouldNotBeVisible", Implementability.IMPLEMENTABLE),
    COMVISIBLETYPEBASETYPESSHOULDBECOMVISIBLE("ComVisibleTypeBaseTypesShouldBeComVisible", Implementability.IMPLEMENTABLE),
    COMVISIBLETYPESSHOULDBECREATABLE("ComVisibleTypesShouldBeCreatable", Implementability.IMPLEMENTABLE),
    CONSIDERPASSINGBASETYPESASPARAMETERS("ConsiderPassingBaseTypesAsParameters", Implementability.IMPLEMENTABLE),
    CONSTANTSSHOULDBETRANSPARENT("ConstantsShouldBeTransparent", Implementability.IMPLEMENTABLE),
    CRITICALTYPESMUSTNOTPARTICIPATEINTYPEEQUIVALENCE("CriticalTypesMustNotParticipateInTypeEquivalence", Implementability.IMPLEMENTABLE),
    DECLAREEVENTHANDLERSCORRECTLY("DeclareEventHandlersCorrectly", Implementability.IMPLEMENTABLE),
    DECLAREPINVOKESCORRECTLY("DeclarePInvokesCorrectly", Implementability.IMPLEMENTABLE),
    DECLARETYPESINNAMESPACES("DeclareTypesInNamespaces", Implementability.IMPLEMENTABLE),
    DEFAULTCONSTRUCTORSMUSTHAVECONSISTENTTRANSPARENCY("DefaultConstructorsMustHaveConsistentTransparency", Implementability.IMPLEMENTABLE),
    DEFAULTPARAMETERSSHOULDNOTBEUSED("DefaultParametersShouldNotBeUsed", Implementability.IMPLEMENTABLE),
    DEFINEACCESSORSFORATTRIBUTEARGUMENTS("DefineAccessorsForAttributeArguments", Implementability.IMPLEMENTABLE),
    DELEGATESMUSTBINDWITHCONSISTENTTRANSPARENCY("DelegatesMustBindWithConsistentTransparency", Implementability.IMPLEMENTABLE),
    DISPOSABLEFIELDSSHOULDBEDISPOSED("DisposableFieldsShouldBeDisposed", Implementability.IMPLEMENTABLE),
    DISPOSABLETYPESSHOULDDECLAREFINALIZER("DisposableTypesShouldDeclareFinalizer", Implementability.IMPLEMENTABLE),
    DISPOSEMETHODSSHOULDCALLBASECLASSDISPOSE("DisposeMethodsShouldCallBaseClassDispose", Implementability.IMPLEMENTABLE),
    DISPOSEOBJECTSBEFORELOSINGSCOPE("DisposeObjectsBeforeLosingScope", Implementability.IMPLEMENTABLE),
    DONOTCALLOVERRIDABLEMETHODSINCONSTRUCTORS("DoNotCallOverridableMethodsInConstructors", Implementability.IMPLEMENTABLE),
    DONOTCASTUNNECESSARILY("DoNotCastUnnecessarily", Implementability.IMPLEMENTABLE),
    DONOTCATCHGENERALEXCEPTIONTYPES("DoNotCatchGeneralExceptionTypes", Implementability.IMPLEMENTABLE),
    DONOTDECLAREPROTECTEDMEMBERSINSEALEDTYPES("DoNotDeclareProtectedMembersInSealedTypes", Implementability.IMPLEMENTABLE),
    DONOTDECLAREREADONLYMUTABLEREFERENCETYPES("DoNotDeclareReadOnlyMutableReferenceTypes", Implementability.IMPLEMENTABLE),
    DONOTDECLARESTATICMEMBERSONGENERICTYPES("DoNotDeclareStaticMembersOnGenericTypes", Implementability.IMPLEMENTABLE),
    DONOTDECLAREVIRTUALMEMBERSINSEALEDTYPES("DoNotDeclareVirtualMembersInSealedTypes", Implementability.IMPLEMENTABLE),
    DONOTDECLAREVISIBLEINSTANCEFIELDS("DoNotDeclareVisibleInstanceFields", Implementability.IMPLEMENTABLE),
    DONOTDECREASEINHERITEDMEMBERVISIBILITY("DoNotDecreaseInheritedMemberVisibility", Implementability.IMPLEMENTABLE),
    DONOTDISPOSEOBJECTSMULTIPLETIMES("DoNotDisposeObjectsMultipleTimes", Implementability.IMPLEMENTABLE),
    DONOTEXPOSEGENERICLISTS("DoNotExposeGenericLists", Implementability.IMPLEMENTABLE),
    DONOTHARDCODELOCALESPECIFICSTRINGS("DoNotHardcodeLocaleSpecificStrings", Implementability.IMPLEMENTABLE),
    DONOTHIDEBASECLASSMETHODS("DoNotHideBaseClassMethods", Implementability.IMPLEMENTABLE),
    DONOTIGNOREMETHODRESULTS("DoNotIgnoreMethodResults", Implementability.IMPLEMENTABLE),
    DONOTINDIRECTLYEXPOSEMETHODSWITHLINKDEMANDS("DoNotIndirectlyExposeMethodsWithLinkDemands", Implementability.IMPLEMENTABLE),
    DONOTLOCKONOBJECTSWITHWEAKIDENTITY("DoNotLockOnObjectsWithWeakIdentity", Implementability.IMPLEMENTABLE),
    DONOTMARKENUMSWITHFLAGS("DoNotMarkEnumsWithFlags", Implementability.IMPLEMENTABLE),
    DONOTMARKSERVICEDCOMPONENTSWITHWEBMETHOD("DoNotMarkServicedComponentsWithWebMethod", Implementability.IMPLEMENTABLE),
    DONOTNAMEENUMVALUESRESERVED("DoNotNameEnumValuesReserved", Implementability.IMPLEMENTABLE),
    DONOTNESTGENERICTYPESINMEMBERSIGNATURES("DoNotNestGenericTypesInMemberSignatures", Implementability.IMPLEMENTABLE),
    DONOTOVERLOADOPERATOREQUALSONREFERENCETYPES("DoNotOverloadOperatorEqualsOnReferenceTypes", Implementability.IMPLEMENTABLE),
    DONOTPASSLITERALSASLOCALIZEDPARAMETERS("DoNotPassLiteralsAsLocalizedParameters", Implementability.IMPLEMENTABLE),
    DONOTPASSTYPESBYREFERENCE("DoNotPassTypesByReference", Implementability.IMPLEMENTABLE),
    DONOTPREFIXENUMVALUESWITHTYPENAME("DoNotPrefixEnumValuesWithTypeName", Implementability.IMPLEMENTABLE),
    DONOTRAISEEXCEPTIONSINEXCEPTIONCLAUSES("DoNotRaiseExceptionsInExceptionClauses", Implementability.IMPLEMENTABLE),
    DONOTRAISEEXCEPTIONSINUNEXPECTEDLOCATIONS("DoNotRaiseExceptionsInUnexpectedLocations", Implementability.IMPLEMENTABLE),
    DONOTRAISERESERVEDEXCEPTIONTYPES("DoNotRaiseReservedExceptionTypes", Implementability.IMPLEMENTABLE),
    DONOTSHIPUNRELEASEDRESOURCEFORMATS("DoNotShipUnreleasedResourceFormats", Implementability.IMPLEMENTABLE),
    DONOTTREATFIBERSASTHREADS("DoNotTreatFibersAsThreads", Implementability.IMPLEMENTABLE),
    DONOTUSEAUTODUALCLASSINTERFACETYPE("DoNotUseAutoDualClassInterfaceType", Implementability.IMPLEMENTABLE),
    DONOTUSEIDLEPROCESSPRIORITY("DoNotUseIdleProcessPriority", Implementability.IMPLEMENTABLE),
    DONOTUSETIMERSTHATPREVENTPOWERSTATECHANGES("DoNotUseTimersThatPreventPowerStateChanges", Implementability.IMPLEMENTABLE),
    ENUMERATORSSHOULDBESTRONGLYTYPED("EnumeratorsShouldBeStronglyTyped", Implementability.IMPLEMENTABLE),
    ENUMSSHOULDHAVEZEROVALUE("EnumsShouldHaveZeroValue", Implementability.IMPLEMENTABLE),
    ENUMSTORAGESHOULDBEINT32("EnumStorageShouldBeInt32", Implementability.IMPLEMENTABLE),
    EVENTSSHOULDNOTHAVEBEFOREORAFTERPREFIX("EventsShouldNotHaveBeforeOrAfterPrefix", Implementability.IMPLEMENTABLE),
    EXCEPTIONSSHOULDBEPUBLIC("ExceptionsShouldBePublic", Implementability.IMPLEMENTABLE),
    FINALIZERSSHOULDBEPROTECTED("FinalizersShouldBeProtected", Implementability.IMPLEMENTABLE),
    FINALIZERSSHOULDCALLBASECLASSFINALIZER("FinalizersShouldCallBaseClassFinalizer", Implementability.IMPLEMENTABLE),
    FLAGSENUMSSHOULDHAVEPLURALNAMES("FlagsEnumsShouldHavePluralNames", Implementability.IMPLEMENTABLE),
    GENERICMETHODSSHOULDPROVIDETYPEPARAMETER("GenericMethodsShouldProvideTypeParameter", Implementability.IMPLEMENTABLE),
    ICOLLECTIONIMPLEMENTATIONSHAVESTRONGLYTYPEDMEMBERS("ICollectionImplementationsHaveStronglyTypedMembers", Implementability.IMPLEMENTABLE),
    IDENTIFIERSSHOULDBECASEDCORRECTLY("IdentifiersShouldBeCasedCorrectly", Implementability.IMPLEMENTABLE),
    IDENTIFIERSSHOULDBESPELLEDCORRECTLY("IdentifiersShouldBeSpelledCorrectly", Implementability.IMPLEMENTABLE),
    IDENTIFIERSSHOULDDIFFERBYMORETHANCASE("IdentifiersShouldDifferByMoreThanCase", Implementability.IMPLEMENTABLE),
    IDENTIFIERSSHOULDHAVECORRECTPREFIX("IdentifiersShouldHaveCorrectPrefix", Implementability.IMPLEMENTABLE),
    IDENTIFIERSSHOULDHAVECORRECTSUFFIX("IdentifiersShouldHaveCorrectSuffix", Implementability.IMPLEMENTABLE),
    IDENTIFIERSSHOULDNOTCONTAINTYPENAMES("IdentifiersShouldNotContainTypeNames", Implementability.IMPLEMENTABLE),
    IDENTIFIERSSHOULDNOTCONTAINUNDERSCORES("IdentifiersShouldNotContainUnderscores", Implementability.IMPLEMENTABLE),
    IDENTIFIERSSHOULDNOTHAVEINCORRECTPREFIX("IdentifiersShouldNotHaveIncorrectPrefix", Implementability.IMPLEMENTABLE),
    IDENTIFIERSSHOULDNOTHAVEINCORRECTSUFFIX("IdentifiersShouldNotHaveIncorrectSuffix", Implementability.IMPLEMENTABLE),
    IDENTIFIERSSHOULDNOTMATCHKEYWORDS("IdentifiersShouldNotMatchKeywords", Implementability.IMPLEMENTABLE),
    IMPLEMENTIDISPOSABLECORRECTLY("ImplementIDisposableCorrectly", Implementability.IMPLEMENTABLE),
    IMPLEMENTISERIALIZABLECORRECTLY("ImplementISerializableCorrectly", Implementability.IMPLEMENTABLE),
    IMPLEMENTSERIALIZATIONCONSTRUCTORS("ImplementSerializationConstructors", Implementability.IMPLEMENTABLE),
    IMPLEMENTSERIALIZATIONMETHODSCORRECTLY("ImplementSerializationMethodsCorrectly", Implementability.IMPLEMENTABLE),
    IMPLEMENTSTANDARDEXCEPTIONCONSTRUCTORS("ImplementStandardExceptionConstructors", Implementability.IMPLEMENTABLE),
    INDEXERSSHOULDNOTBEMULTIDIMENSIONAL("IndexersShouldNotBeMultidimensional", Implementability.IMPLEMENTABLE),
    INITIALIZEREFERENCETYPESTATICFIELDSINLINE("InitializeReferenceTypeStaticFieldsInline", Implementability.IMPLEMENTABLE),
    INITIALIZEVALUETYPESTATICFIELDSINLINE("InitializeValueTypeStaticFieldsInline", Implementability.IMPLEMENTABLE),
    INSTANTIATEARGUMENTEXCEPTIONSCORRECTLY("InstantiateArgumentExceptionsCorrectly", Implementability.IMPLEMENTABLE),
    INTERFACEMETHODSSHOULDBECALLABLEBYCHILDTYPES("InterfaceMethodsShouldBeCallableByChildTypes", Implementability.IMPLEMENTABLE),
    LISTSARESTRONGLYTYPED("ListsAreStronglyTyped", Implementability.IMPLEMENTABLE),
    LITERALSSHOULDBESPELLEDCORRECTLY("LiteralsShouldBeSpelledCorrectly", Implementability.IMPLEMENTABLE),
    MARKALLNONSERIALIZABLEFIELDS("MarkAllNonSerializableFields", Implementability.IMPLEMENTABLE),
    MARKASSEMBLIESWITHASSEMBLYVERSION("MarkAssembliesWithAssemblyVersion", Implementability.IMPLEMENTABLE),
    MARKASSEMBLIESWITHCLSCOMPLIANT("MarkAssembliesWithClsCompliant", Implementability.IMPLEMENTABLE),
    MARKASSEMBLIESWITHCOMVISIBLE("MarkAssembliesWithComVisible", Implementability.IMPLEMENTABLE),
    MARKASSEMBLIESWITHNEUTRALRESOURCESLANGUAGE("MarkAssembliesWithNeutralResourcesLanguage", Implementability.IMPLEMENTABLE),
    MARKATTRIBUTESWITHATTRIBUTEUSAGE("MarkAttributesWithAttributeUsage", Implementability.IMPLEMENTABLE),
    MARKBOOLEANPINVOKEARGUMENTSWITHMARSHALAS("MarkBooleanPInvokeArgumentsWithMarshalAs", Implementability.IMPLEMENTABLE),
    MARKCOMSOURCEINTERFACESASIDISPATCH("MarkComSourceInterfacesAsIDispatch", Implementability.IMPLEMENTABLE),
    MARKENUMSWITHFLAGS("MarkEnumsWithFlags", Implementability.IMPLEMENTABLE),
    MARKISERIALIZABLETYPESWITHSERIALIZABLE("MarkISerializableTypesWithSerializable", Implementability.IMPLEMENTABLE),
    MARKMEMBERSASSTATIC("MarkMembersAsStatic", Implementability.IMPLEMENTABLE),
    MARKWINDOWSFORMSENTRYPOINTSWITHSTATHREAD("MarkWindowsFormsEntryPointsWithStaThread", Implementability.IMPLEMENTABLE),
    MEMBERSSHOULDDIFFERBYMORETHANRETURNTYPE("MembersShouldDifferByMoreThanReturnType", Implementability.IMPLEMENTABLE),
    MEMBERSSHOULDNOTEXPOSECERTAINCONCRETETYPES("MembersShouldNotExposeCertainConcreteTypes", Implementability.IMPLEMENTABLE),
    METHODSECURITYSHOULDBEASUPERSETOFTYPE("MethodSecurityShouldBeASupersetOfType", Implementability.IMPLEMENTABLE),
    METHODSMUSTOVERRIDEWITHCONSISTENTTRANSPARENCY("MethodsMustOverrideWithConsistentTransparency", Implementability.IMPLEMENTABLE),
    MOVEPINVOKESTONATIVEMETHODSCLASS("MovePInvokesToNativeMethodsClass", Implementability.IMPLEMENTABLE),
    NESTEDTYPESSHOULDNOTBEVISIBLE("NestedTypesShouldNotBeVisible", Implementability.IMPLEMENTABLE),
    NONCONSTANTFIELDSSHOULDNOTBEVISIBLE("NonConstantFieldsShouldNotBeVisible", Implementability.IMPLEMENTABLE),
    NORMALIZESTRINGSTOUPPERCASE("NormalizeStringsToUppercase", Implementability.IMPLEMENTABLE),
    ONLYFLAGSENUMSSHOULDHAVEPLURALNAMES("OnlyFlagsEnumsShouldHavePluralNames", Implementability.IMPLEMENTABLE),
    OPERATIONSSHOULDNOTOVERFLOW("OperationsShouldNotOverflow", Implementability.IMPLEMENTABLE),
    OPERATOROVERLOADSHAVENAMEDALTERNATES("OperatorOverloadsHaveNamedAlternates", Implementability.IMPLEMENTABLE),
    OPERATORSSHOULDHAVESYMMETRICALOVERLOADS("OperatorsShouldHaveSymmetricalOverloads", Implementability.IMPLEMENTABLE),
    OVERLOADOPERATOREQUALSONOVERLOADINGADDANDSUBTRACT("OverloadOperatorEqualsOnOverloadingAddAndSubtract", Implementability.IMPLEMENTABLE),
    OVERLOADOPERATOREQUALSONOVERRIDINGVALUETYPEEQUALS("OverloadOperatorEqualsOnOverridingValueTypeEquals", Implementability.IMPLEMENTABLE),
    OVERRIDEEQUALSANDOPERATOREQUALSONVALUETYPES("OverrideEqualsAndOperatorEqualsOnValueTypes", Implementability.IMPLEMENTABLE),
    OVERRIDEEQUALSONOVERLOADINGOPERATOREQUALS("OverrideEqualsOnOverloadingOperatorEquals", Implementability.IMPLEMENTABLE),
    OVERRIDEGETHASHCODEONOVERRIDINGEQUALS("OverrideGetHashCodeOnOverridingEquals", Implementability.IMPLEMENTABLE),
    OVERRIDELINKDEMANDSSHOULDBEIDENTICALTOBASE("OverrideLinkDemandsShouldBeIdenticalToBase", Implementability.IMPLEMENTABLE),
    OVERRIDEMETHODSONCOMPARABLETYPES("OverrideMethodsOnComparableTypes", Implementability.IMPLEMENTABLE),
    PARAMETERNAMESSHOULDMATCHBASEDECLARATION("ParameterNamesShouldMatchBaseDeclaration", Implementability.IMPLEMENTABLE),
    PARAMETERNAMESSHOULDNOTMATCHMEMBERNAMES("ParameterNamesShouldNotMatchMemberNames", Implementability.IMPLEMENTABLE),
    PASSSYSTEMURIOBJECTSINSTEADOFSTRINGS("PassSystemUriObjectsInsteadOfStrings", Implementability.IMPLEMENTABLE),
    PINVOKEDECLARATIONSSHOULDBEPORTABLE("PInvokeDeclarationsShouldBePortable", Implementability.IMPLEMENTABLE),
    PINVOKEENTRYPOINTSSHOULDEXIST("PInvokeEntryPointsShouldExist", Implementability.IMPLEMENTABLE),
    PINVOKESSHOULDNOTBESAFECRITICALFXCOPRULE("PInvokesShouldNotBeSafeCriticalFxCopRule", Implementability.IMPLEMENTABLE),
    PINVOKESSHOULDNOTBEVISIBLE("PInvokesShouldNotBeVisible", Implementability.IMPLEMENTABLE),
    POINTERSSHOULDNOTBEVISIBLE("PointersShouldNotBeVisible", Implementability.IMPLEMENTABLE),
    PREFERJAGGEDARRAYSOVERMULTIDIMENSIONAL("PreferJaggedArraysOverMultidimensional", Implementability.IMPLEMENTABLE),
    PROPERTIESSHOULDNOTBEWRITEONLY("PropertiesShouldNotBeWriteOnly", Implementability.IMPLEMENTABLE),
    PROPERTIESSHOULDNOTRETURNARRAYS("PropertiesShouldNotReturnArrays", Implementability.IMPLEMENTABLE),
    PROPERTYNAMESSHOULDNOTMATCHGETMETHODS("PropertyNamesShouldNotMatchGetMethods", Implementability.IMPLEMENTABLE),
    PROVIDECORRECTARGUMENTSTOFORMATTINGMETHODS("ProvideCorrectArgumentsToFormattingMethods", Implementability.IMPLEMENTABLE),
    PROVIDEDESERIALIZATIONMETHODSFOROPTIONALFIELDS("ProvideDeserializationMethodsForOptionalFields", Implementability.IMPLEMENTABLE),
    PROVIDEOBSOLETEATTRIBUTEMESSAGE("ProvideObsoleteAttributeMessage", Implementability.IMPLEMENTABLE),
    REMOVECALLSTOGCKEEPALIVE("RemoveCallsToGCKeepAlive", Implementability.IMPLEMENTABLE),
    REMOVEEMPTYFINALIZERS("RemoveEmptyFinalizers", Implementability.IMPLEMENTABLE),
    REMOVEUNUSEDLOCALS("RemoveUnusedLocals", Implementability.IMPLEMENTABLE),
    REPLACEREPETITIVEARGUMENTSWITHPARAMSARRAY("ReplaceRepetitiveArgumentsWithParamsArray", Implementability.IMPLEMENTABLE),
    RESOURCESTRINGCOMPOUNDWORDSSHOULDBECASEDCORRECTLY("ResourceStringCompoundWordsShouldBeCasedCorrectly", Implementability.IMPLEMENTABLE),
    RESOURCESTRINGSSHOULDBESPELLEDCORRECTLY("ResourceStringsShouldBeSpelledCorrectly", Implementability.IMPLEMENTABLE),
    RETHROWTOPRESERVESTACKDETAILS("RethrowToPreserveStackDetails", Implementability.IMPLEMENTABLE),
    REVIEWDECLARATIVESECURITYONVALUETYPES("ReviewDeclarativeSecurityOnValueTypes", Implementability.IMPLEMENTABLE),
    REVIEWDENYANDPERMITONLYUSAGE("ReviewDenyAndPermitOnlyUsage", Implementability.IMPLEMENTABLE),
    REVIEWIMPERATIVESECURITY("ReviewImperativeSecurity", Implementability.IMPLEMENTABLE),
    REVIEWMISLEADINGFIELDNAMES("ReviewMisleadingFieldNames", Implementability.IMPLEMENTABLE),
    REVIEWSQLQUERIESFORSECURITYVULNERABILITIES("ReviewSqlQueriesForSecurityVulnerabilities", Implementability.IMPLEMENTABLE),
    REVIEWSUPPRESSUNMANAGEDCODESECURITYUSAGE("ReviewSuppressUnmanagedCodeSecurityUsage", Implementability.IMPLEMENTABLE),
    REVIEWUNUSEDPARAMETERS("ReviewUnusedParameters", Implementability.IMPLEMENTABLE),
    REVIEWVISIBLEEVENTHANDLERS("ReviewVisibleEventHandlers", Implementability.IMPLEMENTABLE),
    SEALMETHODSTHATSATISFYPRIVATEINTERFACES("SealMethodsThatSatisfyPrivateInterfaces", Implementability.IMPLEMENTABLE),
    SECUREASSERTS("SecureAsserts", Implementability.IMPLEMENTABLE),
    SECUREDTYPESSHOULDNOTEXPOSEFIELDS("SecuredTypesShouldNotExposeFields", Implementability.IMPLEMENTABLE),
    SECURESERIALIZATIONCONSTRUCTORS("SecureSerializationConstructors", Implementability.IMPLEMENTABLE),
    SECURITYRULESETLEVEL2METHODSSHOULDNOTBEPROTECTEDWITHLINKDEMANDS("SecurityRuleSetLevel2MethodsShouldNotBeProtectedWithLinkDemands", Implementability.IMPLEMENTABLE),
    SECURITYTRANSPARENTCODESHOULDNOTASSERT("SecurityTransparentCodeShouldNotAssert", Implementability.IMPLEMENTABLE),
    SETLOCALEFORDATATYPES("SetLocaleForDataTypes", Implementability.IMPLEMENTABLE),
    SPECIFYCULTUREINFO("SpecifyCultureInfo", Implementability.IMPLEMENTABLE),
    SPECIFYIFORMATPROVIDER("SpecifyIFormatProvider", Implementability.IMPLEMENTABLE),
    SPECIFYMARSHALINGFORPINVOKESTRINGARGUMENTS("SpecifyMarshalingForPInvokeStringArguments", Implementability.IMPLEMENTABLE),
    SPECIFYMESSAGEBOXOPTIONS("SpecifyMessageBoxOptions", Implementability.IMPLEMENTABLE),
    SPECIFYSTRINGCOMPARISON("SpecifyStringComparison", Implementability.IMPLEMENTABLE),
    STATICCONSTRUCTORSSHOULDBEPRIVATE("StaticConstructorsShouldBePrivate", Implementability.IMPLEMENTABLE),
    STATICHOLDERTYPESSHOULDBESEALED("StaticHolderTypesShouldBeSealed", Implementability.IMPLEMENTABLE),
    STATICHOLDERTYPESSHOULDNOTHAVECONSTRUCTORS("StaticHolderTypesShouldNotHaveConstructors", Implementability.IMPLEMENTABLE),
    STRINGURIOVERLOADSCALLSYSTEMURIOVERLOADS("StringUriOverloadsCallSystemUriOverloads", Implementability.IMPLEMENTABLE),
    TESTFOREMPTYSTRINGSUSINGSTRINGLENGTH("TestForEmptyStringsUsingStringLength", Implementability.IMPLEMENTABLE),
    TESTFORNANCORRECTLY("TestForNaNCorrectly", Implementability.IMPLEMENTABLE),
    TRANSPARENCYANNOTATIONSSHOULDNOTCONFLICT("TransparencyAnnotationsShouldNotConflict", Implementability.IMPLEMENTABLE),
    TRANSPARENTMETHODSMUSTBEVERIFIABLE("TransparentMethodsMustBeVerifiable", Implementability.IMPLEMENTABLE),
    TRANSPARENTMETHODSMUSTNOTCALLNATIVECODE("TransparentMethodsMustNotCallNativeCode", Implementability.IMPLEMENTABLE),
    TRANSPARENTMETHODSMUSTNOTCALLSUPPRESSUNMANAGEDCODESECURITYMETHODS("TransparentMethodsMustNotCallSuppressUnmanagedCodeSecurityMethods", Implementability.IMPLEMENTABLE),
    TRANSPARENTMETHODSMUSTNOTHANDLEPROCESSCORRUPTINGEXCEPTIONS("TransparentMethodsMustNotHandleProcessCorruptingExceptions", Implementability.IMPLEMENTABLE),
    TRANSPARENTMETHODSMUSTNOTREFERENCECRITICALCODE("TransparentMethodsMustNotReferenceCriticalCode", Implementability.IMPLEMENTABLE),
    TRANSPARENTMETHODSMUSTNOTSATISFYLINKDEMANDS("TransparentMethodsMustNotSatisfyLinkDemands", Implementability.IMPLEMENTABLE),
    TRANSPARENTMETHODSSHOULDNOTBEPROTECTEDWITHLINKDEMANDS("TransparentMethodsShouldNotBeProtectedWithLinkDemands", Implementability.IMPLEMENTABLE),
    TRANSPARENTMETHODSSHOULDNOTDEMAND("TransparentMethodsShouldNotDemand", Implementability.IMPLEMENTABLE),
    TRANSPARENTMETHODSSHOULDNOTLOADASSEMBLIESFROMBYTEARRAYS("TransparentMethodsShouldNotLoadAssembliesFromByteArrays", Implementability.IMPLEMENTABLE),
    TRANSPARENTMETHODSSHOULDNOTUSESUPPRESSUNMANAGEDCODESECURITY("TransparentMethodsShouldNotUseSuppressUnmanagedCodeSecurity", Implementability.IMPLEMENTABLE),
    TYPELINKDEMANDSREQUIREINHERITANCEDEMANDS("TypeLinkDemandsRequireInheritanceDemands", Implementability.IMPLEMENTABLE),
    TYPENAMESSHOULDNOTMATCHNAMESPACES("TypeNamesShouldNotMatchNamespaces", Implementability.IMPLEMENTABLE),
    TYPESMUSTBEATLEASTASCRITICALASBASETYPES("TypesMustBeAtLeastAsCriticalAsBaseTypes", Implementability.IMPLEMENTABLE),
    TYPESSHOULDNOTEXTENDCERTAINBASETYPES("TypesShouldNotExtendCertainBaseTypes", Implementability.IMPLEMENTABLE),
    TYPESTHATOWNDISPOSABLEFIELDSSHOULDBEDISPOSABLE("TypesThatOwnDisposableFieldsShouldBeDisposable", Implementability.IMPLEMENTABLE),
    TYPESTHATOWNNATIVERESOURCESSHOULDBEDISPOSABLE("TypesThatOwnNativeResourcesShouldBeDisposable", Implementability.IMPLEMENTABLE),
    URIPARAMETERSSHOULDNOTBESTRINGS("UriParametersShouldNotBeStrings", Implementability.IMPLEMENTABLE),
    URIPROPERTIESSHOULDNOTBESTRINGS("UriPropertiesShouldNotBeStrings", Implementability.IMPLEMENTABLE),
    URIRETURNVALUESSHOULDNOTBESTRINGS("UriReturnValuesShouldNotBeStrings", Implementability.IMPLEMENTABLE),
    USEEVENTSWHEREAPPROPRIATE("UseEventsWhereAppropriate", Implementability.IMPLEMENTABLE),
    USEGENERICEVENTHANDLERINSTANCES("UseGenericEventHandlerInstances", Implementability.IMPLEMENTABLE),
    USEGENERICSWHEREAPPROPRIATE("UseGenericsWhereAppropriate", Implementability.IMPLEMENTABLE),
    USEINTEGRALORSTRINGARGUMENTFORINDEXERS("UseIntegralOrStringArgumentForIndexers", Implementability.IMPLEMENTABLE),
    USELITERALSWHEREAPPROPRIATE("UseLiteralsWhereAppropriate", Implementability.IMPLEMENTABLE),
    USEMANAGEDEQUIVALENTSOFWIN32API("UseManagedEquivalentsOfWin32Api", Implementability.IMPLEMENTABLE),
    USEONLYAPIFROMTARGETEDFRAMEWORK("UseOnlyApiFromTargetedFramework", Implementability.IMPLEMENTABLE),
    USEORDINALSTRINGCOMPARISON("UseOrdinalStringComparison", Implementability.IMPLEMENTABLE),
    USEPARAMSFORVARIABLEARGUMENTS("UseParamsForVariableArguments", Implementability.IMPLEMENTABLE),
    USEPREFERREDTERMS("UsePreferredTerms", Implementability.IMPLEMENTABLE),
    USEPROPERTIESWHEREAPPROPRIATE("UsePropertiesWhereAppropriate", Implementability.IMPLEMENTABLE),
    USESAFEHANDLETOENCAPSULATENATIVERESOURCES("UseSafeHandleToEncapsulateNativeResources", Implementability.IMPLEMENTABLE),
    VALIDATEARGUMENTSOFPUBLICMETHODS("ValidateArgumentsOfPublicMethods", Implementability.IMPLEMENTABLE),
    VALUETYPEFIELDSSHOULDBEPORTABLE("ValueTypeFieldsShouldBePortable", Implementability.IMPLEMENTABLE),
    VARIABLENAMESSHOULDNOTMATCHFIELDNAMES("VariableNamesShouldNotMatchFieldNames", Implementability.IMPLEMENTABLE),
    WRAPVULNERABLEFINALLYCLAUSESINOUTERTRY("WrapVulnerableFinallyClausesInOuterTry", Implementability.IMPLEMENTABLE);

    private Implementability implementability;
    private String title;

    FxCopRule(String title, Implementability implementability) {
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
