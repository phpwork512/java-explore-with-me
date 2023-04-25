package ru.practicum.ewm.service.category.model.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.compilation.model.dto.CompilationDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryDtoMapper {
    public static Category toCategory(CategoryDto categoryDto) {
        if (categoryDto != null) {
            return Category.builder().id(categoryDto.getId()).name(categoryDto.getName()).build();
        } else {
            return null;
        }
    }

    public static CategoryDto toCategoryDto(Category category) {
        if (category != null) {
            return CategoryDto.builder().id(category.getId()).name(category.getName()).build();
        } else {
            return null;
        }
    }

    public static List<CategoryDto> toCategoryDtoList(Collection<Category> categories) {
        if (categories != null) {
            List<CategoryDto> categoryDtoList = new ArrayList<>();
            for(Category category : categories) {
                categoryDtoList.add(toCategoryDto(category));
            }
            return categoryDtoList;
        } else {
            return null;
        }
    }
}