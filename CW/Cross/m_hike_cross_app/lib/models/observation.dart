// Observation model

class Observation {
  final int? id;
  final int hikeId;
  final String? caption;
  final double? latitude;
  final double? longitude;
  final int createdAt;
  final int? updatedAt;

  Observation({
    this.id,
    required this.hikeId,
    this.caption,
    this.latitude,
    this.longitude,
    required this.createdAt,
    this.updatedAt,
  });

  Observation copyWith({
    int? id,
    int? hikeId,
    String? caption,
    double? latitude,
    double? longitude,
    int? createdAt,
    int? updatedAt,
  }) {
    return Observation(
      id: id ?? this.id,
      hikeId: hikeId ?? this.hikeId,
      caption: caption ?? this.caption,
      latitude: latitude ?? this.latitude,
      longitude: longitude ?? this.longitude,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  factory Observation.fromMap(Map<String, dynamic> m) {
    // Helper function to safely convert to int
    int? _toInt(dynamic value) {
      if (value == null) return null;
      if (value is int) return value;
      if (value is String) return int.tryParse(value);
      if (value is num) return value.toInt();
      return null;
    }

    // Helper function to safely convert to double
    double? _toDouble(dynamic value) {
      if (value == null) return null;
      if (value is double) return value;
      if (value is int) return value.toDouble();
      if (value is String) return double.tryParse(value);
      if (value is num) return value.toDouble();
      return null;
    }

    return Observation(
      id: _toInt(m['id']),
      hikeId: _toInt(m['hike_id']) ?? 0,
      caption: m['caption'] as String?,
      latitude: _toDouble(m['latitude']),
      longitude: _toDouble(m['longitude']),
      createdAt: _toInt(m['created_at']) ?? DateTime.now().toUtc().millisecondsSinceEpoch ~/ 1000,
      updatedAt: _toInt(m['updated_at']),
    );
  }

  Map<String, dynamic> toMap() => {
        if (id != null) 'id': id,
        'hike_id': hikeId,
        'caption': caption,
        'latitude': latitude,
        'longitude': longitude,
        'created_at': createdAt,
        'updated_at': updatedAt,
      };
}

