package monaditto.cinemaproject.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDto::categoryToCategoryDto)
                .toList();
    }

    public boolean createCategory(CategoryDto categoryDto) {
        if (categoryRepository.findByCategoryName(categoryDto.categoryName()).isPresent()) {
            return false;
        }
        Category category = new Category(categoryDto.categoryName());
        categoryRepository.save(category);
        return true;
    }

    public Optional<CategoryDto> getCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName)
                .map(CategoryDto::categoryToCategoryDto);
    }

    public List<Long> getCategoryIdsByName(List<String> categoryNames) {
        return categoryNames.stream()
                .map(this::getCategoryByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(CategoryDto::id)
                .toList();
    }

    public boolean editCategory(Long id, CategoryDto categoryDto) {

        Optional<Category> categoryWithName = categoryRepository.findByCategoryName(categoryDto.categoryName());

        if (categoryWithName.isPresent() &&
                categoryDto.categoryName().equals(categoryWithName.get().getCategoryName()) &&
                !Objects.equals(categoryWithName.get().getCategoryId(), id)) {
            return false;
        }

        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setCategoryName(categoryDto.categoryName());
            categoryRepository.save(category);
            return true;
        }
        return false;
    }

    public boolean deleteCategory(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            categoryRepository.delete(category);
            return true;
        }
        return false;
    }

    public List<String> deleteCategories(List<Long> ids) {
        List<String> deletedCategories = new ArrayList<>();
        for (Long id : ids) {
            Optional<Category> optionalCategory = categoryRepository.findById(id);
            if (optionalCategory.isPresent()) {
                Category category = optionalCategory.get();
                categoryRepository.delete(category);
                deletedCategories.add(category.getCategoryName());
            }
        }
        return deletedCategories;
    }
}
