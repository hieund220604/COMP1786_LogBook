# M-Hike UI Quick Reference Guide

## 🎨 Color Usage

### Primary Colors
```xml
@color/md_theme_primary        <!-- #0A7BCD - Use for primary actions, headers -->
@color/md_theme_secondary      <!-- #00A896 - Use for secondary elements -->
@color/md_theme_tertiary       <!-- #FF6B6B - Use for delete/warnings -->
```

### Background Colors
```xml
@color/md_theme_background     <!-- #F7FAFF - Main background -->
@color/primary_light           <!-- #E3F2FD - Light blue backgrounds -->
@color/secondary_light         <!-- #E0F7F4 - Light teal backgrounds -->
```

### Text Colors
```xml
@color/md_theme_onSurface      <!-- #222222 - Primary text -->
@color/gray_600                <!-- #757575 - Secondary text -->
@color/gray_400                <!-- #BDBDBD - Disabled/hint text -->
```

## 🖼️ Icon Reference

### Main Features
- `@drawable/ic_hike` - Hiking person
- `@drawable/ic_mountain` - Mountain landscape
- `@drawable/ic_add` - Add/Create new
- `@drawable/ic_list` - List view
- `@drawable/ic_search` - Search
- `@drawable/ic_export` - Export database

### Hike Details
- `@drawable/ic_location` - Location/Place
- `@drawable/ic_calendar` - Date
- `@drawable/ic_distance` - Length/Distance
- `@drawable/ic_difficulty` - Difficulty level
- `@drawable/ic_parking` - Parking availability
- `@drawable/ic_time` - Time/Duration

### Actions
- `@drawable/ic_edit` - Edit action
- `@drawable/ic_delete` - Delete action
- `@drawable/ic_info` - Information
- `@drawable/ic_star` - Rating/Favorite
- `@drawable/ic_favorite` - Like/Love

## 📐 Layout Patterns

### Card with Gradient Header
```xml
<com.google.android.material.card.MaterialCardView
    style="?attr/materialCardViewElevatedStyle"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="16dp"
    app:cardElevation="4dp">
    
    <LinearLayout
        android:background="@drawable/gradient_primary"
        android:padding="16dp">
        <!-- Content here -->
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### Icon with Circular Background
```xml
<FrameLayout
    android:layout_width="80dp"
    android:layout_height="80dp"
    android:background="@drawable/bg_icon_circle">
    
    <ImageView
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_gravity="center"
        android:src="@drawable/ic_mountain"
        app:tint="@color/md_theme_primary" />
</FrameLayout>
```

### Info Chip/Badge
```xml
<LinearLayout
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:background="@drawable/bg_chip"
    android:padding="8dp"
    android:gravity="center_vertical">
    
    <ImageView
        android:layout_width="20dp"
        android:layout_height="20dp"
        android:src="@drawable/ic_calendar"
        app:tint="@color/md_theme_primary" />
    
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="6dp"
        android:text="2025-11-10"
        android:textColor="@color/md_theme_primary" />
</LinearLayout>
```

### Text Input with Icon
```xml
<com.google.android.material.textfield.TextInputLayout
    style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Location"
    app:startIconDrawable="@drawable/ic_location"
    app:startIconTint="@color/md_theme_primary"
    app:boxCornerRadiusTopStart="8dp"
    app:boxCornerRadiusTopEnd="8dp"
    app:boxCornerRadiusBottomStart="8dp"
    app:boxCornerRadiusBottomEnd="8dp">
    
    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/etLocation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</com.google.android.material.textfield.TextInputLayout>
```

### Material Button with Icon
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Edit"
    app:icon="@drawable/ic_edit"
    app:iconTint="@color/md_theme_primary"
    android:textColor="@color/md_theme_primary"
    style="@style/Widget.Material3.Button.TextButton.Icon" />
```

### Gradient App Bar
```xml
<com.google.android.material.appbar.AppBarLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/topAppBar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="@drawable/gradient_primary"
        app:titleTextColor="@color/white"
        app:title="M-Hike" />
</com.google.android.material.appbar.AppBarLayout>
```

## 🎭 Background Drawables

- `@drawable/gradient_primary` - Blue to teal gradient
- `@drawable/bg_chip` - Light blue rounded rectangle for chips
- `@drawable/bg_icon_circle` - Circular background with ripple
- `@drawable/bg_card_elevated` - White card with border
- `@drawable/bg_input` - Light gray input background
- `@drawable/bg_button_primary` - Rounded button background
- `@drawable/bg_spinner` - Custom spinner background

## 📏 Standard Dimensions

### Corner Radius
- Cards: `16dp`
- Buttons: `12dp`
- Input fields: `8dp`
- Chips: `20dp`

### Elevation
- Cards: `4dp` to `6dp`
- App bar: `8dp`
- Chips: `0dp` (flat)

### Padding
- Screen padding: `16dp`
- Card padding: `16dp`
- Item spacing: `8dp`
- Chip padding: `8dp`

### Icon Sizes
- Large icons (main cards): `48dp` or `80dp` container
- Medium icons (chips): `20dp` to `24dp`
- Small icons (inline): `16dp` to `18dp`

## 🎬 Animations

```xml
<!-- Apply to views -->
android:layoutAnimation="@anim/scale_in"
android:animation="@anim/fade_in"
```

Available animations:
- `@anim/scale_in` - Scale up with fade
- `@anim/slide_up` - Slide from bottom with fade
- `@anim/fade_in` - Simple fade in

## 💡 Best Practices

1. **Always use theme colors** instead of hardcoded colors
2. **Use vector drawables** for all icons (scalable, small size)
3. **Apply consistent corner radius** across similar elements
4. **Use elevation properly** - higher for more important elements
5. **Include content descriptions** for accessibility
6. **Use Material components** instead of basic Android widgets
7. **Apply proper text appearances** for consistency
8. **Use CoordinatorLayout** for scrolling behaviors
9. **Add ripple effects** with `?attr/selectableItemBackground`
10. **Test on different screen sizes** and orientations

## 🔧 Common Customizations

### Change Gradient Colors
Edit `res/drawable/gradient_primary.xml`:
```xml
<gradient
    android:startColor="#YourStartColor"
    android:endColor="#YourEndColor"
    android:angle="135" />
```

### Add New Icon
1. Create `res/drawable/ic_youricon.xml`
2. Use Material Icons or custom SVG
3. Keep viewportWidth/Height at 24
4. Use black fill (will be tinted)

### Create Custom Color
Add to `res/values/colors.xml`:
```xml
<color name="custom_color">#HexCode</color>
```

---

**Version**: 2.0  
**Last Updated**: November 11, 2025

