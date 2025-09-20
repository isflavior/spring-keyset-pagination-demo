package com.example.pagination.controller;

import com.example.pagination.dto.ProductDto;
import com.example.pagination.dto.ScrollResponse;
import com.example.pagination.service.ProductService;
import com.example.pagination.specifications.ProductFilters;
import com.example.pagination.specifications.ProductSort;
import com.example.pagination.utils.FilterOperator;
import com.example.pagination.utils.FilterValue;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  /**
   * Scroll endpoint for keyset pagination and filtering of products.
   *
   * Example: GET /api/products/scroll?name=mouse&price=19.99&sort=name&direction=asc&navigate=forward&limit=10&cursor=...
   *
   * @param search Global search string (matches name/barcode)
   * @param name Filter by product name (contains)
   * @param price Filter by price (greater than)
   * @param sort Field to sort by (default: createdAt)
   * @param direction Sort direction (asc/desc)
   * @param navigate Pagination direction (forward/backward)
   * @param limit Number of items per page
   * @param cursor Encoded cursor for keyset pagination
   * @return Paginated response with products and next/prev cursors
   */
  @GetMapping("/scroll")
  public ScrollResponse<ProductDto> scroll(
          @RequestParam(name = "search", required = false) String search,
          @RequestParam(name = "name", required = false) String name,
          @RequestParam(name = "price", required = false) java.math.BigDecimal price,
          @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
          @RequestParam(name = "direction", defaultValue = "asc") String direction,
          @RequestParam(name = "navigate", defaultValue = "forward") String navigate,
          @RequestParam(name = "limit", required = false) Integer limit,
          @RequestParam(name = "cursor", required = false) String cursor
  ) {
    // Build filter object from request parameters
    ProductFilters filters = new ProductFilters();
    if (org.springframework.util.StringUtils.hasText(name)) {
      filters.setName(new com.example.pagination.utils.FilterValue<>(name, com.example.pagination.utils.FilterOperator.CONTAINS));
    }
    if (price != null) {
      filters.setPrice(new com.example.pagination.utils.FilterValue<>(price, com.example.pagination.utils.FilterOperator.GREATER_THAN));
    }

    // Parse sort direction and build sort object
    org.springframework.data.domain.Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ?
            org.springframework.data.domain.Sort.Direction.DESC : org.springframework.data.domain.Sort.Direction.ASC;
    ProductSort sortObject = new ProductSort(sort, sortDirection);

    // Delegate to service for paginated product retrieval
    return service.findProducts(search, filters, sortObject, cursor, navigate, limit);
  }
}
