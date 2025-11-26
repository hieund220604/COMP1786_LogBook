import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';

class PermissionNotificationView extends StatelessWidget {
  const PermissionNotificationView({
    super.key,
    this.onAllow,
    this.onLater,
  });

  final VoidCallback? onAllow;
  final VoidCallback? onLater;

  static const _kNotifAllowedKey = 'notifications_allowed';

  Future<void> _setNotifAllowedFlag(bool value) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.setBool(_kNotifAllowedKey, value);
    } catch (_) {
      // ignore errors - flag is best-effort
    }
  }

  Future<void> _requestNotificationPermission(BuildContext context) async {
    final status = await Permission.notification.request();
    if (status.isPermanentlyDenied) {
      openAppSettings();
      return;
    }
    // If granted or limited, save the flag so we don't show the screen again
    if (status.isGranted || status.isLimited) {
      await _setNotifAllowedFlag(true);
    }
    if (context.mounted) Navigator.of(context).pushReplacementNamed('/navbar');
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      backgroundColor: isDark
          ? const Color(0xFF102213) // background-dark
          : const Color(0xFFF6F8F6), // background-light
      body: Center(
        child: Container(
          constraints: const BoxConstraints(maxWidth: 420),
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // Icon circle
              Container(
                height: 128,
                width: 128,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: isDark
                      ? const Color(0xFF2A4B31) // primary-dark-bg
                      : const Color(0xFFE2FEE6), // primary-light-bg
                ),
                child: Icon(
                  Icons.notifications,
                  size: 72,
                  color: isDark
                      ? const Color(0xFFA3FFAE) // primary-dark-text
                      : const Color(0xFF0B5F17), // primary-light-text
                ),
              ),

              const SizedBox(height: 32),

              Text(
                "Stay on Track",
                style: TextStyle(
                  fontSize: 28,
                  fontWeight: FontWeight.w700,
                  color: isDark ? Colors.white : const Color(0xFF0f172a),
                ),
                textAlign: TextAlign.center,
              ),

              const SizedBox(height: 12),

              Text(
                "Enable notifications to get timely reminders for your upcoming planned hikes. Never miss an adventure!",
                style: TextStyle(
                  fontSize: 16,
                  height: 1.4,
                  color: isDark
                      ? const Color(0xFF94a3b8)
                      : const Color(0xFF475569),
                ),
                textAlign: TextAlign.center,
              ),

              const SizedBox(height: 48),

              // Buttons
              Column(
                children: [
                  SizedBox(
                    width: double.infinity,
                    height: 52,
                    child: ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF13EC37),
                        foregroundColor: Colors.black,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(999),
                        ),
                        textStyle: const TextStyle(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      onPressed: onAllow ?? () { _requestNotificationPermission(context); },
                      child: const Text("Allow notifications"),
                    ),
                  ),
                  const SizedBox(height: 12),
                  SizedBox(
                    width: double.infinity,
                    height: 52,
                    child: TextButton(
                      style: TextButton.styleFrom(
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(999),
                        ),
                      ),
                      onPressed: onLater ?? () { _setNotifAllowedFlag(true).then((_) => Navigator.of(context).pushReplacementNamed('/navbar')); },
                      child: Text(
                        "Maybe later",
                        style: TextStyle(
                          fontSize: 15,
                          fontWeight: FontWeight.w600,
                          color: isDark
                              ? const Color(0xFF94a3b8)
                              : const Color(0xFF64748b),
                        ),
                      ),
                    ),
                  ),
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}
