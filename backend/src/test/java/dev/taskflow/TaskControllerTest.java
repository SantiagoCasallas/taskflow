package dev.taskflow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.springframework.web.server.ResponseStatusException;
class TaskControllerTest {
 private final TaskRepository tasks=mock(TaskRepository.class);
 private final CategoryRepository categories=mock(CategoryRepository.class);
 private final TaskController controller=new TaskController(tasks,categories);
 @Test void rejectsInvalidPaginationAndSort() {
  assertThrows(ResponseStatusException.class,()->controller.list("",null,null,-1,10,"createdAt","desc"));
  assertThrows(ResponseStatusException.class,()->controller.list("",null,null,0,101,"createdAt","desc"));
  assertThrows(ResponseStatusException.class,()->controller.list("",null,null,0,10,"title","desc"));
  verifyNoInteractions(tasks);
 }
 @Test void missingTaskReturns404() {
  when(tasks.findById(7L)).thenReturn(Optional.empty());
  assertEquals(404,assertThrows(ResponseStatusException.class,()->controller.delete(7L)).getStatusCode().value());
  verify(tasks,never()).delete(any(Task.class));
 }
 @Test void unknownCategoryDoesNotSaveTask() {
  when(categories.findById(8L)).thenReturn(Optional.empty());
  assertThrows(ResponseStatusException.class,()->controller.create(new TaskController.Input("Tarea","",TaskStatus.PENDING,8L,null)));
  verify(tasks,never()).save(any());
 }
}
