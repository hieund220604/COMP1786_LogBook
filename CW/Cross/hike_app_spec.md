# Đặc tả chức năng ứng dụng Hiking

## 1. Mục tiêu và phạm vi

- Ứng dụng mobile cá nhân giúp người dùng:
  - Lên kế hoạch các chuyến hike.
  - Ghi lại nhật ký chuyến đi bằng caption, ảnh, video.
  - Theo dõi thống kê hoạt động hiking theo thời gian.
- App cá nhân, offline first:
  - Không có mạng xã hội, không friend list, không feed công khai.
  - Dữ liệu chính lưu cục bộ trong SQLite.
  - Một user trên một thiết bị, toàn bộ Hike trên thiết bị thuộc user hiện tại.

## 2. Kiến trúc màn hình

**Tabs chính:**

- **Feed**: danh sách Hike đã hoàn thành.
- **Plan**: danh sách Hike chưa hoàn thành (planned, upcoming).
- **Favorite**: danh sách Hike đánh dấu yêu thích.
- **Search**: tìm kiếm, filter Hike.
- **Profile/Settings**: thông tin người dùng, cấu hình ứng dụng, Dashboard thống kê.

**Bên trong Profile/Settings có các nhóm:**

- **Profile**: thông tin cá nhân, avatar, bio.
- **App Settings**: ngôn ngữ, theme, nhạc nền, notification.
- **Dashboard**: thống kê, biểu đồ hoạt động hiking.
- **Authentication**: đăng nhập, đăng ký, đăng xuất (nếu có backend).

## 3. Data model tóm tắt

### 3.1 Bảng `hikes`

- `id`: integer, primary key.
- `name`: text, bắt buộc, tối đa 80 ký tự, không toàn khoảng trắng.
- `description`: text, optional, tối đa 500 ký tự.
- `difficulty`: text, enum:
  - `Easy`
  - `Moderate`
  - `Hard`
  - `Expert`  
  (cho phép null nếu chưa chọn).
- `planned_date`: datetime theo timezone thiết bị.
- `date_utc`: datetime hoặc epoch UTC, phục vụ sắp xếp, đồng bộ.
- `is_completed`: integer 0 hoặc 1, mặc định 0.
- `is_favorite`: integer 0 hoặc 1, mặc định 0.
- `distance_km`: real, km, ≥ 0, optional.
- `parking_status`: text, enum `Yes` / `No` / `Unknown`, mặc định `Unknown`.
- `location_name`: text, tên địa điểm hiển thị.
- `place_id`: text, place id của Google Places, optional.
- `latitude`: real, optional, −90 đến 90.
- `longitude`: real, optional, −180 đến 180.
- `elevation_gain_m`: real, optional.
- `created_at`: datetime, bắt buộc.
- `updated_at`: datetime, optional.

### 3.2 Bảng `observations`

- `id`: integer, primary key.
- `hike_id`: integer, bắt buộc, tham chiếu `hikes.id`.
- `caption`: text, optional, tối đa 140 ký tự.
- `latitude`: real, optional.
- `longitude`: real, optional.
- `created_at`: datetime, bắt buộc.
- `updated_at`: datetime, optional.

### 3.3 Bảng `observation_media`

- `id`: integer, primary key.
- `observation_id`: integer, bắt buộc, tham chiếu `observations.id`.
- `file_path`: text, đường dẫn file cục bộ.
- `media_type`: text, enum `photo` / `video`.
- `duration_seconds`: integer, optional (cho video).
- `width`, `height`: integer, optional.
- `created_at`: datetime, bắt buộc.

### 3.4 Bảng `profile`

- `id`: integer, primary key, 1 record.
- `display_name`: text, bắt buộc, tối đa 50 ký tự, không toàn khoảng trắng.
- `bio`: text, optional, tối đa 160 ký tự.
- `avatar_path`: text, optional.
- `language`: text, ví dụ `en` / `vi`.
- `theme`: text, enum `light` / `dark` / `black_white` / `system`.
- `music_enabled`: integer 0 hoặc 1, mặc định 0.
- `notifications_enabled`: integer 0 hoặc 1, mặc định 1.
- `daily_reminder_hour`: integer 0 đến 23, optional.

### 3.5 Bảng `settings`

- `id`: integer, primary key.
- `notification_morning_hour`: integer, ví dụ 7.
- `notification_evening_hour`: integer, ví dụ 20.
- `weather_enabled`: integer 0 hoặc 1.
- `elevation_enabled`: integer 0 hoặc 1.

