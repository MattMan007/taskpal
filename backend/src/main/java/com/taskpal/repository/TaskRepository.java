package com.taskpal.repository;

import com.taskpal.model.Project;
import com.taskpal.model.Task;
import com.taskpal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByProject(Project project);
    
    List<Task> findByAssignee(User assignee);
    
    List<Task> findByCreator(User creator);
    
    List<Task> findByStatus(Task.TaskStatus status);
    
    List<Task> findByPriority(Task.TaskPriority priority);
    
    @Query("SELECT t FROM Task t WHERE t.project = :project AND t.status = :status")
    List<Task> findByProjectAndStatus(@Param("project") Project project, @Param("status") Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.assignee = :assignee AND t.status = :status")
    List<Task> findByAssigneeAndStatus(@Param("assignee") User assignee, @Param("status") Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :start AND :end")
    List<Task> findByDueDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate < :date AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("date") LocalDateTime date);
    
    @Query("SELECT t FROM Task t WHERE t.project IN :projects ORDER BY t.createdAt DESC")
    List<Task> findByProjectsOrderByCreatedAtDesc(@Param("projects") List<Project> projects);
    
    @Query("SELECT t FROM Task t WHERE (t.assignee = :user OR t.creator = :user) AND t.project IN :projects")
    List<Task> findByUserAndProjects(@Param("user") User user, @Param("projects") List<Project> projects);
} 