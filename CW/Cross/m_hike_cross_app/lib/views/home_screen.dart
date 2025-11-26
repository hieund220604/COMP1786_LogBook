import 'package:flutter/material.dart';
import 'package:m_hike_cross_app/views/navbar/navbar.dart';
import 'package:m_hike_cross_app/views/feed/complete_hike.dart';
import 'package:m_hike_cross_app/views/plan/plan.dart';
import 'package:m_hike_cross_app/views/favorite/favorite.dart';
import 'package:m_hike_cross_app/views/profile/profile.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _selectedIndex = 0;


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _buildBody(),
      bottomNavigationBar: MHikeNavBar(
        selectedIndex: _selectedIndex,
        onItemSelected: (index) {
          setState(() {
            _selectedIndex = index;
          });
        },
      ),
    );
  }

  Widget _buildBody() {
    switch (_selectedIndex) {
      case 0:
        return const CompleteHikeFeedScreen();
      case 1:
        return const PlanScreen();
      case 2:
        return FavoriteScreen(
          onBrowse: () {
            // Navigate to Feed tab
            setState(() {
              _selectedIndex = 0;
            });
          },
        );
      case 3:
        return const ProfileScreen();
      default:
        return const CompleteHikeFeedScreen();
    }
  }
}
