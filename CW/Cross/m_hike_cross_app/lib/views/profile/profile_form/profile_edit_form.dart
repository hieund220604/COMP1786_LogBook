import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:m_hike_cross_app/viewmodels/profile_repository.dart';
import 'package:m_hike_cross_app/models/profile.dart';
import 'package:m_hike_cross_app/widgets/favorite_snackbar.dart';
import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart' as path;

class ProfileEditForm extends StatefulWidget {
  const ProfileEditForm({super.key});

  @override
  State<ProfileEditForm> createState() => _ProfileEditFormState();
}

class _ProfileEditFormState extends State<ProfileEditForm> {
  final ProfileRepository _profileRepository = ProfileRepository();
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _bioController = TextEditingController();

  String? _avatarPath;
  File? _newAvatarFile;
  bool _isLoading = true;
  bool _isSaving = false;

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  @override
  void dispose() {
    _nameController.dispose();
    _bioController.dispose();
    super.dispose();
  }

  Future<void> _loadProfile() async {
    try {
      final profile = await _profileRepository.getProfile();

      setState(() {
        _nameController.text = profile?.displayName ?? 'Hiker';
        _bioController.text = profile?.bio ?? '';
        _avatarPath = profile?.avatarPath;
        _isLoading = false;
      });
    } catch (e) {
      print('Error loading profile: $e');
      setState(() {
        _isLoading = false;
      });
    }
  }
  Future<bool> _requestPermission(ImageSource source) async {
    if (source == ImageSource.camera) {
      // Camera permission
      final status = await Permission.camera.status;

      if (status.isGranted) {
        return true;
      }

      if (status.isDenied) {
        final result = await Permission.camera.request();
        return result.isGranted;
      }

      if (status.isPermanentlyDenied) {
        await _showPermissionDialog('Camera permission is required to take photos.');
        return false;
      }
    } else {
      // Gallery permission - try photos first (Android 13+), fallback to storage
      var status = await Permission.photos.status;

      // If photos permission is not available, try storage permission
      if (status.isPermanentlyDenied || status.isRestricted) {
        status = await Permission.storage.status;
      }

      if (status.isGranted) {
        return true;
      }

      if (status.isDenied || status.isLimited) {
        // Try photos permission first
        var result = await Permission.photos.request();

        // If photos not granted, try storage
        if (!result.isGranted) {
          result = await Permission.storage.request();
        }

        return result.isGranted || result.isLimited;
      }

      if (status.isPermanentlyDenied) {
        await _showPermissionDialog('Storage permission is required to select photos.');
        return false;
      }
    }

    return false;
  }

