package com.example.pagination.mapper;

import com.example.pagination.dto.ProductDto;
import com.example.pagination.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "name", source = "mainNameText")
    ProductDto toDto(Product product);
}
