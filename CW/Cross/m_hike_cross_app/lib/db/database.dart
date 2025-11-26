import 'dart:async';
import 'dart:io';

import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';
import 'package:sqflite/sqflite.dart';

class AppDatabase {
  static const _dbName = 'm_hike.db';
  static const _dbVersion = 2; // Updated version for schema changes

  AppDatabase._privateConstructor();

  static final AppDatabase instance = AppDatabase._privateConstructor();

  Database? _db;

  Future<void> closeDatabase() async {
    if (_db != null) {
      await _db!.close();
      _db = null;
    }
  }

  Future<Database> get database async {
    if (_db != null) {
      return _db!;
    }
    _db = await _initDatabase();
    return _db!;
  }

  Future<Database> _initDatabase() async {
    Directory documentsDirectory = await getApplicationDocumentsDirectory();
    String path = join(documentsDirectory.path, _dbName);

    print('=== DATABASE INIT ===');
    print('Target DB path: $path');

    // Check if database exists at old location (app_flutter directory)
    final oldPath = join(documentsDirectory.parent.path, 'app_flutter', _dbName);
    print('Old DB path: $oldPath');

    final oldDbFile = File(oldPath);
    final newDbFile = File(path);

    final oldExists = await oldDbFile.exists();
    final newExists = await newDbFile.exists();

    print('Old DB exists: $oldExists');
    print('New DB exists: $newExists');

    // If old database exists and new one doesn't, copy it
    if (oldExists && !newExists) {
      print('=== MIGRATING DATABASE ===');
      try {
        // Ensure the new directory exists
        await newDbFile.parent.create(recursive: true);
        // Copy the database file
        await oldDbFile.copy(path);
        print('Database file copied successfully');

        // Also copy journal files if they exist
        final oldJournalFile = File('$oldPath-journal');
        if (await oldJournalFile.exists()) {
          await oldJournalFile.copy('$path-journal');
          print('Journal file copied successfully');
        }
        print('=== MIGRATION COMPLETE ===');
      } catch (e) {
        print('=== MIGRATION ERROR: $e ===');
      }
    } else if (newExists) {
      print('Using existing database at new location');
    } else if (!oldExists && !newExists) {
      print('No existing database found - will create new one');
    }

    return await openDatabase(
      path,
      version: _dbVersion,
      onCreate: _onCreate,
      onUpgrade: _onUpgrade,
      onOpen: _onOpen,
    );
  }

