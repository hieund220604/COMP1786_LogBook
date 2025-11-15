package com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp1786_logbook_ex3_connectdatabaseapplication.R;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.helper.ResUtils;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.Contact;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.VH> {

    private final List<Contact> items = new ArrayList<>();
    private final OnItemClick onItemClick;
    private final OnItemDelete onItemDelete;
    private final OnFavoriteToggle onFavoriteToggle;

    private boolean selectionMode = false;
    private final Set<Long> selectedIds = new HashSet<>();

    public interface OnItemClick { void onClick(Contact c); }
    public interface OnItemDelete { void onDelete(Contact c); }
    public interface OnFavoriteToggle { void onToggle(Contact c); }

    public ContactsAdapter(OnItemClick cb, OnItemDelete delCb, OnFavoriteToggle favCb) {
        this.onItemClick = cb;
        this.onItemDelete = delCb;
        this.onFavoriteToggle = favCb;
    }

    public void setItems(List<Contact> list) {
        items.clear();
        if (list != null) items.addAll(list);
        // Clear selection when data set changes
        selectedIds.clear();
        selectionMode = false;
        notifyDataSetChanged();
    }

    public void setSelectionMode(boolean enabled) {
        if (selectionMode != enabled) {
            selectionMode = enabled;
            if (!enabled) {
                selectedIds.clear();
            }
            notifyDataSetChanged();
        }
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public Set<Long> getSelectedIds() {
        return new HashSet<>(selectedIds);
    }

    public void toggleSelection(long id) {
        if (selectedIds.contains(id)) {
            selectedIds.remove(id);
        } else {
            selectedIds.add(id);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        VH vh = new VH(v, onItemClick, onFavoriteToggle, selectedIds, this);
        // attach delete callback as tag so the static VH can access it
        v.setTag(onItemDelete);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(items.get(position), selectionMode, selectedIds.contains(items.get(position).id));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txtName, txtEmail;
        View btnDelete;
        ImageView imgFavorite;
        CheckBox cbSelect;
        OnItemClick cb;
        OnFavoriteToggle favCb;
        Set<Long> selectedIds;
        ContactsAdapter adapter;

        public VH(@NonNull View itemView, OnItemClick cb, OnFavoriteToggle favCb, Set<Long> selectedIds, ContactsAdapter adapter) {
            super(itemView);
            img = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            this.cb = cb;
            this.favCb = favCb;
            this.selectedIds = selectedIds;
            this.adapter = adapter;
        }
        public void bind(Contact c, boolean selectionMode, boolean isSelected) {
            // Handle uploaded images (file:// or content://) and built-in avatars
            if (c.avatarName != null &&
                (c.avatarName.startsWith("content://") || c.avatarName.startsWith("file://"))) {
                try {
                    img.setImageURI(android.net.Uri.parse(c.avatarName));
                } catch (SecurityException e) {
                    android.util.Log.e("ContactsAdapter", "SecurityException loading image: " + c.avatarName, e);
                    // Fall back to default avatar
                    img.setImageResource(R.drawable.avatar_1);
                } catch (Exception e) {
                    android.util.Log.e("ContactsAdapter", "Error loading image: " + c.avatarName, e);
                    img.setImageResource(R.drawable.avatar_1);
                }
            } else {
                int res = ResUtils.nameToDrawable(itemView.getContext(), c.avatarName);
                if (res != 0) img.setImageResource(res);
            }

            txtName.setText(c.name);
            txtEmail.setText(c.email);

            // Favorite icon - set click listener BEFORE card click to prevent propagation
            if (imgFavorite != null && favCb != null) {
                imgFavorite.setImageResource(c.isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_border);
                imgFavorite.setOnClickListener(v -> {
                    try {
                        android.util.Log.d("ContactsAdapter", "imgFavorite clicked for id=" + c.id + ", name=" + c.name + ", isFavorite=" + c.isFavorite);
                        // Disable rapid double taps
                        v.setEnabled(false);
                        v.postDelayed(() -> v.setEnabled(true), 300);

                        // Delegate the actual toggle to the activity/fragment
                        favCb.onToggle(c);
                    } catch (Exception ex) {
                        android.util.Log.e("ContactsAdapter", "Error toggling favorite", ex);
                    }
                });
            }

            // Selection checkbox visibility/state
            if (cbSelect != null) {
                cbSelect.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
                cbSelect.setChecked(isSelected);
                cbSelect.setOnClickListener(v -> {
                    // Toggle selection state
                    if (selectedIds.contains(c.id)) {
                        selectedIds.remove(c.id);
                    } else {
                        selectedIds.add(c.id);
                    }
                    adapter.notifyDataSetChanged();
                });
            }

            // Card click - set AFTER other click listeners
            View cardContainer = itemView.findViewById(R.id.cardContainer);
            if (cardContainer != null) {
                cardContainer.setOnClickListener(v -> cb.onClick(c));
            } else {
                itemView.setOnClickListener(v -> cb.onClick(c));
            }

            itemView.setOnLongClickListener(v -> {
                // Long press can be handled by activity via adapter state; no-op here
                return false;
            });

            btnDelete.setOnClickListener(v -> {
                Object tag = itemView.getTag();
                if (tag instanceof OnItemDelete) {
                    ((OnItemDelete) tag).onDelete(c);
                }
            });
        }
    }
}
