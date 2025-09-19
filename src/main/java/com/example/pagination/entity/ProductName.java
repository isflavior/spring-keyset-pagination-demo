package com.example.pagination.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_name",
  indexes = {
    @Index(name = "ix_product_name_main_lookup", columnList = "name_number,text,id_product"),
    @Index(name = "ix_product_name_by_product", columnList = "id_product,name_number")
  }
)
public class ProductName {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_product", nullable = false)
  private Product product;

  @Column(nullable = false, length = 255)
  private String text;

  @Column(name = "name_number", nullable = false)
  private int nameNumber; // 0 = main, >0 = alternate

  public ProductName() {}
  public ProductName(Product product, String text, int nameNumber) {
    this.product = product; this.text = text; this.nameNumber = nameNumber;
  }

  public Long getId() { return id; }
  public Product getProduct() { return product; }
  public void setProduct(Product product) { this.product = product; }
  public String getText() { return text; }
  public void setText(String text) { this.text = text; }
  public int getNameNumber() { return nameNumber; }
  public void setNameNumber(int nameNumber) { this.nameNumber = nameNumber; }
}
