package misaka.nemesiss.com.findlostthings.Activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
        ConstraintLayout pushNotice=(ConstraintLayout)findViewById(R.id.push_notice);
        TextView textView1=(TextView)findViewById(R.id.notice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        pushNotice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(textView1.getText().toString().equals("关"))
                    textView1.setText("开");
                else
                    textView1.setText("关");
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}
