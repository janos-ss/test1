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


public class MisraCPP2008 extends AbstractMisraSpecification {

  private static final String NAME = "MISRA C++ 2008";
  private static final String SEE_SECTION_SEARCH_STRING = "MISRA C++:2008,";
  private static final String REFERENCE_PATTERN = "\\d\\d?-\\d\\d?-\\d\\d?";

  private Language language = Language.CPP;

  public enum StandardRule implements CodingStandardRequirableRule {

    M08_0_1_1 ("0-1-1", "A project shall not contain unreachable code.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_1_2 ("0-1-2", "A project shall not contain infeasible paths.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_1_3 ("0-1-3", "A project shall not contain unused variables.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_1_4 ("0-1-4", "A project shall not contain non-volatile POD variables having only one use.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_1_5 ("0-1-5", "A project shall not contain unused type declarations.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_1_6 ("0-1-6", "A project shall not contain instances of non-volatile variables being given values that are never subsequently used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_1_7 ("0-1-7", "The value returned by a function having a non-void return type that is not an overloaded operator shall always be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_1_8 ("0-1-8", "All functions with void return type shall have external side effect(s).", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_1_9 ("0-1-9", "There shall be no dead code.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_1_10 ("0-1-10", "Every defined function shall be called at least once.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_1_11 ("0-1-11", "There shall be no unused parameters (named or unnamed) in non-virtual functions.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_1_12 ("0-1-12", "There shall be no unused parameters (named or unnamed) in the set of parameters for a virtual function and all the functions that override it.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_2_1 ("0-2-1", "An object shall not be assigned to an overlapping object.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_0_3_1 ("0-3-1", "Minimization of run-time failures shall be ensured by the use of at least one of: (a) static analysis tools/techniques; (b) dynamic analysis tools/techniques; (c) explicit coding of checks to handle run-time faults.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    M08_0_3_2 ("0-3-2", "If a function generates error information, then that error information shall be tested.", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    M08_0_4_1 ("0-4-1", "Use of scaled-integer or fixed-point arithmetic shall be documented.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    M08_0_4_2 ("0-4-2", "Use of floating-point arithmetic shall be documented.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    M08_0_4_3 ("0-4-3", "Floating-point implementationsshall comply with a defined floating-point standard.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),

    M08_1_0_1 ("1-0-1", "All code shall conform to ISO/IEC 14882:2003 “The C++ Standard Incorporating Technical Corrigendum 1”.", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    M08_1_0_2 ("1-0-2", "Multiple compilers shall only be used if they have a common, defined interface.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    M08_1_0_3 ("1-0-3", "The implementation of integer division in the chosen compiler shall be determined and documented.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),

    M08_2_2_1 ("2-2-1", "The character set and the corresponding encoding shall be documented.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    M08_2_3_1 ("2-3-1", "Trigraphs shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_5_1 ("2-5-1", "Digraphs should not be used.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_2_7_1 ("2-7-1", "The character sequence /* shall not be used within a C-style comment.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_7_2 ("2-7-2", "Sections of code shall not be “commented out” using C-style comments.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_7_3 ("2-7-3", "Sections of code should not be “commented out” using C++ comments.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_2_10_1 ("2-10-1", "Different identifiers shall be typographically unambiguous.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_10_2 ("2-10-2", "Identifiers declared in an inner scope shall not hide an identifier declared in an outer scope.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_10_3 ("2-10-3", "A typedef name (including qualification, if any) shall be a unique identifier.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_10_4 ("2-10-4", "A class, union or enum name (including qualification, if any) shall be a unique identifier.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_10_5 ("2-10-5", "The identifier name of a non-member object or function with static storage duration should not be reused.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_2_10_6 ("2-10-6", "If an identifier refers to a type, it shall not also refer to an object or a function in the same scope.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_13_1 ("2-13-1", "Only those escape sequencesthat are defined in ISO/IEC14882:2003 shall be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_13_2 ("2-13-2", "Octal constants (other than zero) and octal escape sequences (other than “\\0”) shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_13_3 ("2-13-3", "A “U” suffix shall be applied to all octal or hexadecimal integer literals of unsigned type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_13_4 ("2-13-4", "Literal suffixes shall be upper case.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_2_13_5 ("2-13-5", "Narrow and wide string literals shall not be concatenated.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_3_1_1 ("3-1-1", "It shall be possible to include any header file in multiple translation units without violating the One Definition Rule.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_3_1_2 ("3-1-2", "Functions shall not be declared at block scope.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_3_1_3 ("3-1-3", "When an array is declared, its size shall either be stated explicitly or defined implicitly by initialization.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_3_2_1 ("3-2-1", "All declarations of an object or function shall have compatible types.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_3_2_2 ("3-2-2", "The One Definition Rule shall not be violated.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_3_2_3 ("3-2-3", "A type, object or function that is used in multiple translation units shall be declared in one and only one file.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_3_2_4 ("3-2-4", "An identifier with external linkage shall have exactly one definition.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_3_3_1 ("3-3-1", "Objects or functions with external linkage shall be declared in a header file.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_3_3_2 ("3-3-2", "If a function has internal linkage then all re-declarations shall include the static storage class specifier.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_3_4_1 ("3-4-1", "An identifier declared to be an object or type shall be defined in a block that minimizes its visibility.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_3_9_1 ("3-9-1", "The types used for an object, a function return type, or a function parameter shall be token-for-token identical in all declarations and re-declarations.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_3_9_2 ("3-9-2", "typedefs that indicate size and signedness should be used in place of the basic numerical types.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_3_9_3 ("3-9-3", "The underlying bit representations of floating-point values shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_4_5_1 ("4-5-1", "Expressions with type bool shall not be used as operands to built-in operators other than the assignment operator =, the logical operators &&, ||, !, the equality operators == and !=, the unary & operator, and the conditional operator.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_4_5_2 ("4-5-2", "Expressions with type enum shall not be used as operands to built-in operators other than the subscript operator [ ], the assignment operator =, the equality operators == and !=, the unary & operator, and the relational operators <, <=, >, >=.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_4_5_3 ("4-5-3", "Expressions with type (plain) char and wchar_t shall not be used as operands to built-in operators other than the assignment operator =, the equality operators == and !=, and the unary & operator.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_4_10_1 ("4-10-1", "NULL shall not be used as an integer value.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_4_10_2 ("4-10-2", "Literal zero (0) shall not be used as the null-pointer-constant.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_5_0_1 ("5-0-1", "The value of an expression shall be the same under any order of evaluation that the standard permits.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_2 ("5-0-2", "Limited dependence should be placed on C++ operator precedence rules in expressions.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_5_0_3 ("5-0-3", "A cvalue expression shall not be implicitly converted to a different underlying type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_4 ("5-0-4", "An implicit integral conversion shall not change the signedness of the underlying type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_5 ("5-0-5", "There shall be no implicit floating-integral conversions.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_6 ("5-0-6", "An implicit integral or floating-point conversion shall not reduce the size of the underlying type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_7 ("5-0-7", "There shall be no explicit floating-integral conversions of a cvalue expression.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_8 ("5-0-8", "An explicit integral or floating-point conversion shall not increase the size of the underlying type of a cvalue expression.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_9 ("5-0-9", "An explicit integral conversion shall not change the signedness of the underlying type of a cvalue expression.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_10 ("5-0-10", "If the bitwise operators ~ and << are applied to an operand with an underlying type of unsigned char or unsigned short, the result shall be immediately cast to the underlying type of the operand.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_11 ("5-0-11", "The plain char type shall only be used for the storage and use of character values.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_12 ("5-0-12", "signed char and unsigned char type shall only be used for the storage and use of numeric values.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_13 ("5-0-13", "The condition of an if-statement and the condition of an iteration-statement shall have type bool.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_14 ("5-0-14", "The first operand of a conditional-operator shall have type bool.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_15 ("5-0-15", "Array indexing shall be the only form of pointer arithmetic.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_16 ("5-0-16", "A pointer operand and any pointer resulting from pointer arithmetic using that operand shall both address elements of the same array.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_17 ("5-0-17", "Subtraction between pointers shall only be applied to pointers that address elements of the same array.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_18 ("5-0-18", ">, >=, <, <= shall not be applied to objects of pointer type, except where they point to the same array.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_19 ("5-0-19", "The declaration of objects shall contain no more than two levels of pointer indirection.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_20 ("5-0-20", "Non-constant operands to a binary bitwise operator shall have the same underlying type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_0_21 ("5-0-21", "Bitwise operators shall only be applied to operands of unsigned underlying type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_2_1 ("5-2-1", "Each operand of a logical && or || shall be a postfix‐expression.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_2_2 ("5-2-2", "A pointer to a virtual base class shall only be cast to a pointer to a derived class by means of dynamic_cast.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_2_3 ("5-2-3", "Casts from a base class to a derived class should not be performed on polymorphic types.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_5_2_4 ("5-2-4", "C-style casts (other than void casts) and functional notation casts (other than explicit constructor calls) shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_2_5 ("5-2-5", "A cast shall not remove any const or volatile qualification from the type of a pointer or reference.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_2_6 ("5-2-6", "A cast shall not convert a pointer to a function to any other pointer type, including a pointer to function type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_2_7 ("5-2-7", "An object with pointer type shall not be converted to an unrelated pointer type, either directly or indirectly.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_2_8 ("5-2-8", "An object with integer type or pointer to void type shall not be converted to an object with pointer type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_2_9 ("5-2-9", "A cast should not convert a pointer type to an integral type.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_5_2_10 ("5-2-10", "The increment (++) and decrement (--) operators should not be mixed with other operators in an expression.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_5_2_11 ("5-2-11", "The comma operator, && operator and the || operator shall not be overloaded.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_2_12 ("5-2-12", "An identifier with array type passed as a function argument shall not decay to a pointer.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_3_1 ("5-3-1", "Each operand of the ! operator, the logical && or the logical || operators shall have type bool.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_3_2 ("5-3-2", "The unary minus operator shall not be applied to an expression whose underlying type is unsigned.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_3_3 ("5-3-3", "The unary & operator shall not be overloaded.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_3_4 ("5-3-4", "Evaluation of the operand to the sizeof operator shall not contain side effects.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_8_1 ("5-8-1", "The right hand operand of a shift operator shall lie between zero and one less than the width in bits of the underlying type of the left hand operand.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_14_1 ("5-14-1", "The right hand operand of a logical && or || operator shall not contain side effects.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_17_1 ("5-17-1", "The semantic equivalence between a binary operator and its assignment operator form shall be preserved.", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    M08_5_18_1 ("5-18-1", "The comma operator shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_5_19_1 ("5-19-1", "Evaluation of constant unsigned integer expressions should not lead to wrap-around.", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    M08_6_2_1 ("6-2-1", "Assignment operators shall not be used in sub-expressions.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_2_2 ("6-2-2", "Floating-point expressions shall not be directly or indirectly tested for equality or inequality.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_2_3 ("6-2-3", "Before preprocessing, a null statement shall only occur on a line by itself; it may be followed by a comment, provided that the first character following the null statement is a white-space character.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_3_1 ("6-3-1", "The statement forming the body of a switch, while, do ... while or for statement shall be a compound statement.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_4_1 ("6-4-1", "An if ( condition ) construct shall be followed by a compound statement. The else keyword shall be followed by either a compound statement, or another if statement.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_4_2 ("6-4-2", "All if ... else if constructs shall be terminated with an else clause.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_4_3 ("6-4-3", "A switch statement shall be a well-formed switch statement.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_4_4 ("6-4-4", "A switch-label shall only be used when the most closely-enclosing compound statement is the body of a switch statement.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_4_5 ("6-4-5", "An unconditional throw or break statement shall terminate every non-empty switch-clause.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_4_6 ("6-4-6", "The final clause of a switch statement shall be the default-clause.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_4_7 ("6-4-7", "The condition of a switch statement shall not have bool type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_4_8 ("6-4-8", "Every switch statement shall have at least one case-clause.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_5_1 ("6-5-1", "A for loop shall contain a single loop-counter which shall not have floating type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_5_2 ("6-5-2", "If loop-counter is not modified by -- or ++, then, within condition, the loop-counter shall only be used as an operand to <=, <, > or >=.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_5_3 ("6-5-3", "The loop-counter shall not be modified within condition or statement.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_5_4 ("6-5-4", "The loop-counter shall be modified by one of: --, ++, -=n, or +=n; where n remains constant for the duration of the loop.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_5_5 ("6-5-5", "A loop-control-variable other than the loop-counter shall not be modified within condition or expression.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_5_6 ("6-5-6", "A loop-control-variable other than the loop-counter which is modified in statement shall have type bool.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_6_1 ("6-6-1", "Any label referenced by a goto statement shall be declared in the same block, or in a block enclosing the goto statement.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_6_2 ("6-6-2", "The goto statement shall jump to a label declared later in the same function body.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_6_3 ("6-6-3", "The continue statement shall only be used within a well-formed for loop.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_6_4 ("6-6-4", "For any iteration statement there shall be no more than one break or goto statement used for loop termination.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_6_6_5 ("6-6-5", "A function shall have a single point of exit at the end of the function.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_7_1_1 ("7-1-1", "A variable which is not modified shall be const qualified.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_1_2 ("7-1-2", "A pointer or reference parameter in a function shall be declared as pointer to const or reference to const if the corresponding object is not modified.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_2_1 ("7-2-1", "An expression with enum underlying type shall only have values corresponding to the enumerators of the enumeration.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_3_1 ("7-3-1", "The global namespace shall only contain main, namespace declarations and extern \"C\" declarations.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_3_2 ("7-3-2", "The identifier main shall not be used for a function other than the global function main.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_3_3 ("7-3-3", "There shall be no unnamed namespaces in header files.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_3_4 ("7-3-4", "using-directives shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_3_5 ("7-3-5", "Multiple declarations for an identifier in the same namespace shall not straddle a using-declaration for that identifier.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_3_6 ("7-3-6", "using-directives and using-declarations (excluding class scope or function scope using-declarations) shall not be used in header files.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_4_1 ("7-4-1", "All usage of assembler shall be documented.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    M08_7_4_2 ("7-4-2", "Assembler instructions shall only be introduced using the asm declaration.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_4_3 ("7-4-3", "Assembly language shall be encapsulated and isolated.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_5_1 ("7-5-1", "A function shall not return a reference or a pointer to an automatic variable (including parameters), defined within the function.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_5_2 ("7-5-2", "The address of an object with automatic storage shall not be assigned to another object that may persist after the first object has ceased to exist.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_5_3 ("7-5-3", "A function shall not return a reference or a pointer to a parameter that is passed by reference or const reference.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_7_5_4 ("7-5-4", "Functions should not call themselves, either directly or indirectly.", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    M08_8_0_1 ("8-0-1", "An init-declarator-list or a member-declarator-list shall consist of a single init-declarator or member-declarator respectively.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_8_3_1 ("8-3-1", "Parameters in an overriding virtual function shall either use the same default arguments as the function they override, or else shall not specify any default arguments.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_8_4_1 ("8-4-1", "Functions shall not be defined using the ellipsis notation.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_8_4_2 ("8-4-2", "The identifiers used for the parameters in a re-declaration of a function shall be identical to those in the declaration.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_8_4_3 ("8-4-3", "All exit paths from a function with non-void return type shall have an explicit return statement with an expression.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_8_4_4 ("8-4-4", "A function identifier shall either be used to call the function or it shall be preceded by &.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_8_5_1 ("8-5-1", "All variables shall have a defined value before they are used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_8_5_2 ("8-5-2", "Braces shall be used to indicate and match the structure in the non-zero initialization of arrays and structures.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_8_5_3 ("8-5-3", "In an enumerator list, the = construct shall not be used to explicitly initialize members other than the first, unless all items are explicitly initialized.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_9_3_1 ("9-3-1", "const member functions shall not return non-const pointers or references to class-data.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_9_3_2 ("9-3-2", "Member functions shall not return non-const handles to class-data.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_9_3_3 ("9-3-3", "If a member function can be made static then it shall be made static, otherwise if it can be made const then it shall be made const.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_9_5_1 ("9-5-1", "Unions shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_9_6_1 ("9-6-1", "When the absolute positioning of bits representing a bit-field is required, then the behaviour and packing of bit-fields shall be documented.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    M08_9_6_2 ("9-6-2", "Bit-fieldsshall be either bool type or an explicitly unsigned or signed integral type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_9_6_3 ("9-6-3", "Bit-fields shall not have enum type.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_9_6_4 ("9-6-4", "Named bit-fields with signed integer type shall have a length of more than one bit.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_10_1_1 ("10-1-1", "Classes should not be derived from virtual bases.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_10_1_2 ("10-1-2", "A base class shall only be declared virtual if it is used in a diamond hierarchy.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_10_1_3 ("10-1-3", "An accessible base class shall not be both virtual and non-virtual in the same hierarchy.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_10_2_1 ("10-2-1", "All accessible entity names within a multiple inheritance hierarchy should be unique.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_10_3_1 ("10-3-1", "There shall be no more than one definition of each virtual function on each path through the inheritance hierarchy.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_10_3_2 ("10-3-2", "Each overriding virtual function shall be declared with the virtual keyword.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_10_3_3 ("10-3-3", "A virtual function shall only be overridden by a pure virtual function if it is itself declared as pure virtual.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_11_0_1 ("11-0-1", "Member data in non-POD class types shall be private.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_12_1_1 ("12-1-1", "An object’s dynamic type shall not be used from the body of its constructor or destructor.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_12_1_2 ("12-1-2", "All constructors of a class should explicitly call a constructor for all of its immediate base classes and all virtual base classes.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_12_1_3 ("12-1-3", "All constructors that are callable with a single argument of fundamental type shall be declared explicit.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_12_8_1 ("12-8-1", "A copy constructor shall only initialize its base classes and the non-static members of the class of which it is a member.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_12_8_2 ("12-8-2", "The copy assignment operator shall be declared protected or private in an abstract class.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_14_5_1 ("14-5-1", "A non-member generic function shall only be declared in a namespace that is not an associated namespace.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_14_5_2 ("14-5-2", "A copy constructor shall be declared when there is a template constructor with a single parameter that is a generic parameter.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_14_5_3 ("14-5-3", "A copy assignment operator shall be declared when there is a template assignment operator with a parameter that is a generic parameter.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_14_6_1 ("14-6-1", "In a class template with a dependent base, any name that may be found in that dependent base shall be referred to using a qualified-id or this->", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_14_6_2 ("14-6-2", "The function chosen by overload resolution shall resolve to a function declared previously in the translation unit.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_14_7_1 ("14-7-1", "All class templates, function templates, class template member functions and class template static members shall be instantiated at least once.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_14_7_2 ("14-7-2", "For any given template specialization, an explicit instantiation of the template with the template-arguments used in the specialization shall not render the program ill-formed.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_14_7_3 ("14-7-3", "All partial and explicit specializations for a template shall be declared in the same file as the declaration of their primary template.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_14_8_1 ("14-8-1", "Overloaded function templates shall not be explicitly specialized.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_14_8_2 ("14-8-2", "The viable function set for a function call should either contain no function specializations, or only contain function specializations.", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    M08_15_0_1 ("15-0-1", "Exceptions shall only be used for error handling.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    M08_15_0_2 ("15-0-2", "An exception object should not have pointer type.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_15_0_3 ("15-0-3", "Control shall not be transferred into a try or catch block using a goto or a switch statement.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_1_1 ("15-1-1", "The assignment-expression of a throw statement shall not itself cause an exception to be thrown.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_1_2 ("15-1-2", "NULL shall not be thrown explicitly.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_1_3 ("15-1-3", "An empty throw (throw;) shall only be used in the compound-statement of a catch handler.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_3_1 ("15-3-1", "Exceptions shall be raised only after start-up and before termination of the program.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_3_2 ("15-3-2", "There should be at least one exception handler to catch all otherwise unhandled exceptions", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_15_3_3 ("15-3-3", "Handlers of a function-try-block implementation of a class constructoror destructor shall not reference non-static members from this class or its bases.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_3_4 ("15-3-4", "Each exception explicitly thrown in the code shall have a handler of a compatible type in all call paths that could lead to that point.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_3_5 ("15-3-5", "A class type exception shall always be caught by reference.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_3_6 ("15-3-6", "Where multiple handlers are provided in a single try-catch statement or function-try-block for a derived class and some or all of its bases, the handlers shall be ordered most-derived to base class.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_3_7 ("15-3-7", "Where multiple handlers are provided in a single try-catch statement or function-try-block, any ellipsis (catch-all) handler shall occur last.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_4_1 ("15-4-1", "If a function is declared with an exception-specification, then all declarations of the same function (in other translation units) shall be declared with the same set of type-ids.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_5_1 ("15-5-1", "A class destructor shall not exit with an exception.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_5_2 ("15-5-2", "Where a function’s declaration includes an exception-specification, the function shall only be capable of throwing exceptions of the indicated type(s).", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_15_5_3 ("15-5-3", "The terminate() function shall not be called implicitly.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_16_0_1 ("16-0-1", "#include directives in a file shall only be preceded by other preprocessor directives or comments.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_0_2 ("16-0-2", "Macros shall only be #define’d or #undef’d in the global namespace.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_0_3 ("16-0-3", "#undef shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_0_4 ("16-0-4", "Function-like macros shall not be defined.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_0_5 ("16-0-5", "Arguments to a function-like macro shall not contain tokens that look like preprocessing directives.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_0_6 ("16-0-6", "In the definition of a function-like macro, each instance of a parameter shall be enclosed in parentheses, unless it is used as the operand of # or ##.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_0_7 ("16-0-7", "Undefined macro identifiers shall not be used in #if or #elif preprocessor directives, except as operands to the defined operator.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_0_8 ("16-0-8", "If the # token appears as the first token on a line, then it shall be immediately followed by a preprocessing token.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_1_1 ("16-1-1", "The defined preprocessor operator shall only be used in one of the two standard forms.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_1_2 ("16-1-2", "All #else, #elif and #endif preprocessor directives shall reside in the same file as the #if or #ifdef directive to which they are related.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_2_1 ("16-2-1", "The pre-processor shall only be used for file inclusion and include guards.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_2_2 ("16-2-2", "C++ macros shall only be used for: include guards, type qualifiers, or storage class specifiers.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_2_3 ("16-2-3", "Include guards shall be provided.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_2_4 ("16-2-4", "The ', \", /* or // characters shall not occur in a header file name.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_2_5 ("16-2-5", "The \\ character should not occur in a header file name.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_16_2_6 ("16-2-6", "The #include directive shall be followed by either a <filename> or \"filename\" sequence.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_3_1 ("16-3-1", "There shall be at most one occurrence of the # or ## operators in a single macro definition.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_16_3_2 ("16-3-2", "The # and ## operators should not be used.", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    M08_16_6_1 ("16-6-1", "All uses of the #pragma directive shall be documented.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),

    M08_17_0_1 ("17-0-1", "Reserved identifiers, macros and functions in the standard library shall not be defined, redefined or undefined.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_17_0_2 ("17-0-2", "The names of standard library macros and objects shall not be reused.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_17_0_3 ("17-0-3", "The names of standard library functions shall not be overridden.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_17_0_4 ("17-0-4", "All library code shall conform to MISRA C++.", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    M08_17_0_5 ("17-0-5", "The setjmp macro and the longjmp function shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_18_0_1 ("18-0-1", "The C library shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_18_0_2 ("18-0-2", "The library functions atof, atoi and atol from library <cstdlib> shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_18_0_3 ("18-0-3", "The library functions abort, exit, getenv and system from library <cstdlib> shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_18_0_4 ("18-0-4", "The time handling functions of library <ctime> shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_18_0_5 ("18-0-5", "The unbounded functions of library <cstring> shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_18_2_1 ("18-2-1", "The macro offsetof shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_18_4_1 ("18-4-1", "Dynamic heap memory allocation shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    M08_18_7_1 ("18-7-1", "The signal handling facilities of <csignal> shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_19_3_1 ("19-3-1", "The error indicator errno shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    M08_27_0_1 ("27-0-1", "The stream input/output library <cstdio> shall not be used.", Boolean.TRUE, Implementability.IMPLEMENTABLE);


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
    public boolean isRuleRequired() {
      return isMandatory;
    }

    @Override
    public String getTitle() {
      return title;
    }
  }

  public MisraCPP2008 () {

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
  public List<String> getRspecReferenceFieldValues(Rule rule) {
    return rule.getMisraCpp();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setMisraCpp(ids);
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

  @Override
  public CodingStandardRule[] getCodingStandardRules() {
    return StandardRule.values();
  }

}
