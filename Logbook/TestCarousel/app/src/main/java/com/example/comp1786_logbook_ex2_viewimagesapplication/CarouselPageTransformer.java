package com.example.comp1786_logbook_ex2_viewimagesapplication;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * CarouselPageTransformer creates a 3D carousel-like effect.
 * Center page: scale 1.0, alpha 1.0, rotationY 0
 * Adjacent pages: scaled down (0.85f), alpha reduced (0.7f), rotated along Y-axis
 * Transforms are interpolated smoothly during scroll.
 */
public class CarouselPageTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.7f;
    private static final float MAX_ROTATION = 25f; // degrees around Y-axis

    @Override
    public void transformPage(@NonNull View page, float position) {
        // position: [-inf, +inf], centered page is 0, left is negative, right is positive
        float absPos = Math.abs(position);

        if (absPos >= 1) {
            // Page is off-screen to the left or right.
            page.setAlpha(MIN_ALPHA);
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setRotationY(position < 0 ? MAX_ROTATION : -MAX_ROTATION);
            page.setTranslationX(0f);
        } else {
            // Page is visible - interpolate scale, alpha and rotation
            // Scale interpolates between MIN_SCALE and 1.0
            float scale = MIN_SCALE + (1f - MIN_SCALE) * (1f - absPos);
            page.setScaleX(scale);
            page.setScaleY(scale);

            // Alpha interpolates between MIN_ALPHA and 1.0
            float alpha = MIN_ALPHA + (1f - MIN_ALPHA) * (1f - absPos);
            page.setAlpha(alpha);

            // RotationY: angle should be MAX_ROTATION * position (so left pages rotate positive, right negative)
            float rotation = -position * MAX_ROTATION;
            page.setRotationY(rotation);

            // Slight translation to enhance perspective (push side pages slightly inward)
            float translationX = position * page.getWidth() * 0.2f;
            page.setTranslationX(translationX);
        }

        // Ensure pivot for rotation is vertical center
        page.setPivotY(page.getHeight() * 0.5f);
        page.setPivotX(page.getWidth() * 0.5f);
    }
}

