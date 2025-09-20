package com.example.pagination.entity;

import com.example.pagination.utils.Sortable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products",
  indexes = {
    @Index(name = "ix_products_created_id", columnList = "createdAt,id"),
    @Index(name = "ix_products_price_id", columnList = "price,id")
  }
)
@Getter
@Setter
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Sortable
  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal price;

  @Column(nullable = false, columnDefinition = "timestamp")
  private Instant createdAt = Instant.now();

  @Column(nullable = false)
  private String barcode;

  // Main name for sorting/filtering convenience; derived from product_name table
  @Sortable("name")
  @Formula("(select pn.text from product_name pn where pn.id_product = id and pn.name_number = 0)")
  private String mainName;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<ProductName> names = new ArrayList<>();
}
