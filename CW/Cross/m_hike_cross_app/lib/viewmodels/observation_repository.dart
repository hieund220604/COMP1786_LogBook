import 'dart:io';
import 'package:sqflite/sqflite.dart';

import '../db/database.dart';
import '../models/observation.dart';
import '../models/observation_media.dart';

class ObservationRepository {
  final AppDatabase _dbProvider = AppDatabase.instance;

  // Observations
  Future<int> createObservation(Observation o) async {
    final db = await _dbProvider.database;
    final id = await db.insert('observations', o.toMap());
    return id;
  }

  Future<Observation?> getObservation(int id) async {
    final db = await _dbProvider.database;
    final maps = await db.query('observations', where: 'id = ?', whereArgs: [id]);
    if (maps.isEmpty) return null;
    return Observation.fromMap(maps.first);
  }

  Future<List<Observation>> listObservationsForHike(int hikeId) async {
    final db = await _dbProvider.database;
    final maps = await db.query('observations', where: 'hike_id = ?', whereArgs: [hikeId], orderBy: 'created_at DESC');
    return maps.map((m) => Observation.fromMap(m)).toList();
  }

  Future<int> updateObservation(Observation o) async {
    final db = await _dbProvider.database;
    final count = await db.update('observations', o.toMap(), where: 'id = ?', whereArgs: [o.id]);
    return count;
  }

  Future<int> deleteObservation(int id) async {
    final db = await _dbProvider.database;

    try {
      // Bước 1: Lấy tất cả media của observation
      final mediaList = await db.query(
        'observation_media',
        where: 'observation_id = ?',
        whereArgs: [id],
      );

      // Bước 2: Xóa các file media trên disk
      for (final media in mediaList) {
        final filePath = media['file_path'] as String?;
        if (filePath != null && filePath.isNotEmpty) {
          try {
            final file = File(filePath);
            if (await file.exists()) {
              await file.delete();
            }
          } catch (e) {
            print('Error deleting media file $filePath: $e');
          }
        }
      }

      // Bước 3: Xóa observation - CASCADE sẽ tự động xóa observation_media records
      final count = await db.delete('observations', where: 'id = ?', whereArgs: [id]);

      return count;
    } catch (e) {
      print('Error deleting observation $id: $e');
      rethrow;
    }
  }

  // Observation media
  Future<int> addMedia(ObservationMedia m) async {
    final db = await _dbProvider.database;
    final id = await db.insert('observation_media', m.toMap());
    return id;
  }

  Future<List<ObservationMedia>> listMediaForObservation(int observationId) async {
    final db = await _dbProvider.database;
    final maps = await db.query('observation_media', where: 'observation_id = ?', whereArgs: [observationId], orderBy: 'created_at ASC');
    return maps.map((m) => ObservationMedia.fromMap(m)).toList();
  }

  Future<int> deleteMedia(int id) async {
    final db = await _dbProvider.database;

    try {
      // Bước 1: Lấy thông tin media để biết file path
      final maps = await db.query(
        'observation_media',
        where: 'id = ?',
        whereArgs: [id],
      );

      if (maps.isNotEmpty) {
        final filePath = maps.first['file_path'] as String?;

        // Bước 2: Xóa file trên disk
        if (filePath != null && filePath.isNotEmpty) {
          try {
            final file = File(filePath);
            if (await file.exists()) {
              await file.delete();
            }
          } catch (e) {
            print('Error deleting media file $filePath: $e');
          }
        }
      }

      // Bước 3: Xóa record trong database
      final count = await db.delete('observation_media', where: 'id = ?', whereArgs: [id]);

      return count;
    } catch (e) {
      print('Error deleting media $id: $e');
      rethrow;
    }
  }

  Future<List<ObservationMedia>> listMediaForHike(int hikeId) async {
    final db = await _dbProvider.database;
    final maps = await db.rawQuery('''
      SELECT om.* FROM observation_media om
      JOIN observations o ON o.id = om.observation_id
      WHERE o.hike_id = ?
      ORDER BY o.created_at DESC, om.created_at ASC
    ''', [hikeId]);
    return maps.map((m) => ObservationMedia.fromMap(m)).toList();
  }

  // Thumbnail helper: returns first media file path for a hike or null
  Future<String?> thumbnailForHike(int hikeId) async {
    final db = await _dbProvider.database;
    final maps = await db.rawQuery('''
      SELECT om.file_path FROM observation_media om
      JOIN observations o ON o.id = om.observation_id
      WHERE o.hike_id = ?
      ORDER BY o.created_at DESC, om.created_at ASC
      LIMIT 1
    ''', [hikeId]);
    if (maps.isEmpty) return null;
    return maps.first['file_path'] as String?;
  }
}

