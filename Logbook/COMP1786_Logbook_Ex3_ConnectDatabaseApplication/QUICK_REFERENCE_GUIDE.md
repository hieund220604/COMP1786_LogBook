# Quick Reference Guide - Hướng Dẫn Nhanh

## 🚀 KHỞI ĐỘNG DỰ ÁN / PROJECT SETUP

### Build & Run
```bash
# Clean build
gradlew.bat clean

# Build debug APK
gradlew.bat assembleDebug

# Install on device
gradlew.bat installDebug

# Run all
gradlew.bat clean assembleDebug installDebug
```

### Project Structure
```
app/src/main/
├── java/.../
│   ├── MainActivity.java              # Launcher
│   ├── activity/
│   │   ├── ContactsListActivity.java  # Main screen
│   │   ├── EditContactActivity.java   # Add/Edit form
│   │   ├── DetailContactActivity.java # Detail view
│   │   ├── AvatarPickerActivity.java  # Avatar selector
│   │   ├── ContactsAdapter.java       # RecyclerView adapter
│   │   └── AvatarGridAdapter.java     # Avatar grid adapter
│   ├── model/
│   │   ├── Contact.java               # Entity
│   │   ├── ContactDao.java            # Database queries
│   │   └── AppDatabase.java           # Room database
│   └── helper/
│       ├── Constants.java             # Action constants
│       └── ResUtils.java              # Resource utilities
├── res/
│   ├── layout/                        # 7 XML layouts
│   ├── drawable/                      # Icons & avatars
│   ├── menu/                          # Menu items
│   ├── values/                        # Strings, colors, themes
│   └── xml/                           # Backup rules
└── AndroidManifest.xml
```

---

## 🔑 KEY FILES / TỆP QUAN TRỌNG

### 1. Contact Entity (Model)
**File**: `Contact.java`
```java
public class Contact {
    public long id;              // Auto-generated
    public String name;          // Required
    public String dob;           // Required (dd/MM/yyyy)
    public String email;         // Optional
    public String avatarName;    // Avatar identifier or URI
    public boolean isFavorite;   // Star toggle
    public long createdAt;       // Timestamp
    public long updatedAt;       // Timestamp
}
```

### 2. Database DAO
**File**: `ContactDao.java`
```java
// Main operations
@Insert(onConflict = OnConflictStrategy.REPLACE)
long upsert(Contact contact);

@Query("SELECT * FROM contacts ORDER BY name COLLATE NOCASE")
List<Contact> getAll();

@Query("SELECT * FROM contacts WHERE id = :id LIMIT 1")
Contact getById(long id);

@Delete
void delete(Contact contact);

// Sorting options
List<Contact> getAllSortedByNameAsc();
List<Contact> getAllSortedByNameDesc();
List<Contact> getAllSortedByCreatedAtDesc();
List<Contact> getAllSortedByCreatedAtAsc();

// Filtering
List<Contact> getFavorites();
List<Contact> getByBirthMonth(String monthStr);
List<Contact> searchByNameOrEmail(String pattern);

// Batch operations
void deleteByIds(List<Long> ids);
void clearAll();
```

### 3. BroadcastReceiver Setup
**File**: `ContactsListActivity.java`, `DetailContactActivity.java`
```java
// Define receiver
private final BroadcastReceiver contactsChangedReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.ACTION_CONTACTS_CHANGED.equals(intent.getAction())) {
            loadContacts(); // Refresh data
        }
    }
};

// Register (in onCreate)
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    registerReceiver(contactsChangedReceiver, 
        new IntentFilter(Constants.ACTION_CONTACTS_CHANGED), 
        Context.RECEIVER_NOT_EXPORTED);
} else {
    registerReceiver(contactsChangedReceiver, 
        new IntentFilter(Constants.ACTION_CONTACTS_CHANGED));
}

// Unregister (in onDestroy)
unregisterReceiver(contactsChangedReceiver);

// Send broadcast after changes
Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
sendBroadcast(intent);
```

