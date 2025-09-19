package com.example.pagination.specifications;

import com.example.pagination.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public interface ProductSpecs {
  static Specification<Product> nameContainsIgnoreCase(String q) {
    if (q == null || q.isBlank()) return null;
    String pat = "%" + q.toLowerCase() + "%";
    return (root, cq, cb) -> {
      var join = root.join("names");
      var mainName = cb.equal(join.get("nameNumber"), 0);
      var like = cb.like(cb.lower(join.get("text")), pat);
      cq.distinct(true);
      return cb.and(mainName, like);
    };
  }

  static Specification<Product> priceEquals(BigDecimal price) {
    if (price == null) return null;
    return (root, cq, cb) -> cb.equal(root.get("price"), price);
  }

  static Specification<Product> and(Specification<Product>... specs) {
    Specification<Product> result = Specification.where(null);
    for (var s : specs) if (s != null) result = result.and(s);
    return result;
  }
}
