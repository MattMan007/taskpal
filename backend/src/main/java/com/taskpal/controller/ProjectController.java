package com.taskpal.controller;

import com.taskpal.dto.ProjectDTO;
import com.taskpal.security.FirebaseAuthenticationToken;
import com.taskpal.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(
            @RequestBody Map<String, String> projectData,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        ProjectDTO project = projectService.createProject(
            projectData.get("name"),
            projectData.get("description"),
            projectData.get("color"),
            firebaseAuth.getUid()
        );
        
        return ResponseEntity.ok(project);
    }
    
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getUserProjects(Authentication authentication) {
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        List<ProjectDTO> projects = projectService.getUserProjects(firebaseAuth.getUid());
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProject(
            @PathVariable Long projectId,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        return projectService.getProjectById(projectId, firebaseAuth.getUid())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> updates,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        try {
            ProjectDTO updatedProject = projectService.updateProject(
                projectId,
                updates.get("name"),
                updates.get("description"),
                updates.get("color"),
                firebaseAuth.getUid()
            );
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long projectId,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        try {
            projectService.deleteProject(projectId, firebaseAuth.getUid());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{projectId}/members")
    public ResponseEntity<ProjectDTO> addMember(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> memberData,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        try {
            ProjectDTO updatedProject = projectService.addMemberToProject(
                projectId,
                memberData.get("email"),
                firebaseAuth.getUid()
            );
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<ProjectDTO> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        try {
            ProjectDTO updatedProject = projectService.removeMemberFromProject(
                projectId,
                memberId,
                firebaseAuth.getUid()
            );
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProjectDTO>> searchProjects(
            @RequestParam String query,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        List<ProjectDTO> projects = projectService.searchProjects(query, firebaseAuth.getUid());
        return ResponseEntity.ok(projects);
    }
} 