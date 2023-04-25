package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.common.pagination.PaginationCalculator;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.ewm.service.exceptions.CategoryHaveLinkedEventsException;
import ru.practicum.ewm.service.exceptions.CategoryNameNotUniqueException;
import ru.practicum.ewm.service.exceptions.CategoryNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventService eventService;

    public Category getCategoryById(long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            throw new CategoryNotFoundException("Категория " + categoryId + " не найдена");
        } else {
            return optionalCategory.get();
        }
    }

    public List<Category> getAllCategories(int from, int size) {
        Pageable page = PaginationCalculator.getPage(from, size);
        return categoryRepository.findAll(page).getContent();
    }

    public Category create(Category category) {
        Category storageCategory;
        try {
            storageCategory = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new CategoryNameNotUniqueException("Название категории не уникально");
        }

        return storageCategory;
    }

    public Category update(Category category, long categoryId) {
        Category storageCategory;

        getCategoryById(categoryId);
        category.setId(categoryId);

        try {
            storageCategory = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new CategoryNameNotUniqueException("Название категории не уникально");
        }

        return storageCategory;
    }

    public void delete(long categoryId) {
        Category category = getCategoryById(categoryId);
        if (eventService.getEventsCountByCategoryId(categoryId) > 0) {
            throw new CategoryHaveLinkedEventsException("С категорией " + categoryId + " связаны события");
        } else {
            categoryRepository.delete(category);
        }
    }
}