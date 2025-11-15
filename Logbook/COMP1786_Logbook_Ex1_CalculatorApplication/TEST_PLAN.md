- [ ] API 26-30 → All features work except blur
- [ ] Vibration works on all supported devices

**Test Different Screen Sizes:**
- [ ] Small phone → Layout adapts
- [ ] Large phone → Layout scales properly
- [ ] Tablet → Still works (portrait locked)

**Expected Result:** ✅ Works on all target devices

---

### ⚡ Performance Test
**Test 1: Memory**
- [ ] 1. Open app
- [ ] 2. Let background slideshow run for 5 minutes
- [ ] 3. Perform 20 calculations
- [ ] 4. Check memory usage → Should be stable (no leaks)

**Test 2: Responsiveness**
- [ ] 1. All button presses respond instantly (< 50ms)
- [ ] 2. Live preview updates smoothly
- [ ] 3. Background transitions are smooth

**Test 3: Battery**
- [ ] 1. Use app for 10 minutes
- [ ] 2. Check battery drain → Should be minimal
- [ ] 3. Background slideshow pauses when app is backgrounded

**Expected Result:** ✅ Good performance, no excessive resource usage

---

## 📊 Test Summary Template

### Test Session: [Date/Time]
**Tester:** [Name]
**Device:** [Model & Android Version]

| Feature | Status | Notes |
|---------|--------|-------|
| Background Slideshow | ✅/❌ | |
| Basic Calculator | ✅/❌ | |
| Copy to Clipboard | ✅/❌ | |
| Operator Highlight | ✅/❌ | |
| Live Preview | ✅/❌ | |
| Number Formatting | ✅/❌ | |
| Animations | ✅/❌ | |
| Haptic Feedback | ✅/❌ | |
| Edge Cases | ✅/❌ | |
| Performance | ✅/❌ | |

**Overall Result:** ✅ PASS / ❌ FAIL

**Bugs Found:**
1. [None expected]

**Notes:**
- All features working without conflicts
- Background slideshow runs smoothly
- New features integrated seamlessly

---

## 🎯 Acceptance Criteria

✅ **All tests must pass:**
1. Background slideshow works (5 second intervals)
2. All 4 calculator operations work correctly
3. Copy to clipboard works for result and expression
4. Operator highlight shows active operation
5. Live preview shows calculation in real-time
6. Numbers display with thousand separators
7. All animations play smoothly
8. Haptic feedback provides appropriate intensity
9. No crashes or memory leaks
10. No feature conflicts

**Định nghĩa PASS:**
- Tất cả 10 điều trên ✅
- App mượt mà, không lag
- UX tốt, trực quan
- Không có bug nghiêm trọng

---

## 📝 Test Notes

### How to Test Systematically:
1. **Start fresh:** Uninstall and reinstall app
2. **Test one feature at a time:** Follow checklist
3. **Test combinations:** Ensure no conflicts
4. **Test edge cases:** Try to break it
5. **Test performance:** Long-running sessions

### Common Issues to Watch For:
- ❌ Background stops changing
- ❌ Operator highlight doesn't reset
- ❌ Preview doesn't update
- ❌ Copy doesn't work
- ❌ Animations stutter
- ❌ Haptic feedback doesn't vibrate

### If You Find a Bug:
1. Note exact steps to reproduce
2. Note device and Android version
3. Check logcat for errors
4. Try on different device if possible

---

## ✅ Conclusion

Nếu tất cả test cases trên đều PASS:
🎉 **App hoàn thiện, sẵn sàng submit hoặc deploy!**

**Chất lượng đảm bảo:**
- ✅ Tính năng cũ không bị ảnh hưởng
- ✅ Tính năng mới hoạt động tốt
- ✅ UX được cải thiện đáng kể
- ✅ Performance ổn định
- ✅ Không có xung đột
# Test Plan - Calculator App
**Date:** November 13, 2025

---

## ✅ Comprehensive Test Checklist

