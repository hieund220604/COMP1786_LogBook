// Profile model

class Profile {
  final int? id;
  final String displayName;
  final String? bio;
  final String? avatarPath;
  final String language; // en, vi, etc.
  final String theme; // light, dark, black_white, system
  final int musicEnabled; // 0 or 1
  final int notificationsEnabled; // 0 or 1
  final int? dailyReminderHour; // 0-23

  Profile({
    this.id,
    required this.displayName,
    this.bio,
    this.avatarPath,
    this.language = 'en',
    this.theme = 'system',
    this.musicEnabled = 0,
    this.notificationsEnabled = 1,
    this.dailyReminderHour,
  });

  Profile copyWith({
    int? id,
    String? displayName,
    String? bio,
    String? avatarPath,
    String? language,
    String? theme,
    int? musicEnabled,
    int? notificationsEnabled,
    int? dailyReminderHour,
  }) {
    return Profile(
      id: id ?? this.id,
      displayName: displayName ?? this.displayName,
      bio: bio ?? this.bio,
      avatarPath: avatarPath ?? this.avatarPath,
      language: language ?? this.language,
      theme: theme ?? this.theme,
      musicEnabled: musicEnabled ?? this.musicEnabled,
      notificationsEnabled: notificationsEnabled ?? this.notificationsEnabled,
      dailyReminderHour: dailyReminderHour ?? this.dailyReminderHour,
    );
  }

  factory Profile.fromMap(Map<String, dynamic> m) {
    // Helper function to safely convert to int
    int? _toInt(dynamic value) {
      if (value == null) return null;
      if (value is int) return value;
      if (value is String) return int.tryParse(value);
      if (value is num) return value.toInt();
      return null;
    }

    return Profile(
      id: _toInt(m['id']),
      displayName: m['display_name'] as String,
      bio: m['bio'] as String?,
      avatarPath: m['avatar_path'] as String?,
      language: (m['language'] as String?) ?? 'en',
      theme: (m['theme'] as String?) ?? 'system',
      musicEnabled: _toInt(m['music_enabled']) ?? 0,
      notificationsEnabled: _toInt(m['notifications_enabled']) ?? 1,
      dailyReminderHour: _toInt(m['daily_reminder_hour']),
    );
  }

  Map<String, dynamic> toMap() => {
        if (id != null) 'id': id,
        'display_name': displayName,
        'bio': bio,
        'avatar_path': avatarPath,
        'language': language,
        'theme': theme,
        'music_enabled': musicEnabled,
        'notifications_enabled': notificationsEnabled,
        'daily_reminder_hour': dailyReminderHour,
      };
}

