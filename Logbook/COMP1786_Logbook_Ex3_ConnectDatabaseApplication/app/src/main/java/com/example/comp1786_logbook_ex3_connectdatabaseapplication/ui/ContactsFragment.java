package com.example.comp1786_logbook_ex3_connectdatabaseapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp1786_logbook_ex3_connectdatabaseapplication.R;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity.ContactsAdapter;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity.DetailContactActivity;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity.EditContactActivity;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.helper.Constants;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.AppDatabase;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.Contact;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.ContactDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class ContactsFragment extends Fragment implements View.OnClickListener {

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

    private EditText etSearch;
    private ImageView imgSearchIcon;
    private ImageButton btnSort;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        dao = AppDatabase.getInstance(requireContext()).contactDao();

        rv = v.findViewById(R.id.rvContactsFrag);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ContactsAdapter(this::onItemClick, this::onItemDelete, this::onFavoriteToggle);
        rv.setAdapter(adapter);

        tvContactCount = v.findViewById(R.id.tvContactCountFrag);
        FloatingActionButton fab = v.findViewById(R.id.fabAddFrag);
        if (fab != null) fab.setOnClickListener(this);

        // New search widgets
        etSearch = v.findViewById(R.id.etSearchFrag);
        imgSearchIcon = v.findViewById(R.id.imgSearchIcon);
        btnSort = v.findViewById(R.id.btnSortFrag);

        if (imgSearchIcon != null) imgSearchIcon.setOnClickListener(view -> {
            if (etSearch != null) {
                etSearch.requestFocus();
                openKeyboard(requireContext(), etSearch);
            }
        });

        if (etSearch != null) {
            etSearch.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) openKeyboard(requireContext(), etSearch);
            });
            etSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchQuery = s != null ? s.toString() : "";
                    loadContacts();
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }

        if (btnSort != null) {
            btnSort.setOnClickListener(view -> {
                // Toggle between A-Z and Z-A for now
                if (currentSort == SortMode.NAME_ASC) {
                    currentSort = SortMode.NAME_DESC;
                    btnSort.setImageResource(android.R.drawable.ic_menu_sort_by_size); // visual change
                } else {
                    currentSort = SortMode.NAME_ASC;
                    btnSort.setImageResource(android.R.drawable.ic_menu_sort_alphabetically);
                }
                loadContacts();
            });
        }

        // empty layout's add button should open editor
        View empty = v.findViewById(R.id.empty_container_contacts);
        if (empty != null) {
            View btn = empty.findViewById(R.id.btnAddContactEmpty);
            if (btn != null) btn.setOnClickListener(view -> startActivityForResult(new Intent(requireContext(), EditContactActivity.class), REQ_ADD));
        }

        loadContacts();
        return v;
    }

    private static void openKeyboard(Context ctx, View target) {
        target.post(() -> {
            target.requestFocus();
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
        });
    }

    private void loadContacts() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Contact> all;

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

            if (all != null) {
                List<Contact> filtered = new ArrayList<>(all);

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

                if (showFavoritesOnly) {
                    List<Contact> favFiltered = new ArrayList<>();
                    for (Contact c : filtered) {
                        if (c.isFavorite) {
                            favFiltered.add(c);
                        }
                    }
                    filtered = favFiltered;
                }

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
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                adapter.setItems(finalAll);
                int count = finalAll != null ? finalAll.size() : 0;
                String countText = count + (count == 1 ? " contact" : " contacts");
                if (tvContactCount != null) {
                    tvContactCount.setText(countText);
                }

                View empty = getView() != null ? getView().findViewById(R.id.empty_container_contacts) : null;
                if (empty != null) {
                    empty.setVisibility((finalAll == null || finalAll.isEmpty()) ? View.VISIBLE : View.GONE);
                }
            });
        });
    }

    private void onItemClick(Contact c) {
        Intent i = new Intent(requireContext(), DetailContactActivity.class);
        i.putExtra(DetailContactActivity.EXTRA_ID, c.id);
        startActivityForResult(i, REQ_ADD);
    }

    private void onItemDelete(Contact c) {
        Executors.newSingleThreadExecutor().execute(() -> {
            dao.delete(c);
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
                requireActivity().sendBroadcast(intent);
                Toast.makeText(requireContext(), "Deleted: " + c.name, Toast.LENGTH_SHORT).show();
                loadContacts();
            });
        });
    }

    private void onFavoriteToggle(Contact c) {
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean newFav = !c.isFavorite;
            long ts = System.currentTimeMillis();
            if (c.id > 0) {
                dao.updateFavorite(c.id, newFav ? 1 : 0, ts);
            } else {
                c.isFavorite = newFav;
                c.updatedAt = ts;
                dao.upsert(c);
            }
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
                requireActivity().sendBroadcast(intent);
                Toast.makeText(requireContext(), c.name + (newFav ? " added to favorites" : " removed from favorites"), Toast.LENGTH_SHORT).show();
                loadContacts();
            });
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabAddFrag) {
            startActivityForResult(new Intent(requireContext(), EditContactActivity.class), REQ_ADD);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADD && getActivity() != null && resultCode == getActivity().RESULT_OK) {
            loadContacts();
        }
    }
}
