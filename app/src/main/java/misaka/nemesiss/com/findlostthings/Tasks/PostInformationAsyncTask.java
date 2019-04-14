package misaka.nemesiss.com.findlostthings.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.LoginAccountInfo;
import misaka.nemesiss.com.findlostthings.Model.Response.LoginAccountResponse;
import misaka.nemesiss.com.findlostthings.Services.User.APIDocs;
import misaka.nemesiss.com.findlostthings.Utils.HMacSha256;
import okhttp3.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;

public class PostInformationAsyncTask extends CustomPostExecuteAsyncTask<String,Void,LoginAccountResponse>{
    private OkHttpClient okHttpClient;
    String EncryptedAccessToken = null;
    Context ctx = FindLostThingsApplication.getContext();
    public PostInformationAsyncTask(TaskPostExecuteWrapper<LoginAccountResponse> DoInPostExecute) {
        super(DoInPostExecute);
        APIDocs.encryptionAccessToken();
    }

    @Override
    public LoginAccountResponse doInBackground(String... information) {
        try {
            LoginAccountInfo info = new LoginAccountInfo();
            info.setOpenID(information[0]);
            info.setAccessToken(EncryptedAccessToken);
            info.setNickName(information[1]);
            info.setCurrentDeviceAndroidID(information[2]);
            Gson gson = new Gson();
            String result = gson.toJson(info, LoginAccountInfo.class);
            RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), result);
            Request request = new Request.Builder()
                    .url(APIDocs.FullAddress)
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseData = response.body().string();
                LoginAccountResponse resp = gson.fromJson(responseData, LoginAccountResponse.class);
                int statusCode = resp.getStatusCode();
                long userID = resp.getUserID();
                switch (statusCode) {
                    case 0:
                        SharedPreferences.Editor editor = ctx.getSharedPreferences("userIDData", MODE_PRIVATE).edit();
                        editor.putLong("Snowflake ID", userID);
                        editor.apply();
                        break;
                    default:
                        break;
                }
            }
        }
        catch (Exception e) {
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