### 3.6 Bảng `geocode_cache`

- `key`: text, primary key, format `"lat,lon"`.
- `value`: text, json.
- `expires_at`: datetime, hết hạn sau 7 ngày.

## 4. Quản lý Hike

### 4.1 Tạo Hike

- Thao tác từ tab Plan (nút New Hike).  
- Trường bắt buộc:
  - `name`: trim không rỗng, tối đa 80 ký tự.
  - `planned_date`: không nhỏ hơn ngày hiện tại theo timezone thiết bị.
- Vị trí:
  - Chọn từ Google Places:
    - Gõ tên địa điểm, chọn kết quả.
    - Lưu `place_id`, `latitude`, `longitude`, `location_name`.
  - Hoặc nhập tay:
    - Nhập `location_name`, `latitude`, `longitude`, validate phạm vi.
  - Tụ động tính khoảng cách nếu chọn từ map
- Trường optional:
  - `description`.
  - `difficulty`: `Easy` / `Moderate` / `Hard` / `Expert`.
  - `distance_km` ≥ 0.
  - `parking_status`: `Yes` / `No` / `Unknown` (mặc định `Unknown`).
- Giá trị mặc định:
  - `is_completed` = 0.
  - `is_favorite` = 0.
- Lưu trong transaction, insert vào `hikes`. Thành công thì Hike xuất hiện trong Plan.

### 4.2 Xem chi tiết Hike

- Mở từ card Hike ở Feed, Plan, Favorite, Search.  
- Hiển thị:
  - `name`, `description`, `planned_date` hoặc ngày completed.
  - Trạng thái: Planned hoặc Completed.
  - `difficulty` nếu có.
  - `distance_km` nếu có.
  - `parking_status`.
  - `location_name`, `latitude`, `longitude`.
  - Map với marker nếu có tọa độ.
  - Khoảng cách từ vị trí hiện tại nếu có quyền location:
    - < 1 km: hiển thị mét.
    - ≥ 1 km: hiển thị km 1 chữ số lẻ.
  - Gallery tổng hợp media từ mọi observation của Hike.
  - Danh sách observations (thời gian, caption, thumbnails).
  - Weather: nếu có tọa độ, gọi API, cache theo `(hike_id, ngày)`.
  - Elevation: nếu có tọa độ, gọi API elevation.
- Hành động trong Hike Detail:
  - Edit Hike.
  - Delete Hike.
  - Add Observation.
  - Toggle Favorite.
  - Export JSON.

### 4.3 Cập nhật Hike

- Không sửa:
  - `id`
  - `created_at`
- Sửa được:
  - `name`, `description`.
  - `planned_date`:
    - Nếu Hike còn Planned: `planned_date` mới không được lùi về trước ngày hiện tại.
  - `difficulty`.
  - `parking_status`.
  - `distance_km`.
  - `location_name`, `latitude`, `longitude`, `place_id`.
  - `is_completed`.
  - `is_favorite`.
- Đổi `is_completed` từ 0 sang 1:
  - Hike chuyển từ Plan sang Feed.
- Khi đổi vị trí:
  - Cập nhật `place_id`, `latitude`, `longitude`, `location_name`.
  - Xóa hoặc làm mới cache geocode và weather.
- Sau khi cập nhật:
  - Update `updated_at`.
  - Refresh UI và dữ liệu Dashboard.

### 4.4 Xóa Hike

- Dialog cảnh báo:
  - Xóa Hike sẽ xóa luôn tất cả Observation và media, không thể hoàn tác.
- Transaction:
  - Xóa `observation_media` thuộc các `observations` của Hike.
  - Xóa các `observations`.
  - Xóa Hike.
- Có thể xóa luôn file media vật lý.
- Cập nhật mọi danh sách và Dashboard.

### 4.5 Export Hike ra JSON

- Export một Hike cùng toàn bộ observations, media path.  
- JSON gồm:
  - Hike:
    - `id`, `name`, `description`, trạng thái, `difficulty`, `parking_status`, `planned_date`, `date_utc`.
    - `location`: `latitude`, `longitude`, `place_id`, `location_name`.
    - `distance_km`, `elevation_gain_m`.
  - Observations:
    - `id`, `caption`, `created_at`, `latitude`, `longitude`.
    - `media`:
      - `media_type`, `file_path`, `duration_seconds`, `width`, `height`.
