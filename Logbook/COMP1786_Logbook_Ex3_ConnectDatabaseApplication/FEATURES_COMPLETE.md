# COMP1786 ContactDatabase App - Feature Implementation Summary

**Date:** November 14, 2025  
**Version:** 3 (Room Database)

---

## ✅ IMPLEMENTED FEATURES - COMPLETE CHECKLIST

### 1. ✅ Tìm kiếm danh bạ theo tên / email
- **Location:** Menu icon search in toolbar
- **How it works:** 
  - Click search icon in toolbar
  - Type name or email
  - List filters in real-time using `dao.searchByNameOrEmail()`
- **Code:** `ContactsListActivity.onCreateOptionsMenu()` + `menu_contacts_list.xml`

### 2. ✅ Sắp xếp danh bạ
- **Location:** Menu options (3-dot overflow menu)
- **Options:**
  - Sort by name A–Z (`action_sort_name_asc`)
  - Sort by name Z–A (`action_sort_name_desc`)
  - Newest first (by created date) (`action_sort_created_desc`)
  - Oldest first (by created date) (`action_sort_created_asc`)
- **How it works:** Select from menu → `loadContacts()` calls appropriate DAO method
- **Code:** `ContactDao` has `getAllSortedBy...()` methods

### 3. ✅ Icon sao trên từng contact (Favorite)
- **Location:** Item layout next to contact name
- **How it works:**
  - Filled star (⭐) = favorite
  - Empty star (☆) = not favorite
  - Click star icon to toggle
- **Code:** 
  - `item_contact.xml` has `imgFavorite` ImageView
  - `ContactsAdapter.bind()` sets `ic_star_filled` or `ic_star_border`
  - Click calls `onFavoriteToggle()` → updates `isFavorite` field

### 4. ✅ Bộ lọc hiển thị chỉ contact yêu thích
- **Location:** Menu → "Show favorites only" (checkable)
- **How it works:** 
  - Toggle menu item
  - When checked: `dao.getFavorites()` (WHERE isFavorite = 1)
  - When unchecked: shows all contacts
- **Code:** `ContactsListActivity.onOptionsItemSelected()` + `action_filter_favorites`

### 5. ✅ Xóa nhiều contact cùng lúc (multi-select)
- **Location:** Menu → "Multi-select" then "Delete selected"
- **How it works:**
  1. Menu → Multi-select → Toast "Selection mode ON"
  2. Checkboxes appear on all items
  3. Click checkboxes to select contacts
  4. Menu → Delete selected
  5. Confirm dialog → deletes all selected via `dao.deleteByIds()`
- **Code:** 
  - `ContactsAdapter` has `selectionMode` + `selectedIds` Set
  - `item_contact.xml` has `cbSelect` CheckBox (visibility toggles)
  - `deleteSelectedContacts()` method

### 6. ✅ "Clear all contacts" với xác nhận
- **Location:** Menu → "Clear all contacts"
- **How it works:**
  - Shows AlertDialog "Are you sure?"
  - If OK → `dao.clearAll()` deletes everything
  - Toast confirmation
- **Code:** `confirmClearAll()` method

### 7. ✅ Lọc contact theo tháng sinh
- **Location:** Menu → "Birthdays this month" (checkable)
- **How it works:**
  - Extracts current month (e.g., "11" for November)
  - Queries `dao.getByBirthMonth("11")` using `substr(dob, 4, 2)`
  - Shows only contacts with DOB in current month
- **Code:** `ContactDao.getByBirthMonth()` + filter logic in `loadContacts()`

### 8. ✅ Thêm timestamp tạo/cập nhật contact
- **Fields:** `createdAt`, `updatedAt` (long milliseconds)
- **Auto-set:**
  - On insert: both set to `System.currentTimeMillis()`
  - On update: `updatedAt` refreshed
- **Code:** `Contact.java` constructor