---

## 💡 COMMON OPERATIONS / THAO TÁC THƯỜNG DÙNG

### Add Contact
```java
Contact contact = new Contact(name, dob, email, avatarName);
dao.upsert(contact);

// Broadcast update
Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
sendBroadcast(intent);
```

### Update Contact
```java
contact.name = newName;
contact.email = newEmail;
contact.updatedAt = System.currentTimeMillis();
dao.upsert(contact);

// Broadcast update
sendBroadcast(new Intent(Constants.ACTION_CONTACTS_CHANGED));
```

### Delete Contact
```java
dao.delete(contact);

// Broadcast update
sendBroadcast(new Intent(Constants.ACTION_CONTACTS_CHANGED));
```

### Toggle Favorite
```java
contact.isFavorite = !contact.isFavorite;
contact.updatedAt = System.currentTimeMillis();
dao.upsert(contact);

// Broadcast update
sendBroadcast(new Intent(Constants.ACTION_CONTACTS_CHANGED));
```

### Load Contacts with Filters
```java
// In ContactsListActivity
private void loadContacts() {
    Executors.newSingleThreadExecutor().execute(() -> {
        // 1. Get base list
        List<Contact> all = dao.getAllSortedByNameAsc();
        
        // 2. Apply search
        if (!searchQuery.isEmpty()) {
            all = filterBySearch(all, searchQuery);
        }
        
        // 3. Apply favorites filter
        if (showFavoritesOnly) {
            all = filterFavorites(all);
        }
        
        // 4. Apply birth month filter
        if (filterBirthMonth) {
            all = filterByBirthMonth(all);
        }
        
        // 5. Update UI
        runOnUiThread(() -> adapter.setItems(all));
    });
}
```

---

## 🎨 UI CUSTOMIZATION / TÙY CHỈNH GIAO DIỆN

### Colors
**File**: `res/values/colors.xml`
```xml
<color name="primary">#1976D2</color>        <!-- Blue -->
<color name="secondary">#FF6F00</color>      <!-- Orange -->
<color name="accent">#00BCD4</color>         <!-- Cyan -->
<color name="error">#F44336</color>          <!-- Red -->
<color name="success">#4CAF50</color>        <!-- Green -->
```

### Change Theme
**File**: `res/values/themes.xml`
```xml
<item name="colorPrimary">@color/primary</item>
<item name="colorSecondary">@color/secondary</item>
```

### Add New Avatar
1. Add drawable to `res/drawable/` (e.g., `avatar_7.xml`)
2. Update `res/values/arrays.xml`:
```xml
<array name="contact_avatars">
    <item>@drawable/avatar_1</item>
    <!-- ... existing ... -->
    <item>@drawable/avatar_7</item>
</array>
```

---

## 🐛 DEBUG TIPS / MẸO GỠ LỖI

### Enable Logging
```java
android.util.Log.d("TAG", "Message: " + value);
android.util.Log.e("TAG", "Error: " + e.getMessage(), e);
```

### Common Log Tags
- `ContactsAdapter` - Adapter operations
- `ContactsListActivity` - List screen operations
- `EditContact` - Edit form operations
- `DetailContact` - Detail screen operations
- `AvatarPicker` - Avatar selection & upload

### Check Database
```bash
# Connect to device
adb shell

# Open database
sqlite3 /data/data/com.example.comp1786_logbook_ex3_connectdatabaseapplication/databases/contacts.db

# View all contacts
SELECT * FROM contacts;

# View schema
.schema contacts
```

### Common Issues & Solutions

#### Issue: App crashes on start
```
Solution: Check API level compatibility
- Verify BroadcastReceiver registration code
- Check if all required permissions are in manifest
```

#### Issue: Images not loading
```
Solution: 
- Check file permissions
- Verify image file exists
- Check SecurityException in logs
- Ensure fallback to default avatar
```