- Tên file: `hike_<id>_<yyyyMMdd>.json`.
- Sau khi export: hiển thị thông báo thành công cho người dùng.

## 5. Observation và media

### 5.1 Tạo Observation

- Thao tác từ Hike Detail.  
- Flow:
  - Chọn hoặc chụp media:
    - Ảnh.
    - Video (tối đa 60 giây).
    - Chọn từ thư viện (multi select).
  - Màn compose:
    - Preview media.
    - Nhập `caption` optional (tối đa 140 ký tự).
    - Set `created_at` là thời điểm hiện tại.
    - Nếu có quyền location: tự lấy `latitude`, `longitude` hiện tại.
  - Lưu:
    - Insert vào `observations`.
    - Insert nhiều rows vào `observation_media`.

- Giới hạn media:
  - Ảnh:
    - JPG hoặc PNG.
    - Nén khoảng 85 phần trăm.
    - Cạnh dài tối đa 2048 px.
    - Dung lượng tối đa 10 MB.
  - Video:
    - MP4, H.264, AAC.
    - Thời lượng tối đa 60 giây.
    - Độ phân giải tối đa 1080p.
    - Dung lượng tối đa 200 MB.
  - Vượt giới hạn:
    - Thông báo, không lưu media đó.

- File media:
  - Lưu trong storage riêng của app.
  - DB chỉ lưu `file_path`.

### 5.2 Xem Observation

- Trong Hike Detail:
  - Danh sách `observations`, newest first.
  - Mỗi observation: thời gian, caption, thumbnails media.
- Xem media:
  - Tap ảnh: viewer fullscreen.
  - Tap video: video player fullscreen.

### 5.3 Cập nhật Observation

- Sửa được:
  - `caption`.
  - Thêm hoặc xóa media.
- Không sửa:
  - `hike_id`.
  - `created_at`.
  - `latitude`, `longitude` ban đầu nếu giữ log.
- Lưu:
  - Update `updated_at`.

### 5.4 Xóa Observation hoặc media

- Xóa Observation:
  - Dialog xác nhận.
  - Xóa `observation_media` và bản ghi `observations`.
  - Có thể xóa file vật lý.
- Xóa media đơn:
  - Xóa record trong `observation_media`.
  - Có thể xóa file vật lý.
- Hike vẫn tồn tại kể cả khi xóa observation cuối cùng.

## 6. Danh sách Hike: Feed, Plan, Favorite

### 6.1 Feed

- Dữ liệu:
  - WHERE `is_completed` = 1.
  - ORDER BY `date_utc` hoặc `created_at` DESC.
- Infinite scroll:
  - Mỗi page 10 đến 20 Hike.
- Card Hike:
  - Thumbnail: ảnh đầu tiên của observation mới nhất hoặc placeholder.
  - Nội dung:
    - `name`.
    - Ngày.
    - `difficulty`.
    - `distance_km` nếu có.
    - Khoảng cách nếu có tọa độ và quyền location.
    - Icon Favorite nếu `is_favorite` = 1.
  - Caption đại diện:
    - `caption` của observation mới nhất (cắt ngắn).

- Tương tác:
  - Tap card: mở Hike Detail.
  - Long press: menu nhanh (Favorite, Edit, Delete) tùy thiết kế.

### 6.2 Plan

- Dữ liệu:
  - WHERE `is_completed` = 0.
  - ORDER BY `planned_date` ASC hoặc `created_at` DESC.
- Card Hike:
  - `name`.
  - `planned_date`.
  - `difficulty` nếu có.
  - `distance_km` nếu có.
  - Trạng thái vị trí:
    - Nếu chưa có `latitude`, `longitude`: hiển thị nhắc "Chưa chọn vị trí".
- Hành động nhanh:
  - Mark Completed.
  - Mở Hike Detail.
  - Add Observation.

### 6.3 Favorite

- Dữ liệu:
  - WHERE `is_favorite` = 1.
- Bộ lọc trong Favorite:
  - All.
  - Only Completed.
  - Only Planned.
- Tương tác:
  - Toggle Favorite từ Hike Detail hoặc từ card.

## 7. Search và filter

### 7.1 Search

- Tìm theo:
  - `name`.
  - `location_name`.
  - `description`.
- Hành vi:
  - SQL LIKE hoặc FTS5.
  - Không phân biệt hoa thường.
  - Nên hỗ trợ bỏ dấu tiếng Việt nếu có thể.
- Nếu query trống:
  - Trả về danh sách theo sort mặc định.

