package com.example;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class ChannelUtil {

    public static String CHANNEL_ID = "3";
    public static String CHANNEL_NAME;

    public static void getChannelId(Context context) {
        CHANNEL_NAME = getChannelName(context);
            switch (CHANNEL_NAME) {
                case "xiaomi":
                    CHANNEL_ID = "0";
                    break;
                case "yingyongbao":
                    CHANNEL_ID = "1";
                    break;
        }
    }

    private static String getChannelName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return "";
    }

}
