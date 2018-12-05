Feature: Test comparator

  Scenario: Compare simple values
    Given param a=1
    And param b=1
    Then COMPARE #[a] with #[b]
    And COMPARE 1 with 1

  Scenario: Compare jsons
    Given param json1 =
    """
  {
    "name": "J.*n",
    "age": "\\d+",
    "cars": ["Ford", "~[car]", "Fiat"]
  }
    """
    And param json2 =
    """
  {
	"name": "John",
	"age": 30,
	"cars": ["BMW","Ford","Fiat"]
  }
    """
    Then COMPARE #[json1] with #[json2]
    And COMPARE #[car] with BMW
    Then COMPARE #[json1] with
    """
  {
	"name": "John",
	"age": 30,
	"cars": ["BMW","Ford","Fiat"]
  }
    """

  Scenario: Compare data tables
    Given param a=replaced_value
    And table expectedTable=
      | firstName | lastName |
      | #[a]      | travolta |
      | sam       | .*       |
      | bruce     | ~[name]  |

    Then COMPARE #[expectedTable] against table
      | firstName      | lastName |
      | replaced_value | travolta |
      | sam            | carter   |
      | bruce          | willis   |
    And COMPARE #[name] with willis

  Scenario: Compare empty data tables
    Given table empty_table=
      |  |
    Then COMPARE #[empty_table] against table
      |  |

  Scenario: Compare lists
    Given param a=replaced_value
    And table expectedTable1=
      | pineapples | cherries | .* | strawberries |
    And table expectedTable2=
      | fruits       |
      | pineapples   |
      | cherries     |
      | .*           |
      | strawberries |

    Then COMPARE #[expectedTable1] against table
      | apples | strawberries | pineapples | cherries |
    And COMPARE #[expectedTable2] against table
      | fruits       |
      | apples       |
      | strawberries |
      | pineapples   |
      | cherries     |

  Scenario: Compare resource content containing placeholders
    Given param status=200
    And param contentType=application/json
    And param accept=application/json
    And param body=
    """
{
  "orderType": "KVM"
}
    """
    And param expected from file path placeholders/expected1.json
    Then COMPARE #[expected] with
      """
{
  "status": 200,
  "body": {
    "orderType": "KVM"
  },
  "headers": {
    "Content-type": "application/json",
    "Accept": "application/json"
  }
}
      """
    And COMPARE #[expected] from file path placeholders/actual1.json

