import 'package:flutter/material.dart';

import '../models/task.dart';
import '../services/api_service.dart';

class TaskProvider extends ChangeNotifier {
  final ApiService _apiService = ApiService();

  List<Task> _tasks = [];
  List<Task> _assignedTasks = [];
  List<Task> _projectTasks = [];
  Task? _selectedTask;
  bool _isLoading = false;
  String? _error;

  List<Task> get tasks => _tasks;
  List<Task> get assignedTasks => _assignedTasks;
  List<Task> get projectTasks => _projectTasks;
  Task? get selectedTask => _selectedTask;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> loadTasks() async {
    try {
      _setLoading(true);
      _error = null;
      _tasks = await _apiService.getTasks();
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  Future<void> loadAssignedTasks() async {
    try {
      _setLoading(true);
      _error = null;
      _assignedTasks = await _apiService.getAssignedTasks();
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  Future<void> loadProjectTasks(int projectId) async {
    try {
      _setLoading(true);
      _error = null;
      _projectTasks = await _apiService.getProjectTasks(projectId);
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  Future<void> loadTask(int taskId) async {
    try {
      _setLoading(true);
      _error = null;
      _selectedTask = await _apiService.getTask(taskId);
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> createTask({
    required String title,
    String? description,
    required int projectId,
    TaskPriority? priority,
    DateTime? dueDate,
  }) async {
    try {
      _setLoading(true);
      _error = null;

      final newTask = await _apiService.createTask(
        title: title,
        description: description,
        projectId: projectId,
        priority: priority?.value,
        dueDate: dueDate,
      );

      _tasks.insert(0, newTask);
      _projectTasks.insert(0, newTask);
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> updateTask(
    int taskId, {
    String? title,
    String? description,
    TaskStatus? status,
    TaskPriority? priority,
    DateTime? dueDate,
  }) async {
    try {
      _setLoading(true);
      _error = null;

      final updatedTask = await _apiService.updateTask(
        taskId,
        title: title,
        description: description,
        status: status?.value,
        priority: priority?.value,
        dueDate: dueDate,
      );

      _updateTaskInLists(updatedTask);
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> assignTask(int taskId, int? assigneeId) async {
    try {
      _setLoading(true);
      _error = null;

      final updatedTask = await _apiService.assignTask(taskId, assigneeId);
      _updateTaskInLists(updatedTask);
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> deleteTask(int taskId) async {
    try {
      _setLoading(true);
      _error = null;

      await _apiService.deleteTask(taskId);
      _removeTaskFromLists(taskId);

      if (_selectedTask?.id == taskId) {
        _selectedTask = null;
      }

      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<List<Task>> getTasksByStatus(TaskStatus status) async {
    try {
      return await _apiService.getTasksByStatus(status.value);
    } catch (e) {
      _error = e.toString();
      return [];
    }
  }

  Future<List<Task>> getOverdueTasks() async {
    try {
      return await _apiService.getOverdueTasks();
    } catch (e) {
      _error = e.toString();
      return [];
    }
  }

  Future<List<Task>> getTasksDueSoon({int days = 7}) async {
    try {
      return await _apiService.getTasksDueSoon(days: days);
    } catch (e) {
      _error = e.toString();
      return [];
    }
  }

  void _updateTaskInLists(Task updatedTask) {
    // Update in main tasks list
    final taskIndex = _tasks.indexWhere((t) => t.id == updatedTask.id);
    if (taskIndex != -1) {
      _tasks[taskIndex] = updatedTask;
    }

    // Update in assigned tasks list
    final assignedIndex = _assignedTasks.indexWhere((t) => t.id == updatedTask.id);
    if (assignedIndex != -1) {
      _assignedTasks[assignedIndex] = updatedTask;
    }

    // Update in project tasks list
    final projectIndex = _projectTasks.indexWhere((t) => t.id == updatedTask.id);
    if (projectIndex != -1) {
      _projectTasks[projectIndex] = updatedTask;
    }

    // Update selected task
    if (_selectedTask?.id == updatedTask.id) {
      _selectedTask = updatedTask;
    }
  }

  void _removeTaskFromLists(int taskId) {
    _tasks.removeWhere((t) => t.id == taskId);
    _assignedTasks.removeWhere((t) => t.id == taskId);
    _projectTasks.removeWhere((t) => t.id == taskId);
  }

  void clearSelectedTask() {
    _selectedTask = null;
    notifyListeners();
  }

  void clearProjectTasks() {
    _projectTasks.clear();
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
  Task? getTaskById(int taskId) {
    try {
      return _tasks.firstWhere((t) => t.id == taskId);
    } catch (e) {
      return null;
    }
  }

  List<Task> getTasksByProject(int projectId) {
    return _tasks.where((t) => t.projectId == projectId).toList();
  }

  List<Task> getTasksByAssignee(int userId) {
    return _tasks.where((t) => t.assignee?.id == userId).toList();
  }

  List<Task> getTasksByCreator(int userId) {
    return _tasks.where((t) => t.creator.id == userId).toList();
  }

  Map<TaskStatus, List<Task>> getTasksGroupedByStatus() {
    final Map<TaskStatus, List<Task>> grouped = {};
    for (final status in TaskStatus.values) {
      grouped[status] = _projectTasks.where((t) => t.status == status).toList();
    }
    return grouped;
  }

  Map<TaskPriority, List<Task>> getTasksGroupedByPriority() {
    final Map<TaskPriority, List<Task>> grouped = {};
    for (final priority in TaskPriority.values) {
      grouped[priority] = _projectTasks.where((t) => t.priority == priority).toList();
    }
    return grouped;
  }
} 