package com.example.pagination.specifications;

import com.example.pagination.entity.Product;
import com.example.pagination.entity.ProductName;
import com.example.pagination.utils.FilterOperator;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public interface ProductSpecs {
  static Specification<Product> filterByName(String name, FilterOperator op) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(name)) {
        return criteriaBuilder.conjunction();
      }

      query.distinct(true);

      Join<Product, ProductName> namesJoin = root.join("names");
      var mainName = criteriaBuilder.equal(namesJoin.get("nameNumber"), 0);

      // Allowed operators for this field
      switch (op) {
        case CONTAINS -> {
          Predicate like = criteriaBuilder.like(criteriaBuilder.lower(namesJoin.get("text")), "%" + name.toLowerCase() + "%");
          return criteriaBuilder.and(mainName, like);
        }
        default -> {
          return criteriaBuilder.conjunction();
        }
      }
    };
  }

  static Specification<Product> filterByPrice(BigDecimal price, FilterOperator op) {
    return (root, query, criteriaBuilder) -> {
      if (price == null) {
        return criteriaBuilder.conjunction();
      }

      // Allowed operators for this field
      switch (op) {
        case EQUAL -> {
          return criteriaBuilder.equal(root.get("price"), price);
        }
        case GREATER_THAN -> {
          return criteriaBuilder.gt(root.get("price"), price);
        }
        default -> {
          return criteriaBuilder.conjunction();
        }
      }
    };
  }

  static Specification<Product> filterByBarcode(String code, FilterOperator op) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(code)) {
        return criteriaBuilder.conjunction();
      }

      // Allowed operators for this field
      switch (op) {
        case EQUAL -> {
          return criteriaBuilder.equal(root.get("price"), code);
        }
        case CONTAINS -> {
          return criteriaBuilder.like(root.get("barcode"), code.toLowerCase() + "%");
        }
        default -> {
          return criteriaBuilder.conjunction();
        }
      }
    };
  }
}
