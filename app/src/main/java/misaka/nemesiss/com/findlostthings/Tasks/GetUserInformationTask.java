package misaka.nemesiss.com.findlostthings.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Response.UserInfoRespose;
import misaka.nemesiss.com.findlostthings.Services.User.APIDocs;
import misaka.nemesiss.com.findlostthings.Services.User.UserInfo;
import misaka.nemesiss.com.findlostthings.Utils.HMacSha256;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;

public class GetUserInformationTask extends CustomPostExecuteAsyncTask<Void,Void, UserInfoRespose> {
    private OkHttpClient okHttpClient;

    Context ctx = FindLostThingsApplication.getContext();
    SharedPreferences preferences = ctx.getSharedPreferences("userIDData", MODE_PRIVATE);
    long SnowflakeID = preferences.getLong("Snowflake ID", 0);

    String EncryptedAccessToken = null;


    public GetUserInformationTask(TaskPostExecuteWrapper<UserInfoRespose> DoInPostExecute) {
        super(DoInPostExecute);
       APIDocs.encryptionAccessToken();
    }


    @Override
    protected UserInfoRespose doInBackground(Void... voids) {
        try {
            Request request = new Request.Builder()
                    .url(APIDocs.FullUserInfo)
                    .addHeader(EncryptedAccessToken, String.valueOf(SnowflakeID))
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                String responseData = response.body().string();
                Gson gson = new Gson();//Gson 解析服务器返回的用户信息，存于对象userInfo中，可随时通过get方法调用
                UserInfo userInfo = gson.fromJson(responseData, UserInfo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        okHttpClient = new OkHttpClient.Builder().connectTimeout(4500, TimeUnit.MILLISECONDS).build();
    }
}
