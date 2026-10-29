package dev.taskflow;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/categories")
public class CategoryController {
 private final CategoryRepository categories;
 public CategoryController(CategoryRepository categories) { this.categories=categories; }
 @GetMapping public List<Category> list() { return categories.findAll(Sort.by("name")); }
}
