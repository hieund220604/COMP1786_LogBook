import 'package:flutter/material.dart';

const Color kNavBackgroundDark = Color(0xFF0A100C);
const Color kNavBorderDark = Color(0xFF203529);
const Color kNavPrimary = Color(0xFF3EFF8B);
const Color kNavTextTertiaryDark = Color(0xFF5B6F63);
const Color kNavTextSecondaryDark = Color(0xFF9BAEA5);

class MHikeNavBar extends StatelessWidget {
  final int selectedIndex;
  final ValueChanged<int>? onItemSelected;

  const MHikeNavBar({
    super.key,
    this.selectedIndex = 0,
    this.onItemSelected,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: const BoxDecoration(
        border: Border(
          top: BorderSide(color: kNavBorderDark, width: 1),
        ),
        color: Color(0xCC0A100C), // background-dark/80
      ),
      child: SafeArea(
        top: false,
        child: SizedBox(
          height: 88,
          child: Padding(
            padding: const EdgeInsets.only(top: 12),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _NavItem(
                  icon: Icons.layers,
                  label: 'Feed',
                  isActive: selectedIndex == 0,
                  onTap: () => _onTap(0),
                ),
                _NavItem(
                  icon: Icons.edit_calendar,
                  label: 'Plan',
                  isActive: selectedIndex == 1,
                  onTap: () => _onTap(1),
                ),
                _NavItem(
                  icon: Icons.favorite,
                  label: 'Favorite',
                  isActive: selectedIndex == 2,
                  onTap: () => _onTap(2),
                ),
                _NavItem(
                  icon: Icons.account_circle,
                  label: 'Profile',
                  isActive: selectedIndex == 3,
                  onTap: () => _onTap(3),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  void _onTap(int index) {
    if (onItemSelected != null) {
      onItemSelected!(index);
    }
  }
}

class _NavItem extends StatelessWidget {
  final IconData icon;
  final String label;
  final bool isActive;
  final VoidCallback onTap;

  const _NavItem({
    required this.icon,
    required this.label,
    required this.isActive,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final Color color =
    isActive ? kNavPrimary : kNavTextTertiaryDark;

    final FontWeight fontWeight =
    isActive ? FontWeight.bold : FontWeight.w500;

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(999),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 4),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              icon,
              size: 24,
              color: color,
            ),
            const SizedBox(height: 4),
            Text(
              label,
              style: TextStyle(
                fontSize: 12,
                fontWeight: fontWeight,
                color: isActive ? kNavPrimary : kNavTextSecondaryDark,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
