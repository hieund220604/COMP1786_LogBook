package com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp1786_logbook_ex3_connectdatabaseapplication.R;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.AppDatabase;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.Contact;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.ContactDao;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.helper.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class ContactsListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rv;
    private ContactsAdapter adapter;
    private ContactDao dao;
    private TextView tvContactCount;
    private static final int REQ_ADD = 1;

    private enum SortMode { NAME_ASC, NAME_DESC, CREATED_DESC, CREATED_ASC }
    private SortMode currentSort = SortMode.NAME_ASC;
    private boolean showFavoritesOnly = false;
    private boolean filterBirthMonth = false;
    private String currentSearchQuery = "";

    private final BroadcastReceiver contactsChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_CONTACTS_CHANGED.equals(intent.getAction())) {
                loadContacts();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        dao = AppDatabase.getInstance(this).contactDao();

        // Setup toolbar
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = findViewById(R.id.rvContacts);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactsAdapter(this::onItemClick, this::onItemDelete, this::onFavoriteToggle);
        rv.setAdapter(adapter);

        tvContactCount = findViewById(R.id.tvContactCount);
        findViewById(R.id.fabAdd).setOnClickListener(this);

        loadContacts();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(contactsChangedReceiver);
    }

    private void loadContacts() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Contact> all;

            // First, get the base list based on sorting
            switch (currentSort) {
                case NAME_DESC:
                    all = dao.getAllSortedByNameDesc();
                    break;
                case CREATED_DESC:
                    all = dao.getAllSortedByCreatedAtDesc();
                    break;
                case CREATED_ASC:
                    all = dao.getAllSortedByCreatedAtAsc();
                    break;
                case NAME_ASC:
                default:
                    all = dao.getAllSortedByNameAsc();
                    break;
            }

            // Apply filters in memory to allow combinations
            if (all != null) {
                List<Contact> filtered = new ArrayList<>(all);

                // Apply search filter
                if (currentSearchQuery != null && !currentSearchQuery.trim().isEmpty()) {
                    String query = currentSearchQuery.trim().toLowerCase();
                    filtered = new ArrayList<>();
                    for (Contact c : all) {
                        if ((c.name != null && c.name.toLowerCase().contains(query)) ||
                            (c.email != null && c.email.toLowerCase().contains(query))) {
                            filtered.add(c);
                        }
                    }
                }

                // Apply favorites filter
                if (showFavoritesOnly) {
                    List<Contact> favFiltered = new ArrayList<>();
                    for (Contact c : filtered) {
                        if (c.isFavorite) {
                            favFiltered.add(c);
                        }
                    }
                    filtered = favFiltered;
                }

                // Apply birth month filter
                if (filterBirthMonth) {
                    int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
                    String monthStr = month < 10 ? "0" + month : String.valueOf(month);
                    List<Contact> birthFiltered = new ArrayList<>();
                    for (Contact c : filtered) {
                        if (c.dob != null && c.dob.length() >= 5) {
                            String contactMonth = c.dob.substring(3, 5);
                            if (monthStr.equals(contactMonth)) {
                                birthFiltered.add(c);
                            }
                        }
                    }
                    filtered = birthFiltered;
                }

                all = filtered;
            }

            List<Contact> finalAll = all;
            runOnUiThread(() -> {
                adapter.setItems(finalAll);
                // Update contact count
                int count = finalAll != null ? finalAll.size() : 0;
                String countText = count + (count == 1 ? " contact" : " contacts");
                if (tvContactCount != null) {
                    tvContactCount.setText(countText);
                }
            });
        });
    }

    private void onItemClick(Contact c) {
        // Open detail activity for the selected contact
        Intent i = new Intent(this, DetailContactActivity.class);
        i.putExtra(DetailContactActivity.EXTRA_ID, c.id);
        startActivityForResult(i, REQ_ADD);
    }

    private void onItemDelete(Contact c) {
        // Confirm deletion
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(getString(R.string.confirm_delete_single, c.name))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        dao.delete(c);
                        runOnUiThread(() -> {
                            Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
                            sendBroadcast(intent);
                            Toast.makeText(this, "Deleted: " + c.name, Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void onFavoriteToggle(Contact c) {
        android.util.Log.d("ContactsListActivity", "onFavoriteToggle called for: " + c.name + ", current: " + c.isFavorite + ", id=" + c.id);
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean newFav = !c.isFavorite;
            long ts = System.currentTimeMillis();
            if (c.id > 0) {
                // Use dedicated update to avoid replacing the row
                dao.updateFavorite(c.id, newFav ? 1 : 0, ts);
            } else {
                // fallback: set on object and upsert
                c.isFavorite = newFav;
                c.updatedAt = ts;
                long newId = dao.upsert(c);
                if (newId > 0) c.id = newId;
            }

            // Update in-memory model for immediate feedback (adapter reload will use DB data)
            c.isFavorite = newFav;
            c.updatedAt = ts;

            android.util.Log.d("ContactsListActivity", "Favorite updated in DB for: " + c.name + ", new value: " + c.isFavorite);
            runOnUiThread(() -> {
                Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
                sendBroadcast(intent);
                Toast.makeText(this, c.name + (c.isFavorite ? " added to favorites" : " removed from favorites"), Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabAdd) {
            startActivityForResult(new Intent(this, EditContactActivity.class), REQ_ADD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADD && resultCode == RESULT_OK) {
            // Reload contacts immediately to show updated data
            loadContacts();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Update checkable menu items
        MenuItem favItem = menu.findItem(R.id.action_filter_favorites);
        if (favItem != null) {
            favItem.setChecked(showFavoritesOnly);
        }

        MenuItem birthItem = menu.findItem(R.id.action_filter_birth_month);
        if (birthItem != null) {
            birthItem.setChecked(filterBirthMonth);
        }

        // Show/hide delete selected based on selection mode
        MenuItem deleteSelectedItem = menu.findItem(R.id.action_delete_selected);
        if (deleteSelectedItem != null) {
            deleteSelectedItem.setVisible(adapter != null && adapter.isSelectionMode());
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                loadContacts();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText;
                loadContacts();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_name_asc) {
            currentSort = SortMode.NAME_ASC;
            loadContacts();
            return true;
        } else if (id == R.id.action_sort_name_desc) {
            currentSort = SortMode.NAME_DESC;
            loadContacts();
            return true;
        } else if (id == R.id.action_sort_created_desc) {
            currentSort = SortMode.CREATED_DESC;
            loadContacts();
            return true;
        } else if (id == R.id.action_sort_created_asc) {
            currentSort = SortMode.CREATED_ASC;
            loadContacts();
            return true;
        } else if (id == R.id.action_filter_favorites) {
            showFavoritesOnly = !showFavoritesOnly;
            item.setChecked(showFavoritesOnly);
            loadContacts();
            return true;
        } else if (id == R.id.action_filter_birth_month) {
            filterBirthMonth = !filterBirthMonth;
            item.setChecked(filterBirthMonth);
            loadContacts();
            return true;
        } else if (id == R.id.action_clear_all) {
            confirmClearAll();
            return true;
        } else if (id == R.id.action_multi_select) {
            toggleSelectionMode();
            return true;
        } else if (id == R.id.action_delete_selected) {
            deleteSelectedContacts();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleSelectionMode() {
        boolean enable = !adapter.isSelectionMode();
        adapter.setSelectionMode(enable);
        invalidateOptionsMenu(); // Refresh menu to show/hide delete selected
        if (!enable) {
            Toast.makeText(this, R.string.selection_mode_off, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.selection_mode_on, Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmClearAll() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_all_title)
                .setMessage(R.string.clear_all_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        Executors.newSingleThreadExecutor().execute(() -> {
                            dao.clearAll();
                            runOnUiThread(() -> {
                                Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
                                sendBroadcast(intent);
                                Toast.makeText(this, R.string.cleared_all, Toast.LENGTH_SHORT).show();
                            });
                        }))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void deleteSelectedContacts() {
        Set<Long> ids = adapter.getSelectedIds();
        if (ids.isEmpty()) {
            Toast.makeText(this, R.string.no_items_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(getString(R.string.confirm_delete_multiple, ids.size()))
                .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        Executors.newSingleThreadExecutor().execute(() -> {
                            dao.deleteByIds(new ArrayList<>(ids));
                            runOnUiThread(() -> {
                                Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
                                sendBroadcast(intent);
                                Toast.makeText(this, R.string.deleted_selected, Toast.LENGTH_SHORT).show();
                                adapter.setSelectionMode(false);
                            });
                        }))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
