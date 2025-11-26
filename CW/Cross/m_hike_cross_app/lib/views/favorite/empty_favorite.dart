import 'package:flutter/material.dart';

class EmptyFavoriteView extends StatelessWidget {
  const EmptyFavoriteView({
    super.key,
    this.onBrowse,
  });

  final VoidCallback? onBrowse;

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return SafeArea(
      child: Container(
        width: double.infinity,
        constraints: const BoxConstraints(maxWidth: 480),
        padding: const EdgeInsets.symmetric(horizontal: 24),
        child: Column(
          children: [
            const SizedBox(height: 16),

            // Header
            Padding(
              padding: const EdgeInsets.only(left: 0),
              child: Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  "Favorites",
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: isDark ? Colors.white : const Color(0xFF0f172a),
                  ),
                ),
              ),
            ),

            const SizedBox(height: 32),

            Expanded(
              child: Center(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    // Illustration
                    Container(
                      width: 180,
                      height: 180,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: isDark
                            ? const Color(0xFF1f2933)
                            : const Color(0xFFE2E8F0),
                      ),
                      clipBehavior: Clip.antiAlias,
                      child: Image.network(
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuDpeBPz34BQhpPX5B8FdPV0SmjTlJ_44VjPAuka_7G_2qN55Qq_UoPVy39E3p4-2jDMjNf8-p6Yr6UNktdVBdyA41rOnliUUU-Qdwr8VVtgRPuaGP-2qobonFGmdLIba82NaOzvBznDJVe0bgmH9Gcf02F5QyfYtO2G1S3WcLIW2LF4XPzUUiCynh_SFDB5OEr1FAD0wHjF2UNigxcy8ET6mYhFdMPJec2cj9Eg5sp9rccVwyn7zexcVd7nTWQIj5WsBlOmFfI-Tqs",
                        fit: BoxFit.cover,
                        errorBuilder: (context, error, stackTrace) {
                          return Icon(
                            Icons.favorite_border,
                            size: 80,
                            color: isDark
                                ? const Color(0xFF9ca3af)
                                : const Color(0xFF13EC37).withOpacity(0.5),
                          );
                        },
                      ),
                    ),

                    const SizedBox(height: 28),

                    Text(
                      "No favorites yet?",
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: isDark ? Colors.white : const Color(0xFF0f172a),
                      ),
                    ),

                    const SizedBox(height: 8),

                    Text(
                      "Tap the star icon on any hike to save it here for quick access!",
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontSize: 14,
                        color: isDark
                            ? const Color(0xFF94a3b8)
                            : const Color(0xFF64748b),
                      ),
                    ),

                    const SizedBox(height: 32),

                    SizedBox(
                      width: 240,
                      height: 48,
                      child: ElevatedButton(
                        onPressed: onBrowse,
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF13EC37),
                          foregroundColor: Colors.black,
                          elevation: 0,
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                          textStyle: const TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        child: const Text("Browse Hikes"),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

