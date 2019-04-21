package misaka.nemesiss.com.findlostthings.Activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import misaka.nemesiss.com.findlostthings.BuildConfig;
import misaka.nemesiss.com.findlostthings.R;

public class AboutMe extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        TextView version=(TextView)findViewById(R.id.text3);
        version.setText("版本号："+BuildConfig.VERSION_NAME);
    }
}
