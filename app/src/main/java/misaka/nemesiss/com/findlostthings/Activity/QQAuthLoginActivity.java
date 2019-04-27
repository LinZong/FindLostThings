package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.UserAccount;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Tasks.PostUserInformationAsyncTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.System.currentTimeMillis;

public class QQAuthLoginActivity extends FindLostThingsActivity {
    private Button login, logout;
    private ImageView img;
    private TextView nickName;
    private String name, imgUrl;

    private Tencent mTencent;
    private QQLoginListener mListener;
    private UserInfo userInfo;

    private String openID;
    public String access_token;
    private String expires;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqauth_login);
        init();
        ClearInValidateUserAccountInfo();
    }

    private void ClearInValidateUserAccountInfo() {
        //既然跳到了这个页面，说明之前的登陆信息是无效的，需要清除。
        SharedPreferences.Editor editor = getSharedPreferences("LoginReturnData", MODE_PRIVATE).edit();
        editor.putBoolean("HaveStoredUserIdentity", false);
        editor.apply();
    }

    private void init() {
        mTencent = FindLostThingsApplication.getQQAuthService();
        img = findViewById(R.id.iv_img);
        login = findViewById(R.id.btn_login);
        if (mListener == null) {
            mListener = new QQLoginListener();
        }
        //登陆按钮点击事件
        login.setOnClickListener(v -> QQLogin());
    }

    private void QQLogin() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", mListener);
        }
    }

    private class QQLoginListener implements IUiListener {
        //登陆结果回调
        @Override
        public void onComplete(Object o) { //登录成功
            parseResult(o);
            PersistUserInfo();
            JumpToMainActivity();
        }

        @Override
        public void onError(UiError uiError) {
            //登录失败
            Log.d("QQAuthLoginActivity", "QQ登陆失败，原因为" + uiError.errorCode + uiError.errorMessage);
            Toast.makeText(QQAuthLoginActivity.this,"QQ登陆失败，原因为" + uiError.errorCode + uiError.errorMessage,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            //取消登陆
            Log.d("QQAuthLoginActivity", "用户取消了QQ登陆");
        }
    }

    private void parseResult(Object o) {
        //解析返回的Json串
        JSONObject jsonObject = (JSONObject) o;
        try {
            openID = jsonObject.getString("openid"); //用户标识
            access_token = jsonObject.getString("access_token"); //登录信息
            expires = jsonObject.getString("expires_in"); //token有效期
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void PersistUserInfo()//将返回的openid、access_token、expires_in三个参数保存在本地
    {
        String tokenInvalidDate = String.valueOf(currentTimeMillis() + Long.parseLong(expires) * 1000);//token的失效日期
        Context ctx = QQAuthLoginActivity.this;

        mTencent.setOpenId(openID);
        mTencent.setAccessToken(access_token, tokenInvalidDate);

        SharedPreferences.Editor editor = ctx.getSharedPreferences("LoginReturnData", MODE_PRIVATE).edit();
        editor.putBoolean("HaveStoredUserIdentity", true);
        editor.putString("openID", openID);
        editor.putString("access_token", access_token);
        editor.putString("expires", tokenInvalidDate);
        editor.apply();
    }


    private void JumpToMainActivity() {
        startActivity(new Intent(QQAuthLoginActivity.this, MainActivity.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, mListener);
    }
}




