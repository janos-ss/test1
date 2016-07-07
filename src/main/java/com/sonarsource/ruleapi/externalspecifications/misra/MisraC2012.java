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

    M12_D1_1 ("Dir 1.1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "Any implementation-defined behavio ur on which the output of the program depends shall be documented and understood"),

    M12_D2_1 ("Dir 2.1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "All source files shall compile without any compilation errors"),

    M12_D3_1 ("Dir 3.1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "All code shall be traceable to documented requirements"),

    M12_D4_1 ("Dir 4.1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "Run-time failures shall be minimized"),
    M12_D4_2 ("Dir 4.2", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE, "Advisory All usage of assembly language should be documented"),
    M12_D4_3 ("Dir 4.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Assembly language shall be encapsulated and isolated"),
    M12_D4_4 ("Dir 4.4", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory Sections of code should not be “commented out”"),
    M12_D4_5 ("Dir 4.5", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory Identifiers in the same name space with overlapping visibility should be typographically unambiguous"),
    M12_D4_6 ("Dir 4.6", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory typedefs that indicate size and signedness should be used in place of the basic numerical types"),
    M12_D4_7 ("Dir 4.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "If a function returns error information, then that error information shall be tested"),
    M12_D4_8 ("Dir 4.8", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory If a pointer to a structure or union is never dereferenced within a translation unit, then the implementation of the object should be hidden"),
    M12_D4_9 ("Dir 4.9", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory A function should be used in preference to a function-like macro where they are interchangeable"),
    M12_D4_10 ("Dir 4.10", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Precautions shall be taken in order to prevent the contents of a header file being included more than once"),
    M12_D4_11 ("Dir 4.11", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The validity of values passed to library functions shall be checked"),
    M12_D4_12 ("Dir 4.12", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Dynamic memory allocation shall not be used"),
    M12_D4_13 ("Dir 4.13", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory Functions which are designed to provide operations on a resource should be called in an appropriate sequence"),

    M12_1_1 ("1.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The program shall contain no violations of the standard C syntax and constraints, and shall not exceed the implementation’s translation limits"),
    M12_1_2 ("1.2", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory Language extensions should not be used"),
    M12_1_3 ("1.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "There shall be no occurrence of undefined or critical unspecified behaviour"),

    M12_2_1 ("2.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A project shall not contain unreachable code"),
    M12_2_2 ("2.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "There shall be no dead code"),
    M12_2_3 ("2.3", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory A project should not contain unused type declarations"),
    M12_2_4 ("2.4", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory A project should not contain unused tag declarations"),
    M12_2_5 ("2.5", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory A project should not contain unused macro declarations"),
    M12_2_6 ("2.6", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory A function should not contain unused label declarations"),
    M12_2_7 ("2.7", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory There should be no unused parameters in functions"),

    M12_3_1 ("3.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The character sequences /* and // shall not be used within a comment"),
    M12_3_2 ("3.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Line-splicing shall not be used in // comments"),

    M12_4_1 ("4.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Octal and hexadecimal escape sequences shall be terminated"),
    M12_4_2 ("4.2", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory Trigraphs should not be used"),

    M12_5_1 ("5.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "External identifiers shall be distinct"),
    M12_5_2 ("5.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Identifiers declared in the same scope and name space shall be distinct"),
    M12_5_3 ("5.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An identifier declared in an inner scope shall not hide an identifier declared in an outer scope"),
    M12_5_4 ("5.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Macro identifiers shall be distinct"),
    M12_5_5 ("5.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Identifiers shall be distinct from macro names"),
    M12_5_6 ("5.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A typedef name shall be a unique identifier"),
    M12_5_7 ("5.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A tag name shall be a unique identifier"),
    M12_5_8 ("5.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Identifiers that define objects or functions with external linkage shall be unique"),
    M12_5_9 ("5.9", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory Identifiers that define objects or functions with internal linkage shouldbe unique"),

    M12_6_1 ("6.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Bit-fields shall only be declared with an appropriate type"),
    M12_6_2 ("6.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Single-bit named bit fields shall not be of a signed type"),

    M12_7_1 ("7.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Octal constants shall not be used"),
    M12_7_2 ("7.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A “u” or “U” suffix shall be applied to all integer constants that are represented in an unsigned type"),
    M12_7_3 ("7.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The lowercase character “l” shall not be used in a literal suffix"),
    M12_7_4 ("7.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A string literal shall not be assigned to an object unless the object’s type is “pointer to const-qualified char”"),

    M12_8_1 ("8.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Types shall be explicitly specified"),
    M12_8_2 ("8.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Function types shall be in prototype form with named parameters"),
    M12_8_3 ("8.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All declarations of an object or function shall use the same names and type qualifiers"),
    M12_8_4 ("8.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A compatible declaration shall be visible when an object or function with external linkage is defined"),
    M12_8_5 ("8.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An external object or function shall be declared once in one and only one file"),
    M12_8_6 ("8.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An identifier with external linkage shall have exactly one external definition"),
    M12_8_7 ("8.7", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory Functions and objects should not be defined with external linkage if they are referenced in only one translation unit"),
    M12_8_8 ("8.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The static storage class specifier shall be used in all declarations of objects and functions that have internal linkage"),
    M12_8_9 ("8.9", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory An object should be defined at block scope if its identifier only appears in a single function"),
    M12_8_10 ("8.10", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An inline function shall be declared with the static storage class"),
    M12_8_11 ("8.11", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory When an array with external linkage is declared, its size should be explicitly specified"),
    M12_8_12 ("8.12", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Within an enumerator list, the value of an implicitly-specified enumeration constant shall be unique"),
    M12_8_13 ("8.13", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory A pointer should point to a const-qualified type whenever possible"),
    M12_8_14 ("8.14", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The restrict type qualifier shall not be used"),

    M12_9_1 ("9.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value of an object with automatic storage duration shall not be read before it has been set"),
    M12_9_2 ("9.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The initializer for an aggregate or union shall be enclosed in braces"),
    M12_9_3 ("9.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Arrays shall not be partially initialized"),
    M12_9_4 ("9.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An element of an object shall not be initialized more than once"),
    M12_9_5 ("9.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Where designated initializers are used to initialize an array object the size of the array shall be specified explicitly"),

    M12_10_1 ("10.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Operands shall not be of an inappropriate essential type"),
    M12_10_2 ("10.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Expressions of essentially character type shall not be used inappropriately in addition and subtraction operations"),
    M12_10_3 ("10.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value of an expression shall not be assigned to an object with a narrower essential type or of a different essential type category"),
    M12_10_4 ("10.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Both operands of an operator in which the usual arithmetic conversions are performed shall have the same essential type category"),
    M12_10_5 ("10.5", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory The value of an expression should not be cast to an inappropriate essential type"),
    M12_10_6 ("10.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value of a composite expression shall not be assigned to an object with wider essential type"),
    M12_10_7 ("10.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "If a composite expression is used as one operand of an operator in which the usual arithmetic conversions are performed then the other operand shall not have wider essential type"),
    M12_10_8 ("10.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value of a composite expression shall not be cast to a different essential type category or a wider essential type"),

    M12_11_1 ("11.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Conversions shall not be performed between a pointer to a function and any other type"),
    M12_11_2 ("11.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Conversions shall not be performed between a pointer to an incomplete type and any other type"),
    M12_11_3 ("11.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A cast shall not be performed between a pointer to object type and a pointer to a different object type"),
    M12_11_4 ("11.4", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory A conversion should not be performed between a pointer to object and an integer type"),
    M12_11_5 ("11.5", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory A conversion should not be performed from pointer to void into pointer to object"),
    M12_11_6 ("11.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A cast shall not be performed between pointer to void and an arithmetic type"),
    M12_11_7 ("11.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A cast shall not be performed between pointer to object and a non-integer arithmetic type"),
    M12_11_8 ("11.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A cast shall not remove any const or volatile qualification from the type pointed to by a pointer"),
    M12_11_9 ("11.9", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The macro NULL shall be the only permitted form of integer null pointer constant"),

    M12_12_1 ("12.1", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory The precedence of operators within expressions should be made explicit"),
    M12_12_2 ("12.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The right hand operand of a shift operator shall lie in the range zero to one less than the width in bits of the essential type of the left hand operand"),
    M12_12_3 ("12.3", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory The comma operator should not be used"),
    M12_12_4 ("12.4", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory Evaluation of constant expressions should not lead to unsigned integer wrap-around"),

    M12_13_1 ("13.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Initializer lists shall not contain persistent side effects"),
    M12_13_2 ("13.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value of an expression and its persistent side effects shall be the same under all permitted evaluation orders"),
    M12_13_3 ("13.3", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory A full expression containing an increment (++) or decrement (--) operator should have no other potential side effects other than that caused by the increment or decrement operator"),
    M12_13_4 ("13.4", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory The result of an assignment operator should not be used"),
    M12_13_5 ("13.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The right hand operand of a logical && or || operator shall not contain persistent side effects"),
    M12_13_6 ("13.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The operand of the sizeof operator shall not contain any expression which has potential side effects"),

    M12_14_1 ("14.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A loop counter shall not have essentially fl oating type"),
    M12_14_2 ("14.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A for loop shall be well-formed"),
    M12_14_3 ("14.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Controlling expressions shall not be invariant"),
    M12_14_4 ("14.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The controlling expression of an if statement and the controlling expression of an iteration-statement shall have essentially Boolean type"),

    M12_15_1 ("15.1", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory The goto statement should not be used"),
    M12_15_2 ("15.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The goto statement shall jump to a label declared later in the same function"),
    M12_15_3 ("15.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Any label referenced by a goto statement shall be declared in the same block, or in any block enclosing the goto statement"),
    M12_15_4 ("15.4", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory There should be no more than one break or goto statement used to terminate any iteration statement"),
    M12_15_5 ("15.5", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory A function should have a single point of exit at the end"),
    M12_15_6 ("15.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The body of an iteration-statement or a selection-statement shall be a compound-statement"),
    M12_15_7 ("15.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All if ... else if constructs shall be terminated with an else statement"),

    M12_16_1 ("16.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All switch statements shall be well-formed"),
    M12_16_2 ("16.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A switch label shall only be used when the most closely-enclosing compound statement is the body of a switch statement"),
    M12_16_3 ("16.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An unconditional break statement shall terminate every switch-clause"),
    M12_16_4 ("16.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Every switch statement shall have a default label"),
    M12_16_5 ("16.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A default label shall appear as either the first or the last switch label of a switch statement"),
    M12_16_6 ("16.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Every switch statement shall have at least two switch-clauses"),
    M12_16_7 ("16.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A switch-expression shall not have essentially Boolean type"),

    M12_17_1 ("17.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The features of <stdarg.h> shall not be used"),
    M12_17_2 ("17.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Functions shall not call themselves, either directly or indirectly"),
    M12_17_3 ("17.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A function shall not be declared implicitly"),
    M12_17_4 ("17.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All exit paths from a function with non-void return type shall have an explicit return statement with an expression"),
    M12_17_5 ("17.5", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory The function argument corresponding to a parameter declared to have an array type shall have an appropriate number of elements"),
    M12_17_6 ("17.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The declaration of an array parameter shall not contain the static keyword between the [ ]"),
    M12_17_7 ("17.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value returned by a function having non-void return type shall be used"),
    M12_17_8 ("17.8", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory A function parameter should not be modified"),

    M12_18_1 ("18.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A pointer resulting from arithmetic on a pointer operand shall address an element of the same array as that pointer operand"),
    M12_18_2 ("18.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Subtraction between pointers shall only be applied to pointers that address elements of the same array"),
    M12_18_3 ("18.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The relational operators >, >=, < and <= shall not be applied to objects of pointer type except where they point into the same object"),
    M12_18_4 ("18.4", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory The +, -, += and -= operators should not be applied to an expression of pointer type"),
    M12_18_5 ("18.5", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory Declarations should contain no more than two levels of pointer nesting"),
    M12_18_6 ("18.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The address of an object with automatic storage shall not be copied to another object that persists after the first object has ceased to exist"),
    M12_18_7 ("18.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Flexible array members shall not be declared"),
    M12_18_8 ("18.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Variable-length array types shall not be used"),

    M12_19_1 ("19.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An object shall not be assigned or copied to an overlapping object"),
    M12_19_2 ("19.2", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory The union keyword should not be used"),

    M12_20_1 ("20.1", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory #include directives should only be preceded by preprocessor directives or comments"),
    M12_20_2 ("20.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The ', \" or \\ characters and the /* or // character sequences shall not occur in a header file name"),
    M12_20_3 ("20.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The #include directive shall be followed by either a <filename> or \"filename\" sequence"),
    M12_20_4 ("20.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A macro shall not be defined with the same name as a keyword"),
    M12_20_5 ("20.5", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory #undef should not be used"),
    M12_20_6 ("20.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Tokens that look like a preprocessing directive shall not occur within a macro argument"),
    M12_20_7 ("20.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Expressions resulting from the expansion of macro parameters shall be enclosed in parentheses"),
    M12_20_8 ("20.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The controlling expression of a #if or #elif preprocessing directive shall evaluate to 0 or 1"),
    M12_20_9 ("20.9", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All identifiers used in the controlling expression of #if or #elif preprocessing directives shall be #define’d before evaluation"),
    M12_20_10 ("20.10", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory The # and ## preprocessor operators should not be used"),
    M12_20_11 ("20.11", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A macro parameter immediately following a # operator shall not immediately be followed by a ## operator"),
    M12_20_12 ("20.12", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A macro parameter used as an operand to the # or ## operators, which is itself subject to further macro replacement, shall only be used as an operand to these operators"),
    M12_20_13 ("20.13", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A line whose first token is # shall be a valid preprocessing directive"),
    M12_20_14 ("20.14", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All #else, #elif and #endif preprocessor directives shall reside in the same file as the #if, #ifdef or #ifndef directive to which they are related"),

    M12_21_1 ("21.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "#define and #undef shall not be used on a reserved identifier or reserved macro name"),
    M12_21_2 ("21.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A reserved identifier or macro name shall not be declared"),
    M12_21_3 ("21.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The memory allocation and deallocation functions of <stdlib.h> shall not be used"),
    M12_21_4 ("21.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The standard header file <setjmp.h> shall not be used"),
    M12_21_5 ("21.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The standard header file <signal.h> shall not be used"),
    M12_21_6 ("21.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The Standard Library input/output functions shall not be used"),
    M12_21_7 ("21.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The atof, atoi, atol and atoll functions of <stdlib.h> shall not be used"),
    M12_21_8 ("21.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The library functions abort, exit, getenv and system of <stdlib.h> shall not be used"),
    M12_21_9 ("21.9", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The library functions bsearch and qsort of <stdlib.h> shall not be used"),
    M12_21_10 ("21.10", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The Standard Library time and date functions shall not be used"),
    M12_21_11 ("21.11", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The standard header file <tgmath.h> shall not be used"),
    M12_21_12 ("21.12", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Advisory The exception handling features of <fenv.h> should not be used"),

    M12_22_1 ("22.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All resources obtained dynamically by means of Standard Library functions shall be explicitly released"),
    M12_22_2 ("22.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A block of memory shall only be freed if it was allocated by means of a Standard Library function"),
    M12_22_3 ("22.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The same file shall not be open for read and write access at the same time on different streams"),
    M12_22_4 ("22.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "There shall be no attempt to write to a stream which has been opened as read-only"),
    M12_22_5 ("22.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A pointer to a FILE object shall not be dereferenced"),
    M12_22_6 ("22.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value of a pointer to a FILE shall not be used after the associated stream has been closed");

    private String name;
    private String title;
    private Boolean isMandatory;
    private Implementability implementability;

    StandardRule(String name, Boolean isMandatory, Implementability implementability , String title) {
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
