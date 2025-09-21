package com.example.pagination.utils;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SortableUtils {

  public static Set<String> getSortableFields(Class<?> clazz) {
    return Stream.of(clazz.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Sortable.class))
            .map(Field::getName)
            .collect(Collectors.toSet());
  }

  public static String getSortableFieldName(Class<?> clazz, String name) {
    return Stream.of(clazz.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Sortable.class))
            .filter(field -> {
                if (field.getName().equals(name))
                  return true;
                Sortable annotation = field.getAnnotation(Sortable.class);
                String sortableValue = annotation.value();
                return sortableValue != null && !sortableValue.isEmpty() && sortableValue.equals(name);
            })
            .map(Field::getName)
            .findFirst()
            .orElse(null);
  }
}
