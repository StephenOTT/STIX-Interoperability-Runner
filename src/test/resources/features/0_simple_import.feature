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
