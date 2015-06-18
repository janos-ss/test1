/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.tools;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableExternalTool;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.List;


public class Pylint extends AbstractReportableExternalTool {

  private Language language = Language.PY;
  private String name = "Pylint";


  public enum PylintRule implements CodingStandardRule {
    C0102 ("Black listed name \"%s\"", Implementability.IMPLEMENTABLE),
    C0103 ("Invalid %s name \"%s\"", Implementability.IMPLEMENTABLE),
    C0111 ("Missing %s docstring", Implementability.IMPLEMENTABLE),
    C0112 ("Empty %s docstring", Implementability.IMPLEMENTABLE),
    C0121 ("Missing required attribute \"%s\"", Implementability.IMPLEMENTABLE),
    C0202 ("Class method %s should have cls as first argument", Implementability.IMPLEMENTABLE),
    C0203 ("Metaclass method %s should have mcs as first argument", Implementability.IMPLEMENTABLE),
    C0204 ("Metaclass class method %s should have %s as first argument", Implementability.IMPLEMENTABLE),
    C0301 ("Line too long (%s/%s)", Implementability.IMPLEMENTABLE),
    C0302 ("Too many lines in module (%s)", Implementability.IMPLEMENTABLE),
    C0303 ("Trailing whitespace", Implementability.IMPLEMENTABLE),
    C0304 ("Final newline missing", Implementability.IMPLEMENTABLE),
    C0321 ("More than one statement on a single line", Implementability.IMPLEMENTABLE),
    C0322 ("Old: Operator not preceded by a space", Implementability.REJECTED),
    C0323 ("Old: Operator not followed by a space", Implementability.REJECTED),
    C0324 ("Old: Comma not followed by a space", Implementability.REJECTED),
    C0325 ("Unnecessary parens after %r keyword", Implementability.IMPLEMENTABLE),
    C0326 ("%s space %s %s %s\n%s", Implementability.REJECTED),
    C1001 ("Old-style class defined.", Implementability.IMPLEMENTABLE),
    E0001 ("(syntax error raised for a module; message varies)", Implementability.IMPLEMENTABLE),
    E0011 ("Unrecognized file option %r", Implementability.REJECTED),
    E0012 ("Bad option value %r", Implementability.REJECTED),
    E0100 ("__init__ method is a generator", Implementability.IMPLEMENTABLE),
    E0101 ("Explicit return in __init__", Implementability.IMPLEMENTABLE),
    E0102 ("%s already defined line %s", Implementability.IMPLEMENTABLE),
    E0103 ("%r not properly in loop", Implementability.IMPLEMENTABLE),
    E0104 ("Return outside function", Implementability.IMPLEMENTABLE),
    E0105 ("Yield outside function", Implementability.IMPLEMENTABLE),
    E0106 ("Return with argument inside generator", Implementability.IMPLEMENTABLE),
    E0107 ("Use of the non-existent %s operator", Implementability.IMPLEMENTABLE),
    E0108 ("Duplicate argument name %s in function definition", Implementability.IMPLEMENTABLE),
    E0202 ("An attribute affected in %s line %s hide this method", Implementability.IMPLEMENTABLE),
    E0203 ("Access to member %r before its definition line %s", Implementability.IMPLEMENTABLE),
    E0211 ("Method has no argument", Implementability.IMPLEMENTABLE),
    E0213 ("Method should have \"self\" as first argument", Implementability.IMPLEMENTABLE),
    E0221 ("Interface resolved to %s is not a class", Implementability.IMPLEMENTABLE),
    E0222 ("Missing method %r from %s interface", Implementability.IMPLEMENTABLE),
    E0235 ("__exit__ must accept 3 arguments: type, value, traceback", Implementability.IMPLEMENTABLE),
    E0501 ("Old: Non ascii characters found but no encoding specified (PEP 263)", Implementability.REJECTED),
    E0502 ("Old: Wrong encoding specified (%s)", Implementability.REJECTED),
    E0503 ("Old: Unknown encoding specified (%s)", Implementability.REJECTED),
    E0601 ("Using variable %r before assignment", Implementability.IMPLEMENTABLE),
    E0602 ("Undefined variable %r", Implementability.IMPLEMENTABLE),
    E0603 ("Undefined variable name %r in __all__", Implementability.IMPLEMENTABLE),
    E0604 ("Invalid object %r in __all__, must contain only strings", Implementability.IMPLEMENTABLE),
    E0611 ("No name %r in module %r", Implementability.IMPLEMENTABLE),
    E0701 ("Bad except clauses order (%s)", Implementability.IMPLEMENTABLE),
    E0702 ("Raising %s while only classes, instances or string are allowed", Implementability.IMPLEMENTABLE),
    E0710 ("Raising a new style class which doesn't inherit from BaseException", Implementability.IMPLEMENTABLE),
    E0711 ("NotImplemented raised - should raise NotImplementedError", Implementability.IMPLEMENTABLE),
    E0712 ("Catching an exception which doesn\'t inherit from BaseException: %s", Implementability.IMPLEMENTABLE),
    E1001 ("Use of __slots__ on an old style class", Implementability.IMPLEMENTABLE),
    E1002 ("Use of super on an old style class", Implementability.IMPLEMENTABLE),
    E1003 ("Bad first argument %r given to super()", Implementability.IMPLEMENTABLE),
    E1004 ("Missing argument to super()", Implementability.IMPLEMENTABLE),
    E1101 ("%s %r has no %r member", Implementability.IMPLEMENTABLE),
    E1102 ("%s is not callable", Implementability.IMPLEMENTABLE),
    E1103 ("%s %r has no %r member (but some types could not be inferred)", Implementability.IMPLEMENTABLE),
    E1111 ("Assigning to function call which doesn't return", Implementability.IMPLEMENTABLE),
    E1120 ("No value passed for parameter %s in function call", Implementability.IMPLEMENTABLE),
    E1121 ("Too many positional arguments for function call", Implementability.IMPLEMENTABLE),
    E1122 ("Old: Duplicate keyword argument %r in function call", Implementability.REJECTED),
    E1123 ("Passing unexpected keyword argument %r in function call", Implementability.IMPLEMENTABLE),
    E1124 ("Parameter %r passed as both positional and keyword argument", Implementability.IMPLEMENTABLE),
    E1125 ("Old: Missing mandatory keyword argument %r", Implementability.REJECTED),
    E1200 ("Unsupported logging format character %r (%#02x) at index %d", Implementability.IMPLEMENTABLE),
    E1201 ("Logging format string ends in middle of conversion specifier", Implementability.IMPLEMENTABLE),
    E1205 ("Too many arguments for logging format string", Implementability.IMPLEMENTABLE),
    E1206 ("Not enough arguments for logging format string", Implementability.IMPLEMENTABLE),
    E1300 ("Unsupported format character %r (%#02x) at index %d", Implementability.IMPLEMENTABLE),
    E1301 ("Format string ends in middle of conversion specifier", Implementability.IMPLEMENTABLE),
    E1302 ("Mixing named and unnamed conversion specifiers in format string", Implementability.IMPLEMENTABLE),
    E1303 ("Expected mapping for format string, not %s", Implementability.IMPLEMENTABLE),
    E1304 ("Missing key %r in format string dictionary", Implementability.IMPLEMENTABLE),
    E1305 ("Too many arguments for format string", Implementability.IMPLEMENTABLE),
    E1306 ("Not enough arguments for format string", Implementability.IMPLEMENTABLE),
    E1310 ("Suspicious argument in %s.%s call", Implementability.IMPLEMENTABLE),
    F0001 ("(error prevented analysis; message varies)", Implementability.REJECTED),
    F0002 ("%s: %s (message varies)", Implementability.REJECTED),
    F0003 ("ignored builtin module %s", Implementability.REJECTED),
    F0004 ("unexpected inferred value %s", Implementability.REJECTED),
    F0010 ("error while code parsing: %s", Implementability.REJECTED),
    F0202 ("Unable to check methods signature (%s / %s)", Implementability.REJECTED),
    F0220 ("failed to resolve interfaces implemented by %s (%s)", Implementability.REJECTED),
    F0321 ("Old: Format detection error in %r", Implementability.REJECTED),
    F0401 ("Unable to import %s", Implementability.REJECTED),
    I0001 ("Unable to run raw checkers on built-in module %s", Implementability.REJECTED),
    I0010 ("Unable to consider inline option %r", Implementability.REJECTED),
    I0011 ("Locally disabling %s", Implementability.REJECTED),
    I0012 ("Locally enabling %s", Implementability.REJECTED),
    I0013 ("Ignoring entire file", Implementability.REJECTED),
    I0014 ("Used deprecated directive \"pylint:disable-all\" or \"pylint:disable=all\"", Implementability.REJECTED),
    I0020 ("Suppressed %s (from line %d)", Implementability.REJECTED),
    I0021 ("Useless suppression of %s", Implementability.REJECTED),
    I0022 ("Deprecated pragma \"pylint:disable-msg\" or \"pylint:enable-msg\"", Implementability.REJECTED),
    R0201 ("Method could be a function", Implementability.IMPLEMENTABLE),
    R0401 ("Cyclic import (%s)", Implementability.IMPLEMENTABLE),
    R0801 ("Similar lines in %s files", Implementability.IMPLEMENTABLE),
    R0901 ("Too many ancestors (%s/%s)", Implementability.IMPLEMENTABLE),
    R0902 ("Too many instance attributes (%s/%s)", Implementability.IMPLEMENTABLE),
    R0903 ("Too few public methods (%s/%s)", Implementability.IMPLEMENTABLE),
    R0904 ("Too many public methods (%s/%s)", Implementability.IMPLEMENTABLE),
    R0911 ("Too many return statements (%s/%s)", Implementability.IMPLEMENTABLE),
    R0912 ("Too many branches (%s/%s)", Implementability.IMPLEMENTABLE),
    R0913 ("Too many arguments (%s/%s)", Implementability.IMPLEMENTABLE),
    R0914 ("Too many local variables (%s/%s)", Implementability.IMPLEMENTABLE),
    R0915 ("Too many statements (%s/%s)", Implementability.IMPLEMENTABLE),
    R0921 ("Abstract class not referenced", Implementability.IMPLEMENTABLE),
    R0922 ("Abstract class is only referenced %s times", Implementability.IMPLEMENTABLE),
    R0923 ("Interface not implemented", Implementability.IMPLEMENTABLE),
    W0101 ("Unreachable code", Implementability.IMPLEMENTABLE),
    W0102 ("Dangerous default value %s as argument", Implementability.IMPLEMENTABLE),
    W0104 ("Statement seems to have no effect", Implementability.IMPLEMENTABLE),
    W0105 ("String statement has no effect", Implementability.IMPLEMENTABLE),
    W0106 ("Expression \"%s\" is assigned to nothing", Implementability.IMPLEMENTABLE),
    W0107 ("Unnecessary pass statement", Implementability.IMPLEMENTABLE),
    W0108 ("Lambda may not be necessary", Implementability.IMPLEMENTABLE),
    W0109 ("Duplicate key %r in dictionary", Implementability.IMPLEMENTABLE),
    W0110 ("map/filter on lambda could be replaced by comprehension", Implementability.IMPLEMENTABLE),
    W0120 ("Else clause on loop without a break statement", Implementability.IMPLEMENTABLE),
    W0121 ("Use raise ErrorClass(args) instead of raise ErrorClass, args.", Implementability.IMPLEMENTABLE),
    W0122 ("Use of exec", Implementability.IMPLEMENTABLE),
    W0141 ("Used builtin function %r", Implementability.IMPLEMENTABLE),
    W0142 ("Used * or ** magic", Implementability.IMPLEMENTABLE),
    W0150 ("%s statement in finally block may swallow exception", Implementability.IMPLEMENTABLE),
    W0199 ("Assert called on a 2-uple. Did you mean \'assert x,y\'?", Implementability.IMPLEMENTABLE),
    W0201 ("Attribute %r defined outside __init__", Implementability.IMPLEMENTABLE),
    W0211 ("Static method with %r as first argument", Implementability.IMPLEMENTABLE),
    W0212 ("Access to a protected member %s of a client class", Implementability.IMPLEMENTABLE),
    W0221 ("Arguments number differs from %s method", Implementability.IMPLEMENTABLE),
    W0222 ("Signature differs from %s method", Implementability.IMPLEMENTABLE),
    W0223 ("Method %r is abstract in class %r but is not overridden", Implementability.IMPLEMENTABLE),
    W0231 ("__init__ method from base class %r is not called", Implementability.IMPLEMENTABLE),
    W0232 ("Class has no __init__ method", Implementability.IMPLEMENTABLE),
    W0233 ("__init__ method from a non direct base class %r is called", Implementability.IMPLEMENTABLE),
    W0234 ("iter returns non-iterator", Implementability.IMPLEMENTABLE),
    W0301 ("Unnecessary semicolon", Implementability.IMPLEMENTABLE),
    W0311 ("Bad indentation. Found %s %s, expected %s", Implementability.IMPLEMENTABLE),
    W0312 ("Found indentation with %ss instead of %ss", Implementability.IMPLEMENTABLE),
    W0331 ("Use of the <> operator", Implementability.IMPLEMENTABLE),
    W0332 ("Use of \"l\" as long integer identifier", Implementability.IMPLEMENTABLE),
    W0333 ("Use of the `` operator", Implementability.IMPLEMENTABLE),
    W0401 ("Wildcard import %s", Implementability.IMPLEMENTABLE),
    W0402 ("Uses of a deprecated module %r", Implementability.IMPLEMENTABLE),
    W0403 ("Relative import %r, should be %r", Implementability.IMPLEMENTABLE),
    W0404 ("Reimport %r (imported line %s)", Implementability.IMPLEMENTABLE),
    W0406 ("Module import itself", Implementability.IMPLEMENTABLE),
    W0410 ("__future__ import is not the first non docstring statement", Implementability.IMPLEMENTABLE),
    W0511 ("(warning notes in code comments; message varies)", Implementability.IMPLEMENTABLE),
    W0512 ("Cannot decode using encoding \"%s\", unexpected byte at position %d", Implementability.IMPLEMENTABLE),
    W0601 ("Global variable %r undefined at the module level", Implementability.IMPLEMENTABLE),
    W0602 ("Using global for %r but no assigment is done", Implementability.IMPLEMENTABLE),
    W0603 ("Using the global statement", Implementability.IMPLEMENTABLE),
    W0604 ("Using the global statement at the module level", Implementability.IMPLEMENTABLE),
    W0611 ("Unused import %s", Implementability.IMPLEMENTABLE),
    W0612 ("Unused variable %r", Implementability.IMPLEMENTABLE),
    W0613 ("Unused argument %r", Implementability.IMPLEMENTABLE),
    W0614 ("Unused import %s from wildcard import", Implementability.IMPLEMENTABLE),
    W0621 ("Redefining name %r from outer scope (line %s)", Implementability.IMPLEMENTABLE),
    W0622 ("Redefining built-in %r", Implementability.IMPLEMENTABLE),
    W0623 ("Redefining name %r from %s in exception handler", Implementability.IMPLEMENTABLE),
    W0631 ("Using possibly undefined loop variable %r", Implementability.IMPLEMENTABLE),
    W0632 ("Possible unbalanced tuple unpacking with sequence%s: …", Implementability.IMPLEMENTABLE),
    W0633 ("Attempting to unpack a non-sequence%s", Implementability.IMPLEMENTABLE),
    W0701 ("Raising a string exception", Implementability.IMPLEMENTABLE),
    W0702 ("No exception type(s) specified", Implementability.IMPLEMENTABLE),
    W0703 ("Catching too general exception %s", Implementability.IMPLEMENTABLE),
    W0704 ("Except doesn't do anything", Implementability.IMPLEMENTABLE),
    W0710 ("Exception doesn't inherit from standard \"Exception\" class", Implementability.IMPLEMENTABLE),
    W0711 ("Exception to catch is the result of a binary \"%s\" operation", Implementability.IMPLEMENTABLE),
    W0712 ("Implicit unpacking of exceptions is not supported in Python 3", Implementability.IMPLEMENTABLE),
    W1001 ("Use of \"property\" on an old style class", Implementability.IMPLEMENTABLE),
    W1111 ("Assigning to function call which only returns None", Implementability.IMPLEMENTABLE),
    W1201 ("Specify string format arguments as logging function parameters", Implementability.IMPLEMENTABLE),
    W1300 ("Format string dictionary key should be a string, not %s", Implementability.IMPLEMENTABLE),
    W1301 ("Unused key %r in format string dictionary", Implementability.IMPLEMENTABLE),
    W1401 ("Anomalous backslash in string: \'%s\'. String constant might be missing an r prefix.", Implementability.IMPLEMENTABLE),
    W1402 ("Anomalous Unicode escape in byte string: \'%s\'. String constant might be missing an r or u prefix.", Implementability.IMPLEMENTABLE),
    W1501 ("\"%s\" is not a valid mode for open.", Implementability.IMPLEMENTABLE),

