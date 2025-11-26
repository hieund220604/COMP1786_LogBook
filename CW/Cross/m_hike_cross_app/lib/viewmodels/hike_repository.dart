import 'dart:io';
import 'package:sqflite/sqflite.dart';

import '../db/database.dart';
import '../models/geocode_cache.dart';
import '../models/hike.dart';

class HikeRepository {
  final AppDatabase _dbProvider = AppDatabase.instance;

  // Hikes
  Future<int> createHike(Hike hike) async {
    final db = await _dbProvider.database;
    final id = await db.insert('hikes', hike.toMap());
    return id;
  }

  Future<Hike?> getHike(int id) async {
    final db = await _dbProvider.database;
    final maps = await db.query('hikes', where: 'id = ?', whereArgs: [id]);
    if (maps.isEmpty) return null;
    return Hike.fromMap(maps.first);
  }

  Future<List<Hike>> listHikes({int? isCompleted, int? isFavorite, int limit = 50, int offset = 0}) async {
    try {
      print('HikeRepository: Getting database instance...');
      final db = await _dbProvider.database;
      print('HikeRepository: Database obtained');

      final whereClauses = <String>[];
      final whereArgs = <dynamic>[];
      if (isCompleted != null) {
        whereClauses.add('is_completed = ?');
        whereArgs.add(isCompleted);
      }
      if (isFavorite != null) {
        whereClauses.add('is_favorite = ?');
        whereArgs.add(isFavorite);
      }
      final where = whereClauses.isEmpty ? null : whereClauses.join(' AND ');

      print('HikeRepository: Query - WHERE: $where, ARGS: $whereArgs');

      final maps = await db.query('hikes', where: where, whereArgs: whereArgs, limit: limit, offset: offset, orderBy: 'date_utc DESC');

      print('HikeRepository: Query returned ${maps.length} rows');

      final hikes = maps.map((m) => Hike.fromMap(m)).toList();

      print('HikeRepository: Converted to ${hikes.length} Hike objects');

      return hikes;
    } catch (e, stackTrace) {
      print('HikeRepository ERROR in listHikes: $e');
      print('HikeRepository STACK: $stackTrace');
      rethrow;
    }
  }

  Future<int> updateHike(Hike hike) async {
    final db = await _dbProvider.database;
    final count = await db.update('hikes', hike.toMap(), where: 'id = ?', whereArgs: [hike.id]);
    return count;
  }

  Future<int> deleteHike(int id) async {
    final db = await _dbProvider.database;

    try {
      // Bước 1: Lấy tất cả observations của hike
      final observations = await db.query(
        'observations',
        where: 'hike_id = ?',
        whereArgs: [id],
      );

      // Bước 2: Lấy và xóa tất cả media files của các observations
      for (final obs in observations) {
        final obsId = obs['id'] as int;

        // Lấy tất cả media của observation này
        final mediaList = await db.query(
          'observation_media',
          where: 'observation_id = ?',
          whereArgs: [obsId],
        );

        // Xóa các file media trên disk
        for (final media in mediaList) {
          final filePath = media['file_path'] as String?;
          if (filePath != null && filePath.isNotEmpty) {
            try {
              final file = File(filePath);
              if (await file.exists()) {
                await file.delete();
              }
            } catch (e) {
              // Log error nhưng vẫn tiếp tục xóa
              print('Error deleting media file $filePath: $e');
            }
          }
        }
      }

      // Bước 3: Xóa hike - CASCADE sẽ tự động xóa observations và observation_media records
      final count = await db.delete('hikes', where: 'id = ?', whereArgs: [id]);

      return count;
    } catch (e) {
      print('Error deleting hike $id: $e');
      rethrow;
    }
  }


  Future<int> markHikeCompleted(int hikeId, {int completed = 1}) async {
    final db = await _dbProvider.database;
    final now = DateTime.now().toUtc().millisecondsSinceEpoch ~/ 1000;
    return await db.update('hikes', {'is_completed': completed, 'updated_at': now}, where: 'id = ?', whereArgs: [hikeId]);
  }

  Future<int> toggleFavorite(int hikeId, bool value) async {
    final db = await _dbProvider.database;
    final now = DateTime.now().toUtc().millisecondsSinceEpoch ~/ 1000;
    return await db.update('hikes', {'is_favorite': value ? 1 : 0, 'updated_at': now}, where: 'id = ?', whereArgs: [hikeId]);
  }

  // Geocode cache
  Future<int> upsertGeocodeCache(GeocodeCache c) async {
    final db = await _dbProvider.database;
    return await db.insert('geocode_cache', c.toMap(), conflictAlgorithm: ConflictAlgorithm.replace);
  }

  Future<GeocodeCache?> getGeocodeCache(String key) async {
    final db = await _dbProvider.database;
    final maps = await db.query('geocode_cache', where: 'key = ?', whereArgs: [key]);
    if (maps.isEmpty) return null;
    final item = GeocodeCache.fromMap(maps.first);
    if (item.expiresAt < DateTime.now().toUtc().millisecondsSinceEpoch ~/ 1000) {
      // expired
      await db.delete('geocode_cache', where: 'key = ?', whereArgs: [key]);
      return null;
    }
    return item;
  }

  Future<int> deleteGeocodeCache(String key) async {
    final db = await _dbProvider.database;
    return await db.delete('geocode_cache', where: 'key = ?', whereArgs: [key]);
  }

  // Search
  Future<List<Hike>> searchHikes(String q, {int limit = 50, int offset = 0}) async {
    final db = await _dbProvider.database;
    final like = '%${q}%';
    final maps = await db.query('hikes', where: 'name LIKE ? OR location_name LIKE ? OR description LIKE ?', whereArgs: [like, like, like], limit: limit, offset: offset);
    return maps.map((m) => Hike.fromMap(m)).toList();
  }
}