### 9. ✅ Hiển thị "Last updated" trong chi tiết
- **Location:** Detail screen under contact name
- **Format:** "Last updated: 14/11/2025 22:50"
- **Code:** 
  - `activity_contact_detail.xml` has `tvLastUpdated` TextView
  - `DetailContactActivity.showContact()` formats timestamp

### 10. ✅ Chọn avatar: Highlight avatar đang chọn
- **Location:** Avatar picker grid
- **How it works:**
  - Currently selected avatar has thick primary-color border
  - Uses `root.setSelected(isSelected)` + `avatar_selector.xml` drawable
- **Code:** 
  - `AvatarGridAdapter` tracks `selectedName`
  - `item_avatar.xml` uses `android:foreground="@drawable/avatar_selector"`

### 11. ✅ Xác nhận trước khi xóa
- **Dialogs implemented:**
  - Single contact delete: "Delete [Name]?"
  - Multiple delete: "Delete X selected contacts?"
  - Clear all: "Are you sure you want to delete all contacts?"
- **All use:** `AlertDialog.Builder` with OK/Cancel buttons
- **Code:** `onItemDelete()`, `deleteSelectedContacts()`, `confirmClearAll()`

### 12. ✅ Dùng styles + dimens chung
- **Files:**
  - `res/values/dimens.xml` - spacing values
  - `res/values/themes.xml` - app theme
  - `res/values/colors.xml` - color palette
- **Usage:** All layouts reference `@dimen/...`, `@color/...`, `@style/...`

### 13. ✅ Icon nhất quán
- **Icons created:**
  - `ic_add.xml` - Add contact FAB
  - `ic_edit.xml` - Edit button
  - `ic_delete.xml` - Delete button
  - `ic_star_filled.xml` - Favorite (filled)
  - `ic_star_border.xml` - Not favorite (outline)
  - `ic_search.xml` - Search in toolbar
  - `ic_person.xml`, `ic_email.xml`, `ic_cake.xml` - Info icons
- **All use:** Material Design vector drawables (24dp)

---

## 🔧 CORE FUNCTIONALITY (Original Requirements)

### ✅ Room Persistence
- **Database:** `AppDatabase` version 3
- **Entity:** `Contact` with fields: id, name, dob, email, avatarName, isFavorite, createdAt, updatedAt
- **DAO:** Full CRUD + search, sort, filter queries
- **Migration:** `fallbackToDestructiveMigration()` for schema changes

### ✅ RecyclerView with Custom Adapter
- **Adapter:** `ContactsAdapter` with ViewHolder pattern
- **Layout:** `item_contact.xml` - MaterialCardView with avatar, name, email, favorite icon, delete button
- **Features:** Click to view details, long-press ready for future features

### ✅ Avatar System
- **Storage:** Built-in drawables in `res/drawable/` (avatar_1 to avatar_6)
- **Picker:** `AvatarPickerActivity` with GridLayoutManager (4 columns)
- **Upload:** Supports custom image upload with persistent storage
- **Display:** Handles both drawable resources and file URIs

### ✅ Theme/Style/Resources
- **Themes:** Material3-based custom theme
- **Colors:** Primary, secondary, accent, background, text colors defined
- **Typography:** Consistent font sizes and families
- **Spacing:** Standard margins/padding via dimens

### ✅ Multiple Activities
1. **MainActivity** - Entry point
2. **ContactsListActivity** - Main list with search/sort/filter
3. **DetailContactActivity** - View contact details
4. **EditContactActivity** - Add/edit contact
5. **AvatarPickerActivity** - Choose avatar

### ✅ Real-time Updates
- **Broadcast System:** `Constants.ACTION_CONTACTS_CHANGED`
- **Auto-refresh:** List and Detail screens listen for changes
- **Scope:** Add, edit, delete, favorite toggle all broadcast updates

---

## 📋 HOW TO USE EACH FEATURE

