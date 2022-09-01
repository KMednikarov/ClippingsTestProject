@PriceFilter
Feature: Filter by price - Min and Max fields

  @FilterByMinAndMax
  Scenario Outline: Verification of min and max fields
    Given the user opens the search page
    When the user enters positive numbers in <minPrice> and <maxPrice> fields
    And the search results are received
    Then the price of the products should be between <minPrice> and <maxPrice>
    Examples:
      | minPrice | maxPrice |
      | 25       | 45       |