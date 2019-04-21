package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import misaka.nemesiss.com.findlostthings.R;

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
        Handler handler = new Handler();
        handler.postDelayed(()->{
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            SplashActivity.this.finish();
        }, 4000);
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
}
