package com.sonarsource.ruleapi.domain;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

import org.junit.Test;

public class CodingStandardRuleCoverageTest {

  @Test
  public void checkCodingStandardRuleCoverageFields() {
    CodingStandardRuleCoverage ruleCoverage = new CodingStandardRuleCoverage();
    ruleCoverage.setRule(new CodingStandardRule.Builder("RSPEC-123").build());
    ruleCoverage.setImplemented(Boolean.TRUE);

    Rule rspec = new Rule("Dummy");
    rspec.setKey("RSPEC-123");
    ruleCoverage.setSpecifiedBy(rspec);
    ruleCoverage.setCoveredBy(rspec);

    assertThat(ruleCoverage.getCoveredBy().getKey()).isEqualTo("RSPEC-123");
    assertThat(ruleCoverage.getSpecifiedBy().getKey()).isEqualTo("RSPEC-123");
    assertThat(ruleCoverage.getRule().getKey()).isEqualTo("RSPEC-123");
    assertThat(ruleCoverage.isImplemented()).isTrue();
  }

  @Test
  public void checkDefaultCodingStandardRuleCoverageNotImplemented() {
    CodingStandardRuleCoverage ruleCoverage = new CodingStandardRuleCoverage();

    assertThat(ruleCoverage.isImplemented()).isFalse();
  }
}
