package com.example.pagination.utils;

import java.util.Map;

public record CursorPayload(
    String sortField,
    String direction,
    Map<String, Object> filters,
    Map<String, Object> keys
) {}
