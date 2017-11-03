# sonar-rule-api

Used to interact with the rule repository in an automated fashion as either an API or a CLI.

As an API, it can be used to:
 1. retrieve rules from RSpec
 1. retrieve rules from a running SonarQube instance
 1. compare two rules
 3. update RSpecs
 3. perform any of the functions available via the CLI

## Usage

The rule-api release is [available on repox](https://repox.sonarsource.com/sonarsource-private-releases/com/sonarsource/rule-api/rule-api/), this command will automatically download the latest:

`curl -SLJO "https://repox.sonarsource.com/sonarsource-private-releases/com/sonarsource/rule-api/rule-api/\[RELEASE\]/rule-api-\[RELEASE\].jar" -u `(your LDAP login)

Use:

`java -jar rula-api-`(version number)`.jar` (options)


## Options

### Reporting

These options can take a `-instance` parameter, but default to SonarQube.com
  * `reports`: Generates all reports based on SonarQube.com (default) or a particular `-instance http:...`
  * `single_report`: Generate a single `-report` against `-instance` (defaults to SonarQube.com), `-language`, and `-tool`. Run with missing parameters for more detailed help
  * `diff`: Generates a diff report for the specified '-language' and '-instance'

### RSpec corrections
These options require -login and -password parameters.
  * `outdated`: Marks RSpec rules outdated based on SonarQube.com or instance specified with `-instance` parameter. Requires -login and -password parameters.
  * `integrity`: RSpec internal integrity check. Requires -login and -password parameters.

### Language plugin file generation

To create and maintain html and metadata rules files.
It relies on the `sonarpedia.json` file and a rules directory designated by the `sonarpedia.json` file.
```
(root)
  │
  ├──  ./any/where/in/the/hierarchy/rules
  │                                   ├── Sxxxx_(language).html
  │                                   ├── Sxxxx_(language).json
  │                                   ├── Syyyy_(language).html
  │                                   └── Syyyy_(language).json
  └── sonarpedia.json
```
This `sonarpedia.json` must be at the root of the repository of the language plugin and the following command are run from that directory.  
  * `init`:  as `init -language foo`. It generates a `sonarpedia.json` file pointing on a `rules` directory. This `rules` directory will have to be populated with html description and json metadata files with the next option.
  * `generate`: as  `generate -rule S1234 S3456`. Read the `sonarpedia.json` file in the current directory, generates html and json files for designated rules in the `rules` directory. 
  * `update`: Read the `sonarpedia.json` file in the current directory, find the rules and update their html descriptions. It updates `sonarpedia.json` timestamps.

The format of `sonarpedia.json` is described in the [Sonarpedia-schema.json file](https://github.com/SonarSource/sonar-rule-api/blob/master/sonarpedia-schema.json).
It permits to have more than one language.

By default this rules directory is near the `sonarpedia.json` file, this be set up in any place deeper in the hierarchy.

As soon as a `sonarpedia.json` file is present, when running any **One-Click release**,  **Releasability** will check whether any `update` has been run since last release.

#### Additional options:

  * `-preserve-filenames` : Use the rule keys provided by "-rule" to construct the name of output files, this allow to use legacy keys.
  * `-no-language-in-filenames` : Remove language from file name format (ex: "S123.json" instead of "S123_java.json").

#### Deprecated features:

The use of the `-directory` option is deprecated and replaced by the use of `sonarpedia.json` file.
    * `generate`:  Generates html description and json metadata files specified by `-rule` and `-language` parameters at directory specified by `-directory`
    * `update`: Update html and json description files specified by `-language` found at directory specified by `-directory`