### 🎨 Background Slideshow Test
- [ ] 1. Launch app
- [ ] 2. Wait 5 seconds → Background should crossfade to second image
- [ ] 3. Wait another 5 seconds → Background should crossfade to third image
- [ ] 4. Wait another 5 seconds → Background should loop back to first image
- [ ] 5. During slideshow, try all calculator operations → Should work smoothly
- [ ] 6. Rotate device → Slideshow should continue (if rotation is enabled)
- [ ] 7. Press home and return → Slideshow should resume

**Expected Result:** ✅ Background changes every 5 seconds with smooth fade animation

---

### 🔢 Basic Calculator Operations Test
- [ ] 1. Addition: `100 + 50 = 150`
- [ ] 2. Subtraction: `200 - 75 = 125`
- [ ] 3. Multiplication: `12 × 8 = 96`
- [ ] 4. Division: `100 ÷ 4 = 25`
- [ ] 5. Percent: `200` → `%` = `2`
- [ ] 6. Plus/Minus: `50` → `±` = `-50`
- [ ] 7. Clear: `123` → `C` = `0`
- [ ] 8. Backspace: `123` → `⌫` = `12`

**Expected Result:** ✅ All operations work correctly

---

### 🆕 Copy to Clipboard Test
**Test 1: Copy Result**
- [ ] 1. Calculate: `100 + 50 =`
- [ ] 2. Result shows: `= 150`
- [ ] 3. Long press on `= 150`
- [ ] 4. Toast appears: "Result copied to clipboard"
- [ ] 5. Phone vibrates (medium haptic)
- [ ] 6. Paste in another app → Should show `150` (without "= ")

**Test 2: Copy Expression**
- [ ] 1. Type: `100 + 50`
- [ ] 2. Long press on expression area
- [ ] 3. Toast appears: "Expression copied to clipboard"
- [ ] 4. Phone vibrates
- [ ] 5. Paste → Should show `100 + 50`

**Test 3: Copy Empty**
- [ ] 1. Clear calculator
- [ ] 2. Long press on empty result
- [ ] 3. Toast appears: "Nothing to copy"
- [ ] 4. No vibration

**Expected Result:** ✅ Copy works correctly, with proper feedback

---

### 🎯 Operator Highlight Test
**Test 1: Single Operation**
- [ ] 1. Type: `100`
- [ ] 2. Press `+` → **Plus button becomes dim (alpha 0.6)**
- [ ] 3. Type: `50`
- [ ] 4. Press `=` → **Plus button returns to normal brightness**
- [ ] 5. Calculation complete

**Test 2: Change Operator**
- [ ] 1. Type: `100`
- [ ] 2. Press `+` → **Plus button dims**
- [ ] 3. Press `×` → **Plus button returns to normal, multiply button dims**
- [ ] 4. Type: `5`
- [ ] 5. Press `=` → **Multiply button returns to normal**

**Test 3: Clear Resets Highlight**
- [ ] 1. Type: `100`
- [ ] 2. Press `+` → **Plus button dims**
- [ ] 3. Press `C` → **Plus button returns to normal**

**Expected Result:** ✅ Active operator is always highlighted

---

### 👁️ Live Preview Test
**Test 1: Basic Preview**
- [ ] 1. Type: `100`
- [ ] 2. Press `+`
- [ ] 3. Type: `5` → Result shows `= 105` (dimmed, alpha 0.5)
- [ ] 4. Type: `0` → Result shows `= 150` (dimmed)
- [ ] 5. Press `=` → Result shows `= 150` (bright, alpha 1.0)

**Test 2: Division Preview**
- [ ] 1. Type: `100`
- [ ] 2. Press `÷`
- [ ] 3. Type: `2` → Result shows `= 50` (dimmed)
- [ ] 4. Type: `0` → Result shows `= 5` (dimmed)
- [ ] 5. Press `=` → Result shows `= 5` (bright)

**Test 3: Preview Updates Instantly**
- [ ] 1. Type: `1000`
- [ ] 2. Press `+`
- [ ] 3. Slowly type digits and watch preview update each time

