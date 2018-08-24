# sonar-rule-api

Used to interact with the rule repository in an automated fashion as either an API or a CLI.

As an API, it can be used to:
 1. retrieve rules from RSpec
 1. retrieve rules from a running SonarQube instance
 1. compare two rules
 3. update RSpecs
 3. perform any of the functions available via the CLI

## Usage

The rule-api release is available on repox at [this link](https://repox.sonarsource.com/sonarsource-private-releases/com/sonarsource/rule-api/rule-api/). The following command will automatically download the latest:

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
  * `integrity`: RSpec internal integrity check. Requires -login and -password parameters.

### Language plugin rules files

##### Sonarpedia

To create and maintain html and metadata rules files.
It relies on the `sonarpedia.json` file and a rules directory designated by the `sonarpedia.json` file.
```
(root)
  │
  └──  ./any/where/in/the/hierarchy/rules
                  │                    ├── Sxxxx_(language).html
                  │                    ├── Sxxxx_(language).json
                  │                    ├── Syyyy_(language).html
                  │                    └── Syyyy_(language).json
                  └── sonarpedia.json
```

A repository may contain one or more `sonarpedia.json` file.
`rule-api init`, `rule-api generate` and `rule-api update` must be run from the directory containing the `sonarpedia.json` file.
Therefore when a repository contains several `sonarpedia.json` files, `rule-api ...` must be run several times to keep all the rules updated.

A sonarpedia file may contain more than one language, in which case `rule-api update` will run on the different languages.

By default this `rules` directory is near the `sonarpedia.json` file, but this be set up in any place deeper in the hierarchy.


The format of `sonarpedia.json` is described in the [Sonarpedia-schema.json file](https://github.com/SonarSource/sonar-rule-api/blob/master/sonarpedia-schema.json).
As soon as a `sonarpedia.json` file is present, when running any **One-Click release**,  **Releasability** will check whether any `update` has been run since last release.

#### Commands

  * `init`:  as `init -language foo [-preserve-filenames] [-no-language-in-filenames]`. This generates a new `sonarpedia.json` file pointing on a `rules` directory. This `rules` directory will have to be populated with html description and json metadata files with the next option `generate`.
    * The option  `-preserve-filenames` allows rules filenames as `NoSonar.json`, not only the `S1291.json` format. This is useful for supporting legacy keys. 
    * The option `-no-language-in-filenames` does not include language in the file name format (ex: `S123.json` instead of `S123_java.json`).
  * `generate`: as  `generate -rule S1234 S2345`. Read the `sonarpedia.json` file in the current directory, generates html and json files for designated rules in the `rules` directory. 
  * `update`: Read the `sonarpedia.json` file in the current directory, find the rules and update their html descriptions. It updates `sonarpedia.json` timestamps.

A usual usage of those commands is:  
  * `java -jar ruleapi.jar init -language foo`  once to initialize the `sonarpedia.json` and `rules directory.
  * `java -jar ruleapi.jar generate -rule S1234` everytime you have to add a new rule
  * `java -jar ruleapi.jar update` at every release.

## Using another JIRA instance

Due to a legacy architecture choice, the JIRA instance URL cannot be passed as a command line option.
However, if the ``ruleApi.baseUrl`` system property is set, it will be used as the base URL for JIRA calls
(instead of the default value ``https://jira.sonarsource.com/rest/api/latest/``). Note that this property
must end with a ``/``.
