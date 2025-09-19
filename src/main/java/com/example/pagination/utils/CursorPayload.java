package com.example.pagination.utils;

import java.util.Map;

public record CursorPayload(
    String sortField,
    Map<String, Object> filters,
    Map<String, Object> keys
) {}