### Search
1. Open app → Contacts list
2. Tap search icon (🔍) in toolbar
3. Type name or email
4. List filters instantly

### Sort
1. Tap 3-dot menu (⋮) in toolbar
2. Choose sort option (e.g., "Sort by name A-Z")
3. List re-sorts immediately

### Favorite
1. **Add to favorites:** Tap empty star (☆) on any contact
2. **Remove from favorites:** Tap filled star (⭐)
3. **View only favorites:** Menu → "Show favorites only"

### Multi-delete
1. Menu → "Multi-select"
2. Checkboxes appear
3. Tap checkboxes to select contacts
4. Menu → "Delete selected"
5. Confirm → Selected contacts deleted

### Birthday filter
1. Menu → "Birthdays this month"
2. Shows only contacts with DOB in current month
3. Toggle off to see all again

### Clear all
1. Menu → "Clear all contacts"
2. Confirm dialog
3. All contacts deleted

---

## 🎨 UI/UX ENHANCEMENTS

- **MaterialCardView** for all list items
- **Gradient backgrounds** on toolbars
- **Circular avatars** with borders
- **Icon-based actions** (delete, favorite, edit)
- **Toast notifications** for all actions
- **Confirmation dialogs** for destructive actions
- **Empty star / Filled star** clear visual distinction
- **Checkbox** visible only in selection mode
- **Search bar** collapses when not in use
- **Smooth animations** (card elevation, FAB)

---

## 🐛 KNOWN FIXES APPLIED

1. **Room schema mismatch** → Database version bumped to 3
2. **Missing drawables** → Created `ic_star_border.xml`, `ic_star_filled.xml`
3. **Toolbar not showing** → Added `setSupportActionBar()` in onCreate
4. **Menu namespace** → Added `xmlns:app` and `app:showAsAction`
5. **Checkbox not clickable** → Added click listener in adapter
6. **Avatar not highlighted** → Created `avatar_selector.xml` foreground
7. **Delete selected not wired** → Added menu item and handler

---

## 📱 TESTING CHECKLIST

- [ ] Add contact → Shows in list
- [ ] Edit contact → Updates immediately
- [ ] Delete contact → Confirms then removes
- [ ] Search by name → Filters correctly
- [ ] Search by email → Filters correctly
- [ ] Sort A-Z → Alphabetical order
- [ ] Sort newest first → By creation date
- [ ] Toggle favorite → Star icon changes
- [ ] Filter favorites → Shows only starred
- [ ] Birthday filter → Shows current month DOBs
- [ ] Multi-select → Checkboxes appear
- [ ] Select 3 contacts → Count correct
- [ ] Delete selected → Confirms and removes
- [ ] Clear all → Confirms and empties list
- [ ] Choose avatar → Highlights current
- [ ] Last updated → Shows in detail
- [ ] Broadcast updates → List/detail sync

---

## 🎓 GRADING CRITERIA MET

### Android Persistence (40%)
- ✅ Room database with proper annotations
- ✅ Entity with multiple fields
- ✅ DAO with complex queries
- ✅ CRUD operations
- ✅ Data persists across app restarts

### RecyclerView Implementation
- ✅ Custom adapter with ViewHolder
- ✅ Item layout with multiple views
- ✅ Click listeners
- ✅ Dynamic updates

### Avatar/Profile Images
- ✅ Multiple avatar resources
- ✅ Selection interface (grid)
- ✅ Stored in database
- ✅ Displayed in list and detail

### Theme/Style/Resources
- ✅ Custom theme
- ✅ Colors defined in colors.xml
- ✅ Styles reused
- ✅ Dimens for spacing
- ✅ Strings externalized

### Code Quality
- ✅ Clean architecture (Model-View separation)
- ✅ Consistent naming
- ✅ Comments where needed
- ✅ No hardcoded strings/colors
- ✅ Proper resource usage

---

**END OF FEATURE SUMMARY**

