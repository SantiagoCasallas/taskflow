package dev.taskflow;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/tasks")
public class TaskController {
 private final TaskRepository tasks; private final CategoryRepository categories;
 public TaskController(TaskRepository tasks, CategoryRepository categories) { this.tasks=tasks; this.categories=categories; }
 public record Input(@NotBlank @Size(max=120) String title, @NotNull @Size(max=2000) String description,
  @NotNull TaskStatus status, @NotNull Long categoryId, LocalDate dueDate) {}
 public record TaskPage(List<Task> content,long totalElements,int totalPages,int number,int size) {}
 @GetMapping public TaskPage list(@RequestParam(defaultValue="") @Size(max=120) String search,
  @RequestParam(required=false) TaskStatus status,@RequestParam(required=false) Long categoryId,
  @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,
  @RequestParam(defaultValue="createdAt") String sort,@RequestParam(defaultValue="desc") String direction) {
  if(page<0 || size<1 || size>100 || !Set.of("createdAt","dueDate").contains(sort) || !Set.of("asc","desc").contains(direction))
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Paginación u orden inválido");
  Specification<Task> filter=(r,q,b)->b.conjunction();
  if(!search.isBlank()) { String term="%"+search.trim().toLowerCase(Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
   filter=filter.and((r,q,b)->b.or(b.like(b.lower(r.get("title")),term,'!'),b.like(b.lower(r.get("description")),term,'!'))); }
  if(status!=null) filter=filter.and((r,q,b)->b.equal(r.get("status"),status));
  if(categoryId!=null) filter=filter.and((r,q,b)->b.equal(r.get("category").get("id"),categoryId));
  Page<Task> result=tasks.findAll(filter,PageRequest.of(page,size,Sort.by(Sort.Direction.fromString(direction),sort).and(Sort.by("id"))));
  return new TaskPage(result.getContent(),result.getTotalElements(),result.getTotalPages(),result.getNumber(),result.getSize());
 }
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Task create(@Valid @RequestBody Input input) { return save(new Task(),input); }
 @PutMapping("/{id}") public Task update(@PathVariable Long id,@Valid @RequestBody Input input) { return save(find(id),input); }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { tasks.delete(find(id)); }
 private Task find(Long id) { return tasks.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Tarea no encontrada")); }
 private Task save(Task t,Input i) {
  t.category=categories.findById(i.categoryId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Categoría no encontrada"));
  t.title=i.title().trim(); t.description=i.description().trim(); t.status=i.status(); t.dueDate=i.dueDate(); return tasks.save(t);
 }
}
