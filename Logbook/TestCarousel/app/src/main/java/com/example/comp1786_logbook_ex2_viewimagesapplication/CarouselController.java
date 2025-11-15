package com.example.comp1786_logbook_ex2_viewimagesapplication;

import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Helper to wire a ViewPager2 as a coverflow carousel using ImagePagerAdapter and CoverFlowPageTransformer.
 */
public class CarouselController {
    private final ViewPager2 pager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable autoplayRunnable;
    private final int autoplayDelayMs;
    private boolean autoplayFlag;

    public CarouselController(@NonNull ViewPager2 pager, @NonNull ImagePagerAdapter adapter, @NonNull CoverFlowPageTransformer transformer,
                              boolean loop, boolean autoplay, int autoplayDelayMs, ImageButton btnPrev, ImageButton btnNext) {
        this.pager = pager;
        this.autoplayFlag = autoplay;
        this.autoplayDelayMs = Math.max(1000, autoplayDelayMs);

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        pager.setPageTransformer(transformer);

        // start near center so our adapter looping works (adapter repeats images)
        int imagesLen = adapter.getImagesLength();
        if (imagesLen > 0) {
            int centerStart = adapter.getItemCount() / 2;
            pager.setCurrentItem(centerStart - (centerStart % imagesLen), false);
        }

        // touch to pause autoplay; call performClick on ACTION_UP for accessibility
        pager.getChildAt(0).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (autoplayFlag) stopAutoplay();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return false;
        });

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                RecyclerView rv = (RecyclerView) pager.getChildAt(0);
                if (rv != null && rv.getLayoutManager() != null) {
                    View view = rv.getLayoutManager().findViewByPosition(position);
                    if (view != null) view.setFocusable(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_IDLE && autoplayFlag) scheduleAutoplay();
            }
        });

        if (btnPrev != null) btnPrev.setOnClickListener(v -> pager.setCurrentItem(pager.getCurrentItem() - 1, true));
        if (btnNext != null) btnNext.setOnClickListener(v -> pager.setCurrentItem(pager.getCurrentItem() + 1, true));

        pager.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                pager.setCurrentItem(pager.getCurrentItem() - 1, true);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                return true;
            }
            return false;
        });

        // initialize autoplayRunnable using a holder to avoid referencing the variable inside its initializer
        final Runnable[] holder = new Runnable[1];
        holder[0] = () -> {
            pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            handler.postDelayed(holder[0], CarouselController.this.autoplayDelayMs);
        };
        this.autoplayRunnable = holder[0];

        if (autoplayFlag) scheduleAutoplay();
    }

    public void scheduleAutoplay() {
        stopAutoplay();
        handler.postDelayed(autoplayRunnable, autoplayDelayMs);
    }

    public void stopAutoplay() {
        handler.removeCallbacks(autoplayRunnable);
    }

    public void destroy() {
        stopAutoplay();
    }
}
