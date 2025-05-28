import 'package:flutter/material.dart';

class TaskListScreen extends StatelessWidget {
  const TaskListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Tasks')),
      body: const Center(child: Text('Task List - Coming Soon')),
    );
  }
}

class TaskDetailScreen extends StatelessWidget {
  final int taskId;
  const TaskDetailScreen({super.key, required this.taskId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Task $taskId')),
      body: Center(child: Text('Task Detail $taskId - Coming Soon')),
    );
  }
}

class CreateTaskScreen extends StatelessWidget {
  final int? projectId;
  const CreateTaskScreen({super.key, this.projectId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Create Task')),
      body: Center(child: Text('Create Task for Project $projectId - Coming Soon')),
    );
  }
} 