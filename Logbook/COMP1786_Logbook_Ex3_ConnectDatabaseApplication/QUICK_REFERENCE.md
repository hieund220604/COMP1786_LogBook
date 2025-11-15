# ✅ Quick Reference - Real-time Avatar Update

## What Changed?

**File: `EditContactActivity.java`**

Added immediate database update + broadcast when avatar is selected (for existing contacts only).

## The Code

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQ_PICK_AVATAR && resultCode == RESULT_OK && data != null) {
        String chosen = data.getStringExtra(AvatarPickerActivity.EXTRA_CHOSEN);
        if (chosen != null) {
            avatarName = chosen;
            updateAvatarImage(); // Update preview
            
            // NEW CODE: Save immediately if editing existing contact
            if (editingContact != null) {
                editingContact.avatarName = avatarName;
                Executors.newSingleThreadExecutor().execute(() -> {
                    dao.upsert(editingContact);
                    runOnUiThread(() -> {
                        Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
                        sendBroadcast(intent);
                        Toast.makeText(this, "Avatar updated", Toast.LENGTH_SHORT).show();
                    });
                });
            }
        }
    }
}
```

## How It Works

1. User selects avatar → `onActivityResult()` called
2. Preview updates in EditActivity
3. **IF editing existing contact:**
   - Save avatar to database immediately
   - Send broadcast `CONTACTS_CHANGED`
   - Show toast "Avatar updated"
4. DetailActivity receives broadcast → reloads contact → shows new avatar
5. ContactsListActivity receives broadcast → reloads list → shows new avatar

## Result

✅ Avatar updates **instantly** across all screens  
✅ No need to click Save button  
✅ No need to manually refresh  
✅ No data loss if user exits  

## Test It

1. Open an existing contact
2. Click Edit
3. Choose new avatar
4. Press Back (don't save)
5. **Check:** Avatar should be updated in Detail view ✅
6. Press Back again
7. **Check:** Avatar should be updated in List view ✅

---

**Status: DONE! ✅**