  Future _onCreate(Database db, int version) async {
    // Hikes table
    await db.execute('''
      CREATE TABLE hikes(
        id INTEGER PRIMARY KEY,
        name TEXT NOT NULL,
        description TEXT,
        difficulty TEXT,
        planned_date INTEGER NOT NULL,
        date_utc INTEGER NOT NULL,
        is_completed INTEGER NOT NULL DEFAULT 0,
        is_favorite INTEGER NOT NULL DEFAULT 0,
        distance_km REAL,
        parking_status TEXT NOT NULL DEFAULT 'Unknown',
        location_name TEXT,
        place_id TEXT,
        latitude REAL,
        longitude REAL,
        elevation_gain_m REAL,
        created_at INTEGER NOT NULL,
        updated_at INTEGER
      )
    ''');

    // Observations table
    await db.execute('''
      CREATE TABLE observations(
        id INTEGER PRIMARY KEY,
        hike_id INTEGER NOT NULL,
        caption TEXT,
        latitude REAL,
        longitude REAL,
        created_at INTEGER NOT NULL,
        updated_at INTEGER,
        FOREIGN KEY(hike_id) REFERENCES hikes(id) ON DELETE CASCADE
      )
    ''');

    // Observation media table
    await db.execute('''
      CREATE TABLE observation_media(
        id INTEGER PRIMARY KEY,
        observation_id INTEGER NOT NULL,
        file_path TEXT NOT NULL,
        media_type TEXT NOT NULL,
        duration_seconds INTEGER,
        width INTEGER,
        height INTEGER,
        created_at INTEGER NOT NULL,
        FOREIGN KEY(observation_id) REFERENCES observations(id) ON DELETE CASCADE
      )
    ''');

    // Geocode cache table
    await db.execute('''
      CREATE TABLE geocode_cache(
        key TEXT PRIMARY KEY,
        value TEXT NOT NULL,
        expires_at INTEGER NOT NULL
      )
    ''');

    // Profile table (single record)
    await db.execute('''
      CREATE TABLE profile(
        id INTEGER PRIMARY KEY,
        display_name TEXT NOT NULL,
        bio TEXT,
        avatar_path TEXT,
        language TEXT NOT NULL DEFAULT 'en',
        theme TEXT NOT NULL DEFAULT 'system',
        music_enabled INTEGER NOT NULL DEFAULT 0,
        notifications_enabled INTEGER NOT NULL DEFAULT 1,
        daily_reminder_hour INTEGER
      )
    ''');

    // Settings table (single record)
    await db.execute('''
      CREATE TABLE settings(
        id INTEGER PRIMARY KEY,
        notification_morning_hour INTEGER NOT NULL DEFAULT 7,
        notification_evening_hour INTEGER NOT NULL DEFAULT 20,
        weather_enabled INTEGER NOT NULL DEFAULT 1,
        elevation_enabled INTEGER NOT NULL DEFAULT 1
      )
    ''');

    // Create indexes for better query performance
    await db.execute('CREATE INDEX idx_hikes_completed ON hikes(is_completed)');
    await db.execute('CREATE INDEX idx_hikes_favorite ON hikes(is_favorite)');
    await db.execute(
        'CREATE INDEX idx_hikes_planned_date ON hikes(planned_date)');
    await db.execute(
        'CREATE INDEX idx_observations_hike_id ON observations(hike_id)');
    await db.execute(
        'CREATE INDEX idx_observation_media_observation_id ON observation_media(observation_id)');

    // Ensure foreign keys enforced for this DB connection
    await db.execute('PRAGMA foreign_keys = ON');
  }

