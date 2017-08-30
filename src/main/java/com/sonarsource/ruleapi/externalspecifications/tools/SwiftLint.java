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

/**
 *
 * @author jeanchristophecollet
 */
public class SwiftLint extends AbstractReportableExternalTool {

    private static final String STANDARD_NAME = "SwiftLint";
    private static final Language LANGUAGE = Language.SWIFT;

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
        return rule.getSwiftLint();
    }

    @Override
    public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
        rule.setSwiftLint(ids);
    }

    @Override
    public Language getLanguage() {
        return LANGUAGE;
    }

    @Override
    public CodingStandardRule[] getCodingStandardRules() {
        return SwiftLintRule.values();
    }

    public enum SwiftLintRule implements CodingStandardRule {
        ATTRIBUTES("attributes", Implementability.IMPLEMENTABLE),
        BLOCK_BASED_KVO("block_based_kvo", Implementability.IMPLEMENTABLE),
        CLASS_DELEGATE_PROTOCOL("class_delegate_protocol", Implementability.IMPLEMENTABLE),
        CLOSING_BRACE("closing_brace", Implementability.IMPLEMENTABLE),
        CLOSURE_END_INDENTATION("closure_end_indentation", Implementability.IMPLEMENTABLE),
        CLOSURE_PARAMETER_POSITION("closure_parameter_position", Implementability.IMPLEMENTABLE),
        CLOSURE_SPACING("closure_spacing", Implementability.IMPLEMENTABLE),
        COLON("colon", Implementability.IMPLEMENTABLE),
        COMMA("comma", Implementability.IMPLEMENTABLE),
        COMPILER_PROTOCOL_INIT("compiler_protocol_init", Implementability.IMPLEMENTABLE),
        CONDITIONAL_RETURNS_ON_NEWLINE("conditional_returns_on_newline", Implementability.IMPLEMENTABLE),
        CONTROL_STATEMENT("control_statement", Implementability.IMPLEMENTABLE),
        CUSTOM_RULES("custom_rules", Implementability.IMPLEMENTABLE),
        CYCLOMATIC_COMPLEXITY("cyclomatic_complexity", Implementability.IMPLEMENTABLE),
        DISCARDED_NOTIFICATION_CENTER_OBSERVER("discarded_notification_center_observer", Implementability.IMPLEMENTABLE),
        DISCOURAGED_DIRECT_INIT("discouraged_direct_init", Implementability.IMPLEMENTABLE),
        DYNAMIC_INLINE("dynamic_inline", Implementability.IMPLEMENTABLE),
        EMPTY_COUNT("empty_count", Implementability.IMPLEMENTABLE),
        EMPTY_ENUM_ARGUMENTS("empty_enum_arguments", Implementability.IMPLEMENTABLE),
        EMPTY_PARAMETERS("empty_parameters", Implementability.IMPLEMENTABLE),
        EMPTY_PARENTHESES_WITH_TRAILING_CLOSURE("empty_parentheses_with_trailing_closure", Implementability.IMPLEMENTABLE),
        EXPLICIT_ENUM_RAW_VALUE("explicit_enum_raw_value", Implementability.IMPLEMENTABLE),
        EXPLICIT_INIT("explicit_init", Implementability.IMPLEMENTABLE),
        EXPLICIT_TOP_LEVEL_ACL("explicit_top_level_acl", Implementability.IMPLEMENTABLE),
        EXPLICIT_TYPE_INTERFACE("explicit_type_interface", Implementability.IMPLEMENTABLE),
        EXTENSION_ACCESS_MODIFIER("extension_access_modifier", Implementability.IMPLEMENTABLE),
        FATAL_ERROR_MESSAGE("fatal_error_message", Implementability.IMPLEMENTABLE),
        FILE_HEADER("file_header", Implementability.IMPLEMENTABLE),
        FILE_LENGTH("file_length", Implementability.IMPLEMENTABLE),
        FIRST_WHERE("first_where", Implementability.IMPLEMENTABLE),
        FOR_WHERE("for_where", Implementability.IMPLEMENTABLE),
        FORCE_CAST("force_cast", Implementability.IMPLEMENTABLE),
        FORCE_TRY("force_try", Implementability.IMPLEMENTABLE),
        FORCE_UNWRAPPING("force_unwrapping", Implementability.IMPLEMENTABLE),
        FUNCTION_BODY_LENGTH("function_body_length", Implementability.IMPLEMENTABLE),
        FUNCTION_PARAMETER_COUNT("function_parameter_count", Implementability.IMPLEMENTABLE),
        GENERIC_TYPE_NAME("generic_type_name", Implementability.IMPLEMENTABLE),
        IDENTIFIER_NAME("identifier_name", Implementability.IMPLEMENTABLE),
        IMPLICIT_GETTER("implicit_getter", Implementability.IMPLEMENTABLE),
        IMPLICIT_RETURN("implicit_return", Implementability.IMPLEMENTABLE),
        IMPLICITLY_UNWRAPPED_OPTIONAL("implicitly_unwrapped_optional", Implementability.IMPLEMENTABLE),
        IS_DISJOINT("is_disjoint", Implementability.IMPLEMENTABLE),
        JOINED_DEFAULT_PARAMETER("joined_default_parameter", Implementability.IMPLEMENTABLE),
        LARGE_TUPLE("large_tuple", Implementability.IMPLEMENTABLE),
        LEADING_WHITESPACE("leading_whitespace", Implementability.IMPLEMENTABLE),
        LEGACY_CGGEOMETRY_FUNCTIONS("legacy_cggeometry_functions", Implementability.IMPLEMENTABLE),
        LEGACY_CONSTANT("legacy_constant", Implementability.IMPLEMENTABLE),
        LEGACY_CONSTRUCTOR("legacy_constructor", Implementability.IMPLEMENTABLE),
        LEGACY_NSGEOMETRY_FUNCTIONS("legacy_nsgeometry_functions", Implementability.IMPLEMENTABLE),
        LET_VAR_WHITESPACE("let_var_whitespace", Implementability.IMPLEMENTABLE),
        LINE_LENGTH("line_length", Implementability.IMPLEMENTABLE),
        MARK("mark", Implementability.IMPLEMENTABLE),
        MULTILINE_PARAMETERS("multiline_parameters", Implementability.IMPLEMENTABLE),
        MULTIPLE_CLOSURES_WITH_TRAILING_CLOSURE("multiple_closures_with_trailing_closure", Implementability.IMPLEMENTABLE),
        NESTING("nesting", Implementability.IMPLEMENTABLE),
        NIMBLE_OPERATOR("nimble_operator", Implementability.IMPLEMENTABLE),
        NO_EXTENSION_ACCESS_MODIFIER("no_extension_access_modifier", Implementability.IMPLEMENTABLE),
        NO_GROUPING_EXTENSION("no_grouping_extension", Implementability.IMPLEMENTABLE),
        NOTIFICATION_CENTER_DETACHMENT("notification_center_detachment", Implementability.IMPLEMENTABLE),
        NUMBER_SEPARATOR("number_separator", Implementability.IMPLEMENTABLE),
        OBJECT_LITERAL("object_literal", Implementability.IMPLEMENTABLE),
        OPENING_BRACE("opening_brace", Implementability.IMPLEMENTABLE),
        OPERATOR_USAGE_WHITESPACE("operator_usage_whitespace", Implementability.IMPLEMENTABLE),
        OPERATOR_WHITESPACE("operator_whitespace", Implementability.IMPLEMENTABLE),
        OVERRIDDEN_SUPER_CALL("overridden_super_call", Implementability.IMPLEMENTABLE),
        PATTERN_MATCHING_KEYWORDS("pattern_matching_keywords", Implementability.IMPLEMENTABLE),
        PRIVATE_OUTLET("private_outlet", Implementability.IMPLEMENTABLE),
        PRIVATE_OVER_FILEPRIVATE("private_over_fileprivate", Implementability.IMPLEMENTABLE),
        PRIVATE_UNIT_TEST("private_unit_test", Implementability.IMPLEMENTABLE),
        PROHIBITED_SUPER_CALL("prohibited_super_call", Implementability.IMPLEMENTABLE),
        PROTOCOL_PROPERTY_ACCESSORS_ORDER("protocol_property_accessors_order", Implementability.IMPLEMENTABLE),
        QUICK_DISCOURAGED_CALL("quick_discouraged_call", Implementability.IMPLEMENTABLE),
        REDUNDANT_DISCARDABLE_LET("redundant_discardable_let", Implementability.IMPLEMENTABLE),
        REDUNDANT_NIL_COALESCING("redundant_nil_coalescing", Implementability.IMPLEMENTABLE),
        REDUNDANT_OPTIONAL_INITIALIZATION("redundant_optional_initialization", Implementability.IMPLEMENTABLE),
        REDUNDANT_STRING_ENUM_VALUE("redundant_string_enum_value", Implementability.IMPLEMENTABLE),
        REDUNDANT_VOID_RETURN("redundant_void_return", Implementability.IMPLEMENTABLE),
        RETURN_ARROW_WHITESPACE("return_arrow_whitespace", Implementability.IMPLEMENTABLE),
        SHORTHAND_OPERATOR("shorthand_operator", Implementability.IMPLEMENTABLE),
        SINGLE_TEST_CLASS("single_test_class", Implementability.IMPLEMENTABLE),
        SORTED_IMPORTS("sorted_imports", Implementability.IMPLEMENTABLE),
        STATEMENT_POSITION("statement_position", Implementability.IMPLEMENTABLE),
        STRICT_FILEPRIVATE("strict_fileprivate", Implementability.IMPLEMENTABLE),
        SUPERFLUOUS_DISABLE_COMMAND("superfluous_disable_command", Implementability.IMPLEMENTABLE),
        SWITCH_CASE_ON_NEWLINE("switch_case_on_newline", Implementability.IMPLEMENTABLE),
        SYNTACTIC_SUGAR("syntactic_sugar", Implementability.IMPLEMENTABLE),
        TODO("todo", Implementability.IMPLEMENTABLE),
        TRAILING_CLOSURE("trailing_closure", Implementability.IMPLEMENTABLE),
        TRAILING_COMMA("trailing_comma", Implementability.IMPLEMENTABLE),
        TRAILING_NEWLINE("trailing_newline", Implementability.IMPLEMENTABLE),
        TRAILING_SEMICOLON("trailing_semicolon", Implementability.IMPLEMENTABLE),
        TRAILING_WHITESPACE("trailing_whitespace", Implementability.IMPLEMENTABLE),
        TYPE_BODY_LENGTH("type_body_length", Implementability.IMPLEMENTABLE),
        TYPE_NAME("type_name", Implementability.IMPLEMENTABLE),
        UNNEEDED_PARENTHESES_IN_CLOSURE_ARGUMENT("unneeded_parentheses_in_closure_argument", Implementability.IMPLEMENTABLE),
        UNUSED_CLOSURE_PARAMETER("unused_closure_parameter", Implementability.IMPLEMENTABLE),
        UNUSED_ENUMERATED("unused_enumerated", Implementability.IMPLEMENTABLE),
        UNUSED_OPTIONAL_BINDING("unused_optional_binding", Implementability.IMPLEMENTABLE),
        VALID_IBINSPECTABLE("valid_ibinspectable", Implementability.IMPLEMENTABLE),
        VERTICAL_PARAMETER_ALIGNMENT("vertical_parameter_alignment", Implementability.IMPLEMENTABLE),
        VERTICAL_PARAMETER_ALIGNMENT_ON_CALL("vertical_parameter_alignment_on_call", Implementability.IMPLEMENTABLE),
        VERTICAL_WHITESPACE("vertical_whitespace", Implementability.IMPLEMENTABLE),
        VOID_RETURN("void_return", Implementability.IMPLEMENTABLE),
        WEAK_DELEGATE("weak_delegate", Implementability.IMPLEMENTABLE),
        XCTFAIL_MESSAGE("xctfail_message", Implementability.IMPLEMENTABLE);

        private String name;
        private Implementability implementability;

        SwiftLintRule(String name, Implementability implementability) {
            this.name = name;
            this.implementability = implementability;
        }

        @Override
        public Implementability getImplementability() {
            return this.implementability;
        }

        @Override
        public String getCodingStandardRuleId() {
            return name;
        }
    }

}
