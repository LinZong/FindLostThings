package misaka.nemesiss.com.findlostthings.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import misaka.nemesiss.com.findlostthings.R;

public class SetUp extends AppCompatActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView pushNotice=(TextView)findViewById(R.id.push_notice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        pushNotice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                if(pushNotice.getText().toString().equals("关"))
                    pushNotice.setText("开");
                else
                    pushNotice.setText("关");
            }
        });
    }

}
