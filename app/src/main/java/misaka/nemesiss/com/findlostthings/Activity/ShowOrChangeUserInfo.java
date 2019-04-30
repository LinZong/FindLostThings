package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;
import misaka.nemesiss.com.findlostthings.Model.Response.CommonResponse;
import misaka.nemesiss.com.findlostthings.Model.UserAccount;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import misaka.nemesiss.com.findlostthings.Tasks.UpdateUserInformationAsyncTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.io.File;

public class ShowOrChangeUserInfo extends AppCompatActivity {
    private EditText qqEditText;
    private EditText weChatEditText;
    private EditText telEditText;
    private EditText emailEditText;

    private int realPersonValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_or_change_user_info);
        UserService.LoadUserProfile();
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView imageView = (ImageView) findViewById(R.id.QQImage);
        ConstraintLayout qq = (ConstraintLayout) findViewById(R.id.qq);
        ConstraintLayout weChat = (ConstraintLayout) findViewById(R.id.weChat);
        ConstraintLayout tel = (ConstraintLayout) findViewById(R.id.tel);
        ConstraintLayout email = (ConstraintLayout) findViewById(R.id.email);
        ConstraintLayout realNameAuthentication = (ConstraintLayout) findViewById(R.id.RealNameAuthentication);
        TextView realNameText = (TextView) findViewById(R.id.realNameText);
        ImageView imageView1 = (ImageView) findViewById(R.id.imageView);

        qqEditText = (EditText) findViewById(R.id.qq_number);
        weChatEditText = (EditText) findViewById(R.id.weChat_number);
        telEditText = (EditText) findViewById(R.id.phone_number);
        emailEditText = (EditText) findViewById(R.id.emailEditText);


        String qqStr = FindLostThingsApplication.getUserService().getMyProfile().getQQ();
        String weChatStr = FindLostThingsApplication.getUserService().getMyProfile().getWxID();
        String telStr = FindLostThingsApplication.getUserService().getMyProfile().getPhoneNumber();
        String emailStr = FindLostThingsApplication.getUserService().getMyProfile().getEmail();
        qqEditText.setText(qqStr);
        weChatEditText.setText(weChatStr);
        telEditText.setText(telStr);
        emailEditText.setText(emailStr);


        UserAccount userAccount = FindLostThingsApplication.getUserService().getUserAccount();
        Glide.with(ShowOrChangeUserInfo.this)
                .load(userAccount.getImageUrl())
                .into(imageView);
        AppUtils.ToolbarShowReturnButton(this, toolbar);

        realPersonValid = FindLostThingsApplication.getUserService().getMyProfile().getRealPersonValid();
        switch (realPersonValid) {
            case 0://未认证
                realNameText.setText("未实名认证");
                imageView1.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
                break;
            case 1://已认证
                realNameText.setText("实名认证成功");
                break;
            case 2://认证失败
                realNameText.setText("实名认证失败");
                imageView1.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
                break;
        }
        ClearRealPersonValidActivityState();
    }


    private void ClearRealPersonValidActivityState() {
        new Thread(() -> {
            String cache = AppUtils.GetAppCachePath();
            File file = new File(new File(cache), "RealPersonValidState.json");
            if (file.exists()) {
                file.delete();
            }
        }).start();
    }

    @OnClick({R.id.RealNameAuthentication})
    public void EnterRealNameAuthentication(View v) {
        UserService.LoadUserProfile();
        if (realPersonValid == 0 || realPersonValid == 2) {
            startActivity(new Intent(ShowOrChangeUserInfo.this, RealPersonValidActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                UpdateUserProfile();
                finish();
                break;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UpdateUserProfile();
        finish();
    }

    private void UpdateUserProfile() {
        UserInformation userInformation = FindLostThingsApplication.getUserService().getMyProfile();
        userInformation.setEmail(emailEditText.getText().toString());
        userInformation.setQQ(qqEditText.getText().toString());
        userInformation.setWxID(weChatEditText.getText().toString());
        userInformation.setPhoneNumber(telEditText.getText().toString());

        new UpdateUserInformationAsyncTask((result) -> {
            int status = result.getStatusCode();
            if (status == 0) {
                Toast.makeText(this, "更新信息成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "更新信息失败", Toast.LENGTH_SHORT).show();
            }

        }).execute(userInformation);
    }
}
