package com.example.pagination.paging;

import org.springframework.data.domain.Sort;

public enum SortSpec {
  CREATED_AT("createdAt"),
  NAME("mainNameText"),
  PRICE("price");

  public final String prop;
  SortSpec(String p) { this.prop = p; }

  public static SortSpec from(String s) {
    if (s == null) return CREATED_AT;
    for (var v : values()) {
      if (v.name().equalsIgnoreCase(s) || v.prop.equalsIgnoreCase(s)) return v;
      if ((v == NAME && "name".equalsIgnoreCase(s))) return NAME;
    }
    throw new IllegalArgumentException("Unsupported sort: " + s);
  }

  public Sort toSort(String dir) {
    var d = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    return Sort.by(new Sort.Order(d, prop), new Sort.Order(d, "id"));
  }
}
