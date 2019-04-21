package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Utils.PermissionsHelper;

import java.util.ArrayList;

public class SplashActivity extends FindLostThingsActivity
{
    @BindView(R.id.SplashActivity_SchoolName)
    TextView SchoolName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HideNavigationBar();
        LoadSchoolNameAnimation();
        if(PermissionsHelper.RequestAllPermissions(SplashActivity.this, SplashActivity.this))
        {
            InitApplication();
        }
    }

    private void HideNavigationBar()
    {
        if (Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else
        {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void InitApplication()
    {
        if(QQAuthCredentials.Validate())
        {
            //TODO 进入主界面
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
        }
        else
        {
            //TODO 跳到QQ登陆界面
            startActivity(new Intent(SplashActivity.this, QQAuthLoginActivity.class));
        }
        finish();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        HideNavigationBar();
    }

    private void LoadSchoolNameAnimation()
    {
        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this,R.anim.school_name_anim);
        animation.setStartOffset(800);
        SchoolName.startAnimation(animation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        ArrayList<String> StillNeedPermission = new ArrayList<>();
        switch (requestCode){
            case PermissionsHelper.GRANT_ALL_PERMISSION_CODE:{
                if(grantResults.length > 0){
                    for (int i = 0; i < permissions.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            StillNeedPermission.add(permissions[i]);
                            boolean checked = ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this,permissions[i]);
                            if(checked) PermissionsHelper.SetDontShowAgain(true);
                        }
                    }

                    if(StillNeedPermission.size() <= 0) {
                        InitApplication();
                    }
                    else {
                        if(PermissionsHelper.GetDontShowAgain()){
                            AlertDialog.Builder builder =  ShowPermissionAlert(R.string.PermissionNoGrantedAlertTitle,
                                    R.string.PermissionNoGrantedAlertTitle,false);
                            builder.setPositiveButton("OK", (dialogInterface, i) -> finish());
                            builder.show();
                        }
                        else {
                            AlertDialog.Builder builder = ShowPermissionAlert(R.string.PermissionNoGrantedAlertTitle,
                                    R.string.PermissionNoGrantedTryAgainTitle,false);
                            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                                PermissionsHelper.RequestPermissions(SplashActivity.this,SplashActivity.this,StillNeedPermission);
                            });
                            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

                                AlertDialog.Builder exitDialog =  ShowPermissionAlert(R.string.PermissionNoGrantedAlertTitle,
                                        R.string.PermissionNoGrantedAlertTitle,false);
                                exitDialog.setCancelable(false);
                                exitDialog.setPositiveButton("OK", (d, ig) -> finish());
                                exitDialog.show();

                            });
                            builder.show();
                        }
                    }
                }
            }
        }
    }
    private AlertDialog.Builder ShowPermissionAlert(int TitleId,int MessageId,boolean Cancelable)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
        alertDialog.setTitle(R.string.PermissionNoGrantedAlertTitle);
        alertDialog.setMessage(R.string.PermissionDontShowAgainAlertMessage);
        alertDialog.setCancelable(Cancelable);
        return alertDialog;
    }
}
