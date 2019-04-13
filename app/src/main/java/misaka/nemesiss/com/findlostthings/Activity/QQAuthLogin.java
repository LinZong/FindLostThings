package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
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
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.User.APIDocs;
import org.json.JSONException;
import org.json.JSONObject;

public class QQAuthLogin extends AppCompatActivity  {
    private Button login, logout;
    private ImageView img;
    private TextView nickName;
    private String name, imgUrl;

    private static final String APPID = "101570216";
    private Tencent mTencent;
    private QQLoginListener mListener;
    private UserInfo userInfo;
    private GetInfoListener mInfoListener;

    private  String openID;
    private String access_token;
    private  String expires;
    APIDocs apiDocs=new APIDocs();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqauth_login);
        init();
    }

    private void init() {
        img = (ImageView) findViewById(R.id.iv_img);
        nickName = (TextView) findViewById(R.id.tv_nickname);
        login = (Button) findViewById(R.id.btn_login);
        logout = (Button) findViewById(R.id.btn_logout);
        //初始化Tencent对象
        if (mTencent == null) {
            mTencent = Tencent.createInstance(APPID, this);
        }
        //初始化登陆回调Listener
        if (mListener == null) {
            mListener = new QQLoginListener();
        }
        //登陆按钮点击事件
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QQLogin();
            }
        });
        //退出按钮点击事件
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QQLogout();
            }
        });
    }

    private void QQLogin() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", mListener);
        }
    }

    private void QQLogout() {
        if (mTencent.isSessionValid()) {
            mTencent.logout(this);
            //修改UI
            img.setImageResource(R.mipmap.ic_launcher);
            nickName.setText("未登录");
        }
    }

    private class QQLoginListener implements IUiListener {
        //登陆结果回调
        @Override
        public void onComplete(Object o) { //登录成功
            parseResult(o);
            setUserInfo();
        }

        @Override
        public void onError(UiError uiError) { //登录失败

        }

        @Override
        public void onCancel() { //取消登陆

        }
    }

    private void parseResult(Object o) {
        //解析返回的Json串
        JSONObject jsonObject = (JSONObject) o;
        try {
             openID = jsonObject.getString("openid"); //用户标识
             access_token = jsonObject.getString("access_token"); //登录信息
             expires = jsonObject.getString("expires_in"); //token有效期
            //配置token
            mTencent.setOpenId(openID);
            mTencent.setAccessToken(access_token, expires);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUserInfo() {
        //用户信息获取与展示
        QQToken qqToken = mTencent.getQQToken();
        userInfo = new UserInfo(this, qqToken);
        if (mInfoListener == null) {
            mInfoListener = new GetInfoListener();
        }
        userInfo.getUserInfo(mInfoListener);
    }

    //获取用户信息回调
    private class GetInfoListener implements IUiListener {
        @Override
        public void onComplete(Object o) { //获取成功，开始展示
            JSONObject jsonObject = (JSONObject) o;
            try {
                name = jsonObject.getString("nickname");
                imgUrl = jsonObject.getString("figureurl_qq_2");  //头像url
                nickName.setText(name);
                Glide.with(QQAuthLogin.this).load(imgUrl).into(img);
                apiDocs.postInformation(openID,access_token,name,getAndroidId(QQAuthLogin.this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) { //获取失败

        }

        @Override
        public void onCancel() {

        }
    }
    //获取登录设备的安卓ID
    public static String getAndroidId (Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return ANDROID_ID;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResultData(requestCode, resultCode, data, mListener);
    }
}




