# sonar-rule-api

Used to interact with the rule repository in an automated fashion as either an API or a CLI.

As an API, it can be used to:
 1. retrieve rules from RSpec
 1. retrieve rules from a running SonarQube instance
 1. compare two rules
 3. update RSpecs
 3. perform any of the functions available via the CLI

As a CLI, it can be used with the following parameters:

**Reporting**
These options can take a `-instance` parameter, but default to SonarQube.com
  * `reports`: Generates all reports based on SonarQube.com (default) or a particular `-instance http:...`
  * `single_report`: Generate a single `-report` against `-instance` (defaults to SonarQube.com), `-language`, and `-tool`. Run with missing parameters for more detailed help
  * `diff`: Generates a diff report for the specified '-language' and '-instance'

**RSpec corrections**
These options require -login and -password parameters.
  * `outdated`: Marks RSpec rules outdated based on SonarQube.com or instance specified with `-instance` parameter. Requires -login and -password parameters.
  * `integrity`: RSpec internal integrity check. Requires -login and -password parameters.

**Language plugin file generation**
To create and maintain html and metadata rules files.
It relies on the `sonarpedia.json` a rules directory designated by the `sonarpedia.json` file. This `sonarpedia.json` is typically at the root directory of the language plugin and the following command are run from that directory.  
  * `init`:  as `init -language foo -rulesDir (rules)`. In the current directory, generate a `sonarpedia.json` file pointing on a `rules` directory. This `rules` directory is to be populated with html description and json metadata files.
  * `generate`: as  `generate -rule S1234 S3456`. Read the `sonarpedia.json` file in the current directory, generate html and json files for designated rules.
  * `update`: Read the `sonarpedia.json` file in the current directory, find the rules and update their html description. Update `sonarpedia.json` timestamps.  
The format of `sonarpedia.json` is described in  [Sonarpedia-schema.json|file https://github.com/SonarSource/sonar-rule-api/blob/master/sonarpedia-schema.json] file.
It supports more than one language in a plugin.

***Additional options:***
  * -preserve-filenames : Use the rule keys provided by "-rule" to construct the name of output files, this allow to use legacy keys.
  * -no-language-in-filenames : Remove language from file name format (ex: "S123.json" instead of "S123_java.json").

***Deprecated features***
The use of the `-directory` option is deprecated and replaced by the use of `sonarpedia.json` file.
    * `generate`:  Generates html description and json metadata files specified by `-rule` and `-language` parameters at directory specified by `-directory`
    * `update`: Update html and json description files specified by `-language` found at directory specified by `-directory`