  Future<void> _showPermissionDialog(String message) async {
    if (!mounted) return;

    final shouldOpenSettings = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Permission Required'),
        content: Text('$message Please grant permission in settings.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Open Settings'),
          ),
        ],
      ),
    );

    if (shouldOpenSettings == true) {
      await openAppSettings();
    }
  }


  Future<void> _pickImage(ImageSource source) async {
    try {
      // Request permission first
      final hasPermission = await _requestPermission(source);

      if (!hasPermission) {
        if (mounted) {
          FavoriteSnackBar.showError(
            context,
            message: 'Permission denied',
          );
        }
        return;
      }

      final picker = ImagePicker();
      final pickedFile = await picker.pickImage(
        source: source,
        maxWidth: 800,
        maxHeight: 800,
        imageQuality: 85,
      );

      if (pickedFile != null) {
        setState(() {
          _newAvatarFile = File(pickedFile.path);
        });
      }
    } catch (e) {
      print('Error picking image: $e');
      if (mounted) {
        FavoriteSnackBar.showError(
          context,
          message: 'Failed to pick image: ${e.toString()}',
        );
      }
    }
  }

  void _showImageSourceDialog() {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    showModalBottomSheet(
      context: context,
      backgroundColor: isDark ? const Color(0xFF1a2920) : Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) => SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 20),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                'Choose Image Source',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  color: isDark ? Colors.white : const Color(0xFF0f172a),
                ),
              ),
              const SizedBox(height: 20),
              ListTile(
                leading: Icon(
                  Icons.camera_alt,
                  color: isDark ? Colors.white70 : const Color(0xFF475569),
                ),
                title: Text(
                  'Camera',
                  style: TextStyle(
                    color: isDark ? Colors.white : const Color(0xFF0f172a),
                  ),
                ),
                onTap: () {
                  Navigator.pop(context);
                  _pickImage(ImageSource.camera);
                },
              ),
              ListTile(
                leading: Icon(
                  Icons.photo_library,
                  color: isDark ? Colors.white70 : const Color(0xFF475569),
                ),
                title: Text(
                  'Gallery',
                  style: TextStyle(
                    color: isDark ? Colors.white : const Color(0xFF0f172a),
                  ),
                ),
                onTap: () {
                  Navigator.pop(context);
                  _pickImage(ImageSource.gallery);
                },
              ),
              if (_avatarPath != null || _newAvatarFile != null)
                ListTile(
                  leading: const Icon(
                    Icons.delete,
                    color: Colors.red,
                  ),
                  title: const Text(
                    'Remove Avatar',
                    style: TextStyle(color: Colors.red),
                  ),
                  onTap: () {
                    Navigator.pop(context);
                    setState(() {
                      _avatarPath = null;
                      _newAvatarFile = null;
                    });
                  },
                ),
            ],
          ),
        ),
      ),
    );
  }

  Future<String?> _saveAvatarToAppDirectory(File imageFile) async {
    try {
      final appDir = await getApplicationDocumentsDirectory();
      final fileName = 'avatar_${DateTime.now().millisecondsSinceEpoch}${path.extension(imageFile.path)}';
      final savedPath = path.join(appDir.path, 'avatars', fileName);

      // Create avatars directory if not exists
      final avatarsDir = Directory(path.join(appDir.path, 'avatars'));
      if (!await avatarsDir.exists()) {
        await avatarsDir.create(recursive: true);
      }

      // Copy file to app directory
      final savedFile = await imageFile.copy(savedPath);
      return savedFile.path;
    } catch (e) {
      print('Error saving avatar: $e');
      return null;
    }
  }

  Future<void> _deleteOldAvatar(String? oldAvatarPath) async {
    if (oldAvatarPath != null && oldAvatarPath.isNotEmpty) {
      try {
        final oldFile = File(oldAvatarPath);
        if (await oldFile.exists()) {
          await oldFile.delete();
        }
      } catch (e) {
        print('Error deleting old avatar: $e');
      }
    }
  }

  Future<void> _saveProfile() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    setState(() {
      _isSaving = true;
    });

    try {
      String? finalAvatarPath = _avatarPath;

      // Save new avatar if picked
      if (_newAvatarFile != null) {
        // Delete old avatar
        await _deleteOldAvatar(_avatarPath);

        // Save new avatar
        finalAvatarPath = await _saveAvatarToAppDirectory(_newAvatarFile!);
      }

      // Get current profile to preserve ID
      final currentProfile = await _profileRepository.getProfile();

      // Create updated profile
      final updatedProfile = Profile(
        id: currentProfile?.id,
        displayName: _nameController.text.trim(),
        bio: _bioController.text.trim().isEmpty ? null : _bioController.text.trim(),
        avatarPath: finalAvatarPath,
        language: currentProfile?.language ?? 'en',
        theme: currentProfile?.theme ?? 'system',
        musicEnabled: currentProfile?.musicEnabled ?? 0,
        notificationsEnabled: currentProfile?.notificationsEnabled ?? 1,
        dailyReminderHour: currentProfile?.dailyReminderHour,
      );

      await _profileRepository.upsertProfile(updatedProfile);

      if (mounted) {
        FavoriteSnackBar.showSuccess(
          context,
          message: 'Profile updated successfully',
        );

        // Go back to profile screen
        Navigator.pop(context, true); // true indicates profile was updated
      }
    } catch (e) {
      print('Error saving profile: $e');
      if (mounted) {
        FavoriteSnackBar.showError(
          context,
          message: 'Failed to save profile',
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isSaving = false;
        });
      }
    }
  }

  String _getInitials(String? name) {
    if (name == null || name.isEmpty) return "H";
    return name.trim()[0].toUpperCase();
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    if (_isLoading) {
      return Scaffold(
        backgroundColor: isDark ? const Color(0xFF102213) : const Color(0xFFF6F8F6),
        body: const Center(
          child: CircularProgressIndicator(),
        ),
      );
    }

    return Scaffold(
      backgroundColor: isDark ? const Color(0xFF102213) : const Color(0xFFF6F8F6),
      appBar: AppBar(
        backgroundColor: isDark ? const Color(0xFF102213) : const Color(0xFFF6F8F6),
        elevation: 0,
        leading: IconButton(
          icon: Icon(
            Icons.close,
            color: isDark ? Colors.white : const Color(0xFF0f172a),
          ),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Edit Profile',
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: isDark ? Colors.white : const Color(0xFF0f172a),
          ),
        ),
        actions: [
          if (_isSaving)
            const Padding(
              padding: EdgeInsets.all(16.0),
              child: SizedBox(
                width: 24,
                height: 24,
                child: CircularProgressIndicator(strokeWidth: 2),
              ),
            )
          else
            TextButton(
              onPressed: _saveProfile,
              child: const Text(
                'Save',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF13EC37),
                ),
              ),
            ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              // Avatar with edit button
              GestureDetector(
                onTap: _showImageSourceDialog,
                child: Stack(
                  children: [
                    Container(
                      width: 120,
                      height: 120,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        border: Border.all(
                          color: isDark
                              ? const Color(0xFF19331E)
                              : Colors.white,
                          width: 4,
                        ),
                      ),
                      child: ClipOval(
                        child: _buildAvatarWidget(isDark),
                      ),
                    ),
                    Positioned(
                      bottom: 0,
                      right: 0,
                      child: Container(
                        padding: const EdgeInsets.all(8),
                        decoration: BoxDecoration(
                          color: const Color(0xFF13EC37),
                          shape: BoxShape.circle,
                          border: Border.all(
                            color: isDark ? const Color(0xFF102213) : const Color(0xFFF6F8F6),
                            width: 3,
                          ),
                        ),
                        child: const Icon(
                          Icons.camera_alt,
                          size: 20,
                          color: Colors.white,
                        ),
                      ),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 12),

              Text(
                'Tap to change avatar',
                style: TextStyle(
                  fontSize: 14,
                  color: isDark
                      ? const Color(0xFF94A3B8)
                      : const Color(0xFF6B7280),
                ),
              ),

              const SizedBox(height: 32),

              // Name field
              TextFormField(
                controller: _nameController,
                style: TextStyle(
                  color: isDark ? Colors.white : const Color(0xFF0f172a),
                ),
                decoration: InputDecoration(
                  labelText: 'Name',
                  labelStyle: TextStyle(
                    color: isDark
                        ? const Color(0xFF94A3B8)
                        : const Color(0xFF6B7280),
                  ),
                  hintText: 'Enter your name',
                  hintStyle: TextStyle(
                    color: isDark
                        ? const Color(0xFF64748b)
                        : const Color(0xFF94a3b8),
                  ),
                  filled: true,
                  fillColor: isDark
                      ? const Color(0xFF19331E)
                      : Colors.white,
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: BorderSide.none,
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: BorderSide(
                      color: isDark
                          ? const Color(0xFF19331E)
                          : const Color(0xFFe2e8f0),
                      width: 1,
                    ),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: const BorderSide(
                      color: Color(0xFF13EC37),
                      width: 2,
                    ),
                  ),
                  prefixIcon: Icon(
                    Icons.person,
                    color: isDark
                        ? const Color(0xFF94A3B8)
                        : const Color(0xFF6B7280),
                  ),
                ),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Name is required';
                  }
                  if (value.trim().length < 2) {
                    return 'Name must be at least 2 characters';
                  }
                  return null;
                },
              ),

              const SizedBox(height: 20),

              // Bio field
              TextFormField(
                controller: _bioController,
                maxLines: 3,
                maxLength: 150,
                style: TextStyle(
                  color: isDark ? Colors.white : const Color(0xFF0f172a),
                ),
                decoration: InputDecoration(
                  labelText: 'Bio',
                  labelStyle: TextStyle(
                    color: isDark
                        ? const Color(0xFF94A3B8)
                        : const Color(0xFF6B7280),
                  ),
                  hintText: 'Tell us about yourself...',
                  hintStyle: TextStyle(
                    color: isDark
                        ? const Color(0xFF64748b)
                        : const Color(0xFF94a3b8),
                  ),
                  filled: true,
                  fillColor: isDark
                      ? const Color(0xFF19331E)
                      : Colors.white,
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: BorderSide.none,
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: BorderSide(
                      color: isDark
                          ? const Color(0xFF19331E)
                          : const Color(0xFFe2e8f0),
                      width: 1,
                    ),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: const BorderSide(
                      color: Color(0xFF13EC37),
                      width: 2,
                    ),
                  ),
                  alignLabelWithHint: true,
                ),
              ),

              const SizedBox(height: 32),

              // Info text
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: const Color(0xFF13EC37).withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(
                    color: const Color(0xFF13EC37).withOpacity(0.3),
                    width: 1,
                  ),
                ),
                child: Row(
                  children: [
                    const Icon(
                      Icons.info_outline,
                      color: Color(0xFF13EC37),
                      size: 20,
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Text(
                        'Your profile information will be visible to you only',
                        style: TextStyle(
                          fontSize: 13,
                          color: isDark
                              ? Colors.white.withOpacity(0.8)
                              : const Color(0xFF0f172a).withOpacity(0.8),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildAvatarWidget(bool isDark) {
    // Show new picked image first
    if (_newAvatarFile != null) {
      return Image.file(
        _newAvatarFile!,
        fit: BoxFit.cover,
      );
    }

    // Show existing avatar
    if (_avatarPath != null && _avatarPath!.isNotEmpty) {
      final isNetwork = _avatarPath!.startsWith('http');

      if (isNetwork) {
        return Image.network(
          _avatarPath!,
          fit: BoxFit.cover,
          errorBuilder: (context, error, stackTrace) {
            return _buildAvatarHolder(isDark);
          },
        );
      } else {
        return Image.file(
          File(_avatarPath!),
          fit: BoxFit.cover,
          errorBuilder: (context, error, stackTrace) {
            return _buildAvatarHolder(isDark);
          },
        );
      }
    }

    // Show avatar holder with initials
    return _buildAvatarHolder(isDark);
  }

  Widget _buildAvatarHolder(bool isDark) {
    return Container(
      width: 120,
      height: 120,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [
            const Color(0xFF13EC37).withOpacity(0.8),
            const Color(0xFF0ea82e).withOpacity(0.9),
          ],
        ),
      ),
      child: Center(
        child: Text(
          _getInitials(_nameController.text),
          style: const TextStyle(
            fontSize: 48,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
      ),
    );
  }
}

