package misaka.nemesiss.com.findlostthings.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Response.UserInfoResponse;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import okhttp3.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;

public class GetUserInformationTask extends CustomPostExecuteAsyncTask<Long,Void, UserInfoResponse> {
    private OkHttpClient okHttpClient;

    private UserService userService = FindLostThingsApplication.getUserService();
    long SnowflakeID = userService.GetUserID();

    String EncryptedAccessToken = null;


    public GetUserInformationTask(TaskPostExecuteWrapper<UserInfoResponse> DoInPostExecute) {
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
    protected UserInfoResponse doInBackground(Long... UserIDs) {
        try {
            String url = APIDocs.FullUserInfo;
            if(UserIDs.length > 0 && UserIDs[0] != null ){
                url = url + "?query=" + UserIDs[0];
            }

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("actk", EncryptedAccessToken)
                    .addHeader("userid",String.valueOf(SnowflakeID))
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                String responseData = response.body().string();
                Gson gson = new Gson();//Gson 解析服务器返回的用户信息，存于对象userInfo中，可随时通过get方法调用
               UserInfoResponse userInfoRespose= gson.fromJson(responseData, UserInfoResponse.class);
               return userInfoRespose;
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
