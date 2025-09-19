package com.example.pagination.dto;

import java.util.List;

public record ScrollResponse<T>(List<T> items, Cursors cursors, boolean hasNext, boolean hasPrev) {
  public record Cursors(String next, String prev) {}
}
