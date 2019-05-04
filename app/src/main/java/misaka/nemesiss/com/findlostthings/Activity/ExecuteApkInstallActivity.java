package misaka.nemesiss.com.findlostthings.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

public class ExecuteApkInstallActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String apkPath = getIntent().getStringExtra("ApkPath");
        if(!TextUtils.isEmpty(apkPath))
        {
            AppUtils.InstallApk(apkPath);
        }
    }
}
