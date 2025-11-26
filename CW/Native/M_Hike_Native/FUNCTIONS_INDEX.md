# Project Function Index — M_Hike_Native

Phiên bản: tạo tự động
Ngày: 2025-11-23

Mục đích: tệp này liệt kê nhanh các hàm / phương thức chính trong mã nguồn `app/src/main/java` để bạn dễ duyệt. Mỗi entry gồm chữ ký phương thức (signature) và mô tả ngắn.

---

## Tóm tắt nhanh
- Các package chính: `com.example.m_hike_native` (Main), `com.example.m_hike_native.view` (Activities), `com.example.m_hike_native.view.adapter` (Adapters), `com.example.m_hike_native.data` (DB helper), `com.example.m_hike_native.model` (data models), `com.example.m_hike_native.util`.
- Thay đổi quan trọng gần đây: model `Hike` có 2 field mới (`elevation`, `durationMinutes`) và DB schema đã được nâng version (DB_VERSION = 3) để lưu 2 trường này (ALTER TABLE khi upgrade).

---

## File / phương thức

### `MainActivity.java`
- protected void onCreate(Bundle savedInstanceState)
  - Khởi tạo Activity chính, cấu hình edge-to-edge, setContentView, set text cho `topAppBar`, bắt listener cho các card (Add / List / Search / Export). Gọi `DatabaseExportUtils.exportDatabase` khi export.

### `util/DatabaseExportUtils.java`
- public static File exportDatabase(Context context, String dbName) throws IOException
  - Sao chép file DB nội bộ sang `externalFiles/exports` và trả về File xuất.

### `database/HikeDatabaseHelper.java`
- public HikeDatabaseHelper(Context context)
  - Constructor SQLiteOpenHelper.
- public void onCreate(SQLiteDatabase db)
  - Tạo bảng `hikes` (có `elevation`, `duration_minutes`) và `observations`.
- public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  - Khi `oldVersion < 3` thực hiện `ALTER TABLE` để thêm hai cột mới, giữ dữ liệu cũ.
- public boolean insertHike(Hike hike)
  - Thêm hike (ghi elevation và duration).
- public Cursor getAllHikes()
  - Lấy tất cả hikes.
- public Cursor getHikeById(int id)
  - Lấy một hike theo id.
- public int deleteHike(int id)
  - Xóa hike.
- public boolean insertObservation(Observation obs)
  - Thêm observation.
- public Cursor getObservationsForHike(int hikeId)
  - Lấy observation cho hike.
- public Cursor searchHikes(String query)
  - Tìm hikes theo tên/location.
- public int updateHike(Hike hike)
  - Cập nhật hike (ghi elevation/duration nếu có).

### `model/Hike.java`
- public Hike()
- public Hike(String name, String location, String date, double length, String difficulty, boolean parking, String description)
  - Constructor cũ (đặt elevation/duration mặc định là 0).
- public Hike(String name, String location, String date, double length, String difficulty, boolean parking, String description, double elevation, int durationMinutes)
  - Constructor mới có elevation/duration.
- Getter/Setter cho: id, name, location, date, difficulty, description, length, parking, elevation, durationMinutes
- public String toString()

### `model/Observation.java`
- public Observation()
- public Observation(int hikeId, String observation, String time, String comments)
- Getter/Setter cho id, hikeId, observation, time, comments

### `view/AddHikeActivity.java`
- protected void onCreate(Bundle savedInstanceState)
  - Setup UI cho màn thêm hike.
- private void validateInput()
  - Validate dữ liệu, tạo `Hike` mới (dùng constructor cũ), gọi `db.insertHike`.

### `view/EditHikeActivity.java`
- protected void onCreate(Bundle savedInstanceState)
  - Setup UI cho chỉnh sửa.
- private void loadHike()
  - Đọc dữ liệu từ DB (`getHikeById`) và populate form; lưu `loadedElevation`/`loadedDuration` để preserve khi update.
