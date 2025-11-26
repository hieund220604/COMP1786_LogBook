import 'dart:io';
import 'package:flutter/material.dart';
import 'package:m_hike_cross_app/viewmodels/profile_repository.dart';
import 'package:m_hike_cross_app/models/profile.dart';
import 'package:m_hike_cross_app/views/profile/profile_form/profile_edit_form.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final ProfileRepository _profileRepository = ProfileRepository();
  String? _userName;
  String? _userAvatar;
  String? _userBio;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadUserProfile();
  }

  Future<void> _loadUserProfile() async {
    try {
      print('=== Starting to load profile ===');

      // Verify database first
      await _profileRepository.verifyDatabase();

      // Load profile from DB
      final profile = await _profileRepository.getProfile();

      print('=== Profile loaded ===');
      print('Profile object: $profile');
      print('Name: ${profile?.displayName}');
      print('Bio: ${profile?.bio}');
      print('Avatar: ${profile?.avatarPath}');

      setState(() {
        _userName = profile?.displayName ?? 'Hiker';
        _userAvatar = profile?.avatarPath;
        _userBio = profile?.bio;
        _isLoading = false;
      });

      print('=== State updated ===');
      print('_userName: $_userName');
      print('_userBio: $_userBio');
      print('_userAvatar: $_userAvatar');
    } catch (e, stackTrace) {
      print('Error loading profile: $e');
      print('Stack trace: $stackTrace');
      setState(() {
        _userName = 'Hiker';
        _userAvatar = null;
        _userBio = null;
        _isLoading = false;
      });
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

    return SafeArea(
      child: Scaffold(
        backgroundColor: isDark ? const Color(0xFF102213) : const Color(0xFFF6F8F6),
        body: Column(
          children: [
            // Top App Bar - đồng bộ với các tab khác
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 16),
              child: Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  "Profile",
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: isDark ? Colors.white : const Color(0xFF0F172A),
                  ),
                ),
              ),
            ),

            Expanded(
              child: ListView(
                padding: EdgeInsets.zero,
                children: [
                  const SizedBox(height: 8),

                  // Avatar + name
                  Column(
                    children: [
                      const SizedBox(height: 10),
                      // Avatar with holder if no image
                      Container(
                        width: 96,
                        height: 96,
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
                          child: _userAvatar != null && _userAvatar!.isNotEmpty
                              ? Image.file(
                                  File(_userAvatar!),
                                  fit: BoxFit.cover,
                                  errorBuilder: (context, error, stackTrace) {
                                    return _buildAvatarHolder(isDark);
                                  },
                                )
                              : _buildAvatarHolder(isDark),
                        ),
                      ),
                      const SizedBox(height: 12),
                      Text(
                        _userName ?? "Hiker",
                        style: TextStyle(
                          fontSize: 22,
                          fontWeight: FontWeight.bold,
                          color: isDark ? Colors.white : const Color(0xFF0F172A),
                        ),
                      ),
                      // Bio
                      if (_userBio != null && _userBio!.isNotEmpty) ...[
                        const SizedBox(height: 8),
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 32),
                          child: Text(
                            _userBio!,
                            textAlign: TextAlign.center,
                            maxLines: 2,
                            overflow: TextOverflow.ellipsis,
                            style: TextStyle(
                              fontSize: 14,
                              color: isDark
                                  ? const Color(0xFF94A3B8)
                                  : const Color(0xFF6B7280),
                            ),
                          ),
                        ),
                      ],
                    ],
                  ),

                  const SizedBox(height: 24),

                  // List items
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    child: Column(
                      children: [
                        _menuItem(
                          isDark: isDark,
                          icon: Icons.person,
                          text: "Edit profile",
                        ),
                        _menuItem(
                          isDark: isDark,
                          icon: Icons.settings,
                          text: "App settings",
                        ),
                        _menuItem(
                          isDark: isDark,
                          icon: Icons.bar_chart,
                          text: "Dashboard statistics",
                        ),
                        _menuItem(
                          isDark: isDark,
                          icon: Icons.info,
                          text: "About app",
                        ),
                      ],
                    ),
                  ),

                  const SizedBox(height: 40),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAvatarHolder(bool isDark) {
    return Container(
      width: 96,
      height: 96,
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
          _getInitials(_userName),
          style: const TextStyle(
            fontSize: 36,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
      ),
    );
  }
  Future<void> _navigateToEditProfile() async {
    final result = await Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => const ProfileEditForm(),
      ),
    );

    // Reload profile if edited
    if (result == true) {
      _loadUserProfile();
    }
  }


  Widget _menuItem({
    required bool isDark,
    required IconData icon,
    required String text,
  }) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: isDark
            ? const Color(0xFF19331E)
            : const Color(0xFFe2e8f0).withOpacity(0.5),
        borderRadius: BorderRadius.circular(14),
      ),
      child: ListTile(
        leading: Icon(
          icon,
          color: isDark ? Colors.white70 : const Color(0xFF475569),
        ),
        title: Text(
          text,
          style: TextStyle(
            fontSize: 15,
            fontWeight: FontWeight.w500,
            color: isDark ? Colors.white : const Color(0xFF0F172A),
          ),
        ),
        trailing: Icon(
          Icons.arrow_forward_ios,
          size: 16,
          color: isDark ? Colors.white54 : const Color(0xFF94A3B8),

        ),
        onTap: text == 'Edit profile' ? _navigateToEditProfile : () {},
      ),
    );
  }
}

