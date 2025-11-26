import 'dart:io';
import 'package:flutter/material.dart';
import 'package:m_hike_cross_app/models/hike.dart';

/// Hike card item cho danh sách completed hikes
class CompletedHikeCard extends StatelessWidget {
  const CompletedHikeCard({
    super.key,
    required this.hike,
    this.thumbnailUrl,
    this.onTap,
    this.onToggleFavorite,
  });

  final Hike hike;
  final String? thumbnailUrl;
  final VoidCallback? onTap;
  final VoidCallback? onToggleFavorite;

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    final dateLabel = _formatDate(hike.plannedDate);
    final distanceLabel = hike.distanceKm != null
        ? "${hike.distanceKm!.toStringAsFixed(1)} mi"
        : null;

    return InkWell(
      borderRadius: BorderRadius.circular(20),
      onTap: onTap,
      child: Container(
        decoration: BoxDecoration(
          color: isDark ? const Color(0xFF1f2937) : const Color(0xFFf3f4f6),
          borderRadius: BorderRadius.circular(20),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.1),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Image with favorite button
            Stack(
              children: [
                _HikeThumbnail(thumbnailUrl: thumbnailUrl),
                // Favorite button on top right
                if (onToggleFavorite != null)
                  Positioned(
                    top: 12,
                    right: 12,
                    child: GestureDetector(
                      onTap: onToggleFavorite,
                      child: Container(
                        width: 44,
                        height: 44,
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.3),
                          shape: BoxShape.circle,
                        ),
                        child: Icon(
                          hike.isFavorite == 1
                              ? Icons.favorite
                              : Icons.favorite_border,
                          color: Colors.red,
                          size: 24,
                        ),
                      ),
                    ),
                  ),
              ],
            ),

            // Content
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Title
                  Text(
                    hike.name,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                    style: TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.w600,
                      color: isDark ? Colors.white : const Color(0xFF111827),
                      height: 1.2,
                    ),
                  ),

                  const SizedBox(height: 8),

                  // Date and distance info
                  if (dateLabel != null || hike.locationName != null)
                    Text(
                      '${dateLabel ?? ''} ${dateLabel != null && hike.locationName != null ? '• ' : ''}${hike.locationName != null ? '${_getDistanceFromLocation(hike.locationName!)} away' : ''}',
                      style: TextStyle(
                        fontSize: 14,
                        color: isDark ? Colors.white.withOpacity(0.7) : const Color(0xFF6b7280),
                      ),
                    ),

                  const SizedBox(height: 12),

                  // Chips
                  Wrap(
                    spacing: 8,
                    runSpacing: 8,
                    children: [
                      if (hike.difficulty != null)
                        _DifficultyChip(label: hike.difficulty!),
                      if (distanceLabel != null)
                        _InfoChip(
                          label: distanceLabel,
                        ),
                    ],
                  ),

                  if (hike.description != null &&
                      hike.description!.trim().isNotEmpty) ...[
                    const SizedBox(height: 12),
                    Text(
                      hike.description!,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: TextStyle(
                        fontSize: 14,
                        height: 1.4,
                        color: isDark ? Colors.white.withOpacity(0.8) : const Color(0xFF4b5563),
                      ),
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _getDistanceFromLocation(String location) {
    // Simple mock - you can implement real distance calculation
    return '12 mi';
  }

  String? _formatDate(int? epochSeconds) {
    if (epochSeconds == null) return null;
    final dt = DateTime.fromMillisecondsSinceEpoch(epochSeconds * 1000).toLocal();
    final d = dt.day.toString().padLeft(2, "0");
    final m = dt.month.toString().padLeft(2, "0");
    final y = dt.year.toString();
    return "$d/$m/$y";
  }
}

class _HikeThumbnail extends StatelessWidget {
  const _HikeThumbnail({this.thumbnailUrl});

  final String? thumbnailUrl;

  @override
  Widget build(BuildContext context) {
    final borderRadius = const BorderRadius.vertical(top: Radius.circular(20));

    // Nếu có thumbnail path (từ observation_media)
    if (thumbnailUrl != null && thumbnailUrl!.isNotEmpty) {
      // Load from local file
      return ClipRRect(
        borderRadius: borderRadius,
        child: Image.file(
          File(thumbnailUrl!),
          width: double.infinity,
          height: 240,
          fit: BoxFit.cover,
          errorBuilder: (context, error, stackTrace) {
            // Nếu lỗi khi load file, hiển thị default image
            return _DefaultImage(borderRadius: borderRadius);
          },
        ),
      );
    }

    // Không có thumbnail, hiển thị default image
    return _DefaultImage(borderRadius: borderRadius);
  }
}

class _DefaultImage extends StatelessWidget {
  const _DefaultImage({required this.borderRadius});

  final BorderRadius borderRadius;

  @override
  Widget build(BuildContext context) {
    return ClipRRect(
      borderRadius: borderRadius,
      child: Image.asset(
        'assets/images/image_holder.png',
        width: double.infinity,
        height: 240,
        fit: BoxFit.cover,
        errorBuilder: (context, error, stackTrace) {
          // Nếu không tìm thấy asset, hiển thị placeholder với icon
          return _PlaceholderThumbnail(
            isDark: Theme.of(context).brightness == Brightness.dark,
            borderRadius: borderRadius,
          );
        },
      ),
    );
  }
}

class _PlaceholderThumbnail extends StatelessWidget {
  const _PlaceholderThumbnail({
    required this.isDark,
    required this.borderRadius,
  });

  final bool isDark;
  final BorderRadius borderRadius;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      height: 240,
      decoration: BoxDecoration(
        borderRadius: borderRadius,
        color: isDark
            ? const Color(0xFF1f2933)
            : const Color(0xFFE2E8F0),
      ),
      child: Icon(
        Icons.terrain,
        size: 64,
        color: isDark
            ? const Color(0xFF9ca3af)
            : const Color(0xFF13EC37).withOpacity(0.9),
      ),
    );
  }
}

class _DifficultyChip extends StatelessWidget {
  const _DifficultyChip({required this.label});

  final String label;

  Color _difficultyColor(String value) {
    switch (value.toLowerCase()) {
      case "easy":
        return const Color(0xFF10b981); // Green
      case "moderate":
        return const Color(0xFFf59e0b); // Amber/Yellow
      case "hard":
        return const Color(0xFFef4444); // Red
      case "expert":
        return const Color(0xFF991b1b); // Dark Red
      default:
        return const Color(0xFF6b7280); // Gray
    }
  }

  @override
  Widget build(BuildContext context) {
    final color = _difficultyColor(label);

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: color.withOpacity(0.2),
        borderRadius: BorderRadius.circular(999),
      ),
      child: Text(
        label,
        style: TextStyle(
          fontSize: 12,
          fontWeight: FontWeight.w600,
          color: color,
        ),
      ),
    );
  }
}

class _InfoChip extends StatelessWidget {
  const _InfoChip({
    required this.label,
  });

  final String label;

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: isDark ? Colors.white.withOpacity(0.15) : const Color(0xFF374151),
        borderRadius: BorderRadius.circular(999),
      ),
      child: Text(
        label,
        style: const TextStyle(
          fontSize: 12,
          color: Colors.white,
          fontWeight: FontWeight.w500,
        ),
      ),
    );
  }
}

