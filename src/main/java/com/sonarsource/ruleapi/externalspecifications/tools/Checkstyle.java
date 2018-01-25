/*
 * Copyright (C) 2014-2018 SonarSource SA
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


public class Checkstyle extends AbstractReportableExternalTool {

  private String standardName = "Checkstyle";
  private Language language = Language.JAVA;

  public enum CheckstyleRule implements CodingStandardRule {

    ABBREVIATIONASWORDINNAME("AbbreviationAsWordInName", Implementability.IMPLEMENTABLE),
    ABSTRACTCLASSNAME("AbstractClassName", Implementability.IMPLEMENTABLE),
    ANNOTATIONLOCATION("AnnotationLocation", Implementability.REJECTED),
    ANNOTATIONUSESTYLE("AnnotationUseStyle", Implementability.REJECTED),
    ANONINNERLENGTH("AnonInnerLength", Implementability.IMPLEMENTABLE),
    ARRAYTRAILINGCOMMA("ArrayTrailingComma", Implementability.REJECTED),
    ARRAYTYPESTYLE("ArrayTypeStyle", Implementability.IMPLEMENTABLE),
    ATCLAUSEORDER("AtclauseOrder", Implementability.REJECTED),
    AVOIDESCAPEDUNICODECHARACTERS("AvoidEscapedUnicodeCharacters", Implementability.IMPLEMENTABLE),
    AVOIDINLINECONDITIONALS("AvoidInlineConditionals", Implementability.IMPLEMENTABLE),
    AVOIDNESTEDBLOCKS("AvoidNestedBlocks", Implementability.IMPLEMENTABLE),
    AVOIDSTARIMPORT("AvoidStarImport", Implementability.IMPLEMENTABLE),
    AVOIDSTATICIMPORT("AvoidStaticImport", Implementability.REJECTED),
    BOOLEANEXPRESSIONCOMPLEXITY("BooleanExpressionComplexity", Implementability.IMPLEMENTABLE),
    CLASSDATAABSTRACTIONCOUPLING("ClassDataAbstractionCoupling", Implementability.IMPLEMENTABLE),
    CLASSFANOUTCOMPLEXITY("ClassFanOutComplexity", Implementability.IMPLEMENTABLE),
    CLASSTYPEPARAMETERNAME("ClassTypeParameterName", Implementability.IMPLEMENTABLE),
    CONSTANTNAME("ConstantName", Implementability.IMPLEMENTABLE),
    COVARIANTEQUALS("CovariantEquals", Implementability.IMPLEMENTABLE),
    CUSTOMIMPORTORDER("CustomImportOrder", Implementability.REJECTED),
    CYCLOMATICCOMPLEXITY("CyclomaticComplexity", Implementability.IMPLEMENTABLE),
    DECLARATIONORDER("DeclarationOrder", Implementability.IMPLEMENTABLE),
    DEFAULTCOMESLAST("DefaultComesLast", Implementability.IMPLEMENTABLE),
    DESIGNFOREXTENSION("DesignForExtension", Implementability.REJECTED),
    EMPTYBLOCK("EmptyBlock", Implementability.IMPLEMENTABLE),
    EMPTYCATCHBLOCK("EmptyCatchBlock", Implementability.IMPLEMENTABLE),
    EMPTYFORINITIALIZERPAD("EmptyForInitializerPad", Implementability.REJECTED),
    EMPTYFORITERATORPAD("EmptyForIteratorPad", Implementability.REJECTED),
    EMPTYLINESEPARATOR("EmptyLineSeparator", Implementability.REJECTED),
    EMPTYSTATEMENT("EmptyStatement", Implementability.IMPLEMENTABLE),
    EQUALSAVOIDNULL("EqualsAvoidNull", Implementability.IMPLEMENTABLE),
    EQUALSHASHCODE("EqualsHashCode", Implementability.IMPLEMENTABLE),
    EXECUTABLESTATEMENTCOUNT("ExecutableStatementCount", Implementability.REJECTED),
    EXPLICITINITIALIZATION("ExplicitInitialization", Implementability.IMPLEMENTABLE),
    FALLTHROUGH("FallThrough", Implementability.IMPLEMENTABLE),
    FILELENGTH("FileLength", Implementability.IMPLEMENTABLE),
    FILETABCHARACTER("FileTabCharacter", Implementability.IMPLEMENTABLE),
    FINALCLASS("FinalClass", Implementability.IMPLEMENTABLE),
    FINALLOCALVARIABLE("FinalLocalVariable", Implementability.IMPLEMENTABLE),
    FINALPARAMETERS("FinalParameters", Implementability.IMPLEMENTABLE),
    GENERICWHITESPACE("GenericWhitespace", Implementability.REJECTED),
    HEADER("Header", Implementability.IMPLEMENTABLE),
    HIDDENFIELD("HiddenField", Implementability.IMPLEMENTABLE),
    HIDEUTILITYCLASSCONSTRUCTOR("HideUtilityClassConstructor", Implementability.IMPLEMENTABLE),
    ILLEGALCATCH("IllegalCatch", Implementability.IMPLEMENTABLE),
    ILLEGALIMPORT("IllegalImport", Implementability.IMPLEMENTABLE),
    ILLEGALINSTANTIATION("IllegalInstantiation", Implementability.REJECTED),
    ILLEGALTHROWS("IllegalThrows", Implementability.IMPLEMENTABLE),
    ILLEGALTOKEN("IllegalToken", Implementability.REJECTED),
    ILLEGALTOKENTEXT("IllegalTokenText", Implementability.REJECTED),
    ILLEGALTYPE("IllegalType", Implementability.IMPLEMENTABLE),
    IMPORTCONTROL("ImportControl", Implementability.IMPLEMENTABLE),
    IMPORTORDER("ImportOrder", Implementability.REJECTED),
    INDENTATION("Indentation", Implementability.IMPLEMENTABLE),
    INNERASSIGNMENT("InnerAssignment", Implementability.IMPLEMENTABLE),
    INNERTYPELAST("InnerTypeLast", Implementability.REJECTED),
    INTERFACETYPEPARAMETERNAME("InterfaceTypeParameterName", Implementability.IMPLEMENTABLE),
    INTERFACEISTYPE("InterfaceIsType", Implementability.IMPLEMENTABLE),
    JAVADOCMETHOD("JavadocMethod", Implementability.IMPLEMENTABLE),
    JAVADOCPACKAGE("JavadocPackage", Implementability.IMPLEMENTABLE),
    JAVADOCPARAGRAPH("JavadocParagraph", Implementability.REJECTED),
    JAVADOCSTYLE("JavadocStyle", Implementability.REJECTED),
    JAVADOCTAGCONTINUATIONINDENTATION("JavadocTagContinuationIndentation", Implementability.REJECTED),
    JAVADOCTYPE("JavadocType", Implementability.IMPLEMENTABLE),
    JAVADOCVARIABLE("JavadocVariable", Implementability.REJECTED),
    JAVANCSS("JavaNCSS", Implementability.IMPLEMENTABLE),
    LEFTCURLY("LeftCurly", Implementability.IMPLEMENTABLE),
    LINELENGTH("LineLength", Implementability.IMPLEMENTABLE),
    LOCALFINALVARIABLENAME("LocalFinalVariableName", Implementability.IMPLEMENTABLE),
    LOCALVARIABLENAME("LocalVariableName", Implementability.IMPLEMENTABLE),
    MAGICNUMBER("MagicNumber", Implementability.IMPLEMENTABLE),
    MEMBERNAME("MemberName", Implementability.IMPLEMENTABLE),
    METHODNAME("MethodName", Implementability.IMPLEMENTABLE),
    METHODCOUNT("MethodCount", Implementability.IMPLEMENTABLE),
    METHODLENGTH("MethodLength", Implementability.IMPLEMENTABLE),
    METHODPARAMPAD("MethodParamPad", Implementability.REJECTED),
    METHODTYPEPARAMETERNAME("MethodTypeParameterName", Implementability.IMPLEMENTABLE),
    MISSINGCTOR("MissingCtor", Implementability.IMPLEMENTABLE),
    MISSINGDEPRECATED("MissingDeprecated", Implementability.IMPLEMENTABLE),
    MISSINGOVERRIDE("MissingOverride", Implementability.IMPLEMENTABLE),
    MISSINGSWITCHDEFAULT("MissingSwitchDefault", Implementability.IMPLEMENTABLE),
    MODIFIEDCONTROLVARIABLE("ModifiedControlVariable", Implementability.IMPLEMENTABLE),
    MODIFIERORDER("ModifierOrder", Implementability.IMPLEMENTABLE),
    MULTIPLESTRINGLITERALS("MultipleStringLiterals", Implementability.IMPLEMENTABLE),
    MULTIPLEVARIABLEDECLARATIONS("MultipleVariableDeclarations", Implementability.IMPLEMENTABLE),
    MUTABLEEXCEPTION("MutableException", Implementability.IMPLEMENTABLE),
    NEEDBRACES("NeedBraces", Implementability.IMPLEMENTABLE),
    NESTEDFORDEPTH("NestedForDepth", Implementability.IMPLEMENTABLE),
    NESTEDIFDEPTH("NestedIfDepth", Implementability.IMPLEMENTABLE),
    NESTEDTRYDEPTH("NestedTryDepth", Implementability.IMPLEMENTABLE),
    NEWLINEATENDOFFILE("NewlineAtEndOfFile", Implementability.IMPLEMENTABLE),
    NOCLONE("NoClone", Implementability.IMPLEMENTABLE),
    NOFINALIZER("NoFinalizer", Implementability.IMPLEMENTABLE),
    NOLINEWRAP("NoLineWrap", Implementability.REJECTED),
    NONEMPTYATCLAUSEDESCRIPTION("NonEmptyAtclauseDescription", Implementability.IMPLEMENTABLE),
    NOWHITESPACEAFTER("NoWhitespaceAfter", Implementability.REJECTED),
    NOWHITESPACEBEFORE("NoWhitespaceBefore", Implementability.REJECTED),
    NPATHCOMPLEXITY("NPathComplexity", Implementability.IMPLEMENTABLE),
    ONESTATEMENTPERLINE("OneStatementPerLine", Implementability.IMPLEMENTABLE),
    ONETOPLEVELCLASS("OneTopLevelClass", Implementability.IMPLEMENTABLE),
    OPERATORWRAP("OperatorWrap", Implementability.REJECTED),
    OUTERTYPEFILENAME("OuterTypeFileName", Implementability.REJECTED),
    OUTERTYPENUMBER("OuterTypeNumber", Implementability.IMPLEMENTABLE),
    OVERLOADMETHODSDECLARATIONORDER("OverloadMethodsDeclarationOrder", Implementability.REJECTED),
    PACKAGENAME("PackageName", Implementability.IMPLEMENTABLE),
    PACKAGEANNOTATION("PackageAnnotation", Implementability.REJECTED),
    PACKAGEDECLARATION("PackageDeclaration", Implementability.IMPLEMENTABLE),
    PARAMETERNAME("ParameterName", Implementability.IMPLEMENTABLE),
    PARENPAD("ParenPad", Implementability.REJECTED),
    REDUNDANTIMPORT("RedundantImport", Implementability.IMPLEMENTABLE),
    REDUNDANTMODIFIER("RedundantModifier", Implementability.IMPLEMENTABLE),
    REGEXP("Regexp", Implementability.REJECTED),
    REGEXPHEADER("RegexpHeader", Implementability.IMPLEMENTABLE),
    REGEXPMULTILINE("RegexpMultiline", Implementability.REJECTED),
    REGEXPSINGLELINE("RegexpSingleline", Implementability.REJECTED),
    REGEXPSINGLELINEJAVA("RegexpSinglelineJava", Implementability.REJECTED),
    REQUIRETHIS("RequireThis", Implementability.REJECTED),
    RETURNCOUNT("ReturnCount", Implementability.IMPLEMENTABLE),
    RIGHTCURLY("RightCurly", Implementability.IMPLEMENTABLE),
    SEPARATORWRAP("SeparatorWrap", Implementability.REJECTED),
    SIMPLIFYBOOLEANEXPRESSION("SimplifyBooleanExpression", Implementability.IMPLEMENTABLE),
    SIMPLIFYBOOLEANRETURN("SimplifyBooleanReturn", Implementability.IMPLEMENTABLE),
    SINGLELINEJAVADOC("SingleLineJavadoc", Implementability.REJECTED),
    STATICVARIABLENAME("StaticVariableName", Implementability.IMPLEMENTABLE),
    STRICTDUPLICATECODE("StrictDuplicateCode", Implementability.IMPLEMENTABLE),
    STRINGLITERALEQUALITY("StringLiteralEquality", Implementability.IMPLEMENTABLE),
    SUMMARYJAVADOC("SummaryJavadoc", Implementability.REJECTED),
    SUPERCLONE("SuperClone", Implementability.IMPLEMENTABLE),
    SUPERFINALIZE("SuperFinalize", Implementability.IMPLEMENTABLE),
    SUPPRESSWARNINGS("SuppressWarnings", Implementability.IMPLEMENTABLE),
    SUPPRESSWARNINGSHOLDER("SuppressWarningsHolder", Implementability.REJECTED),
    THROWSCOUNT("ThrowsCount", Implementability.IMPLEMENTABLE),
    TODOCOMMENT("TodoComment", Implementability.IMPLEMENTABLE),
    TRAILINGCOMMENT("TrailingComment", Implementability.IMPLEMENTABLE),
    TYPENAME("TypeName", Implementability.IMPLEMENTABLE),
    TYPECASTPARENPAD("TypecastParenPad", Implementability.REJECTED),
    UNCOMMENTEDMAIN("UncommentedMain", Implementability.IMPLEMENTABLE),
    UNIQUEPROPERTIES("UniqueProperties", Implementability.REJECTED),
    UNNECESSARYPARENTHESES("UnnecessaryParentheses", Implementability.IMPLEMENTABLE),
    UNUSEDIMPORTS("UnusedImports", Implementability.IMPLEMENTABLE),
    UPPERELL("UpperEll", Implementability.IMPLEMENTABLE),
    VARIABLEDECLARATIONUSAGEDISTANCE("VariableDeclarationUsageDistance", Implementability.IMPLEMENTABLE),
    VISIBILITYMODIFIER("VisibilityModifier", Implementability.IMPLEMENTABLE),
    WHITESPACEAFTER("WhitespaceAfter", Implementability.REJECTED),
    WHITESPACEAROUND("WhitespaceAround", Implementability.REJECTED),
    WRITETAG("WriteTag", Implementability.REJECTED);


    private String name;
    private Implementability implementability;

    CheckstyleRule(String name, Implementability implementability){
      this.name = name;
      this.implementability = implementability;
    }

    @Override
    public Implementability getImplementability() {
      return this.implementability;
    }

    @Override
    public String getCodingStandardRuleId(){
      return name;
    }
  }


  @Override
  public String getStandardName() {
    return standardName;
  }

  @Override
  public Language getLanguage() {
    return language;
  }

  @Override
  public String getRSpecReferenceFieldName() {
    return standardName;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {

    return rule.getCheckstyle();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setCheckstyle(ids);
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {
    return CheckstyleRule.values();
  }


}
