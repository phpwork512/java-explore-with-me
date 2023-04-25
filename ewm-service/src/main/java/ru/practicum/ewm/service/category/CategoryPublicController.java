package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.category.model.dto.CategoryDto;
import ru.practicum.ewm.service.category.model.dto.CategoryDtoMapper;
import ru.practicum.ewm.service.compilation.model.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.model.dto.CompilationDtoMapper;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAllCategories(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                              @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Public get all categories: from = {}, size = {}", from, size);
        return CategoryDtoMapper.toCategoryDtoList(categoryService.getAllCategories(from, size));
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@Positive @PathVariable long categoryId) {
        log.info("Public get category: categoryId = {}", categoryId);
        return CategoryDtoMapper.toCategoryDto(categoryService.getCategoryById(categoryId));
    }
}
