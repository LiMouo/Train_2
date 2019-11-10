package com.ziker.train.Utils.ToolClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
     * Send a post request.
     * @throws IOException OutMoney
     **/
    public static String SendRequest(String url, Map map) throws IOException {
        map.put("UserName",MyAppCompatActivity.user);
        String parms = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), parms);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(MyAppCompatActivity.UrlHead + url).post(requestBody).build();
        Response response = okHttpClient.newCall(request).execute();
        String Data = response.body().string();
        response.close();//close资源，静态方法不释放对象会内存溢出
        return Data;
    }

    /**
     * Return a ProgressDialog,The msg parameter is what needs to be displayed.
     */
    public static ProgressDialog WaitDialog(Context context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("请稍等");
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    /**
     * Set the backgroundColor of the button.
     **/
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
     *BITMAP 转 BASE64
     **/
    public static String BitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
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
        if (!isEmpty(base64Data)){
            byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return  null;
    }

    /**
     *判断字符串为空
     **/
    public static boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }
}
