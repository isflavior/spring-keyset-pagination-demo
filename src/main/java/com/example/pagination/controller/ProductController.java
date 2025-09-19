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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  // GET /api/products/scroll?name=mouse&price=19.99&sort=name&direction=asc&navigate=forward&limit=10&cursor=...
  @GetMapping("/scroll")
  public ScrollResponse<ProductDto> scroll(
          @RequestParam(name = "name", required = false) String name,
          @RequestParam(name = "price", required = false) BigDecimal price,
          @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
          @RequestParam(name = "direction", defaultValue = "asc") String direction,
          @RequestParam(name = "navigate", defaultValue = "forward") String navigate,
          @RequestParam(name = "limit", required = false) Integer limit,
          @RequestParam(name = "cursor", required = false) String cursor
  ) {
    ProductFilters filters = new ProductFilters();
    if (StringUtils.hasText(name)) {
      filters.setName(new FilterValue<>(name, FilterOperator.CONTAINS));
    }
    if (price != null) {
      filters.setPrice(new FilterValue<>(price, FilterOperator.GREATER_THAN));
    }

    Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ?
            Sort.Direction.DESC : Sort.Direction.ASC;

    ProductSort sortObject = new ProductSort(sort, sortDirection);

    ProductService.Result products = service.findProducts(filters, sortObject, cursor, navigate, limit);

    return new ScrollResponse<>(
            products.items().stream().map(ProductDto::from).collect(Collectors.toList()),
            new ScrollResponse.Cursors(products.nextCursor(), products.prevCursor()),
            products.hasNext(), products.hasPrev()
    );
  }
}
