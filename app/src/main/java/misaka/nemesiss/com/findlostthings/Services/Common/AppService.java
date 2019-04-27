package misaka.nemesiss.com.findlostthings.Services.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.AppSettings;

public class AppService {
    private AppSettings appSettings;
    private SharedPreferences sp;
    private Gson gson = new Gson();
    public AppService()
    {
        Context context = FindLostThingsApplication.getContext();
        sp = context.getSharedPreferences("AppServices",Context.MODE_PRIVATE);
        LoadAppSettings();
    }

    public void SaveAppSettings() {
        String settingJson = gson.toJson(appSettings,AppSettings.class);
        SharedPreferences.Editor EditSp = sp.edit();
        EditSp.putString("AppSettings",settingJson);
        EditSp.apply();
        Log.d("AppService","成功保存AppSettings.");
    }
    public AppSettings GetAppSettings() {
        return appSettings;
    }
    private void LoadAppSettings() {
        new Thread(() -> {
            String settingJson = sp.getString("AppSettings",null);
            if(!TextUtils.isEmpty(settingJson)){
                Log.d("AppService","成功读取先前已完成持久化的AppSettings");
                Log.d("AppService",settingJson);
                appSettings = gson.fromJson(settingJson,AppSettings.class);
            }
            else {
                Log.d("AppService","找不到持久化后的AppSettings，读取默认设置...");
                appSettings = new AppSettings();
                SaveAppSettings();
            }
        }).start();
    }
}
