package ecarx.videomanuals.application;

import android.app.Application;
import android.os.Environment;

import com.ecarx.log.Lg;

import java.io.File;

import ecarx.videomanuals.BuildConfig;

/**
 * Created by doudou on 2017/8/4.
 */

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initECarXLog();
    }

    private void initECarXLog() {

        String packageName = getPackageName();
        String xsfCarLifeLogPath = "xsf/log/" + packageName;       // 所有日志文件存放到统一根目录下，便于收集，同时按照应用分子目录存放，必要时按应用内功能模块划分
        File carlifeLogDir = new File(Environment.getExternalStorageDirectory(), xsfCarLifeLogPath);

        Lg.config(this)
                .setLogSwitch(true)
                .setConsoleSwitch(true)                             // debug版本，仅输出到控制台
                .setConsoleFilter(BuildConfig.DEBUG ? Lg.V : Lg.W)  // release 版本，warning 及以上级别日志输出到控制台及文件
                .setLog2FileSwitch(!BuildConfig.DEBUG)              // debug版本，仅输出到控制台
                .setFileFilter(BuildConfig.DEBUG ? Lg.D : Lg.I)     // release 版本，info及以上级别日志保存到文件，
                .setLogHeadSwitch(BuildConfig.DEBUG)
                .setBorderSwitch(BuildConfig.DEBUG)
//                .setGlobalTag("XCViseoManuals")
                .setDir(carlifeLogDir.getPath())
                .setsFilePrefix("XCViseoManuals");

    }


}
