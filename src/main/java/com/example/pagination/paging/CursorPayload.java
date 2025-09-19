package com.example.pagination.paging;

import java.util.Map;

public record CursorPayload(
    String sortField,
    String direction,
    Map<String, Object> filters,
    Map<String, Object> keys
) {}
