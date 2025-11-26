import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';

// Có thể tái dùng màu giống các màn khác
const Color kPrimary = Color(0xFF13EC49);
const Color kBackgroundLight = Color(0xFFF6F8F6);
const Color kBackgroundDark = Color(0xFF102215);

class PermissionCamScreen extends StatelessWidget {
  final VoidCallback? onAllow;
  final VoidCallback? onMaybeLater;

  const PermissionCamScreen({
    super.key,
    this.onAllow,
    this.onMaybeLater,
  });

  Future<void> _requestPermissions(BuildContext context) async {
    // Request camera first (primary). Photos permission can be requested too
    final cameraStatus = await Permission.camera.request();
    final photosStatus = await Permission.photos.request();

    // If any permission is permanently denied, open app settings so user can enable it.
    if (cameraStatus.isPermanentlyDenied || photosStatus.isPermanentlyDenied) {
      openAppSettings();
      return;
    }

    // If camera granted -> check notifications and navigate accordingly
    if (cameraStatus.isGranted) {
      bool alreadyAsked = false;
      try {
        final prefs = await SharedPreferences.getInstance();
        alreadyAsked = prefs.getBool('notifications_allowed') ?? false;
      } catch (_) {
        // ignore
      }

      if (alreadyAsked) {
        if (context.mounted) Navigator.of(context).pushReplacementNamed('/navbar');
        return;
      }

      final notificationStatus = await Permission.notification.status;
      if (notificationStatus.isGranted) {
        if (context.mounted) Navigator.of(context).pushReplacementNamed('/navbar');
      } else {
        if (context.mounted) Navigator.of(context).pushReplacementNamed('/permission_notification');
      }
      return;
    }

    // For all other outcomes (denied temporarily), continue to feed (navbar)
    if (context.mounted) {
      Navigator.of(context).pushReplacementNamed('/navbar');
    }
  }

  void _navigateToFeed(BuildContext context) {
    Navigator.of(context).pushReplacementNamed('/navbar');
  }

  @override
  Widget build(BuildContext context) {
    final bool isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      backgroundColor: isDark ? kBackgroundDark : kBackgroundLight,
      body: SafeArea(
        child: Container(
          width: double.infinity,
          height: double.infinity,
          color: isDark ? kBackgroundDark : kBackgroundLight,
          child: Column(
            children: [
              Expanded(
                child: Center(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        // Camera + photo overlay
                        SizedBox(
                          width: 224,
                          height: 224,
                          child: Stack(
                            alignment: Alignment.center,
                            children: [
                              Container(
                                width: 224,
                                height: 224,
                                decoration: BoxDecoration(
                                  color: kPrimary.withOpacity(0.2),
                                  shape: BoxShape.circle,
                                ),
                                child: const Center(
                                  child: Icon(
                                    Icons.photo_camera,
                                    size: 120,
                                    color: kPrimary,
                                  ),
                                ),
                              ),
                              Positioned(
                                right: 16,
                                bottom: 16,
                                child: Container(
                                  width: 80,
                                  height: 80,
                                  decoration: BoxDecoration(
                                    color: isDark
                                        ? kBackgroundDark
                                        : Colors.white,
                                    borderRadius: BorderRadius.circular(20),
                                    border: Border.all(
                                      color: isDark
                                          ? kBackgroundDark
                                          : Colors.transparent,
                                      width: 4,
                                    ),
                                    boxShadow: [
                                      BoxShadow(
                                        color: Colors.black.withOpacity(0.3),
                                        blurRadius: 12,
                                        offset: const Offset(0, 6),
                                      ),
                                    ],
                                  ),
                                  child: const Center(
                                    child: Icon(
                                      Icons.photo_library,
                                      size: 40,
                                      color: kPrimary,
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),

                        const SizedBox(height: 24),

                        // Title
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 16),
                          child: Text(
                            "Access Your Camera & Photos",
                            textAlign: TextAlign.center,
                            style: TextStyle(
                              fontFamily: "Inter",
                              fontWeight: FontWeight.bold,
                              fontSize: 32,
                              height: 1.1,
                              color: isDark ? Colors.white : Colors.black87,
                            ),
                          ),
                        ),

                        const SizedBox(height: 12),

                        // Description
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 16),
                          child: Text(
                            "To capture and share photos from your amazing hikes, M-Hike needs access to your camera and photo library.",
                            textAlign: TextAlign.center,
                            style: TextStyle(
                              fontFamily: "Inter",
                              fontSize: 16,
                              height: 1.4,
                              color: isDark
                                  ? const Color(0xFFCBD5F5)
                                  : const Color(0xFF475569),
                            ),
                          ),
                        ),

                        const SizedBox(height: 24),

                        // Allow Access button
                        Padding(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 16,
                            vertical: 8,
                          ),
                          child: SizedBox(
                            height: 48,
                            width: double.infinity,
                            child: ElevatedButton(
                              style: ElevatedButton.styleFrom(
                                backgroundColor: kPrimary,
                                shape: const StadiumBorder(),
                              ),
                              onPressed: onAllow ??
                                  () {
                                    _requestPermissions(context);
                                  },
                              child: const Text(
                                "Allow Access",
                                style: TextStyle(
                                  fontFamily: "Inter",
                                  fontWeight: FontWeight.bold,
                                  fontSize: 16,
                                  color: kBackgroundDark,
                                ),
                              ),
                            ),
                          ),
                        ),

                        // Maybe Later button
                        Padding(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 16,
                            vertical: 4,
                          ),
                          child: SizedBox(
                            height: 40,
                            width: double.infinity,
                            child: TextButton(
                              style: TextButton.styleFrom(
                                shape: const StadiumBorder(),
                              ),
                              onPressed: onMaybeLater ??
                                  () {
                                    // When user skips camera permission, they may still want the app — check notifications and go to navbar
                                    _navigateToFeed(context);
                                  },
                              child: Text(
                                "Maybe Later",
                                style: TextStyle(
                                  fontFamily: "Inter",
                                  fontWeight: FontWeight.bold,
                                  fontSize: 14,
                                  color: isDark
                                      ? const Color(0xFF94A3B8)
                                      : const Color(0xFF64748B),
                                ),
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
