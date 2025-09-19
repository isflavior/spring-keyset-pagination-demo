package com.example.pagination.utils;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SortableUtils {

  public static Set<String> getSortableFields(Class<?> clazz) {
    return Stream.of(clazz.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Sortable.class))
            .map(field -> {
                Sortable annotation = field.getAnnotation(Sortable.class);
                String name = annotation.value();
                return (name != null && !name.isEmpty()) ? name : field.getName();
            })
            .collect(Collectors.toSet());
  }
}
