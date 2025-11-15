# 🎨 M-Hike Visual Style Guide

## Color Palette Overview

### Primary Colors
```
Primary Blue:       #1E88E5  ████████████  Buttons, Icons, Primary Actions
Primary Dark:       #1565C0  ████████████  Gradients, Status Bar
Primary Light:      #42A5F5  ████████████  Highlights, Dark Mode

Secondary Teal:     #26A69A  ████████████  Accents, Secondary Actions  
Secondary Dark:     #00897B  ████████████  Gradients
Secondary Light:    #4DB6AC  ████████████  Highlights
```

### Accent Colors
```
Warning Orange:     #FFA726  ████████████  Difficulty, Warnings
Success Green:      #66BB6A  ████████████  Parking, Success
Danger Red:         #EF5350  ████████████  Delete, Errors
Purple:             #7E57C2  ████████████  Search, Special
```

### Background Colors
```
App Background:     #F5F7FA  ████████████  Main Background
Surface White:      #FFFFFF  ████████████  Cards
Primary Ultra:      #F1F8FE  ████████████  Light Blue Background
Secondary Ultra:    #F0F9F8  ████████████  Light Teal Background
```

---

## Gradient Combinations

### 1. Primary Gradient (Blue)
```
Start: #1E88E5 ████████
End:   #1565C0 ████████
Angle: 135°
Usage: Main toolbar, Hero card, Primary buttons
```

### 2. Secondary Gradient (Teal)
```
Start: #26A69A ████████
End:   #00897B ████████
Angle: 135°
Usage: List view toolbar, Secondary features
```

### 3. Purple Gradient
```
Start: #7E57C2 ████████
End:   #5E35B1 ████████
Angle: 135°
Usage: Search screen, Special features
```

### 4. Orange Gradient
```
Start: #FF9800 ████████
End:   #F57C00 ████████
Angle: 135°
Usage: Edit screen, Warning elements
```

---

## Typography Scale

### Headlines
```
HeadlineLarge    : 32sp, Bold, #1A1A1A
HeadlineMedium   : 28sp, Bold, #1A1A1A
HeadlineSmall    : 24sp, Bold, #1A1A1A
```

### Titles
```
TitleLarge       : 22sp, Bold, #1A1A1A
TitleMedium      : 18sp, Bold, #1A1A1A
TitleSmall       : 16sp, SemiBold, #1A1A1A
```

### Body
```
BodyLarge        : 16sp, Regular, #424242
BodyMedium       : 14sp, Regular, #616161
BodySmall        : 12sp, Regular, #757575
```

### Labels
```
LabelLarge       : 14sp, Medium, #616161
LabelMedium      : 12sp, Medium, #757575
LabelSmall       : 11sp, Medium, #9E9E9E
```

---

## Spacing System (8dp Grid)

```
XXS:  4dp   ▪
XS:   8dp   ▪▪
S:    12dp  ▪▪▪
M:    16dp  ▪▪▪▪
L:    20dp  ▪▪▪▪▪
XL:   24dp  ▪▪▪▪▪▪
XXL:  32dp  ▪▪▪▪▪▪▪▪
XXXL: 40dp  ▪▪▪▪▪▪▪▪▪▪
```

---

## Component Styles

### Cards
```
Corner Radius: 20dp
Elevation:     8dp
Padding:       20-24dp
Margin:        12dp
Background:    #FFFFFF
```

### Buttons
```
Height:        64dp
Corner Radius: 16dp
Elevation:     8dp
Padding H:     24dp
Text Size:     16sp, Bold
```

### Input Fields
```
Corner Radius: 12dp
Stroke Width:  2dp
Height:        56dp
Padding:       16dp
Icon Size:     24dp
```

### Chips/Badges
```
Corner Radius: 12dp
Padding:       12dp
Height:        40dp
Icon Size:     22dp
Text Size:     13sp, Bold
```

### Icon Circles
```
Size:          64-72dp
Corner Radius: 50% (circle)
Icon Size:     32-36dp
Background:    Ultra light colors
```

---

## Screen-Specific Colors