### 7.2 Filters

- Thời gian:
  - Hôm nay.
  - 7 ngày qua.
  - Tháng này.
  - Khoảng tùy chọn.
  - Áp dụng cho `planned_date` hoặc ngày completed.
- Difficulty:
  - Multi select:
    - `Easy`
    - `Moderate`
    - `Hard`
    - `Expert`
- Parking:
  - `Yes`
  - `No`
  - `Unknown`
- Trạng thái:
  - All.
  - Completed.
  - Planned.
- Favorite:
  - All.
  - Only Favorite.

- Logic:
  - Kết hợp AND giữa các nhóm filter.
- Lưu trạng thái filter:
  - Lưu local để khi quay lại giữ cấu hình filter cũ.

### 7.3 Gợi ý và tối ưu

- Lịch sử query:
  - Tối đa 20 query.
  - Hiển thị khi focus vào ô search.
  - Cho phép xóa từng query hoặc toàn bộ.
- Gợi ý địa điểm:
  - Dựa trên Hike và Observation có `location_name` xuất hiện nhiều hoặc được mở gần đây.
- Tối ưu:
  - Index các cột `name`, `location_name`, `description`.
  - Giới hạn số kết quả mỗi page.

## 8. Map, địa điểm, khoảng cách, geocode cache

- Nhập vị trí:
  - Nhập tay `location_name`, `latitude`, `longitude`.
  - Hoặc chọn trên map (Google Maps SDK):
    - Tap để đặt marker.
    - Lưu `latitude`, `longitude`.
    - Nếu có mạng: reverse geocoding lấy `location_name` và lưu cache.
- `geocode_cache`:
  - `key`: `"lat,lon"`.
  - `value`: json kết quả geocode.
  - `expires_at`: 7 ngày.
- Khoảng cách:
  - Lấy vị trí hiện tại nếu có quyền.
  - Tính Haversine tới tọa độ Hike.
  - Hiển thị trên card Feed và Hike Detail:
    - < 1 km: hiển thị mét.
    - ≥ 1 km: hiển thị km 1 chữ số lẻ.

## 9. Weather và elevation

- Weather:
  - Dùng `latitude`, `longitude`, ngày Hike để gọi API thời tiết.
  - Cache theo `(hike_id, ngày)`.
  - Hiển thị nhiệt độ và điều kiện tổng quát.
  - Offline hoặc lỗi: ẩn phần weather hoặc hiển thị "Không có dữ liệu thời tiết".
- Elevation:
  - Dùng `latitude`, `longitude` gọi API elevation.
  - Hiển thị độ cao hoặc biểu đồ elevation.
  - Nếu không có dữ liệu: ẩn phần elevation.

## 10. Profile/Settings và Dashboard

### 10.1 Profile

- `display_name`:
  - Bắt buộc, tối đa 50 ký tự, không toàn khoảng trắng.
- `bio`:
  - Optional, tối đa 160 ký tự.
- Avatar:
  - Nếu chưa upload:
    - Tạo avatar chữ cái đầu của `display_name`.
  - Nếu upload:
    - Chọn ảnh, lưu cục bộ, cập nhật `avatar_path`.

### 10.2 Ngôn ngữ

- Hỗ trợ tối thiểu:
  - English.
  - Vietnamese.
- Áp dụng cho toàn bộ text UI.
- Không dịch nội dung do user tạo (name, caption, description).
- Mặc định:
  - Dùng ngôn ngữ hệ thống nếu chưa cấu hình.
- Có thể đổi trong Settings, áp dụng ngay.

### 10.3 Theme

- Lựa chọn:
  - `light`
  - `dark`
  - `black_white`
  - `system`
- `black_white`:
  - UI đơn sắc, ít màu accent.
- `system`:
  - Theo theme hệ điều hành.
- Lưu trong `profile` hoặc `settings` và áp dụng toàn app.

### 10.4 Nhạc nền

- Bật tắt nhạc nền toàn app.
- Chỉ phát khi app foreground.
- Dừng khi app background hoặc bị đóng.
- Tôn trọng âm lượng và chế độ im lặng hệ thống.
- Tùy chọn nâng cao:
  - Chỉ phát khi WiFi.
  - Không phát khi dùng dữ liệu di động.
- Mặc định: off.

### 10.5 Notification

