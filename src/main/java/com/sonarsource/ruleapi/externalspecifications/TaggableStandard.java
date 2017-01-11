/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import java.util.List;


/**
 * A CodingStandard for which we tag relevant rules in the GUI.
 *
 * In Jira, relevant RSpecs will have values in 3 places:
 * <ul>
 *   <li>The <em>See</em> section of the rule description will contain a specific reference</li>
 *   <li>The References field will contain the ids of the relevant specification rule</li>
 *   <li>The Labels field will include the related tag</li>
 * </ul>
 */
public interface TaggableStandard extends CodingStandard {

  boolean isTagShared();

  String getTag();

  String getSeeSectionSearchString();

  String getReferencePattern();

  boolean doesReferenceNeedUpdating(String reference, List<String> replacements, String ruleKey);


}
