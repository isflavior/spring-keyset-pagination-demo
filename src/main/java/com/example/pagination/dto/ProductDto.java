package com.example.pagination.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    Long id;
    String name;
    String barcode;
    BigDecimal price;
    Instant createdAt;
}
