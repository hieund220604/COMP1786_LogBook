package com.example.comp1786_logbook_ex2_viewimagesapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Simple RecyclerView.Adapter for ViewPager2 that shows an ImageView per page.
 * This adapter expects there to be drawable resources in the project named image1..image5
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.VH> {
    private final Context context;
    private final int[] images;

    public ImageAdapter(Context context, int[] images) {
        this.context = context;
        this.images = images == null ? new int[0] : images;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (images.length == 0) return;
        int idx = position % images.length;
        holder.image.setImageResource(images[idx]);
    }

    @Override
    public int getItemCount() {
        if (images.length <= 1) return images.length;
        // small loop multiplier to allow swiping both directions
        return images.length * 1000;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
        }
    }
}
