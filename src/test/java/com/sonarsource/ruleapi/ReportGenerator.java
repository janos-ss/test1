/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi;

import org.junit.Test;

import com.sonar.orchestrator.Orchestrator;

public class ReportGenerator {

	@Test
	public void generateReports() {
		Orchestrator orchestrator = Orchestrator
				.builderEnv()
				.setOrchestratorProperty("sonar.runtimeVersion", "LTS")
				.setOrchestratorProperty("orchestrator.updateCenterUrl",
						"http://update.sonarsource.org/update-center-dev.properties")
				.setOrchestratorProperty("sonar.jdbc.dialect", "h2")
				.setOrchestratorProperty("cobolVersion", "DEV").addPlugin("cobol")
				.setOrchestratorProperty("javaVersion", "DEV").addPlugin("java")
				.setOrchestratorProperty("javascriptVersion", "DEV").addPlugin("javascript")
				.setOrchestratorProperty("cppVersion", "DEV").addPlugin("cpp")
				.setOrchestratorProperty("webVersion", "DEV").addPlugin("web")
				.setOrchestratorProperty("plsqlVersion", "DEV").addPlugin("plsql")
				.setOrchestratorProperty("phpVersion", "DEV").addPlugin("php")
				.setOrchestratorProperty("csharpVersion", "DEV").addPlugin("csharp")
				.setOrchestratorProperty("groovyVersion", "DEV").addPlugin("groovy")
				.setOrchestratorProperty("abapVersion", "DEV").addPlugin("abap")
				.setOrchestratorProperty("flexVersion", "DEV").addPlugin("flex")
				.setOrchestratorProperty("pliVersion", "DEV").addPlugin("pli")
				.setOrchestratorProperty("pythonVersion", "DEV").addPlugin("python")
				.setOrchestratorProperty("rpgVersion", "DEV").addPlugin("rpg")
				.setOrchestratorProperty("vbVersion", "DEV").addPlugin("vb")
				.setOrchestratorProperty("vbnetVersion", "DEV").addPlugin("vbnet")
				.setOrchestratorProperty("xmlVersion", "DEV").addPlugin("xml")
				.build();
		orchestrator.start();
		String sonarqubeUrl = orchestrator.getServer().getUrl();
		System.out.println("SonarQube URL : " + sonarqubeUrl);
		orchestrator.stop();
	}
}
