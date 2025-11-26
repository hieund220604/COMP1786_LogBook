import 'dart:io';
import 'package:flutter/material.dart';
import 'package:m_hike_cross_app/views/favorite/empty_favorite.dart';
import 'package:m_hike_cross_app/viewmodels/hike_repository.dart';
import 'package:m_hike_cross_app/viewmodels/observation_repository.dart';
import 'package:m_hike_cross_app/models/hike.dart';
import 'package:m_hike_cross_app/widgets/favorite_snackbar.dart';

class FavoriteScreen extends StatefulWidget {
  const FavoriteScreen({
    super.key,
    this.onBrowse,
  });

  final VoidCallback? onBrowse;

  @override
  State<FavoriteScreen> createState() => _FavoriteScreenState();
}

class _FavoriteScreenState extends State<FavoriteScreen> {
  final HikeRepository _hikeRepository = HikeRepository();
  final ObservationRepository _observationRepository = ObservationRepository();
  List<Hike>? _favoriteHikes;
  Map<int, String?> _thumbnails = {};
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadFavoriteHikes();
  }

  Future<void> _loadFavoriteHikes() async {
    try {
      print('=== Loading favorite hikes ===');
      final hikes = await _hikeRepository.listHikes(isFavorite: 1);
      print('=== Favorite hikes loaded: ${hikes.length} hikes ===');

      // Load thumbnails for each hike
      final thumbnails = <int, String?>{};
      for (final hike in hikes) {
        final thumbnail = await _observationRepository.thumbnailForHike(hike.id!);
        thumbnails[hike.id!] = thumbnail;
      }

      setState(() {
        _favoriteHikes = hikes;
        _thumbnails = thumbnails;
        _isLoading = false;
      });
    } catch (e, stackTrace) {
      print('=== ERROR loading favorite hikes ===');
      print('Error: $e');
      print('Stack trace: $stackTrace');
      setState(() {
        _favoriteHikes = [];
        _thumbnails = {};
        _isLoading = false;
      });
    }
  }

  Future<void> _toggleFavorite(Hike hike) async {
    try {
      // Remove from favorites
      await _hikeRepository.toggleFavorite(hike.id!, false);

      if (mounted) {
        FavoriteSnackBar.show(
          context,
          isFavorite: false,
          hikeName: hike.name,
        );
      }

      // Reload the list
      await _loadFavoriteHikes();
    } catch (e) {
      if (mounted) {
        FavoriteSnackBar.showError(
          context,
          message: 'Failed to update favorite',
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    // Show loading indicator
    if (_isLoading) {
      return const Scaffold(
        body: Center(
          child: CircularProgressIndicator(),
        ),
      );
    }

    // Show empty view if no favorite hikes
    if (_favoriteHikes == null || _favoriteHikes!.isEmpty) {
      return EmptyFavoriteView(
        onBrowse: widget.onBrowse,
      );
    }

    // Show favorite hikes with data
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      backgroundColor: isDark
          ? const Color(0xFF102213)
          : const Color(0xFFF6F8F6),
      body: SafeArea(
        child: Column(
          children: [
            // Header (không search, không filter)
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 16),
              child: Row(
                children: [
                  Text(
                    "Favorites",
                    style: TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                      color: isDark ? Colors.white : const Color(0xFF0F172A),
                    ),
                  ),
                ],
              ),
            ),

            // List
            Expanded(
              child: RefreshIndicator(
                onRefresh: _loadFavoriteHikes,
                child: ListView.builder(
                  padding: EdgeInsets.zero,
                  itemCount: _favoriteHikes!.length,
                  itemBuilder: (context, index) {
                    final hike = _favoriteHikes![index];
                    final thumbnail = _thumbnails[hike.id];

                    return Padding(
                      padding: EdgeInsets.fromLTRB(
                        16,
                        index == 0 ? 8 : 0,
                        16,
                        8,
                      ),
                      child: _favoriteCard(
                        hike: hike,
                        isDark: isDark,
                        imageUrl: thumbnail,
                        onTap: () {
                          // TODO: Navigate to hike detail
                        },
                        onToggleFavorite: () => _toggleFavorite(hike),
                      ),
                    );
                  },
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _favoriteCard({
    required Hike hike,
    required bool isDark,
    required String? imageUrl,
    required VoidCallback onTap,
    required VoidCallback onToggleFavorite,
  }) {
    // Determine tag based on completion status
    final tagIcon = hike.isCompleted == 1 ? Icons.check_circle : Icons.calendar_month;
    final tagText = hike.isCompleted == 1 ? "Completed" : "Planned";

    return GestureDetector(
      onTap: onTap,
      child: Container(
        decoration: BoxDecoration(
          color: isDark ? Colors.white.withOpacity(0.05) : Colors.white,
          borderRadius: BorderRadius.circular(16),
        ),
        child: Column(
          children: [
            // Top Image
            Container(
              height: 180,
              decoration: BoxDecoration(
                borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
                color: isDark ? const Color(0xFF1f2933) : const Color(0xFFE2E8F0),
              ),
              child: imageUrl != null && imageUrl.isNotEmpty
                  ? ClipRRect(
                      borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
                      child: Image.file(
                        File(imageUrl),
                        width: double.infinity,
                        height: 180,
                        fit: BoxFit.cover,
                        errorBuilder: (context, error, stackTrace) {
                          return _placeholderImage(isDark);
                        },
                      ),
                    )
                  : _placeholderImage(isDark),
            ),

            // Content
            Container(
              padding: const EdgeInsets.all(16),
              child: Stack(
                children: [
                  // Favorite icon
                  Positioned(
                    right: 0,
                    top: 0,
                    child: GestureDetector(
                      onTap: onToggleFavorite,
                      child: Container(
                        padding: const EdgeInsets.all(4),
                        child: Icon(
                          Icons.favorite,
                          size: 26,
                          color: Colors.redAccent,
                        ),
                      ),
                    ),
                  ),

                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Icon(
                            tagIcon,
                            size: 16,
                            color: isDark
                                ? const Color(0xFFCBD5E1)
                                : const Color(0xFF64748B),
                          ),
                          const SizedBox(width: 4),
                          Text(
                            tagText,
                            style: TextStyle(
                              fontSize: 14,
                              color: isDark
                                  ? const Color(0xFFCBD5E1)
                                  : const Color(0xFF64748B),
                            ),
                          )
                        ],
                      ),

                      const SizedBox(height: 8),

                      Text(
                        hike.name,
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                          color: isDark ? Colors.white : const Color(0xFF0F172A),
                        ),
                      ),

                      const SizedBox(height: 4),

                      if (hike.locationName != null && hike.locationName!.isNotEmpty)
                        Text(
                          hike.locationName!,
                          style: TextStyle(
                            fontSize: 16,
                            color: isDark ? const Color(0xFF94A3B8) : const Color(0xFF64748B),
                          ),
                        ),

                      const SizedBox(height: 4),

                      Text(
                        _buildMetaText(hike),
                        style: TextStyle(
                          fontSize: 16,
                          color: isDark ? const Color(0xFF94A3B8) : const Color(0xFF64748B),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _placeholderImage(bool isDark) {
    return ClipRRect(
      borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
      child: Image.asset(
        'assets/images/image_holder.png',
        width: double.infinity,
        height: 180,
        fit: BoxFit.cover,
        errorBuilder: (context, error, stackTrace) {
          // Fallback to icon if asset not found
          return Container(
            width: double.infinity,
            height: 180,
            decoration: BoxDecoration(
              borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
              color: isDark ? const Color(0xFF1f2933) : const Color(0xFFE2E8F0),
            ),
            child: Icon(
              Icons.terrain,
              size: 64,
              color: isDark
                  ? const Color(0xFF9ca3af)
                  : const Color(0xFF13EC37).withOpacity(0.5),
            ),
          );
        },
      ),
    );
  }

  String _buildMetaText(Hike hike) {
    final parts = <String>[];

    if (hike.distanceKm != null && hike.distanceKm! > 0) {
      parts.add('${hike.distanceKm!.toStringAsFixed(1)} km');
    }

    if (hike.elevationGainM != null && hike.elevationGainM! > 0) {
      parts.add('${hike.elevationGainM!.toStringAsFixed(0)}m');
    }

    if (hike.difficulty != null && hike.difficulty!.isNotEmpty) {
      parts.add(hike.difficulty!);
    }

    return parts.isEmpty ? 'No details' : parts.join(' | ');
  }
}

