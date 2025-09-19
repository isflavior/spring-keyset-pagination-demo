package com.example.pagination.utils;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class FilterValue<T> {
  private T value;
  private FilterOperator operator = FilterOperator.EQUAL;

  public FilterValue(T value) {
    this.value = value;
  }

  public FilterValue(T value, FilterOperator operator) {
    this.value = value;
    this.operator = operator;
  }

}
