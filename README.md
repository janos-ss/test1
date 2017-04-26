# sonar-rule-api

Used to interact with the rule repository in an automated fashion as either an API or a CLI.

As an API, it can be used to:
 1. retrive rules from RSpec
 1. retrive rules from a running SonarQube instance
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
These options require `-langauge` and `-directory` parameters. Files will be named for the canonical keys of their rules; legacy keys are not supported.
  * `generate`: Generates html description and json metadata files specified by `-rule` and `-language` parameters at directory specified by `-directory`
  * `update`: Update html and json description files specified by `-language` found at directory specified by `-directory`

