package com.example.pagination.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFilter {
    public Map<String, Object> toCursorFilterMap() {
        Map<String, Object> map = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (FilterValue.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    FilterValue<?> filterValue = (FilterValue<?>) field.get(this);
                    if (filterValue != null && filterValue.getValue() != null) {
                        map.put(field.getName(), filterValue.getValue());
                    }
                } catch (IllegalAccessException ignored) {}
            }
        }
        return map;
    }
}

