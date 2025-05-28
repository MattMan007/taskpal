import 'package:flutter/material.dart';

import '../models/project.dart';
import '../services/api_service.dart';

class ProjectProvider extends ChangeNotifier {
  final ApiService _apiService = ApiService();

  List<Project> _projects = [];
  Project? _selectedProject;
  bool _isLoading = false;
  String? _error;

  List<Project> get projects => _projects;
  Project? get selectedProject => _selectedProject;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> loadProjects() async {
    try {
      _setLoading(true);
      _error = null;
      _projects = await _apiService.getProjects();
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  Future<void> loadProject(int projectId) async {
    try {
      _setLoading(true);
      _error = null;
      _selectedProject = await _apiService.getProject(projectId);
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> createProject({
    required String name,
    String? description,
    String? color,
  }) async {
    try {
      _setLoading(true);
      _error = null;

      final newProject = await _apiService.createProject(
        name: name,
        description: description,
        color: color,
      );

      _projects.insert(0, newProject);
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> updateProject(
    int projectId, {
    String? name,
    String? description,
    String? color,
  }) async {
    try {
      _setLoading(true);
      _error = null;

      final updatedProject = await _apiService.updateProject(
        projectId,
        name: name,
        description: description,
        color: color,
      );

      final index = _projects.indexWhere((p) => p.id == projectId);
      if (index != -1) {
        _projects[index] = updatedProject;
      }

      if (_selectedProject?.id == projectId) {
        _selectedProject = updatedProject;
      }

      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> deleteProject(int projectId) async {
    try {
      _setLoading(true);
      _error = null;

      await _apiService.deleteProject(projectId);
      _projects.removeWhere((p) => p.id == projectId);

      if (_selectedProject?.id == projectId) {
        _selectedProject = null;
      }

      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> addMemberToProject(int projectId, String email) async {
    try {
      _setLoading(true);
      _error = null;

      final updatedProject = await _apiService.addMemberToProject(projectId, email);

      final index = _projects.indexWhere((p) => p.id == projectId);
      if (index != -1) {
        _projects[index] = updatedProject;
      }

      if (_selectedProject?.id == projectId) {
        _selectedProject = updatedProject;
      }

      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> removeMemberFromProject(int projectId, int memberId) async {
    try {
      _setLoading(true);
      _error = null;

      final updatedProject = await _apiService.removeMemberFromProject(projectId, memberId);

      final index = _projects.indexWhere((p) => p.id == projectId);
      if (index != -1) {
        _projects[index] = updatedProject;
      }

      if (_selectedProject?.id == projectId) {
        _selectedProject = updatedProject;
      }

      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<List<Project>> searchProjects(String query) async {
    try {
      return await _apiService.searchProjects(query);
    } catch (e) {
      _error = e.toString();
      return [];
    }
  }

  void clearSelectedProject() {
    _selectedProject = null;
    notifyListeners();
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }

  void _setLoading(bool loading) {
    _isLoading = loading;
    notifyListeners();
  }

  // Helper methods
  Project? getProjectById(int projectId) {
    try {
      return _projects.firstWhere((p) => p.id == projectId);
    } catch (e) {
      return null;
    }
  }

  List<Project> getOwnedProjects(int userId) {
    return _projects.where((p) => p.owner.id == userId).toList();
  }

  List<Project> getMemberProjects(int userId) {
    return _projects.where((p) => p.members.any((m) => m.id == userId)).toList();
  }
} 