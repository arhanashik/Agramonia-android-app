package com.blackspider.util.helper;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageLoader {
    public static void load(Context context, ImageView imageView, int imageResource){
        Glide.with(context)
                .load(imageResource)
                .into(imageView);
    }
}
