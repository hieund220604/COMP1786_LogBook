// Settings model

class Settings {
  final int? id;
  final int notificationMorningHour; // 0-23
  final int notificationEveningHour; // 0-23
  final int weatherEnabled; // 0 or 1
  final int elevationEnabled; // 0 or 1

  Settings({
    this.id,
    this.notificationMorningHour = 7,
    this.notificationEveningHour = 20,
    this.weatherEnabled = 1,
    this.elevationEnabled = 1,
  });

  Settings copyWith({
    int? id,
    int? notificationMorningHour,
    int? notificationEveningHour,
    int? weatherEnabled,
    int? elevationEnabled,
  }) {
    return Settings(
      id: id ?? this.id,
      notificationMorningHour: notificationMorningHour ?? this.notificationMorningHour,
      notificationEveningHour: notificationEveningHour ?? this.notificationEveningHour,
      weatherEnabled: weatherEnabled ?? this.weatherEnabled,
      elevationEnabled: elevationEnabled ?? this.elevationEnabled,
    );
  }

  factory Settings.fromMap(Map<String, dynamic> m) {
    // Helper function to safely convert to int
    int? _toInt(dynamic value) {
      if (value == null) return null;
      if (value is int) return value;
      if (value is String) return int.tryParse(value);
      if (value is num) return value.toInt();
      return null;
    }

    return Settings(
      id: _toInt(m['id']),
      notificationMorningHour: _toInt(m['notification_morning_hour']) ?? 7,
      notificationEveningHour: _toInt(m['notification_evening_hour']) ?? 20,
      weatherEnabled: _toInt(m['weather_enabled']) ?? 1,
      elevationEnabled: _toInt(m['elevation_enabled']) ?? 1,
    );
  }

  Map<String, dynamic> toMap() => {
        if (id != null) 'id': id,
        'notification_morning_hour': notificationMorningHour,
        'notification_evening_hour': notificationEveningHour,
        'weather_enabled': weatherEnabled,
        'elevation_enabled': elevationEnabled,
      };
}

