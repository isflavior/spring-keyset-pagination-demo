package com.example.pagination.service;

import com.example.pagination.entity.Product;
import com.example.pagination.repository.ProductRepository;
import com.example.pagination.paging.CursorCodec;
import com.example.pagination.paging.CursorPayload;
import com.example.pagination.paging.SortSpec;
import com.example.pagination.search.ProductSpecs;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  public Result scroll(
      String nameContains, BigDecimal price,
      String sort, String dir,
      String cursorToken, String direction, Integer limitReq
  ) {
    int limit = Math.min(limitReq != null && limitReq > 0 ? limitReq : DEFAULT_LIMIT, MAX_LIMIT);

    var spec = ProductSpecs.and(
        ProductSpecs.nameContainsIgnoreCase(nameContains),
        ProductSpecs.priceEquals(price)
    );

    var sortSpec = SortSpec.from(sort == null ? "createdAt" : sort);
    var normalizedDir = "asc".equalsIgnoreCase(dir) ? "asc" : "desc";
    Sort srt = sortSpec.toSort(normalizedDir);

    CursorPayload cursor = codec.decode(cursorToken);

    boolean backward = "backward".equalsIgnoreCase(direction);
    var position = CursorCodec.toPosition(cursor, backward);

    Window<Product> window = repo.findBy(spec, q -> q.sortBy(srt).limit(limit).scroll(position));

    List<Product> items = window.getContent();

    String next = null, prev = null;
    if (!items.isEmpty()) {
      Product first = items.get(0), last = items.get(items.size()-1);
      Map<String,Object> firstKeys = edgeKeys(sortSpec, first);
      Map<String,Object> lastKeys  = edgeKeys(sortSpec, last);

      Map<String,Object> filters = filtersMap(nameContains, price);
      prev = codec.encode(new CursorPayload(sortSpec.prop, normalizedDir, filters, firstKeys));
      next = codec.encode(new CursorPayload(sortSpec.prop, normalizedDir, filters, lastKeys));
    }

    boolean hasPrev = cursor != null;
    boolean hasNext = window.hasNext();

    return new Result(items, next, prev, hasNext, hasPrev);
  }

  private static Map<String,Object> filtersMap(String name, BigDecimal price) {
    Map<String,Object> map = new HashMap<>();
    if (name != null && !name.isBlank()) map.put("name", name);
    if (price != null) map.put("price", price);
    return map;
  }

  private static Map<String,Object> edgeKeys(SortSpec spec, Product p) {
    Map<String,Object> keys = new HashMap<>();
    switch (spec) {
      case NAME -> keys.put("mainNameText", p.getMainNameText());
      case PRICE -> keys.put("price", p.getPrice());
      case CREATED_AT -> keys.put("createdAt", p.getCreatedAt());
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
  ) {}
}
