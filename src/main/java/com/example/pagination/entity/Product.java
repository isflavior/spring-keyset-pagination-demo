package com.example.pagination.entity;

import com.example.pagination.utils.Sortable;
import jakarta.persistence.*;
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
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Sortable
  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal price;

  @Column(nullable = false, columnDefinition = "timestamp")
  private Instant createdAt = Instant.now();

  // Main name for sorting/filtering convenience; derived from product_name table
  @Sortable("name")
  @Formula("(select pn.text from product_name pn where pn.id_product = id and pn.name_number = 0)")
  private String mainNameText;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<ProductName> names = new ArrayList<>();

  public Product() {}
  public Product(BigDecimal price, Instant createdAt) {
    this.price = price; this.createdAt = createdAt;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public List<ProductName> getNames() { return names; }
  public void setNames(List<ProductName> names) { this.names = names; }
  public String getMainNameText() { return mainNameText; }
}
