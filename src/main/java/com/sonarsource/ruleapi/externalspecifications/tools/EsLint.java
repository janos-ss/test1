/*
 * Copyright (C) 2014-2017 SonarSource SA
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


public class EsLint extends AbstractReportableExternalTool{

  private static final String STANDARD_NAME = "ESLint";
  private static final Language LANGUAGE = Language.JS;


  @Override
  public String getStandardName() {
    return STANDARD_NAME;
  }

  @Override
  public String getRSpecReferenceFieldName() {
    return STANDARD_NAME;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {
    return rule.getEsLint();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setEsLint(ids);
  }

  @Override
  public Language getLanguage() {
    return LANGUAGE;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {
    return EsLintRule.values();
  }

  public enum EsLintRule implements CodingStandardRule {

    NO_COND_ASSIGN ("no-cond-assign", "disallow assignment operators in conditional expressions", Implementability.IMPLEMENTABLE),
    NO_CONSOLE ("no-console", "disallow the use of console", Implementability.IMPLEMENTABLE),
    NO_CONSTANT_CONDITION ("no-constant-condition", "disallow constant expressions in conditions", Implementability.IMPLEMENTABLE),
    NO_CONTROL_REGEX ("no-control-regex", "disallow control characters in regular expressions", Implementability.IMPLEMENTABLE),
    NO_DEBUGGER ("no-debugger", "disallow the use of debugger", Implementability.IMPLEMENTABLE),
    NO_DUPE_ARGS ("no-dupe-args", "disallow duplicate arguments in function definitions", Implementability.IMPLEMENTABLE),
    NO_DUPE_KEYS ("no-dupe-keys", "disallow duplicate keys in object literals", Implementability.IMPLEMENTABLE),
    NO_DUPLICATE_CASE ("no-duplicate-case", "disallow duplicate case labels", Implementability.IMPLEMENTABLE),
    NO_EMPTY_CHARACTER_CLASS ("no-empty-character-class", "disallow empty character classes in regular expressions", Implementability.IMPLEMENTABLE),
    NO_EMPTY ("no-empty", "disallow empty block statements", Implementability.IMPLEMENTABLE),
    NO_EX_ASSIGN ("no-ex-assign", "disallow reassigning exceptions in catch clauses", Implementability.IMPLEMENTABLE),
    NO_EXTRA_BOOLEAN_CAST ("no-extra-boolean-cast", "disallow unnecessary boolean casts", Implementability.IMPLEMENTABLE),
    NO_EXTRA_PARENS ("no-extra-parens", "disallow unnecessary parentheses", Implementability.IMPLEMENTABLE),
    NO_EXTRA_SEMI ("no-extra-semi", "disallow unnecessary semicolons", Implementability.IMPLEMENTABLE),
    NO_FUNC_ASSIGN ("no-func-assign", "disallow reassigning function declarations", Implementability.IMPLEMENTABLE),
    NO_INNER_DECLARATIONS ("no-inner-declarations", "disallow function or var declarations in nested blocks", Implementability.IMPLEMENTABLE),
    NO_INVALID_REGEXP ("no-invalid-regexp", "disallow invalid regular expression strings in RegExp constructors", Implementability.IMPLEMENTABLE),
    NO_IRREGULAR_WHITESPACE ("no-irregular-whitespace", "disallow irregular whitespace outside of strings and comments", Implementability.IMPLEMENTABLE),
    NO_OBJ_CALLS ("no-obj-calls", "disallow calling global object properties as functions", Implementability.IMPLEMENTABLE),
    NO_PROTOTYPE_BUILTINS ("no-prototype-builtins", "disallow calling some Object.prototype methods directly on objects", Implementability.IMPLEMENTABLE),
    NO_REGEX_SPACES ("no-regex-spaces", "disallow multiple spaces in regular expressions", Implementability.IMPLEMENTABLE),
    NO_SPARSE_ARRAYS ("no-sparse-arrays", "disallow sparse arrays", Implementability.IMPLEMENTABLE),
    NO_TEMPLATE_CURLY_IN_STRING ("no-template-curly-in-string", "disallow template literal placeholder syntax in regular strings", Implementability.IMPLEMENTABLE),
    NO_UNEXPECTED_MULTILINE ("no-unexpected-multiline", "disallow confusing multiline expressions", Implementability.IMPLEMENTABLE),
    NO_UNREACHABLE ("no-unreachable", "disallow unreachable code after return, throw, continue, and break statements", Implementability.IMPLEMENTABLE),
    NO_UNSAFE_FINALLY ("no-unsafe-finally", "disallow control flow statements in finally blocks", Implementability.IMPLEMENTABLE),
    NO_UNSAFE_NEGATION ("no-unsafe-negation", "disallow negating the left operand of relational operators", Implementability.IMPLEMENTABLE),
    USE_ISNAN ("use-isnan", "require calls to isNaN) when checking for NaN", Implementability.IMPLEMENTABLE),
    VALID_JSDOC ("valid-jsdoc", "enforce valid JSDoc comments", Implementability.IMPLEMENTABLE),
    VALID_TYPEOF ("valid-typeof", "enforce comparing typeof expressions against valid strings", Implementability.IMPLEMENTABLE),

    ACCESSOR_PAIRS ("accessor-pairs", "enforce getter and setter pairs in objects", Implementability.IMPLEMENTABLE),
    ARRAY_CALLBACK_RETURN ("array-callback-return", "enforce return statements in callbacks of array methods", Implementability.IMPLEMENTABLE),
    BLOCK_SCOPED_VAR ("block-scoped-var", "enforce the use of variables within the scope they are defined", Implementability.IMPLEMENTABLE),
    CLASS_METHODS_USE_THIS ("class-methods-use-this", "enforce that class methods utilize this", Implementability.IMPLEMENTABLE),
    COMPLEXITY ("complexity", "enforce a maximum cyclomatic complexity allowed in a program", Implementability.IMPLEMENTABLE),
    CONSISTENT_RETURN ("consistent-return", "require return statements to either always or never specify values", Implementability.IMPLEMENTABLE),
    CURLY ("curly", "enforce consistent brace style for all control statements", Implementability.IMPLEMENTABLE),
    DEFAULT_CASE ("default-case", "require default cases in switch statements", Implementability.IMPLEMENTABLE),
    DOT_LOCATION ("dot-location", "enforce consistent newlines before and after dots", Implementability.REJECTED),
    DOT_NOTATION ("dot-notation", "enforce dot notation whenever possible", Implementability.IMPLEMENTABLE),
    EQEQEQ ("eqeqeq", "require the use of === and !==", Implementability.IMPLEMENTABLE),
    GUARD_FOR_IN ("guard-for-in", "require for-in loops to include an if statement", Implementability.IMPLEMENTABLE),
    NO_ALERT ("no-alert", "disallow the use of alert, confirm, and prompt", Implementability.IMPLEMENTABLE),
    NO_CALLER ("no-caller", "disallow the use of arguments.caller or arguments.callee", Implementability.IMPLEMENTABLE),
    NO_CASE_DECLARATIONS ("no-case-declarations", "disallow lexical declarations in case clauses", Implementability.IMPLEMENTABLE),
    NO_DIV_REGEX ("no-div-regex", "disallow division operators explicitly at the beginning of regular expressions", Implementability.IMPLEMENTABLE),
    NO_ELSE_RETURN ("no-else-return", "disallow else blocks after return statements in if statements", Implementability.IMPLEMENTABLE),
    NO_EMPTY_FUNCTION ("no-empty-function", "disallow empty functions", Implementability.IMPLEMENTABLE),
    NO_EMPTY_PATTERN ("no-empty-pattern", "disallow empty destructuring patterns", Implementability.IMPLEMENTABLE),
    NO_EQ_NULL ("no-eq-null", "disallow null comparisons without type-checking operators", Implementability.IMPLEMENTABLE),
    NO_EVAL ("no-eval", "disallow the use of eval)", Implementability.IMPLEMENTABLE),
    NO_EXTEND_NATIVE ("no-extend-native", "disallow extending native types", Implementability.IMPLEMENTABLE),
    NO_EXTRA_BIND ("no-extra-bind", "disallow unnecessary calls to .bind)", Implementability.IMPLEMENTABLE),
    NO_EXTRA_LABEL ("no-extra-label", "disallow unnecessary labels", Implementability.IMPLEMENTABLE),
    NO_FALLTHROUGH ("no-fallthrough", "disallow fallthrough of case statements", Implementability.IMPLEMENTABLE),
    NO_FLOATING_DECIMAL ("no-floating-decimal", "disallow leading or trailing decimal points in numeric literals", Implementability.IMPLEMENTABLE),
    NO_GLOBAL_ASSIGN ("no-global-assign", "disallow assignments to native objects or read-only global variables", Implementability.IMPLEMENTABLE),
    NO_IMPLICIT_COERCION ("no-implicit-coercion", "disallow shorthand type conversions", Implementability.IMPLEMENTABLE),
    NO_IMPLICIT_GLOBALS ("no-implicit-globals", "disallow var and named function declarations in the global scope", Implementability.IMPLEMENTABLE),
    NO_IMPLIED_EVAL ("no-implied-eval", "disallow the use of eval)-like methods", Implementability.IMPLEMENTABLE),
    NO_INVALID_THIS ("no-invalid-this", "disallow this keywords outside of classes or class-like objects", Implementability.IMPLEMENTABLE),
    NO_ITERATOR ("no-iterator", "disallow the use of the __iterator__ property", Implementability.IMPLEMENTABLE),
    NO_LABELS ("no-labels", "disallow labeled statements", Implementability.IMPLEMENTABLE),
    NO_LONE_BLOCKS ("no-lone-blocks", "disallow unnecessary nested blocks", Implementability.IMPLEMENTABLE),
    NO_LOOP_FUNC ("no-loop-func", "disallow function declarations and expressions inside loop statements", Implementability.IMPLEMENTABLE),
    NO_MAGIC_NUMBERS ("no-magic-numbers", "disallow magic numbers", Implementability.IMPLEMENTABLE),
    NO_MULTI_SPACES ("no-multi-spaces", "disallow multiple spaces", Implementability.REJECTED),
    NO_MULTI_STR ("no-multi-str", "disallow multiline strings", Implementability.IMPLEMENTABLE),
    NO_NEW_FUNC ("no-new-func", "disallow new operators with the Function object", Implementability.IMPLEMENTABLE),
    NO_NEW_WRAPPERS ("no-new-wrappers", "disallow new operators with the String, Number, and Boolean objects", Implementability.IMPLEMENTABLE),
    NO_NEW ("no-new", "disallow new operators outside of assignments or comparisons", Implementability.IMPLEMENTABLE),
    NO_OCTAL_ESCAPE ("no-octal-escape", "disallow octal escape sequences in string literals", Implementability.IMPLEMENTABLE),
    NO_OCTAL ("no-octal", "disallow octal literals", Implementability.IMPLEMENTABLE),
    NO_PARAM_REASSIGN ("no-param-reassign", "disallow reassigning function parameters", Implementability.IMPLEMENTABLE),
    NO_PROTO ("no-proto", "disallow the use of the __proto__ property", Implementability.IMPLEMENTABLE),
    NO_REDECLARE ("no-redeclare", "disallow var redeclaration", Implementability.IMPLEMENTABLE),
    NO_RETURN_ASSIGN ("no-return-assign", "disallow assignment operators in return statements", Implementability.IMPLEMENTABLE),
    NO_SCRIPT_URL ("no-script-url", "disallow javascript: urls", Implementability.IMPLEMENTABLE),
    NO_SELF_ASSIGN ("no-self-assign", "disallow assignments where both sides are exactly the same", Implementability.IMPLEMENTABLE),
    NO_SELF_COMPARE ("no-self-compare", "disallow comparisons where both sides are exactly the same", Implementability.IMPLEMENTABLE),
    NO_SEQUENCES ("no-sequences", "disallow comma operators", Implementability.IMPLEMENTABLE),
    NO_THROW_LITERAL ("no-throw-literal", "disallow throwing literals as exceptions", Implementability.IMPLEMENTABLE),
    NO_UNMODIFIED_LOOP_CONDITION ("no-unmodified-loop-condition", "disallow unmodified loop conditions", Implementability.IMPLEMENTABLE),
    NO_UNUSED_EXPRESSIONS ("no-unused-expressions", "disallow unused expressions", Implementability.IMPLEMENTABLE),
    NO_UNUSED_LABELS ("no-unused-labels", "disallow unused labels", Implementability.IMPLEMENTABLE),
    NO_USELESS_CALL ("no-useless-call", "disallow unnecessary calls to .call) and .apply)", Implementability.IMPLEMENTABLE),
    NO_USELESS_CONCAT ("no-useless-concat", "disallow unnecessary concatenation of literals or template literals", Implementability.IMPLEMENTABLE),
    NO_USELESS_ESCAPE ("no-useless-escape", "disallow unnecessary escape characters", Implementability.IMPLEMENTABLE),
    NO_VOID ("no-void", "disallow void operators", Implementability.IMPLEMENTABLE),
    NO_WARNING_COMMENTS ("no-warning-comments", "disallow specified warning terms in comments", Implementability.REJECTED),
    NO_WITH ("no-with", "disallow with statements", Implementability.IMPLEMENTABLE),
    RADIX ("radix", "enforce the consistent use of the radix argument when using parseInt)", Implementability.IMPLEMENTABLE),
    VARS_ON_TOP ("vars-on-top", "require var declarations be placed at the top of their containing scope", Implementability.IMPLEMENTABLE),
    WRAP_IIFE ("wrap-iife", "require parentheses around immediate function invocations", Implementability.IMPLEMENTABLE),
    YODA ("yoda", "require or disallow “Yoda” conditions", Implementability.IMPLEMENTABLE),

    STRICT ("strict", "require or disallow strict mode directives", Implementability.IMPLEMENTABLE),

    INIT_DECLARATIONS ("init-declarations", "require or disallow initialization in var declarations", Implementability.IMPLEMENTABLE),
    NO_CATCH_SHADOW ("no-catch-shadow", "disallow catch clause parameters from shadowing variables in the outer scope", Implementability.IMPLEMENTABLE),
    NO_DELETE_VAR ("no-delete-var", "disallow deleting variables", Implementability.IMPLEMENTABLE),
    NO_LABEL_VAR ("no-label-var", "disallow labels that share a name with a variable", Implementability.IMPLEMENTABLE),
    NO_RESTRICTED_GLOBALS ("no-restricted-globals", "disallow specified global variables", Implementability.IMPLEMENTABLE),
    NO_SHADOW_RESTRICTED_NAMES ("no-shadow-restricted-names", "disallow identifiers from shadowing restricted names", Implementability.IMPLEMENTABLE),
    NO_SHADOW ("no-shadow", "disallow var declarations from shadowing variables in the outer scope", Implementability.IMPLEMENTABLE),
    NO_UNDEF_INIT ("no-undef-init", "disallow initializing variables to undefined", Implementability.IMPLEMENTABLE),
    NO_UNDEF ("no-undef", "disallow the use of undeclared variables unless mentioned in /*global */ comments", Implementability.IMPLEMENTABLE),
    NO_UNDEFINED ("no-undefined", "disallow the use of undefined as an identifier", Implementability.IMPLEMENTABLE),
    NO_UNUSED_VARS ("no-unused-vars", "disallow unused variables", Implementability.IMPLEMENTABLE),
    NO_USE_BEFORE_DEFINE ("no-use-before-define", "disallow the use of variables before they are defined", Implementability.IMPLEMENTABLE),

    CALLBACK_RETURN ("callback-return", "require return statements after callbacks", Implementability.IMPLEMENTABLE),
    GLOBAL_REQUIRE ("global-require", "require require) calls to be placed at top-level module scope", Implementability.IMPLEMENTABLE),
    HANDLE_CALLBACK_ERR ("handle-callback-err", "require error handling in callbacks", Implementability.IMPLEMENTABLE),
    NO_MIXED_REQUIRES ("no-mixed-requires", "disallow require calls to be mixed with regular var declarations", Implementability.IMPLEMENTABLE),
    NO_NEW_REQUIRE ("no-new-require", "disallow new operators with calls to require", Implementability.IMPLEMENTABLE),
    NO_PATH_CONCAT ("no-path-concat", "disallow string concatenation with __dirname and __filename", Implementability.IMPLEMENTABLE),
    NO_PROCESS_ENV ("no-process-env", "disallow the use of process.env", Implementability.IMPLEMENTABLE),
    NO_PROCESS_EXIT ("no-process-exit", "disallow the use of process.exit)", Implementability.IMPLEMENTABLE),
    NO_RESTRICTED_MODULES ("no-restricted-modules", "disallow specified modules when loaded by require", Implementability.IMPLEMENTABLE),
    NO_SYNC ("no-sync", "disallow synchronous methods", Implementability.IMPLEMENTABLE),

    ARRAY_BRACKET_SPACING ("array-bracket-spacing", "enforce consistent spacing inside array brackets", Implementability.REJECTED),
    BLOCK_SPACING ("block-spacing", "enforce consistent spacing inside single-line blocks", Implementability.REJECTED),
    BRACE_STYLE ("brace-style", "enforce consistent brace style for blocks", Implementability.IMPLEMENTABLE),
    CAMELCASE ("camelcase", "enforce camelcase naming convention", Implementability.IMPLEMENTABLE),
    COMMA_DANGLE ("comma-dangle", "require or disallow trailing commas", Implementability.IMPLEMENTABLE),
    COMMA_SPACING ("comma-spacing", "enforce consistent spacing before and after commas", Implementability.REJECTED),
    COMMA_STYLE ("comma-style", "enforce consistent comma style", Implementability.REJECTED),
    COMPUTED_PROPERTY_SPACING ("computed-property-spacing", "enforce consistent spacing inside computed property brackets", Implementability.REJECTED),
    CONSISTENT_THIS ("consistent-this", "enforce consistent naming when capturing the current execution context", Implementability.IMPLEMENTABLE),
    EOL_LAST ("eol-last", "enforce at least one newline at the end of files", Implementability.IMPLEMENTABLE),
    FUNC_CALL_SPACING ("func-call-spacing", "require or disallow spacing between function identifiers and their invocations", Implementability.REJECTED),
    FUNC_NAMES ("func-names", "require or disallow named function expressions", Implementability.IMPLEMENTABLE),
    FUNC_STYLE ("func-style", "enforce the consistent use of either function declarations or expressions", Implementability.IMPLEMENTABLE),
    ID_BLACKLIST ("id-blacklist", "disallow specified identifiers", Implementability.REJECTED),
    ID_LENGTH ("id-length", "enforce minimum and maximum identifier lengths", Implementability.IMPLEMENTABLE),
    ID_MATCH ("id-match", "require identifiers to match a specified regular expression", Implementability.IMPLEMENTABLE),
    INDENT ("indent", "enforce consistent indentation", Implementability.REJECTED),
    JSX_QUOTES ("jsx-quotes", "enforce the consistent use of either double or single quotes in JSX attributes", Implementability.IMPLEMENTABLE),
    KEY_SPACING ("key-spacing", "enforce consistent spacing between keys and values in object literal properties", Implementability.REJECTED),
    KEYWORD_SPACING ("keyword-spacing", "enforce consistent spacing before and after keywords", Implementability.REJECTED),
    LINEBREAK_STYLE ("linebreak-style", "enforce consistent linebreak style", Implementability.REJECTED),
    LINES_AROUND_COMMENT ("lines-around-comment", "require empty lines around comments", Implementability.REJECTED),
    MAX_DEPTH ("max-depth", "enforce a maximum depth that blocks can be nested", Implementability.IMPLEMENTABLE),
    MAX_LEN ("max-len", "enforce a maximum line length", Implementability.IMPLEMENTABLE),
    MAX_LINES ("max-lines", "enforce a maximum number of lines per file", Implementability.IMPLEMENTABLE),
    MAX_NESTED_CALLBACKS ("max-nested-callbacks", "enforce a maximum depth that callbacks can be nested", Implementability.IMPLEMENTABLE),
    MAX_PARAMS ("max-params", "enforce a maximum number of parameters in function definitions", Implementability.IMPLEMENTABLE),
    MAX_STATEMENTS_PER_LINE ("max-statements-per-line", "enforce a maximum number of statements allowed per line", Implementability.IMPLEMENTABLE),
    MAX_STATEMENTS ("max-statements", "enforce a maximum number of statements allowed in function blocks", Implementability.IMPLEMENTABLE),
    MULTILINE_TERNARY ("multiline-ternary", "enforce newlines between operands of ternary expressions", Implementability.REJECTED),
    NEW_CAP ("new-cap", "require constructor function names to begin with a capital letter", Implementability.IMPLEMENTABLE),
    NEW_PARENS ("new-parens", "require parentheses when invoking a constructor with no arguments", Implementability.IMPLEMENTABLE),
    NEWLINE_AFTER_VAR ("newline-after-var", "require or disallow an empty line after var declarations", Implementability.REJECTED),
    NEWLINE_BEFORE_RETURN ("newline-before-return", "require an empty line before return statements", Implementability.REJECTED),
    NEWLINE_PER_CHAINED_CALL ("newline-per-chained-call", "require a newline after each call in a method chain", Implementability.REJECTED),
    NO_ARRAY_CONSTRUCTOR ("no-array-constructor", "disallow Array constructors", Implementability.IMPLEMENTABLE),
    NO_BITWISE ("no-bitwise", "disallow bitwise operators", Implementability.IMPLEMENTABLE),
    NO_CONTINUE ("no-continue", "disallow continue statements", Implementability.IMPLEMENTABLE),
    NO_INLINE_COMMENTS ("no-inline-comments", "disallow inline comments after code", Implementability.IMPLEMENTABLE),
    NO_LONELY_IF ("no-lonely-if", "disallow if statements as the only statement in else blocks", Implementability.IMPLEMENTABLE),
    NO_MIXED_OPERATORS ("no-mixed-operators", "disallow mixed binary operators", Implementability.IMPLEMENTABLE),
    NO_MIXED_SPACES_AND_TABS ("no-mixed-spaces-and-tabs", "disallow mixed spaces and tabs for indentation", Implementability.REJECTED),
    NO_MULTIPLE_EMPTY_LINES ("no-multiple-empty-lines", "disallow multiple empty lines", Implementability.REJECTED),
    NO_NEGATED_CONDITION ("no-negated-condition", "disallow negated conditions", Implementability.IMPLEMENTABLE),
    NO_NESTED_TERNARY ("no-nested-ternary", "disallow nested ternary expressions", Implementability.IMPLEMENTABLE),
    NO_NEW_OBJECT ("no-new-object", "disallow Object constructors", Implementability.IMPLEMENTABLE),
    NO_PLUSPLUS ("no-plusplus", "disallow the unary operators ++ and --", Implementability.IMPLEMENTABLE),
    NO_RESTRICTED_SYNTAX ("no-restricted-syntax", "disallow specified syntax", Implementability.IMPLEMENTABLE),
    NO_TABS ("no-tabs", "disallow tabs in file", Implementability.REJECTED),
    NO_TERNARY ("no-ternary", "disallow ternary operators", Implementability.IMPLEMENTABLE),
    NO_TRAILING_SPACES ("no-trailing-spaces", "disallow trailing whitespace at the end of lines", Implementability.REJECTED),
    NO_UNDERSCORE_DANGLE ("no-underscore-dangle", "disallow dangling underscores in identifiers", Implementability.IMPLEMENTABLE),
    NO_UNNEEDED_TERNARY ("no-unneeded-ternary", "disallow ternary operators when simpler alternatives exist", Implementability.IMPLEMENTABLE),
    NO_WHITESPACE_BEFORE_PROPERTY ("no-whitespace-before-property", "disallow whitespace before properties", Implementability.REJECTED),
    OBJECT_CURLY_NEWLINE ("object-curly-newline", "enforce consistent line breaks inside braces", Implementability.REJECTED),
    OBJECT_CURLY_SPACING ("object-curly-spacing", "enforce consistent spacing inside braces", Implementability.REJECTED),
    OBJECT_PROPERTY_NEWLINE ("object-property-newline", "enforce placing object properties on separate lines", Implementability.REJECTED),
    ONE_VAR_DECLARATION_PER_LINE ("one-var-declaration-per-line", "require or disallow newlines around var declarations", Implementability.IMPLEMENTABLE),
    ONE_VAR ("one-var", "enforce variables to be declared either together or separately in functions", Implementability.IMPLEMENTABLE),
    OPERATOR_ASSIGNMENT ("operator-assignment", "require or disallow assignment operator shorthand where possible", Implementability.IMPLEMENTABLE),
    OPERATOR_LINEBREAK ("operator-linebreak", "enforce consistent linebreak style for operators", Implementability.REJECTED),
    PADDED_BLOCKS ("padded-blocks", "require or disallow padding within blocks", Implementability.REJECTED),
    QUOTE_PROPS ("quote-props", "require quotes around object literal property names", Implementability.IMPLEMENTABLE),
    QUOTES ("quotes", "enforce the consistent use of either backticks, double, or single quotes", Implementability.IMPLEMENTABLE),
    REQUIRE_JSDOC ("require-jsdoc", "require JSDoc comments", Implementability.IMPLEMENTABLE),
    SEMI_SPACING ("semi-spacing", "enforce consistent spacing before and after semicolons", Implementability.REJECTED),
    SEMI ("semi", "require or disallow semicolons instead of ASI", Implementability.IMPLEMENTABLE),
    SORT_KEYS ("sort-keys", "requires object keys to be sorted", Implementability.REJECTED),
    SORT_VARS ("sort-vars", "require variables within the same declaration block to be sorted", Implementability.REJECTED),
    SPACE_BEFORE_BLOCKS ("space-before-blocks", "enforce consistent spacing before blocks", Implementability.REJECTED),
    SPACE_BEFORE_FUNCTION_PAREN ("space-before-function-paren", "enforce consistent spacing before function definition opening parenthesis", Implementability.REJECTED),
    SPACE_IN_PARENS ("space-in-parens", "enforce consistent spacing inside parentheses", Implementability.REJECTED),
    SPACE_INFIX_OPS ("space-infix-ops", "require spacing around operators", Implementability.REJECTED),
    SPACE_UNARY_OPS ("space-unary-ops", "enforce consistent spacing before or after unary operators", Implementability.REJECTED),
    SPACED_COMMENT ("spaced-comment", "enforce consistent spacing after the // or /* in a comment", Implementability.REJECTED),
    UNICODE_BOM ("unicode-bom", "require or disallow Unicode byte order mark BOM)", Implementability.REJECTED),
    WRAP_REGEX ("wrap-regex", "require parenthesis around regex literals", Implementability.IMPLEMENTABLE),

    ARROW_BODY_STYLE ("arrow-body-style", "require braces around arrow function bodies", Implementability.IMPLEMENTABLE),
    ARROW_PARENS ("arrow-parens", "require parentheses around arrow function arguments", Implementability.IMPLEMENTABLE),
    ARROW_SPACING ("arrow-spacing", "enforce consistent spacing before and after the arrow in arrow functions", Implementability.REJECTED),
    CONSTRUCTOR_SUPER ("constructor-super", "require super) calls in constructors", Implementability.IMPLEMENTABLE),
    GENERATOR_STAR_SPACING ("generator-star-spacing", "enforce consistent spacing around * operators in generator functions", Implementability.IMPLEMENTABLE),
    NO_CLASS_ASSIGN ("no-class-assign", "disallow reassigning class members", Implementability.IMPLEMENTABLE),
    NO_CONFUSING_ARROW ("no-confusing-arrow", "disallow arrow functions where they could be confused with comparisons", Implementability.IMPLEMENTABLE),
    NO_CONST_ASSIGN ("no-const-assign", "disallow reassigning const variables", Implementability.IMPLEMENTABLE),
    NO_DUPE_CLASS_MEMBERS ("no-dupe-class-members", "disallow duplicate class members", Implementability.IMPLEMENTABLE),
    NO_DUPLICATE_IMPORTS ("no-duplicate-imports", "disallow duplicate module imports", Implementability.IMPLEMENTABLE),
    NO_NEW_SYMBOL ("no-new-symbol", "disallow new operators with the Symbol object", Implementability.IMPLEMENTABLE),
    NO_RESTRICTED_IMPORTS ("no-restricted-imports", "disallow specified modules when loaded by import", Implementability.IMPLEMENTABLE),
    NO_THIS_BEFORE_SUPER ("no-this-before-super", "disallow this/super before calling super) in constructors", Implementability.IMPLEMENTABLE),
    NO_USELESS_COMPUTED_KEY ("no-useless-computed-key", "disallow unnecessary computed property keys in object literals", Implementability.IMPLEMENTABLE),
    NO_USELESS_CONSTRUCTOR ("no-useless-constructor", "disallow unnecessary constructors", Implementability.IMPLEMENTABLE),
    NO_USELESS_RENAME ("no-useless-rename", "disallow renaming import, export, and destructured assignments to the same name", Implementability.IMPLEMENTABLE),
    NO_VAR ("no-var", "require let or const instead of var", Implementability.IMPLEMENTABLE),
    OBJECT_SHORTHAND ("object-shorthand", "require or disallow method and property shorthand syntax for object literals", Implementability.IMPLEMENTABLE),
    PREFER_ARROW_CALLBACK ("prefer-arrow-callback", "require arrow functions as callbacks", Implementability.IMPLEMENTABLE),
    PREFER_CONST ("prefer-const", "require const declarations for variables that are never reassigned after declared", Implementability.IMPLEMENTABLE),
    PREFER_REFLECT ("prefer-reflect", "require Reflect methods where applicable", Implementability.IMPLEMENTABLE),
    PREFER_REST_PARAMS ("prefer-rest-params", "require rest parameters instead of arguments", Implementability.IMPLEMENTABLE),
    PREFER_SPREAD ("prefer-spread", "require spread operators instead of .apply)", Implementability.IMPLEMENTABLE),
    PREFER_TEMPLATE ("prefer-template", "require template literals instead of string concatenation", Implementability.IMPLEMENTABLE),
    REQUIRE_YIELD ("require-yield", "require generator functions to contain yield", Implementability.IMPLEMENTABLE),
    REST_SPREAD_SPACING ("rest-spread-spacing", "enforce spacing between rest and spread operators and their expressions", Implementability.REJECTED),
    SORT_IMPORTS ("sort-imports", "enforce sorted import declarations within modules", Implementability.IMPLEMENTABLE),
    SYMBOL_DESCRIPTION ("symbol-description", "require symbol descriptions", Implementability.IMPLEMENTABLE),
    TEMPLATE_CURLY_SPACING ("template-curly-spacing", "require or disallow spacing around embedded expressions of template strings", Implementability.IMPLEMENTABLE),
    YIELD_STAR_SPACING ("yield-star-spacing", "require or disallow spacing around the * in yield* expressions", Implementability.REJECTED);


    private String name;
    private String description;
    private Implementability implementability;

    EsLintRule(String name, String description, Implementability implementability) {
      this.name = name;
      this.description = description;
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
}
