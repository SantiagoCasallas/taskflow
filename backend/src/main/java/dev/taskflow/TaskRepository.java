package dev.taskflow;
import org.springframework.data.jpa.repository.*;
public interface TaskRepository extends JpaRepository<Task,Long>, JpaSpecificationExecutor<Task> {}
