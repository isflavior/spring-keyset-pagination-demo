package com.example.pagination.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import java.util.List;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScrollResponse<T> {
    List<T> items;
    String nextCursor;
    String prevCursor;
}
