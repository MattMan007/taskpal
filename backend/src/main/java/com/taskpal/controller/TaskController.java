package com.taskpal.controller;

import com.taskpal.dto.TaskDTO;
import com.taskpal.model.Task;
import com.taskpal.security.FirebaseAuthenticationToken;
import com.taskpal.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @RequestBody Map<String, Object> taskData,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        LocalDateTime dueDate = null;
        if (taskData.get("dueDate") != null) {
            dueDate = LocalDateTime.parse((String) taskData.get("dueDate"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        
        Task.TaskPriority priority = null;
        if (taskData.get("priority") != null) {
            priority = Task.TaskPriority.valueOf((String) taskData.get("priority"));
        }
        
        TaskDTO task = taskService.createTask(
            (String) taskData.get("title"),
            (String) taskData.get("description"),
            Long.valueOf(taskData.get("projectId").toString()),
            priority,
            dueDate,
            firebaseAuth.getUid()
        );
        
        return ResponseEntity.ok(task);
    }
    
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getProjectTasks(
            @PathVariable Long projectId,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        try {
            List<TaskDTO> tasks = taskService.getProjectTasks(projectId, firebaseAuth.getUid());
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskDTO>> getUserTasks(Authentication authentication) {
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        List<TaskDTO> tasks = taskService.getUserTasks(firebaseAuth.getUid());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/assigned")
    public ResponseEntity<List<TaskDTO>> getAssignedTasks(Authentication authentication) {
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        List<TaskDTO> tasks = taskService.getAssignedTasks(firebaseAuth.getUid());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTask(
            @PathVariable Long taskId,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        return taskService.getTaskById(taskId, firebaseAuth.getUid())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long taskId,
            @RequestBody Map<String, Object> updates,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        try {
            LocalDateTime dueDate = null;
            if (updates.get("dueDate") != null) {
                dueDate = LocalDateTime.parse((String) updates.get("dueDate"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            
            Task.TaskStatus status = null;
            if (updates.get("status") != null) {
                status = Task.TaskStatus.valueOf((String) updates.get("status"));
            }
            
            Task.TaskPriority priority = null;
            if (updates.get("priority") != null) {
                priority = Task.TaskPriority.valueOf((String) updates.get("priority"));
            }
            
            TaskDTO updatedTask = taskService.updateTask(
                taskId,
                (String) updates.get("title"),
                (String) updates.get("description"),
                status,
                priority,
                dueDate,
                firebaseAuth.getUid()
            );
            
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{taskId}/assign")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable Long taskId,
            @RequestBody Map<String, Object> assignmentData,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        try {
            Long assigneeId = null;
            if (assignmentData.get("assigneeId") != null) {
                assigneeId = Long.valueOf(assignmentData.get("assigneeId").toString());
            }
            
            TaskDTO updatedTask = taskService.assignTask(taskId, assigneeId, firebaseAuth.getUid());
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        try {
            taskService.deleteTask(taskId, firebaseAuth.getUid());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(
            @PathVariable String status,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        try {
            Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
            List<TaskDTO> tasks = taskService.getTasksByStatus(taskStatus, firebaseAuth.getUid());
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks(Authentication authentication) {
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        List<TaskDTO> tasks = taskService.getOverdueTasks(firebaseAuth.getUid());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/due-soon")
    public ResponseEntity<List<TaskDTO>> getTasksDueSoon(
            @RequestParam(defaultValue = "7") int days,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        List<TaskDTO> tasks = taskService.getTasksDueSoon(firebaseAuth.getUid(), days);
        return ResponseEntity.ok(tasks);
    }
} 