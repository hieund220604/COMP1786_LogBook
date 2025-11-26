import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';

class MHikeWelcomeScreen extends StatefulWidget {
  const MHikeWelcomeScreen({super.key});

  static const Color primary = Color(0xFF13EC49);
  static const Color backgroundDark = Color(0xFF102215);

  @override
  State<MHikeWelcomeScreen> createState() => _MHikeWelcomeScreenState();
}

class _MHikeWelcomeScreenState extends State<MHikeWelcomeScreen>
    with TickerProviderStateMixin {
  late AnimationController _logoController;
  late AnimationController _titleController;
  late AnimationController _featuresController;
  late AnimationController _buttonController;

  late Animation<double> _logoAnimation;
  late Animation<double> _titleAnimation;
  late Animation<double> _featuresAnimation;
  late Animation<double> _buttonAnimation;

  @override
  void initState() {
    super.initState();

    // Logo animation
    _logoController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    _logoAnimation = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(parent: _logoController, curve: Curves.easeOut),
    );

    // Title animation (starts after logo)
    _titleController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    _titleAnimation = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(parent: _titleController, curve: Curves.easeOut),
    );

    // Features animation (starts after title)
    _featuresController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    _featuresAnimation = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(parent: _featuresController, curve: Curves.easeOut),
    );

    // Button animation (starts after features)
    _buttonController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    _buttonAnimation = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(parent: _buttonController, curve: Curves.easeOut),
    );

    // Start animations in sequence
    _startAnimationSequence();
  }

  void _startAnimationSequence() async {
    await _logoController.forward();
    await _titleController.forward();
    await _featuresController.forward();
    await _buttonController.forward();
  }

  @override
  void dispose() {
    _logoController.dispose();
    _titleController.dispose();
    _featuresController.dispose();
    _buttonController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          // Background image
          Positioned.fill(
            child: Stack(
              children: [
                Positioned.fill(
                  child: Image.network(
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuBshSmF5VnfMSO1GBZQqqNBet6mgfxWdGXeHBudam2n72PbURcZHVK6C4GLbycAEqI9Z22c8daMGTspqFwCu96A74uA3bmVR-qBjftjLZxqekPvsmB-3Tx4WNM2SqE3-dGkbj4hpmtRBUa217KPMkb-IY_vjTP6Zgj3WenvmfE8WM0Ja7HO4_XuevQCPwJcSzlC2jvIew76y1-DmlJYksKonRnMX2-PVHle_9Q8BQtbUuP26DW0WIlhfd2OYlQo-ii2MKBH8LH0lZA",
                    fit: BoxFit.cover,
                  ),
                ),
                Positioned.fill(
                  child: Container(
                    color: MHikeWelcomeScreen.backgroundDark.withOpacity(0.8),
                  ),
                ),
              ],
            ),
          ),

          // Content
          Positioned.fill(
            child: SafeArea(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  children: [
                    // App logo with fade animation
                    FadeTransition(
                      opacity: _logoAnimation,
                      child: Padding(
                        padding: const EdgeInsets.only(top: 16),
                        child: Center(
                          child: Container(
                            height: 64,
                            width: 64,
                            decoration: BoxDecoration(
                              color: MHikeWelcomeScreen.primary.withOpacity(0.2),
                              borderRadius: BorderRadius.circular(24),
                            ),
                            child: const Center(
                              child: Icon(
                                Icons.hiking,
                                size: 32,
                                color: MHikeWelcomeScreen.primary,
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),

                    // Middle content
                    Expanded(
                      child: Center(
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            // Title with fade animation
                            FadeTransition(
                              opacity: _titleAnimation,
                              child: Padding(
                                padding:
                                const EdgeInsets.symmetric(horizontal: 16),
                                child: Text(
                                  "Welcome to M-Hike",
                                  textAlign: TextAlign.center,
                                  style: const TextStyle(
                                    fontFamily: "Inter",
                                    fontWeight: FontWeight.bold,
                                    fontSize: 32,
                                    height: 1.1,
                                    color: Colors.white,
                                  ),
                                ),
                              ),
                            ),
                            const SizedBox(height: 24),
                            // Features with fade animation
                            FadeTransition(
                              opacity: _featuresAnimation,
                              child: SizedBox(
                                width: 360,
                                child: Column(
                                  mainAxisSize: MainAxisSize.min,
                                  children: [
                                    _FeatureItem(
                                      icon: Icons.photo_camera_outlined,
                                      text:
                                      "Capture hikes with photos and videos",
                                      primary: MHikeWelcomeScreen.primary,
                                      backgroundDark: MHikeWelcomeScreen.backgroundDark,
                                    ),
                                    const SizedBox(height: 12),
                                    _FeatureItem(
                                      icon: Icons.map_outlined,
                                      text:
                                      "Plan hikes with maps and weather",
                                      primary: MHikeWelcomeScreen.primary,
                                      backgroundDark: MHikeWelcomeScreen.backgroundDark,
                                    ),
                                    const SizedBox(height: 12),
                                    _FeatureItem(
                                      icon: Icons.forum_outlined,
                                      text:
                                      "React, chat, and share with friends",
                                      primary: MHikeWelcomeScreen.primary,
                                      backgroundDark: MHikeWelcomeScreen.backgroundDark,
                                    ),
                                  ],
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),

                    // Action button with fade animation
                    FadeTransition(
                      opacity: _buttonAnimation,
                      child: Padding(
                        padding: const EdgeInsets.only(bottom: 24, top: 24),
                        child: SizedBox(
                          width: double.infinity,
                          height: 56,
                          child: ElevatedButton(
                            style: ElevatedButton.styleFrom(
                              backgroundColor: MHikeWelcomeScreen.primary,
                              shape: const StadiumBorder(),
                              elevation: 6,
                            ),
                            onPressed: () {
                              // Check existing permissions in sequence:
                              () async {
                                final cameraStatus = await Permission.camera.status;
                                // If camera permission already granted -> check notifications
                                if (cameraStatus.isGranted) {
                                  // Check saved flag first
                                  bool alreadyAsked = false;
                                  try {
                                    final prefs = await SharedPreferences.getInstance();
                                    alreadyAsked = prefs.getBool('notifications_allowed') ?? false;
                                  } catch (_) {
                                    // ignore
                                  }

                                  if (alreadyAsked) {
                                    if (context.mounted) {
                                      Navigator.of(context).pushReplacementNamed('/navbar');
                                    }
                                    return;
                                  }

                                  // If saved flag not set, check system notification permission
                                  final notificationStatus = await Permission.notification.status;
                                  if (notificationStatus.isGranted) {
                                    if (context.mounted) {
                                      Navigator.of(context).pushReplacementNamed('/navbar');
                                    }
                                  } else {
                                    // Notifications not granted -> ask for them
                                    if (context.mounted) {
                                      Navigator.of(context).pushReplacementNamed('/permission_notification');
                                    }
                                  }
                                } else {
                                  // Camera not granted -> go to camera permission screen
                                  if (context.mounted) {
                                    Navigator.of(context).pushReplacementNamed('/permission');
                                  }
                                }
                              }();
                            },
                            child: const Text(
                              "Get started",
                              style: TextStyle(
                                fontFamily: "Inter",
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                                color: MHikeWelcomeScreen.backgroundDark,
                              ),
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
    );
  }
}

class _FeatureItem extends StatelessWidget {
  final IconData icon;
  final String text;
  final Color primary;
  final Color backgroundDark;

  const _FeatureItem({
    required this.icon,
    required this.text,
    required this.primary,
    required this.backgroundDark,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      constraints: const BoxConstraints(minHeight: 56),
      padding: const EdgeInsets.symmetric(horizontal: 16),
      decoration: BoxDecoration(
        color: backgroundDark.withOpacity(0.5),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        children: [
          Container(
            height: 40,
            width: 40,
            decoration: BoxDecoration(
              color: primary.withOpacity(0.2),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Center(
              child: Icon(
                icon,
                color: primary,
                size: 24,
              ),
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Text(
              text,
              style: const TextStyle(
                fontFamily: "Inter",
                fontSize: 16,
                color: Colors.white70,
                height: 1.3,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
