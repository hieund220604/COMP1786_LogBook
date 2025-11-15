package com.example.comp1786_logbook_ex3_connectdatabaseapplication.helper;

import android.content.Context;
import androidx.annotation.ArrayRes;

import java.util.ArrayList;
import java.util.List;

public class ResUtils {
    public static int nameToDrawable(Context ctx, String name) {
        if (name == null) return 0;
        return ctx.getResources().getIdentifier(name, "drawable", ctx.getPackageName());
    }

    public static List<String> avatarNamesFromArray(Context ctx, @ArrayRes int arrRes) {
        List<String> out = new ArrayList<>();
        android.content.res.TypedArray ta = ctx.getResources().obtainTypedArray(arrRes);
        for (int i = 0; i < ta.length(); i++) {
            int resId = ta.getResourceId(i, 0);
            if (resId != 0) out.add(ctx.getResources().getResourceEntryName(resId));
        }
        ta.recycle();
        return out;
    }
}

