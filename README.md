# STIX Interoperability Runner (Work in Progress)

The STIX Interoperability Runner is a Gherkin/Cucumber based testing platform for writing 
and validating STIX interoperability scenarios and tests.

The runner provides gherkin syntax and cucumber steps to easily write meaningful and relevant STIX import, parse, manipulate, and validation features/scenarios.

Given the variance in the producers and consumers of STIX and the wide range of interoperability tests that are possible, 
the runner provides a REST API for getting and submitting data during the tests.  You can:

1. Import STIX from JSON files
1. Submit test start data by submitting to a provided REST endpoint
1. Get STIX data and test metadata at defined points in a test
1. Submit data and test metadata at defined points in a test

The purpose of this functionality is to allow those who are implementing the interoperability tests to choose how they want to validate:

1. They may submit data directly from their application/hardware
1. Completely automate the process
1. Run the tests in a manual process where they submit and validate data / scenarios through a tool like [postman](https://getpostman.com)
1. A test step may be to "affirm" that the producer/consumer's system provides access to the data. (this could support file uploads such as pictures if the community really wanted this..)

By automating, we gain the ability to quickly validate all STIX content against the cost STIX Spec, and then write additional test steps based on the additional needs of interoperability.
This will drastically reduce the size of Interoperability Spec business text, and provide repeatable, testable, common set of objective evidence results for interoperability validation.


The runner will provide:
1. report output of all inputs and outputs
1. Validation of each step and feature
1. A plugable STIX Parser, providing a default parser, but can be replaced with other parsers (Kotlin, Python, Go, etc) 
1. Ability to run specific tests/scenarios that are relevant to the producer/consumer that is validating ("I only want to run the TIP tests")
1. Configuration options for starting points, endpoint ports, and other helpful settings.


# Example Scenario:

Run the Runner in `com.github.stephenott.stix.interop.Runner`

```gherkin
Feature: Simple Attack Pattern Imports

  Scenario: Import and validate a Attack Pattern from a third-party System

    Given a STIX JSON object at "/json/0_simple_object.json"
    When I parse it as STIX
    Then it must have property "key" as type "STRING"
    Then it must be STIX type "attack-pattern"
    When wait for take of object at "GET" "/scenario1"
    When wait for submission of object at "POST" "/scenario1/update" as "sub1"
    Then parse "sub1" as an "AttackPattern"
    And "sub1" must have a different "created_by_ref" property
    Then print the object
    Then print parsed object "sub1"
```

-----

# Raw development output

```
Feature: Simple Attack Pattern Imports

  Scenario: Import and validate a Attack Pattern from a third-party System     # src/test/resources/features/0_simple_import.feature:3
    Given a STIX JSON object at "/json/0_simple_object.json"                   # En.java:1004
    When I parse it as STIX                                                    # En.java:1885
    Then it must have property "key" as type "STRING"                          # En.java:1503
    Then it must be STIX type "attack-pattern"                                 # En.java:1463
Wiremock on localhost:8081
waiting for take at GET /scenario1
running action...CompleterId(id=f4348d3e-4020-489d-ab5b-cafe7767fc09)
class com.github.stephenott.stix.interop.glue.CompleterId
all done action
ALL DONE
    When wait for take of object at "GET" "/scenario1"                         # En.java:1962
Wiremock on localhost:8081
waiting for submission at POST /scenario1/update
running action...CompleterId(id=e0d334ae-60f9-4618-9987-6e1f29ca95eb)
class com.github.stephenott.stix.interop.glue.CompleterId
all done action
submission with id: sub1 has body of: {
    "key": "value",
    "created_by_ref": "cats2",
    "type": "attack-pattern"
}
ALL DONE
    When wait for submission of object at "POST" "/scenario1/update" as "sub1" # En.java:2004
    Then parse "sub1" as an "AttackPattern"                                    # En.java:1503
    And "sub1" must have a different "created_by_ref" property                 # En.java:126
{"key":"value","created_by_ref":"cats","type":"attack-pattern"}
    Then print the object                                                      # En.java:1426
{"key":"value","created_by_ref":"cats2","type":"attack-pattern"}
    Then print parsed object "sub1"                                            # En.java:1463
```