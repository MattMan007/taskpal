package com.taskpal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taskpal.model.Project;
import com.taskpal.model.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByOwner(User owner);
    
    @Query("SELECT p FROM Project p WHERE p.owner = :user OR :user MEMBER OF p.members")
    List<Project> findByUserMembership(@Param("user") User user);
    
    @Query("SELECT p FROM Project p WHERE p.name ILIKE %:name% AND (p.owner = :user OR :user MEMBER OF p.members)")
    List<Project> findByNameContainingIgnoreCaseAndUserMembership(@Param("name") String name, @Param("user") User user);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project = :project")
    Long countTasksByProject(@Param("project") Project project);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project = :project AND t.status = 'DONE'")
    Long countCompletedTasksByProject(@Param("project") Project project);
} 