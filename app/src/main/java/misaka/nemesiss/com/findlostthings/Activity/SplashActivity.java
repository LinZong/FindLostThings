package misaka.nemesiss.com.findlostthings.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Utils.EventProxy;
import misaka.nemesiss.com.findlostthings.Utils.PermissionsHelper;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SplashActivity extends FindLostThingsActivity
{
    @BindView(R.id.SplashActivity_SchoolName)
    TextView SchoolName;

    // 注意，这个Handler只能在App运行的时候使用。从Service启动Activity需要拿context去起，不能用这个Handler。

    public static Handler GoToMainActivityHandler;
    public static EventProxy<String> GotoMainActivityEvent;
    public static final int CAN_GOTO_MAINACTIVITY = 1;
    public static final int OVERTIME_GOTO_MAINACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        GoToMainActivityHandler = new Handler(this::SplashMessageHandler);

        GotoMainActivityEvent = new EventProxy<>();
        // 设置进入MainActivity的条件

        GotoMainActivityEvent.all(new EventProxy.EventResult<String>() {
            @Override
            public void handle(ConcurrentHashMap<String, Object> evs, ConcurrentHashMap<String, EventProxy.EventStatus> evStatus) {
                SplashActivity.GoToMainActivityHandler.sendEmptyMessage(SplashActivity.CAN_GOTO_MAINACTIVITY);
            }
        }, "qq_login", "get_school_name", "get_thing_category");


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

    private boolean SplashMessageHandler(Message message) {

        if (message.what == CAN_GOTO_MAINACTIVITY) {
            //计时器还没有超时，正常返回结果，所以取消掉计时器。
            GoToMainActivityHandler.removeMessages(OVERTIME_GOTO_MAINACTIVITY);
        }

        ArrayList<Activity> BackStack = FindLostThingsActivity.GetAllActivities();
        if(BackStack.isEmpty()) {
            // 以NEW TASK 方式启动
            Context context = FindLostThingsApplication.getContext();
            Intent intent = new Intent(context,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        else {
            Activity topActivity = BackStack.get(BackStack.size() - 1);
            topActivity.startActivity(new Intent(topActivity,MainActivity.class));
            topActivity.finish();
        }
        return true;
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
            GoToMainActivityHandler.sendEmptyMessageDelayed(OVERTIME_GOTO_MAINACTIVITY,10000);
            QQAuthCredentials.LoadUserAccountInfo();
        }
        else
        {
            startActivity(new Intent(SplashActivity.this, QQAuthLoginActivity.class));
            finish();
       }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        HideNavigationBar();
    }

    private void LoadSchoolNameAnimation()
    {

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
