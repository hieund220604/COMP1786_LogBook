import 'package:sqflite/sqflite.dart';

import '../db/database.dart';
import '../models/settings.dart';

class SettingsRepository {
  final AppDatabase _dbProvider = AppDatabase.instance;

  // Get settings (there should only be one)
  Future<Settings?> getSettings() async {
    final db = await _dbProvider.database;
    final maps = await db.query('settings', limit: 1);
    if (maps.isEmpty) return null;
    return Settings.fromMap(maps.first);
  }

  // Create or update settings
  Future<int> upsertSettings(Settings settings) async {
    final db = await _dbProvider.database;
    final existing = await getSettings();
    if (existing == null) {
      return await db.insert('settings', settings.toMap());
    } else {
      return await db.update(
        'settings',
        settings.toMap(),
        where: 'id = ?',
        whereArgs: [existing.id],
      );
    }
  }

  // Update specific settings fields
  Future<int> updateSettings(Map<String, dynamic> fields) async {
    final db = await _dbProvider.database;
    final existing = await getSettings();
    if (existing == null) {
      // Create default settings first
      final defaultSettings = Settings();
      await db.insert('settings', defaultSettings.toMap());
      return await db.update(
        'settings',
        fields,
        where: 'id = ?',
        whereArgs: [1],
      );
    }
    return await db.update(
      'settings',
      fields,
      where: 'id = ?',
      whereArgs: [existing.id],
    );
  }

  // Initialize default settings if not exists
  Future<void> initializeDefaultSettings() async {
    final existing = await getSettings();
    if (existing == null) {
      final defaultSettings = Settings(
        id: 1,
        notificationMorningHour: 7,
        notificationEveningHour: 20,
        weatherEnabled: 1,
        elevationEnabled: 1,
      );
      await upsertSettings(defaultSettings);
    }
  }
}

