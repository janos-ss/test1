/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.domain.RuleException;

import java.util.Iterator;
import java.util.List;


public class FindBugs extends AbstractReportableStandard implements ExternalTool {

  private String standardName = "FindBugs";
  private String rspecFieldName = "FindBugs";
  private Language language = Language.JAVA;

  protected int implementable = 0;
  protected int skipped = 0;
  protected int specified = 0;
  protected int implemented = 0;


  public enum StandardRule implements CodingStandardRule {
    AM_CREATES_EMPTY_JAR_FILE_ENTRY(Implementability.REJECTED),
    AM_CREATES_EMPTY_ZIP_FILE_ENTRY(Implementability.REJECTED),
    AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION(Implementability.IMPLEMENTABLE),
    BAC_BAD_APPLET_CONSTRUCTOR(Implementability.REJECTED),
    BC_BAD_CAST_TO_ABSTRACT_COLLECTION(Implementability.REJECTED),
    BC_BAD_CAST_TO_CONCRETE_COLLECTION(Implementability.REJECTED),
    BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS(Implementability.IMPLEMENTABLE),
    BC_IMPOSSIBLE_CAST(Implementability.IMPLEMENTABLE),
    BC_IMPOSSIBLE_DOWNCAST(Implementability.IMPLEMENTABLE),
    BC_IMPOSSIBLE_DOWNCAST_OF_TOARRAY(Implementability.IMPLEMENTABLE),
    BC_IMPOSSIBLE_INSTANCEOF(Implementability.IMPLEMENTABLE),
    BC_UNCONFIRMED_CAST(Implementability.IMPLEMENTABLE),
    BC_UNCONFIRMED_CAST_OF_RETURN_VALUE(Implementability.IMPLEMENTABLE),
    BC_VACUOUS_INSTANCEOF(Implementability.IMPLEMENTABLE),
    BIT_ADD_OF_SIGNED_BYTE(Implementability.IMPLEMENTABLE),
    BIT_AND(Implementability.IMPLEMENTABLE),
    BIT_AND_ZZ(Implementability.IMPLEMENTABLE),
    BIT_IOR(Implementability.IMPLEMENTABLE),
    BIT_IOR_OF_SIGNED_BYTE(Implementability.IMPLEMENTABLE),
    BIT_SIGNED_CHECK(Implementability.IMPLEMENTABLE),
    BIT_SIGNED_CHECK_HIGH_BIT(Implementability.IMPLEMENTABLE),
    BOA_BADLY_OVERRIDDEN_ADAPTER(Implementability.REJECTED),
    BX_BOXING_IMMEDIATELY_UNBOXED(Implementability.IMPLEMENTABLE),
    BX_BOXING_IMMEDIATELY_UNBOXED_TO_PERFORM_COERCION(Implementability.IMPLEMENTABLE),
    BX_UNBOXED_AND_COERCED_FOR_TERNARY_OPERATOR(Implementability.IMPLEMENTABLE),
    BX_UNBOXING_IMMEDIATELY_REBOXED(Implementability.IMPLEMENTABLE),
    CD_CIRCULAR_DEPENDENCY(Implementability.IMPLEMENTABLE),
    CI_CONFUSED_INHERITANCE(Implementability.IMPLEMENTABLE),
    CN_IDIOM(Implementability.IMPLEMENTABLE),
    CN_IDIOM_NO_SUPER_CALL(Implementability.IMPLEMENTABLE),
    CN_IMPLEMENTS_CLONE_BUT_NOT_CLONEABLE(Implementability.IMPLEMENTABLE),
    CNT_ROUGH_CONSTANT_VALUE(Implementability.IMPLEMENTABLE),
    CO_ABSTRACT_SELF(Implementability.REJECTED),
    CO_COMPARETO_RESULTS_MIN_VALUE(Implementability.IMPLEMENTABLE),
    CO_SELF_NO_OBJECT(Implementability.REJECTED),
    DB_DUPLICATE_BRANCHES(Implementability.IMPLEMENTABLE),
    DB_DUPLICATE_SWITCH_CLAUSES(Implementability.IMPLEMENTABLE),
    DC_DOUBLECHECK(Implementability.IMPLEMENTABLE),
    DE_MIGHT_DROP(Implementability.IMPLEMENTABLE),
    DE_MIGHT_IGNORE(Implementability.IMPLEMENTABLE),
    DL_SYNCHRONIZATION_ON_BOOLEAN(Implementability.IMPLEMENTABLE),
    DL_SYNCHRONIZATION_ON_BOXED_PRIMITIVE(Implementability.IMPLEMENTABLE),
    DL_SYNCHRONIZATION_ON_SHARED_CONSTANT(Implementability.IMPLEMENTABLE),
    DL_SYNCHRONIZATION_ON_UNSHARED_BOXED_PRIMITIVE(Implementability.IMPLEMENTABLE),
    DLS_DEAD_LOCAL_INCREMENT_IN_RETURN(Implementability.IMPLEMENTABLE),
    DLS_DEAD_LOCAL_STORE(Implementability.IMPLEMENTABLE),
    DLS_DEAD_LOCAL_STORE_IN_RETURN(Implementability.IMPLEMENTABLE),
    DLS_DEAD_LOCAL_STORE_OF_NULL(Implementability.IMPLEMENTABLE),
    DLS_DEAD_LOCAL_STORE_SHADOWS_FIELD(Implementability.IMPLEMENTABLE),
    DLS_DEAD_STORE_OF_CLASS_LITERAL(Implementability.IMPLEMENTABLE),
    DLS_OVERWRITTEN_INCREMENT(Implementability.IMPLEMENTABLE),
    DM_BOOLEAN_CTOR(Implementability.IMPLEMENTABLE),
    DM_BOXED_PRIMITIVE_FOR_PARSING(Implementability.IMPLEMENTABLE),
    DM_BOXED_PRIMITIVE_TOSTRING(Implementability.IMPLEMENTABLE),
    DM_CONVERT_CASE(Implementability.IMPLEMENTABLE),
    DM_DEFAULT_ENCODING(Implementability.IMPLEMENTABLE),
    DM_EXIT(Implementability.IMPLEMENTABLE),
    DM_FP_NUMBER_CTOR(Implementability.IMPLEMENTABLE),
    DM_GC(Implementability.IMPLEMENTABLE),
    DM_MONITOR_WAIT_ON_CONDITION(Implementability.IMPLEMENTABLE),
    DM_NEW_FOR_GETCLASS(Implementability.IMPLEMENTABLE),
    DM_NEXTINT_VIA_NEXTDOUBLE(Implementability.IMPLEMENTABLE),
    DM_NUMBER_CTOR(Implementability.IMPLEMENTABLE),
    DM_RUN_FINALIZERS_ON_EXIT(Implementability.IMPLEMENTABLE),
    DM_STRING_CTOR(Implementability.IMPLEMENTABLE),
    DM_STRING_TOSTRING(Implementability.IMPLEMENTABLE),
    DM_STRING_VOID_CTOR(Implementability.IMPLEMENTABLE),
    DM_USELESS_THREAD(Implementability.IMPLEMENTABLE),
    DMI_ANNOTATION_IS_NOT_VISIBLE_TO_REFLECTION(Implementability.IMPLEMENTABLE),
    DMI_ARGUMENTS_WRONG_ORDER(Implementability.REJECTED),
    DMI_BAD_MONTH(Implementability.IMPLEMENTABLE),
    DMI_BIGDECIMAL_CONSTRUCTED_FROM_DOUBLE(Implementability.IMPLEMENTABLE),
    DMI_BLOCKING_METHODS_ON_URL(Implementability.IMPLEMENTABLE),
    DMI_CALLING_NEXT_FROM_HASNEXT(Implementability.IMPLEMENTABLE),
    DMI_COLLECTION_OF_URLS(Implementability.IMPLEMENTABLE),
    DMI_COLLECTIONS_SHOULD_NOT_CONTAIN_THEMSELVES(Implementability.IMPLEMENTABLE),
    DMI_CONSTANT_DB_PASSWORD(Implementability.IMPLEMENTABLE),
    DMI_DOH(Implementability.REJECTED),
    DMI_EMPTY_DB_PASSWORD(Implementability.IMPLEMENTABLE),
    DMI_ENTRY_SETS_MAY_REUSE_ENTRY_OBJECTS(Implementability.REJECTED),
    DMI_FUTILE_ATTEMPT_TO_CHANGE_MAXPOOL_SIZE_OF_SCHEDULED_THREAD_POOL_EXECUTOR(Implementability.REJECTED),
    DMI_HARDCODED_ABSOLUTE_FILENAME(Implementability.IMPLEMENTABLE),
    DMI_INVOKING_HASHCODE_ON_ARRAY(Implementability.IMPLEMENTABLE),
    DMI_INVOKING_TOSTRING_ON_ANONYMOUS_ARRAY(Implementability.IMPLEMENTABLE),
    DMI_INVOKING_TOSTRING_ON_ARRAY(Implementability.IMPLEMENTABLE),
    DMI_LONG_BITS_TO_DOUBLE_INVOKED_ON_INT(Implementability.IMPLEMENTABLE),
    DMI_NONSERIALIZABLE_OBJECT_WRITTEN(Implementability.IMPLEMENTABLE),
    DMI_RANDOM_USED_ONLY_ONCE(Implementability.IMPLEMENTABLE),
    DMI_SCHEDULED_THREAD_POOL_EXECUTOR_WITH_ZERO_CORE_THREADS(Implementability.IMPLEMENTABLE),
    DMI_THREAD_PASSED_WHERE_RUNNABLE_EXPECTED(Implementability.IMPLEMENTABLE),
    DMI_UNSUPPORTED_METHOD(Implementability.IMPLEMENTABLE),
    DMI_USELESS_SUBSTRING(Implementability.IMPLEMENTABLE),
    DMI_USING_REMOVEALL_TO_CLEAR_COLLECTION(Implementability.IMPLEMENTABLE),
    DMI_VACUOUS_SELF_COLLECTION_CALL(Implementability.IMPLEMENTABLE),
    DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED(Implementability.REJECTED),
    DP_DO_INSIDE_DO_PRIVILEGED(Implementability.REJECTED),
    EC_ARRAY_AND_NONARRAY(Implementability.IMPLEMENTABLE),
    EC_BAD_ARRAY_COMPARE(Implementability.IMPLEMENTABLE),
    EC_INCOMPATIBLE_ARRAY_COMPARE(Implementability.IMPLEMENTABLE),
    EC_NULL_ARG(Implementability.IMPLEMENTABLE),
    EC_UNRELATED_CLASS_AND_INTERFACE(Implementability.IMPLEMENTABLE),
    EC_UNRELATED_INTERFACES(Implementability.IMPLEMENTABLE),
    EC_UNRELATED_TYPES(Implementability.IMPLEMENTABLE),
    EC_UNRELATED_TYPES_USING_POINTER_EQUALITY(Implementability.IMPLEMENTABLE),
    EI_EXPOSE_REP(Implementability.IMPLEMENTABLE),
    EI_EXPOSE_REP2(Implementability.IMPLEMENTABLE),
    EI_EXPOSE_STATIC_REP2(Implementability.IMPLEMENTABLE),
    EQ_ABSTRACT_SELF(Implementability.IMPLEMENTABLE),
    EQ_ALWAYS_FALSE(Implementability.IMPLEMENTABLE),
    EQ_ALWAYS_TRUE(Implementability.IMPLEMENTABLE),
    EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS(Implementability.IMPLEMENTABLE),
    EQ_COMPARETO_USE_OBJECT_EQUALS(Implementability.IMPLEMENTABLE),
    EQ_COMPARING_CLASS_NAMES(Implementability.IMPLEMENTABLE),
    EQ_DOESNT_OVERRIDE_EQUALS(Implementability.IMPLEMENTABLE),
    EQ_DONT_DEFINE_EQUALS_FOR_ENUM(Implementability.IMPLEMENTABLE),
    EQ_GETCLASS_AND_CLASS_CONSTANT(Implementability.IMPLEMENTABLE),
    EQ_OTHER_NO_OBJECT(Implementability.IMPLEMENTABLE),
    EQ_OTHER_USE_OBJECT(Implementability.IMPLEMENTABLE),
    EQ_OVERRIDING_EQUALS_NOT_SYMMETRIC(Implementability.IMPLEMENTABLE),
    EQ_SELF_NO_OBJECT(Implementability.IMPLEMENTABLE),
    EQ_SELF_USE_OBJECT(Implementability.IMPLEMENTABLE),
    EQ_UNUSUAL(Implementability.REJECTED),
    ES_COMPARING_PARAMETER_STRING_WITH_EQ(Implementability.IMPLEMENTABLE),
    ES_COMPARING_STRINGS_WITH_EQ(Implementability.IMPLEMENTABLE),
    ESync_EMPTY_SYNC(Implementability.IMPLEMENTABLE),
    FB_MISSING_EXPECTED_WARNING(Implementability.REJECTED),
    FB_UNEXPECTED_WARNING(Implementability.REJECTED),
    FE_FLOATING_POINT_EQUALITY(Implementability.IMPLEMENTABLE),
    FE_TEST_IF_EQUAL_TO_NOT_A_NUMBER(Implementability.IMPLEMENTABLE),
    FI_EMPTY(Implementability.IMPLEMENTABLE),
    FI_EXPLICIT_INVOCATION(Implementability.IMPLEMENTABLE),
    FI_FINALIZER_NULLS_FIELDS(Implementability.IMPLEMENTABLE),
    FI_FINALIZER_ONLY_NULLS_FIELDS(Implementability.IMPLEMENTABLE),
    FI_MISSING_SUPER_CALL(Implementability.IMPLEMENTABLE),
    FI_NULLIFY_SUPER(Implementability.IMPLEMENTABLE),
    FI_PUBLIC_SHOULD_BE_PROTECTED(Implementability.IMPLEMENTABLE),
    FI_USELESS(Implementability.IMPLEMENTABLE),
    FL_MATH_USING_FLOAT_PRECISION(Implementability.IMPLEMENTABLE),
    GC_UNCHECKED_TYPE_IN_GENERIC_CALL(Implementability.IMPLEMENTABLE),
    GC_UNRELATED_TYPES(Implementability.IMPLEMENTABLE),
    HE_EQUALS_NO_HASHCODE(Implementability.IMPLEMENTABLE),
    HE_EQUALS_USE_HASHCODE(Implementability.IMPLEMENTABLE),
    HE_HASHCODE_NO_EQUALS(Implementability.IMPLEMENTABLE),
    HE_HASHCODE_USE_OBJECT_EQUALS(Implementability.IMPLEMENTABLE),
    HE_INHERITS_EQUALS_USE_HASHCODE(Implementability.REJECTED),
    HE_SIGNATURE_DECLARES_HASHING_OF_UNHASHABLE_CLASS(Implementability.IMPLEMENTABLE),
    HE_USE_OF_UNHASHABLE_CLASS(Implementability.IMPLEMENTABLE),
    HRS_REQUEST_PARAMETER_TO_COOKIE(Implementability.IMPLEMENTABLE),
    HRS_REQUEST_PARAMETER_TO_HTTP_HEADER(Implementability.IMPLEMENTABLE),
    HSC_HUGE_SHARED_STRING_CONSTANT(Implementability.IMPLEMENTABLE),
    IA_AMBIGUOUS_INVOCATION_OF_INHERITED_OR_OUTER_METHOD(Implementability.IMPLEMENTABLE),
    IC_INIT_CIRCULARITY(Implementability.IMPLEMENTABLE),
    IC_SUPERCLASS_USES_SUBCLASS_DURING_INITIALIZATION(Implementability.IMPLEMENTABLE),
    ICAST_BAD_SHIFT_AMOUNT(Implementability.IMPLEMENTABLE),
    ICAST_IDIV_CAST_TO_DOUBLE(Implementability.IMPLEMENTABLE),
    ICAST_INT_2_LONG_AS_INSTANT(Implementability.IMPLEMENTABLE),
    ICAST_INT_CAST_TO_DOUBLE_PASSED_TO_CEIL(Implementability.IMPLEMENTABLE),
    ICAST_INT_CAST_TO_FLOAT_PASSED_TO_ROUND(Implementability.IMPLEMENTABLE),
    ICAST_INTEGER_MULTIPLY_CAST_TO_LONG(Implementability.IMPLEMENTABLE),
    ICAST_QUESTIONABLE_UNSIGNED_RIGHT_SHIFT(Implementability.IMPLEMENTABLE),
    IIO_INEFFICIENT_INDEX_OF(Implementability.IMPLEMENTABLE),
    IIO_INEFFICIENT_LAST_INDEX_OF(Implementability.IMPLEMENTABLE),
    IJU_ASSERT_METHOD_INVOKED_FROM_RUN_METHOD(Implementability.IMPLEMENTABLE),
    IJU_BAD_SUITE_METHOD(Implementability.IMPLEMENTABLE),
    IJU_NO_TESTS(Implementability.IMPLEMENTABLE),
    IJU_SETUP_NO_SUPER(Implementability.IMPLEMENTABLE),
    IJU_SUITE_NOT_STATIC(Implementability.IMPLEMENTABLE),
    IJU_TEARDOWN_NO_SUPER(Implementability.IMPLEMENTABLE),
    IL_CONTAINER_ADDED_TO_ITSELF(Implementability.IMPLEMENTABLE),
    IL_INFINITE_LOOP(Implementability.IMPLEMENTABLE),
    IL_INFINITE_RECURSIVE_LOOP(Implementability.IMPLEMENTABLE),
    IM_AVERAGE_COMPUTATION_COULD_OVERFLOW(Implementability.REJECTED),
    IM_BAD_CHECK_FOR_ODD(Implementability.IMPLEMENTABLE),
    IM_MULTIPLYING_RESULT_OF_IREM(Implementability.IMPLEMENTABLE),
    IMA_INEFFICIENT_MEMBER_ACCESS(Implementability.REJECTED),
    IMSE_DONT_CATCH_IMSE(Implementability.IMPLEMENTABLE),
    INT_BAD_COMPARISON_WITH_INT_VALUE(Implementability.IMPLEMENTABLE),
    INT_BAD_COMPARISON_WITH_NONNEGATIVE_VALUE(Implementability.IMPLEMENTABLE),
    INT_BAD_COMPARISON_WITH_SIGNED_BYTE(Implementability.IMPLEMENTABLE),
    INT_BAD_REM_BY_1(Implementability.IMPLEMENTABLE),
    INT_VACUOUS_BIT_OPERATION(Implementability.IMPLEMENTABLE),
    INT_VACUOUS_COMPARISON(Implementability.IMPLEMENTABLE),
    IO_APPENDING_TO_OBJECT_OUTPUT_STREAM(Implementability.IMPLEMENTABLE),
    IP_PARAMETER_IS_DEAD_BUT_OVERWRITTEN(Implementability.IMPLEMENTABLE),
    IS_FIELD_NOT_GUARDED(Implementability.IMPLEMENTABLE),
    IS_INCONSISTENT_SYNC(Implementability.IMPLEMENTABLE),
    IS2_INCONSISTENT_SYNC(Implementability.IMPLEMENTABLE),
    ISC_INSTANTIATE_STATIC_CLASS(Implementability.IMPLEMENTABLE),
    IT_NO_SUCH_ELEMENT(Implementability.IMPLEMENTABLE),
    ITA_INEFFICIENT_TO_ARRAY(Implementability.IMPLEMENTABLE),
    J2EE_STORE_OF_NON_SERIALIZABLE_OBJECT_INTO_SESSION(Implementability.IMPLEMENTABLE),
    JCIP_FIELD_ISNT_FINAL_IN_IMMUTABLE_CLASS(Implementability.IMPLEMENTABLE),
    JLM_JSR166_LOCK_MONITORENTER(Implementability.IMPLEMENTABLE),
    JML_JSR166_CALLING_WAIT_RATHER_THAN_AWAIT(Implementability.IMPLEMENTABLE),
    LG_LOST_LOGGER_DUE_TO_WEAK_REFERENCE(Implementability.IMPLEMENTABLE),
    LI_LAZY_INIT_STATIC(Implementability.IMPLEMENTABLE),
    LI_LAZY_INIT_UPDATE_STATIC(Implementability.IMPLEMENTABLE),
    MF_CLASS_MASKS_FIELD(Implementability.IMPLEMENTABLE),
    MF_METHOD_MASKS_FIELD(Implementability.IMPLEMENTABLE),
    ML_SYNC_ON_FIELD_TO_GUARD_CHANGING_THAT_FIELD(Implementability.IMPLEMENTABLE),
    ML_SYNC_ON_UPDATED_FIELD(Implementability.IMPLEMENTABLE),
    MS_CANNOT_BE_FINAL(Implementability.IMPLEMENTABLE),
    MS_EXPOSE_REP(Implementability.IMPLEMENTABLE),
    MS_FINAL_PKGPROTECT(Implementability.IMPLEMENTABLE),
    MS_MUTABLE_ARRAY(Implementability.IMPLEMENTABLE),
    MS_MUTABLE_HASHTABLE(Implementability.IMPLEMENTABLE),
    MS_OOI_PKGPROTECT(Implementability.IMPLEMENTABLE),
    MS_PKGPROTECT(Implementability.IMPLEMENTABLE),
    MS_SHOULD_BE_FINAL(Implementability.IMPLEMENTABLE),
    MS_SHOULD_BE_REFACTORED_TO_BE_FINAL(Implementability.IMPLEMENTABLE),
    MSF_MUTABLE_SERVLET_FIELD(Implementability.IMPLEMENTABLE),
    MTIA_SUSPECT_SERVLET_INSTANCE_FIELD(Implementability.IMPLEMENTABLE),
    MTIA_SUSPECT_STRUTS_INSTANCE_FIELD(Implementability.IMPLEMENTABLE),
    MWN_MISMATCHED_NOTIFY(Implementability.IMPLEMENTABLE),
    MWN_MISMATCHED_WAIT(Implementability.IMPLEMENTABLE),
    NM_BAD_EQUAL(Implementability.IMPLEMENTABLE),
    NM_CLASS_NAMING_CONVENTION(Implementability.IMPLEMENTABLE),
    NM_CLASS_NOT_EXCEPTION(Implementability.IMPLEMENTABLE),
    NM_CONFUSING(Implementability.IMPLEMENTABLE),
    NM_FIELD_NAMING_CONVENTION(Implementability.IMPLEMENTABLE),
    NM_FUTURE_KEYWORD_USED_AS_IDENTIFIER(Implementability.IMPLEMENTABLE),
    NM_FUTURE_KEYWORD_USED_AS_MEMBER_IDENTIFIER(Implementability.IMPLEMENTABLE),
    NM_LCASE_HASHCODE(Implementability.IMPLEMENTABLE),
    NM_LCASE_TOSTRING(Implementability.IMPLEMENTABLE),
    NM_METHOD_CONSTRUCTOR_CONFUSION(Implementability.IMPLEMENTABLE),
    NM_METHOD_NAMING_CONVENTION(Implementability.IMPLEMENTABLE),
    NM_SAME_SIMPLE_NAME_AS_INTERFACE(Implementability.IMPLEMENTABLE),
    NM_SAME_SIMPLE_NAME_AS_SUPERCLASS(Implementability.IMPLEMENTABLE),
    NM_VERY_CONFUSING(Implementability.IMPLEMENTABLE),
    NM_VERY_CONFUSING_INTENTIONAL(Implementability.IMPLEMENTABLE),
    NM_WRONG_PACKAGE(Implementability.IMPLEMENTABLE),
    NM_WRONG_PACKAGE_INTENTIONAL(Implementability.IMPLEMENTABLE),
    NN_NAKED_NOTIFY(Implementability.IMPLEMENTABLE),
    NO_NOTIFY_NOT_NOTIFYALL(Implementability.IMPLEMENTABLE),
    NP_ALWAYS_NULL(Implementability.IMPLEMENTABLE),
    NP_ALWAYS_NULL_EXCEPTION(Implementability.IMPLEMENTABLE),
    NP_ARGUMENT_MIGHT_BE_NULL(Implementability.IMPLEMENTABLE),
    NP_BOOLEAN_RETURN_NULL(Implementability.IMPLEMENTABLE),
    NP_CLONE_COULD_RETURN_NULL(Implementability.IMPLEMENTABLE),
    NP_CLOSING_NULL(Implementability.IMPLEMENTABLE),
    NP_DEREFERENCE_OF_READLINE_VALUE(Implementability.IMPLEMENTABLE),
    NP_EQUALS_SHOULD_HANDLE_NULL_ARGUMENT(Implementability.IMPLEMENTABLE),
    NP_GUARANTEED_DEREF(Implementability.IMPLEMENTABLE),
    NP_GUARANTEED_DEREF_ON_EXCEPTION_PATH(Implementability.IMPLEMENTABLE),
    NP_IMMEDIATE_DEREFERENCE_OF_READLINE(Implementability.IMPLEMENTABLE),
    NP_LOAD_OF_KNOWN_NULL_VALUE(Implementability.IMPLEMENTABLE),
    NP_METHOD_PARAMETER_TIGHTENS_ANNOTATION(Implementability.IMPLEMENTABLE),
    NP_METHOD_RETURN_RELAXING_ANNOTATION(Implementability.IMPLEMENTABLE),
    NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR(Implementability.IMPLEMENTABLE),
    NP_NONNULL_PARAM_VIOLATION(Implementability.IMPLEMENTABLE),
    NP_NONNULL_RETURN_VIOLATION(Implementability.IMPLEMENTABLE),
    NP_NULL_INSTANCEOF(Implementability.IMPLEMENTABLE),
    NP_NULL_ON_SOME_PATH(Implementability.IMPLEMENTABLE),
    NP_NULL_ON_SOME_PATH_EXCEPTION(Implementability.IMPLEMENTABLE),
    NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE(Implementability.IMPLEMENTABLE),
    NP_NULL_ON_SOME_PATH_MIGHT_BE_INFEASIBLE(Implementability.IMPLEMENTABLE),
    NP_NULL_PARAM_DEREF(Implementability.IMPLEMENTABLE),
    NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS(Implementability.IMPLEMENTABLE),
    NP_NULL_PARAM_DEREF_NONVIRTUAL(Implementability.IMPLEMENTABLE),
    NP_OPTIONAL_RETURN_NULL(Implementability.IMPLEMENTABLE),
    NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE(Implementability.IMPLEMENTABLE),
    NP_STORE_INTO_NONNULL_FIELD(Implementability.IMPLEMENTABLE),
    NP_SYNC_AND_NULL_CHECK_FIELD(Implementability.IMPLEMENTABLE),
    NP_TOSTRING_COULD_RETURN_NULL(Implementability.IMPLEMENTABLE),
    NP_UNWRITTEN_FIELD(Implementability.IMPLEMENTABLE),
    NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD(Implementability.IMPLEMENTABLE),
    NS_DANGEROUS_NON_SHORT_CIRCUIT(Implementability.IMPLEMENTABLE),
    NS_NON_SHORT_CIRCUIT(Implementability.IMPLEMENTABLE),
    OBL_UNSATISFIED_OBLIGATION(Implementability.IMPLEMENTABLE),
    OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE(Implementability.IMPLEMENTABLE),
    ODR_OPEN_DATABASE_RESOURCE(Implementability.IMPLEMENTABLE),
    ODR_OPEN_DATABASE_RESOURCE_EXCEPTION_PATH(Implementability.IMPLEMENTABLE),
    OS_OPEN_STREAM(Implementability.IMPLEMENTABLE),
    OS_OPEN_STREAM_EXCEPTION_PATH(Implementability.IMPLEMENTABLE),
    PS_PUBLIC_SEMAPHORES(Implementability.REJECTED),
    PT_ABSOLUTE_PATH_TRAVERSAL(Implementability.IMPLEMENTABLE),
    PT_RELATIVE_PATH_TRAVERSAL(Implementability.IMPLEMENTABLE),
    PZ_DONT_REUSE_ENTRY_OBJECTS_IN_ITERATORS(Implementability.REJECTED),
    PZLA_PREFER_ZERO_LENGTH_ARRAYS(Implementability.IMPLEMENTABLE),
    QBA_QUESTIONABLE_BOOLEAN_ASSIGNMENT(Implementability.IMPLEMENTABLE),
    QF_QUESTIONABLE_FOR_LOOP(Implementability.IMPLEMENTABLE),
    RC_REF_COMPARISON(Implementability.IMPLEMENTABLE),
    RC_REF_COMPARISON_BAD_PRACTICE(Implementability.IMPLEMENTABLE),
    RC_REF_COMPARISON_BAD_PRACTICE_BOOLEAN(Implementability.IMPLEMENTABLE),
    RCN_REDUNDANT_COMPARISON_OF_NULL_AND_NONNULL_VALUE(Implementability.IMPLEMENTABLE),
    RCN_REDUNDANT_COMPARISON_TWO_NULL_VALUES(Implementability.IMPLEMENTABLE),
    RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE(Implementability.IMPLEMENTABLE),
    RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE(Implementability.IMPLEMENTABLE),
    RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE(Implementability.IMPLEMENTABLE),
    RE_BAD_SYNTAX_FOR_REGULAR_EXPRESSION(Implementability.IMPLEMENTABLE),
    RE_CANT_USE_FILE_SEPARATOR_AS_REGULAR_EXPRESSION(Implementability.IMPLEMENTABLE),
    RE_POSSIBLE_UNINTENDED_PATTERN(Implementability.IMPLEMENTABLE),
    REC_CATCH_EXCEPTION(Implementability.IMPLEMENTABLE),
    RI_REDUNDANT_INTERFACES(Implementability.IMPLEMENTABLE),
    RpC_REPEATED_CONDITIONAL_TEST(Implementability.IMPLEMENTABLE),
    RR_NOT_CHECKED(Implementability.IMPLEMENTABLE),
    RS_READOBJECT_SYNC(Implementability.IMPLEMENTABLE),
    RU_INVOKE_RUN(Implementability.IMPLEMENTABLE),
    RV_01_TO_INT(Implementability.IMPLEMENTABLE),
    RV_ABSOLUTE_VALUE_OF_HASHCODE(Implementability.IMPLEMENTABLE),
    RV_ABSOLUTE_VALUE_OF_RANDOM_INT(Implementability.IMPLEMENTABLE),
    RV_CHECK_COMPARETO_FOR_SPECIFIC_RETURN_VALUE(Implementability.IMPLEMENTABLE),
    RV_CHECK_FOR_POSITIVE_INDEXOF(Implementability.IMPLEMENTABLE),
    RV_DONT_JUST_NULL_CHECK_READLINE(Implementability.IMPLEMENTABLE),
    RV_EXCEPTION_NOT_THROWN(Implementability.IMPLEMENTABLE),
    RV_NEGATING_RESULT_OF_COMPARETO(Implementability.IMPLEMENTABLE),
    RV_REM_OF_HASHCODE(Implementability.IMPLEMENTABLE),
    RV_REM_OF_RANDOM_INT(Implementability.IMPLEMENTABLE),
    RV_RETURN_VALUE_IGNORED(Implementability.IMPLEMENTABLE),
    RV_RETURN_VALUE_IGNORED_BAD_PRACTICE(Implementability.IMPLEMENTABLE),
    RV_RETURN_VALUE_IGNORED_INFERRED(Implementability.IMPLEMENTABLE),
    RV_RETURN_VALUE_IGNORED2(Implementability.IMPLEMENTABLE),
    RV_RETURN_VALUE_OF_PUTIFABSENT_IGNORED(Implementability.IMPLEMENTABLE),
    SA_FIELD_DOUBLE_ASSIGNMENT(Implementability.IMPLEMENTABLE),
    SA_FIELD_SELF_ASSIGNMENT(Implementability.IMPLEMENTABLE),
    SA_FIELD_SELF_COMPARISON(Implementability.IMPLEMENTABLE),
    SA_FIELD_SELF_COMPUTATION(Implementability.IMPLEMENTABLE),
    SA_LOCAL_DOUBLE_ASSIGNMENT(Implementability.IMPLEMENTABLE),
    SA_LOCAL_SELF_ASSIGNMENT(Implementability.IMPLEMENTABLE),
    SA_LOCAL_SELF_ASSIGNMENT_INSTEAD_OF_FIELD(Implementability.IMPLEMENTABLE),
    SA_LOCAL_SELF_COMPARISON(Implementability.IMPLEMENTABLE),
    SA_LOCAL_SELF_COMPUTATION(Implementability.IMPLEMENTABLE),
    SBSC_USE_STRINGBUFFER_CONCATENATION(Implementability.IMPLEMENTABLE),
    SC_START_IN_CTOR(Implementability.IMPLEMENTABLE),
    SE_BAD_FIELD(Implementability.IMPLEMENTABLE),
    SE_BAD_FIELD_INNER_CLASS(Implementability.IMPLEMENTABLE),
    SE_BAD_FIELD_STORE(Implementability.REJECTED),
    SE_COMPARATOR_SHOULD_BE_SERIALIZABLE(Implementability.IMPLEMENTABLE),
    SE_INNER_CLASS(Implementability.IMPLEMENTABLE),
    SE_METHOD_MUST_BE_PRIVATE(Implementability.IMPLEMENTABLE),
    SE_NO_SERIALVERSIONID(Implementability.IMPLEMENTABLE),
    SE_NO_SUITABLE_CONSTRUCTOR(Implementability.IMPLEMENTABLE),
    SE_NO_SUITABLE_CONSTRUCTOR_FOR_EXTERNALIZATION(Implementability.IMPLEMENTABLE),
    SE_NONFINAL_SERIALVERSIONID(Implementability.IMPLEMENTABLE),
    SE_NONLONG_SERIALVERSIONID(Implementability.IMPLEMENTABLE),
    SE_NONSTATIC_SERIALVERSIONID(Implementability.IMPLEMENTABLE),
    SE_PRIVATE_READ_RESOLVE_NOT_INHERITED(Implementability.IMPLEMENTABLE),
    SE_READ_RESOLVE_IS_STATIC(Implementability.IMPLEMENTABLE),
    SE_READ_RESOLVE_MUST_RETURN_OBJECT(Implementability.IMPLEMENTABLE),
    SE_TRANSIENT_FIELD_NOT_RESTORED(Implementability.IMPLEMENTABLE),
    SE_TRANSIENT_FIELD_OF_NONSERIALIZABLE_CLASS(Implementability.IMPLEMENTABLE),
    SF_DEAD_STORE_DUE_TO_SWITCH_FALLTHROUGH(Implementability.IMPLEMENTABLE),
    SF_DEAD_STORE_DUE_TO_SWITCH_FALLTHROUGH_TO_THROW(Implementability.IMPLEMENTABLE),
    SF_SWITCH_FALLTHROUGH(Implementability.IMPLEMENTABLE),
    SF_SWITCH_NO_DEFAULT(Implementability.IMPLEMENTABLE),
    SI_INSTANCE_BEFORE_FINALS_ASSIGNED(Implementability.IMPLEMENTABLE),
    SIC_INNER_SHOULD_BE_STATIC(Implementability.IMPLEMENTABLE),
    SIC_INNER_SHOULD_BE_STATIC_ANON(Implementability.IMPLEMENTABLE),
    SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS(Implementability.IMPLEMENTABLE),
    SIC_THREADLOCAL_DEADLY_EMBRACE(Implementability.IMPLEMENTABLE),
    SIO_SUPERFLUOUS_INSTANCEOF(Implementability.IMPLEMENTABLE),
    SKIPPED_CLASS_TOO_BIG(Implementability.REJECTED),
    SP_SPIN_ON_FIELD(Implementability.IMPLEMENTABLE),
    SQL_BAD_PREPARED_STATEMENT_ACCESS(Implementability.IMPLEMENTABLE),
    SQL_BAD_RESULTSET_ACCESS(Implementability.IMPLEMENTABLE),
    SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE(Implementability.IMPLEMENTABLE),
    SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING(Implementability.IMPLEMENTABLE),
    SR_NOT_CHECKED(Implementability.IMPLEMENTABLE),
    SS_SHOULD_BE_STATIC(Implementability.IMPLEMENTABLE),
    ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD(Implementability.IMPLEMENTABLE),
    STCAL_INVOKE_ON_STATIC_CALENDAR_INSTANCE(Implementability.IMPLEMENTABLE),
    STCAL_INVOKE_ON_STATIC_DATE_FORMAT_INSTANCE(Implementability.IMPLEMENTABLE),
    STCAL_STATIC_CALENDAR_INSTANCE(Implementability.IMPLEMENTABLE),
    STCAL_STATIC_SIMPLE_DATE_FORMAT_INSTANCE(Implementability.IMPLEMENTABLE),
    STI_INTERRUPTED_ON_CURRENTTHREAD(Implementability.IMPLEMENTABLE),
    STI_INTERRUPTED_ON_UNKNOWNTHREAD(Implementability.IMPLEMENTABLE),
    SW_SWING_METHODS_INVOKED_IN_SWING_THREAD(Implementability.IMPLEMENTABLE),
    SWL_SLEEP_WITH_LOCK_HELD(Implementability.IMPLEMENTABLE),
    TLW_TWO_LOCK_WAIT(Implementability.IMPLEMENTABLE),
    TQ_ALWAYS_VALUE_USED_WHERE_NEVER_REQUIRED(Implementability.IMPLEMENTABLE),
    TQ_COMPARING_VALUES_WITH_INCOMPATIBLE_TYPE_QUALIFIERS(Implementability.IMPLEMENTABLE),
    TQ_EXPLICIT_UNKNOWN_SOURCE_VALUE_REACHES_ALWAYS_SINK(Implementability.IMPLEMENTABLE),
    TQ_EXPLICIT_UNKNOWN_SOURCE_VALUE_REACHES_NEVER_SINK(Implementability.IMPLEMENTABLE),
    TQ_MAYBE_SOURCE_VALUE_REACHES_ALWAYS_SINK(Implementability.IMPLEMENTABLE),
    TQ_MAYBE_SOURCE_VALUE_REACHES_NEVER_SINK(Implementability.IMPLEMENTABLE),
    TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED(Implementability.IMPLEMENTABLE),
    TQ_UNKNOWN_VALUE_USED_WHERE_ALWAYS_STRICTLY_REQUIRED(Implementability.IMPLEMENTABLE),
    UCF_USELESS_CONTROL_FLOW(Implementability.IMPLEMENTABLE),
    UCF_USELESS_CONTROL_FLOW_NEXT_LINE(Implementability.IMPLEMENTABLE),
    UG_SYNC_SET_UNSYNC_GET(Implementability.IMPLEMENTABLE),
    UI_INHERITANCE_UNSAFE_GETRESOURCE(Implementability.IMPLEMENTABLE),
    UL_UNRELEASED_LOCK(Implementability.IMPLEMENTABLE),
    UL_UNRELEASED_LOCK_EXCEPTION_PATH(Implementability.IMPLEMENTABLE),
    UM_UNNECESSARY_MATH(Implementability.IMPLEMENTABLE),
    UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS(Implementability.IMPLEMENTABLE),
    UOE_USE_OBJECT_EQUALS(Implementability.IMPLEMENTABLE),
    UPM_UNCALLED_PRIVATE_METHOD(Implementability.IMPLEMENTABLE),
    UR_UNINIT_READ(Implementability.IMPLEMENTABLE),
    UR_UNINIT_READ_CALLED_FROM_SUPER_CONSTRUCTOR(Implementability.IMPLEMENTABLE),
    URF_UNREAD_FIELD(Implementability.IMPLEMENTABLE),
    URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD(Implementability.REJECTED),
    USM_USELESS_ABSTRACT_METHOD(Implementability.IMPLEMENTABLE),
    USM_USELESS_SUBCLASS_METHOD(Implementability.IMPLEMENTABLE),
    UUF_UNUSED_FIELD(Implementability.IMPLEMENTABLE),
    UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD(Implementability.REJECTED),
    UW_UNCOND_WAIT(Implementability.IMPLEMENTABLE),
    UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR(Implementability.IMPLEMENTABLE),
    UWF_NULL_FIELD(Implementability.IMPLEMENTABLE),
    UWF_UNWRITTEN_FIELD(Implementability.IMPLEMENTABLE),
    UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD(Implementability.IMPLEMENTABLE),
    VA_FORMAT_STRING_ARG_MISMATCH(Implementability.IMPLEMENTABLE),
    VA_FORMAT_STRING_BAD_ARGUMENT(Implementability.IMPLEMENTABLE),
    VA_FORMAT_STRING_BAD_CONVERSION(Implementability.IMPLEMENTABLE),
    VA_FORMAT_STRING_BAD_CONVERSION_FROM_ARRAY(Implementability.IMPLEMENTABLE),
    VA_FORMAT_STRING_BAD_CONVERSION_TO_BOOLEAN(Implementability.IMPLEMENTABLE),
    VA_FORMAT_STRING_EXPECTED_MESSAGE_FORMAT_SUPPLIED(Implementability.IMPLEMENTABLE),
    VA_FORMAT_STRING_EXTRA_ARGUMENTS_PASSED(Implementability.IMPLEMENTABLE),
    VA_FORMAT_STRING_ILLEGAL(Implementability.IMPLEMENTABLE),
    VA_FORMAT_STRING_MISSING_ARGUMENT(Implementability.IMPLEMENTABLE),
    VA_FORMAT_STRING_NO_PREVIOUS_ARGUMENT(Implementability.IMPLEMENTABLE),
    VA_FORMAT_STRING_USES_NEWLINE(Implementability.IMPLEMENTABLE),
    VA_PRIMITIVE_ARRAY_PASSED_TO_OBJECT_VARARG(Implementability.IMPLEMENTABLE),
    VO_VOLATILE_INCREMENT(Implementability.IMPLEMENTABLE),
    VO_VOLATILE_REFERENCE_TO_ARRAY(Implementability.IMPLEMENTABLE),
    WA_AWAIT_NOT_IN_LOOP(Implementability.IMPLEMENTABLE),
    WA_NOT_IN_LOOP(Implementability.IMPLEMENTABLE),
    WL_USING_GETCLASS_RATHER_THAN_CLASS_LITERAL(Implementability.IMPLEMENTABLE),
    WMI_WRONG_MAP_ITERATOR(Implementability.IMPLEMENTABLE),
    WS_WRITEOBJECT_SYNC(Implementability.IMPLEMENTABLE),
    XFB_XML_FACTORY_BYPASS(Implementability.IMPLEMENTABLE),
    XSS_REQUEST_PARAMETER_TO_JSP_WRITER(Implementability.IMPLEMENTABLE),
    XSS_REQUEST_PARAMETER_TO_SEND_ERROR(Implementability.IMPLEMENTABLE),
    XSS_REQUEST_PARAMETER_TO_SERVLET_WRITER(Implementability.IMPLEMENTABLE);

