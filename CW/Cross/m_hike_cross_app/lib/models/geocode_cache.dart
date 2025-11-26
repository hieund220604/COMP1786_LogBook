// GeocodeCache model

class GeocodeCache {
  final String key; // "lat,lon"
  final String value; // json string
  final int expiresAt;

  GeocodeCache({
    required this.key,
    required this.value,
    required this.expiresAt,
  });

  factory GeocodeCache.fromMap(Map<String, dynamic> m) {
    // Helper function to safely convert to int
    int _toInt(dynamic value) {
      if (value is int) return value;
      if (value is String) return int.tryParse(value) ?? 0;
      if (value is num) return value.toInt();
      return 0;
    }

    return GeocodeCache(
      key: m['key'] as String,
      value: m['value'] as String,
      expiresAt: _toInt(m['expires_at']),
    );
  }

  Map<String, dynamic> toMap() => {
        'key': key,
        'value': value,
        'expires_at': expiresAt,
      };
}

