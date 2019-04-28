package misaka.nemesiss.com.findlostthings.Activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.support.v7.widget.Toolbar;
import com.bumptech.glide.Glide;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.UserAccount;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

public class ShowOrChangeUserInfo extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_or_change_user_info);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        ImageView imageView=(ImageView)findViewById(R.id.QQImage);
        ConstraintLayout qq=(ConstraintLayout)findViewById(R.id.qq);
        ConstraintLayout weChat=( ConstraintLayout)findViewById(R.id.weChat);
        ConstraintLayout tel=( ConstraintLayout)findViewById(R.id.tel);
        ConstraintLayout email=( ConstraintLayout)findViewById(R.id.email);
        UserAccount userAccount= FindLostThingsApplication.getUserService().getUserAccount();
        Glide.with(ShowOrChangeUserInfo.this)
                .load(userAccount.getImageUrl())
                .into(imageView);
        AppUtils.ToolbarShowReturnButton(this,toolbar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return  true;
    }
}
