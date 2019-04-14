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
import okhttp3.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;

public class UpdateUserInformationAsyncTask extends CustomPostExecuteAsyncTask<String,Void, UserInfoRespose>{
    private OkHttpClient okHttpClient;
    String EncryptedAccessToken = null;
    Context ctx = FindLostThingsApplication.getContext();
    SharedPreferences preferences = ctx.getSharedPreferences("userIDData", MODE_PRIVATE);
    long SnowflakeID = preferences.getLong("Snowflake ID", 0);

    public UpdateUserInformationAsyncTask(TaskPostExecuteWrapper<UserInfoRespose> DoInPostExecute) {
        super(DoInPostExecute);
        APIDocs.encryptionAccessToken();
    }


    @Override
    protected UserInfoRespose doInBackground(String... information) {
        try {
            UserInfo userInfo = new UserInfo();
            userInfo.setQQ(information[0]);
            userInfo.setWxID(information[1]);
            userInfo.setPhoneNumber(information[2]);
            userInfo.setEmail(information[3]);
            Gson gson = new Gson();
            String result = gson.toJson(userInfo, UserInfo.class);
            RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), result);
            Request request = new Request.Builder()
                    .url(APIDocs.FullUserInfo)
                    .addHeader(EncryptedAccessToken, String.valueOf(SnowflakeID))
                    .put(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                UserInfo userInfo1 = gson.fromJson(responseData, UserInfo.class);
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
