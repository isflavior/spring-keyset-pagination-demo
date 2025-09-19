package com.example.pagination.service;

import com.example.pagination.entity.Product;
import com.example.pagination.repository.ProductRepository;
import com.example.pagination.utils.CursorCodec;
import com.example.pagination.utils.CursorPayload;
import com.example.pagination.specifications.ProductSpecs;

import com.example.pagination.utils.SortableUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProductService {

  private static final int DEFAULT_LIMIT = 20;
  private static final int MAX_LIMIT = 200;

  private final ProductRepository repo;
  private final CursorCodec codec;

  public ProductService(ProductRepository repo, CursorCodec codec) {
    this.repo = repo;
    this.codec = codec;
  }

  public Result findProducts(
          String nameContains, BigDecimal price,
          String sortField, String direction,
          String cursorToken, String navigate, Integer limitReq
  ) {
    int limit = Math.min(limitReq != null && limitReq > 0 ? limitReq : DEFAULT_LIMIT, MAX_LIMIT);

    Specification<Product> spec = ProductSpecs.and(
            ProductSpecs.nameContainsIgnoreCase(nameContains),
            ProductSpecs.priceEquals(price)
    );

    Set<String> sortableFields = SortableUtils.getSortableFields(Product.class);
    Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ?
            Sort.Direction.DESC : Sort.Direction.ASC;

    Sort sort;
    if (StringUtils.hasText(sortField) && sortableFields.contains(sortField)) {
      sort = Sort.by(new Sort.Order(sortDirection, sortField), new Sort.Order(sortDirection, "id"));
    } else {
      sort = Sort.by(new Sort.Order(sortDirection, "id"));
    }

    CursorPayload cursor = codec.decode(cursorToken);

    ScrollPosition.Direction navigateDirection = navigate.equalsIgnoreCase("forward") ?
            ScrollPosition.Direction.FORWARD : ScrollPosition.Direction.BACKWARD;
    ScrollPosition position = CursorCodec.toPosition(cursor, navigateDirection);

    Window<Product> window = repo.findBy(spec, q -> q.sortBy(sort).limit(limit).scroll(position));

    List<Product> items = window.getContent();

    String next = null, prev = null;
    if (!items.isEmpty()) {
      Product first = items.getFirst();
      Product last = items.getLast();

      Map<String, Object> firstKeys = edgeKeys(sortField, first);
      Map<String, Object> lastKeys = edgeKeys(sortField, last);

      Map<String, Object> filters = filtersMap(nameContains, price);
      prev = codec.encode(new CursorPayload(sortField, direction, filters, firstKeys));
      next = codec.encode(new CursorPayload(sortField, direction, filters, lastKeys));
    }

    boolean hasPrev = cursor != null;
    boolean hasNext = window.hasNext();

    return new Result(items, next, prev, hasNext, hasPrev);
  }

  private static Map<String, Object> filtersMap(String name, BigDecimal price) {
    Map<String, Object> map = new HashMap<>();
    if (name != null && !name.isBlank()) map.put("name", name);
    if (price != null) map.put("price", price);
    return map;
  }

  private static Map<String, Object> edgeKeys(String sortField, Product p) {
    Map<String, Object> keys = new HashMap<>();
    switch (sortField) {
      case "name" -> keys.put("mainNameText", p.getMainNameText());
      case "price" -> keys.put("price", p.getPrice());
      case "created_at" -> keys.put("createdAt", p.getCreatedAt());
    }
    keys.put("id", p.getId());
    return keys;
  }

  public record Result(
          List<Product> items,
          String nextCursor,
          String prevCursor,
          boolean hasNext,
          boolean hasPrev
  ) {
  }
}
