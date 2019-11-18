package com.ziker.train.Utils.ToolClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * public tools class.
 *
 * @author ziker
 */
public class Tools {
    private static boolean Toasting = false;
    private static Toast toast;
    private static Gson gson = new Gson();
    //不直接用 # 方便其他类调用颜色资源设置透明度
    public static String[] Colors = {"1AE642", "ffff00", "ff9999", "ffccff", "ff00ff", "ccffff", "99ff99", "9999ff", "6666ff", "66ff33", "FF4040", "669933", "cc0000", "8B6508",
            "ffdc6b", "ffe96b", "daff6b", "9fff6b", "6aff84", "69ff79", "6affb5", "6bffee", "69cdff", "6ac6ff", "7FC13F", "e9e9e9", "777777"};


    /**
     * 发送一个Post请求
     * @param url  目标url
     * @param map  参数
     * @return  返回的数据
     * @throws IOException
     */
    public static String SendPostRequest(String url, Map map) throws IOException {
        map.put("UserName",MyAppCompatActivity.user);
        String parms = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), parms);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(MyAppCompatActivity.UrlHead+url).post(requestBody).build();
        Response response = okHttpClient.newCall(request).execute();
        String Data = response.body().string();
        response.close();//close资源，静态方法不释放对象会内存溢出
        return Data;
    }

    public static byte[] SendPostRequest(String url) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://"+MyAppCompatActivity.Ip+":"+MyAppCompatActivity.Port+"/transportservice"+url).build();
        Response response = okHttpClient.newCall(request).execute();
        byte[] s =response.body().bytes();
        response.close();//close资源，静态方法不释放对象会内存溢出
        return s;
    }

    /**
     * 创建等待对话框
     * @param context  上下文
     * @param msg  显示的提示消息
     * @return   对话框对象
     */
    public static ProgressDialog WaitDialog(Context context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("请稍等");
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    /**
     * 设置按钮颜色
     * @param button 按钮
     */
    public static void SetButtonColor(Button button) {
        int i = (int) Math.floor(Math.random() * Colors.length);
        button.setBackgroundColor(Color.parseColor("#" + Colors[i]));
    }
    public static void SetButtonColor(Button button,int index) {
        button.setBackgroundColor(Color.parseColor("#" + Colors[index]));
    }

    /**
     *杜绝Toast重复弹出
     **/
    public static void Toast(Context context, String msg, boolean LongTime) {
        if (Toasting)
            toast.cancel();
        toast = Toast.makeText(context, null, LongTime ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
        Toasting = true;
    }
    /**
     *判断字符串为空
     **/
    public static boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }


    /**
     * 字节流装bitmap
     * @param temp  字节
     * @return  bitmap对象
     */
    public static Bitmap BitmapFromByte(byte[] temp){
        if(temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        }else{
            return null;
        }
    }
    /**
     * bitmap转字节流
     * @param bitmap  bitmap对象
     * @param quality   压缩质量
     * @return  字节流
     */
    public static byte[] BitmapToByte(Bitmap bitmap, int quality){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //参数1转换类型，参数2压缩质量，参数3字节流资源
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality > 100 ?100:quality<10?10:quality, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * 打开相机
     * @param activity
     * @param imageUri  保存的uri地址
     * @param requestCode  返回码
     */
    public static void OpenCamera(Activity activity, Uri imageUri, int requestCode) {
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
     * @param activity 当前activity
     * @param requestCode 打开相册的请求码
     */
    public static void OpenPictrue(Activity activity, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, requestCode);
    }

    /**
     * 文件流转字节流
     * @param path  文件路径
     * @return  字节数组
     * @throws Exception
     */
    public static byte[] ReadStream(String path) throws Exception {
        FileInputStream fs = new FileInputStream(path);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while (-1 != (len = fs.read(buffer))) {
            outStream.write(buffer, 0, len);
        }
        byte[] b = outStream.toByteArray();
        outStream.close();
        fs.close();
        return b;
    }

    public static Bitmap QRCode(String param, int width, int height){
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); //记得要自定义长宽
        BitMatrix encode = null;
        try {
            encode = qrCodeWriter.encode(param, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int[] colors = new int[width * height];
        //利用for循环将要表示的信息写出来
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (encode.get(i, j)) {
                    colors[i * width + j] = Color.BLACK;
                } else {
                    colors[i * width + j] = Color.WHITE;
                }
            }
        }
        return Bitmap.createBitmap(colors, width, height, Bitmap.Config.RGB_565);
    }
}
