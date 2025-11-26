// ObservationMedia model

class ObservationMedia {
  final int? id;
  final int observationId;
  final String filePath;
  final String mediaType; // photo or video
  final int? durationSeconds; // for videos
  final int? width;
  final int? height;
  final int createdAt;

  ObservationMedia({
    this.id,
    required this.observationId,
    required this.filePath,
    required this.mediaType,
    this.durationSeconds,
    this.width,
    this.height,
    required this.createdAt,
  });

  ObservationMedia copyWith({
    int? id,
    int? observationId,
    String? filePath,
    String? mediaType,
    int? durationSeconds,
    int? width,
    int? height,
    int? createdAt,
  }) {
    return ObservationMedia(
      id: id ?? this.id,
      observationId: observationId ?? this.observationId,
      filePath: filePath ?? this.filePath,
      mediaType: mediaType ?? this.mediaType,
      durationSeconds: durationSeconds ?? this.durationSeconds,
      width: width ?? this.width,
      height: height ?? this.height,
      createdAt: createdAt ?? this.createdAt,
    );
  }

  factory ObservationMedia.fromMap(Map<String, dynamic> m) {
    // Helper function to safely convert to int
    int? _toInt(dynamic value) {
      if (value == null) return null;
      if (value is int) return value;
      if (value is String) return int.tryParse(value);
      if (value is num) return value.toInt();
      return null;
    }

    return ObservationMedia(
      id: _toInt(m['id']),
      observationId: _toInt(m['observation_id']) ?? 0,
      filePath: m['file_path'] as String,
      mediaType: m['media_type'] as String,
      durationSeconds: _toInt(m['duration_seconds']),
      width: _toInt(m['width']),
      height: _toInt(m['height']),
      createdAt: _toInt(m['created_at']) ?? DateTime.now().toUtc().millisecondsSinceEpoch ~/ 1000,
    );
  }

  Map<String, dynamic> toMap() => {
        if (id != null) 'id': id,
        'observation_id': observationId,
        'file_path': filePath,
        'media_type': mediaType,
        'duration_seconds': durationSeconds,
        'width': width,
        'height': height,
        'created_at': createdAt,
      };
}

