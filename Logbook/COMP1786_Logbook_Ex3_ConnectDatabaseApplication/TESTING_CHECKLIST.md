# Testing Checklist - Danh Sách Kiểm Tra

## ✅ KIỂM TRA CƠ BẢN / BASIC TESTS

### 1. Khởi động ứng dụng / App Launch
- [ ] App khởi động không crash
- [ ] Màn hình ContactsListActivity hiển thị
- [ ] Toolbar hiển thị đúng
- [ ] FAB button hiển thị

### 2. Thêm Contact mới / Add New Contact
- [ ] Click FAB → EditContactActivity mở
- [ ] Toolbar title hiển thị "Add Contact"
- [ ] Tất cả fields hiển thị đúng
- [ ] Avatar mặc định hiển thị

#### Test Input Validation:
- [ ] Bỏ trống tên → Hiển thị lỗi "Name required"
- [ ] Bỏ trống ngày sinh → Hiển thị lỗi "Date of birth required"
- [ ] Nhập email sai định dạng → Hiển thị lỗi "Invalid email"
- [ ] Email bỏ trống → Cho phép (optional field)

#### Test Date Picker:
- [ ] Click vào Date of Birth field → DatePickerDialog mở
- [ ] Chọn ngày → Định dạng dd/MM/yyyy (VD: 01/05/2024)
- [ ] Ngày hiển thị đúng trong field

#### Test Avatar Selection:
- [ ] Click "Choose Avatar" → AvatarPickerActivity mở
- [ ] Hiển thị 6 avatars + 1 upload button
- [ ] Click avatar → Quay về và avatar cập nhật
- [ ] Avatar được highlight khi đã chọn

#### Test Upload Avatar:
- [ ] Click upload icon → Permission request (nếu chưa cấp)
- [ ] Cấp permission → Photo picker mở
- [ ] Chọn ảnh → Ảnh hiển thị trong ImageView
- [ ] Ảnh được lưu vào internal storage

#### Test Save:
- [ ] Nhập đầy đủ thông tin → Click Save
- [ ] Toast "Contact added" hiển thị
- [ ] Quay về ContactsListActivity
- [ ] Contact mới hiển thị trong danh sách
- [ ] Contact count cập nhật (+1)

### 3. Xem Chi Tiết Contact / View Contact Details
- [ ] Click vào contact → DetailContactActivity mở
- [ ] Toolbar title hiển thị tên contact
- [ ] Avatar hiển thị đúng
- [ ] Name, DOB, Email hiển thị đúng
- [ ] "Last updated" timestamp hiển thị

### 4. Chỉnh Sửa Contact / Edit Contact
- [ ] Từ DetailActivity, click Edit button
- [ ] EditContactActivity mở với dữ liệu đã có
- [ ] Toolbar title hiển thị "Edit Contact"
- [ ] Thay đổi name, email, DOB
- [ ] Click Save → Toast "Contact updated"
- [ ] Quay về DetailActivity → Dữ liệu mới hiển thị
- [ ] ContactsListActivity tự động cập nhật

#### Test Avatar Update During Edit:
- [ ] Trong EditContactActivity, chọn avatar mới
- [ ] Toast "Avatar updated" hiển thị ngay
- [ ] Quay về DetailActivity → Avatar đã thay đổi
- [ ] Không cần click Save để avatar cập nhật

### 5. Xóa Contact / Delete Contact
- [ ] Từ ContactsListActivity, click nút Delete (màu đỏ)
- [ ] Confirmation dialog hiển thị
- [ ] Tên contact hiển thị trong dialog
- [ ] Click Cancel → Không xóa
- [ ] Click OK → Contact bị xóa
- [ ] Toast "Deleted: [Name]" hiển thị
- [ ] Danh sách cập nhật
- [ ] Contact count giảm (-1)

---

## ✅ KIỂM TRA TÍNH NĂNG NÂNG CAO / ADVANCED FEATURES

### 6. Search / Tìm Kiếm
- [ ] Click search icon → SearchView mở
- [ ] Gõ tên contact → Kết quả lọc theo tên
- [ ] Gõ email → Kết quả lọc theo email
- [ ] Xóa search → Hiển thị tất cả lại
- [ ] Không tìm thấy → Hiển thị danh sách trống

### 7. Sort / Sắp Xếp
- [ ] Menu → "Sort by name A-Z" → Sắp xếp A→Z
- [ ] Menu → "Sort by name Z-A" → Sắp xếp Z→A
- [ ] Menu → "Newest first" → Mới nhất trước
- [ ] Menu → "Oldest first" → Cũ nhất trước

