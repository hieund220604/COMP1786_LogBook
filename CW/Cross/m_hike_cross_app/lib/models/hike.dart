// Hike model

class Hike {
  final int? id;
  final String name;
  final String? description;
  final String? difficulty; // Easy, Moderate, Hard, Expert
  final int plannedDate; // unix epoch seconds (device timezone)
  final int dateUtc; // unix epoch seconds (UTC for sorting/sync)
  final int isCompleted; // 0 or 1
  final int isFavorite; // 0 or 1
  final double? distanceKm;
  final String parkingStatus; // Yes, No, Unknown
  final String? locationName;
  final String? placeId; // Google Places ID
  final double? latitude;
  final double? longitude;
  final double? elevationGainM;
  final int createdAt;
  final int? updatedAt;

  Hike({
    this.id,
    required this.name,
    this.description,
    this.difficulty,
    required this.plannedDate,
    required this.dateUtc,
    this.isCompleted = 0,
    this.isFavorite = 0,
    this.distanceKm,
    this.parkingStatus = 'Unknown',
    this.locationName,
    this.placeId,
    this.latitude,
    this.longitude,
    this.elevationGainM,
    required this.createdAt,
    this.updatedAt,
  });

  Hike copyWith({
    int? id,
    String? name,
    String? description,
    String? difficulty,
    int? plannedDate,
    int? dateUtc,
    int? isCompleted,
    int? isFavorite,
    double? distanceKm,
    String? parkingStatus,
    String? locationName,
    String? placeId,
    double? latitude,
    double? longitude,
    double? elevationGainM,
    int? createdAt,
    int? updatedAt,
  }) {
    return Hike(
      id: id ?? this.id,
      name: name ?? this.name,
      description: description ?? this.description,
      difficulty: difficulty ?? this.difficulty,
      plannedDate: plannedDate ?? this.plannedDate,
      dateUtc: dateUtc ?? this.dateUtc,
      isCompleted: isCompleted ?? this.isCompleted,
      isFavorite: isFavorite ?? this.isFavorite,
      distanceKm: distanceKm ?? this.distanceKm,
      parkingStatus: parkingStatus ?? this.parkingStatus,
      locationName: locationName ?? this.locationName,
      placeId: placeId ?? this.placeId,
      latitude: latitude ?? this.latitude,
      longitude: longitude ?? this.longitude,
      elevationGainM: elevationGainM ?? this.elevationGainM,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  factory Hike.fromMap(Map<String, dynamic> m) {
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

    return Hike(
      id: _toInt(m['id']),
      name: m['name'] as String,
      description: m['description'] as String?,
      difficulty: m['difficulty'] as String?,
      plannedDate: _toInt(m['planned_date']) ?? _toInt(m['date_utc']) ?? _toInt(m['created_at']) ?? 0,
      dateUtc: _toInt(m['date_utc']) ?? _toInt(m['created_at']) ?? 0,
      isCompleted: _toInt(m['is_completed']) ?? 0,
      isFavorite: _toInt(m['is_favorite']) ?? 0,
      distanceKm: _toDouble(m['distance_km']),
      parkingStatus: (m['parking_status'] as String?) ?? 'Unknown',
      locationName: m['location_name'] as String?,
      placeId: m['place_id'] as String?,
      latitude: _toDouble(m['latitude']),
      longitude: _toDouble(m['longitude']),
      elevationGainM: _toDouble(m['elevation_gain_m']),
      createdAt: _toInt(m['created_at']) ?? DateTime.now().toUtc().millisecondsSinceEpoch ~/ 1000,
      updatedAt: _toInt(m['updated_at']),
    );
  }

  Map<String, dynamic> toMap() => {
        if (id != null) 'id': id,
        'name': name,
        'description': description,
        'difficulty': difficulty,
        'planned_date': plannedDate,
        'date_utc': dateUtc,
        'is_completed': isCompleted,
        'is_favorite': isFavorite,
        'distance_km': distanceKm,
        'parking_status': parkingStatus,
        'location_name': locationName,
        'place_id': placeId,
        'latitude': latitude,
        'longitude': longitude,
        'elevation_gain_m': elevationGainM,
        'created_at': createdAt,
        'updated_at': updatedAt,
      };
}
