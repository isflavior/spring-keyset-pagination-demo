package com.example.pagination.specifications;

import com.example.pagination.entity.Product;
import com.example.pagination.utils.SortableUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductSort {

  protected String field;
  protected Sort.Direction direction = Sort.Direction.ASC;

  public ProductSort(String field) {
    this.field = SortableUtils.getSortableFieldName(Product.class, field);
  }

  public ProductSort(String field, Sort.Direction direction) {
    this.field = SortableUtils.getSortableFieldName(Product.class, field);
    this.direction = direction;
  }

  public Map<String, Object> toCursorKeys(Product p) {
    Map<String, Object> keys = new HashMap<>();
    keys.put("id", p.getId());

    if (this.field == null) return keys;

    switch (this.field) {
      case "mainName" -> keys.put("mainName", p.getMainName());
      case "price" -> keys.put("price", p.getPrice());
      case "createdAt" -> keys.put("createdAt", p.getCreatedAt());
    }
    return keys;
  }
}
