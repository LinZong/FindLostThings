package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import misaka.nemesiss.com.findlostthings.Model.UserAccount;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import misaka.nemesiss.com.findlostthings.Tasks.UpdateUserInformationAsyncTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ShowOrChangeUserInfo extends AppCompatActivity
{
    private EditText qqEditText;
    private EditText weChatEditText;
    private EditText telEditText;
    private EditText emailEditText;
    private int realPersonValid;
    private ImageView imageView;
    private ConstraintLayout qq;
    private ConstraintLayout weChat;
    private ConstraintLayout tel;
    private ConstraintLayout email;
    private ConstraintLayout realNameAuthentication;
    private TextView realNameText;
    private ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_or_change_user_info);

        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.QQImage);
        qq = findViewById(R.id.qq);
        weChat = findViewById(R.id.weChat);
        tel = findViewById(R.id.tel);
        email = findViewById(R.id.email);
        realNameAuthentication = findViewById(R.id.RealNameAuthentication);
        realNameText = findViewById(R.id.realNameText);
        imageView1 = findViewById(R.id.imageView);

        qqEditText = findViewById(R.id.qq_number);
        weChatEditText = findViewById(R.id.weChat_number);
        telEditText = findViewById(R.id.phone_number);
        emailEditText = findViewById(R.id.emailEditText);

        AppUtils.ToolbarShowReturnButton(this, toolbar);

        if(FindLostThingsApplication.GetCurrentNetworkStatusObservable().getValue())
        {
            LoadUserInformation();
        }

        ClearRealPersonValidActivityState();
    }

    private void LoadUserInformation()
    {
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


        realPersonValid = FindLostThingsApplication.getUserService().getMyProfile().getRealPersonValid();
        switch (realPersonValid)
        {
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
    }

    private void ClearRealPersonValidActivityState()
    {
        new Thread(() -> {
            String cache = AppUtils.GetAppCachePath();
            File file = new File(new File(cache), "RealPersonValidState.json");
            if (file.exists())
            {
                file.delete();
            }
        }).start();
    }

    @OnClick({R.id.RealNameAuthentication})
    public void EnterRealNameAuthentication(View v)
    {
        UserService.LoadUserProfile();
        if (realPersonValid == 0 || realPersonValid == 2)
        {
            startActivity(new Intent(ShowOrChangeUserInfo.this, RealPersonValidActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                if (FindLostThingsApplication.GetCurrentNetworkStatusObservable().getValue() && ValidateEnteredInformation())
                {
                    UpdateUserProfile();

                } finish();
                break;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (FindLostThingsApplication.GetCurrentNetworkStatusObservable().getValue() && ValidateEnteredInformation())
        {
            UpdateUserProfile();

        } finish();
    }

    private void UpdateUserProfile()
    {
        UserInformation userInformation = FindLostThingsApplication.getUserService().getMyProfile();
        userInformation.setEmail(emailEditText.getText().toString().trim());
        userInformation.setQQ(qqEditText.getText().toString().trim());
        userInformation.setWxID(weChatEditText.getText().toString().trim());
        userInformation.setPhoneNumber(telEditText.getText().toString().trim());
        new UpdateUserInformationAsyncTask((result) -> {
            int status = result.getStatusCode();
            if (status == 0)
            {
                Toast.makeText(this, "更新信息成功", Toast.LENGTH_SHORT).show();
            } else
            {
                Toast.makeText(this, "更新信息失败", Toast.LENGTH_SHORT).show();
            }
        }).execute(userInformation);
    }

    private boolean ValidateEnteredInformation()
    {

        //QQ判断
        String MatchQQ = "[1-9][0-9]{4,9}";
        String MatchEmail = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        String MatchPhone = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";
        String MatchWx = "^[a-zA-Z]{1}[-_a-zA-Z0-9]{5,19}$";


        String email = emailEditText.getText().toString().trim();
        String qq = qqEditText.getText().toString().trim();
        String wx = weChatEditText.getText().toString().trim();
        String mobile = telEditText.getText().toString().trim();

        String[] test = {email, qq, wx, mobile};
        String[] matcher = {MatchEmail, MatchQQ, MatchWx, MatchPhone};
        TextView[] container = {emailEditText, qqEditText, weChatEditText, telEditText};

        ClearError(container);

        // 首先判4个是不是空

        boolean AllEmpty = true;
        List<TextView> NotEmptyContainer = new ArrayList<>();
        List<String> NotEmptyString = new ArrayList<>();
        List<String> NotEmptyMatcher = new ArrayList<>();
        for (int i = 0; i < test.length; i++)
        {
            if (!TextUtils.isEmpty(test[i]))
            {
                AllEmpty = false;
                NotEmptyString.add(test[i]);
                NotEmptyContainer.add(container[i]);
                NotEmptyMatcher.add(matcher[i]);
            }
        }
        if (AllEmpty)
        {
            Toast.makeText(ShowOrChangeUserInfo.this, "必须至少填写一项联系方式!", Toast.LENGTH_SHORT).show();
            return false;
        } else
        {

            boolean AllOK = true;
            for (int i = 0; i < NotEmptyString.size(); i++)
            {
                if (!IsMatch(NotEmptyMatcher.get(i), NotEmptyString.get(i)))
                {
                    NotEmptyContainer.get(i).setError("联系方式格式错误!");
                    AllOK = false;
                }
            }
            return AllOK;
        }
    }

    private void ClearError(TextView... views)
    {
        for (int i = 0; i < views.length; i++)
        {
            views[i].setError(null);
        }
    }

    private boolean IsMatch(String regex, CharSequence input)
    {
        return TextUtils.isEmpty(input) || Pattern.matches(regex, input);
    }
}