  Future _onUpgrade(Database db, int oldVersion, int newVersion) async {
    if (oldVersion < 2) {
      // Migrate from version 1 to version 2
      // Add new columns to hikes table
      try {
        await db.execute('ALTER TABLE hikes ADD COLUMN planned_date INTEGER');
      } catch (e) {
        // Ignore error
      }
      try {
        await db.execute('ALTER TABLE hikes ADD COLUMN place_id TEXT');
      } catch (e) {
        // Ignore error
      }
      try {
        await db.execute('ALTER TABLE hikes ADD COLUMN elevation_gain_m REAL');
      } catch (e) {
        // Ignore error
      }

      // Rename columns (SQLite doesn't support ALTER COLUMN, so we need to recreate the table)
      try {
        await db.execute('ALTER TABLE hikes RENAME TO hikes_old');
      } catch (e) {
        // Ignore error
      }

      try {
        await db.execute('''
        CREATE TABLE hikes(
          id INTEGER PRIMARY KEY,
          name TEXT NOT NULL,
          description TEXT,
          difficulty TEXT,
          planned_date INTEGER NOT NULL,
          date_utc INTEGER NOT NULL,
          is_completed INTEGER NOT NULL DEFAULT 0,
          is_favorite INTEGER NOT NULL DEFAULT 0,
          distance_km REAL,
          parking_status TEXT NOT NULL DEFAULT 'Unknown',
          location_name TEXT,
          place_id TEXT,
          latitude REAL,
          longitude REAL,
          elevation_gain_m REAL,
          created_at INTEGER NOT NULL,
          updated_at INTEGER
        )
      ''');
      } catch (e) {
        // Ignore error
      }

      // Copy data from old table to new table
      try {
        await db.execute('''
        INSERT INTO hikes (
          id, name, description, difficulty, planned_date, date_utc, 
          is_completed, is_favorite, distance_km, parking_status,
          location_name, place_id, latitude, longitude, elevation_gain_m,
          created_at, updated_at
        )
        SELECT 
          id, name, description, difficulty, COALESCE(planned_date, COALESCE(date_utc, created_at)) as planned_date,
          COALESCE(date_utc, created_at) as date_utc,
          is_completed, is_favorite, 
          length_km as distance_km,
          COALESCE(parking_available, 'Unknown') as parking_status,
          location_name, NULL as place_id, latitude, longitude, NULL as elevation_gain_m,
          created_at, updated_at
        FROM hikes_old
      ''');
      } catch (e) {
        // Ignore error
      }

      try {
        await db.execute('DROP TABLE IF EXISTS hikes_old');
      } catch (e) {
        // Ignore error
      }

      // Add new columns to observations table
      try {
        await db.execute('ALTER TABLE observations ADD COLUMN latitude REAL');
      } catch (e) {
        // Ignore error
      }
      try {
        await db.execute('ALTER TABLE observations ADD COLUMN longitude REAL');
      } catch (e) {
        // Ignore error
      }
      try {
        await db.execute('ALTER TABLE observations ADD COLUMN updated_at INTEGER');
      } catch (e) {
        // Ignore error
      }

      // Add new columns to observation_media table
      try {
        await db.execute('ALTER TABLE observation_media ADD COLUMN duration_seconds INTEGER');
      } catch (e) {
        // Ignore error
      }
      try {
        await db.execute('ALTER TABLE observation_media ADD COLUMN width INTEGER');
      } catch (e) {
        // Ignore error
      }
      try {
        await db.execute('ALTER TABLE observation_media ADD COLUMN height INTEGER');
      } catch (e) {
        // Ignore error
      }

      // Create profile table
      try {
        await db.execute('''
        CREATE TABLE profile(
          id INTEGER PRIMARY KEY,
          display_name TEXT NOT NULL,
          bio TEXT,
          avatar_path TEXT,
          language TEXT NOT NULL DEFAULT 'en',
          theme TEXT NOT NULL DEFAULT 'system',
          music_enabled INTEGER NOT NULL DEFAULT 0,
          notifications_enabled INTEGER NOT NULL DEFAULT 1,
          daily_reminder_hour INTEGER
        )
      ''');
      } catch (e) {
        // Ignore error
      }

      // Create settings table
      try {
        await db.execute('''
        CREATE TABLE settings(
          id INTEGER PRIMARY KEY,
          notification_morning_hour INTEGER NOT NULL DEFAULT 7,
          notification_evening_hour INTEGER NOT NULL DEFAULT 20,
          weather_enabled INTEGER NOT NULL DEFAULT 1,
          elevation_enabled INTEGER NOT NULL DEFAULT 1
        )
      ''');
      } catch (e) {
        // Ignore error
      }

      // Create indexes
      try {
        await db.execute('CREATE INDEX IF NOT EXISTS idx_hikes_completed ON hikes(is_completed)');
      } catch (e) {
        // Ignore error
      }
      try {
        await db.execute('CREATE INDEX IF NOT EXISTS idx_hikes_favorite ON hikes(is_favorite)');
      } catch (e) {
        // Ignore error
      }
      try {
        await db.execute('CREATE INDEX IF NOT EXISTS idx_hikes_planned_date ON hikes(planned_date)');
      } catch (e) {
        // Ignore error
      }
      try {
        await db.execute('CREATE INDEX IF NOT EXISTS idx_observations_hike_id ON observations(hike_id)');
      } catch (e) {
        // Ignore error
      }
      try {
        await db.execute('CREATE INDEX IF NOT EXISTS idx_observation_media_observation_id ON observation_media(observation_id)');
      } catch (e) {
        // Ignore error
      }
    }
  }

  Future _onOpen(Database db) async {
    // ensure foreign keys enforced on every open
    await db.execute('PRAGMA foreign_keys = ON');
  }

  Future close() async {
    final db = _db;
    if (db != null) await db.close();
    _db = null;
  }
}
