import 'package:flutter/material.dart';
import 'package:m_hike_cross_app/models/hike.dart';

class CreateHikeForm extends StatefulWidget {
  final VoidCallback onCancel;
  final void Function(Hike) onSaved;

  const CreateHikeForm({
    super.key,
    required this.onCancel,
    required this.onSaved,
  });

  @override
  State<CreateHikeForm> createState() => _CreateHikeFormState();
}

class _CreateHikeFormState extends State<CreateHikeForm> {
  final TextEditingController _nameCtrl = TextEditingController();
  final TextEditingController _descCtrl = TextEditingController();
  final TextEditingController _distanceCtrl = TextEditingController();
  final TextEditingController _durationCtrl = TextEditingController();

  bool showNameError = false;

  DateTime? plannedDate = DateTime.now();

  String difficulty = "Moderate";
  String parkingStatus = "No";

  String? locationName = "Mount Evergreen";
  double? lat = 45.6789;
  double? lng = -123.4567;

  @override
  Widget build(BuildContext context) {
    final dark = Theme.of(context).brightness == Brightness.dark;

    return SafeArea(
      child: Column(
        children: [
          Expanded(
            child: ListView(
              padding: const EdgeInsets.all(16),
              children: [
                // Hike Name
                Text(
                  "Name *",
                  style: TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                    color: dark ? Colors.white70 : Colors.black87,
                  ),
                ),
                const SizedBox(height: 6),
                TextField(
                  controller: _nameCtrl,
                  decoration: InputDecoration(
                    hintText: "e.g., Sunrise Peak Trail",
                    errorText: showNameError ? "Hike name is required" : null,
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                ),

                const SizedBox(height: 20),

                // Planned Date
                Text(
                  "Planned date",
                  style: TextStyle(
                    fontWeight: FontWeight.w600,
                    color: dark ? Colors.white70 : Colors.black87,
                  ),
                ),
                const SizedBox(height: 6),
                GestureDetector(
                  onTap: pickDate,
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                        vertical: 14, horizontal: 14),
                    decoration: BoxDecoration(
                      border: Border.all(
                        color: dark ? Colors.grey.shade700 : Colors.grey.shade300,
                      ),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          plannedDate != null
                              ? "${plannedDate!.year}-${plannedDate!.month.toString().padLeft(2, '0')}-${plannedDate!.day.toString().padLeft(2, '0')}"
                              : "Select date",
                          style: TextStyle(
                            color: dark ? Colors.white : Colors.black87,
                          ),
                        ),
                        Icon(
                          Icons.calendar_today,
                          size: 20,
                          color: dark ? Colors.grey : Colors.grey.shade600,
                        )
                      ],
                    ),
                  ),
                ),

                const SizedBox(height: 20),

                // Description
                Text(
                  "Description",
                  style: TextStyle(
                    fontWeight: FontWeight.w600,
                    color: dark ? Colors.white70 : Colors.black87,
                  ),
                ),
                const SizedBox(height: 6),
                TextField(
                  controller: _descCtrl,
                  maxLines: 4,
                  decoration: InputDecoration(
                    hintText: "Add notes about the trail, gear, reminders...",
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                ),

                const SizedBox(height: 24),

                // Difficulty
                Text(
                  "Difficulty",
                  style: TextStyle(
                    fontWeight: FontWeight.w600,
                    color: dark ? Colors.white70 : Colors.black87,
                  ),
                ),
                const SizedBox(height: 12),

                Container(
                  padding: const EdgeInsets.all(6),
                  decoration: BoxDecoration(
                    color: dark
                        ? const Color(0xFF19331e)
                        : const Color(0xFFE2E8F0),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Wrap(
                    spacing: 6,
                    runSpacing: 6,
                    children: [
                      difficultyButton("Easy"),
                      difficultyButton("Moderate"),
                      difficultyButton("Hard"),
                      difficultyButton("Expert"),
                      difficultyButton("Not set"),
                    ],
                  ),
                ),

                const SizedBox(height: 24),

                // Distance + Duration
                Row(
                  children: [
                    Expanded(
                      child: columnField(
                        label: "Est. distance",
                        controller: _distanceCtrl,
                        suffix: "km",
                        dark: dark,
                      ),
                    ),
                    const SizedBox(width: 14),
                    Expanded(
                      child: columnField(
                        label: "Est. duration",
                        controller: _durationCtrl,
                        suffix: "hr",
                        dark: dark,
                      ),
                    ),
                  ],
                ),

                const SizedBox(height: 24),

                // Parking Status
                Text(
                  "Parking status",
                  style: TextStyle(
                    fontWeight: FontWeight.w600,
                    color: dark ? Colors.white70 : Colors.black87,
                  ),
                ),
                const SizedBox(height: 12),

                Container(
                  padding: const EdgeInsets.all(6),
                  decoration: BoxDecoration(
                    color: dark ? const Color(0xFF19331e) : Colors.grey.shade200,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Row(
                    children: [
                      parkingButton("Yes"),
                      parkingButton("No"),
                      parkingButton("Unknown"),
                    ],
                  ),
                ),

                const SizedBox(height: 24),

                // Location section
                Text(
                  "Location",
                  style: TextStyle(
                    fontWeight: FontWeight.w600,
                    color: dark ? Colors.white70 : Colors.black87,
                  ),
                ),
                const SizedBox(height: 12),

                locationButton(Icons.search, "Search location"),
                const SizedBox(height: 10),

                locationButton(Icons.pin_drop, "Drop a pin on map"),
                const SizedBox(height: 10),

                locationButton(Icons.edit_location_alt, "Enter coordinates"),
                const SizedBox(height: 14),

                // Selected Location Info
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                        color: dark
                            ? Colors.grey.shade700
                            : Colors.grey.shade300),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            locationName ?? "",
                            style: TextStyle(
                              fontWeight: FontWeight.w600,
                              color: dark ? Colors.white : Colors.black87,
                            ),
                          ),
                          const SizedBox(height: 3),
                          Text(
                            "${lat!.toStringAsFixed(4)}°, ${lng!.toStringAsFixed(4)}°",
                            style: TextStyle(
                              color: dark ? Colors.grey : Colors.grey.shade600,
                            ),
                          ),
                        ],
                      ),
                      Text(
                        "Change",
                        style: TextStyle(
                          color: const Color(0xFF13EC37),
                          fontWeight: FontWeight.w600,
                        ),
                      )
                    ],
                  ),
                ),

                const SizedBox(height: 40),
              ],
            ),
          ),

          // Bottom buttons
          Container(
            padding: const EdgeInsets.fromLTRB(16, 12, 16, 18),
            decoration: BoxDecoration(
              border: Border(
                top: BorderSide(
                  color: dark ? Colors.grey.shade800 : Colors.grey.shade200,
                ),
              ),
              color: dark
                  ? const Color(0xFF102213)
                  : Colors.white.withOpacity(0.9),
            ),
            child: Row(
              children: [
                Expanded(
                  child: bottomButton(
                    label: "Cancel",
                    color: dark
                        ? const Color(0xFF19331e)
                        : Colors.grey.shade300,
                    textColor: dark ? Colors.white : Colors.black87,
                    onTap: widget.onCancel,
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: bottomButton(
                    label: "Save",
                    color: const Color(0xFF13EC37),
                    textColor: Colors.black,
                    onTap: submitForm,
                  ),
                ),
              ],
            ),
          )
        ],
      ),
    );
  }

  // Difficulty button
  Widget difficultyButton(String value) {
    final active = difficulty == value;
    final dark = Theme.of(context).brightness == Brightness.dark;

    return GestureDetector(
      onTap: () => setState(() => difficulty = value),
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 14),
        decoration: BoxDecoration(
          color: active
              ? (dark ? Colors.grey.shade700 : Colors.white)
              : Colors.transparent,
          borderRadius: BorderRadius.circular(10),
        ),
        child: Text(
          value,
          style: TextStyle(
            fontWeight: FontWeight.w600,
            color: active
                ? (dark ? Colors.white : Colors.black)
                : (dark ? Colors.grey.shade300 : Colors.grey.shade800),
          ),
        ),
      ),
    );
  }

  // Parking button
  Widget parkingButton(String value) {
    final active = parkingStatus == value;
    final dark = Theme.of(context).brightness == Brightness.dark;

    return Expanded(
      child: GestureDetector(
        onTap: () => setState(() => parkingStatus = value),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 12),
          margin: const EdgeInsets.symmetric(horizontal: 4),
          alignment: Alignment.center,
          decoration: BoxDecoration(
            color: active
                ? (dark ? Colors.white10 : Colors.white)
                : Colors.transparent,
            borderRadius: BorderRadius.circular(10),
          ),
          child: Text(
            value,
            style: TextStyle(
              color: active
                  ? (dark ? Colors.white : Colors.black)
                  : (dark ? Colors.grey.shade300 : Colors.grey.shade800),
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ),
    );
  }

  Widget columnField({
    required String label,
    required TextEditingController controller,
    required String suffix,
    required bool dark,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label,
            style: TextStyle(
              fontWeight: FontWeight.w600,
              color: dark ? Colors.white70 : Colors.black87,
            )),
        const SizedBox(height: 6),

        // Input with suffix
        Container(
          height: 48,
          padding: const EdgeInsets.only(right: 10),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(12),
            border:
            Border.all(color: dark ? Colors.grey.shade700 : Colors.grey.shade300),
          ),
          child: Row(
            children: [
              Expanded(
                child: TextField(
                  controller: controller,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(
                    border: InputBorder.none,
                    contentPadding:
                    EdgeInsets.symmetric(horizontal: 12, vertical: 12),
                  ),
                ),
              ),
              Text(
                suffix,
                style: TextStyle(
                  color: dark ? Colors.grey.shade300 : Colors.grey.shade600,
                ),
              )
            ],
          ),
        ),
      ],
    );
  }

  Widget locationButton(IconData icon, String label) {
    final dark = Theme.of(context).brightness == Brightness.dark;

    return Container(
      padding: const EdgeInsets.symmetric(vertical: 14),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(12),
        border:
        Border.all(color: dark ? Colors.grey.shade700 : Colors.grey.shade400),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(icon, size: 20, color: dark ? Colors.grey : Colors.grey.shade700),
          const SizedBox(width: 8),
          Text(
            label,
            style: TextStyle(
              fontWeight: FontWeight.w600,
              color: dark ? Colors.grey.shade300 : Colors.grey.shade700,
            ),
          ),
        ],
      ),
    );
  }

  Widget bottomButton({
    required String label,
    required Color color,
    required Color textColor,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        height: 50,
        decoration: BoxDecoration(
          color: color,
          borderRadius: BorderRadius.circular(40),
        ),
        alignment: Alignment.center,
        child: Text(
          label,
          style: TextStyle(
            color: textColor,
            fontWeight: FontWeight.w700,
          ),
        ),
      ),
    );
  }

  Future<void> pickDate() async {
    final d = await showDatePicker(
      context: context,
      firstDate: DateTime(2020),
      lastDate: DateTime(2030),
      initialDate: plannedDate ?? DateTime.now(),
    );

    if (d != null) {
      setState(() => plannedDate = d);
    }
  }

  void submitForm() {
    if (_nameCtrl.text.trim().isEmpty) {
      setState(() => showNameError = true);
      return;
    }

    // Build Hike object from form fields
    final name = _nameCtrl.text.trim();
    final desc = _descCtrl.text.trim().isEmpty ? null : _descCtrl.text.trim();
    final distance = double.tryParse(_distanceCtrl.text.trim());

    final plannedSec = plannedDate != null
        ? plannedDate!.millisecondsSinceEpoch ~/ 1000
        : DateTime.now().millisecondsSinceEpoch ~/ 1000;
    final plannedUtcSec = plannedDate != null
        ? plannedDate!.toUtc().millisecondsSinceEpoch ~/ 1000
        : DateTime.now().toUtc().millisecondsSinceEpoch ~/ 1000;
    final nowSec = DateTime.now().toUtc().millisecondsSinceEpoch ~/ 1000;

    final hike = Hike(
      name: name,
      description: desc,
      difficulty: difficulty == 'Not set' ? null : difficulty,
      plannedDate: plannedSec,
      dateUtc: plannedUtcSec,
      isCompleted: 0,
      isFavorite: 0,
      distanceKm: distance,
      parkingStatus: parkingStatus,
      locationName: locationName,
      latitude: lat,
      longitude: lng,
      createdAt: nowSec,
      updatedAt: null,
    );

    widget.onSaved(hike);
  }
}
