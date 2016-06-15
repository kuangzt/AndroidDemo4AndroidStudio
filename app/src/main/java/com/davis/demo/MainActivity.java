package com.davis.demo;

import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getAppSize(this,"com.clean.spaceplus",new StatsObserver());
        delAppCache(this,"com.clean.spaceplus",new ClearCacheObserver());
    }

    public static void getAppSize(Context context, String pkgName, IPackageStatsObserver.Stub observer) {
        try {
            if (observer != null) {
                PackageManager pm = context.getPackageManager();
                Method getPackageSizeInfo = pm.getClass()
                        .getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, pkgName, observer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class StatsObserver extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
            long cacheSize = stats.cacheSize;
            if (Environment.isExternalStorageEmulated()) {
                cacheSize += stats.externalCacheSize;
            }
            Log.d("onGetStatsCompleted",String.valueOf(cacheSize));
        }
    }

    public static void delAppCache(Context context, String pkgName, IPackageDataObserver.Stub observer){
        try{
            if (observer != null) {
                PackageManager pm = context.getPackageManager();
                Method deleteApplicationCacheFiles = pm.getClass()
                        .getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                deleteApplicationCacheFiles.invoke(pm, pkgName, observer);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static class ClearCacheObserver extends IPackageDataObserver.Stub {
        @Override
        public void onRemoveCompleted(String packageName,boolean succeeded) {
            Log.d("onRemoveCompleted",String.format("packageName = %s,succeeded = %b",packageName,succeeded));
        }
    }
}
