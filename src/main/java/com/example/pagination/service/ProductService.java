package com.example.pagination.service;

import com.example.pagination.entity.Product;
import com.example.pagination.repository.ProductRepository;
import com.example.pagination.specifications.ProductFilters;
import com.example.pagination.specifications.ProductSort;
import com.example.pagination.utils.CursorCodec;
import com.example.pagination.utils.CursorPayload;
import com.example.pagination.specifications.ProductSpecs;

import com.example.pagination.utils.FilterOperator;
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
          String globalSearch,
          ProductFilters filters,
          ProductSort sortObject,
          String cursorToken, String navigate, Integer perPage
  ) {
    int limit = Math.min(perPage != null && perPage > 0 ? perPage : DEFAULT_LIMIT, MAX_LIMIT);

    Specification<Product> spec = Specification.where(null);

    // If the user uses the global search, find by name or barcode and ignore other filters
    if (StringUtils.hasText(globalSearch)) {
      spec = spec.or(ProductSpecs.filterByName(globalSearch, FilterOperator.CONTAINS));
      spec = spec.or(ProductSpecs.filterByBarcode(globalSearch, FilterOperator.CONTAINS));
    } else {
      if (filters.getName() != null) {
        spec = spec.and(ProductSpecs.filterByName(filters.getName().getValue(), filters.getName().getOperator()));
      }
      if (filters.getPrice() != null) {
        spec = spec.and(ProductSpecs.filterByPrice(filters.getPrice().getValue(), filters.getPrice().getOperator()));
      }
    }

    Sort sort;
    Set<String> sortableFields = SortableUtils.getSortableFields(Product.class);
    String sortField = sortObject.getField();

    if (StringUtils.hasText(sortField) && sortableFields.contains(sortField)) {
      sort = Sort.by(new Sort.Order(sortObject.getDirection(), sortField), new Sort.Order(sortObject.getDirection(), "id"));
    } else {
      sort = Sort.by(new Sort.Order(sortObject.getDirection(), "id"));
    }

    CursorPayload cursor = codec.decode(cursorToken);

    ScrollPosition.Direction navigateDirection = navigate.equalsIgnoreCase("backward") ?
            ScrollPosition.Direction.BACKWARD : ScrollPosition.Direction.FORWARD;
    ScrollPosition position = CursorCodec.toPosition(cursor, navigateDirection);

    Window<Product> window = repo.findBy(spec, q -> q.sortBy(sort).limit(limit).scroll(position));

    List<Product> items = window.getContent();

    String next = null, prev = null;
    if (!items.isEmpty()) {
      Product first = items.getFirst();
      Product last = items.getLast();

      Map<String, Object> firstKeys = sortObject.toCursorKeys(first);
      Map<String, Object> lastKeys = sortObject.toCursorKeys(last);

      prev = codec.encode(new CursorPayload(firstKeys));
      next = codec.encode(new CursorPayload(lastKeys));
    }

    boolean hasPrev = navigateDirection == ScrollPosition.Direction.FORWARD ? cursor != null : window.hasNext();
    boolean hasNext = navigateDirection == ScrollPosition.Direction.FORWARD ? window.hasNext() : cursor != null;

    return new Result(items, hasNext ? next : null, hasPrev ? prev : null);
  }

  public record Result(
          List<Product> items,
          String nextCursor,
          String prevCursor
  ) {
  }
}
