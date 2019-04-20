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
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.LostThingsCategory;
import misaka.nemesiss.com.findlostthings.Model.Response.*;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.User.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Services.User.MySchoolBuildings;
import misaka.nemesiss.com.findlostthings.Services.User.WaterfallThingsInfo;
import misaka.nemesiss.com.findlostthings.Tasks.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static java.lang.System.currentTimeMillis;

public class QQAuthLogin extends FindLostThingsActivity
{
    private Button login, logout;
    private ImageView img;
    private TextView nickName;
    private String name, imgUrl;

    private static final String APPID = "101570216";
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
        LogThingsCategory1();
        LogThingsCategory();
    }

    private void init()
    {
        img = (ImageView) findViewById(R.id.iv_img);
        nickName = (TextView) findViewById(R.id.tv_nickname);
        login = (Button) findViewById(R.id.btn_login);
        logout = (Button) findViewById(R.id.btn_logout);
        CheckIfDateValid();
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
            setUserInfo();
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
            SaveUserInfo();
            CheckIfDateValid();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private Boolean CheckIfDateValid()
    {
        SharedPreferences preferences = getSharedPreferences("LoginReturnData", MODE_PRIVATE);

        Boolean HaveUserIdentity = preferences.getBoolean("HaveStoredUserIdentity", false);
        if (HaveUserIdentity)
        {
            String openID = preferences.getString("openID", "");
            String access_token = preferences.getString("access_token", "");
            String expires = preferences.getString("expires", "");

            if ((Long.parseLong(expires) - currentTimeMillis()) / 1000 > 0)
            {
                mTencent = Tencent.createInstance(APPID, this);
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(access_token, String.valueOf((Long.parseLong(expires) - currentTimeMillis()) / 1000));
                setUserInfo();
                return true;
            }
            else
            {
                Toast.makeText(QQAuthLogin.this, "当前用户已过期，请重新登录", Toast.LENGTH_SHORT).show();

            }
        }
        mTencent = Tencent.createInstance(APPID, this);
        return false;
    }

    private void SaveUserInfo()//将返回的openid、access_token、expires_in三个参数保存在本地
    {
        String tokenInvalidDate = String.valueOf(currentTimeMillis() + Long.parseLong(expires) * 1000);//token的失效日期
        Context ctx = QQAuthLogin.this;
        SharedPreferences.Editor editor = ctx.getSharedPreferences("LoginReturnData", MODE_PRIVATE).edit();
        editor.putBoolean("HaveStoredUserIdentity", true);
        editor.putString("openID", openID);
        editor.putString("access_token", access_token);
        editor.putString("expires", tokenInvalidDate);
        editor.apply();
    }

    private void setUserInfo()
    {
        //用户信息获取与展示
        QQToken qqToken = mTencent.getQQToken();
        userInfo = new UserInfo(this, qqToken);
        if (mInfoListener == null)
        {
            mInfoListener = new GetInfoListener();
        }
        userInfo.getUserInfo(mInfoListener);
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
                nickName.setText(name);
                Glide.with(QQAuthLogin.this).load(imgUrl).into(img);
                new PostInformationAsyncTask((res) ->
                {
                }).execute(openID, name, getAndroidId(QQAuthLogin.this));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError)
        { //获取失败

        }

        @Override
        public void onCancel()
        {

        }
    }

    //获取登录设备的安卓ID
    public static String getAndroidId(Context context)
    {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return ANDROID_ID;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mTencent.onActivityResultData(requestCode, resultCode, data, mListener);
    }

        private void LogThingsCategory()
    {
        LostThingsInfo lostThingsInfo=new LostThingsInfo();
        lostThingsInfo.setFoundTime(111);
        lostThingsInfo.setGivenTime(222);
        lostThingsInfo.setGiven(22);
        lostThingsInfo.setPublisher(333);
        lostThingsInfo.setIsgiven(1);
        lostThingsInfo.setGivenContacts("a2");
        lostThingsInfo.setThingAddiDescription("sbpy");
        lostThingsInfo.setPublisherContacts("sbpt");
        lostThingsInfo.setThingPhotoUrls("121");
        lostThingsInfo.setFoundAddrDescription("2324");
        lostThingsInfo.setFoundAddress("45w");
        lostThingsInfo.setId("444");
        lostThingsInfo.setPublishTime(2131);
        lostThingsInfo.setTitle("2323");
        lostThingsInfo.setThingCatId(131241);
        lostThingsInfo.setThingDetailId(2);

        new LostThingsInfoTask(new TaskPostExecuteWrapper<Integer>()
        {
            @Override
            public void DoOnPostExecute(Integer TaskRet)
            {
                    Log.d("CategoryLog",TaskRet.toString());
            }
        }).execute(lostThingsInfo);
    }

    private void LogThingsCategory1()
    {
        new GetSchoolBuildingsTask(TaskRet -> {
            for (MySchoolBuildings sb : TaskRet.getSchoolBuildings())
            {
                Log.d("QQAuthLogin",sb.getBuildingName());
            }
        }).execute(1);
    }
}