### Main Screen
```
Toolbar:     Gradient Primary (Blue)
Hero Card:   Gradient Primary (Blue)
Add Card:    Primary Light Background + Primary Icon
List Card:   Secondary Light Background + Secondary Icon
Search Card: Primary Light Background + Primary Icon
Export Card: Secondary Light Background + Secondary Icon
```

### Add Hike Screen
```
Toolbar:     Gradient Primary (Blue)
Header Card: Primary Light Background
Form Card:   White
Save Button: Gradient Primary (Blue)
```

### Edit Hike Screen
```
Toolbar:     Gradient Orange
Header Card: #FFF3E0 (Orange Light)
Form Card:   White
Update Btn:  Gradient Orange
```

### List Screen
```
Toolbar:     Gradient Secondary (Teal)
Item Header: Gradient Primary (Blue)
Date Chip:   Primary Ultra Light + Primary Text
Length Chip: Secondary Ultra Light + Secondary Text
Diff Chip:   #FFF3E0 (Orange Light) + Orange Text
Park Chip:   #E8F5E9 (Green Light) + Green Text
Edit Button: Primary Ultra Light + Primary Text
Del Button:  #FFEBEE (Red Light) + Red Text
```

### Search Screen
```
Toolbar:     Gradient Purple
Search Card: White + Purple Icon Background
Results:     Same as List items
```

### Observation Card
```
Header:      Gradient Secondary (Teal)
Details:     Secondary Ultra Light
Comments:    Primary Ultra Light
```

---

## Icon Color Coding

```
Mountain/Hike:    #1E88E5 (Primary Blue)
Location:         #26A69A (Secondary Teal)
Calendar/Date:    #1E88E5 (Primary Blue)
Distance:         #26A69A (Secondary Teal)
Difficulty:       #FFA726 (Orange)
Parking:          #66BB6A (Green)
Edit:             #1E88E5 (Primary Blue) or #FFA726 (Orange)
Delete:           #EF5350 (Red)
Search:           #7E57C2 (Purple)
Info:             #26A69A (Secondary Teal)
Add:              #1E88E5 (Primary Blue)
```

---

## Elevation Levels

```
Level 0:  0dp   (Flat)
Level 1:  2dp   (Subtle)
Level 2:  4dp   (Cards)
Level 3:  6dp   (Raised cards)
Level 4:  8dp   (Toolbars, Buttons)
Level 5:  12dp  (FAB)
Level 6:  16dp  (Dialogs)
```

---

## Accessibility

### Contrast Ratios (WCAG AA)
```
Primary Blue on White:      ✅ 4.5:1
Secondary Teal on White:    ✅ 4.5:1
Body Text on White:         ✅ 7:1
Gray 600 on White:          ✅ 4.6:1
```

### Touch Targets
```
Minimum Size:    48dp × 48dp
Recommended:     64dp × 64dp (buttons)
Icon Buttons:    48dp × 48dp
```

---

## Animation Timing

```
Fast:      150ms  (Ripples, Hovers)
Normal:    300ms  (Slides, Fades)
Medium:    400ms  (Cards appear)
Slow:      600ms  (Bounce effects)
```

---

## Best Practices

### DO ✅
- Use 8dp spacing grid
- Apply consistent corner radius
- Use semantic colors
- Maintain visual hierarchy
- Use Material icons
- Follow touch target sizes
- Provide visual feedback

### DON'T ❌
- Mix different corner radius
- Use random spacing
- Ignore color meaning
- Make buttons too small
- Use low contrast text
- Skip empty states
- Forget dark mode

---

## Quick Color Picker

**Copy-Paste Ready:**

```xml
<!-- Primary -->
<color name="md_theme_primary">#1E88E5</color>
<color name="md_theme_primaryDark">#1565C0</color>

<!-- Secondary -->
<color name="md_theme_secondary">#26A69A</color>
<color name="md_theme_secondaryDark">#00897B</color>

<!-- Accent -->
<color name="warning">#FFA726</color>
<color name="success">#66BB6A</color>
<color name="danger">#EF5350</color>

<!-- Background -->
<color name="md_theme_background">#F5F7FA</color>
<color name="primary_ultra_light">#F1F8FE</color>
<color name="secondary_ultra_light">#F0F9F8</color>
```

---

**Use this guide as your reference for consistent, beautiful UI design! 🎨**

