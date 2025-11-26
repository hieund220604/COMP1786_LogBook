import 'package:flutter/material.dart';
import 'package:m_hike_cross_app/views/search/search_screen.dart';
import 'package:m_hike_cross_app/views/feed/empty_feed.dart';
import 'package:m_hike_cross_app/views/hikes/hike_item.dart';
import 'package:m_hike_cross_app/viewmodels/hike_repository.dart';
import 'package:m_hike_cross_app/viewmodels/observation_repository.dart';
import 'package:m_hike_cross_app/models/hike.dart';
import 'package:m_hike_cross_app/widgets/favorite_snackbar.dart';

class CompleteHikeFeedScreen extends StatefulWidget {
  const CompleteHikeFeedScreen({super.key});

  @override
  State<CompleteHikeFeedScreen> createState() => _CompleteHikeFeedScreenState();
}

class _CompleteHikeFeedScreenState extends State<CompleteHikeFeedScreen> {
  final HikeRepository _hikeRepository = HikeRepository();
  final ObservationRepository _observationRepository = ObservationRepository();
  List<Hike>? _completedHikes;
  Map<int, String?> _thumbnails = {};
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadCompletedHikes();
  }

  Future<void> _loadCompletedHikes() async {
    try {
      print('=== Loading completed hikes ===');
      final hikes = await _hikeRepository.listHikes(isCompleted: 1);
      print('=== Completed hikes loaded: ${hikes.length} hikes ===');

      // Load thumbnails for each hike
      final thumbnails = <int, String?>{};
      for (final hike in hikes) {
        final thumbnail = await _observationRepository.thumbnailForHike(hike.id!);
        thumbnails[hike.id!] = thumbnail;
      }

      setState(() {
        _completedHikes = hikes;
        _thumbnails = thumbnails;
        _isLoading = false;
      });
    } catch (e, stackTrace) {
      print('=== ERROR loading completed hikes ===');
      print('Error: $e');
      print('Stack trace: $stackTrace');
      setState(() {
        _completedHikes = [];
        _thumbnails = {};
        _isLoading = false;
      });
    }
  }

  Future<void> _toggleFavorite(Hike hike) async {
    try {
      final newValue = hike.isFavorite == 1 ? 0 : 1;
      await _hikeRepository.toggleFavorite(hike.id!, newValue == 1);

      // Update local state
      setState(() {
        final index = _completedHikes!.indexWhere((h) => h.id == hike.id);
        if (index != -1) {
          _completedHikes![index] = hike.copyWith(isFavorite: newValue);
        }
      });

      if (mounted) {
        FavoriteSnackBar.show(
          context,
          isFavorite: newValue == 1,
          hikeName: hike.name,
        );
      }
    } catch (e) {
      if (mounted) {
        FavoriteSnackBar.showError(
          context,
          message: 'Failed to update favorite',
        );
      }
    }
  }

  void _showHikeOptions(BuildContext context, Hike hike) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    showModalBottomSheet(
      context: context,
      backgroundColor: isDark ? const Color(0xFF1a2920) : Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (BuildContext context) {
        return SafeArea(
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 20),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                // Header
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 20),
                  child: Text(
                    hike.name,
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: isDark ? Colors.white : const Color(0xFF0f172a),
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
                const SizedBox(height: 20),
                const Divider(height: 1),

                // Edit option
                ListTile(
                  leading: Icon(
                    Icons.edit,
                    color: isDark ? Colors.white70 : const Color(0xFF475569),
                  ),
                  title: Text(
                    'Edit Hike',
                    style: TextStyle(
                      color: isDark ? Colors.white : const Color(0xFF0f172a),
                    ),
                  ),
                  onTap: () {
                    Navigator.pop(context);
                    _editHike(hike);
                  },
                ),

                // Delete option
                ListTile(
                  leading: const Icon(
                    Icons.delete,
                    color: Colors.red,
                  ),
                  title: const Text(
                    'Delete Hike',
                    style: TextStyle(
                      color: Colors.red,
                    ),
                  ),
                  onTap: () {
                    Navigator.pop(context);
                    _confirmDeleteHike(context, hike);
                  },
                ),

                const Divider(height: 1),

                // Cancel
                ListTile(
                  leading: Icon(
                    Icons.close,
                    color: isDark ? Colors.white70 : const Color(0xFF475569),
                  ),
                  title: Text(
                    'Cancel',
                    style: TextStyle(
                      color: isDark ? Colors.white : const Color(0xFF0f172a),
                    ),
                  ),
                  onTap: () => Navigator.pop(context),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  void _editHike(Hike hike) {
    // TODO: Navigate to edit screen
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('Edit hike: ${hike.name}'),
        backgroundColor: Colors.blue,
      ),
    );
  }

  void _confirmDeleteHike(BuildContext context, Hike hike) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          backgroundColor: isDark ? const Color(0xFF1a2920) : Colors.white,
          title: Text(
            'Delete Hike',
            style: TextStyle(
              color: isDark ? Colors.white : const Color(0xFF0f172a),
            ),
          ),
          content: Text(
            'Are you sure you want to delete "${hike.name}"? This action cannot be undone.',
            style: TextStyle(
              color: isDark ? const Color(0xFF94a3b8) : const Color(0xFF475569),
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: Text(
                'Cancel',
                style: TextStyle(
                  color: isDark ? Colors.white70 : const Color(0xFF475569),
                ),
              ),
            ),
            TextButton(
              onPressed: () {
                Navigator.pop(context);
                _deleteHike(hike);
              },
              child: const Text(
                'Delete',
                style: TextStyle(color: Colors.red),
              ),
            ),
          ],
        );
      },
    );
  }

  Future<void> _deleteHike(Hike hike) async {
    try {
      await _hikeRepository.deleteHike(hike.id!);

      // Show success message
      if (mounted) {
        FavoriteSnackBar.showSuccess(
          context,
          message: 'Deleted "${hike.name}"',
        );
      }

      // Reload the list
      await _loadCompletedHikes();
    } catch (e) {
      // Show error message
      if (mounted) {
        FavoriteSnackBar.showError(
          context,
          message: 'Failed to delete hike',
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

    // Show empty view if no completed hikes
    if (_completedHikes == null || _completedHikes!.isEmpty) {
      return EmptyFeedView(
        onNewHike: () {
          // TODO: Navigate to new hike screen
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Navigate to Plan New Hike')),
          );
        },
      );
    }

    // Show hike feed with data
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      backgroundColor: isDark
          ? const Color(0xFF102213)
          : const Color(0xFFF6F8F6),
      body: SafeArea(
        child: Container(
          width: double.infinity,
          constraints: const BoxConstraints(maxWidth: 480),
          child: Column(
            children: [
              // Top App Bar with Search
              Container(
                height: 64,
                padding: const EdgeInsets.symmetric(horizontal: 16),
                color: isDark
                    ? const Color(0xFF102213)
                    : const Color(0xFFF6F8F6),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    // Title - căn trái
                    Text(
                      "Hike Feed",
                      style: TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                        color: isDark ? Colors.white : const Color(0xFF0f172a),
                      ),
                    ),

                    // Search button
                    IconButton(
                      icon: Icon(
                        Icons.search,
                        color: isDark ? Colors.white : const Color(0xFF0f172a),
                      ),
                      onPressed: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => const SearchScreen(),
                          ),
                        );
                      },
                    ),
                  ],
                ),
              ),

              // Hike List
              Expanded(
                child: RefreshIndicator(
                  onRefresh: _loadCompletedHikes,
                  child: ListView.separated(
                    padding: const EdgeInsets.fromLTRB(16, 12, 16, 24),
                    itemCount: _completedHikes!.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 12),
                    itemBuilder: (context, index) {
                      final hike = _completedHikes![index];
                      final thumbnail = _thumbnails[hike.id];

                      return CompletedHikeCard(
                        hike: hike,
                        thumbnailUrl: thumbnail,
                        onTap: () => _showHikeOptions(context, hike),
                        onToggleFavorite: () => _toggleFavorite(hike),
                      );
                    },
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