- Công tắc `notifications_enabled` trong app, vẫn phụ thuộc quyền OS.
- Nhắc lịch Hike:
  - 1 ngày trước `planned_date` lúc `notification_evening_hour`.
  - Sáng ngày `planned_date` lúc `notification_morning_hour` nếu Hike chưa Completed.
- User:
  - Chỉnh giờ morning/evening.
  - Tắt notification cho từng Hike nếu cần.
- Dùng timezone thiết bị, khi timezone đổi phải reschedule notifications.

### 10.6 Dashboard thống kê (trong Profile/Settings)

- Chỉ số chính:
  - Tổng số Hike Completed.
  - Tổng quãng đường đã đi.
  - Tổng `elevation_gain_m` nếu có.
  - Số ngày active trong khoảng thời gian chọn.
- Biểu đồ:
  - Cột hoặc đường theo ngày/tuần/tháng cho distance hoặc số Hike.
  - Tròn phân bố difficulty:
    - `Easy`, `Moderate`, `Hard`, `Expert`.
- Bộ lọc:
  - Tuần này.
  - Tháng này.
  - 3 tháng gần nhất.
  - 1 năm.
  - Khoảng tùy chọn.
  - Filter difficulty, favorite nếu cần.
- Highlight:
  - Hike dài nhất.
  - Hike có elevation gain cao nhất.
  - Streak ngày liên tiếp có hoạt động dài nhất.
- Dữ liệu:
  - Đọc từ `hikes`, `observations` và dữ liệu thống kê liên quan.
  - Cập nhật khi CRUD Hike/Observation hoặc đổi `is_completed`.

## 11. Storage và offline

- Dùng SQLite với các bảng:
  - `hikes`
  - `observations`
  - `observation_media`
  - `profile`
  - `settings`
  - `geocode_cache`
- Media:
  - Lưu file vật lý trong storage riêng của app.
  - DB chỉ lưu đường dẫn.
- Khởi động app:
  - Mở DB.
  - Load `profile` và `settings`.
  - Không load toàn bộ Feed cho đến khi tab được mở.
- Nếu DB hỏng:
  - Có cơ chế tạo DB mới.
  - Cảnh báo nguy cơ mất dữ liệu.
- Offline:
  - CRUD Hike, Observation, media, search, filter, thống kê local đều chạy được.
  - Cần mạng cho:
    - Map online.
    - Weather.
    - Elevation.
    - AI.
    - Authentication (nếu có backend).

## 12. Authentication (nếu dùng backend)

- Login:
  - Input: `email`, `password`.
  - Validate: email đúng định dạng, password tối thiểu 8 ký tự.
  - Gửi request lên server, nhận token, lưu secure.
- Register:
  - Input: `email`, `password`, `confirm_password`, `display_name`.
  - Validate: email chưa tồn tại, password đủ mạnh, `confirm_password` trùng.
  - Sau khi đăng ký: auto login hoặc yêu cầu verify email.
- Logout:
  - Xóa token khỏi secure storage.
  - Xóa cache nhạy cảm.
  - Điều hướng về màn Login hoặc Welcome.
- Forgot password:
  - User nhập email.
  - Server gửi mail reset.
  - Thông báo dạng: "Nếu email tồn tại, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu".
  - Link reset một lần, có thời hạn.

## 13. Tính năng AI

### 13.1 Hỏi đáp kiến thức hiking

- User nhập câu hỏi tiếng Việt hoặc tiếng Anh.
- App gửi prompt lên dịch vụ AI, có thể kèm context về mức kinh nghiệm user.
- Hiển thị câu trả lời dạng text, có thể format bullet, checklist, step by step.
- Lưu một số Q&A gần đây để user xem lại.
- Có disclaimer: nội dung chỉ mang tính tham khảo.

### 13.2 Gợi ý lịch trình Hike

- User nhập:
  - Ngày bắt đầu, số ngày.
  - Khu vực hoặc Hike ưu tiên.
  - Difficulty mong muốn:
    - `Easy`
    - `Moderate`
    - `Hard`
    - `Expert`
  - Thời lượng mỗi ngày: nửa ngày hoặc 1 ngày.
- App dùng dữ liệu Hike local (Planned, Completed) và nếu có thể thì lấy thêm weather để tạo context.
- Gửi lên AI để gợi ý lịch trình theo ngày, gồm:
  - Hike gợi ý.
  - Giờ xuất phát gợi ý.
  - Lưu ý về difficulty và chuẩn bị đồ.
- Entry point:
  - Nút Suggestion plan trong tab Plan hoặc trong Profile/Settings.
