package misaka.nemesiss.com.findlostthings.Tasks;

import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;
import misaka.nemesiss.com.findlostthings.Model.Response.CommonResponse;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import okhttp3.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class UpdateUserInformationAsyncTask extends CustomPostExecuteAsyncTask<UserInformation,Void, CommonResponse>{
    private OkHttpClient okHttpClient;
    String EncryptedAccessToken = null;
    private UserService userService = FindLostThingsApplication.getUserService();
    long SnowflakeID = userService.GetUserID();

    public UpdateUserInformationAsyncTask(TaskPostExecuteWrapper<CommonResponse> DoInPostExecute) {
        super(DoInPostExecute);
        try
        {
            EncryptedAccessToken= QQAuthCredentials.GetEncryptedAccessToken();
        } catch (InvalidKeyException e)
        {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected CommonResponse doInBackground(UserInformation... us) {
        try {

            Gson gson = new Gson();
            String result = gson.toJson(us[0], UserInformation.class);
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
                CommonResponse userInfoResponse = gson.fromJson(responseData, CommonResponse.class);
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
