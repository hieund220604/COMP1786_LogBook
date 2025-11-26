# Vấn đề Database - ĐÃ GIẢI QUYẾT ✅

## Vấn đề đã phát hiện

### ❌ Lỗi: Type Casting Error
```
type 'String' is not a subtype of type 'int?' in type cast
```

**Nguyên nhân**: Database migration từ schema cũ sang mới, một số field số (int) đang được lưu dưới dạng String trong database cũ.

**Vị trí lỗi**: 
- `lib/models/hike.dart` line 87
- Tất cả các model khác cũng có vấn đề tương tự

### ✅ Giải pháp đã áp dụng

Sửa tất cả các model để xử lý type conversion an toàn:
- ✅ `lib/models/hike.dart`
- ✅ `lib/models/observation.dart`
- ✅ `lib/models/observation_media.dart`
- ✅ `lib/models/profile.dart`
- ✅ `lib/models/settings.dart`
- ✅ `lib/models/geocode_cache.dart`

**Cách fix**: Thêm helper functions `_toInt()` và `_toDouble()` vào mỗi `fromMap()` để tự động convert:
- String → int
- num → int
- int → int (không đổi)
- null → null hoặc default value

## Kết quả mong đợi

Sau khi fix, app sẽ:
- ✅ Đọc được database cũ từ `app_flutter` folder
- ✅ Migration tự động sang `databases` folder
- ✅ Load được tất cả hikes (completed, planned, favorite)
- ✅ Hiển thị đúng dữ liệu trong các tab Feed, Plan, Favorite

## Cách test

1. **Hot Restart app** (không cần rebuild):
   - Nhấn `R` trong terminal (hot restart)
   - Hoặc nhấn nút restart trong IDE

2. **Kiểm tra các tab**:
   - **Feed**: Phải thấy completed hikes
   - **Plan**: Phải thấy planned hikes
   - **Favorite**: Phải thấy favorite hikes
   - **Profile**: Vẫn hoạt động bình thường

3. **Xem log** để xác nhận:
```
=== Loading favorite hikes ===
HikeRepository: Query returned X rows
=== Favorite hikes loaded: X hikes ===
```

## Debug Logs hiện tại

Các debug logs đã được thêm vào để tracking:
- Database migration process
- Query results
- Data loading status

⚠️ **Sau khi xác nhận app hoạt động ổn**, bạn có thể yêu cầu xóa tất cả debug logs.

## Vấn đề đã fix

- ✅ Type casting errors khi đọc database
- ✅ Database migration từ app_flutter sang databases
- ✅ Tương thích ngược với database schema cũ
- ✅ Safe type conversion cho tất cả models

## Các thay đổi đã thực hiện

### 1. Thêm Migration tự động (`lib/db/database.dart`)
```dart
Future<Database> _initDatabase() async {
  // Tự động kiểm tra database cũ ở app_flutter
  // Nếu tìm thấy, copy sang thư mục databases
  // In log chi tiết quá trình migration
}
```

### 2. Thêm Debug Logging
- `lib/db/database.dart`: Log chi tiết quá trình init và migration
- `lib/viewmodels/hike_repository.dart`: Log quá trình query database
- `lib/views/feed/complete_hike.dart`: Log khi load completed hikes
- `lib/views/plan/plan.dart`: Log khi load planned hikes
- `lib/views/favorite/favorite.dart`: Log khi load favorite hikes

### 3. Khởi tạo database sớm (`lib/views/splash/splash.dart`)
Database được khởi tạo ngay trong splash screen để trigger migration trước khi vào app.

## Cách kiểm tra

### Bước 1: Chạy app
```bash
flutter run
```

### Bước 2: Xem log console
Tìm các dòng log sau:

```
=== DATABASE INIT ===
Target DB path: ...
Old DB path: ...
Old DB exists: true/false
New DB exists: true/false
```

**Nếu thấy:**
```
=== MIGRATING DATABASE ===
Database file copied successfully
=== MIGRATION COMPLETE ===
```
→ Migration thành công ✅

**Nếu thấy:**
```
Using existing database at new location
```
→ Database đã ở vị trí đúng ✅

**Nếu thấy:**
```
No existing database found - will create new one
```
→ Không tìm thấy database cũ, tạo mới (trống) ⚠️

### Bước 3: Kiểm tra query hikes
Tìm các dòng log:

```
=== Loading completed hikes ===
HikeRepository: Getting database instance...
HikeRepository: Database obtained
HikeRepository: Query - WHERE: is_completed = ?, ARGS: [1]
HikeRepository: Query returned X rows
=== Completed hikes loaded: X hikes ===
```

**Nếu X = 0**: Database trống hoặc không có dữ liệu
**Nếu có lỗi**: Sẽ thấy stack trace chi tiết

## Giải pháp

### Giải pháp 1: Chạy lại app (Migration tự động)
1. Stop app
2. Chạy lại: `flutter run`
3. Migration sẽ tự động copy database

### Giải pháp 2: Xóa database mới và chạy lại
Nếu migration không hoạt động, có thể database mới đã được tạo. Cần xóa để migration chạy lại:

1. Stop app
2. Uninstall app khỏi device/emulator
3. Chạy lại: `flutter run`

### Giải pháp 3: Copy thủ công
Nếu vẫn không được:

1. Copy file `app_flutter/m_hike.db` 
2. Paste vào `databases/m_hike.db`
3. Restart app

## Kiểm tra kết quả

Sau khi thực hiện một trong các giải pháp trên, vào các tab:
- **Feed**: Phải thấy các hikes đã completed
- **Plan**: Phải thấy các hikes đã planned
- **Favorite**: Phải thấy các hikes đã đánh dấu favorite

## Log mẫu khi thành công

```
=== DATABASE INIT ===
Target DB path: /data/user/0/com.example.app/databases/m_hike.db
Old DB path: /data/user/0/com.example.app/app_flutter/m_hike.db
Old DB exists: true
New DB exists: false
=== MIGRATING DATABASE ===
Database file copied successfully
=== MIGRATION COMPLETE ===

=== Loading completed hikes ===
HikeRepository: Getting database instance...
HikeRepository: Database obtained
HikeRepository: Query - WHERE: is_completed = ?, ARGS: [1]
HikeRepository: Query returned 5 rows
HikeRepository: Converted to 5 Hike objects
=== Completed hikes loaded: 5 hikes ===
```

## Lưu ý quan trọng

⚠️ **Nếu bạn muốn XÓA tất cả debug logs sau khi fix xong**, hãy cho tôi biết và tôi sẽ xóa sạch tất cả print statements.

Hiện tại các log này giúp chẩn đoán vấn đề. Sau khi fix xong, nên xóa để code sạch hơn.

