import 'package:flutter/material.dart';
import 'views/splash/splash.dart';
import 'views/splash/welcome.dart';
import 'views/splash/permission_cam.dart';
import 'views/splash/permission_notification.dart';
import 'views/home_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  Route<dynamic> _createRoute(Widget page) {
    return PageRouteBuilder(
      pageBuilder: (context, animation, secondaryAnimation) => page,
      transitionsBuilder: (context, animation, secondaryAnimation, child) {
        return FadeTransition(
          opacity: animation,
          child: child,
        );
      },
      transitionDuration: const Duration(milliseconds: 800),
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'M-Hike',
      debugShowCheckedModeBanner: false,
      theme: ThemeData.light(),
      darkTheme: ThemeData.dark(),
      themeMode: ThemeMode.system,
      home: const MHikeSplashScreen(),
      onGenerateRoute: (settings) {
        switch (settings.name) {
          case '/welcome':
            return _createRoute(const MHikeWelcomeScreen());
          case '/permission':
            return _createRoute(const PermissionCamScreen());
          case '/permission_notification':
            return _createRoute(const PermissionNotificationView());
          case '/navbar':
            return _createRoute(const HomeScreen());
          default:
            return null;
        }
      },
    );
  }
}
