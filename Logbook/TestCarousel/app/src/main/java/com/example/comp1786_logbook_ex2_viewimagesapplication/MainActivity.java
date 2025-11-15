package com.example.comp1786_logbook_ex2_viewimagesapplication;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * MainActivity that uses the project's existing ImagePagerAdapter (which creates reflections, sizing, etc.)
 * and the CoverFlowPageTransformer to preserve the original 3D effect and image sizing.
 * The toolbar was removed per user request.
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private FloatingActionButton btnBackward;
    private FloatingActionButton btnForward;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        btnBackward = findViewById(R.id.btnBackward);
        btnForward = findViewById(R.id.btnForward);

        // Use all drawable images present in res/drawable (image1..image9)
        final int[] images = new int[] {
                R.drawable.image1,
                R.drawable.image2,
                R.drawable.image3,
                R.drawable.image4,
                R.drawable.image5,
                R.drawable.image6,
                R.drawable.image7,
                R.drawable.image8,
                R.drawable.image9
        };

        // Use the existing, more advanced adapter in this project which preserves image sizing and reflections
        int cardWidthPx = getResources().getDimensionPixelSize(R.dimen.card_width);
        ImagePagerAdapter adapter = new ImagePagerAdapter(images, cardWidthPx);
        viewPager.setAdapter(adapter);

        // Preserve several offscreen pages for smoothness
        viewPager.setOffscreenPageLimit(3);

        // Allow adjacent pages to peek
        int peek = getResources().getDimensionPixelSize(R.dimen.pager_peek);
        viewPager.setPadding(peek, 0, peek, 0);
        RecyclerView rv = (RecyclerView) viewPager.getChildAt(0);
        if (rv != null) {
            rv.setClipToPadding(false);
            rv.setClipChildren(false);
            rv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        // Use the original 3D coverflow transformer available in the project
        viewPager.setPageTransformer(new CoverFlowPageTransformer());

        // Set up button click listeners
        btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem > 0) {
                    viewPager.setCurrentItem(currentItem - 1, true);
                }
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if (adapter != null && currentItem < adapter.getItemCount() - 1) {
                    viewPager.setCurrentItem(currentItem + 1, true);
                }
            }
        });
    }
}
