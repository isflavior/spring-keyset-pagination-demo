package com.example.pagination.controller;

import com.example.pagination.dto.ProductDto;
import com.example.pagination.dto.ScrollResponse;
import com.example.pagination.service.ProductService;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService service;
  public ProductController(ProductService service) { this.service = service; }

  // GET /api/products/scroll?name=mouse&price=19.99&sort=name&dir=asc&direction=forward&limit=10&cursor=...
  @GetMapping("/scroll")
  public ScrollResponse<ProductDto> scroll(
          @RequestParam(name = "name", required = false) String name,
          @RequestParam(name = "price", required = false) BigDecimal price,
          @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
          @RequestParam(name = "dir", defaultValue = "asc") String dir,
          @RequestParam(name = "direction", defaultValue = "forward") String direction,
          @RequestParam(name = "limit", required = false) Integer limit,
          @RequestParam(name = "cursor", required = false) String cursor
  ) {
    var r = service.scroll(name, price, sort, dir, cursor, direction, limit);
    return new ScrollResponse<>(
        r.items().stream().map(ProductDto::from).collect(Collectors.toList()),
        new ScrollResponse.Cursors(r.nextCursor(), r.prevCursor()),
        r.hasNext(), r.hasPrev()
    );
  }
}
