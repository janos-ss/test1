/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.misra;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.AbstractMisraSpecification;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRequirableRule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.List;


public class MisraC2004 extends AbstractMisraSpecification {

  private static final String NAME = "MISRA C 2004";
  private static final String SEE_SECTION_SEARCH_STRING = "MISRA C:2004,";
  private static final String REFERENCE_PATTERN = "\\d\\d?\\.\\d\\d?";

  private Language language = Language.C;

  public enum StandardRule implements CodingStandardRequirableRule {

    M04_1_1 ("1.1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "All code shall conform to ISO/IEC 9899:1990 “Programming languages — C”, amended and corrected by ISO/IEC 9899/COR1:1995, ISO/IEC 9899/ AMD1:1995, and ISO/IEC 9899/COR2:1996"),
    M04_1_2 ("1.2", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "No reliance shall be placed on undefined or unspecified behaviour."),
    M04_1_3 ("1.3", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "Multiple compilers and/or languages shall only be used if there is a common defined interface standard for object code to which the languages/compilers/assemblers conform."),
    M04_1_4 ("1.4", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "The compiler/linker shall be checked to ensure that 31 character significance andcase sensitivity are supported for external identifiers."),
    M04_1_5 ("1.5", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE, "Floating-_ implementations should comply with a defined floating-_ standard."),

    M04_2_1 ("2.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Assembly language shall be encapsulated and isolated."),
    M04_2_2 ("2.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Source code shall only use /* ... */ style comments."),
    M04_2_3 ("2.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The character sequence /* shall not be used within a comment."),
    M04_2_4 ("2.4", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Sections of code should not be “commented out”."),

    M04_3_1 ("3.1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "All usage of implementation-defined behaviour shall be documented."),
    M04_3_2 ("3.2", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "The character set and the corresponding encoding shall be documented."),
    M04_3_3 ("3.3", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE, "The implementation of integer division in the chosen compiler should bedetermined, documented and taken into account."),
    M04_3_4 ("3.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All uses of the #pragma directive shall be documented and explained."),
    M04_3_5 ("3.5", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "The implementation defined behaviour and packing of bitfields shall be documented if being relied upon."),
    M04_3_6 ("3.6", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "All libraries used in production code shall be written to comply with the provisions of this document, and shall have been subject to appropriate validation."),

    M04_4_1 ("4.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Only those escape sequences that are defined in the ISO C standard shall be used."),
    M04_4_2 ("4.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Trigraphs shall not be used."),

    M04_5_1 ("5.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Identifiers (internal and external) shall not rely on the significance of more than 31 characters."),
    M04_5_2 ("5.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Identifiers in an inner scope shall not use the same name as an identifier in an outer scope, and therefore hide that identifier."),
    M04_5_3 ("5.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A typedef name shall be a unique identifier."),
    M04_5_4 ("5.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A tag name shall be a unique identifier."),
    M04_5_5 ("5.5", Boolean.FALSE, Implementability.IMPLEMENTABLE, "No object or function identifier with static storage duration should be reused."),
    M04_5_6 ("5.6", Boolean.FALSE, Implementability.IMPLEMENTABLE, "No identifier in one name space should have the same spelling as an identifier in another name space, with the exception of structure member and union member names."),
    M04_5_7 ("5.7", Boolean.FALSE, Implementability.IMPLEMENTABLE, "No identifier name should be reused."),

    M04_6_1 ("6.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The plain char type shall be used only for storage and use of character values."),
    M04_6_2 ("6.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "signed and unsigned char type shall be used only for the storage and use of numeric values."),
    M04_6_3 ("6.3", Boolean.FALSE, Implementability.IMPLEMENTABLE, "typedefs that indicate size and signedness should be used in place of the basic numerical types."),
    M04_6_4 ("6.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Bit fields shall only be defined to be of type unsigned int or signed int."),
    M04_6_5 ("6.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Bit fields of signed type shall be at least 2 bits long."),

    M04_7_1 ("7.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Octal constants (other than zero) and octal escape sequences shall not be used."),

    M04_8_1 ("8.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Functions shall have prototype declarations and the prototype shall be visible at both the function definition and call."),
    M04_8_2 ("8.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Whenever an object or function is declared or defined, its type shall be explicitly stated."),
    M04_8_3 ("8.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "For each function parameter the type given in the declaration and definition shall be identical, and the return types shall also be identical."),
    M04_8_4 ("8.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "If objects or functions are declared more than once their types shall be compatible."),
    M04_8_5 ("8.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "There shall be no definitions of objects or functions in a header file."),
    M04_8_6 ("8.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Functions shall be declared at file scope."),
    M04_8_7 ("8.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Objects shall be defined at block scope if they are only accessed from within a single function."),
    M04_8_8 ("8.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An external object or function shall be declared in one and only one file."),
    M04_8_9 ("8.9", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An identifier with external linkage shall have exactly one external definition."),
    M04_8_10 ("8.10", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All declarations and definitions of objects or functions at file scope shall have internal linkage unless external linkage is required."),
    M04_8_11 ("8.11", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The static storage class specifier shall be used in definitions and declarations of objects and functions that have internal linkage."),
    M04_8_12 ("8.12", Boolean.TRUE, Implementability.IMPLEMENTABLE, "When an array is declared with external linkage, its size shall be stated explicitly or defined implicitly by initialisation."),

    M04_9_1 ("9.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All automatic variables shall have been assigned a value before being used."),
    M04_9_2 ("9.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Braces shall be used to indicate and match the structure in the non-zero initialisation of arrays and structures."),
    M04_9_3 ("9.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "In an enumerator list, the “=” construct shall not be used to explicitly initialise members other than the first, unless all items are explicitly initialised."),

    M04_10_1 ("10.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value of an expression of integer type shall not be implicitly converted to a different underlying type if:\n(a) it is not a conversion to a wider integer type of the same signedness, or\n(b) the expression is complex, or\n(c) the expression is not constant and is a function argument, or\n(d) the expression is not constant and is a return expression"),
    M04_10_2 ("10.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value of an expression of floating type shall not be implicitly converted to a different type if:\n(a) it is not a conversion to a wider floating type, or\n(b) the expression is complex, or\n(c) the expression is a function argument, or\n(d) the expression is a return expression"),
    M04_10_3 ("10.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value of a complex expression of integer type shall only be cast to a type of the same signedness that is no wider than the underlying type of the expression."),
    M04_10_4 ("10.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value of a complex expression of floating type shall only be cast to a floating type that is narrower or of the same size."),
    M04_10_5 ("10.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "If the bitwise operators ~ and << are applied to an operand of underlying type unsigned char or unsigned short, the result shall be immediately cast to the underlying type of the operand."),
    M04_10_6 ("10.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A “U” suffix shall be applied to all constants of unsigned type."),

    M04_11_1 ("11.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Conversions shall not be performed between a _er to a function and any type other than an integral type."),
    M04_11_2 ("11.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Conversions shall not be performed between a _er to object and any type other than an integral type, another _er to object type or a _er to void."),
    M04_11_3 ("11.3", Boolean.FALSE, Implementability.IMPLEMENTABLE, "A cast should not be performed between a _er type and an integral type."),
    M04_11_4 ("11.4", Boolean.FALSE, Implementability.IMPLEMENTABLE, "A cast should not be performed between a _er to object type and a different _er to object type."),
    M04_11_5 ("11.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A cast shall not be performed that removes any const or volatile qualification from the type addressed by a _er."),

    M04_12_1 ("12.1", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Limited dependence should be placed on C’s operator precedence rules in expressions."),
    M04_12_2 ("12.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The value of an expression shall be the same under any order of evaluation that the standard permits."),
    M04_12_3 ("12.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The sizeof operator shall not be used on expressions that contain side effects."),
    M04_12_4 ("12.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The right-hand operand of a logical && or || operator shall not contain side effects."),
    M04_12_5 ("12.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The operands of a logical && or || shall be primary-expressions."),
    M04_12_6 ("12.6", Boolean.FALSE, Implementability.IMPLEMENTABLE, "The operands of logical operators (&&, || and !) should be effectively Boolean. Expressions that are effectively Boolean should not be used as operands to operators other than (&&, || , !, =, ==, != and ?:)."),
    M04_12_7 ("12.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Bitwise operators shall not be applied to operands whose underlying type is signed."),
    M04_12_8 ("12.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The right-hand operand of a shift operator shall lie between zero and one less than the width in bits of the underlying type of the left-hand operand."),
    M04_12_9 ("12.9", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The unary minus operator shall not be applied to an expression whose underlying type is unsigned."),
    M04_12_10 ("12.10", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The comma operator shall not be used."),
    M04_12_11 ("12.11", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Evaluation of constant unsigned integer expressions should not lead to wrap-around."),
    M04_12_12 ("12.12", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The underlying bit representations of floating-_ values shall not be used."),
    M04_12_13 ("12.13", Boolean.FALSE, Implementability.IMPLEMENTABLE, "The increment (++) and decrement (--) operators should not be mixed with other operators in an expression."),

    M04_13_1 ("13.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Assignment operators shall not be used in expressions that yield a Boolean value."),
    M04_13_2 ("13.2", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Tests of a value against zero should be made explicit, unless the operand is effectively Boolean."),
    M04_13_3 ("13.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Floating-_ expressions shall not be tested for equality or inequality."),
    M04_13_4 ("13.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The controlling expression of a for statement shall not contain any objects of floating type."),
    M04_13_5 ("13.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The three expressions of a for statement shall be concerned only with loop control."),
    M04_13_6 ("13.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Numeric variables being used within a for loop for iteration counting shall not be modified in the body of the loop."),
    M04_13_7 ("13.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Boolean operations whose results are invariant shall not be permitted."),

    M04_14_1 ("14.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "There shall be no unreachable code."),
    M04_14_2 ("14.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All non-null statements shall either\n(a) have at least one side-effect however executed, or\n(b) cause control flow to change."),
    M04_14_3 ("14.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Before preprocessing, a null statement shall only occur on a line by itself; it may be followed by a comment provided that the first character following the null statement is a white-space character."),
    M04_14_4 ("14.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The goto statement shall not be used."),
    M04_14_5 ("14.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The continue statement shall not be used."),
    M04_14_6 ("14.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "For any iteration statement there shall be at most one break statement used for loop termination."),
    M04_14_7 ("14.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A function shall have a single _ of exit at the end of the function."),
    M04_14_8 ("14.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The statement forming the body of a switch, while, do ... while or for statement shall be a compound statement."),
    M04_14_9 ("14.9", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An if (expression) construct shall be followed by a compound statement. The else keyword shall be followed by either a compound statement, or another if statement."),
    M04_14_10 ("14.10", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All if ... else if constructs shall be terminated with an else clause."),

    M04_15_0 ("15.0", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The MISRA C switch syntax shall be used."),
    M04_15_1 ("15.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A switch label shall only be used when the most closely-enclosing compound statement is the body of a switch statement."),
    M04_15_2 ("15.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An unconditional break statement shall terminate every non-empty switch clause."),
    M04_15_3 ("15.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The final clause of a switch statement shall be the default clause."),
    M04_15_4 ("15.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A switch expression shall not represent a value that is effectively Boolean."),
    M04_15_5 ("15.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Every switch statement shall have at least one case clause."),

    M04_16_1 ("16.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Functions shall not be defined with variable numbers of arguments."),
    M04_16_2 ("16.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Functions shall not call themselves, either directly or indirectly."),
    M04_16_3 ("16.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Identifiers shall be given for all of the parameters in a function prototype declaration."),
    M04_16_4 ("16.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The identifiers used in the declaration and definition of a function shall be identical."),
    M04_16_5 ("16.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Functions with no parameters shall be declared and defined with the parameter list void."),
    M04_16_6 ("16.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The number of arguments passed to a function shall match the number of parameters."),
    M04_16_7 ("16.7", Boolean.FALSE, Implementability.IMPLEMENTABLE, "A _er parameter in a function prototype should be declared as _er to const if the _er is not used to modify the addressed object."),
    M04_16_8 ("16.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All exit paths from a function with non-void return type shall have an explicit return statement with an expression."),
    M04_16_9 ("16.9", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A function identifier shall only be used with either a preceding &, or with a parenthesised parameter list, which may be empty."),
    M04_16_10 ("16.10", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "If a function returns error information, then that error information shall be tested."),

    M04_17_1 ("17.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "_er arithmetic shall only be applied to _ers that address an array or array element."),
    M04_17_2 ("17.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "_er subtraction shall only be applied to _ers that address elements of the same array."),
    M04_17_3 ("17.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, ">, >=, <, <= shall not be applied to _er types except where they _ to the same array."),
    M04_17_4 ("17.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Array indexing shall be the only allowed form of _er arithmetic."),
    M04_17_5 ("17.5", Boolean.FALSE, Implementability.IMPLEMENTABLE, "The declaration of objects should contain no more than 2 levels of _er indirection."),
    M04_17_6 ("17.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The address of an object with automatic storage shall not be assigned to another object that may persist after the first object has ceased to exist."),

    M04_18_1 ("18.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All structure or union types shall be complete at the end of a translation unit."),
    M04_18_2 ("18.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "An object shall not be assigned to an overlapping object."),
    M04_18_3 ("18.3", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "An area of memory shall not be reused for unrelated purposes."),
    M04_18_4 ("18.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Unions shall not be used."),

    M04_19_1 ("19.1", Boolean.FALSE, Implementability.IMPLEMENTABLE, "#include statements in a file should only be preceded by other preprocessor directives or comments."),
    M04_19_2 ("19.2", Boolean.FALSE, Implementability.IMPLEMENTABLE, "Non-standard characters should not occur in header file names in #include directives."),
    M04_19_3 ("19.3", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The #include directive shall be followed by either a <filename> or \"filename\" sequence."),
    M04_19_4 ("19.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "C macros shall only expand to a braced initialiser, a constant, a string literal, a parenthesised expression, a type qualifier, a storage class specifier, or a do-while-zero construct."),
    M04_19_5 ("19.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Macros shall not be #define’d or #undef’d within a block."),
    M04_19_6 ("19.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "#undef shall not be used."),
    M04_19_7 ("19.7", Boolean.FALSE, Implementability.IMPLEMENTABLE, "A function should be used in preference to a function-like macro."),
    M04_19_8 ("19.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "A function-like macro shall not be invoked without all of its arguments."),
    M04_19_9 ("19.9", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Arguments to a function-like macro shall not contain tokens that look like preprocessing directives."),
    M04_19_10 ("19.10", Boolean.TRUE, Implementability.IMPLEMENTABLE, "In the definition of a function-like macro each instance of a parameter shall be enclosed in parentheses unless it is used as the operand of # or ##."),
    M04_19_11 ("19.11", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All macro identifiers in preprocessor directives shall be defined before use, except in #ifdef and #ifndef preprocessor directives and the defined() operator."),
    M04_19_12 ("19.12", Boolean.TRUE, Implementability.IMPLEMENTABLE, "There shall be at most one occurrence of the # or ## preprocessor operators in a single macro definition."),
    M04_19_13 ("19.13", Boolean.FALSE, Implementability.IMPLEMENTABLE, "The # and ## preprocessor operators should not be used."),
    M04_19_14 ("19.14", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The defined preprocessor operator shall only be used in one of the two standard forms."),
    M04_19_15 ("19.15", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Precautions shall be taken in order to prevent the contents of a header file being included twice."),
    M04_19_16 ("19.16", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Preprocessing directives shall be syntactically meaningful even when excluded by the preprocessor."),
    M04_19_17 ("19.17", Boolean.TRUE, Implementability.IMPLEMENTABLE, "All #else, #elif and #endif preprocessor directives shall reside in the same file as the #if or #ifdef directive to which they are related."),

    M04_20_1 ("20.1", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Reserved identifiers, macros and functions in the standard library, shall not be defined, redefined or undefined."),
    M04_20_2 ("20.2", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The names of standard library macros, objects and functions shall not be reused."),
    M04_20_3 ("20.3", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "The validity of values passed to library functions shall be checked."),
    M04_20_4 ("20.4", Boolean.TRUE, Implementability.IMPLEMENTABLE, "Dynamic heap memory allocation shall not be used."),
    M04_20_5 ("20.5", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The error indicator errno shall not be used."),
    M04_20_6 ("20.6", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The macro offsetof, in library <stddef.h>, shall not be used."),
    M04_20_7 ("20.7", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The setjmp macro and the longjmp function shall not be used."),
    M04_20_8 ("20.8", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The signal handling facilities of <signal.h> shall not be used."),
    M04_20_9 ("20.9", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The input/output library <stdio.h> shall not be used in production code."),
    M04_20_10 ("20.10", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The library functions atof, atoi and atol from library <stdlib.h> shall not be used."),
    M04_20_11 ("20.11", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The library functions abort, exit, getenv and system from library <stdlib.h> shall not be used."),
    M04_20_12 ("20.12", Boolean.TRUE, Implementability.IMPLEMENTABLE, "The time handling functions of library <time.h> shall not be used."),

    M04_21_1 ("21.1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE, "Minimisation of run-time failures shall be ensured by the use of at least one of\n(a) static analysis tools/techniques;\n(b) dynamic analysis tools/techniques;\n(c) explicit coding of checks to handle run-time faults.");


    private String name;
    private String title;
    private Boolean isMandatory;
    private Implementability implementability;

    StandardRule(String name, Boolean isMandatory, Implementability implementability, String title) {
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
    public boolean isRuleRequired() {
      return isMandatory;
    }

    @Override
    public String getTitle() {
      return title;
    }
  }

  public MisraC2004(){
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
    return rule.getMisraC04();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setMisraC04(ids);
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
    return getStandardName();
  }

}
