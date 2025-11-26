import 'package:flutter/material.dart';
import 'package:m_hike_cross_app/db/database.dart';

class MHikeSplashScreen extends StatefulWidget {
  const MHikeSplashScreen({super.key});

  @override
  State<MHikeSplashScreen> createState() => _MHikeSplashScreenState();
}

class _MHikeSplashScreenState extends State<MHikeSplashScreen> {
  @override
  void initState() {
    super.initState();
    _initializeApp();
  }

  Future<void> _initializeApp() async {
    try {
      // Initialize database (this will trigger migration if needed)
      await AppDatabase.instance.database;

      // Navigate to welcome screen after initialization
      await Future.delayed(const Duration(seconds: 3));

      if (mounted) {
        Navigator.of(context).pushReplacementNamed('/welcome');
      }
    } catch (e) {
      // Still navigate even if there's an error
      await Future.delayed(const Duration(seconds: 3));
      if (mounted) {
        Navigator.of(context).pushReplacementNamed('/welcome');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    const Color primary = Color(0xFF13EC49);
    const Color backgroundLight = Color(0xFFF6F8F6);
    const Color backgroundDark = Color(0xFF102215);

    final bool isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      body: Container(
        width: double.infinity,
        height: double.infinity,
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: isDark
                ? [
              backgroundDark,
              primary.withOpacity(0.2),
            ]
                : [
              backgroundLight,
              primary.withOpacity(0.2),
            ],
          ),
        ),
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            // Main content area
            Expanded(
              child: Center(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    // Icon (M-Hike logo)
                    const Padding(
                      padding: EdgeInsets.only(bottom: 16),
                      child: Icon(
                        Icons.terrain,
                        size: 96,
                        color: primary,
                      ),
                    ),
                    // Title
                    Text(
                      'M-Hike',
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontFamily: 'Inter',
                        fontWeight: FontWeight.bold,
                        fontSize: 32,
                        height: 1.1,
                        color: isDark ? Colors.white : backgroundDark,
                      ),
                    ),
                  ],
                ),
              ),
            ),

            // Bottom loading area
            SizedBox(
              height: 80,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // Loading indicator
                  SizedBox(
                    height: 24,
                    width: 24,
                    child: Stack(
                      alignment: Alignment.center,
                      children: [
                        // Outer ring
                        Container(
                          height: 24,
                          width: 24,
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            border: Border.all(
                              color: primary.withOpacity(0.3),
                              width: 2,
                            ),
                          ),
                        ),
                        // Spinning arc
                        const _SpinRing(color: primary),
                      ],
                    ),
                  ),
                  const SizedBox(height: 12),
                  // Loading text
                  Text(
                    'Loading...',
                    style: TextStyle(
                      fontFamily: 'Inter',
                      fontSize: 16,
                      color: (isDark ? Colors.white : backgroundDark)
                          .withOpacity(0.6),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _SpinRing extends StatefulWidget {
  final Color color;

  const _SpinRing({required this.color});

  @override
  State<_SpinRing> createState() => _SpinRingState();
}

class _SpinRingState extends State<_SpinRing>
    with SingleTickerProviderStateMixin {
  late final AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    )..repeat();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return RotationTransition(
      turns: _controller,
      child: CustomPaint(
        size: const Size(24, 24),
        painter: _RingPainter(color: widget.color),
      ),
    );
  }
}

class _RingPainter extends CustomPainter {
  final Color color;

  _RingPainter({required this.color});

  @override
  void paint(Canvas canvas, Size size) {
    const strokeWidth = 2.0;
    final paint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round
      ..color = color;

    const startAngleTop = -3.14 / 2;
    const sweepAngle = 3.14 / 2;

    final inset = strokeWidth / 2;
    final arcRect = Rect.fromLTWH(
      inset,
      inset,
      size.width - strokeWidth,
      size.height - strokeWidth,
    );

    canvas.drawArc(arcRect, startAngleTop, sweepAngle, false, paint);
    canvas.drawArc(arcRect, startAngleTop + 3.14, sweepAngle, false, paint);
  }

  @override
  bool shouldRepaint(covariant _RingPainter oldDelegate) {
    return oldDelegate.color != color;
  }
}
