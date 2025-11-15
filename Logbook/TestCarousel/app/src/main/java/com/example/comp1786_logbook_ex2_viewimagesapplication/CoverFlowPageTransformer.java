package com.example.comp1786_logbook_ex2_viewimagesapplication;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Configurable 3D coverflow-style PageTransformer for ViewPager2.
 *
 * Usage:
 * viewPager.setOffscreenPageLimit(3);
 * viewPager.setPageTransformer(new CoverFlowPageTransformer(...));
 *
 * Requires pages to have an ImageView with id R.id.itemImage (matches pager_item layout).
 */
public class CoverFlowPageTransformer implements ViewPager2.PageTransformer {
    private final float rotationDegrees; // e.g., 30-40
    private final float neighborPeekPercent; // percent of width visible for neighbor (0.2 - 0.35)
    private final float neighborBlurPx; // 2-4
    private final float neighborOpacity; // 0.4-0.65
    private final float centerScaleMax; // 1.0-1.08
    private final int perspectiveDistance; // camera distance/perspective

    public CoverFlowPageTransformer() {
        this(30f, 0.28f, 3f, 0.55f, 1.04f, 1000);
    }

    public CoverFlowPageTransformer(float rotationDegrees, float neighborPeekPercent, float neighborBlurPx, float neighborOpacity, float centerScaleMax, int perspectiveDistance) {
        this.rotationDegrees = rotationDegrees;
        this.neighborPeekPercent = neighborPeekPercent;
        this.neighborBlurPx = neighborBlurPx;
        this.neighborOpacity = neighborOpacity;
        this.centerScaleMax = centerScaleMax;
        this.perspectiveDistance = perspectiveDistance;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        // position: 0 is centered page. -1 is one page to the left, +1 to the right
        float absPos = Math.abs(position);

        // set camera distance for perspective
        page.setCameraDistance(perspectiveDistance * page.getResources().getDisplayMetrics().density);

        // compute visibility / peeking translation
        int pageWidth = page.getWidth();
        float peekOffset = pageWidth * neighborPeekPercent;

        // scale: center page scales up slightly
        float scale = 1f;
        if (absPos <= 1f) {
            float scaleFactor = 1f + (centerScaleMax - 1f) * (1f - absPos);
            scale = scaleFactor;
        } else {
            scale = 1f;
        }

        // rotation on Y for 3D coverflow
        float rot = rotationDegrees * position;

        // translationX to make neighbors peek
        float translationX = -position * (peekOffset);

        // z ordering via translationZ (pages behind center have lower translationZ)
        float translationZ = (1f - absPos) * 200f; // front pages have higher z

        page.setTranslationX(translationX);
        page.setTranslationZ(translationZ);
        page.setScaleX(scale);
        page.setScaleY(scale);
        page.setRotationY(rot);

        // Opacity and blur/desaturation for neighbors
        ImageView img = page.findViewById(R.id.itemImage);
        if (img != null) {
            if (absPos >= 1f) {
                // fully off-center: hide
                page.setAlpha(0f);
                clearEffects(img);
            } else if (absPos > 0f) {
                // neighbor
                float op = 1f - (1f - neighborOpacity) * (1f - (1f - absPos));
                page.setAlpha(op);

                // apply blur on API31+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    float blur = neighborBlurPx * absPos; // reduce blur closer to center
                    img.setRenderEffect(RenderEffect.createBlurEffect(blur, blur, Shader.TileMode.CLAMP));
                }

                // desaturate slightly
                float saturation = 1f - 0.18f * (1f - absPos); // ~10-25% desat depending on position
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(saturation);
                img.setColorFilter(new ColorMatrixColorFilter(cm));

                // remove drop shadow from neighbors if any
                page.setElevation(4f * (1f - absPos));
            } else {
                // center page
                page.setAlpha(1f);
                clearEffects(img);
                page.setElevation(12f);
            }
        } else {
            // fallback: just adjust alpha
            page.setAlpha(1f - Math.min(0.6f, absPos * 0.6f));
        }
    }

    private void clearEffects(ImageView img) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            img.setRenderEffect(null);
        }
        img.clearColorFilter();
    }
}

