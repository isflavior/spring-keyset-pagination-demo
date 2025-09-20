package com.example.pagination.specifications;

import com.example.pagination.utils.FilterValue;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductFilters {

  protected FilterValue<String> name;
  protected FilterValue<BigDecimal> price;

}
