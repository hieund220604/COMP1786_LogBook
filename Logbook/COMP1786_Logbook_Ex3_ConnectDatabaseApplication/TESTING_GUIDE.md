# 🚀 QUICK TEST GUIDE - ContactDatabase App

## Build & Run

```cmd
cd C:\Users\ADMIN\Desktop\COMP1786\Logbook\COMP1786_Logbook_Ex3_ConnectDatabaseApplication
gradlew.bat assembleDebug
```

Then run on emulator/device from Android Studio.

---

## ✅ Feature Testing Steps

### 1. SEARCH (Tìm kiếm)
1. Open app
2. Click 🔍 icon in top right
3. Type "John" → List shows matching names
4. Type "@gmail" → List shows matching emails
5. Clear search → All contacts return

**Expected:** Real-time filtering as you type

---

### 2. SORT (Sắp xếp)
1. Click ⋮ (3-dot menu) top right
2. Select "Sort by name A-Z" → List alphabetical
3. Select "Sort by name Z-A" → List reverse
4. Select "Newest first" → Sorted by creation date (newest on top)
5. Select "Oldest first" → Oldest contacts first

**Expected:** List re-orders instantly after each selection

---

### 3. FAVORITE STAR (Yêu thích)
**Toggle favorite:**
1. See star icon (☆ or ⭐) next to each contact name
2. Click empty star ☆ → Becomes filled ⭐
3. Click filled star ⭐ → Becomes empty ☆

**Filter favorites:**
1. Menu ⋮ → "Show favorites only" (check it)
2. List shows only starred contacts
3. Uncheck → All contacts return

**Expected:** 
- Star toggles instantly
- Favorite filter works correctly

---

### 4. MULTI-DELETE (Xóa nhiều)
1. Menu ⋮ → "Multi-select"
2. Toast appears: "Selection mode ON"
3. Checkboxes appear on all items
4. Click checkboxes to select 3 contacts
5. Menu ⋮ → "Delete selected"
6. Dialog: "Delete 3 selected contacts?"
7. Click OK → Contacts deleted
8. Toast: "Selected contacts deleted"

**Expected:**
- Checkboxes toggle selection
- Only selected contacts deleted
- Selection mode exits after delete

---

### 5. BIRTHDAY FILTER (Lọc sinh nhật tháng này)
**Setup:** Make sure you have 1-2 contacts with DOB in current month (November = 11)

1. Menu ⋮ → "Birthdays this month" (check it)
2. List shows only contacts with DOB like `*/11/*`
3. Uncheck → All contacts return

**Expected:** Filters correctly by current month

---

### 6. CLEAR ALL (Xóa tất cả)
1. Menu ⋮ → "Clear all contacts"
2. Dialog: "Are you sure you want to delete all contacts?"
3. Click OK → All deleted
4. List empty
5. Toast: "All contacts deleted"

**Expected:** Confirmation dialog, then complete clear

---

### 7. LAST UPDATED (Timestamp)
1. Open any contact (click on item)
2. Detail screen shows under name:
   - "Last updated: 14/11/2025 22:50" (example)
3. Go back, edit that contact
4. Save changes
5. Return to detail → timestamp updated

**Expected:** Shows formatted date/time

---

### 8. AVATAR HIGHLIGHT (Chọn avatar)
1. Add new contact or edit existing
2. Click "Choose Avatar" button
3. Avatar picker grid appears
4. Click any avatar → **Thick border** appears around it
5. Click another → Border moves to new selection
6. Click OK/Save → Avatar saved

**Expected:** 
- Current avatar has visible border
- Border follows selection
- Saved avatar shows in list/detail

---

### 9. DELETE CONFIRMATION (Xác nhận xóa)
**Single delete:**
1. Click delete button (🗑️) on any contact item
2. Dialog: "Delete [Name]?"
3. Click Cancel → Nothing happens
4. Click OK → Contact deleted

**Expected:** Always shows confirmation dialog

---

### 10. FAVORITE ICON WORKING
**Quick test:**
1. Add 2 contacts
2. Star the first one (click ☆ → ⭐)
3. Menu → "Show favorites only"
4. Only first contact visible
5. Click star to unfavorite
6. Contact disappears from filtered list
7. Turn off filter → Both contacts visible again

**Expected:** Favorite state persists and filters work

---

## 🐛 If Something Doesn't Work

### Menu not showing
- **Check:** Toolbar is set as action bar in `onCreate()`
- **Solution:** Already added `setSupportActionBar(toolbar)` ✅

### Search icon missing
- **Check:** `menu_contacts_list.xml` has `app:showAsAction`
- **Solution:** Already fixed with `xmlns:app` ✅

### Checkbox can't be clicked
- **Check:** Adapter binds click listener to checkbox
- **Solution:** Already added in `bind()` method ✅

### Star icon doesn't toggle
- **Check:** `onFavoriteToggle()` is called and broadcasts change
- **Solution:** Already wired in adapter ✅

### Avatar not highlighted
- **Check:** `item_avatar.xml` has `android:foreground="@drawable/avatar_selector"`
- **Solution:** Already added ✅

### Multi-delete option missing
- **Check:** Menu has `action_delete_selected` item
- **Solution:** Already added ✅

---

## 📊 Expected Results Summary

| Feature | What to See |
|---------|-------------|
| Search | List filters as you type |
| Sort | List reorders instantly |
| Favorite star | ☆ ↔ ⭐ on click |
| Favorite filter | Shows only starred |
| Multi-select | Checkboxes appear |
| Delete selected | Deletes checked items |
| Birthday filter | Shows current month DOBs |
| Clear all | Empties entire list |
| Last updated | Timestamp in detail |
| Avatar highlight | Border on selected |
| Delete confirm | Dialog before delete |

---

## 🎯 All Features Status

- ✅ Search
- ✅ Sort (4 modes)
- ✅ Favorite toggle
- ✅ Favorite filter
- ✅ Multi-select
- ✅ Delete selected
- ✅ Birthday filter
- ✅ Clear all
- ✅ Timestamps
- ✅ Last updated display
- ✅ Avatar highlight
- ✅ Delete confirmation

**100% Complete! 🎉**


