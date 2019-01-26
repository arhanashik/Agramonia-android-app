package com.workfort.apps.util.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.workfort.apps.agramonia.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.Nullable;

public class ImageLoader {
    public static void load(Context context, ImageView imageView, int imageResource){
        Glide.with(context)
                .load(imageResource)
                .into(imageView);
    }

    public static void load(Context context, ImageView imageView, String imgUrl) {
        RequestOptions requestOptions = RequestOptions
                .placeholderOf(R.drawable.ic_user_avatar)
                .error(R.drawable.ic_user_avatar);
        Glide.with(context)
                .load(imgUrl)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }
}
