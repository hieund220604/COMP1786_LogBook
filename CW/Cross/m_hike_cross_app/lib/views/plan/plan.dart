import 'package:flutter/material.dart';
import 'package:m_hike_cross_app/views/plan/empty_plan.dart';
import 'package:m_hike_cross_app/views/search/search_screen.dart';
import 'package:m_hike_cross_app/viewmodels/hike_repository.dart';
import 'package:m_hike_cross_app/viewmodels/observation_repository.dart';
import 'package:m_hike_cross_app/models/hike.dart';
import 'package:m_hike_cross_app/widgets/favorite_snackbar.dart';
import 'package:m_hike_cross_app/views/hikes/hike_form.dart';

class PlanScreen extends StatefulWidget {
  const PlanScreen({super.key});

  @override
  State<PlanScreen> createState() => _PlanScreenState();
}

class _PlanScreenState extends State<PlanScreen> {
  final HikeRepository _hikeRepository = HikeRepository();
  final ObservationRepository _observationRepository = ObservationRepository();
  List<Hike>? _plannedHikes;
  Map<int, String?> _thumbnails = {};
  bool _isLoading = true;
  bool _isCreating = false; // new flag

  @override
  void initState() {
    super.initState();
    _loadPlannedHikes();
  }

  Future<void> _loadPlannedHikes() async {
    try {
      print('=== Loading planned hikes ===');
      final hikes = await _hikeRepository.listHikes(isCompleted: 0);
      print('=== Planned hikes loaded: ${hikes.length} hikes ===');

      // Load thumbnails for each hike
      final thumbnails = <int, String?>{};
      for (final hike in hikes) {
        final thumbnail = await _observationRepository.thumbnailForHike(hike.id!);
        thumbnails[hike.id!] = thumbnail;
      }

      setState(() {
        _plannedHikes = hikes;
        _thumbnails = thumbnails;
        _isLoading = false;
      });
    } catch (e, stackTrace) {
      print('=== ERROR loading planned hikes ===');
      print('Error: $e');
      print('Stack trace: $stackTrace');
      setState(() {
        _plannedHikes = [];
        _thumbnails = {};
        _isLoading = false;
      });
    }
  }

  Future<void> _markAsCompleted(Hike hike) async {
    try {
      await _hikeRepository.markHikeCompleted(hike.id!, completed: 1);

      if (mounted) {
        FavoriteSnackBar.showSuccess(
          context,
          message: 'Marked "${hike.name}" as completed',
        );
      }

      // Reload the list
      await _loadPlannedHikes();
    } catch (e) {
      if (mounted) {
        FavoriteSnackBar.showError(
          context,
          message: 'Failed to mark as completed',
        );
      }
    }
  }

