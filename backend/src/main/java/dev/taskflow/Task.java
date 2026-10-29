package dev.taskflow;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="tasks")
public class Task {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,length=120) public String title;
 @Column(nullable=false,length=2000) public String description;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public TaskStatus status;
 @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="category_id") public Category category;
 @Column(nullable=false,updatable=false) public Instant createdAt;
 public LocalDate dueDate;
 @PrePersist void initialize() { createdAt=Instant.now(); }
}
