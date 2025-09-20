package com.example.pagination.utils;

import java.util.Map;

public record CursorPayload(
    Map<String, Object> keys
) {}
