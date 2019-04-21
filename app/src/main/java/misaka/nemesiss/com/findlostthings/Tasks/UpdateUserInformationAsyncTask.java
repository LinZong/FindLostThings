package misaka.nemesiss.com.findlostthings.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;
import misaka.nemesiss.com.findlostthings.Model.Response.UserInfoUpdateResponse;
import misaka.nemesiss.com.findlostthings.Services.User.APIDocs;
import okhttp3.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;

public class UpdateUserInformationAsyncTask extends CustomPostExecuteAsyncTask<String,Void, UserInfoUpdateResponse>{
    private OkHttpClient okHttpClient;
    String EncryptedAccessToken = null;
    Context ctx = FindLostThingsApplication.getContext();
    SharedPreferences preferences = ctx.getSharedPreferences("userIDData", MODE_PRIVATE);
    long SnowflakeID = preferences.getLong("Snowflake ID", 0);

    public UpdateUserInformationAsyncTask(TaskPostExecuteWrapper<UserInfoUpdateResponse> DoInPostExecute) {
        super(DoInPostExecute);
        try
        {
            EncryptedAccessToken= APIDocs.encryptionAccessToken();
        } catch (InvalidKeyException e)
        {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected UserInfoUpdateResponse doInBackground(String... information) {
        try {
            UserInformation userInfo = new UserInformation();
            userInfo.setQQ(information[0]);
            userInfo.setWxID(information[1]);
            userInfo.setPhoneNumber(information[2]);
            userInfo.setEmail(information[3]);
            Gson gson = new Gson();
            String result = gson.toJson(userInfo, UserInformation.class);
            RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), result);
            Request request = new Request.Builder()
                    .url(APIDocs.FullUserInfo)
                    .addHeader("actk", EncryptedAccessToken)
                    .addHeader("userid",String.valueOf(SnowflakeID))
                    .put(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                UserInfoUpdateResponse userInfoResponse = gson.fromJson(responseData, UserInfoUpdateResponse.class);
                return userInfoResponse;
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