- private void doUpdate()
  - Validate input, tạo `Hike` object, set các thuộc tính, preserve elevation/duration khi UI chưa cho chỉnh sửa, gọi `db.updateHike`.

### `view/ListHikeActivity.java`
- protected void onCreate(Bundle savedInstanceState)
  - Setup RecyclerView và adapter.
- private void loadData()
  - Gọi `dbHelper.getAllHikes()`, parse cursor, tạo `Hike` objects (đọc `elevation` và `duration_minutes` nếu tồn tại), cập nhật adapter.
- protected void onResume()
- protected void onDestroy()

### `view/SearchActivity.java`
- protected void onCreate(Bundle savedInstanceState)
  - Setup SearchView, RecyclerView; gán listener cho SearchView.
- public boolean onQueryTextSubmit(String query) (inner)
- public boolean onQueryTextChange(String newText) (inner)
- private void doSearch(String q)
  - Gọi `db.searchHikes(q)`, parse cursor, tạo `Hike` objects (đọc elevation/duration nếu có), cập nhật adapter.

### `view/AddObservationActivity.java`
- protected void onCreate(Bundle savedInstanceState)
  - Setup UI thêm observation, RecyclerView cho danh sách observation.
- private void loadObservations()
  - Lấy danh sách observation từ DB cho hike hiện tại.
- private void saveObservation()
  - Validate và insert observation mới rồi reload list.

### `view/adapter/HikeAdapter.java`
- public HikeAdapter(ArrayList<Hike> list, Context ctx)
- public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
- public void onBindViewHolder(@NonNull VH holder, int position)
  - Bind views: name/location/date/length/difficulty/parking/elevation/duration; xử lý click Edit/Delete/OpenObservations.
- public int getItemCount()
- public void cleanup()
- static class VH extends RecyclerView.ViewHolder
  - Fields: tvName, tvLocation, tvDate, tvLength, tvElevation, tvDuration, tvDifficulty, tvParking, btnEdit, btnDelete

### `view/adapter/ObservationAdapter.java`
- public ObservationAdapter(ArrayList<Observation> list, Context ctx)
- public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
- public void onBindViewHolder(@NonNull VH holder, int position)
  - Bind observation text/time/comments
- public int getItemCount()
- static class VH extends RecyclerView.ViewHolder
  - Fields: tvObsText, tvObsTime, tvObsComments

---

## Ghi chú về DB & migration
- `HikeDatabaseHelper.DB_VERSION` = 3
- Thêm 2 cột: `elevation` (REAL) và `duration_minutes` (INTEGER).
- `onUpgrade` thêm cột mới khi nâng từ phiên bản cũ để tránh mất dữ liệu.

## Cách rebuild / kiểm tra (tùy chọn)
- Build debug (Windows CMD):

```bat
cd C:\Users\ADMIN\Desktop\COMP1786\CW\Native\M_Hike_Native
gradlew.bat clean assembleDebug
```

- Trong Android Studio: chạy `Build -> Rebuild Project` hoặc `Run` trên device.

## Cách tái sinh danh sách hàm (nếu muốn cập nhật index)
- Dùng Android Studio: `Find in Files` (pattern: regex tìm các khai báo method).
- Hoặc dùng `ripgrep`/`grep` (nếu có):

```bash
# trên Git Bash / Linux
rg "^\s*(public|protected|private)\s+[^
(]+\([^)]*\)" app/src/main/java -n
```

## Next steps gợi ý
- Muốn tôi thêm input UI cho `elevation` và `duration` trong `AddHikeActivity`/`EditHikeActivity` để người dùng có thể điền? (Tôi có thể thêm nhanh.)
- Muốn tôi export file này thành `FUNCTIONS_INDEX.md` (đã tạo ở root) hoặc di chuyển vào `DOCUMENTATION/`? (đã tạo tại gốc)
- Muốn tôi mở rộng chỉ mục để bao gồm các phương thức package-private (không có modifier)?