  Future<void> _openCreateHike() async {
    // Open modal bottom sheet with CreateHikeForm
    final result = await showModalBottomSheet<Hike>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) {
        return FractionallySizedBox(
          heightFactor: 0.92,
          child: Container(
            decoration: BoxDecoration(
              color: Theme.of(context).brightness == Brightness.dark
                  ? const Color(0xFF102213)
                  : Colors.white,
              borderRadius: const BorderRadius.vertical(top: Radius.circular(20)),
            ),
            child: CreateHikeForm(
              onCancel: () => Navigator.of(context).pop(),
              onSaved: (hike) => Navigator.of(context).pop(hike),
            ),
          ),
        );
      },
    );

    if (result != null) {
      setState(() => _isCreating = true);
      try {
        final id = await _hikeRepository.createHike(result);
        // assign returned id to the object (optional, useful for immediate display)
        final created = result.copyWith(id: id);
        if (mounted) {
          FavoriteSnackBar.showSuccess(context, message: 'Hike "${created.name}" created');
        }
        await _loadPlannedHikes();
      } catch (e) {
        if (mounted) {
          FavoriteSnackBar.showError(context, message: 'Failed to create hike');
        }
      } finally {
        if (mounted) setState(() => _isCreating = false);
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

    // Show empty view if no planned hikes
    if (_plannedHikes == null || _plannedHikes!.isEmpty) {
      return Scaffold(
        backgroundColor: Theme.of(context).brightness == Brightness.dark
            ? const Color(0xFF102213)
            : const Color(0xFFF6F8F6),
        body: SafeArea(
          child: Stack(
            children: [
              Container(
                width: double.infinity,
                constraints: const BoxConstraints(maxWidth: 480),
                child: Column(
                  children: [
                    // Top App Bar
                    Container(
                      height: 64,
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: Row(
                        children: [
                          Text(
                            "Plan",
                            style: TextStyle(
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                              color: Theme.of(context).brightness == Brightness.dark
                                  ? Colors.white
                                  : const Color(0xFF0f172a),
                            ),
                          ),
                        ],
                      ),
                    ),
                    // Empty Plan View
                    Expanded(
                      child: EmptyPlanView(
                        onStartPlanning: () => _openCreateHike(),
                      ),
                    ),
                  ],
                ),
              ),
              if (_isCreating) ...[
                Positioned.fill(
                  child: ModalBarrier(
                    color: Colors.black.withOpacity(0.4),
                    dismissible: false,
                  ),
                ),
                const Center(child: CircularProgressIndicator()),
              ],
            ],
          ),
        ),
      );
    }

    // Show planned hikes list
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      backgroundColor: isDark ? const Color(0xFF102213) : const Color(0xFFF6F8F6),
      body: SafeArea(
        child: Stack(
          children: [
            Column(
              children: [
                // Header
                Padding(
                  padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
                  child: Align(
                    alignment: Alignment.centerLeft,
                    child: Text(
                      "Planned Hikes",
                      style: TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                        color: isDark ? Colors.white : const Color(0xFF0F172A),
                      ),
                    ),
                  ),
                ),

                const SizedBox(height: 8),

                Expanded(
                  child: RefreshIndicator(
                    onRefresh: _loadPlannedHikes,
                    child: ListView.builder(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      itemCount: _plannedHikes!.length,
                      itemBuilder: (context, index) {
                        final hike = _plannedHikes![index];
                        final hasLocation = hike.locationName != null &&
                            hike.locationName!.isNotEmpty;

                        return Padding(
                          padding: const EdgeInsets.only(bottom: 16),
                          child: _PlannedHikeCard(
                            hike: hike,
                            onTap: () {
                              // TODO: Navigate to hike detail
                            },
                            onCompleted: () => _markAsCompleted(hike),
                            isWarning: !hasLocation,
                          ),
                        );
                      },
                    ),
                  ),
                ),
                const SizedBox(height: 100), // Space for FAB
              ],
            ),

            // Floating Action Button
            Positioned(
              right: 16,
              bottom: 32,
              child: GestureDetector(
                onTap: _openCreateHike,
                child: Container(
                  padding: const EdgeInsets.fromLTRB(20, 12, 20, 12),
                  decoration: BoxDecoration(
                    color: const Color(0xFF13EC37),
                    borderRadius: BorderRadius.circular(999),
                    boxShadow: [
                      BoxShadow(
                        blurRadius: 12,
                        offset: const Offset(0, 4),
                        color: Colors.black.withOpacity(0.25),
                      ),
                    ],
                  ),
                  child: Row(
                    children: [
                      const Icon(
                        Icons.add,
                        size: 24,
                        color: Color(0xFF102213),
                      ),
                      const SizedBox(width: 8),
                      Text(
                        "New Hike",
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color: isDark ? Colors.black : const Color(0xFF102213),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),

            if (_isCreating) ...[
              Positioned.fill(
                child: ModalBarrier(
                  color: Colors.black.withOpacity(0.4),
                  dismissible: false,
                ),
              ),
              const Center(child: CircularProgressIndicator()),
            ],
          ],
        ),
      ),
    );
  }
}

class _PlannedHikeCard extends StatelessWidget {
  const _PlannedHikeCard({
    required this.hike,
    this.onTap,
    this.onCompleted,
    required this.isWarning,
  });

  final Hike hike;
  final VoidCallback? onTap;
  final VoidCallback? onCompleted;
  final bool isWarning;

  String _formatDate(int? epochSeconds) {
    if (epochSeconds == null) return 'No date';
    final dt = DateTime.fromMillisecondsSinceEpoch(epochSeconds * 1000).toLocal();
    final days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    final months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    final dayName = days[dt.weekday - 1];
    final monthName = months[dt.month - 1];

    return '$dayName, $monthName ${dt.day}';
  }

  String _calculateDuration(double? distanceKm) {
    if (distanceKm == null || distanceKm == 0) return 'N/A';
    // Rough estimate: 3km/hour hiking speed
    final hours = (distanceKm / 3).ceil();
    if (hours < 1) return '< 1h';
    return '${hours}h';
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Container(
      decoration: BoxDecoration(
        color: isDark ? const Color(0xFF19331E) : Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          if (!isDark)
            BoxShadow(
              blurRadius: 8,
              offset: const Offset(0, 2),
              color: Colors.black.withOpacity(0.08),
            ),
        ],
      ),
      child: Column(
        children: [
          // Main card
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Title + date
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            hike.name,
                            style: TextStyle(
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                              color: isDark ? Colors.white : const Color(0xFF0F172A),
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            _formatDate(hike.plannedDate),
                            style: TextStyle(
                              fontSize: 14,
                              color: isDark
                                  ? const Color(0xFF94A3B8)
                                  : const Color(0xFF64748B),
                            ),
                          ),
                        ],
                      ),
                    ),

                    GestureDetector(
                      onTap: onTap,
                      child: Container(
                        padding: const EdgeInsets.all(8),
                        decoration: BoxDecoration(
                          color: isDark
                              ? Colors.white.withOpacity(0.1)
                              : const Color(0xFFF1F5F9),
                          borderRadius: BorderRadius.circular(999),
                        ),
                        child: Icon(
                          Icons.chevron_right,
                          size: 22,
                          color: isDark
                              ? const Color(0xFFD1D5DB)
                              : const Color(0xFF475569),
                        ),
                      ),
                    ),
                  ],
                ),

                const SizedBox(height: 16),

                // Difficulty - Distance - Duration
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    _info(
                      icon: Icons.signal_cellular_alt,
                      label: hike.difficulty ?? 'N/A',
                      isDark: isDark,
                    ),
                    _info(
                      icon: Icons.straighten,
                      label: hike.distanceKm != null
                          ? '${hike.distanceKm!.toStringAsFixed(1)} km'
                          : 'N/A',
                      isDark: isDark,
                    ),
                    _info(
                      icon: Icons.schedule,
                      label: _calculateDuration(hike.distanceKm),
                      isDark: isDark,
                    ),
                  ],
                ),
              ],
            ),
          ),

          // Bottom row (Completed or Warning)
          if (!isWarning)
            GestureDetector(
              onTap: onCompleted,
              child: Container(
                padding: const EdgeInsets.all(14),
                width: double.infinity,
                decoration: BoxDecoration(
                  color: const Color(0xFF13EC37).withOpacity(0.2),
                  borderRadius: const BorderRadius.only(
                    bottomLeft: Radius.circular(16),
                    bottomRight: Radius.circular(16),
                  ),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.check_circle,
                      size: 20,
                      color: isDark
                          ? const Color(0xFF13EC37)
                          : const Color(0xFF234829),
                    ),
                    const SizedBox(width: 6),
                    Text(
                      "Mark as Completed",
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        color: isDark
                            ? const Color(0xFF13EC37)
                            : const Color(0xFF234829),
                      ),
                    )
                  ],
                ),
              ),
            )
          else
            Container(
              padding: const EdgeInsets.all(14),
              width: double.infinity,
              decoration: BoxDecoration(
                color: Colors.amber.withOpacity(0.2),
                borderRadius: const BorderRadius.only(
                  bottomLeft: Radius.circular(16),
                  bottomRight: Radius.circular(16),
                ),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.warning,
                    size: 20,
                    color: isDark ? Colors.amber : Colors.orange.shade700,
                  ),
                  const SizedBox(width: 6),
                  Text(
                    "No Location Set",
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      color: isDark ? Colors.amber : Colors.orange.shade700,
                    ),
                  )
                ],
              ),
            ),
        ],
      ),
    );
  }

  Widget _info({required IconData icon, required String label, required bool isDark}) {
    return Row(
      children: [
        Icon(
          icon,
          size: 18,
          color: isDark ? const Color(0xFF94A3B8) : const Color(0xFF64748B),
        ),
        const SizedBox(width: 4),
        Text(
          label,
          style: TextStyle(
            fontSize: 12,
            color: isDark ? const Color(0xFF94A3B8) : const Color(0xFF64748B),
          ),
        ),
      ],
    );
  }
}
