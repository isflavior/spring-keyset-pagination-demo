package com.example.pagination.specifications;

import com.example.pagination.entity.Product;
import com.example.pagination.utils.AbstractFilter;
import com.example.pagination.utils.FilterValue;
import lombok.*;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductSort {

  protected String field;
  protected Sort.Direction direction = Sort.Direction.ASC;

  public ProductSort(String field) {
    this.field = field;
    this.direction = Sort.Direction.ASC;
  }

  public Map<String, Object> toCursorKeys(Product p) {
    Map<String, Object> keys = new HashMap<>();
    switch (this.field) {
      case "name" -> keys.put("mainNameText", p.getMainNameText());
      case "price" -> keys.put("price", p.getPrice());
      case "created_at" -> keys.put("createdAt", p.getCreatedAt());
    }
    keys.put("id", p.getId());
    return keys;
  }
}
