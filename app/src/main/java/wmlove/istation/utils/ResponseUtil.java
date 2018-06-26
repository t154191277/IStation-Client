package wmlove.istation.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import retrofit2.HttpException;
import wmlove.istation.LoginActivity;

public class ResponseUtil {

    //403 FORBIDDEN
    private static final int FORBIDDEN = 403;

    /**
     * 处理Token失效，失效后登出
     *
     * @param context Activity
     * @param e       HttpException
     */
    public static void onResponseError(Context context, Throwable e) {
        if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            switch (exception.code()) {
                case FORBIDDEN:
                    logoutUser(context);
                    break;
            }
        }
    }

    /**
     * 处理Token失效，失效后登出
     *
     * @param context Activity
     * @param code 状态码
     */
    public static void onResponseError(Context context, int code) {
        switch (code) {
            case FORBIDDEN:
                logoutUser(context);
                break;
        }
    }

    /**
     * logout，返回LoginActivity
     *
     * @param context Activity
     */
    private static void logoutUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        sp.edit().putString("tokenStr", "").apply();
        sp.edit().putString("phoneStr", "").apply();

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).finish();
    }
}
