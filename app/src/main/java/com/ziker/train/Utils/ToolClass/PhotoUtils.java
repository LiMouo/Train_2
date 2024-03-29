package com.ziker.train.Utils.ToolClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PhotoUtils {
    private static final String TAG = "PhotoUtils";

    /**
     * @param activity 当前activity
     * @param imageUri 拍照后照片存储路径
     * @param requestCode 调用系统相机请求码
     */
    public static void takePicture(Activity activity, Uri imageUri, int requestCode) {
        //调用系统相机
        Intent intentCamera = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intentCamera, requestCode);
    }

    /**
     * @param fragment 当前fragment
     * @param imageUri 拍照后照片存储路径
     * @param requestCode 调用系统相机请求码
     */
    public static void takePicture(Fragment fragment, Uri imageUri, int requestCode) {
        //调用系统相机
        Intent intentCamera = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        fragment.startActivityForResult(intentCamera, requestCode);
    }


    /**
     * @param activity 当前activity
     * @param requestCode 打开相册的请求码
     */
    public static void openPictrue(Activity activity, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, requestCode);
    }

    /**
     * @param fragment 当前fragment
     * @param requestCode 打开相册的请求码
     */
    public static void openPictrue(Fragment fragment, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        fragment.startActivityForResult(photoPickerIntent, requestCode);
    }

    /**
     * @param activity  当前activity
     * @param imageUri 剪裁原图的Uri
     * @param FileUri 剪裁后的图片的Uri
     * @param width 剪裁图片的宽度
     * @param height  剪裁图片高度
     * @param requestCode 剪裁图片的请求码
     */
    public static void cropImageUri(Activity activity, Uri imageUri,  Uri FileUri,Integer width, Integer height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
          intent.putExtra("outputX", width);
          intent.putExtra("outputY", height);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra("return-data", true);
        Uri uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * @param fragment  当前activity
     * @param imageUri 剪裁原图的Uri
     * @param FileUri 剪裁后的图片的Uri
     * @param width 剪裁图片的宽度
     * @param height  剪裁图片高度
     * @param requestCode 剪裁图片的请求码
     */
    public static void cropImageUri(Fragment fragment, Uri imageUri,  Uri FileUri,Integer width, Integer height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        if(width != null && height != null){
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
        }
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 读取uri所在的图片
     * @param uri  图片对应的Uri
     * @param mContext  上下文对象
     * @return 获取图像的Bitmap
     */
    public static Bitmap getBitmapFromUri(Uri uri, Context mContext) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param context 上下文对象
     * @param uri 当前相册照片的Uri
     * @return 解析后的Uri对应的String
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String pathHead = "file:///";
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {// DocumentProvider  文件提供者
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {// ExternalStorageProvider 外部储存
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Log.d(TAG, "getPath: 1");
                if ("primary".equalsIgnoreCase(type)) {
                    return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {// DownloadsProvider  下载文件
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.
                        withAppendedId(Uri.parse("content://downloads/public_downloads"),
                                Long.valueOf(id));
                Log.d(TAG, "getPath: 2");
                return pathHead + getDataColumn(context,
                        contentUri, null, null);
            } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {// MediaProvider  媒体文件
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type))
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                 else if ("video".equals(type))
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                 else if ("audio".equals(type))
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                Log.d(TAG, "getPath: 3");
                return pathHead + getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            return pathHead + getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return pathHead + uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     * @param context The context.
     * @param uri  The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection,
                    selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     *BITMAP 转 BASE64
     **/
    public static String BitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream b = null;
        try {
            if (bitmap != null) {
                b = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
                b.flush();
                b.close();
                byte[] bitmapBytes = b.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (b != null) {
                    b.flush();
                    b.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     *BASE64 转 BITMAP
     **/
    public static Bitmap Base64ToBitmap(String base64Data) {
        if (!Tools.isEmpty(base64Data)){
            byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return  null;
    }
}