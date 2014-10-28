/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi;

import com.sonarsource.ruleapi.RuleMaker;
import com.sonarsource.ruleapi.domain.Parameter;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.List;

public class RuleMakerTest extends TestCase{

  private RuleMaker rm = new RuleMaker();

  public void testIsLangaugeMatchEasyTrue() throws Exception {
    Assert.assertTrue(rm.isLanguageMatch("Java", "Java"));
  }

  public void testIsLanguageMatchEasyFalse() throws Exception {
    Assert.assertFalse(rm.isLanguageMatch("RPG", "Java"));
  }

  public void testIsLanguageMatchFalse() throws Exception {
    Assert.assertFalse(rm.isLanguageMatch("Java", "JavaScript"));
  }

  public void testIsLanguageMatchTrue() throws Exception {
    Assert.assertTrue(rm.isLanguageMatch("Java", "Java: ..."));
  }

  public void testHandleParameterList() throws Exception{
    String paramString = "* key: complexity_threshold\r\n* type = text\r\n** Description: The minimum complexity at which this rule will be triggered.\r\n** Default: 250";
    List<Parameter> paramList = rm.handleParameterList(paramString, "Java");
    Assert.assertTrue(paramList != null && paramList.size() == 1);
  }

  public void testHandleParameterListMultilanguage() throws Exception {
    String paramString = "Key: format \r\nDescription: Regular expression used to check the names against. \r\nDefault Value for Java : ^[a-z][a-zA-Z0-9]*$ \r\nDefault Value for Flex : ^[_a-z][a-zA-Z0-9]*$";
    List<Parameter> paramList = rm.handleParameterList(paramString, "Java");
    Assert.assertEquals("^[a-z][a-zA-Z0-9]*$", paramList.get(0).getDefaultVal());

  }

  public void testPullValueFromJson() throws Exception {
    String json = "[{,\",s,e,l,f,\",:,\",h,t,t,p,:,\\,/,\\,/,j,i,r,a,.,s,o,n,a,r,s,o,u,r,c,e,.,c,o,m,\\,/,r,e,s,t,\\,/,a,p,i,\\,/,2,\\,/,c,u,s,t,o,m,F,i,e,l,d,O,p,t,i,o,n,\\,/,1,0,0,7,1,\",,,\",v,a,l,u,e,\",:,\",R,e,l,i,a,b,i,l,i,t,y,\",,,\",i,d,\",:,\",1,0,0,7,1,\",,,\",c,h,i,l,d,\",:,{,\",s,e,l,f,\",:,\",h,t,t,p,:,\\,/,\\,/,j,i,r,a,.,s,o,n,a,r,s,o,u,r,c,e,.,c,o,m,\\,/,r,e,s,t,\\,/,a,p,i,\\,/,2,\\,/,c,u,s,t,o,m,F,i,e,l,d,O,p,t,i,o,n,\\,/,1,0,0,7,3,\",,,\",v,a,l,u,e,\",:,\",D,a,t,a, ,r,e,l,a,t,e,d, ,r,e,l,i,a,b,i,l,i,t,y,\",,,\",i,d,\",:,\",1,0,0,7,3,\",},}]";
    Assert.assertEquals(rm.pullValueFromJson(json),"Reliability");
  }



}