    RP0001 ("Messages by category", Implementability.IMPLEMENTABLE),
    RP0002 ("% errors / warnings by module", Implementability.IMPLEMENTABLE),
    RP0003 ("Messages", Implementability.IMPLEMENTABLE),
    RP0004 ("Global evaluation", Implementability.IMPLEMENTABLE),
    RP0101 ("Statistics by type", Implementability.IMPLEMENTABLE),
    RP0401 ("External dependencies", Implementability.IMPLEMENTABLE),
    RP0402 ("Modules dependencies graph", Implementability.IMPLEMENTABLE),
    RP0701 ("Raw metrics", Implementability.IMPLEMENTABLE),
    RP0801 ("Duplication", Implementability.IMPLEMENTABLE);

    private Implementability ability;
    private String title;

    PylintRule(String title, Implementability ability) {
      this.title = title;
      this.ability = ability;
    }

    @Override
    public Implementability getImplementability(){
      return this.ability;
    }

    @Override
    public String getCodingStandardRuleId() {
      return this.name();
    }

    public String getTitle() {
      return this.title;
    }
  }

  @Override
  public Language getLanguage() {

    return language;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return PylintRule.values();
  }

  @Override
  public String getStandardName() {

    return name;
  }

  @Override
  public String getRSpecReferenceFieldName() {

    return name;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {
    return rule.getPylint();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setPylint(ids);
  }
}