    private Implementability ability;

    StandardRule(Implementability ability) {

      this.ability = ability;
    }

    public Implementability getImplementability() {

      return this.ability;
    }

    @Override
    public String getCodingStandardRuleId() {

      return this.name();
    }
  }

  @Override
  public String getReport() throws RuleException {
    return getSummaryReport();
  }

  @Override
  public String getReport(String instance) throws RuleException {

    return getSummaryReport(instance);
  }

  @Override
  public String getSummaryReport() throws RuleException {

    return getSummaryReport(RuleManager.NEMO);
  }

  @Override
  public String getSummaryReport(String instance) throws RuleException {

    initCoverageResults(instance);
    computeCoverage();

    String linebreak = String.format("%n");

    int count = StandardRule.values().length;
    int unspecified = count - specified - skipped;

    StringBuilder sb = new StringBuilder();
    sb.append(linebreak).append(standardName).append(linebreak);
    sb.append(formatLine("FB rule count:", count, 100));
    sb.append(formatLine("rejected:", skipped, ((float)skipped/count)*100));
    sb.append(formatLine("implementable:", implementable, ((float)implementable/count)*100));
    sb.append(linebreak).append("Of Implementable rules:").append(linebreak);
    sb.append(formatLine("unspecified:", unspecified, ((float)unspecified/implementable)*100));
    sb.append(formatLine("specified:", specified, ((float)specified/implementable)*100));
    sb.append(formatLine("implemented:", implemented, ((float)implemented/implementable)*100));

    return sb.toString();
  }

