package com.workfort.apps.util.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import com.workfort.apps.AgramoniaApp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageUtil {
    public static String base64Encode(String filePath){
        String encodeString = null;
        try{
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bt = bos.toByteArray();
            encodeString = Base64.encodeToString(bt, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }

        return encodeString;
    }

    public static String getPath(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public static MultipartBody.Part getMultiPartBody(Uri uri) {
        Context ctx = AgramoniaApp.Companion.getBaseApplicationContext();
        String path = ImageUtil.getPath(ctx, uri);
        String mediaTypeStr = ctx.getContentResolver().getType(uri);

        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(mediaTypeStr)) {
            MediaType mediaType = MediaType.parse(mediaTypeStr);
            RequestBody requestBody = RequestBody.create(mediaType, new File(path));
            return MultipartBody.Part.createFormData("file", path, requestBody);
        }

        return null;
    }

    public static ArrayList<MultipartBody.Part> getMultiPartBody(List<Uri> uri) {
        ArrayList<MultipartBody.Part> multipartBodyParts = new ArrayList<>();

        int fileNo = 0;
        for (int i = 0; i < uri.size(); i++) {
            Context ctx = AgramoniaApp.Companion.getBaseApplicationContext();
            String path = ImageUtil.getPath(ctx, uri.get(i));
            String mediaTypeStr = ctx.getContentResolver().getType(uri.get(i));

            if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(mediaTypeStr)) {
                MediaType mediaType = MediaType.parse(mediaTypeStr);
                RequestBody requestBody = RequestBody.create(mediaType, new File(path));
                MultipartBody.Part multipartBody = MultipartBody.Part
                        .createFormData("files["+fileNo+"]", path, requestBody);
                multipartBodyParts.add(multipartBody);
                fileNo++;
            }
        }

        return multipartBodyParts;
    }
}
