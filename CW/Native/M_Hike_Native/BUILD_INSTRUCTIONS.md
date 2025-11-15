# M-Hike Build Instructions

## 🔧 Build Fixes Applied

### Issues Fixed:
1. ✅ **ic_star.xml** - Fixed XML corruption (duplicate content removed)
2. ✅ **gradient_primary.xml** - Fixed gradient structure (wrapped in shape element)
3. ✅ All other drawable files verified and correct

### Files to Remove (Optional):
- `app/src/main/res/layout/activity_main_new.xml` - This is a temporary duplicate

## 📋 Build Steps

### Option 1: Clean Build (Recommended)
```cmd
cd C:\Users\ADMIN\Desktop\COMP1786\CW\Native\M_Hike_Native

REM Clean the build directory
gradlew.bat clean

REM Build the app
gradlew.bat assembleDebug
```

### Option 2: Force Clean and Rebuild
```cmd
cd C:\Users\ADMIN\Desktop\COMP1786\CW\Native\M_Hike_Native

REM Delete build folder manually
rmdir /s /q app\build
rmdir /s /q build

REM Rebuild
gradlew.bat clean assembleDebug
```

### Option 3: Build with Full Logs
```cmd
cd C:\Users\ADMIN\Desktop\COMP1786\CW\Native\M_Hike_Native
gradlew.bat clean assembleDebug --info
```

## 🎯 Expected Output

After successful build, you should find:
```
app/build/outputs/apk/debug/app-debug.apk
```

## 📱 Installation

### Using ADB:
```cmd
adb install app\build\outputs\apk\debug\app-debug.apk
```

### Manual Installation:
1. Copy `app-debug.apk` to your Android device
2. Enable "Install from Unknown Sources" in Settings
3. Tap the APK file to install

## 🐛 Troubleshooting

### If build still fails:

1. **Check Java Version:**
   ```cmd
   java -version
   ```
   Should be Java 11 or higher

2. **Invalidate Caches (if using Android Studio):**
   - File → Invalidate Caches → Invalidate and Restart

3. **Check Gradle Daemon:**
   ```cmd
   gradlew.bat --stop
   gradlew.bat clean assembleDebug
   ```

4. **Verify XML Files:**
   All drawable XML files should start with:
   - Vector files: `<vector xmlns:android="..."`
   - Shape files: `<shape xmlns:android="..."`
   - Selector files: `<selector xmlns:android="..."`

## ✅ Verification Checklist

Before building, verify:
- [ ] No duplicate content in XML files
- [ ] All `<vector>` tags are properly closed
- [ ] All `<shape>` tags are properly closed
- [ ] Gradient is wrapped in `<shape>` element
- [ ] No XML prolog errors
- [ ] Build directory can be cleaned

## 📝 Common Errors and Fixes

### Error: "Content is not allowed in prolog"
**Solution:** Check for duplicate content or BOM characters at start of XML file
**Status:** ✅ FIXED in ic_star.xml

### Error: "Failed to parse XML"
**Solution:** Ensure proper XML structure with namespace declaration
**Status:** ✅ FIXED in gradient_primary.xml

### Error: "Resource not found"
**Solution:** Clean build and rebuild
**Command:** `gradlew.bat clean assembleDebug`

## 🚀 Quick Build Command

For most cases, this should work:
```cmd
cd C:\Users\ADMIN\Desktop\COMP1786\CW\Native\M_Hike_Native && gradlew.bat clean assembleDebug
```

## 📊 Build Status

**Current Status:** ✅ Ready to Build

All XML issues have been resolved. The project should now build successfully.

---

**Last Updated:** November 11, 2025  
**Build System:** Gradle 8.x  
**Target SDK:** 36  
**Min SDK:** 31

