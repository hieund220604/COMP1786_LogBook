package com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp1786_logbook_ex3_connectdatabaseapplication.R;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.helper.ResUtils;

import java.util.List;

public class AvatarGridAdapter extends RecyclerView.Adapter<AvatarGridAdapter.VH> {

    private final List<String> names;
    private final OnPick onPick;
    private String selectedName;

    public interface OnPick { void onPick(String name); }

    public AvatarGridAdapter(List<String> names, OnPick onPick) {
        this.names = names;
        this.onPick = onPick;
        this.selectedName = null;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_avatar, parent, false);
        return new VH(v, onPick);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String name;
        if (position < names.size()) {
            name = names.get(position);
        } else {
            name = "upload";
        }
        boolean isSelected = selectedName != null && selectedName.equals(name);
        holder.bind(name, isSelected, () -> {
            this.selectedName = name;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() { return names.size() + 1; } // Add 1 for the upload button

    public static class VH extends RecyclerView.ViewHolder {
        ImageView img; OnPick onPick;
        View root;
        public VH(View v, OnPick onPick) {
            super(v);
            img = v.findViewById(R.id.imgAvatar);
            this.onPick = onPick;
            this.root = v;
        }
        public void bind(String name, boolean isSelected, Runnable onSelectChanged) {
            if (name.equals("upload")) {
                img.setImageResource(R.drawable.ic_add_a_photo); // A new drawable for upload
                root.setSelected(false);
                root.setOnClickListener(v -> onPick.onPick("upload"));
            } else {
                int res = ResUtils.nameToDrawable(img.getContext(), name);
                if (res != 0) img.setImageResource(res);
                root.setSelected(isSelected);
                root.setOnClickListener(v -> {
                    onSelectChanged.run();
                    onPick.onPick(name);
                });
            }
        }
    }
}
