package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Tasks.PostInformationAsyncTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.System.currentTimeMillis;

public class QQAuthLoginActivity extends FindLostThingsActivity
{
    private Button login, logout;
    private ImageView img;
    private TextView nickName;
    private String name, imgUrl;

    private Tencent mTencent;
    private QQLoginListener mListener;
    private UserInfo userInfo;
    private GetInfoListener mInfoListener;

    private String openID;
    public String access_token;
    private String expires;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqauth_login);
        init();
    }

    private void init()
    {
        mTencent = FindLostThingsApplication.getQQAuthService();
        img = (ImageView) findViewById(R.id.iv_img);
        nickName = (TextView) findViewById(R.id.tv_nickname);
        login = (Button) findViewById(R.id.btn_login);
        logout = (Button) findViewById(R.id.btn_logout);
        //初始化登陆回调Listener
        if (mListener == null)
        {
            mListener = new QQLoginListener();
        }
        //登陆按钮点击事件
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                QQLogin();
            }
        });
        //退出按钮点击事件
        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                QQLogout();
            }
        });
    }

    private void QQLogin()
    {
        if (!mTencent.isSessionValid())
        {
            mTencent.login(this, "all", mListener);
        }
    }

    private void QQLogout()
    {
        if (mTencent.isSessionValid())
        {
            mTencent.logout(this);
            //修改UI
            img.setImageResource(R.mipmap.ic_launcher);
            nickName.setText("未登录");

            SharedPreferences.Editor editor = getSharedPreferences("LoginReturnData", MODE_PRIVATE).edit();
            editor.putBoolean("HaveStoredUserIdentity", false);
            editor.apply();
        }
    }

    private class QQLoginListener implements IUiListener
    {
        //登陆结果回调
        @Override
        public void onComplete(Object o)
        { //登录成功
            parseResult(o);
            PersistUserInfo();
            ReportUserProfile();
        }

        @Override
        public void onError(UiError uiError)
        { //登录失败

        }

        @Override
        public void onCancel()
        { //取消登陆

        }
    }

    private void parseResult(Object o)
    {
        //解析返回的Json串
        JSONObject jsonObject = (JSONObject) o;
        try
        {
            openID = jsonObject.getString("openid"); //用户标识
            access_token = jsonObject.getString("access_token"); //登录信息
            expires = jsonObject.getString("expires_in"); //token有效期
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void PersistUserInfo()//将返回的openid、access_token、expires_in三个参数保存在本地
    {
        String tokenInvalidDate = String.valueOf(currentTimeMillis() + Long.parseLong(expires) * 1000);//token的失效日期
        Context ctx = QQAuthLoginActivity.this;

        mTencent.setOpenId(openID);
        mTencent.setAccessToken(access_token,tokenInvalidDate);

        SharedPreferences.Editor editor = ctx.getSharedPreferences("LoginReturnData", MODE_PRIVATE).edit();
        editor.putBoolean("HaveStoredUserIdentity", true);
        editor.putString("openID", openID);
        editor.putString("access_token", access_token);
        editor.putString("expires", tokenInvalidDate);
        editor.apply();
    }

    private void ReportUserProfile()
    {
        //用户信息获取与展示
        QQToken qqToken = mTencent.getQQToken();
        userInfo = new UserInfo(this, qqToken);
        if (mInfoListener == null)
        {
            mInfoListener = new GetInfoListener();
        }
        userInfo.getUserInfo(mInfoListener);
        JumpToMainActivity();
    }

    private void JumpToMainActivity()
    {
        startActivity(new Intent(QQAuthLoginActivity.this, MainActivity.class));
    }

    //获取用户信息回调
    private class GetInfoListener implements IUiListener
    {
        @Override
        public void onComplete(Object o)
        { //获取成功，开始展示
            JSONObject jsonObject = (JSONObject) o;
            try
            {
                name = jsonObject.getString("nickname");
                imgUrl = jsonObject.getString("figureurl_qq_2");  //头像url

                new PostInformationAsyncTask((res) ->
                {
                    if (res.getStatusCode() != 0)
                    {
                        Log.d("QQAuthLoginActivity", "上报数据给服务器出现异常!，活动退出.");
                    }
                    Log.d("QQAuthLoginActivity", "成功上报数据给服务器，活动退出.");
                    QQAuthLoginActivity.this.finish();
                }).execute(openID, name, AppUtils.getAndroidId(QQAuthLoginActivity.this));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError)
        { //获取失败
            Log.d("QQAuthLoginActivity", "获取个人信息出现异常!程序退出!");
            AppUtils.ShowAlertDialog(QQAuthLoginActivity.this,false,"获取个人信息失败", "应用程序遇到了严重错误，无法获取您的个人信息，即将退出。").show();
            runOnUiThread(QQAuthLoginActivity.this::finish);
        }

        @Override
        public void onCancel()
        {
            AppUtils.ShowAlertDialog(QQAuthLoginActivity.this,false,"您取消了授权", "应用程序无法获取您的个人信息, 可能影响您使用发布失物信息和找回失物等功能!").show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mTencent.onActivityResultData(requestCode, resultCode, data, mListener);
    }
}




