package com.example.pagination.specifications;

import com.example.pagination.utils.FilterValue;
import com.example.pagination.utils.AbstractFilter;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductFilters extends AbstractFilter {

  protected FilterValue<String> name;
  protected FilterValue<BigDecimal> price;

}
