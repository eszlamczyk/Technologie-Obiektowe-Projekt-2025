package monaditto.cinemaproject.RESTcontrollers;

import monaditto.cinemaproject.category.CategoryCreateStatus;
import monaditto.cinemaproject.category.CategoryDto;
import monaditto.cinemaproject.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping()
    public ResponseEntity<List<CategoryDto>> getCategories() {
        return ResponseEntity.ok().body(categoryService.getCategories());
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable String categoryName) {
        Optional<CategoryDto> optionalCategoryDto = categoryService.getCategoryByName(categoryName);
        return optionalCategoryDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{categoryID}")
    public ResponseEntity<String> updateCategory(@PathVariable Long categoryID,
                                                 @RequestBody CategoryDto categoryDTO) {
        CategoryCreateStatus status = categoryService.editCategory(categoryID, categoryDTO);

        return switch (status) {
            case SUCCESS -> ResponseEntity.ok("Category updated successfully");
            case CATEGORY_NAME_TAKEN -> ResponseEntity.status(HttpStatus.CONFLICT).body("Category name already taken");
            case INCORRECT_ID -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category ID not found");
        };
    }

    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody CategoryDto categoryDTO) {
        CategoryCreateStatus status = categoryService.createCategory(categoryDTO);

        return switch (status) {
            case SUCCESS -> ResponseEntity.ok("Category created successfully");
            case CATEGORY_NAME_TAKEN -> ResponseEntity.status(HttpStatus.CONFLICT).body("Category name already taken");
            case INCORRECT_ID -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category ID not found");
        };
    }

    @DeleteMapping("/{categoryID}")
    public ResponseEntity<String> deleteMovieRoom(@PathVariable Long categoryID) {
        if (categoryService.deleteCategory(categoryID)){
            return ResponseEntity.ok("Successfully deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category ID not found");
    }
}