### 8. Filter Favorites / Lọc Yêu Thích
#### Test Favorite Toggle:
- [ ] Click star icon (trống) → Star filled (vàng)
- [ ] Toast "[Name] added to favorites" hiển thị
- [ ] Click star filled → Star trống lại
- [ ] Toast "[Name] removed from favorites" hiển thị
- [ ] Thay đổi propagate sang DetailActivity ngay lập tức

#### Test Favorite Filter:
- [ ] Menu → "Show favorites only" → Check
- [ ] Chỉ hiển thị contacts có star filled
- [ ] Contact count cập nhật
- [ ] Menu → "Show favorites only" lại → Uncheck → Hiển thị tất cả

### 9. Filter Birth Month / Lọc Sinh Nhật Tháng Này
- [ ] Menu → "Birthdays this month" → Check
- [ ] Chỉ hiển thị contacts có sinh nhật trong tháng hiện tại
- [ ] Contact count cập nhật
- [ ] Uncheck → Hiển thị tất cả lại

### 10. Combine Filters / Kết Hợp Bộ Lọc
- [ ] Search "John" + Filter favorites → Chỉ Johns là favorite
- [ ] Search + Birth month → Chỉ contacts match search và sinh nhật tháng này
- [ ] Favorites + Birth month → Chỉ favorites có sinh nhật tháng này
- [ ] Search + Favorites + Birth month → Tất cả bộ lọc áp dụng cùng lúc

### 11. Multi-Select & Batch Delete / Chọn Nhiều & Xóa Hàng Loạt
#### Enable Multi-Select:
- [ ] Menu → "Multi-select"
- [ ] Toast "Selection mode ON" hiển thị
- [ ] Checkboxes hiển thị trên tất cả items
- [ ] Menu "Delete selected" hiển thị

#### Select Items:
- [ ] Click checkbox → Item được chọn
- [ ] Click lại → Item bỏ chọn
- [ ] Chọn nhiều items (3-5 items)

#### Delete Selected:
- [ ] Menu → "Delete selected"
- [ ] Confirmation dialog hiển thị số lượng (VD: "Delete 3 selected contacts?")
- [ ] Click OK → Tất cả contacts đã chọn bị xóa
- [ ] Toast "Selected contacts deleted" hiển thị
- [ ] Selection mode tự động tắt
- [ ] Danh sách cập nhật

#### Exit Multi-Select:
- [ ] Menu → "Multi-select" lại → Selection mode OFF
- [ ] Checkboxes ẩn
- [ ] Toast "Selection mode OFF"

### 12. Clear All / Xóa Tất Cả
- [ ] Menu → "Clear all contacts"
- [ ] Confirmation dialog hiển thị
- [ ] Click OK → Tất cả contacts bị xóa
- [ ] Toast "All contacts deleted"
- [ ] Contact count = 0
- [ ] Danh sách trống

---

## ✅ KIỂM TRA REAL-TIME UPDATES / CẬP NHẬT THỜI GIAN THỰC

### 13. Cross-Screen Updates
#### Scenario 1: Edit trong DetailActivity
1. [ ] Mở DetailActivity của contact A
2. [ ] Nhớ avatar và thông tin hiện tại
3. [ ] Click Edit → Thay đổi name, avatar
4. [ ] Save → Quay về DetailActivity
5. [ ] **Kiểm tra**: Thông tin mới hiển thị ngay (không cần refresh)
6. [ ] Quay về ContactsListActivity
7. [ ] **Kiểm tra**: Contact A hiển thị thông tin mới

#### Scenario 2: Avatar Update Immediate
1. [ ] Mở EditContactActivity (edit mode)
2. [ ] Chọn avatar mới
3. [ ] **KHÔNG CLICK SAVE**
4. [ ] Quay về DetailActivity
5. [ ] **Kiểm tra**: Avatar đã cập nhật (immediate save)
6. [ ] ContactsListActivity cũng hiển thị avatar mới

#### Scenario 3: Favorite Toggle Propagation
1. [ ] Mở DetailActivity của contact B
2. [ ] Quay về ContactsListActivity
3. [ ] Toggle favorite star
4. [ ] Vào DetailActivity của contact B lại
5. [ ] **Kiểm tra**: Favorite state đã thay đổi

---

## ✅ KIỂM TRA XỬ LÝ LỖI / ERROR HANDLING

