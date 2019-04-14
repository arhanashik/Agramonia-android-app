package com.workfort.apps.util.helper;

import android.content.ContentValues;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
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
        Cursor cursor = context.getContentResolver().query(
                uri, projection, null, null, null
        );
        if (cursor == null) return null;
        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(columnIndex);
        cursor.close();
        return s;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static File getCompressedFile(Context ctx, String filePath){
        File compressedImageFile = null;
        try {
            compressedImageFile = new Compressor(ctx).compressToFile(new File(filePath));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return compressedImageFile;
    }

    public static MultipartBody.Part getMultiPartBody(Uri uri) {
        Context ctx = AgramoniaApp.Companion.getBaseApplicationContext();

        String path = getPath(ctx, uri);
        if(TextUtils.isEmpty(path)) return null;

        File compressedFile = getCompressedFile(ctx, path);
        if(compressedFile != null) {
            Uri compressedUri = getImageContentUri(ctx, compressedFile);
            if(compressedUri != null) {
                uri = compressedUri;
                path = getPath(ctx, uri);
            }
        }

        String mediaTypeStr = ctx.getContentResolver().getType(uri);

        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(mediaTypeStr)) {
            MediaType mediaType = MediaType.parse(mediaTypeStr);
            RequestBody requestBody = RequestBody.create(mediaType, new File(path));
            return MultipartBody.Part.createFormData("file", path, requestBody);
        }

        return null;
    }

    public static ArrayList<MultipartBody.Part> getMultiPartBody(List<Uri> uriList) {
        ArrayList<MultipartBody.Part> multipartBodyParts = new ArrayList<>();

        int fileNo = 0;
        for (int i = 0; i < uriList.size(); i++) {
            Context ctx = AgramoniaApp.Companion.getBaseApplicationContext();

            Uri uri = uriList.get(i);
            String path = ImageUtil.getPath(ctx, uri);
            if(TextUtils.isEmpty(path)) return null;

            File compressedFile = getCompressedFile(ctx, path);
            if(compressedFile != null) {
                Uri compressedUri = getImageContentUri(ctx, compressedFile);
                if(compressedUri != null) {
                    uri = compressedUri;
                    path = getPath(ctx, uri);
                }
            }

            String mediaTypeStr = ctx.getContentResolver().getType(uri);

            if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(mediaTypeStr)) {
                MediaType mediaType = MediaType.parse(mediaTypeStr);
                RequestBody requestBody = RequestBody.create(mediaType, new File(path));
                MultipartBody.Part multipartBody = MultipartBody.Part.createFormData(
                        "files["+fileNo+"]", path, requestBody
                );
                multipartBodyParts.add(multipartBody);
                fileNo++;
            }
        }

        return multipartBodyParts;
    }
}