**Expected Result:** ✅ Preview shows in real-time with 50% opacity

---

### 🔢 Number Formatting Test
**Test 1: Thousands Separator**
- [ ] 1. Type: `1000` → Shows `1,000`
- [ ] 2. Type: `1234567` → Shows `1,234,567`
- [ ] 3. Type: `1000000000` → Shows `1,000,000,000`

**Test 2: Decimals**
- [ ] 1. Type: `1000.5` → Shows `1,000.5`
- [ ] 2. Type: `1234567.89` → Shows `1,234,567.89`

**Test 3: Negative Numbers**
- [ ] 1. Type: `50000`
- [ ] 2. Press `±` → Shows `-50,000`

**Expected Result:** ✅ Large numbers display with comma separators

---

### 🎬 Animation Test
**Test 1: Number Entry Animation**
- [ ] 1. Type any digit → Result area animates (scale + fade)

**Test 2: Result Reveal Animation**
- [ ] 1. Complete calculation and press `=` → Result slides up and fades in

**Test 3: Error Shake Animation**
- [ ] 1. Press `5 ÷ 0 =` → Display shakes left-right
- [ ] 2. Strong vibration (50ms)

**Test 4: Clear Button Rotate**
- [ ] 1. Press `C` → Clear button rotates 360° with scale pulse

**Test 5: Button Press Animation**
- [ ] 1. Press any button → Scales down then back up
- [ ] 2. Light vibration (10ms for numbers, 20ms for operators)

**Expected Result:** ✅ All animations smooth and responsive

---

### 📳 Haptic Feedback Test
**Test 1: Light Haptic (Numbers)**
- [ ] 1. Press `1`, `2`, `3` → Each has subtle vibration (10ms)

**Test 2: Medium Haptic (Operators)**
- [ ] 1. Press `+`, `-`, `×`, `÷` → Slightly stronger vibration (20ms)
- [ ] 2. Copy to clipboard → Medium vibration

**Test 3: Heavy Haptic (Errors)**
- [ ] 1. Try `5 ÷ 0 =` → Strong vibration (50ms)

**Expected Result:** ✅ Different haptic intensities for different actions

---

### 🔄 Integration Test (No Conflicts)
**Test: All Features Working Together**
- [ ] 1. Start app → Background slideshow starts
- [ ] 2. Type `100` → Number formatting works
- [ ] 3. Press `+` → Operator highlights + medium haptic
- [ ] 4. Type `50` → Live preview shows `= 150` (dimmed)
- [ ] 5. Background changes during calculation → No interruption
- [ ] 6. Press `=` → Result animates in, operator unhighlights
- [ ] 7. Long press result → Copy to clipboard works
- [ ] 8. Clear → Everything resets
- [ ] 9. Background continues changing throughout

**Expected Result:** ✅ All features work simultaneously without conflicts

---

### 🐛 Edge Cases Test
**Test 1: Division by Zero**
- [ ] 1. `100 ÷ 0 =` → Error message: "Cannot divide by 0"
- [ ] 2. Display shakes + heavy haptic

**Test 2: Very Large Numbers**
- [ ] 1. `999999999 + 1 =` → Shows `1,000,000,000` with commas
- [ ] 2. Copy works correctly

**Test 3: Many Decimals**
- [ ] 1. `10 ÷ 3 =` → Shows `3.3333333333` (10 decimal places)

**Test 4: Rapid Button Presses**
- [ ] 1. Quickly press multiple buttons
- [ ] 2. All animations queue properly, no crash
- [ ] 3. Background slideshow continues

**Test 5: Long Press on Empty**
- [ ] 1. Start app
- [ ] 2. Long press on `0` → "Nothing to copy"

**Expected Result:** ✅ App handles all edge cases gracefully

---

### 📱 Device Compatibility Test
**Test on Different API Levels:**
- [ ] API 31+ (Android 12+) → Blur effect on background works

