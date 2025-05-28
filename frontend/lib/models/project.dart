import 'user.dart';

class Project {
  final int id;
  final String name;
  final String? description;
  final String? color;
  final User owner;
  final List<User> members;
  final int totalTasks;
  final int completedTasks;
  final DateTime createdAt;
  final DateTime updatedAt;

  Project({
    required this.id,
    required this.name,
    this.description,
    this.color,
    required this.owner,
    required this.members,
    required this.totalTasks,
    required this.completedTasks,
    required this.createdAt,
    required this.updatedAt,
  });

  factory Project.fromJson(Map<String, dynamic> json) {
    return Project(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      color: json['color'],
      owner: User.fromJson(json['owner']),
      members: (json['members'] as List)
          .map((member) => User.fromJson(member))
          .toList(),
      totalTasks: json['totalTasks'] ?? 0,
      completedTasks: json['completedTasks'] ?? 0,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'color': color,
      'owner': owner.toJson(),
      'members': members.map((member) => member.toJson()).toList(),
      'totalTasks': totalTasks,
      'completedTasks': completedTasks,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }

  Project copyWith({
    int? id,
    String? name,
    String? description,
    String? color,
    User? owner,
    List<User>? members,
    int? totalTasks,
    int? completedTasks,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return Project(
      id: id ?? this.id,
      name: name ?? this.name,
      description: description ?? this.description,
      color: color ?? this.color,
      owner: owner ?? this.owner,
      members: members ?? this.members,
      totalTasks: totalTasks ?? this.totalTasks,
      completedTasks: completedTasks ?? this.completedTasks,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  double get completionPercentage {
    if (totalTasks == 0) return 0.0;
    return completedTasks / totalTasks;
  }

  bool isOwner(User user) {
    return owner.id == user.id;
  }

  bool isMember(User user) {
    return members.any((member) => member.id == user.id) || isOwner(user);
  }

  @override
  String toString() {
    return 'Project(id: $id, name: $name, totalTasks: $totalTasks, completedTasks: $completedTasks)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is Project && other.id == id;
  }

  @override
  int get hashCode => id.hashCode;
} 