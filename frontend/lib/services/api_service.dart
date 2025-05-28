import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as firebase_auth;

import '../models/user.dart';
import '../models/project.dart';
import '../models/task.dart';

class ApiService {
  static const String baseUrl = 'http://localhost:8080/api';
  late final Dio _dio;

  ApiService() {
    _dio = Dio(BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),
      headers: {
        'Content-Type': 'application/json',
      },
    ));

    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        // Add Firebase auth token to requests
        final user = firebase_auth.FirebaseAuth.instance.currentUser;
        if (user != null) {
          final token = await user.getIdToken();
          options.headers['Authorization'] = 'Bearer $token';
        }
        handler.next(options);
      },
      onError: (error, handler) {
        print('API Error: ${error.message}');
        handler.next(error);
      },
    ));
  }

  // Auth endpoints
  Future<User> registerUser() async {
    try {
      final response = await _dio.post('/auth/register');
      return User.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<User> getCurrentUser() async {
    try {
      final response = await _dio.get('/auth/me');
      return User.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<User> updateProfile({String? name, String? profilePictureUrl}) async {
    try {
      final data = <String, dynamic>{};
      if (name != null) data['name'] = name;
      if (profilePictureUrl != null) data['profilePictureUrl'] = profilePictureUrl;

      final response = await _dio.put('/auth/profile', data: data);
      return User.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  // Project endpoints
  Future<List<Project>> getProjects() async {
    try {
      final response = await _dio.get('/projects');
      return (response.data as List)
          .map((json) => Project.fromJson(json))
          .toList();
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Project> getProject(int projectId) async {
    try {
      final response = await _dio.get('/projects/$projectId');
      return Project.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Project> createProject({
    required String name,
    String? description,
    String? color,
  }) async {
    try {
      final data = {
        'name': name,
        if (description != null) 'description': description,
        if (color != null) 'color': color,
      };

      final response = await _dio.post('/projects', data: data);
      return Project.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Project> updateProject(
    int projectId, {
    String? name,
    String? description,
    String? color,
  }) async {
    try {
      final data = <String, dynamic>{};
      if (name != null) data['name'] = name;
      if (description != null) data['description'] = description;
      if (color != null) data['color'] = color;

      final response = await _dio.put('/projects/$projectId', data: data);
      return Project.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<void> deleteProject(int projectId) async {
    try {
      await _dio.delete('/projects/$projectId');
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Project> addMemberToProject(int projectId, String email) async {
    try {
      final response = await _dio.post(
        '/projects/$projectId/members',
        data: {'email': email},
      );
      return Project.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Project> removeMemberFromProject(int projectId, int memberId) async {
    try {
      final response = await _dio.delete('/projects/$projectId/members/$memberId');
      return Project.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<List<Project>> searchProjects(String query) async {
    try {
      final response = await _dio.get('/projects/search', queryParameters: {'query': query});
      return (response.data as List)
          .map((json) => Project.fromJson(json))
          .toList();
    } catch (e) {
      throw _handleError(e);
    }
  }

  // Task endpoints
  Future<List<Task>> getTasks() async {
    try {
      final response = await _dio.get('/tasks/my-tasks');
      return (response.data as List)
          .map((json) => Task.fromJson(json))
          .toList();
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<List<Task>> getAssignedTasks() async {
    try {
      final response = await _dio.get('/tasks/assigned');
      return (response.data as List)
          .map((json) => Task.fromJson(json))
          .toList();
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<List<Task>> getProjectTasks(int projectId) async {
    try {
      final response = await _dio.get('/tasks/project/$projectId');
      return (response.data as List)
          .map((json) => Task.fromJson(json))
          .toList();
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Task> getTask(int taskId) async {
    try {
      final response = await _dio.get('/tasks/$taskId');
      return Task.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Task> createTask({
    required String title,
    String? description,
    required int projectId,
    String? priority,
    DateTime? dueDate,
  }) async {
    try {
      final data = {
        'title': title,
        'projectId': projectId,
        if (description != null) 'description': description,
        if (priority != null) 'priority': priority,
        if (dueDate != null) 'dueDate': dueDate.toIso8601String(),
      };

      final response = await _dio.post('/tasks', data: data);
      return Task.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Task> updateTask(
    int taskId, {
    String? title,
    String? description,
    String? status,
    String? priority,
    DateTime? dueDate,
  }) async {
    try {
      final data = <String, dynamic>{};
      if (title != null) data['title'] = title;
      if (description != null) data['description'] = description;
      if (status != null) data['status'] = status;
      if (priority != null) data['priority'] = priority;
      if (dueDate != null) data['dueDate'] = dueDate.toIso8601String();

      final response = await _dio.put('/tasks/$taskId', data: data);
      return Task.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Task> assignTask(int taskId, int? assigneeId) async {
    try {
      final data = {'assigneeId': assigneeId};
      final response = await _dio.put('/tasks/$taskId/assign', data: data);
      return Task.fromJson(response.data);
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<void> deleteTask(int taskId) async {
    try {
      await _dio.delete('/tasks/$taskId');
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<List<Task>> getTasksByStatus(String status) async {
    try {
      final response = await _dio.get('/tasks/status/$status');
      return (response.data as List)
          .map((json) => Task.fromJson(json))
          .toList();
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<List<Task>> getOverdueTasks() async {
    try {
      final response = await _dio.get('/tasks/overdue');
      return (response.data as List)
          .map((json) => Task.fromJson(json))
          .toList();
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<List<Task>> getTasksDueSoon({int days = 7}) async {
    try {
      final response = await _dio.get('/tasks/due-soon', queryParameters: {'days': days});
      return (response.data as List)
          .map((json) => Task.fromJson(json))
          .toList();
    } catch (e) {
      throw _handleError(e);
    }
  }

  String _handleError(dynamic error) {
    if (error is DioException) {
      switch (error.type) {
        case DioExceptionType.connectionTimeout:
        case DioExceptionType.receiveTimeout:
          return 'Connection timeout. Please check your internet connection.';
        case DioExceptionType.badResponse:
          final statusCode = error.response?.statusCode;
          if (statusCode == 401) {
            return 'Authentication failed. Please log in again.';
          } else if (statusCode == 403) {
            return 'You don\'t have permission to perform this action.';
          } else if (statusCode == 404) {
            return 'Resource not found.';
          } else if (statusCode == 500) {
            return 'Server error. Please try again later.';
          }
          return 'Request failed with status code: $statusCode';
        case DioExceptionType.cancel:
          return 'Request was cancelled.';
        default:
          return 'Network error. Please check your connection.';
      }
    }
    return 'An unexpected error occurred.';
  }
} 