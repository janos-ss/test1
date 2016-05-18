/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.misra.MisraC2004;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class AbstractMisraSpecificationTest {

  @Test
  public void testReportsEmpty() {

    MisraC2004 c4 = new MisraC2004();
    c4.populateRulesCoverageMap();

    String summaryReport = "";
    String report = "";
    summaryReport = c4.getSummaryReport(null);
    report = c4.getReport(null);

    String linebreak = String.format("%n");
    String expectedSummary = "MISRA C 2004" + linebreak +
            "Mandatory:\tSpecified: 110\tImplemented: 0\t=> 0.00%" + linebreak +
            "Optional:\tSpecified: 18\tImplemented: 0\t=> 0.00%" + linebreak +
            "Total:\tSpecified: 128\tImplemented: 0\t=> 0.00%" + linebreak;

    assertThat(summaryReport).isEqualTo(expectedSummary);
    assertThat(report).endsWith(summaryReport);
    assertThat(report).contains("1.1\tS: NA\t\tC: NA\t\tI: N");

  }

  @Test
  public void testReportsNonEmpty() {

    MisraC2004 c4 = new MisraC2004();
    c4.populateRulesCoverageMap();

    Rule rule = new Rule("C");
    List<String> ids = new ArrayList<>();
    ids.add("2.1");  // implementable, required
    ids.add("2.4");  // implementable, optional

    c4.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);
    c4.setCodingStandardRuleCoverageImplemented(ids,rule);

    c4.computeCoverage();

    String summaryReport = "";
    String report = "";
    summaryReport = c4.getSummaryReport(null);
    report = c4.getReport(null);

    String linebreak = String.format("%n");

    String expectedSummary = "MISRA C 2004" + linebreak +
            "Mandatory:\tSpecified: 110\tImplemented: 1\t=> 0.91%" + linebreak +
            "Optional:\tSpecified: 18\tImplemented: 1\t=> 5.56%" + linebreak +
            "Total:\tSpecified: 128\tImplemented: 2\t=> 1.56%" + linebreak;

    assertThat(summaryReport).isEqualTo(expectedSummary);
    assertThat(report).contains(expectedSummary);
    assertThat(report).contains("2.1\tS: NA\t\tC: NA\t\tI: N");

  }

  @Test
  public void testHtmlReport() {
    MisraC2004 misraC2004 = new MisraC2004();
    misraC2004.populateRulesCoverageMap();

    Rule rule = new Rule("C");
    rule.setRepo("c");
    rule.setKey("RSPEC-1234");
    rule.setTitle("Rule title...");
    misraC2004.getRulesCoverage().get("2.1").addImplementedBy(rule);

    misraC2004.computeCoverage();

    String report = misraC2004.getHtmlReport("");
    String expectedReport = "<h2>SonarQube C coverage of MISRA C 2004</h2>These are the MISRA C 2004 rules covered for C by the <a href='http://sonarsource.com'>SonarSource</a> <a href='http://www.sonarsource.com/products/plugins/languages/cpp/'>C/C++ plugin</a>.<h3>Summary</h3><table><tr><th>&nbsp;</th><th>Optional</th><th>Mandatory</th></tr><tr><td>Rule count</td><td class='number'>20</td><td class='number'>122</td></tr><tr><td><a href='#nc'>Not statically checkable</a></td><td class='number'>2</td><td class='number'>12</td></tr><tr><td>Statically checkable</td><td class='number'>18</td><td class='number'>110</td></tr><tr><td><a href='#c'>Covered</a></td><td class='number'>0</td><td class='number'>1</td></tr><tr><td><a href='#p'>Pending</a></td><td class='number'>18</td><td class='number'>109</td></tr></table><a name='c' id='c'></a><h3>Covered</h3><table><tr><td>2.1</td><td>Required</td><td><a href='/coding_rules#rule_key=c%3AS1234' target='rule'>S1234</a> Rule title...<br/>\n" +
            "</td></tr></table><a name='p' id='p'></a><h3>Pending</h3><table><tr><td>2.2</td><td>Required</td><td></td></tr><tr><td>2.3</td><td>Required</td><td></td></tr><tr><td>2.4</td><td>Optional</td><td></td></tr><tr><td>3.4</td><td>Required</td><td></td></tr><tr><td>4.1</td><td>Required</td><td></td></tr><tr><td>4.2</td><td>Required</td><td></td></tr><tr><td>5.1</td><td>Required</td><td></td></tr><tr><td>5.2</td><td>Required</td><td></td></tr><tr><td>5.3</td><td>Required</td><td></td></tr><tr><td>5.4</td><td>Required</td><td></td></tr><tr><td>5.5</td><td>Optional</td><td></td></tr><tr><td>5.6</td><td>Optional</td><td></td></tr><tr><td>5.7</td><td>Optional</td><td></td></tr><tr><td>6.1</td><td>Required</td><td></td></tr><tr><td>6.2</td><td>Required</td><td></td></tr><tr><td>6.3</td><td>Optional</td><td></td></tr><tr><td>6.4</td><td>Required</td><td></td></tr><tr><td>6.5</td><td>Required</td><td></td></tr><tr><td>7.1</td><td>Required</td><td></td></tr><tr><td>8.1</td><td>Required</td><td></td></tr><tr><td>8.2</td><td>Required</td><td></td></tr><tr><td>8.3</td><td>Required</td><td></td></tr><tr><td>8.4</td><td>Required</td><td></td></tr><tr><td>8.5</td><td>Required</td><td></td></tr><tr><td>8.6</td><td>Required</td><td></td></tr><tr><td>8.7</td><td>Required</td><td></td></tr><tr><td>8.8</td><td>Required</td><td></td></tr><tr><td>8.9</td><td>Required</td><td></td></tr><tr><td>8.10</td><td>Required</td><td></td></tr><tr><td>8.11</td><td>Required</td><td></td></tr><tr><td>8.12</td><td>Required</td><td></td></tr><tr><td>9.1</td><td>Required</td><td></td></tr><tr><td>9.2</td><td>Required</td><td></td></tr><tr><td>9.3</td><td>Required</td><td></td></tr><tr><td>10.1</td><td>Required</td><td></td></tr><tr><td>10.2</td><td>Required</td><td></td></tr><tr><td>10.3</td><td>Required</td><td></td></tr><tr><td>10.4</td><td>Required</td><td></td></tr><tr><td>10.5</td><td>Required</td><td></td></tr><tr><td>10.6</td><td>Required</td><td></td></tr><tr><td>11.1</td><td>Required</td><td></td></tr><tr><td>11.2</td><td>Required</td><td></td></tr><tr><td>11.3</td><td>Optional</td><td></td></tr><tr><td>11.4</td><td>Optional</td><td></td></tr><tr><td>11.5</td><td>Required</td><td></td></tr><tr><td>12.1</td><td>Optional</td><td></td></tr><tr><td>12.2</td><td>Required</td><td></td></tr><tr><td>12.3</td><td>Required</td><td></td></tr><tr><td>12.4</td><td>Required</td><td></td></tr><tr><td>12.5</td><td>Required</td><td></td></tr><tr><td>12.6</td><td>Optional</td><td></td></tr><tr><td>12.7</td><td>Required</td><td></td></tr><tr><td>12.8</td><td>Required</td><td></td></tr><tr><td>12.9</td><td>Required</td><td></td></tr><tr><td>12.10</td><td>Required</td><td></td></tr><tr><td>12.11</td><td>Optional</td><td></td></tr><tr><td>12.12</td><td>Required</td><td></td></tr><tr><td>12.13</td><td>Optional</td><td></td></tr><tr><td>13.1</td><td>Required</td><td></td></tr><tr><td>13.2</td><td>Optional</td><td></td></tr><tr><td>13.3</td><td>Required</td><td></td></tr><tr><td>13.4</td><td>Required</td><td></td></tr><tr><td>13.5</td><td>Required</td><td></td></tr><tr><td>13.6</td><td>Required</td><td></td></tr><tr><td>13.7</td><td>Required</td><td></td></tr><tr><td>14.1</td><td>Required</td><td></td></tr><tr><td>14.2</td><td>Required</td><td></td></tr><tr><td>14.3</td><td>Required</td><td></td></tr><tr><td>14.4</td><td>Required</td><td></td></tr><tr><td>14.5</td><td>Required</td><td></td></tr><tr><td>14.6</td><td>Required</td><td></td></tr><tr><td>14.7</td><td>Required</td><td></td></tr><tr><td>14.8</td><td>Required</td><td></td></tr><tr><td>14.9</td><td>Required</td><td></td></tr><tr><td>14.10</td><td>Required</td><td></td></tr><tr><td>15.0</td><td>Required</td><td></td></tr><tr><td>15.1</td><td>Required</td><td></td></tr><tr><td>15.2</td><td>Required</td><td></td></tr><tr><td>15.3</td><td>Required</td><td></td></tr><tr><td>15.4</td><td>Required</td><td></td></tr><tr><td>15.5</td><td>Required</td><td></td></tr><tr><td>16.1</td><td>Required</td><td></td></tr><tr><td>16.2</td><td>Required</td><td></td></tr><tr><td>16.3</td><td>Required</td><td></td></tr><tr><td>16.4</td><td>Required</td><td></td></tr><tr><td>16.5</td><td>Required</td><td></td></tr><tr><td>16.6</td><td>Required</td><td></td></tr><tr><td>16.7</td><td>Optional</td><td></td></tr><tr><td>16.8</td><td>Required</td><td></td></tr><tr><td>16.9</td><td>Required</td><td></td></tr><tr><td>17.1</td><td>Required</td><td></td></tr><tr><td>17.2</td><td>Required</td><td></td></tr><tr><td>17.3</td><td>Required</td><td></td></tr><tr><td>17.4</td><td>Required</td><td></td></tr><tr><td>17.5</td><td>Optional</td><td></td></tr><tr><td>17.6</td><td>Required</td><td></td></tr><tr><td>18.1</td><td>Required</td><td></td></tr><tr><td>18.2</td><td>Required</td><td></td></tr><tr><td>18.4</td><td>Required</td><td></td></tr><tr><td>19.1</td><td>Optional</td><td></td></tr><tr><td>19.2</td><td>Optional</td><td></td></tr><tr><td>19.3</td><td>Required</td><td></td></tr><tr><td>19.4</td><td>Required</td><td></td></tr><tr><td>19.5</td><td>Required</td><td></td></tr><tr><td>19.6</td><td>Required</td><td></td></tr><tr><td>19.7</td><td>Optional</td><td></td></tr><tr><td>19.8</td><td>Required</td><td></td></tr><tr><td>19.9</td><td>Required</td><td></td></tr><tr><td>19.10</td><td>Required</td><td></td></tr><tr><td>19.11</td><td>Required</td><td></td></tr><tr><td>19.12</td><td>Required</td><td></td></tr><tr><td>19.13</td><td>Optional</td><td></td></tr><tr><td>19.14</td><td>Required</td><td></td></tr><tr><td>19.15</td><td>Required</td><td></td></tr><tr><td>19.16</td><td>Required</td><td></td></tr><tr><td>19.17</td><td>Required</td><td></td></tr><tr><td>20.1</td><td>Required</td><td></td></tr><tr><td>20.2</td><td>Required</td><td></td></tr><tr><td>20.4</td><td>Required</td><td></td></tr><tr><td>20.5</td><td>Required</td><td></td></tr><tr><td>20.6</td><td>Required</td><td></td></tr><tr><td>20.7</td><td>Required</td><td></td></tr><tr><td>20.8</td><td>Required</td><td></td></tr><tr><td>20.9</td><td>Required</td><td></td></tr><tr><td>20.10</td><td>Required</td><td></td></tr><tr><td>20.11</td><td>Required</td><td></td></tr><tr><td>20.12</td><td>Required</td><td></td></tr></table><a name='nc' id='nc'></a><h3>Not Statically Coverable</h3><table><tr><td>1.1</td><td>Required</td></tr><tr><td>1.2</td><td>Required</td></tr><tr><td>1.3</td><td>Required</td></tr><tr><td>1.4</td><td>Required</td></tr><tr><td>1.5</td><td>Optional</td></tr><tr><td>3.1</td><td>Required</td></tr><tr><td>3.2</td><td>Required</td></tr><tr><td>3.3</td><td>Optional</td></tr><tr><td>3.5</td><td>Required</td></tr><tr><td>3.6</td><td>Required</td></tr><tr><td>16.10</td><td>Required</td></tr><tr><td>18.3</td><td>Required</td></tr><tr><td>20.3</td><td>Required</td></tr><tr><td>21.1</td><td>Required</td></tr></table>";

    assertThat(report).isEqualTo(expectedReport);
  }

  @Test
  public void testIsFieldEntryFormatNeedUpdating() {

    MisraC2004 misra = new MisraC2004();

    Rule rule = new Rule("C");
    rule.setKey("RSPEC-1345");
    rule.getMisraC04().add("hello");

    List<String> updates = new ArrayList<>();

    assertThat(misra.doesReferenceNeedUpdating("hello",updates, rule.getKey())).isFalse();

  }

  @Test
  public void testGetCodingStandardRuleFromId() {

    MisraC2004 m4 = new MisraC2004();

    assertThat(m4.getCodingStandardRuleFromId(null)).isNull();

    assertThat(m4.getCodingStandardRuleFromId("1.1")).isEqualTo(MisraC2004.StandardRule.MISRAC2004_1POINT1);

  }

  @Test
  public void testSetRspecReferenceFieldValues() {
    MisraC2004 m4 = new MisraC2004();
    Rule rule = new Rule("C");
    List<String> ids = new ArrayList<>(3);
    ids.add("red");
    ids.add("green");
    ids.add("blue");

    m4.setRspecReferenceFieldValues(rule, ids);

    assertThat(rule.getMisraC04()).isEqualTo(ids);
  }

  @Test
  public void testGetReportTypes(){
    MisraC2004 mc = new MisraC2004();
    assertThat(mc.getReportTypes()).isNotEmpty();
  }

}
