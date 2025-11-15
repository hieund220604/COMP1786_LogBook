package com.example.comp1786_logbook_ex2_viewimagesapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.VH> {
    private final int[] images;
    private final int cardWidthPx;

    // A large multiplier so ViewPager appears infinite; keep within int range
    private static final int LOOP_COUNT = 1000;

    // Reflection configurable properties (defaults chosen to match prompt ranges)
    private int gapPx = 10; // original gap in px
    // Revert to original: reflection height equals source height
    private float reflectionHeightRatio = 1.0f; // reflection height equals source height
    private float topOpacity = 1.00f; // original top opacity
    private float blurEndPx = 3.0f; // reduce base blur (sharper)
    private float brightness = 1.10f; // slight boost (>1 increases perceived intensity; may clip to 255)

    public ImagePagerAdapter(int[] images) {
        this(images, -1);
    }

    public ImagePagerAdapter(int[] images, int cardWidthPx) {
        this.images = images;
        this.cardWidthPx = cardWidthPx; // if -1, we use the layout default dimen
    }

    // Optional setters so host can tune reflection behavior at runtime
    public void setGapPx(int gapPx) { this.gapPx = gapPx; }
    public void setReflectionHeightRatio(float ratio) { this.reflectionHeightRatio = ratio; }
    public void setTopOpacity(float topOpacity) { this.topOpacity = topOpacity; }
    public void setBlurEndPx(float endPx) { this.blurEndPx = endPx; }
    public void setBrightness(float brightness) { this.brightness = brightness; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        // Map the large adapter position to actual image index
        int realPos = position % images.length;

        // Determine safe target dimensions for the image to avoid huge bitmap allocation
        DisplayMetrics dm = holder.itemView.getResources().getDisplayMetrics();
        int screenW = dm.widthPixels;

        int targetW = 0, targetH = 0;
        if (cardWidthPx > 0) {
            targetW = cardWidthPx;
            targetH = (int) (cardWidthPx * (4f / 3f));
        } else if (holder.card.getWidth() > 0 && holder.card.getHeight() > 0) {
            targetW = holder.card.getWidth();
            targetH = holder.card.getHeight();
        } else if (holder.image.getWidth() > 0 && holder.image.getHeight() > 0) {
            targetW = holder.image.getWidth();
            targetH = holder.image.getHeight();
        } else {
            // fallback to a reasonable size (80% of screen width, 4:3)
            targetW = Math.max(100, (int) (screenW * 0.8f));
            targetH = (int) (targetW * (4f / 3f));
        }

        // Load a scaled bitmap from resources to fit target size (this prevents OOM for very large drawables)
        Bitmap srcBmp = null;
        try {
            srcBmp = loadScaledBitmapFromResource(holder.itemView.getResources(), images[realPos], targetW, targetH);
            if (srcBmp != null) {
                Log.d("ImagePagerAdapter", "Loaded scaled srcBmp w=" + srcBmp.getWidth() + " h=" + srcBmp.getHeight() + " for pos=" + realPos);
            } else {
                Log.d("ImagePagerAdapter", "Scaled decode returned null for pos=" + realPos);
            }
        } catch (Throwable t) {
            Log.w("ImagePagerAdapter", "loadScaledBitmapFromResource failed", t);
            srcBmp = null;
        }

        if (srcBmp != null) {
            holder.image.setImageBitmap(srcBmp);
        } else {
            // fallback to resource setter if decoding failed; this may still cause large drawables but preserves behavior
            holder.image.setImageResource(images[realPos]);
        }

        // Ensure reflection is presentational for accessibility
        holder.reflection.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        holder.reflection.setFocusable(false);
        holder.reflection.setClickable(false);

        // If a runtime card size was provided, apply it to the card so layout math matches
        if (cardWidthPx > 0) {
            ViewGroup.LayoutParams lp = holder.card.getLayoutParams();
            lp.width = cardWidthPx;
            // compute height preserving 3:4 aspect ratio (height = width * 4 / 3)
            lp.height = (int) (cardWidthPx * (4f / 3f));
            holder.card.setLayoutParams(lp);
        }

        // Make reflection follow clipping/rounded corners of source if set
        try {
            holder.reflection.setClipToOutline(holder.image.getClipToOutline());
        } catch (Throwable ignored) { }

        // Small-screen adaptive behavior: if screen width < 360dp, reduce reflection size/blur
        float screenDp = dm.widthPixels / dm.density;
        float effectiveReflectionRatio = reflectionHeightRatio;
        float effectiveBlurEnd = blurEndPx;
        if (screenDp < 360f) {
            effectiveReflectionRatio = Math.max(0.25f, reflectionHeightRatio * 0.6f); // reduce height
            effectiveBlurEnd = Math.max(0f, blurEndPx * 0.5f); // reduce blur
        }

        // Create reflection from the scaled bitmap (if available) or fallback to drawable handling
        try {
            if (srcBmp != null) {
                int reflectionHeight = Math.max(1, (int) (srcBmp.getHeight() * effectiveReflectionRatio));
                Bitmap reflected = createReflectionBitmapFromBitmap(srcBmp, reflectionHeight, topOpacity, brightness);
                if (reflected != null) {
                    Log.d("ImagePagerAdapter", "Reflection created w=" + reflected.getWidth() + " h=" + reflected.getHeight() + " for pos=" + realPos);
                    holder.reflection.setImageBitmap(reflected);
                    holder.reflection.setTranslationY(gapPx);
                    holder.reflection.setScaleY(1f);

                    // Compute blur radius as 20% of reflection height (original behavior)
                    float blurRadiusPx = Math.max(0f, reflected.getHeight() * 0.2f);
                     // clamp to reasonable range
                     blurRadiusPx = Math.max(0f, Math.min(25f, blurRadiusPx));
                     Log.d("ImagePagerAdapter", "Applying blurRadiusPx=" + blurRadiusPx + " for reflection pos=" + realPos);

                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                         try {
                             holder.reflection.setRenderEffect(android.graphics.RenderEffect.createBlurEffect(blurRadiusPx, blurRadiusPx, Shader.TileMode.CLAMP));
                         } catch (Throwable t) {
                             Log.e("ImagePagerAdapter", "RenderEffect blur failed", t);
                         }
                     } else {
                         // Fallback: a cheap blur approximation by downscaling and upscaling the reflection bitmap
                         try {
                             Bitmap fallbackBlur = fastDownscaleBlur(reflected, blurRadiusPx);
                             if (fallbackBlur != null) {
                                 // replace the reflection bitmap with the blurred version and recycle the original
                                 holder.reflection.setImageBitmap(fallbackBlur);
                                 if (!reflected.isRecycled()) reflected.recycle();
                                 reflected = fallbackBlur;
                             }
                         } catch (Throwable t) {
                             Log.w("ImagePagerAdapter", "fallback blur failed", t);
                         }
                     }

                     holder.image.post(() -> {
                        try {
                            holder.image.setPivotX(holder.image.getWidth() / 2f);
                            holder.image.setPivotY(holder.image.getHeight());
                            holder.reflection.setPivotX(holder.image.getWidth() / 2f);
                            holder.reflection.setPivotY(0f);

                            holder.reflection.setRotationX(-holder.image.getRotationX());
                            holder.reflection.setRotationY(holder.image.getRotationY());
                            holder.reflection.setRotation(holder.image.getRotation());
                            holder.reflection.setScaleX(holder.image.getScaleX());
                            holder.reflection.setScaleY(holder.image.getScaleY());
                            holder.reflection.setCameraDistance(holder.image.getCameraDistance());
                        } catch (Throwable t) {
                            Log.w("ImagePagerAdapter", "mirror transform setup failed", t);
                        }
                    });
                }
            } else {
                // Drawable fallback path (older behavior) - keep but flipped
                Drawable srcDrawable = holder.image.getDrawable();
                if (srcDrawable != null) {
                    // determine source dimensions (use drawable intrinsic if view size not available yet)
                    int srcW = srcDrawable.getIntrinsicWidth() > 0 ? srcDrawable.getIntrinsicWidth() : holder.image.getWidth();
                    int srcH = srcDrawable.getIntrinsicHeight() > 0 ? srcDrawable.getIntrinsicHeight() : holder.image.getHeight();
                    if (srcW <= 0) srcW = holder.card.getWidth() > 0 ? holder.card.getWidth() : 400;
                    if (srcH <= 0) srcH = holder.card.getHeight() > 0 ? holder.card.getHeight() : (int)(srcW * (4f/3f));

                    int reflectionHeight = Math.max(1, (int) (srcH * effectiveReflectionRatio));
                    Bitmap reflected = createReflectionBitmap(srcDrawable, srcW, srcH, reflectionHeight, topOpacity, brightness, effectiveBlurEnd);
                    if (reflected != null) {
                        holder.reflection.setImageBitmap(reflected);
                        holder.reflection.setTranslationY(gapPx);
                        holder.reflection.setScaleY(1f);
                    } else {
                        holder.reflection.setImageDrawable(srcDrawable);
                        holder.reflection.setScaleY(-1f);
                        holder.reflection.setAlpha(0.35f);
                        // convert gap from dp-like value to pixels using display density
                        float density = holder.itemView.getResources().getDisplayMetrics().density;
                        int gapPxPx = (int) (gapPx * density + 0.5f);
                        holder.reflection.setTranslationY(gapPxPx);
                    }
                }
            }
        } catch (Throwable t) {
            Log.e("ImagePagerAdapter", "reflection creation failed", t);
        }

        holder.itemView.setContentDescription(holder.itemView.getContext().getString(R.string.image_content_description) + " " + (realPos + 1));
    }

    @Override
    public int getItemCount() {
        // Safely handle null images array
        if (images == null) return 0;
        // If only one image, don't loop
        if (images.length <= 1) return images.length;
        return images.length * LOOP_COUNT; // large number to simulate infinite scrolling
    }

    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);
        try {
            // clear drawables to break references to large bitmaps
            holder.image.setImageDrawable(null);
            holder.reflection.setImageDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                try {
                    holder.reflection.setRenderEffect(null);
                } catch (Throwable ignored) { }
            }
        } catch (Throwable t) {
            Log.w("ImagePagerAdapter", "onViewRecycled cleanup failed", t);
        }
    }

    public static final class VH extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView reflection;
        CardView card;
        public VH(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.itemImage);
            reflection = v.findViewById(R.id.reflection);
            card = v.findViewById(R.id.card);
        }
    }

    // Expose number of distinct images so external controllers can compute starting indices without accessing private field
    public int getImagesLength() {
        return images == null ? 0 : images.length;
    }

    // New: create reflection from Bitmap source (avoids creating extra huge intermediate bitmaps)
    private Bitmap createReflectionBitmapFromBitmap(Bitmap srcBmp, int reflectionHeightPx, float topOpacity, float brightness) {
        try {
            if (srcBmp == null) return null;
            int srcW = srcBmp.getWidth();
            int srcH = srcBmp.getHeight();
            int cropY = Math.max(0, srcH - reflectionHeightPx);
            Bitmap cropped = Bitmap.createBitmap(srcBmp, 0, cropY, Math.max(1, srcW), Math.max(1, reflectionHeightPx));

            Matrix flip = new Matrix();
            // Flip vertically (up-down) so the reflection is inverted vertically (like a water reflection)
            flip.preScale(1f, -1f);
            Bitmap flipped = Bitmap.createBitmap(cropped, 0, 0, cropped.getWidth(), cropped.getHeight(), flip, true);

            Bitmap out = Bitmap.createBitmap(flipped.getWidth(), flipped.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(out);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

            if (brightness != 1f) {
                ColorMatrix cm = new ColorMatrix(new float[] {
                        brightness, 0, 0, 0, 0,
                        0, brightness, 0, 0, 0,
                        0, 0, brightness, 0, 0,
                        0, 0, 0, 1, 0
                });
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
            }

            canvas.drawBitmap(flipped, 0, 0, paint);

            Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            int height = out.getHeight();
            int topAlpha = Math.max(0, Math.min(255, (int) (topOpacity * 255f)));
            LinearGradient lg = new LinearGradient(0, 0, 0, height,
                    Color.argb(topAlpha, 255, 255, 255), Color.argb(0, 255, 255, 255), Shader.TileMode.CLAMP);
            maskPaint.setShader(lg);
            maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawRect(new Rect(0,0,out.getWidth(), out.getHeight()), maskPaint);

            // Clean up intermediates
            if (!cropped.isRecycled()) cropped.recycle();
            if (!flipped.isRecycled()) flipped.recycle();

            return out;
        } catch (Throwable t) {
            Log.e("ImagePagerAdapter", "createReflectionBitmapFromBitmap failed", t);
            return null;
        }
    }

    // Fast cheap blur fallback: downscale then upscale to approximate blur. Not a true Gaussian but inexpensive.
    private static Bitmap fastDownscaleBlur(Bitmap src, float blurPx) {
        try {
            if (src == null) return null;
            // Compute scale factor: higher blurPx -> smaller scale. Clamp between 0.05 and 0.5 to avoid degenerate sizes.
            float f = 1f / (1f + (blurPx / Math.max(1f, Math.min(src.getHeight(), src.getWidth())) * 10f));
            f = Math.max(0.05f, Math.min(0.5f, f));
            int smallW = Math.max(1, (int) (src.getWidth() * f));
            int smallH = Math.max(1, (int) (src.getHeight() * f));
            Bitmap small = Bitmap.createScaledBitmap(src, smallW, smallH, true);
            Bitmap up = Bitmap.createScaledBitmap(small, src.getWidth(), src.getHeight(), true);
            if (!small.isRecycled()) small.recycle();
            return up;
        } catch (OutOfMemoryError oom) {
            Log.w("ImagePagerAdapter", "fastDownscaleBlur OOM", oom);
            return null;
        } catch (Throwable t) {
            Log.w("ImagePagerAdapter", "fastDownscaleBlur failed", t);
            return null;
        }
    }

    // Utility: generate a vertically flipped reflection bitmap from a drawable, preserving alpha and corners.
    private Bitmap createReflectionBitmap(Drawable srcDrawable, int srcW, int srcH, int reflectionHeightPx, float topOpacity, float brightness, float blurEndPx) {
        try {
            // Render drawable to bitmap at desired size
            Bitmap srcBmp = drawableToBitmap(srcDrawable, srcW, srcH);
            if (srcBmp == null) return null;

            // Crop the bottom portion of the source to use as reflection source
            int cropY = Math.max(0, srcH - reflectionHeightPx);
            Bitmap cropped = Bitmap.createBitmap(srcBmp, 0, cropY, srcW, reflectionHeightPx);

            // Flip vertically
            Matrix flip = new Matrix();
            // Flip vertically (up-down) to act like a vertical reflection
            flip.preScale(1f, -1f);
            Bitmap flipped = Bitmap.createBitmap(cropped, 0, 0, cropped.getWidth(), cropped.getHeight(), flip, true);

            // Prepare output bitmap with alpha preserved
            Bitmap out = Bitmap.createBitmap(flipped.getWidth(), flipped.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(out);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

            // Apply brightness via ColorMatrix
            if (brightness != 1f) {
                ColorMatrix cm = new ColorMatrix(new float[] {
                        brightness, 0, 0, 0, 0,
                        0, brightness, 0, 0, 0,
                        0, 0, brightness, 0, 0,
                        0, 0, 0, 1, 0
                });
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
            }

            // Draw the flipped image first
            canvas.drawBitmap(flipped, 0, 0, paint);

            // Create a vertical alpha gradient mask from topOpacity -> 0
            Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            int height = out.getHeight();
            int topAlpha = Math.max(0, Math.min(255, (int) (topOpacity * 255f)));
            LinearGradient lg = new LinearGradient(0, 0, 0, height,
                    Color.argb(topAlpha, 255, 255, 255), Color.argb(0, 255, 255, 255), Shader.TileMode.CLAMP);
            maskPaint.setShader(lg);
            maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawRect(new Rect(0,0,out.getWidth(), out.getHeight()), maskPaint);

            // Note: RenderEffect blur is applied at the view level in onBindViewHolder for API31+.

            // Clean up intermediates
            cropped.recycle();
            flipped.recycle();
            if (!srcBmp.isRecycled()) srcBmp.recycle();

            return out;
        } catch (Throwable t) {
            Log.e("ImagePagerAdapter", "createReflectionBitmap failed", t);
            return null;
        }
    }

    private static Bitmap drawableToBitmap(Drawable drawable, int reqW, int reqH) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
            if (bmp.getWidth() == reqW && bmp.getHeight() == reqH) return bmp;
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(Math.max(1, reqW), Math.max(1, reqH), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError oom) {
            Log.e("ImagePagerAdapter", "drawableToBitmap OOM", oom);
            return null;
        }
    }

    // New: load a downsampled bitmap from resources to target size safely.
    private static Bitmap loadScaledBitmapFromResource(Resources res, int resId, int targetW, int targetH) {
        if (res == null) return null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, opts);
        int srcW = opts.outWidth;
        int srcH = opts.outHeight;
        if (srcW <= 0 || srcH <= 0) return null;

        // Safety cap: avoid creating bitmaps with extremely large pixel counts.
        final long MAX_PIXELS = 4_000_000; // ~4MP -> ~16MB ARGB_8888
        long srcPixels = (long) srcW * (long) srcH;
        float scaleForPixels = srcPixels > MAX_PIXELS ? (float) Math.sqrt((double) MAX_PIXELS / (double) srcPixels) : 1f;

        // Desired scale to fit target dimensions
        float scaleW = targetW > 0 ? (float) targetW / (float) srcW : 1f;
        float scaleH = targetH > 0 ? (float) targetH / (float) srcH : 1f;
        float desiredScale = Math.min(scaleW, scaleH);

        // Use the smaller of desiredScale and pixel-safety scale
        float finalScale = Math.min(desiredScale, scaleForPixels);
        if (finalScale <= 0f) finalScale = 0.1f;

        // Calculate inSampleSize (power of 2) for efficient decoding
        int inSampleSize = 1;
        while ((srcW / inSampleSize) * (srcH / inSampleSize) > MAX_PIXELS) {
            inSampleSize *= 2;
        }

        // Also try to make decoded size >= target to avoid upscaling after decode
        int approxW = Math.max(1, srcW / inSampleSize);
        int approxH = Math.max(1, srcH / inSampleSize);

        opts = new BitmapFactory.Options();
        opts.inSampleSize = inSampleSize;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap decoded = null;
        try {
            decoded = BitmapFactory.decodeResource(res, resId, opts);
            if (decoded == null) return null;

            // If decoded is still larger than final desired, scale down preserving aspect
            int finalW = Math.max(1, (int) Math.round(srcW * finalScale));
            int finalH = Math.max(1, (int) Math.round(srcH * finalScale));

            if (decoded.getWidth() != finalW || decoded.getHeight() != finalH) {
                Bitmap scaled = Bitmap.createScaledBitmap(decoded, finalW, finalH, true);
                if (scaled != decoded) decoded.recycle();
                decoded = scaled;
            }
            return decoded;
        } catch (OutOfMemoryError oom) {
            Log.e("ImagePagerAdapter", "decodeResource OOM", oom);
            if (decoded != null && !decoded.isRecycled()) decoded.recycle();
            return null;
        }
    }
}
