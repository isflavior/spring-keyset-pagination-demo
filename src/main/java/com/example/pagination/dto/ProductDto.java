package com.example.pagination.dto;

import com.example.pagination.entity.Product;
import java.math.BigDecimal;
import java.time.Instant;

public record ProductDto(Long id, String name, String barcode, BigDecimal price, Instant createdAt) {
  public static ProductDto from(Product p) {
    return new ProductDto(p.getId(), p.getMainNameText(), p.getBarcode(), p.getPrice(), p.getCreatedAt());
  }
}