### 14. Permission Handling
- [ ] Từ chối permission upload ảnh → Toast "Permission denied"
- [ ] Cấp permission sau khi từ chối → Upload hoạt động
- [ ] Android 13+: READ_MEDIA_IMAGES permission
- [ ] Android < 13: READ_EXTERNAL_STORAGE permission

### 15. Image Loading Errors
- [ ] Xóa ảnh đã upload khỏi thiết bị → Fallback to default avatar
- [ ] SecurityException → Hiển thị avatar mặc định + toast warning

### 16. Empty States
- [ ] Database trống → Hiển thị "0 contacts"
- [ ] Search không tìm thấy → Danh sách trống (không crash)
- [ ] Filter không match → Danh sách trống

### 17. Navigation
- [ ] Click back từ DetailActivity → Quay về ListActivity
- [ ] Click back từ EditActivity → Quay về screen trước
- [ ] Click toolbar back arrow → Hoạt động như nút back

---

## ✅ KIỂM TRA HIỆU NĂNG / PERFORMANCE TESTS

### 18. Large Dataset
- [ ] Thêm 20+ contacts
- [ ] Scroll list → Mượt, không lag
- [ ] Search trong danh sách lớn → Nhanh
- [ ] Sort → Không lag
- [ ] Filter → Hiệu quả

### 19. Memory Leaks
- [ ] Mở/đóng nhiều activities → Không crash
- [ ] BroadcastReceiver unregistered khi destroy → Không leak
- [ ] Upload nhiều ảnh → Không OutOfMemory

---

## ✅ KIỂM TRA UI/UX / UI/UX TESTS

### 20. Visual Design
- [ ] Material Design 3 components
- [ ] Gradient toolbar đẹp
- [ ] Card corners rounded (16dp)
- [ ] Shadows/elevations phù hợp
- [ ] Colors consistent với theme
- [ ] Icons rõ ràng và đúng nghĩa

### 21. Responsive Layout
- [ ] Portrait mode → Layout đúng
- [ ] Landscape mode → Layout adapt tốt
- [ ] Scroll hoạt động khi content dài
- [ ] Keyboard hiển thị → Fields không bị che

### 22. Feedback & Messages
- [ ] Toast messages rõ ràng
- [ ] Confirmation dialogs có message phù hợp
- [ ] Contact count cập nhật chính xác
- [ ] Loading states (nếu có)

---

## ✅ KIỂM TRA EDGE CASES / TRƯỜNG HỢP ĐẶC BIỆT

### 23. Special Characters
- [ ] Tên có ký tự đặc biệt: "O'Brien", "José", "李明"
- [ ] Email có + và .: "test+tag@example.co.uk"
- [ ] Tất cả hiển thị và tìm kiếm đúng

### 24. Long Text
- [ ] Tên rất dài → Ellipsize đúng
- [ ] Email rất dài → Ellipsize đúng
- [ ] Không làm vỡ layout

### 25. Date Edge Cases
- [ ] Ngày 01/01/2000 → Định dạng đúng
- [ ] Ngày 31/12/2025 → Lưu và hiển thị đúng
- [ ] Birth month filter với các tháng khác nhau

---

## 📊 SUMMARY / TÓM TẮT

### Tổng Số Tests: **75+ test cases**

#### Nhóm Tests:
- ✅ Basic CRUD: 15 tests
- ✅ Search & Filter: 20 tests
- ✅ Real-time Updates: 10 tests
- ✅ Error Handling: 10 tests
- ✅ Performance: 5 tests
- ✅ UI/UX: 10 tests
- ✅ Edge Cases: 5 tests

### Mức Độ Ưu Tiên:
- 🔴 **Critical (P0)**: Basic CRUD, App Launch, Navigation
- 🟡 **High (P1)**: Search, Filter, Favorite, Delete
- 🟢 **Medium (P2)**: Multi-select, Sort, UI polish
- 🔵 **Low (P3)**: Edge cases, Long text handling

---

## 🎯 ACCEPTANCE CRITERIA / TIÊU CHÍ CHẤP NHẬN

Để dự án được coi là **HOÀN CHỈNH VÀ SẴN SÀNG**, cần đạt:

- [ ] **100% P0 tests** pass
- [ ] **95%+ P1 tests** pass
- [ ] **90%+ P2 tests** pass
- [ ] **Không có crash** trong normal usage
- [ ] **UI smooth** và responsive
- [ ] **Data consistency** across screens

---

**Status khi hoàn thành checklist này**: ✅ **READY FOR SUBMISSION**

