package com.example.m_hike_native.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike_native.R;
import com.example.m_hike_native.model.Observation;

import java.util.ArrayList;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.VH> {
    ArrayList<Observation> list; Context ctx;

    public ObservationAdapter(ArrayList<Observation> list, Context ctx) { this.list = list; this.ctx = ctx; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_observation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Observation o = list.get(position);
        holder.tvText.setText(o.getObservation());
        holder.tvTime.setText(o.getTime());
        holder.tvComments.setText(o.getComments());
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvText, tvTime, tvComments;
        public VH(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvObsText);
            tvTime = itemView.findViewById(R.id.tvObsTime);
            tvComments = itemView.findViewById(R.id.tvObsComments);
        }
    }
}

