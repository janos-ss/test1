/*
 * Copyright (C) 2014-2016 SonarSource SA
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

public class MisraC2012 extends AbstractMisraSpecification {

  private static final String NAME = "MISRA C 2012";
  private static final String SEE_SECTION_SEARCH_STRING = "MISRA C:2012,";
  private static final String REFERENCE_PATTERN = "\\d\\d?\\.\\d\\d?";

  private Language language = Language.C;

  public enum StandardRule implements CodingStandardRequirableRule {

    MISRAC1012_D1_1 ("Dir 1.1", "Any implementation-defined behavio ur on which the output of the program depends shall be documented and understood", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),

    MISRAC1012_D2_1 ("Dir 2.1", "All source files shall compile without any compilation errors", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),

    MISRAC1012_D3_1 ("Dir 3.1", "All code shall be traceable to documented requirements", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),

    MISRAC1012_D4_1 ("Dir 4.1", "Run-time failures shall be minimized", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC1012_D4_2 ("Dir 4.2", "Advisory All usage of assembly language should be documented", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC1012_D4_3 ("Dir 4.3", "Assembly language shall be encapsulated and isolated", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC1012_D4_4 ("Dir 4.4", "Advisory Sections of code should not be “commented out”", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC1012_D4_5 ("Dir 4.5", "Advisory Identifiers in the same name space with overlapping visibility should be typographically unambiguous", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC1012_D4_6 ("Dir 4.6", "Advisory typedefs that indicate size and signedness should be used in place of the basic numerical types", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC1012_D4_7 ("Dir 4.7", "If a function returns error information, then that error information shall be tested", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC1012_D4_8 ("Dir 4.8", "Advisory If a pointer to a structure or union is never dereferenced within a translation unit, then the implementation of the object should be hidden", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC1012_D4_9 ("Dir 4.9", "Advisory A function should be used in preference to a function-like macro where they are interchangeable", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC1012_D4_10 ("Dir 4.10", "Precautions shall be taken in order to prevent the contents of a header file being included more than once", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC1012_D4_11 ("Dir 4.11", "The validity of values passed to library functions shall be checked", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC1012_D4_12 ("Dir 4.12", "Dynamic memory allocation shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC1012_D4_13 ("Dir 4.13", "Advisory Functions which are designed to provide operations on a resource should be called in an appropriate sequence", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    M12_1_1 ("1.1", "The program shall contain no violations of the standard C syntax and constraints, and shall not exceed the implementation’s translation limits", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_1_2 ("1.2", "Advisory Language extensions should not be used", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_1_3 ("1.3", "There shall be no occurrence of undefined or critical unspecified behaviour", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_2_1 ("2.1", "A project shall not contain unreachable code", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_2_2 ("2.2", "There shall be no dead code", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_2_3 ("2.3", "Advisory A project should not contain unused type declarations", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_2_4 ("2.4", "Advisory A project should not contain unused tag declarations", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_2_5 ("2.5", "Advisory A project should not contain unused macro declarations", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_2_6 ("2.6", "Advisory A function should not contain unused label declarations", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_2_7 ("2.7", "Advisory There should be no unused parameters in functions", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    M12_3_1 ("3.1", "The character sequences /* and // shall not be used within a comment", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_3_2 ("3.2", "Line-splicing shall not be used in // comments", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_4_1 ("4.1", "Octal and hexadecimal escape sequences shall be terminated", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_4_2 ("4.2", "Advisory Trigraphs should not be used", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    M12_5_1 ("5.1", "External identifiers shall be distinct", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_5_2 ("5.2", "Identifiers declared in the same scope and name space shall be distinct", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_5_3 ("5.3", "An identifier declared in an inner scope shall not hide an identifier declared in an outer scope", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_5_4 ("5.4", "Macro identifiers shall be distinct", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_5_5 ("5.5", "Identifiers shall be distinct from macro names", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_5_6 ("5.6", "A typedef name shall be a unique identifier", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_5_7 ("5.7", "A tag name shall be a unique identifier", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_5_8 ("5.8", "Identifiers that define objects or functions with external linkage shall be unique", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_5_9 ("5.9", "Advisory Identifiers that define objects or functions with internal linkage shouldbe unique", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    M12_6_1 ("6.1", "Bit-fields shall only be declared with an appropriate type", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_6_2 ("6.2", "Single-bit named bit fields shall not be of a signed type", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_7_1 ("7.1", "Octal constants shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_7_2 ("7.2", "A “u” or “U” suffix shall be applied to all integer constants that are represented in an unsigned type", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_7_3 ("7.3", "The lowercase character “l” shall not be used in a literal suffix", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_7_4 ("7.4", "A string literal shall not be assigned to an object unless the object’s type is “pointer to const-qualified char”", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_8_1 ("8.1", "Types shall be explicitly specified", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_8_2 ("8.2", "Function types shall be in prototype form with named parameters", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_8_3 ("8.3", "All declarations of an object or function shall use the same names and type qualifiers", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_8_4 ("8.4", "A compatible declaration shall be visible when an object or function with external linkage is defined", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_8_5 ("8.5", "An external object or function shall be declared once in one and only one file", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_8_6 ("8.6", "An identifier with external linkage shall have exactly one external definition", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_8_7 ("8.7", "Advisory Functions and objects should not be defined with external linkage if they are referenced in only one translation unit", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_8_8 ("8.8", "The static storage class specifier shall be used in all declarations of objects and functions that have internal linkage", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_8_9 ("8.9", "Advisory An object should be defined at block scope if its identifier only appears in a single function", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_8_10 ("8.10", "An inline function shall be declared with the static storage class", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_8_11 ("8.11", "Advisory When an array with external linkage is declared, its size should be explicitly specified", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_8_12 ("8.12", "Within an enumerator list, the value of an implicitly-specified enumeration constant shall be unique", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_8_13 ("8.13", "Advisory A pointer should point to a const-qualified type whenever possible", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_8_14 ("8.14", "The restrict type qualifier shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_9_1 ("9.1", "The value of an object with automatic storage duration shall not be read before it has been set", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_9_2 ("9.2", "The initializer for an aggregate or union shall be enclosed in braces", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_9_3 ("9.3", "Arrays shall not be partially initialized", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_9_4 ("9.4", "An element of an object shall not be initialized more than once", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_9_5 ("9.5", "Where designated initializers are used to initialize an array object the size of the array shall be specified explicitly", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_10_1 ("10.1", "Operands shall not be of an inappropriate essential type", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_10_2 ("10.2", "Expressions of essentially character type shall not be used inappropriately in addition and subtraction operations", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_10_3 ("10.3", "The value of an expression shall not be assigned to an object with a narrower essential type or of a different essential type category", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_10_4 ("10.4", "Both operands of an operator in which the usual arithmetic conversions are performed shall have the same essential type category", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_10_5 ("10.5", "Advisory The value of an expression should not be cast to an inappropriate essential type", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_10_6 ("10.6", "The value of a composite expression shall not be assigned to an object with wider essential type", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_10_7 ("10.7", "If a composite expression is used as one operand of an operator in which the usual arithmetic conversions are performed then the other operand shall not have wider essential type", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_10_8 ("10.8", "The value of a composite expression shall not be cast to a different essential type category or a wider essential type", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_11_1 ("11.1", "Conversions shall not be performed between a pointer to a function and any other type", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_11_2 ("11.2", "Conversions shall not be performed between a pointer to an incomplete type and any other type", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_11_3 ("11.3", "A cast shall not be performed between a pointer to object type and a pointer to a different object type", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_11_4 ("11.4", "Advisory A conversion should not be performed between a pointer to object and an integer type", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_11_5 ("11.5", "Advisory A conversion should not be performed from pointer to void into pointer to object", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_11_6 ("11.6", "A cast shall not be performed between pointer to void and an arithmetic type", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_11_7 ("11.7", "A cast shall not be performed between pointer to object and a non-integer arithmetic type", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_11_8 ("11.8", "A cast shall not remove any const or volatile qualification from the type pointed to by a pointer", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_11_9 ("11.9", "The macro NULL shall be the only permitted form of integer null pointer constant", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_12_1 ("12.1", "Advisory The precedence of operators within expressions should be made explicit", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_12_2 ("12.2", "The right hand operand of a shift operator shall lie in the range zero to one less than the width in bits of the essential type of the left hand operand", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_12_3 ("12.3", "Advisory The comma operator should not be used", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_12_4 ("12.4", "Advisory Evaluation of constant expressions should not lead to unsigned integer wrap-around", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    M12_13_1 ("13.1", "Initializer lists shall not contain persistent side effects", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_13_2 ("13.2", "The value of an expression and its persistent side effects shall be the same under all permitted evaluation orders", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_13_3 ("13.3", "Advisory A full expression containing an increment (++) or decrement (--) operator should have no other potential side effects other than that caused by the increment or decrement operator", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_13_4 ("13.4", "Advisory The result of an assignment operator should not be used", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_13_5 ("13.5", "The right hand operand of a logical && or || operator shall not contain persistent side effects", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_13_6 ("13.6", "The operand of the sizeof operator shall not contain any expression which has potential side effects", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_14_1 ("14.1", "A loop counter shall not have essentially fl oating type", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_14_2 ("14.2", "A for loop shall be well-formed", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_14_3 ("14.3", "Controlling expressions shall not be invariant", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_14_4 ("14.4", "The controlling expression of an if statement and the controlling expression of an iteration-statement shall have essentially Boolean type", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_15_1 ("15.1", "Advisory The goto statement should not be used", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_15_2 ("15.2", "The goto statement shall jump to a label declared later in the same function", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_15_3 ("15.3", "Any label referenced by a goto statement shall be declared in the same block, or in any block enclosing the goto statement", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_15_4 ("15.4", "Advisory There should be no more than one break or goto statement used to terminate any iteration statement", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_15_5 ("15.5", "Advisory A function should have a single point of exit at the end", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_15_6 ("15.6", "The body of an iteration-statement or a selection-statement shall be a compound-statement", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_15_7 ("15.7", "All if ... else if constructs shall be terminated with an else statement", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_16_1 ("16.1", "All switch statements shall be well-formed", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_16_2 ("16.2", "A switch label shall only be used when the most closely-enclosing compound statement is the body of a switch statement", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_16_3 ("16.3", "An unconditional break statement shall terminate every switch-clause", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_16_4 ("16.4", "Every switch statement shall have a default label", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_16_5 ("16.5", "A default label shall appear as either the first or the last switch label of a switch statement", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_16_6 ("16.6", "Every switch statement shall have at least two switch-clauses", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_16_7 ("16.7", "A switch-expression shall not have essentially Boolean type", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_17_1 ("17.1", "The features of <stdarg.h> shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_17_2 ("17.2", "Functions shall not call themselves, either directly or indirectly", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_17_3 ("17.3", "A function shall not be declared implicitly", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_17_4 ("17.4", "All exit paths from a function with non-void return type shall have an explicit return statement with an expression", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_17_5 ("17.5", "Advisory The function argument corresponding to a parameter declared to have an array type shall have an appropriate number of elements", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_17_6 ("17.6", "The declaration of an array parameter shall not contain the static keyword between the [ ]", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_17_7 ("17.7", "The value returned by a function having non-void return type shall be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_17_8 ("17.8", "Advisory A function parameter should not be modified", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    M12_18_1 ("18.1", "A pointer resulting from arithmetic on a pointer operand shall address an element of the same array as that pointer operand", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_18_2 ("18.2", "Subtraction between pointers shall only be applied to pointers that address elements of the same array", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_18_3 ("18.3", "The relational operators >, >=, < and <= shall not be applied to objects of pointer type except where they point into the same object", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_18_4 ("18.4", "Advisory The +, -, += and -= operators should not be applied to an expression of pointer type", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_18_5 ("18.5", "Advisory Declarations should contain no more than two levels of pointer nesting", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_18_6 ("18.6", "The address of an object with automatic storage shall not be copied to another object that persists after the first object has ceased to exist", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_18_7 ("18.7", "Flexible array members shall not be declared", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_18_8 ("18.8", "Variable-length array types shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_19_1 ("19.1", "An object shall not be assigned or copied to an overlapping object", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_19_2 ("19.2", "Advisory The union keyword should not be used", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    M12_20_1 ("20.1", "Advisory #include directives should only be preceded by preprocessor directives or comments", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_20_2 ("20.2", "The ', \" or \\ characters and the /* or // character sequences shall not occur in a header file name", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_20_3 ("20.3", "The #include directive shall be followed by either a <filename> or \"filename\" sequence", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_20_4 ("20.4", "A macro shall not be defined with the same name as a keyword", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_20_5 ("20.5", "Advisory #undef should not be used", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_20_6 ("20.6", "Tokens that look like a preprocessing directive shall not occur within a macro argument", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_20_7 ("20.7", "Expressions resulting from the expansion of macro parameters shall be enclosed in parentheses", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_20_8 ("20.8", "The controlling expression of a #if or #elif preprocessing directive shall evaluate to 0 or 1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_20_9 ("20.9", "All identifiers used in the controlling expression of #if or #elif preprocessing directives shall be #define’d before evaluation", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_20_10 ("20.10", "Advisory The # and ## preprocessor operators should not be used", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M12_20_11 ("20.11", "A macro parameter immediately following a # operator shall not immediately be followed by a ## operator", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_20_12 ("20.12", "A macro parameter used as an operand to the # or ## operators, which is itself subject to further macro replacement, shall only be used as an operand to these operators", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_20_13 ("20.13", "A line whose first token is # shall be a valid preprocessing directive", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_20_14 ("20.14", "All #else, #elif and #endif preprocessor directives shall reside in the same file as the #if, #ifdef or #ifndef directive to which they are related", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M12_21_1 ("21.1", "#define and #undef shall not be used on a reserved identifier or reserved macro name", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_21_2 ("21.2", "A reserved identifier or macro name shall not be declared", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_21_3 ("21.3", "The memory allocation and deallocation functions of <stdlib.h> shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_21_4 ("21.4", "The standard header file <setjmp.h> shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_21_5 ("21.5", "The standard header file <signal.h> shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_21_6 ("21.6", "The Standard Library input/output functions shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_21_7 ("21.7", "The atof, atoi, atol and atoll functions of <stdlib.h> shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_21_8 ("21.8", "The library functions abort, exit, getenv and system of <stdlib.h> shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_21_9 ("21.9", "The library functions bsearch and qsort of <stdlib.h> shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_21_10 ("21.10", "The Standard Library time and date functions shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_21_11 ("21.11", "The standard header file <tgmath.h> shall not be used", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_21_12 ("21.12", "Advisory The exception handling features of <fenv.h> should not be used", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    M12_22_1 ("22.1", "All resources obtained dynamically by means of Standard Library functions shall be explicitly released", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_22_2 ("22.2", "A block of memory shall only be freed if it was allocated by means of a Standard Library function", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_22_3 ("22.3", "The same file shall not be open for read and write access at the same time on different streams", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_22_4 ("22.4", "There shall be no attempt to write to a stream which has been opened as read-only", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_22_5 ("22.5", "A pointer to a FILE object shall not be dereferenced", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M12_22_6 ("22.6", "The value of a pointer to a FILE shall not be used after the associated stream has been closed", Boolean.TRUE, Implementability.IMPLEMENTABLE);

    private String name;
    private String title;
    private Boolean isMandatory;
    private Implementability implementability;

    StandardRule(String name, String title, Boolean isMandatory, Implementability implementability) {
      this.name = name;
      this.title = title;
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
    public boolean isRuleRequired(){
      return isMandatory;
    }

    @Override
    public String getTitle() {
      return title;
    }
  }

  public MisraC2012() {

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
  public CodingStandardRule[] getCodingStandardRules() {
    return StandardRule.values();
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {
    return rule.getMisraC12();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setMisraC12(ids);
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

}
