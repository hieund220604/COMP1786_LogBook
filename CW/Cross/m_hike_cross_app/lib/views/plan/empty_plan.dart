import 'package:flutter/material.dart';

class EmptyPlanView extends StatelessWidget {
  const EmptyPlanView({
    super.key,
    this.onStartPlanning,
  });

  final VoidCallback? onStartPlanning;

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Container(
      width: double.infinity,
      constraints: const BoxConstraints(maxWidth: 480),
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Illustration
            Container(
              width: 280,
              height: 280,
              decoration: BoxDecoration(
                image: DecorationImage(
                  image: NetworkImage(
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuAXiL_KG5xQiC5z9B-DqXpT1walHBAMJbUSEVGG_ThE2XmcC7ON9iav-_D-BMpVNvnwau_ViZVb970kj_eNukV9DmUsYp2H2BUdYOuYOJWn9N2NazyC9-T6zX9kMaM6PARYjbAK_qk9K3ws6GYDXhaASsRq7oiqDETR-YCKMeWuxZkl6F1CIDMrKLJLVJMBCro4x0xGSwERytw6jVPt2lum6lOzuHefvbGqS2Z2DvDD4LUmCGcHuHbZ3_G1FY3Xq5EIqhBCi6hfRxo",
                  ),
                  fit: BoxFit.contain,
                ),
              ),
            ),

            const SizedBox(height: 24),

            Text(
              "Ready to explore?",
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: isDark
                    ? const Color(0xFFF2F2F7)
                    : const Color(0xFF1C1C1E),
              ),
            ),

            const SizedBox(height: 8),

            Text(
              "Plan your next journey and see it here.",
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 14,
                color: isDark
                    ? const Color(0xFF8E8E93)
                    : const Color(0xFF636366),
              ),
            ),

            const SizedBox(height: 32),

            SizedBox(
              width: 240,
              height: 48,
              child: ElevatedButton(
                onPressed: onStartPlanning,
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
                child: const Text("Start Planning Now"),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
