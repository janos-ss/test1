/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.services.ReportService;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cwe extends AbstractMultiLanguageStandard implements TaggableStandard {

  private static final Logger LOGGER = Logger.getLogger(Cwe.class.getName());

  private static final String TAG = "cwe";
  private static final String REFERENCE_PATTERN = "CWE-\\d+";
  private static final String NAME = "CWE";
  private static final String TITLE_AND_INTRO = "<h2>%1$s Coverage of CWE</h2>\n" +
          "<p>The following table lists the CWE items %1$s is able to detect, " +
          "and for each of them, the rules providing this coverage.</p>";

  private Language language = null;

  @Override
  public boolean isTagShared() {

    return false;
  }

  @Override
  public String getTag() {

    return TAG;
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
  public List<String> getRspecReferenceFieldValues(Rule rule) {

    return rule.getCwe();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {

    rule.setCwe(ids);
  }

  @Override
  public String getSeeSectionSearchString() {

    return NAME;
  }

  @Override
  public String getReferencePattern() {

    return REFERENCE_PATTERN;
  }

  @Override
  public boolean doesReferenceNeedUpdating(String reference, List<String> replacements, String ruleKey){

    if (reference.matches("\\d+")) {
      replacements.add(NAME + "-" + reference);
      return true;
    } else {
      if (!reference.matches(REFERENCE_PATTERN)) {
        LOGGER.log(Level.INFO, "Unrecognized CWE reference pattern {0} in {1}",
                new Object[] {reference, ruleKey});
      }

      replacements.add(reference);
    }

    return false;
  }

  @Override
  public String getReport(String instance) {

    if (language == null) {
      return null;
    }

    initCoverageResults(instance);
    return generateReport(instance);
  }

  @Override
  protected String generateReport(String instance) {

    if (language == null || this.getRulesCoverage() == null) {
      return null;
    }

    StringBuilder sb = new StringBuilder();

    sb.append(String.format(ReportService.HEADER_TEMPLATE,getLanguage().getReportName(), NAME))
            .append(String.format(TITLE_AND_INTRO,getLanguage().getReportName()))
            .append(ReportService.TABLE_OPEN)
            .append("<thead><tr><th>CWE ID</th><th>CWE Name</th><th>Implementing Rules</th></tr></thead>")
            .append("<tbody>");

    for (CweRule cwe : CweRule.values()) {
      String key = cwe.getCodingStandardRuleId();
      CodingStandardRuleCoverage csrc = getRulesCoverage().get(key);
      if (!csrc.getImplementedBy().isEmpty()) {

        Integer ikey = Integer.valueOf(key.split("-")[1]);
        sb.append("<tr><td><a href='http://cwe.mitre.org/data/definitions/").append(ikey)
                .append("' target='_blank'>").append(key).append("</a></td><td>")
                .append(cwe.getTitle()).append("</td><td>");

        for (Rule rule : csrc.getImplementedBy()) {
          sb.append(Utilities.getNemoLinkedRuleReference(instance, rule));
        }
        sb.append("</td></tr>\n");
      }
    }
    sb.append("</table>")
            .append(ReportService.FOOTER_TEMPLATE);

    return sb.toString();
  }


  /**
   /**
   * This override required by the fact that we don't hold the list of CWE id's
   * in this class. As a result the {{rulesCoverage}} map starts out non-null, but empty.
   *
   * We must simply assume that each passed id is valid and store the related data.
   *
   * @param ids list of CWE ids implemented by rspecRule
   * @param rule
   */
  @Override
  public void setCodingStandardRuleCoverageImplemented(List<String> ids, Rule rule) {

    if (getRulesCoverage() == null) {
      populateRulesCoverageMap();
    }

    if (ids != null && ! ids.isEmpty()) {
      for (String id : ids) {
        CodingStandardRuleCoverage cov = getRulesCoverage().get(id);
        if (cov == null) {
          cov = new CodingStandardRuleCoverage();
          cov.setCodingStandardRuleId(id);
          getRulesCoverage().put(id, cov);
        }
        cov.addImplementedBy(rule);
      }
    }
  }

  @Override
  public String getSummaryReport(String instance) {

    return getReport(instance);
  }

  @Override
  public Language getLanguage() {

    return language;
  }

  @Override
  public void setLanguage(Language language) {
    this.language = language;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return CweRule.values();
  }

  public enum CweRule implements CodingStandardRule {

    CWE_5 ("J2EE Misconfiguration: Data Transmission Without Encryption", Implementability.IMPLEMENTABLE),
    CWE_6 ("J2EE Misconfiguration: Insufficient Session-ID Length", Implementability.IMPLEMENTABLE),
    CWE_7 ("J2EE Misconfiguration: Missing Custom Error Page", Implementability.IMPLEMENTABLE),
    CWE_8 ("J2EE Misconfiguration: Entity Bean Declared Remote", Implementability.IMPLEMENTABLE),
    CWE_9 ("J2EE Misconfiguration: Weak Access Permissions for EJB Methods", Implementability.IMPLEMENTABLE),
    CWE_11 ("ASP.NET Misconfiguration: Creating Debug Binary", Implementability.IMPLEMENTABLE),
    CWE_12 ("ASP.NET Misconfiguration: Missing Custom Error Page", Implementability.IMPLEMENTABLE),
    CWE_13 ("ASP.NET Misconfiguration: Password in Configuration File", Implementability.IMPLEMENTABLE),
    CWE_14 ("Compiler Removal of Code to Clear Buffers", Implementability.IMPLEMENTABLE),
    CWE_15 ("External Control of System or Configuration Setting", Implementability.IMPLEMENTABLE),
    CWE_20 ("Improper Input Validation", Implementability.IMPLEMENTABLE),
    CWE_22 ("Improper Limitation of a Pathname to a Restricted Directory ('Path Traversal')", Implementability.IMPLEMENTABLE),
    CWE_23 ("Relative Path Traversal", Implementability.IMPLEMENTABLE),
    CWE_24 ("Path Traversal: '../filedir'", Implementability.IMPLEMENTABLE),
    CWE_25 ("Path Traversal: '/../filedir'", Implementability.IMPLEMENTABLE),
    CWE_26 ("Path Traversal: '/dir/../filename'", Implementability.IMPLEMENTABLE),
    CWE_27 ("Path Traversal: 'dir/../../filename'", Implementability.IMPLEMENTABLE),
    CWE_28 ("Path Traversal: '..\filedir'", Implementability.IMPLEMENTABLE),
    CWE_29 ("Path Traversal: '\\..\\filename'", Implementability.IMPLEMENTABLE),
    CWE_30 ("Path Traversal: '\\dir\\..\\filename'", Implementability.IMPLEMENTABLE),
    CWE_31 ("Path Traversal: 'dir\\..\\..\\filename'", Implementability.IMPLEMENTABLE),
    CWE_32 ("Path Traversal: '...' (Triple Dot)", Implementability.IMPLEMENTABLE),
    CWE_33 ("Path Traversal: '....' (Multiple Dot)", Implementability.IMPLEMENTABLE),
    CWE_34 ("Path Traversal: '....//'", Implementability.IMPLEMENTABLE),
    CWE_35 ("Path Traversal: '.../...//'", Implementability.IMPLEMENTABLE),
    CWE_36 ("Absolute Path Traversal", Implementability.IMPLEMENTABLE),
    CWE_37 ("Path Traversal: '/absolute/pathname/here'", Implementability.IMPLEMENTABLE),
    CWE_38 ("Path Traversal: '\\absolute\\pathname\\here'", Implementability.IMPLEMENTABLE),
    CWE_39 ("Path Traversal: 'C:dirname'", Implementability.IMPLEMENTABLE),
    CWE_40 ("Path Traversal: '\\\\UNC\\share\\name\\' (Windows UNC Share)", Implementability.IMPLEMENTABLE),
    CWE_41 ("Improper Resolution of Path Equivalence", Implementability.IMPLEMENTABLE),
    CWE_42 ("Path Equivalence: 'filename.' (Trailing Dot)", Implementability.IMPLEMENTABLE),
    CWE_43 ("Path Equivalence: 'filename....' (Multiple Trailing Dot)", Implementability.IMPLEMENTABLE),
    CWE_44 ("Path Equivalence: 'file.name' (Internal Dot)", Implementability.IMPLEMENTABLE),
    CWE_45 ("Path Equivalence: 'file...name' (Multiple Internal Dot)", Implementability.IMPLEMENTABLE),
    CWE_46 ("Path Equivalence: 'filename ' (Trailing Space)", Implementability.IMPLEMENTABLE),
    CWE_47 ("Path Equivalence: ' filename' (Leading Space)", Implementability.IMPLEMENTABLE),
    CWE_48 ("Path Equivalence: 'file name' (Internal Whitespace)", Implementability.IMPLEMENTABLE),
    CWE_49 ("Path Equivalence: 'filename/' (Trailing Slash)", Implementability.IMPLEMENTABLE),
    CWE_50 ("Path Equivalence: '//multiple/leading/slash'", Implementability.IMPLEMENTABLE),
    CWE_51 ("Path Equivalence: '/multiple//internal/slash'", Implementability.IMPLEMENTABLE),
    CWE_52 ("Path Equivalence: '/multiple/trailing/slash//'", Implementability.IMPLEMENTABLE),
    CWE_53 ("Path Equivalence: '\\multiple\\\\internal\\backslash'", Implementability.IMPLEMENTABLE),
    CWE_54 ("Path Equivalence: 'filedir\' (Trailing Backslash)", Implementability.IMPLEMENTABLE),
    CWE_55 ("Path Equivalence: '/./' (Single Dot Directory)", Implementability.IMPLEMENTABLE),
    CWE_56 ("Path Equivalence: 'filedir*' (Wildcard)", Implementability.IMPLEMENTABLE),
    CWE_57 ("Path Equivalence: 'fakedir/../realdir/filename'", Implementability.IMPLEMENTABLE),
    CWE_58 ("Path Equivalence: Windows 8.3 Filename", Implementability.IMPLEMENTABLE),
    CWE_59 ("Improper Link Resolution Before File Access ('Link Following')", Implementability.IMPLEMENTABLE),
    CWE_61 ("Composite UNIX Symbolic Link (Symlink) Following", Implementability.IMPLEMENTABLE),
    CWE_62 ("UNIX Hard Link", Implementability.IMPLEMENTABLE),
    CWE_64 ("Windows Shortcut Following (.LNK)", Implementability.IMPLEMENTABLE),
    CWE_65 ("Windows Hard Link", Implementability.IMPLEMENTABLE),
    CWE_66 ("Improper Handling of File Names that Identify Virtual Resources", Implementability.IMPLEMENTABLE),
    CWE_67 ("Improper Handling of Windows Device Names", Implementability.IMPLEMENTABLE),
    CWE_69 ("Improper Handling of Windows ::DATA Alternate Data Stream", Implementability.IMPLEMENTABLE),
    CWE_71 ("Apple '.DS_Store'", Implementability.IMPLEMENTABLE),
    CWE_72 ("Improper Handling of Apple HFS+ Alternate Data Stream Path", Implementability.IMPLEMENTABLE),
    CWE_73 ("External Control of File Name or Path", Implementability.IMPLEMENTABLE),
    CWE_74 ("Improper Neutralization of Special Elements in Output Used by a Downstream Component ('Injection')", Implementability.IMPLEMENTABLE),
    CWE_75 ("Failure to Sanitize Special Elements into a Different Plane (Special Element Injection)", Implementability.IMPLEMENTABLE),
    CWE_76 ("Improper Neutralization of Equivalent Special Elements", Implementability.IMPLEMENTABLE),
    CWE_77 ("Improper Neutralization of Special Elements used in a Command ('Command Injection')", Implementability.IMPLEMENTABLE),
    CWE_78 ("Improper Neutralization of Special Elements used in an OS Command ('OS Command Injection')", Implementability.IMPLEMENTABLE),
    CWE_79 ("Improper Neutralization of Input During Web Page Generation ('Cross-site Scripting')", Implementability.IMPLEMENTABLE),
    CWE_80 ("Improper Neutralization of Script-Related HTML Tags in a Web Page (Basic XSS)", Implementability.IMPLEMENTABLE),
    CWE_81 ("Improper Neutralization of Script in an Error Message Web Page", Implementability.IMPLEMENTABLE),
    CWE_82 ("Improper Neutralization of Script in Attributes of IMG Tags in a Web Page", Implementability.IMPLEMENTABLE),
    CWE_83 ("Improper Neutralization of Script in Attributes in a Web Page", Implementability.IMPLEMENTABLE),
    CWE_84 ("Improper Neutralization of Encoded URI Schemes in a Web Page", Implementability.IMPLEMENTABLE),
    CWE_85 ("Doubled Character XSS Manipulations", Implementability.IMPLEMENTABLE),
    CWE_86 ("Improper Neutralization of Invalid Characters in Identifiers in Web Pages", Implementability.IMPLEMENTABLE),
    CWE_87 ("Improper Neutralization of Alternate XSS Syntax", Implementability.IMPLEMENTABLE),
    CWE_88 ("Argument Injection or Modification", Implementability.IMPLEMENTABLE),
    CWE_89 ("Improper Neutralization of Special Elements used in an SQL Command ('SQL Injection')", Implementability.IMPLEMENTABLE),
    CWE_90 ("Improper Neutralization of Special Elements used in an LDAP Query ('LDAP Injection')", Implementability.IMPLEMENTABLE),
    CWE_91 ("XML Injection (aka Blind XPath Injection)", Implementability.IMPLEMENTABLE),
    CWE_93 ("Improper Neutralization of CRLF Sequences ('CRLF Injection')", Implementability.IMPLEMENTABLE),
    CWE_94 ("Improper Control of Generation of Code ('Code Injection')", Implementability.IMPLEMENTABLE),
    CWE_95 ("Improper Neutralization of Directives in Dynamically Evaluated Code ('Eval Injection')", Implementability.IMPLEMENTABLE),
    CWE_96 ("Improper Neutralization of Directives in Statically Saved Code ('Static Code Injection')", Implementability.IMPLEMENTABLE),
    CWE_97 ("Improper Neutralization of Server-Side Includes (SSI) Within a Web Page", Implementability.IMPLEMENTABLE),
    CWE_98 ("Improper Control of Filename for Include/Require Statement in PHP Program ('PHP Remote File Inclusion')", Implementability.IMPLEMENTABLE),
    CWE_99 ("Improper Control of Resource Identifiers ('Resource Injection')", Implementability.IMPLEMENTABLE),
    CWE_100 ("Technology-Specific Input Validation Problems", Implementability.IMPLEMENTABLE),
    CWE_102 ("Struts: Duplicate Validation Forms", Implementability.IMPLEMENTABLE),
    CWE_103 ("Struts: Incomplete validate() Method Definition", Implementability.IMPLEMENTABLE),
    CWE_104 ("Struts: Form Bean Does Not Extend Validation Class", Implementability.IMPLEMENTABLE),
    CWE_105 ("Struts: Form Field Without Validator", Implementability.IMPLEMENTABLE),
    CWE_106 ("Struts: Plug-in Framework not in Use", Implementability.IMPLEMENTABLE),
    CWE_107 ("Struts: Unused Validation Form", Implementability.IMPLEMENTABLE),
    CWE_108 ("Struts: Unvalidated Action Form", Implementability.IMPLEMENTABLE),
    CWE_109 ("Struts: Validator Turned Off", Implementability.IMPLEMENTABLE),
    CWE_110 ("Struts: Validator Without Form Field", Implementability.IMPLEMENTABLE),
    CWE_111 ("Direct Use of Unsafe JNI", Implementability.IMPLEMENTABLE),
    CWE_112 ("Missing XML Validation", Implementability.IMPLEMENTABLE),
    CWE_113 ("Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')", Implementability.IMPLEMENTABLE),
    CWE_114 ("Process Control", Implementability.IMPLEMENTABLE),
    CWE_115 ("Misinterpretation of Input", Implementability.IMPLEMENTABLE),
    CWE_116 ("Improper Encoding or Escaping of Output", Implementability.IMPLEMENTABLE),
    CWE_117 ("Improper Output Neutralization for Logs", Implementability.IMPLEMENTABLE),
    CWE_118 ("Improper Access of Indexable Resource ('Range Error')", Implementability.IMPLEMENTABLE),
    CWE_119 ("Improper Restriction of Operations within the Bounds of a Memory Buffer", Implementability.IMPLEMENTABLE),
    CWE_120 ("Buffer Copy without Checking Size of Input ('Classic Buffer Overflow')", Implementability.IMPLEMENTABLE),
    CWE_121 ("Stack-based Buffer Overflow", Implementability.IMPLEMENTABLE),
    CWE_122 ("Heap-based Buffer Overflow", Implementability.IMPLEMENTABLE),
    CWE_123 ("Write-what-where Condition", Implementability.IMPLEMENTABLE),
    CWE_124 ("Buffer Underwrite ('Buffer Underflow')", Implementability.IMPLEMENTABLE),
    CWE_125 ("Out-of-bounds Read", Implementability.IMPLEMENTABLE),
    CWE_126 ("Buffer Over-read", Implementability.IMPLEMENTABLE),
    CWE_127 ("Buffer Under-read", Implementability.IMPLEMENTABLE),
    CWE_128 ("Wrap-around Error", Implementability.IMPLEMENTABLE),
    CWE_129 ("Improper Validation of Array Index", Implementability.IMPLEMENTABLE),
    CWE_130 ("Improper Handling of Length Parameter Inconsistency", Implementability.IMPLEMENTABLE),
    CWE_131 ("Incorrect Calculation of Buffer Size", Implementability.IMPLEMENTABLE),
    CWE_134 ("Use of Externally-Controlled Format String", Implementability.IMPLEMENTABLE),
    CWE_135 ("Incorrect Calculation of Multi-Byte String Length", Implementability.IMPLEMENTABLE),
    CWE_138 ("Improper Neutralization of Special Elements", Implementability.IMPLEMENTABLE),
    CWE_140 ("Improper Neutralization of Delimiters", Implementability.IMPLEMENTABLE),
    CWE_141 ("Improper Neutralization of Parameter/Argument Delimiters", Implementability.IMPLEMENTABLE),
    CWE_142 ("Improper Neutralization of Value Delimiters", Implementability.IMPLEMENTABLE),
    CWE_143 ("Improper Neutralization of Record Delimiters", Implementability.IMPLEMENTABLE),
    CWE_144 ("Improper Neutralization of Line Delimiters", Implementability.IMPLEMENTABLE),
    CWE_145 ("Improper Neutralization of Section Delimiters", Implementability.IMPLEMENTABLE),
    CWE_146 ("Improper Neutralization of Expression/Command Delimiters", Implementability.IMPLEMENTABLE),
    CWE_147 ("Improper Neutralization of Input Terminators", Implementability.IMPLEMENTABLE),
    CWE_148 ("Improper Neutralization of Input Leaders", Implementability.IMPLEMENTABLE),
    CWE_149 ("Improper Neutralization of Quoting Syntax", Implementability.IMPLEMENTABLE),
    CWE_150 ("Improper Neutralization of Escape, Meta, or Control Sequences", Implementability.IMPLEMENTABLE),
    CWE_151 ("Improper Neutralization of Comment Delimiters", Implementability.IMPLEMENTABLE),
    CWE_152 ("Improper Neutralization of Macro Symbols", Implementability.IMPLEMENTABLE),
    CWE_153 ("Improper Neutralization of Substitution Characters", Implementability.IMPLEMENTABLE),
    CWE_154 ("Improper Neutralization of Variable Name Delimiters", Implementability.IMPLEMENTABLE),
    CWE_155 ("Improper Neutralization of Wildcards or Matching Symbols", Implementability.IMPLEMENTABLE),
    CWE_156 ("Improper Neutralization of Whitespace", Implementability.IMPLEMENTABLE),
    CWE_157 ("Failure to Sanitize Paired Delimiters", Implementability.IMPLEMENTABLE),
    CWE_158 ("Improper Neutralization of Null Byte or NUL Character", Implementability.IMPLEMENTABLE),
    CWE_159 ("Failure to Sanitize Special Element", Implementability.IMPLEMENTABLE),
    CWE_160 ("Improper Neutralization of Leading Special Elements", Implementability.IMPLEMENTABLE),
    CWE_161 ("Improper Neutralization of Multiple Leading Special Elements", Implementability.IMPLEMENTABLE),
    CWE_162 ("Improper Neutralization of Trailing Special Elements", Implementability.IMPLEMENTABLE),
    CWE_163 ("Improper Neutralization of Multiple Trailing Special Elements", Implementability.IMPLEMENTABLE),
    CWE_164 ("Improper Neutralization of Internal Special Elements", Implementability.IMPLEMENTABLE),
    CWE_165 ("Improper Neutralization of Multiple Internal Special Elements", Implementability.IMPLEMENTABLE),
    CWE_166 ("Improper Handling of Missing Special Element", Implementability.IMPLEMENTABLE),
    CWE_167 ("Improper Handling of Additional Special Element", Implementability.IMPLEMENTABLE),
    CWE_168 ("Improper Handling of Inconsistent Special Elements", Implementability.IMPLEMENTABLE),
    CWE_170 ("Improper Null Termination", Implementability.IMPLEMENTABLE),
    CWE_171 ("Cleansing, Canonicalization, and Comparison Errors", Implementability.IMPLEMENTABLE),
    CWE_172 ("Encoding Error", Implementability.IMPLEMENTABLE),
    CWE_173 ("Improper Handling of Alternate Encoding", Implementability.IMPLEMENTABLE),
    CWE_174 ("Double Decoding of the Same Data", Implementability.IMPLEMENTABLE),
    CWE_175 ("Improper Handling of Mixed Encoding", Implementability.IMPLEMENTABLE),
    CWE_176 ("Improper Handling of Unicode Encoding", Implementability.IMPLEMENTABLE),
    CWE_177 ("Improper Handling of URL Encoding (Hex Encoding)", Implementability.IMPLEMENTABLE),
    CWE_178 ("Improper Handling of Case Sensitivity", Implementability.IMPLEMENTABLE),
    CWE_179 ("Incorrect Behavior Order: Early Validation", Implementability.IMPLEMENTABLE),
    CWE_180 ("Incorrect Behavior Order: Validate Before Canonicalize", Implementability.IMPLEMENTABLE),
    CWE_181 ("Incorrect Behavior Order: Validate Before Filter", Implementability.IMPLEMENTABLE),
    CWE_182 ("Collapse of Data into Unsafe Value", Implementability.IMPLEMENTABLE),
    CWE_183 ("Permissive Whitelist", Implementability.IMPLEMENTABLE),
    CWE_184 ("Incomplete Blacklist", Implementability.IMPLEMENTABLE),
    CWE_185 ("Incorrect Regular Expression", Implementability.IMPLEMENTABLE),
    CWE_186 ("Overly Restrictive Regular Expression", Implementability.IMPLEMENTABLE),
    CWE_187 ("Partial Comparison", Implementability.IMPLEMENTABLE),
    CWE_188 ("Reliance on Data/Memory Layout", Implementability.IMPLEMENTABLE),
    CWE_190 ("Integer Overflow or Wraparound", Implementability.IMPLEMENTABLE),
    CWE_191 ("Integer Underflow (Wrap or Wraparound)", Implementability.IMPLEMENTABLE),
    CWE_192 ("Integer Coercion Error", Implementability.IMPLEMENTABLE),
    CWE_193 ("Off-by-one Error", Implementability.IMPLEMENTABLE),
    CWE_194 ("Unexpected Sign Extension", Implementability.IMPLEMENTABLE),
    CWE_195 ("Signed to Unsigned Conversion Error", Implementability.IMPLEMENTABLE),
    CWE_196 ("Unsigned to Signed Conversion Error", Implementability.IMPLEMENTABLE),
    CWE_197 ("Numeric Truncation Error", Implementability.IMPLEMENTABLE),
    CWE_198 ("Use of Incorrect Byte Ordering", Implementability.IMPLEMENTABLE),
    CWE_200 ("Information Exposure", Implementability.IMPLEMENTABLE),
    CWE_201 ("Information Exposure Through Sent Data", Implementability.IMPLEMENTABLE),
    CWE_202 ("Exposure of Sensitive Data Through Data Queries", Implementability.IMPLEMENTABLE),
    CWE_203 ("Information Exposure Through Discrepancy", Implementability.IMPLEMENTABLE),
    CWE_204 ("Response Discrepancy Information Exposure", Implementability.IMPLEMENTABLE),
    CWE_205 ("Information Exposure Through Behavioral Discrepancy", Implementability.IMPLEMENTABLE),
    CWE_206 ("Information Exposure of Internal State Through Behavioral Inconsistency", Implementability.IMPLEMENTABLE),
    CWE_207 ("Information Exposure Through an External Behavioral Inconsistency", Implementability.IMPLEMENTABLE),
    CWE_208 ("Information Exposure Through Timing Discrepancy", Implementability.IMPLEMENTABLE),
    CWE_209 ("Information Exposure Through an Error Message", Implementability.IMPLEMENTABLE),
    CWE_210 ("Information Exposure Through Self-generated Error Message", Implementability.IMPLEMENTABLE),
    CWE_211 ("Information Exposure Through Externally-generated Error Message", Implementability.IMPLEMENTABLE),
    CWE_212 ("Improper Cross-boundary Removal of Sensitive Data", Implementability.IMPLEMENTABLE),
    CWE_213 ("Intentional Information Exposure", Implementability.IMPLEMENTABLE),
    CWE_214 ("Information Exposure Through Process Environment", Implementability.IMPLEMENTABLE),
    CWE_215 ("Information Exposure Through Debug Information", Implementability.IMPLEMENTABLE),
    CWE_216 ("Containment Errors (Container Errors)", Implementability.IMPLEMENTABLE),
    CWE_219 ("Sensitive Data Under Web Root", Implementability.IMPLEMENTABLE),
    CWE_220 ("Sensitive Data Under FTP Root", Implementability.IMPLEMENTABLE),
    CWE_221 ("Information Loss or Omission", Implementability.IMPLEMENTABLE),
    CWE_222 ("Truncation of Security-relevant Information", Implementability.IMPLEMENTABLE),
    CWE_223 ("Omission of Security-relevant Information", Implementability.IMPLEMENTABLE),
    CWE_224 ("Obscured Security-relevant Information by Alternate Name", Implementability.IMPLEMENTABLE),
    CWE_226 ("Sensitive Information Uncleared Before Release", Implementability.IMPLEMENTABLE),
    CWE_227 ("Improper Fulfillment of API Contract ('API Abuse')", Implementability.IMPLEMENTABLE),
    CWE_228 ("Improper Handling of Syntactically Invalid Structure", Implementability.IMPLEMENTABLE),
    CWE_229 ("Improper Handling of Values", Implementability.IMPLEMENTABLE),
    CWE_230 ("Improper Handling of Missing Values", Implementability.IMPLEMENTABLE),
    CWE_231 ("Improper Handling of Extra Values", Implementability.IMPLEMENTABLE),
    CWE_232 ("Improper Handling of Undefined Values", Implementability.IMPLEMENTABLE),
    CWE_233 ("Improper Handling of Parameters", Implementability.IMPLEMENTABLE),
    CWE_234 ("Failure to Handle Missing Parameter", Implementability.IMPLEMENTABLE),
    CWE_235 ("Improper Handling of Extra Parameters", Implementability.IMPLEMENTABLE),
    CWE_236 ("Improper Handling of Undefined Parameters", Implementability.IMPLEMENTABLE),
    CWE_237 ("Improper Handling of Structural Elements", Implementability.IMPLEMENTABLE),
    CWE_238 ("Improper Handling of Incomplete Structural Elements", Implementability.IMPLEMENTABLE),
    CWE_239 ("Failure to Handle Incomplete Element", Implementability.IMPLEMENTABLE),
    CWE_240 ("Improper Handling of Inconsistent Structural Elements", Implementability.IMPLEMENTABLE),
    CWE_241 ("Improper Handling of Unexpected Data Type", Implementability.IMPLEMENTABLE),
    CWE_242 ("Use of Inherently Dangerous Function", Implementability.IMPLEMENTABLE),
    CWE_243 ("Creation of chroot Jail Without Changing Working Directory", Implementability.IMPLEMENTABLE),
    CWE_244 ("Improper Clearing of Heap Memory Before Release ('Heap Inspection')", Implementability.IMPLEMENTABLE),
    CWE_245 ("J2EE Bad Practices: Direct Management of Connections", Implementability.IMPLEMENTABLE),
    CWE_246 ("J2EE Bad Practices: Direct Use of Sockets", Implementability.IMPLEMENTABLE),
    CWE_248 ("Uncaught Exception", Implementability.IMPLEMENTABLE),
    CWE_250 ("Execution with Unnecessary Privileges", Implementability.IMPLEMENTABLE),
    CWE_252 ("Unchecked Return Value", Implementability.IMPLEMENTABLE),
    CWE_253 ("Incorrect Check of Function Return Value", Implementability.IMPLEMENTABLE),
    CWE_256 ("Plaintext Storage of a Password", Implementability.IMPLEMENTABLE),
    CWE_257 ("Storing Passwords in a Recoverable Format", Implementability.IMPLEMENTABLE),
    CWE_258 ("Empty Password in Configuration File", Implementability.IMPLEMENTABLE),
    CWE_259 ("Use of Hard-coded Password", Implementability.IMPLEMENTABLE),
    CWE_260 ("Password in Configuration File", Implementability.IMPLEMENTABLE),
    CWE_261 ("Weak Cryptography for Passwords", Implementability.IMPLEMENTABLE),
    CWE_262 ("Not Using Password Aging", Implementability.IMPLEMENTABLE),
    CWE_263 ("Password Aging with Long Expiration", Implementability.IMPLEMENTABLE),
    CWE_264 ("Permissions, Privileges, and Access Controls", Implementability.IMPLEMENTABLE),
    CWE_265 ("Privilege / Sandbox Issues", Implementability.IMPLEMENTABLE),
    CWE_266 ("Incorrect Privilege Assignment", Implementability.IMPLEMENTABLE),
    CWE_267 ("Privilege Defined With Unsafe Actions", Implementability.IMPLEMENTABLE),
    CWE_268 ("Privilege Chaining", Implementability.IMPLEMENTABLE),
    CWE_269 ("Improper Privilege Management", Implementability.IMPLEMENTABLE),
    CWE_270 ("Privilege Context Switching Error", Implementability.IMPLEMENTABLE),
    CWE_271 ("Privilege Dropping / Lowering Errors", Implementability.IMPLEMENTABLE),
    CWE_272 ("Least Privilege Violation", Implementability.IMPLEMENTABLE),
    CWE_273 ("Improper Check for Dropped Privileges", Implementability.IMPLEMENTABLE),
    CWE_274 ("Improper Handling of Insufficient Privileges", Implementability.IMPLEMENTABLE),
    CWE_275 ("Permission Issues", Implementability.IMPLEMENTABLE),
    CWE_276 ("Incorrect Default Permissions", Implementability.IMPLEMENTABLE),
    CWE_277 ("Insecure Inherited Permissions", Implementability.IMPLEMENTABLE),
    CWE_278 ("Insecure Preserved Inherited Permissions", Implementability.IMPLEMENTABLE),
    CWE_279 ("Incorrect Execution-Assigned Permissions", Implementability.IMPLEMENTABLE),
    CWE_280 ("Improper Handling of Insufficient Permissions or Privileges", Implementability.IMPLEMENTABLE),
    CWE_281 ("Improper Preservation of Permissions", Implementability.IMPLEMENTABLE),
    CWE_282 ("Improper Ownership Management", Implementability.IMPLEMENTABLE),
    CWE_283 ("Unverified Ownership", Implementability.IMPLEMENTABLE),
    CWE_284 ("Improper Access Control", Implementability.IMPLEMENTABLE),
    CWE_285 ("Improper Authorization", Implementability.IMPLEMENTABLE),
    CWE_286 ("Incorrect User Management", Implementability.IMPLEMENTABLE),
    CWE_287 ("Improper Authentication", Implementability.IMPLEMENTABLE),
    CWE_288 ("Authentication Bypass Using an Alternate Path or Channel", Implementability.IMPLEMENTABLE),
    CWE_289 ("Authentication Bypass by Alternate Name", Implementability.IMPLEMENTABLE),
    CWE_290 ("Authentication Bypass by Spoofing", Implementability.IMPLEMENTABLE),
    CWE_291 ("Reliance on IP Address for Authentication", Implementability.IMPLEMENTABLE),
    CWE_293 ("Using Referer Field for Authentication", Implementability.IMPLEMENTABLE),
    CWE_294 ("Authentication Bypass by Capture-replay", Implementability.IMPLEMENTABLE),
    CWE_295 ("Improper Certificate Validation", Implementability.IMPLEMENTABLE),
    CWE_296 ("Improper Following of a Certificate's Chain of Trust", Implementability.IMPLEMENTABLE),
    CWE_297 ("Improper Validation of Certificate with Host Mismatch", Implementability.IMPLEMENTABLE),
    CWE_298 ("Improper Validation of Certificate Expiration", Implementability.IMPLEMENTABLE),
    CWE_299 ("Improper Check for Certificate Revocation", Implementability.IMPLEMENTABLE),
    CWE_300 ("Channel Accessible by Non-Endpoint ('Man-in-the-Middle')", Implementability.IMPLEMENTABLE),
    CWE_301 ("Reflection Attack in an Authentication Protocol", Implementability.IMPLEMENTABLE),
    CWE_302 ("Authentication Bypass by Assumed-Immutable Data", Implementability.IMPLEMENTABLE),
    CWE_303 ("Incorrect Implementation of Authentication Algorithm", Implementability.IMPLEMENTABLE),
    CWE_304 ("Missing Critical Step in Authentication", Implementability.IMPLEMENTABLE),
    CWE_305 ("Authentication Bypass by Primary Weakness", Implementability.IMPLEMENTABLE),
    CWE_306 ("Missing Authentication for Critical Function", Implementability.IMPLEMENTABLE),
    CWE_307 ("Improper Restriction of Excessive Authentication Attempts", Implementability.IMPLEMENTABLE),
    CWE_308 ("Use of Single-factor Authentication", Implementability.IMPLEMENTABLE),
    CWE_309 ("Use of Password System for Primary Authentication", Implementability.IMPLEMENTABLE),
    CWE_311 ("Missing Encryption of Sensitive Data", Implementability.IMPLEMENTABLE),
    CWE_312 ("Cleartext Storage of Sensitive Information", Implementability.IMPLEMENTABLE),
    CWE_313 ("Cleartext Storage in a File or on Disk", Implementability.IMPLEMENTABLE),
    CWE_314 ("Cleartext Storage in the Registry", Implementability.IMPLEMENTABLE),
    CWE_315 ("Cleartext Storage of Sensitive Information in a Cookie", Implementability.IMPLEMENTABLE),
    CWE_316 ("Cleartext Storage of Sensitive Information in Memory", Implementability.IMPLEMENTABLE),
    CWE_317 ("Cleartext Storage of Sensitive Information in GUI", Implementability.IMPLEMENTABLE),
    CWE_318 ("Cleartext Storage of Sensitive Information in Executable", Implementability.IMPLEMENTABLE),
    CWE_319 ("Cleartext Transmission of Sensitive Information", Implementability.IMPLEMENTABLE),
    CWE_321 ("Use of Hard-coded Cryptographic Key", Implementability.IMPLEMENTABLE),
    CWE_322 ("Key Exchange without Entity Authentication", Implementability.IMPLEMENTABLE),
    CWE_323 ("Reusing a Nonce, Key Pair in Encryption", Implementability.IMPLEMENTABLE),
    CWE_324 ("Use of a Key Past its Expiration Date", Implementability.IMPLEMENTABLE),
    CWE_325 ("Missing Required Cryptographic Step", Implementability.IMPLEMENTABLE),
    CWE_326 ("Inadequate Encryption Strength", Implementability.IMPLEMENTABLE),
    CWE_327 ("Use of a Broken or Risky Cryptographic Algorithm", Implementability.IMPLEMENTABLE),
    CWE_328 ("Reversible One-Way Hash", Implementability.IMPLEMENTABLE),
    CWE_329 ("Not Using a Random IV with CBC Mode", Implementability.IMPLEMENTABLE),
    CWE_330 ("Use of Insufficiently Random Values", Implementability.IMPLEMENTABLE),
    CWE_331 ("Insufficient Entropy", Implementability.IMPLEMENTABLE),
    CWE_332 ("Insufficient Entropy in PRNG", Implementability.IMPLEMENTABLE),
    CWE_333 ("Improper Handling of Insufficient Entropy in TRNG", Implementability.IMPLEMENTABLE),
    CWE_334 ("Small Space of Random Values", Implementability.IMPLEMENTABLE),
    CWE_335 ("PRNG Seed Error", Implementability.IMPLEMENTABLE),
    CWE_336 ("Same Seed in PRNG", Implementability.IMPLEMENTABLE),
    CWE_337 ("Predictable Seed in PRNG", Implementability.IMPLEMENTABLE),
    CWE_338 ("Use of Cryptographically Weak Pseudo-Random Number Generator (PRNG)", Implementability.IMPLEMENTABLE),
    CWE_339 ("Small Seed Space in PRNG", Implementability.IMPLEMENTABLE),
    CWE_340 ("Predictability Problems", Implementability.IMPLEMENTABLE),
    CWE_341 ("Predictable from Observable State", Implementability.IMPLEMENTABLE),
    CWE_342 ("Predictable Exact Value from Previous Values", Implementability.IMPLEMENTABLE),
    CWE_343 ("Predictable Value Range from Previous Values", Implementability.IMPLEMENTABLE),
    CWE_344 ("Use of Invariant Value in Dynamically Changing Context", Implementability.IMPLEMENTABLE),
    CWE_345 ("Insufficient Verification of Data Authenticity", Implementability.IMPLEMENTABLE),
    CWE_346 ("Origin Validation Error", Implementability.IMPLEMENTABLE),
    CWE_347 ("Improper Verification of Cryptographic Signature", Implementability.IMPLEMENTABLE),
    CWE_348 ("Use of Less Trusted Source", Implementability.IMPLEMENTABLE),
    CWE_349 ("Acceptance of Extraneous Untrusted Data With Trusted Data", Implementability.IMPLEMENTABLE),
    CWE_350 ("Reliance on Reverse DNS Resolution for a Security-Critical Action", Implementability.IMPLEMENTABLE),
    CWE_351 ("Insufficient Type Distinction", Implementability.IMPLEMENTABLE),
    CWE_352 ("Composite Cross-Site Request Forgery (CSRF)", Implementability.IMPLEMENTABLE),
    CWE_353 ("Missing Support for Integrity Check", Implementability.IMPLEMENTABLE),
    CWE_354 ("Improper Validation of Integrity Check Value", Implementability.IMPLEMENTABLE),
    CWE_356 ("Product UI does not Warn User of Unsafe Actions", Implementability.IMPLEMENTABLE),
    CWE_357 ("Insufficient UI Warning of Dangerous Operations", Implementability.IMPLEMENTABLE),
    CWE_358 ("Improperly Implemented Security Check for Standard", Implementability.IMPLEMENTABLE),
    CWE_359 ("Exposure of Private Information ('Privacy Violation')", Implementability.IMPLEMENTABLE),
    CWE_360 ("Trust of System Event Data", Implementability.IMPLEMENTABLE),
    CWE_362 ("Concurrent Execution using Shared Resource with Improper Synchronization ('Race Condition')", Implementability.IMPLEMENTABLE),
    CWE_363 ("Race Condition Enabling Link Following", Implementability.IMPLEMENTABLE),
    CWE_364 ("Signal Handler Race Condition", Implementability.IMPLEMENTABLE),
    CWE_365 ("Race Condition in Switch", Implementability.IMPLEMENTABLE),
    CWE_366 ("Race Condition within a Thread", Implementability.IMPLEMENTABLE),
    CWE_367 ("Time-of-check Time-of-use (TOCTOU) Race Condition", Implementability.IMPLEMENTABLE),
    CWE_368 ("Context Switching Race Condition", Implementability.IMPLEMENTABLE),
    CWE_369 ("Divide By Zero", Implementability.IMPLEMENTABLE),
    CWE_370 ("Missing Check for Certificate Revocation after Initial Check", Implementability.IMPLEMENTABLE),
    CWE_371 ("State Issues", Implementability.IMPLEMENTABLE),
    CWE_372 ("Incomplete Internal State Distinction", Implementability.IMPLEMENTABLE),
    CWE_374 ("Passing Mutable Objects to an Untrusted Method", Implementability.IMPLEMENTABLE),
    CWE_375 ("Returning a Mutable Object to an Untrusted Caller", Implementability.IMPLEMENTABLE),
    CWE_377 ("Insecure Temporary File", Implementability.IMPLEMENTABLE),
    CWE_378 ("Creation of Temporary File With Insecure Permissions", Implementability.IMPLEMENTABLE),
    CWE_379 ("Creation of Temporary File in Directory with Incorrect Permissions", Implementability.IMPLEMENTABLE),
    CWE_382 ("J2EE Bad Practices: Use of System.exit()", Implementability.IMPLEMENTABLE),
    CWE_383 ("J2EE Bad Practices: Direct Use of Threads", Implementability.IMPLEMENTABLE),
    CWE_384 ("Composite Session Fixation", Implementability.IMPLEMENTABLE),
    CWE_385 ("Covert Timing Channel", Implementability.IMPLEMENTABLE),
    CWE_386 ("Symbolic Name not Mapping to Correct Object", Implementability.IMPLEMENTABLE),
    CWE_388 ("Error Handling", Implementability.IMPLEMENTABLE),
    CWE_390 ("Detection of Error Condition Without Action", Implementability.IMPLEMENTABLE),
    CWE_391 ("Unchecked Error Condition", Implementability.IMPLEMENTABLE),
    CWE_392 ("Missing Report of Error Condition", Implementability.IMPLEMENTABLE),
    CWE_393 ("Return of Wrong Status Code", Implementability.IMPLEMENTABLE),
    CWE_394 ("Unexpected Status Code or Return Value", Implementability.IMPLEMENTABLE),
    CWE_395 ("Use of NullPointerException Catch to Detect NULL Pointer Dereference", Implementability.IMPLEMENTABLE),
    CWE_396 ("Declaration of Catch for Generic Exception", Implementability.IMPLEMENTABLE),
    CWE_397 ("Declaration of Throws for Generic Exception", Implementability.IMPLEMENTABLE),
    CWE_398 ("Indicator of Poor Code Quality", Implementability.IMPLEMENTABLE),
    CWE_400 ("Uncontrolled Resource Consumption ('Resource Exhaustion')", Implementability.IMPLEMENTABLE),
    CWE_401 ("Improper Release of Memory Before Removing Last Reference ('Memory Leak')", Implementability.IMPLEMENTABLE),
    CWE_402 ("Transmission of Private Resources into a New Sphere ('Resource Leak')", Implementability.IMPLEMENTABLE),
    CWE_403 ("Exposure of File Descriptor to Unintended Control Sphere ('File Descriptor Leak')", Implementability.IMPLEMENTABLE),
    CWE_404 ("Improper Resource Shutdown or Release", Implementability.IMPLEMENTABLE),
    CWE_405 ("Asymmetric Resource Consumption (Amplification)", Implementability.IMPLEMENTABLE),
    CWE_406 ("Insufficient Control of Network Message Volume (Network Amplification)", Implementability.IMPLEMENTABLE),
    CWE_407 ("Algorithmic Complexity", Implementability.IMPLEMENTABLE),
    CWE_408 ("Incorrect Behavior Order: Early Amplification", Implementability.IMPLEMENTABLE),
    CWE_409 ("Improper Handling of Highly Compressed Data (Data Amplification)", Implementability.IMPLEMENTABLE),
    CWE_410 ("Insufficient Resource Pool", Implementability.IMPLEMENTABLE),
    CWE_412 ("Unrestricted Externally Accessible Lock", Implementability.IMPLEMENTABLE),
    CWE_413 ("Improper Resource Locking", Implementability.IMPLEMENTABLE),
    CWE_414 ("Missing Lock Check", Implementability.IMPLEMENTABLE),
    CWE_415 ("Double Free", Implementability.IMPLEMENTABLE),
    CWE_416 ("Use After Free", Implementability.IMPLEMENTABLE),
    CWE_419 ("Unprotected Primary Channel", Implementability.IMPLEMENTABLE),
    CWE_420 ("Unprotected Alternate Channel", Implementability.IMPLEMENTABLE),
    CWE_421 ("Race Condition During Access to Alternate Channel", Implementability.IMPLEMENTABLE),
    CWE_422 ("Unprotected Windows Messaging Channel ('Shatter')", Implementability.IMPLEMENTABLE),
    CWE_424 ("Improper Protection of Alternate Path", Implementability.IMPLEMENTABLE),
    CWE_425 ("Direct Request ('Forced Browsing')", Implementability.IMPLEMENTABLE),
    CWE_426 ("Composite Untrusted Search Path", Implementability.IMPLEMENTABLE),
    CWE_427 ("Uncontrolled Search Path Element", Implementability.IMPLEMENTABLE),
    CWE_428 ("Unquoted Search Path or Element", Implementability.IMPLEMENTABLE),
    CWE_430 ("Deployment of Wrong Handler", Implementability.IMPLEMENTABLE),
    CWE_431 ("Missing Handler", Implementability.IMPLEMENTABLE),
    CWE_432 ("Dangerous Signal Handler not Disabled During Sensitive Operations", Implementability.IMPLEMENTABLE),
    CWE_433 ("Unparsed Raw Web Content Delivery", Implementability.IMPLEMENTABLE),
    CWE_434 ("Unrestricted Upload of File with Dangerous Type", Implementability.IMPLEMENTABLE),
    CWE_435 ("Interaction Error", Implementability.IMPLEMENTABLE),
    CWE_436 ("Interpretation Conflict", Implementability.IMPLEMENTABLE),
    CWE_437 ("Incomplete Model of Endpoint Features", Implementability.IMPLEMENTABLE),
    CWE_439 ("Behavioral Change in New Version or Environment", Implementability.IMPLEMENTABLE),
    CWE_440 ("Expected Behavior Violation", Implementability.IMPLEMENTABLE),
    CWE_441 ("Unintended Proxy or Intermediary ('Confused Deputy')", Implementability.IMPLEMENTABLE),
    CWE_444 ("Inconsistent Interpretation of HTTP Requests ('HTTP Request Smuggling')", Implementability.IMPLEMENTABLE),
    CWE_446 ("UI Discrepancy for Security Feature", Implementability.IMPLEMENTABLE),
    CWE_447 ("Unimplemented or Unsupported Feature in UI", Implementability.IMPLEMENTABLE),
    CWE_448 ("Obsolete Feature in UI", Implementability.IMPLEMENTABLE),
    CWE_449 ("The UI Performs the Wrong Action", Implementability.IMPLEMENTABLE),
    CWE_450 ("Multiple Interpretations of UI Input", Implementability.IMPLEMENTABLE),
    CWE_451 ("User Interface (UI) Misrepresentation of Critical Information", Implementability.IMPLEMENTABLE),
    CWE_453 ("Insecure Default Variable Initialization", Implementability.IMPLEMENTABLE),
    CWE_454 ("External Initialization of Trusted Variables or Data Stores", Implementability.IMPLEMENTABLE),
    CWE_455 ("Non-exit on Failed Initialization", Implementability.IMPLEMENTABLE),
    CWE_456 ("Missing Initialization of a Variable", Implementability.IMPLEMENTABLE),
    CWE_457 ("Use of Uninitialized Variable", Implementability.IMPLEMENTABLE),
    CWE_459 ("Incomplete Cleanup", Implementability.IMPLEMENTABLE),
    CWE_460 ("Improper Cleanup on Thrown Exception", Implementability.IMPLEMENTABLE),
    CWE_462 ("Duplicate Key in Associative List (Alist)", Implementability.IMPLEMENTABLE),
    CWE_463 ("Deletion of Data Structure Sentinel", Implementability.IMPLEMENTABLE),
    CWE_464 ("Addition of Data Structure Sentinel", Implementability.IMPLEMENTABLE),
    CWE_466 ("Return of Pointer Value Outside of Expected Range", Implementability.IMPLEMENTABLE),
    CWE_467 ("Use of sizeof() on a Pointer Type", Implementability.IMPLEMENTABLE),
    CWE_468 ("Incorrect Pointer Scaling", Implementability.IMPLEMENTABLE),
    CWE_469 ("Use of Pointer Subtraction to Determine Size", Implementability.IMPLEMENTABLE),
    CWE_470 ("Use of Externally-Controlled Input to Select Classes or Code ('Unsafe Reflection')", Implementability.IMPLEMENTABLE),
    CWE_471 ("Modification of Assumed-Immutable Data (MAID)", Implementability.IMPLEMENTABLE),
    CWE_472 ("External Control of Assumed-Immutable Web Parameter", Implementability.IMPLEMENTABLE),
    CWE_473 ("PHP External Variable Modification", Implementability.IMPLEMENTABLE),
    CWE_474 ("Use of Function with Inconsistent Implementations", Implementability.IMPLEMENTABLE),
    CWE_475 ("Undefined Behavior for Input to API", Implementability.IMPLEMENTABLE),
    CWE_476 ("NULL Pointer Dereference", Implementability.IMPLEMENTABLE),
    CWE_477 ("Use of Obsolete Functions", Implementability.IMPLEMENTABLE),
    CWE_478 ("Missing Default Case in Switch Statement", Implementability.IMPLEMENTABLE),
    CWE_479 ("Signal Handler Use of a Non-reentrant Function", Implementability.IMPLEMENTABLE),
    CWE_480 ("Use of Incorrect Operator", Implementability.IMPLEMENTABLE),
    CWE_481 ("Assigning instead of Comparing", Implementability.IMPLEMENTABLE),
    CWE_482 ("Comparing instead of Assigning", Implementability.IMPLEMENTABLE),
    CWE_483 ("Incorrect Block Delimitation", Implementability.IMPLEMENTABLE),
    CWE_484 ("Omitted Break Statement in Switch", Implementability.IMPLEMENTABLE),
    CWE_485 ("Insufficient Encapsulation", Implementability.IMPLEMENTABLE),
    CWE_486 ("Comparison of Classes by Name", Implementability.IMPLEMENTABLE),
    CWE_487 ("Reliance on Package-level Scope", Implementability.IMPLEMENTABLE),
    CWE_488 ("Exposure of Data Element to Wrong Session", Implementability.IMPLEMENTABLE),
    CWE_489 ("Leftover Debug Code", Implementability.IMPLEMENTABLE),
    CWE_491 ("Public cloneable() Method Without Final ('Object Hijack')", Implementability.IMPLEMENTABLE),
    CWE_492 ("Use of Inner Class Containing Sensitive Data", Implementability.IMPLEMENTABLE),
    CWE_493 ("Critical Public Variable Without Final Modifier", Implementability.IMPLEMENTABLE),
    CWE_494 ("Download of Code Without Integrity Check", Implementability.IMPLEMENTABLE),
    CWE_495 ("Private Array-Typed Field Returned From A Public Method", Implementability.IMPLEMENTABLE),
    CWE_496 ("Public Data Assigned to Private Array-Typed Field", Implementability.IMPLEMENTABLE),
    CWE_497 ("Exposure of System Data to an Unauthorized Control Sphere", Implementability.IMPLEMENTABLE),
    CWE_498 ("Cloneable Class Containing Sensitive Information", Implementability.IMPLEMENTABLE),
    CWE_499 ("Serializable Class Containing Sensitive Data", Implementability.IMPLEMENTABLE),
    CWE_500 ("Public Static Field Not Marked Final", Implementability.IMPLEMENTABLE),
    CWE_501 ("Trust Boundary Violation", Implementability.IMPLEMENTABLE),
    CWE_502 ("Deserialization of Untrusted Data", Implementability.IMPLEMENTABLE),
    CWE_506 ("Embedded Malicious Code", Implementability.IMPLEMENTABLE),
    CWE_507 ("Trojan Horse", Implementability.IMPLEMENTABLE),
    CWE_508 ("Non-Replicating Malicious Code", Implementability.IMPLEMENTABLE),
    CWE_509 ("Replicating Malicious Code (Virus or Worm)", Implementability.IMPLEMENTABLE),
    CWE_510 ("Trapdoor", Implementability.IMPLEMENTABLE),
    CWE_511 ("Logic/Time Bomb", Implementability.IMPLEMENTABLE),
    CWE_512 ("Spyware", Implementability.IMPLEMENTABLE),
    CWE_514 ("Covert Channel", Implementability.IMPLEMENTABLE),
    CWE_515 ("Covert Storage Channel", Implementability.IMPLEMENTABLE),
    CWE_520 (".NET Misconfiguration: Use of Impersonation", Implementability.IMPLEMENTABLE),
    CWE_521 ("Weak Password Requirements", Implementability.IMPLEMENTABLE),
    CWE_522 ("Insufficiently Protected Credentials", Implementability.IMPLEMENTABLE),
    CWE_523 ("Unprotected Transport of Credentials", Implementability.IMPLEMENTABLE),
    CWE_524 ("Information Exposure Through Caching", Implementability.IMPLEMENTABLE),
    CWE_525 ("Information Exposure Through Browser Caching", Implementability.IMPLEMENTABLE),
    CWE_526 ("Information Exposure Through Environmental Variables", Implementability.IMPLEMENTABLE),
    CWE_527 ("Exposure of CVS Repository to an Unauthorized Control Sphere", Implementability.IMPLEMENTABLE),
    CWE_528 ("Exposure of Core Dump File to an Unauthorized Control Sphere", Implementability.IMPLEMENTABLE),
    CWE_529 ("Exposure of Access Control List Files to an Unauthorized Control Sphere", Implementability.IMPLEMENTABLE),
    CWE_530 ("Exposure of Backup File to an Unauthorized Control Sphere", Implementability.IMPLEMENTABLE),
    CWE_531 ("Information Exposure Through Test Code", Implementability.IMPLEMENTABLE),
    CWE_532 ("Information Exposure Through Log Files", Implementability.IMPLEMENTABLE),
    CWE_533 ("Information Exposure Through Server Log Files", Implementability.IMPLEMENTABLE),
    CWE_534 ("Information Exposure Through Debug Log Files", Implementability.IMPLEMENTABLE),
    CWE_535 ("Information Exposure Through Shell Error Message", Implementability.IMPLEMENTABLE),
    CWE_536 ("Information Exposure Through Servlet Runtime Error Message", Implementability.IMPLEMENTABLE),
    CWE_537 ("Information Exposure Through Java Runtime Error Message", Implementability.IMPLEMENTABLE),
    CWE_538 ("File and Directory Information Exposure", Implementability.IMPLEMENTABLE),
    CWE_539 ("Information Exposure Through Persistent Cookies", Implementability.IMPLEMENTABLE),
    CWE_540 ("Information Exposure Through Source Code", Implementability.IMPLEMENTABLE),
    CWE_541 ("Information Exposure Through Include Source Code", Implementability.IMPLEMENTABLE),
    CWE_542 ("Information Exposure Through Cleanup Log Files", Implementability.IMPLEMENTABLE),
    CWE_543 ("Use of Singleton Pattern Without Synchronization in a Multithreaded Context", Implementability.IMPLEMENTABLE),
    CWE_544 ("Missing Standardized Error Handling Mechanism", Implementability.IMPLEMENTABLE),
    CWE_545 ("Use of Dynamic Class Loading", Implementability.IMPLEMENTABLE),
    CWE_546 ("Suspicious Comment", Implementability.IMPLEMENTABLE),
    CWE_547 ("Use of Hard-coded, Security-relevant Constants", Implementability.IMPLEMENTABLE),
    CWE_548 ("Information Exposure Through Directory Listing", Implementability.IMPLEMENTABLE),
    CWE_549 ("Missing Password Field Masking", Implementability.IMPLEMENTABLE),
    CWE_550 ("Information Exposure Through Server Error Message", Implementability.IMPLEMENTABLE),
    CWE_551 ("Incorrect Behavior Order: Authorization Before Parsing and Canonicalization", Implementability.IMPLEMENTABLE),
    CWE_552 ("Files or Directories Accessible to External Parties", Implementability.IMPLEMENTABLE),
    CWE_553 ("Command Shell in Externally Accessible Directory", Implementability.IMPLEMENTABLE),
    CWE_554 ("ASP.NET Misconfiguration: Not Using Input Validation Framework", Implementability.IMPLEMENTABLE),
    CWE_555 ("J2EE Misconfiguration: Plaintext Password in Configuration File", Implementability.IMPLEMENTABLE),
    CWE_556 ("ASP.NET Misconfiguration: Use of Identity Impersonation", Implementability.IMPLEMENTABLE),
    CWE_557 ("Concurrency Issues", Implementability.IMPLEMENTABLE),
    CWE_558 ("Use of getlogin() in Multithreaded Application", Implementability.IMPLEMENTABLE),
    CWE_560 ("Use of umask() with chmod-style Argument", Implementability.IMPLEMENTABLE),
    CWE_561 ("Dead Code", Implementability.IMPLEMENTABLE),
    CWE_562 ("Return of Stack Variable Address", Implementability.IMPLEMENTABLE),
    CWE_563 ("Assignment to Variable without Use ('Unused Variable')", Implementability.IMPLEMENTABLE),
    CWE_564 ("SQL Injection: Hibernate", Implementability.IMPLEMENTABLE),
    CWE_565 ("Reliance on Cookies without Validation and Integrity Checking", Implementability.IMPLEMENTABLE),
    CWE_566 ("Authorization Bypass Through User-Controlled SQL Primary Key", Implementability.IMPLEMENTABLE),
    CWE_567 ("Unsynchronized Access to Shared Data in a Multithreaded Context", Implementability.IMPLEMENTABLE),
    CWE_568 ("finalize() Method Without super.finalize()", Implementability.IMPLEMENTABLE),
    CWE_570 ("Expression is Always False", Implementability.IMPLEMENTABLE),
    CWE_571 ("Expression is Always True", Implementability.IMPLEMENTABLE),
    CWE_572 ("Call to Thread run() instead of start()", Implementability.IMPLEMENTABLE),
    CWE_573 ("Improper Following of Specification by Caller", Implementability.IMPLEMENTABLE),
    CWE_574 ("EJB Bad Practices: Use of Synchronization Primitives", Implementability.IMPLEMENTABLE),
    CWE_575 ("EJB Bad Practices: Use of AWT Swing", Implementability.IMPLEMENTABLE),
    CWE_576 ("EJB Bad Practices: Use of Java I/O", Implementability.IMPLEMENTABLE),
    CWE_577 ("EJB Bad Practices: Use of Sockets", Implementability.IMPLEMENTABLE),
    CWE_578 ("EJB Bad Practices: Use of Class Loader", Implementability.IMPLEMENTABLE),
    CWE_579 ("J2EE Bad Practices: Non-serializable Object Stored in Session", Implementability.IMPLEMENTABLE),
    CWE_580 ("clone() Method Without super.clone()", Implementability.IMPLEMENTABLE),
    CWE_581 ("Object Model Violation: Just One of Equals and Hashcode Defined", Implementability.IMPLEMENTABLE),
    CWE_582 ("Array Declared Public, Final, and Static", Implementability.IMPLEMENTABLE),
    CWE_583 ("finalize() Method Declared Public", Implementability.IMPLEMENTABLE),
    CWE_584 ("Return Inside Finally Block", Implementability.IMPLEMENTABLE),
    CWE_585 ("Empty Synchronized Block", Implementability.IMPLEMENTABLE),
    CWE_586 ("Explicit Call to Finalize()", Implementability.IMPLEMENTABLE),
    CWE_587 ("Assignment of a Fixed Address to a Pointer", Implementability.IMPLEMENTABLE),
    CWE_588 ("Attempt to Access Child of a Non-structure Pointer", Implementability.IMPLEMENTABLE),
    CWE_589 ("Call to Non-ubiquitous API", Implementability.IMPLEMENTABLE),
    CWE_590 ("Free of Memory not on the Heap", Implementability.IMPLEMENTABLE),
    CWE_591 ("Sensitive Data Storage in Improperly Locked Memory", Implementability.IMPLEMENTABLE),
    CWE_592 ("Authentication Bypass Issues", Implementability.IMPLEMENTABLE),
    CWE_593 ("Authentication Bypass: OpenSSL CTX Object Modified after SSL Objects are Created", Implementability.IMPLEMENTABLE),
    CWE_594 ("J2EE Framework: Saving Unserializable Objects to Disk", Implementability.IMPLEMENTABLE),
    CWE_595 ("Comparison of Object References Instead of Object Contents", Implementability.IMPLEMENTABLE),
    CWE_596 ("Incorrect Semantic Object Comparison", Implementability.IMPLEMENTABLE),
    CWE_597 ("Use of Wrong Operator in String Comparison", Implementability.IMPLEMENTABLE),
    CWE_598 ("Information Exposure Through Query Strings in GET Request", Implementability.IMPLEMENTABLE),
    CWE_599 ("Missing Validation of OpenSSL Certificate", Implementability.IMPLEMENTABLE),
    CWE_600 ("Uncaught Exception in Servlet", Implementability.IMPLEMENTABLE),
    CWE_601 ("URL Redirection to Untrusted Site ('Open Redirect')", Implementability.IMPLEMENTABLE),
    CWE_602 ("Client-Side Enforcement of Server-Side Security", Implementability.IMPLEMENTABLE),
    CWE_603 ("Use of Client-Side Authentication", Implementability.IMPLEMENTABLE),
    CWE_605 ("Multiple Binds to the Same Port", Implementability.IMPLEMENTABLE),
    CWE_606 ("Unchecked Input for Loop Condition", Implementability.IMPLEMENTABLE),
    CWE_607 ("Public Static Final Field References Mutable Object", Implementability.IMPLEMENTABLE),
    CWE_608 ("Struts: Non-private Field in ActionForm Class", Implementability.IMPLEMENTABLE),
    CWE_609 ("Double-Checked Locking", Implementability.IMPLEMENTABLE),
    CWE_610 ("Externally Controlled Reference to a Resource in Another Sphere", Implementability.IMPLEMENTABLE),
    CWE_611 ("Improper Restriction of XML External Entity Reference ('XXE')", Implementability.IMPLEMENTABLE),
    CWE_612 ("Information Exposure Through Indexing of Private Data", Implementability.IMPLEMENTABLE),
    CWE_613 ("Insufficient Session Expiration", Implementability.IMPLEMENTABLE),
    CWE_614 ("Sensitive Cookie in HTTPS Session Without 'Secure' Attribute", Implementability.IMPLEMENTABLE),
    CWE_615 ("Information Exposure Through Comments", Implementability.IMPLEMENTABLE),
    CWE_616 ("Incomplete Identification of Uploaded File Variables (PHP)", Implementability.IMPLEMENTABLE),
    CWE_617 ("Reachable Assertion", Implementability.IMPLEMENTABLE),
    CWE_618 ("Exposed Unsafe ActiveX Method", Implementability.IMPLEMENTABLE),
    CWE_619 ("Dangling Database Cursor ('Cursor Injection')", Implementability.IMPLEMENTABLE),
    CWE_620 ("Unverified Password Change", Implementability.IMPLEMENTABLE),
    CWE_621 ("Variable Extraction Error", Implementability.IMPLEMENTABLE),
    CWE_622 ("Improper Validation of Function Hook Arguments", Implementability.IMPLEMENTABLE),
    CWE_623 ("Unsafe ActiveX Control Marked Safe For Scripting", Implementability.IMPLEMENTABLE),
    CWE_624 ("Executable Regular Expression Error", Implementability.IMPLEMENTABLE),
    CWE_625 ("Permissive Regular Expression", Implementability.IMPLEMENTABLE),
    CWE_626 ("Null Byte Interaction Error (Poison Null Byte)", Implementability.IMPLEMENTABLE),
    CWE_627 ("Dynamic Variable Evaluation", Implementability.IMPLEMENTABLE),
    CWE_628 ("Function Call with Incorrectly Specified Arguments", Implementability.IMPLEMENTABLE),
    CWE_636 ("Not Failing Securely ('Failing Open')", Implementability.IMPLEMENTABLE),
    CWE_637 ("Unnecessary Complexity in Protection Mechanism (Not Using 'Economy of Mechanism')", Implementability.IMPLEMENTABLE),
    CWE_638 ("Not Using Complete Mediation", Implementability.IMPLEMENTABLE),
    CWE_639 ("Authorization Bypass Through User-Controlled Key", Implementability.IMPLEMENTABLE),
    CWE_640 ("Weak Password Recovery Mechanism for Forgotten Password", Implementability.IMPLEMENTABLE),
    CWE_641 ("Improper Restriction of Names for Files and Other Resources", Implementability.IMPLEMENTABLE),
    CWE_642 ("External Control of Critical State Data", Implementability.IMPLEMENTABLE),
    CWE_643 ("Improper Neutralization of Data within XPath Expressions ('XPath Injection')", Implementability.IMPLEMENTABLE),
    CWE_644 ("Improper Neutralization of HTTP Headers for Scripting Syntax", Implementability.IMPLEMENTABLE),
    CWE_645 ("Overly Restrictive Account Lockout Mechanism", Implementability.IMPLEMENTABLE),
    CWE_646 ("Reliance on File Name or Extension of Externally-Supplied File", Implementability.IMPLEMENTABLE),
    CWE_647 ("Use of Non-Canonical URL Paths for Authorization Decisions", Implementability.IMPLEMENTABLE),
    CWE_648 ("Incorrect Use of Privileged APIs", Implementability.IMPLEMENTABLE),
    CWE_649 ("Reliance on Obfuscation or Encryption of Security-Relevant Inputs without Integrity Checking", Implementability.IMPLEMENTABLE),
    CWE_650 ("Trusting HTTP Permission Methods on the Server Side", Implementability.IMPLEMENTABLE),
    CWE_651 ("Information Exposure Through WSDL File", Implementability.IMPLEMENTABLE),
    CWE_652 ("Improper Neutralization of Data within XQuery Expressions ('XQuery Injection')", Implementability.IMPLEMENTABLE),
    CWE_653 ("Insufficient Compartmentalization", Implementability.IMPLEMENTABLE),
    CWE_654 ("Reliance on a Single Factor in a Security Decision", Implementability.IMPLEMENTABLE),
    CWE_655 ("Insufficient Psychological Acceptability", Implementability.IMPLEMENTABLE),
    CWE_656 ("Reliance on Security Through Obscurity", Implementability.IMPLEMENTABLE),
    CWE_657 ("Violation of Secure Design Principles", Implementability.IMPLEMENTABLE),
    CWE_662 ("Improper Synchronization", Implementability.IMPLEMENTABLE),
    CWE_663 ("Use of a Non-reentrant Function in a Concurrent Context", Implementability.IMPLEMENTABLE),
    CWE_664 ("Improper Control of a Resource Through its Lifetime", Implementability.IMPLEMENTABLE),
    CWE_665 ("Improper Initialization", Implementability.IMPLEMENTABLE),
    CWE_666 ("Operation on Resource in Wrong Phase of Lifetime", Implementability.IMPLEMENTABLE),
    CWE_667 ("Improper Locking", Implementability.IMPLEMENTABLE),
    CWE_668 ("Exposure of Resource to Wrong Sphere", Implementability.IMPLEMENTABLE),
    CWE_669 ("Incorrect Resource Transfer Between Spheres", Implementability.IMPLEMENTABLE),
    CWE_670 ("Always-Incorrect Control Flow Implementation", Implementability.IMPLEMENTABLE),
    CWE_671 ("Lack of Administrator Control over Security", Implementability.IMPLEMENTABLE),
    CWE_672 ("Operation on a Resource after Expiration or Release", Implementability.IMPLEMENTABLE),
    CWE_673 ("External Influence of Sphere Definition", Implementability.IMPLEMENTABLE),
    CWE_674 ("Uncontrolled Recursion", Implementability.IMPLEMENTABLE),
    CWE_675 ("Duplicate Operations on Resource", Implementability.IMPLEMENTABLE),
    CWE_676 ("Use of Potentially Dangerous Function", Implementability.IMPLEMENTABLE),
    CWE_680 ("Chain Integer Overflow to Buffer Overflow", Implementability.IMPLEMENTABLE),
    CWE_681 ("Incorrect Conversion between Numeric Types", Implementability.IMPLEMENTABLE),
    CWE_682 ("Incorrect Calculation", Implementability.IMPLEMENTABLE),
    CWE_683 ("Function Call With Incorrect Order of Arguments", Implementability.IMPLEMENTABLE),
    CWE_684 ("Incorrect Provision of Specified Functionality", Implementability.IMPLEMENTABLE),
    CWE_685 ("Function Call With Incorrect Number of Arguments", Implementability.IMPLEMENTABLE),
    CWE_686 ("Function Call With Incorrect Argument Type", Implementability.IMPLEMENTABLE),
    CWE_687 ("Function Call With Incorrectly Specified Argument Value", Implementability.IMPLEMENTABLE),
    CWE_688 ("Function Call With Incorrect Variable or Reference as Argument", Implementability.IMPLEMENTABLE),
    CWE_689 ("Composite Permission Race Condition During Resource Copy", Implementability.IMPLEMENTABLE),
    CWE_690 ("Chain Unchecked Return Value to NULL Pointer Dereference", Implementability.IMPLEMENTABLE),
    CWE_691 ("Insufficient Control Flow Management", Implementability.IMPLEMENTABLE),
    CWE_692 ("Chain Incomplete Blacklist to Cross-Site Scripting", Implementability.IMPLEMENTABLE),
    CWE_693 ("Protection Mechanism Failure", Implementability.IMPLEMENTABLE),
    CWE_694 ("Use of Multiple Resources with Duplicate Identifier", Implementability.IMPLEMENTABLE),
    CWE_695 ("Use of Low-Level Functionality", Implementability.IMPLEMENTABLE),
    CWE_696 ("Incorrect Behavior Order", Implementability.IMPLEMENTABLE),
    CWE_697 ("Insufficient Comparison", Implementability.IMPLEMENTABLE),
    CWE_698 ("Execution After Redirect (EAR)", Implementability.IMPLEMENTABLE),
    CWE_703 ("Improper Check or Handling of Exceptional Conditions", Implementability.IMPLEMENTABLE),
    CWE_704 ("Incorrect Type Conversion or Cast", Implementability.IMPLEMENTABLE),
    CWE_705 ("Incorrect Control Flow Scoping", Implementability.IMPLEMENTABLE),
    CWE_706 ("Use of Incorrectly-Resolved Name or Reference", Implementability.IMPLEMENTABLE),
    CWE_707 ("Improper Enforcement of Message or Data Structure", Implementability.IMPLEMENTABLE),
    CWE_708 ("Incorrect Ownership Assignment", Implementability.IMPLEMENTABLE),
    CWE_710 ("Coding Standards Violation", Implementability.IMPLEMENTABLE),
    CWE_732 ("Incorrect Permission Assignment for Critical Resource", Implementability.IMPLEMENTABLE),
    CWE_733 ("Compiler Optimization Removal or Modification of Security-critical Code", Implementability.IMPLEMENTABLE),
    CWE_749 ("Exposed Dangerous Method or Function", Implementability.IMPLEMENTABLE),
    CWE_754 ("Improper Check for Unusual or Exceptional Conditions", Implementability.IMPLEMENTABLE),
    CWE_755 ("Improper Handling of Exceptional Conditions", Implementability.IMPLEMENTABLE),
    CWE_756 ("Missing Custom Error Page", Implementability.IMPLEMENTABLE),
    CWE_757 ("Selection of Less-Secure Algorithm During Negotiation ('Algorithm Downgrade')", Implementability.IMPLEMENTABLE),
    CWE_758 ("Reliance on Undefined, Unspecified, or Implementation-Defined Behavior", Implementability.IMPLEMENTABLE),
    CWE_759 ("Use of a One-Way Hash without a Salt", Implementability.IMPLEMENTABLE),
    CWE_760 ("Use of a One-Way Hash with a Predictable Salt", Implementability.IMPLEMENTABLE),
    CWE_761 ("Free of Pointer not at Start of Buffer", Implementability.IMPLEMENTABLE),
    CWE_762 ("Mismatched Memory Management Routines", Implementability.IMPLEMENTABLE),
    CWE_763 ("Release of Invalid Pointer or Reference", Implementability.IMPLEMENTABLE),
    CWE_764 ("Multiple Locks of a Critical Resource", Implementability.IMPLEMENTABLE),
    CWE_765 ("Multiple Unlocks of a Critical Resource", Implementability.IMPLEMENTABLE),
    CWE_766 ("Critical Variable Declared Public", Implementability.IMPLEMENTABLE),
    CWE_767 ("Access to Critical Private Variable via Public Method", Implementability.IMPLEMENTABLE),
    CWE_768 ("Incorrect Short Circuit Evaluation", Implementability.IMPLEMENTABLE),
    CWE_770 ("Allocation of Resources Without Limits or Throttling", Implementability.IMPLEMENTABLE),
    CWE_771 ("Missing Reference to Active Allocated Resource", Implementability.IMPLEMENTABLE),
    CWE_772 ("Missing Release of Resource after Effective Lifetime", Implementability.IMPLEMENTABLE),
    CWE_773 ("Missing Reference to Active File Descriptor or Handle", Implementability.IMPLEMENTABLE),
    CWE_774 ("Allocation of File Descriptors or Handles Without Limits or Throttling", Implementability.IMPLEMENTABLE),
    CWE_775 ("Missing Release of File Descriptor or Handle after Effective Lifetime", Implementability.IMPLEMENTABLE),
    CWE_776 ("Improper Restriction of Recursive Entity References in DTDs ('XML Entity Expansion')", Implementability.IMPLEMENTABLE),
    CWE_777 ("Regular Expression without Anchors", Implementability.IMPLEMENTABLE),
    CWE_778 ("Insufficient Logging", Implementability.IMPLEMENTABLE),
    CWE_779 ("Logging of Excessive Data", Implementability.IMPLEMENTABLE),
    CWE_780 ("Use of RSA Algorithm without OAEP", Implementability.IMPLEMENTABLE),
    CWE_781 ("Improper Address Validation in IOCTL with METHOD_NEITHER I/O Control Code", Implementability.IMPLEMENTABLE),
    CWE_782 ("Exposed IOCTL with Insufficient Access Control", Implementability.IMPLEMENTABLE),
    CWE_783 ("Operator Precedence Logic Error", Implementability.IMPLEMENTABLE),
    CWE_784 ("Reliance on Cookies without Validation and Integrity Checking in a Security Decision", Implementability.IMPLEMENTABLE),
    CWE_785 ("Use of Path Manipulation Function without Maximum-sized Buffer", Implementability.IMPLEMENTABLE),
    CWE_786 ("Access of Memory Location Before Start of Buffer", Implementability.IMPLEMENTABLE),
    CWE_787 ("Out-of-bounds Write", Implementability.IMPLEMENTABLE),
    CWE_788 ("Access of Memory Location After End of Buffer", Implementability.IMPLEMENTABLE),
    CWE_789 ("Uncontrolled Memory Allocation", Implementability.IMPLEMENTABLE),
    CWE_790 ("Improper Filtering of Special Elements", Implementability.IMPLEMENTABLE),
    CWE_791 ("Incomplete Filtering of Special Elements", Implementability.IMPLEMENTABLE),
    CWE_792 ("Incomplete Filtering of One or More Instances of Special Elements", Implementability.IMPLEMENTABLE),
    CWE_793 ("Only Filtering One Instance of a Special Element", Implementability.IMPLEMENTABLE),
    CWE_794 ("Incomplete Filtering of Multiple Instances of Special Elements", Implementability.IMPLEMENTABLE),
    CWE_795 ("Only Filtering Special Elements at a Specified Location", Implementability.IMPLEMENTABLE),
    CWE_796 ("Only Filtering Special Elements Relative to a Marker", Implementability.IMPLEMENTABLE),
    CWE_797 ("Only Filtering Special Elements at an Absolute Position", Implementability.IMPLEMENTABLE),
    CWE_798 ("Use of Hard-coded Credentials", Implementability.IMPLEMENTABLE),
    CWE_799 ("Improper Control of Interaction Frequency", Implementability.IMPLEMENTABLE),
    CWE_804 ("Guessable CAPTCHA", Implementability.IMPLEMENTABLE),
    CWE_805 ("Buffer Access with Incorrect Length Value", Implementability.IMPLEMENTABLE),
    CWE_806 ("Buffer Access Using Size of Source Buffer", Implementability.IMPLEMENTABLE),
    CWE_807 ("Reliance on Untrusted Inputs in a Security Decision", Implementability.IMPLEMENTABLE),
    CWE_820 ("Missing Synchronization", Implementability.IMPLEMENTABLE),
    CWE_821 ("Incorrect Synchronization", Implementability.IMPLEMENTABLE),
    CWE_822 ("Untrusted Pointer Dereference", Implementability.IMPLEMENTABLE),
    CWE_823 ("Use of Out-of-range Pointer Offset", Implementability.IMPLEMENTABLE),
    CWE_824 ("Access of Uninitialized Pointer", Implementability.IMPLEMENTABLE),
    CWE_825 ("Expired Pointer Dereference", Implementability.IMPLEMENTABLE),
    CWE_826 ("Premature Release of Resource During Expected Lifetime", Implementability.IMPLEMENTABLE),
    CWE_827 ("Improper Control of Document Type Definition", Implementability.IMPLEMENTABLE),
    CWE_828 ("Signal Handler with Functionality that is not Asynchronous-Safe", Implementability.IMPLEMENTABLE),
    CWE_829 ("Inclusion of Functionality from Untrusted Control Sphere", Implementability.IMPLEMENTABLE),
    CWE_830 ("Inclusion of Web Functionality from an Untrusted Source", Implementability.IMPLEMENTABLE),
    CWE_831 ("Signal Handler Function Associated with Multiple Signals", Implementability.IMPLEMENTABLE),
    CWE_832 ("Unlock of a Resource that is not Locked", Implementability.IMPLEMENTABLE),
    CWE_833 ("Deadlock", Implementability.IMPLEMENTABLE),
    CWE_834 ("Excessive Iteration", Implementability.IMPLEMENTABLE),
    CWE_835 ("Loop with Unreachable Exit Condition ('Infinite Loop')", Implementability.IMPLEMENTABLE),
    CWE_836 ("Use of Password Hash Instead of Password for Authentication", Implementability.IMPLEMENTABLE),
    CWE_837 ("Improper Enforcement of a Single, Unique Action", Implementability.IMPLEMENTABLE),
    CWE_838 ("Inappropriate Encoding for Output Context", Implementability.IMPLEMENTABLE),
    CWE_839 ("Numeric Range Comparison Without Minimum Check", Implementability.IMPLEMENTABLE),
    CWE_841 ("Improper Enforcement of Behavioral Workflow", Implementability.IMPLEMENTABLE),
    CWE_842 ("Placement of User into Incorrect Group", Implementability.IMPLEMENTABLE),
    CWE_843 ("Access of Resource Using Incompatible Type ('Type Confusion')", Implementability.IMPLEMENTABLE),
    CWE_862 ("Missing Authorization", Implementability.IMPLEMENTABLE),
    CWE_863 ("Incorrect Authorization", Implementability.IMPLEMENTABLE),
    CWE_908 ("Use of Uninitialized Resource", Implementability.IMPLEMENTABLE),
    CWE_909 ("Missing Initialization of Resource", Implementability.IMPLEMENTABLE),
    CWE_910 ("Use of Expired File Descriptor", Implementability.IMPLEMENTABLE),
    CWE_911 ("Improper Update of Reference Count", Implementability.IMPLEMENTABLE),
    CWE_912 ("Hidden Functionality", Implementability.IMPLEMENTABLE),
    CWE_913 ("Improper Control of Dynamically-Managed Code Resources", Implementability.IMPLEMENTABLE),
    CWE_914 ("Improper Control of Dynamically-Identified Variables", Implementability.IMPLEMENTABLE),
    CWE_915 ("Improperly Controlled Modification of Dynamically-Determined Object Attributes", Implementability.IMPLEMENTABLE),
    CWE_916 ("Use of Password Hash With Insufficient Computational Effort", Implementability.IMPLEMENTABLE),
    CWE_917 ("Improper Neutralization of Special Elements used in an Expression Language Statement ('Expression Language Injection')", Implementability.IMPLEMENTABLE),
    CWE_918 ("Server-Side Request Forgery (SSRF)", Implementability.IMPLEMENTABLE),
    CWE_920 ("Improper Restriction of Power Consumption", Implementability.IMPLEMENTABLE),
    CWE_921 ("Storage of Sensitive Data in a Mechanism without Access Control", Implementability.IMPLEMENTABLE),
    CWE_922 ("Insecure Storage of Sensitive Information", Implementability.IMPLEMENTABLE),
    CWE_923 ("Improper Restriction of Communication Channel to Intended Endpoints", Implementability.IMPLEMENTABLE),
    CWE_924 ("Improper Enforcement of Message Integrity During Transmission in a Communication Channel", Implementability.IMPLEMENTABLE),
    CWE_925 ("Improper Verification of Intent by Broadcast Receiver", Implementability.IMPLEMENTABLE),
    CWE_926 ("Improper Export of Android Application Components", Implementability.IMPLEMENTABLE),
    CWE_927 ("Use of Implicit Intent for Sensitive Communication", Implementability.IMPLEMENTABLE),
    CWE_939 ("Improper Authorization in Handler for Custom URL Scheme", Implementability.IMPLEMENTABLE),
    CWE_940 ("Improper Verification of Source of a Communication Channel", Implementability.IMPLEMENTABLE),
    CWE_941 ("Incorrectly Specified Destination in a Communication Channel", Implementability.IMPLEMENTABLE),
    CWE_942 ("Overly Permissive Cross-domain Whitelist", Implementability.IMPLEMENTABLE),
    CWE_943 ("Improper Neutralization of Special Elements in Data Query Logic", Implementability.IMPLEMENTABLE);


    private String title;
    private Implementability implementability;

    CweRule(String title, Implementability implementability) {
      this.title = title;
      this.implementability = implementability;
    }


    @Override
    public String getCodingStandardRuleId() {

      return name().replace('_','-');
    }

    @Override
    public Implementability getImplementability() {

      return implementability;
    }

    public String getTitle() {
      return title;
    }

    public static CweRule fromString(String id) {
      String test = id;
      if (id.matches("CWE-\\d+")) {
        test = test.replace('-','_');
      }
      if (test.matches("CWE_\\d+")) {
        return valueOf(test);
      }
      return null;

    }

  }

}
