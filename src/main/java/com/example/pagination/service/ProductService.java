package com.example.pagination.service;

import com.example.pagination.dto.ProductDto;
import com.example.pagination.dto.ScrollResponse;
import com.example.pagination.entity.Product;
import com.example.pagination.mapper.ProductMapper;
import com.example.pagination.repository.ProductRepository;
import com.example.pagination.specifications.ProductFilters;
import com.example.pagination.specifications.ProductSort;
import com.example.pagination.specifications.ProductSpecs;
import com.example.pagination.utils.CursorCodec;
import com.example.pagination.utils.CursorPayload;
import com.example.pagination.utils.FilterOperator;
import com.example.pagination.utils.SortableUtils;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProductService {

  private static final int DEFAULT_LIMIT = 20;
  private static final int MAX_LIMIT = 200;

  private final ProductRepository repo;
  private final CursorCodec codec;
  private final ProductMapper productMapper;

  public ProductService(ProductRepository repo, CursorCodec codec, ProductMapper productMapper) {
    this.repo = repo;
    this.codec = codec;
    this.productMapper = productMapper;
  }

  /**
   * Finds products using keyset pagination, supporting global search, filters, sorting, and cursor-based navigation.
   *
   * @param search Global search string (matches name or barcode). If present, filters are ignored.
   * @param filters Field-specific filters (name, price, etc.)
   * @param sortObject Sorting field and direction
   * @param cursorToken Encoded cursor for pagination
   * @param navigate Direction to paginate ("forward" or "backward")
   * @param perPage Number of items per page (limit)
   * @return ScrollResponse containing product DTOs and next/prev cursors
   */
  public ScrollResponse<ProductDto> findProducts(
          String search,
          ProductFilters filters,
          ProductSort sortObject,
          String cursorToken, String navigate, Integer perPage
  ) {
    // Determine the page size, enforcing min/max limits
    int limit = Math.min(perPage != null && perPage > 0 ? perPage : DEFAULT_LIMIT, MAX_LIMIT);

    // Start with an empty specification (no filters)
    Specification<Product> spec = Specification.where(null);

    // If global search is used, filter by name or barcode and ignore other filters
    if (StringUtils.hasText(search)) {
      spec = spec.or(ProductSpecs.filterByName(search, FilterOperator.CONTAINS));
      spec = spec.or(ProductSpecs.filterByBarcode(search, FilterOperator.STARTS_WITH));
    } else {
      // Otherwise, apply field-specific filters if present
      if (filters.getName() != null) {
        spec = spec.and(ProductSpecs.filterByName(filters.getName().getValue(), filters.getName().getOperator()));
      }
      if (filters.getPrice() != null) {
        spec = spec.and(ProductSpecs.filterByPrice(filters.getPrice().getValue(), filters.getPrice().getOperator()));
      }
    }

    // Build the sort order, always including 'id' for stable keyset pagination
    Sort sort;
    Set<String> sortableFields = SortableUtils.getSortableFields(Product.class);
    String sortField = sortObject.getField();

    if (StringUtils.hasText(sortField) && sortableFields.contains(sortField)) {
      sort = Sort.by(new Sort.Order(sortObject.getDirection(), sortField), new Sort.Order(sortObject.getDirection(), "id"));
    } else {
      sort = Sort.by(new Sort.Order(sortObject.getDirection(), "id"));
    }

    // Decode the cursor token to get the current position
    CursorPayload cursor = codec.decode(cursorToken);

    // Determine the scroll direction (forward/backward)
    ScrollPosition.Direction navigateDirection = navigate.equalsIgnoreCase("backward") ?
            ScrollPosition.Direction.BACKWARD : ScrollPosition.Direction.FORWARD;
    // Convert cursor payload to ScrollPosition
    ScrollPosition position = CursorCodec.toPosition(cursor, navigateDirection);

    // Query the repository using specification, sort, limit, and scroll position
    Window<Product> window = repo.findBy(spec, q -> q.sortBy(sort).limit(limit).scroll(position));

    // Map entities to DTOs
    List<ProductDto> items = window.getContent().stream().map(productMapper::toDto).toList();

    // Prepare next/prev cursor tokens for pagination
    String next = null, prev = null;
    if (!window.getContent().isEmpty()) {
      Product first = window.getContent().getFirst();
      Product last = window.getContent().getLast();

      Map<String, Object> firstKeys = sortObject.toCursorKeys(first);
      Map<String, Object> lastKeys = sortObject.toCursorKeys(last);

      prev = codec.encode(new CursorPayload(firstKeys));
      next = codec.encode(new CursorPayload(lastKeys));
    }

    // Determine if there are previous/next pages
    boolean hasPrev = navigateDirection == ScrollPosition.Direction.FORWARD ? cursor != null : window.hasNext();
    boolean hasNext = navigateDirection == ScrollPosition.Direction.FORWARD ? window.hasNext() : cursor != null;

    // Return paginated response
    return new ScrollResponse<>(items, hasNext ? next : null, hasPrev ? prev : null);
  }
}