  protected void computeCoverage() {

    for (StandardRule standardRule : StandardRule.values()) {
      Implementability impl = standardRule.getImplementability();
      if (impl.equals(Implementability.IMPLEMENTABLE)) {
        implementable ++;
      } else if (impl.equals(Implementability.REJECTED)) {
        skipped++;
      }
    }

    Iterator<CodingStandardRuleCoverage> itr = getRulesCoverage().values().iterator();
    while (itr.hasNext()) {
      CodingStandardRuleCoverage cov = itr.next();

      if (cov.getImplementedBy() != null) {
        implemented++;
      }
      if (cov.getSpecifiedBy() != null) {
        specified ++;
      }
    }
  }

  protected String formatLine(String label, int count, float percentage) {
    return String.format("  %-15s %3d  %6.2f%%%n", label, count, percentage);
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
    return rspecFieldName;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {
    return rule.getFindbugs();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setFindbugs(ids);
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return StandardRule.values();
  }

  @Override
  public String getDeprecationReport(String instance) throws RuleException {

    initCoverageResults(instance);
    StringBuilder sb = new StringBuilder();

    for (CodingStandardRuleCoverage cov : getRulesCoverage().values()) {
      if (cov.getImplementedBy() != null) {
        if (sb.length() > 0) {
          sb.append(", ");
        }
        sb.append(cov.getRule());
      }
    }

    return sb.toString();
  }

}
