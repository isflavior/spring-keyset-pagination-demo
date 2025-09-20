package com.example.pagination.dto;

import java.util.List;

public record ScrollResponse<T>(List<T> items, Cursors cursors) {
  public record Cursors(String next, String prev) {}
}