#### Issue: Filters not working
```
Solution:
- Check filter logic order
- Verify date format (dd/MM/yyyy)
- Check if all filters combine properly
```

#### Issue: Real-time updates not working
```
Solution:
- Verify BroadcastReceiver is registered
- Check if sendBroadcast() is called
- Ensure receiver is unregistered in onDestroy()
```

---

## 📱 USER FEATURES / TÍNH NĂNG NGƯỜI DÙNG

### Main Screen (ContactsListActivity)
- **View all contacts** - Scroll through list
- **Search** - Tap search icon, type name/email
- **Add contact** - Tap FAB (+) button
- **View details** - Tap any contact card
- **Delete** - Tap red delete button on card
- **Toggle favorite** - Tap star icon
- **Sort** - Menu → Sort options
- **Filter** - Menu → Filter options
- **Multi-select** - Menu → Multi-select → Tap checkboxes
- **Delete selected** - Menu → Delete selected (in multi-select mode)
- **Clear all** - Menu → Clear all contacts

### Edit Screen (EditContactActivity)
- **Enter name** - Required field
- **Select date** - Tap DOB field → DatePicker
- **Enter email** - Optional, validates format
- **Choose avatar** - Tap "Choose Avatar" button
- **Save** - Tap Save button

### Detail Screen (DetailContactActivity)
- **View info** - See all contact details
- **Edit** - Tap Edit button

### Avatar Picker (AvatarPickerActivity)
- **Select built-in** - Tap any of 6 avatars
- **Upload custom** - Tap camera icon → Choose from photos

---

## 🔐 PERMISSIONS / QUYỀN TRUY CẬP

### Required Permissions
```xml
<!-- Android 13+ -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- Android 12 and below -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

### Runtime Permission Check
```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    // Android 13+
    if (checkSelfPermission(READ_MEDIA_IMAGES) != GRANTED) {
        requestPermissions(new String[]{READ_MEDIA_IMAGES}, REQUEST_CODE);
    }
} else {
    // Android 12 and below
    if (checkSelfPermission(READ_EXTERNAL_STORAGE) != GRANTED) {
        requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_CODE);
    }
}
```

---

## 📊 DATA FORMAT / ĐỊNH DẠNG DỮ LIỆU

### Date Format
- **Format**: dd/MM/yyyy
- **Example**: 01/05/2024
- **Note**: Always use leading zeros

### Avatar Naming
- **Built-in**: "avatar_1", "avatar_2", etc.
- **Uploaded**: "file:///data/.../avatars/avatar_1234567890.jpg"
- **Content URI**: "content://media/..."

### Email Validation
```java
if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
    // Invalid email
}
```

---

## ⚡ PERFORMANCE TIPS / TỐI ƯU HIỆU NĂNG

### Database
- Use transactions for batch operations
- Index frequently queried columns
- Use LIMIT for large result sets

### RecyclerView
- Use ViewHolder pattern (already implemented)
- Don't create new objects in onBindViewHolder
- Use DiffUtil for smart updates (future improvement)

### Images
- Resize large images before saving
- Use image caching library (Glide/Picasso)
- Load thumbnails for list, full size for detail

---

## 📞 SUPPORT / HỖ TRỢ

### Documentation Files
- `PROJECT_REVIEW_COMPLETE.md` - Full project review
- `BUG_FIX_REPORT.md` - All bugs fixed
- `TESTING_CHECKLIST.md` - Complete testing guide
- `QUICK_REFERENCE.md` - This file

### Key Classes to Understand
1. `ContactsListActivity` - Main screen logic
2. `ContactsAdapter` - RecyclerView binding
3. `Contact` - Data model
4. `ContactDao` - Database operations

---

**Last Updated**: 14/11/2025  
**Version**: 1.0 Stable  
**Status**: ✅ Production Ready

