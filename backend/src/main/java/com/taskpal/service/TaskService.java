package com.taskpal.service;

import com.taskpal.dto.TaskDTO;
import com.taskpal.model.Project;
import com.taskpal.model.Task;
import com.taskpal.model.User;
import com.taskpal.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProjectService projectService;
    
    public TaskDTO createTask(String title, String description, Long projectId, 
                             Task.TaskPriority priority, LocalDateTime dueDate, String creatorFirebaseUid) {
        User creator = userService.getUserEntityByFirebaseUid(creatorFirebaseUid);
        Project project = projectService.getProjectEntityById(projectId);
        
        // Check if user is a member of the project
        if (!project.getMembers().contains(creator) && !project.getOwner().equals(creator)) {
            throw new RuntimeException("User is not a member of this project");
        }
        
        Task task = new Task(title, description, project, creator);
        if (priority != null) task.setPriority(priority);
        if (dueDate != null) task.setDueDate(dueDate);
        
        task = taskRepository.save(task);
        return new TaskDTO(task);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDTO> getProjectTasks(Long projectId, String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        Project project = projectService.getProjectEntityById(projectId);
        
        // Check if user is a member of the project
        if (!project.getMembers().contains(user) && !project.getOwner().equals(user)) {
            throw new RuntimeException("User is not a member of this project");
        }
        
        return taskRepository.findByProject(project)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TaskDTO> getUserTasks(String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        List<Project> userProjects = user.getProjects().stream().collect(Collectors.toList());
        
        return taskRepository.findByUserAndProjects(user, userProjects)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TaskDTO> getAssignedTasks(String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        
        return taskRepository.findByAssignee(user)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<TaskDTO> getTaskById(Long taskId, String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        
        return taskRepository.findById(taskId)
                .filter(task -> {
                    Project project = task.getProject();
                    return project.getMembers().contains(user) || project.getOwner().equals(user);
                })
                .map(TaskDTO::new);
    }
    
    public TaskDTO updateTask(Long taskId, String title, String description, 
                             Task.TaskStatus status, Task.TaskPriority priority, 
                             LocalDateTime dueDate, String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Check if user is a member of the project
        Project project = task.getProject();
        if (!project.getMembers().contains(user) && !project.getOwner().equals(user)) {
            throw new RuntimeException("User is not a member of this project");
        }
        
        if (title != null) task.setTitle(title);
        if (description != null) task.setDescription(description);
        if (status != null) task.setStatus(status);
        if (priority != null) task.setPriority(priority);
        if (dueDate != null) task.setDueDate(dueDate);
        
        task = taskRepository.save(task);
        return new TaskDTO(task);
    }
    
    public TaskDTO assignTask(Long taskId, Long assigneeId, String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        Project project = task.getProject();
        if (!project.getMembers().contains(user) && !project.getOwner().equals(user)) {
            throw new RuntimeException("User is not a member of this project");
        }
        
        User assignee = null;
        if (assigneeId != null) {
            assignee = userService.getUserEntityById(assigneeId);
            // Check if assignee is a member of the project
            if (!project.getMembers().contains(assignee) && !project.getOwner().equals(assignee)) {
                throw new RuntimeException("Assignee is not a member of this project");
            }
        }
        
        task.setAssignee(assignee);
        task = taskRepository.save(task);
        return new TaskDTO(task);
    }
    
    public void deleteTask(Long taskId, String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Only task creator or project owner can delete task
        if (!task.getCreator().equals(user) && !task.getProject().getOwner().equals(user)) {
            throw new RuntimeException("Only task creator or project owner can delete task");
        }
        
        taskRepository.delete(task);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByStatus(Task.TaskStatus status, String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        List<Project> userProjects = user.getProjects().stream().collect(Collectors.toList());
        
        return taskRepository.findByProjectsOrderByCreatedAtDesc(userProjects)
                .stream()
                .filter(task -> task.getStatus() == status)
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TaskDTO> getOverdueTasks(String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());
        
        return overdueTasks.stream()
                .filter(task -> {
                    Project project = task.getProject();
                    return project.getMembers().contains(user) || project.getOwner().equals(user);
                })
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksDueSoon(String firebaseUid, int days) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);
        
        List<Task> dueSoonTasks = taskRepository.findByDueDateBetween(now, future);
        
        return dueSoonTasks.stream()
                .filter(task -> {
                    Project project = task.getProject();
                    return project.getMembers().contains(user) || project.getOwner().equals(user);
                })
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }
} 