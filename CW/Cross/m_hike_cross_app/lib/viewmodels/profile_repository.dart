import 'package:sqflite/sqflite.dart';

import '../db/database.dart';
import '../models/profile.dart';

class ProfileRepository {
  final AppDatabase _dbProvider = AppDatabase.instance;

  // Get profile (there should only be one)
  Future<Profile?> getProfile() async {
    try {
      print('ProfileRepository: Getting database instance...');
      final db = await _dbProvider.database;
      print('ProfileRepository: Database instance obtained');

      print('ProfileRepository: Querying profile table...');
      final maps = await db.query('profile', limit: 1);
      print('ProfileRepository: Query result: $maps');
      print('ProfileRepository: Number of records: ${maps.length}');

      if (maps.isEmpty) {
        print('ProfileRepository: No profile found in database');
        return null;
      }

      print('ProfileRepository: Creating Profile object from map...');
      final profile = Profile.fromMap(maps.first);
      print('ProfileRepository: Profile created successfully');
      return profile;
    } catch (e, stackTrace) {
      print('ProfileRepository ERROR: $e');
      print('ProfileRepository STACK: $stackTrace');
      rethrow;
    }
  }

  // Create or update profile
  Future<int> upsertProfile(Profile profile) async {
    try {
      print('ProfileRepository: Upserting profile...');
      print('ProfileRepository: Profile data: ${profile.toMap()}');

      final db = await _dbProvider.database;
      final existing = await getProfile();

      print('ProfileRepository: Existing profile: ${existing?.toMap()}');

      if (existing == null) {
        print('ProfileRepository: No existing profile, inserting new one...');
        final result = await db.insert('profile', profile.toMap());
        print('ProfileRepository: Insert result (row id): $result');
        return result;
      } else {
        print('ProfileRepository: Updating existing profile (id: ${existing.id})...');
        final result = await db.update(
          'profile',
          profile.toMap(),
          where: 'id = ?',
          whereArgs: [existing.id],
        );
        print('ProfileRepository: Update result (rows affected): $result');
        return result;
      }
    } catch (e, stackTrace) {
      print('ProfileRepository upsertProfile ERROR: $e');
      print('ProfileRepository upsertProfile STACK: $stackTrace');
      rethrow;
    }
  }

  // Update specific profile fields
  Future<int> updateProfile(Map<String, dynamic> fields) async {
    final db = await _dbProvider.database;
    final existing = await getProfile();
    if (existing == null) {
      // Create a default profile first
      final defaultProfile = Profile(displayName: 'Hiker');
      await db.insert('profile', defaultProfile.toMap());
      return await db.update(
        'profile',
        fields,
        where: 'id = ?',
        whereArgs: [1],
      );
    }
    return await db.update(
      'profile',
      fields,
      where: 'id = ?',
      whereArgs: [existing.id],
    );
  }

  // Verify database and tables
  Future<void> verifyDatabase() async {
    try {
      print('ProfileRepository: Verifying database...');
      final db = await _dbProvider.database;

      // Check if profile table exists
      final tables = await db.rawQuery(
        "SELECT name FROM sqlite_master WHERE type='table' AND name='profile'"
      );
      print('ProfileRepository: Profile table exists: ${tables.isNotEmpty}');

      if (tables.isEmpty) {
        print('ProfileRepository ERROR: Profile table does not exist!');
        return;
      }

      // Get table schema
      final schema = await db.rawQuery("PRAGMA table_info(profile)");
      print('ProfileRepository: Profile table schema:');
      for (var column in schema) {
        print('  - ${column['name']}: ${column['type']}');
      }

      // Count rows
      final count = await db.rawQuery('SELECT COUNT(*) as count FROM profile');
      print('ProfileRepository: Profile rows: ${count.first['count']}');

      // Show all data
      final allProfiles = await db.query('profile');
      print('ProfileRepository: All profile data: $allProfiles');

    } catch (e, stackTrace) {
      print('ProfileRepository verifyDatabase ERROR: $e');
      print('ProfileRepository verifyDatabase STACK: $stackTrace');
    }
  }
}