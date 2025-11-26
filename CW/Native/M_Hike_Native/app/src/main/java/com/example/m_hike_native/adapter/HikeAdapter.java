package com.example.m_hike_native.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike_native.R;
import com.example.m_hike_native.data.HikeDatabaseHelper;
import com.example.m_hike_native.model.Hike;

import java.util.ArrayList;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.VH> {
    ArrayList<Hike> list; Context ctx; HikeDatabaseHelper db;

    public HikeAdapter(ArrayList<Hike> list, Context ctx) {
        this.list = list; this.ctx = ctx; db = new HikeDatabaseHelper(ctx);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hike, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (position < 0 || position >= list.size()) return;

        Hike h = list.get(position);
        holder.tvName.setText(h.getName());
        holder.tvLocation.setText(h.getLocation());

        // Set additional fields
        if (holder.tvDate != null) {
            holder.tvDate.setText(h.getDate() != null ? h.getDate() : "");
        }
        if (holder.tvLength != null) {
            holder.tvLength.setText(String.format("%.1f km", h.getLength()));
        }
        if (holder.tvDifficulty != null) {
            holder.tvDifficulty.setText(h.getDifficulty() != null ? h.getDifficulty() : "");
        }
        if (holder.tvParking != null) {
            holder.tvParking.setText(h.isParking() ? "Parking" : "No Parking");
        }
        if (holder.tvElevation != null) {
            holder.tvElevation.setText(String.format("%.0f m", h.getElevation()));
        }
        if (holder.tvDuration != null) {
            int mins = h.getDurationMinutes();
            if (mins <= 0) {
                holder.tvDuration.setText("");
            } else {
                int hrs = mins / 60;
                int rem = mins % 60;
                if (hrs > 0) holder.tvDuration.setText(String.format("%dh %02dm", hrs, rem));
                else holder.tvDuration.setText(String.format("%dm", rem));
            }
        }

        holder.itemView.setOnClickListener(v -> {
            try {
                int pos = holder.getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) pos = position;
                if (pos < 0 || pos >= list.size()) return;
                Hike cur = list.get(pos);
                Intent i = new Intent(ctx, com.example.m_hike_native.ui.AddObservationActivity.class);
                i.putExtra("hike_id", cur.getId());
                i.putExtra("hike_name", cur.getName());
                ctx.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            try {
                int pos = holder.getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) pos = position;
                if (pos < 0 || pos >= list.size()) return;
                Hike cur = list.get(pos);
                Intent i = new Intent(ctx, com.example.m_hike_native.ui.EditHikeActivity.class);
                i.putExtra("hike_id", cur.getId());
                ctx.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            try {
                int pos = holder.getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) pos = position;
                if (pos < 0 || pos >= list.size()) return;
                Hike cur = list.get(pos);
                final int idx = pos;
                final int hikeId = cur.getId();
                new AlertDialog.Builder(ctx)
                        .setMessage(ctx.getString(R.string.confirm_delete))
                        .setPositiveButton(ctx.getString(R.string.delete), (dialog, which) -> {
                            try {
                                int rows = db.deleteHike(hikeId);
                                if (rows > 0 && idx < list.size()) {
                                    list.remove(idx);
                                    notifyItemRemoved(idx);
                                    Toast.makeText(ctx, "Deleted", Toast.LENGTH_SHORT).show();
                                } else Toast.makeText(ctx, "Delete failed", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .setNegativeButton(ctx.getString(R.string.cancel), null)
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

    public void cleanup() {
        if (db != null) {
            try {
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateList(ArrayList<Hike> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvLocation, tvDate, tvLength, tvDifficulty, tvParking;
        TextView tvElevation, tvDuration;
        Button btnDelete, btnEdit;
        public VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLength = itemView.findViewById(R.id.tvLength);
            tvElevation = itemView.findViewById(R.id.tvElevation);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvParking = itemView.findViewById(R.id.tvParking);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
