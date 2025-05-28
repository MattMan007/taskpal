package com.taskpal.service;

import com.taskpal.dto.ProjectDTO;
import com.taskpal.model.Project;
import com.taskpal.model.User;
import com.taskpal.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserService userService;
    
    public ProjectDTO createProject(String name, String description, String color, String ownerFirebaseUid) {
        User owner = userService.getUserEntityByFirebaseUid(ownerFirebaseUid);
        
        Project project = new Project(name, description, owner);
        project.setColor(color);
        project.addMember(owner); // Owner is automatically a member
        
        project = projectRepository.save(project);
        
        ProjectDTO projectDTO = new ProjectDTO(project);
        projectDTO.setTotalTasks(0L);
        projectDTO.setCompletedTasks(0L);
        
        return projectDTO;
    }
    
    @Transactional(readOnly = true)
    public List<ProjectDTO> getUserProjects(String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        List<Project> projects = projectRepository.findByUserMembership(user);
        
        return projects.stream()
                .map(project -> {
                    ProjectDTO dto = new ProjectDTO(project);
                    dto.setTotalTasks(projectRepository.countTasksByProject(project));
                    dto.setCompletedTasks(projectRepository.countCompletedTasksByProject(project));
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<ProjectDTO> getProjectById(Long projectId, String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        
        return projectRepository.findById(projectId)
                .filter(project -> project.getOwner().equals(user) || project.getMembers().contains(user))
                .map(project -> {
                    ProjectDTO dto = new ProjectDTO(project);
                    dto.setTotalTasks(projectRepository.countTasksByProject(project));
                    dto.setCompletedTasks(projectRepository.countCompletedTasksByProject(project));
                    return dto;
                });
    }
    
    public ProjectDTO updateProject(Long projectId, String name, String description, String color, String ownerFirebaseUid) {
        User owner = userService.getUserEntityByFirebaseUid(ownerFirebaseUid);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (!project.getOwner().equals(owner)) {
            throw new RuntimeException("Only project owner can update project");
        }
        
        if (name != null) project.setName(name);
        if (description != null) project.setDescription(description);
        if (color != null) project.setColor(color);
        
        project = projectRepository.save(project);
        
        ProjectDTO projectDTO = new ProjectDTO(project);
        projectDTO.setTotalTasks(projectRepository.countTasksByProject(project));
        projectDTO.setCompletedTasks(projectRepository.countCompletedTasksByProject(project));
        
        return projectDTO;
    }
    
    public void deleteProject(Long projectId, String ownerFirebaseUid) {
        User owner = userService.getUserEntityByFirebaseUid(ownerFirebaseUid);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (!project.getOwner().equals(owner)) {
            throw new RuntimeException("Only project owner can delete project");
        }
        
        projectRepository.delete(project);
    }
    
    public ProjectDTO addMemberToProject(Long projectId, String memberEmail, String ownerFirebaseUid) {
        User owner = userService.getUserEntityByFirebaseUid(ownerFirebaseUid);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (!project.getOwner().equals(owner)) {
            throw new RuntimeException("Only project owner can add members");
        }
        
        User member = userService.getUserByEmail(memberEmail)
                .map(dto -> userService.getUserEntityById(dto.getId()))
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        project.addMember(member);
        project = projectRepository.save(project);
        
        ProjectDTO projectDTO = new ProjectDTO(project);
        projectDTO.setTotalTasks(projectRepository.countTasksByProject(project));
        projectDTO.setCompletedTasks(projectRepository.countCompletedTasksByProject(project));
        
        return projectDTO;
    }
    
    public ProjectDTO removeMemberFromProject(Long projectId, Long memberId, String ownerFirebaseUid) {
        User owner = userService.getUserEntityByFirebaseUid(ownerFirebaseUid);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (!project.getOwner().equals(owner)) {
            throw new RuntimeException("Only project owner can remove members");
        }
        
        User member = userService.getUserEntityById(memberId);
        
        if (member.equals(owner)) {
            throw new RuntimeException("Cannot remove project owner");
        }
        
        project.removeMember(member);
        project = projectRepository.save(project);
        
        ProjectDTO projectDTO = new ProjectDTO(project);
        projectDTO.setTotalTasks(projectRepository.countTasksByProject(project));
        projectDTO.setCompletedTasks(projectRepository.countCompletedTasksByProject(project));
        
        return projectDTO;
    }
    
    @Transactional(readOnly = true)
    public List<ProjectDTO> searchProjects(String query, String firebaseUid) {
        User user = userService.getUserEntityByFirebaseUid(firebaseUid);
        List<Project> projects = projectRepository.findByNameContainingIgnoreCaseAndUserMembership(query, user);
        
        return projects.stream()
                .map(project -> {
                    ProjectDTO dto = new ProjectDTO(project);
                    dto.setTotalTasks(projectRepository.countTasksByProject(project));
                    dto.setCompletedTasks(projectRepository.countCompletedTasksByProject(project));
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    // Helper method to get Project entity (for internal use)
    @Transactional(readOnly = true)
    public Project getProjectEntityById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
} 