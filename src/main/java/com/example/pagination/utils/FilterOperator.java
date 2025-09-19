package com.example.pagination.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.Generated;

/**
 * Filter Operator Enum.
 *
 * <p>Includes all filter operations, with their own 'keyword' and 'precedence'.
 */
@RequiredArgsConstructor
public enum FilterOperator {

  /**
   * Comparison operators
   */
  EQUAL("eq"),
  NOT_EQUAL("ne"),

  CONTAINS("ct"),
  NOT_CONTAINS("nct"),

  STARTS_WITH("sw"),
  ENDS_WITH("ew"),

  GREATER_THAN("gt"),
  GREATER_EQUAL("ge"),
  LESS_THAN("lt"),
  LESS_EQUAL("le"),

  /**
   * Logical operators
   */
  OR("or"),
  AND("and"),
  NOT("not");

  @Getter
  private final String keyword;

  public static FilterOperator fromString(String string) {
    for (FilterOperator filterOperator : values()) {
      if (string.equalsIgnoreCase(filterOperator.getKeyword())) {
        return filterOperator;
      }
    }
    return null;
  }
}
