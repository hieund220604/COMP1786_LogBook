import 'package:flutter/material.dart';

class EmptyFeedView extends StatelessWidget {
  const EmptyFeedView({
    super.key,
    this.onNewHike,
  });

  final VoidCallback? onNewHike;

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      backgroundColor: isDark
          ? const Color(0xFF102213)     // background-dark
          : const Color(0xFFF6F8F6),    // background-light
      body: SafeArea(
        child: Container(
          width: double.infinity,
          constraints: const BoxConstraints(maxWidth: 480),
          child: Column(
            children: [
              // Top App Bar
              Container(
                height: 64,
                padding: const EdgeInsets.symmetric(horizontal: 16),
                alignment: Alignment.centerLeft,
                color: isDark
                    ? const Color(0xFF102213)
                    : const Color(0xFFF6F8F6),
                child: Text(
                  "My Hikes",
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: isDark ? Colors.white : const Color(0xFF0f172a),
                  ),
                ),
              ),

              // Content
              Expanded(
                child: Center(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 24),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        // Image
                        ClipRRect(
                          borderRadius: BorderRadius.circular(16),
                          child: Image.network(
                            "https://lh3.googleusercontent.com/aida-public/AB6AXuAnpf_f71HZHZgIQhja3SLPZIvKeXpbbKCirXfxLzMvuhEskwT9BCY-j_hEBUYi_SVOTzEOKJ8XgrfIUVmQIktaC7kmQXQY67DWtykvIzkVB7mKkcFwFhOWSzrNFh8XkheMpvvRaGITxYLwzuWxvQKTdog6mkGVaHsVNP9dreMlkCdeLCY6V-hbFTE7tjNobb5YPwEju0CUFxx2Kj-bakkPbgld0wmpI_PNZYM3eMScMWoDlWzpXqeCByM0YFDZgqev0HiBD17biww",
                            width: 280,
                            height: 280,
                            fit: BoxFit.cover,
                          ),
                        ),

                        const SizedBox(height: 24),

                        Text(
                          "Your adventure awaits!",
                          style: TextStyle(
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                            color: isDark ? Colors.white : const Color(0xFF0f172a),
                          ),
                          textAlign: TextAlign.center,
                        ),

                        const SizedBox(height: 8),

                        Text(
                          "Log your first completed hike to see it here.",
                          style: TextStyle(
                            fontSize: 14,
                            color: isDark
                                ? const Color(0xFF94a3b8)
                                : const Color(0xFF475569),
                          ),
                          textAlign: TextAlign.center,
                        ),

                        const SizedBox(height: 32),

                        SizedBox(
                          width: double.infinity,
                          height: 52,
                          child: ElevatedButton(
                            onPressed: onNewHike,
                            style: ElevatedButton.styleFrom(
                              backgroundColor: const Color(0xFF13EC37),
                              foregroundColor: const Color(0xFF102213),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(12),
                              ),
                              textStyle: const TextStyle(
                                fontSize: 14,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            child: const Text("Plan a New Hike"),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}
