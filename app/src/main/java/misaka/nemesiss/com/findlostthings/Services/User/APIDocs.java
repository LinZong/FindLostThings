package misaka.nemesiss.com.findlostthings.Services.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.widget.Toast;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.LoginAccountInfo;
import misaka.nemesiss.com.findlostthings.Model.Response.LoginAccountResponse;
import okhttp3.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import static android.content.Context.MODE_PRIVATE;

public class APIDocs {
    private static final String DeploymentAddress="http://111.230.238.192/learn/lost";
    private static final String RequestAddress="/user/login ";
    private static final String UserInfo="/user/info";
    private static final String LostThingsCategory="/category";
    private static final String LostThingsCategoryPartition="/category/detail?id=";

    private static final String FullAddress=DeploymentAddress+RequestAddress;
    private static final String FullUserInfo=DeploymentAddress+UserInfo;
    private static final String FullLostThingsCategory=DeploymentAddress+LostThingsCategory;
    private static final String FullLostThingsCategoryPartition=DeploymentAddress+LostThingsCategoryPartition;

    Context ctx = FindLostThingsApplication.getContext();
    SharedPreferences preferences=ctx.getSharedPreferences("userIDData",MODE_PRIVATE);
    long SnowflakeID=preferences.getLong("Snowflake ID",0);

    OkHttpClient okHttpClient=new OkHttpClient();

    public void getUserInformation(String AccessToken)
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{
                    Request request=new Request.Builder()
                            .url(FullUserInfo)
                            .addHeader(AccessToken,String.valueOf(SnowflakeID))
                            .build();
                    Response response=okHttpClient.newCall(request).execute();
                    String responseData=response.body().string();
                    Gson gson=new Gson();//Gson 解析服务器返回的用户信息，存于对象userInfo中，可随时通过get方法调用
                    UserInfo userInfo=gson.fromJson(responseData,UserInfo.class);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
    }).start();
    }

    public void UpdateUserInformation(String AccessToken,String QQ,String WxID,String PhoneNumber,String Email)
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{
                    UserInfo userInfo=new UserInfo();
                    userInfo.setQQ(QQ);
                    userInfo.setWxID(WxID);
                    userInfo.setPhoneNumber(PhoneNumber);
                    userInfo.setEmail(Email);
                    Gson gson=new Gson();
                    String result=gson.toJson(userInfo,UserInfo.class);
                    RequestBody requestBody= FormBody.create(MediaType.parse("application/json"),result);
                    Request request=new Request.Builder()
                            .url(FullUserInfo)
                            .addHeader(AccessToken,String.valueOf(SnowflakeID))
                            .put(requestBody)
                            .build();
                    Response response=okHttpClient.newCall(request).execute();
                    if(response.isSuccessful())
                    {
                        String responseData=response.body().string();
                       UserInfo userInfo1 = gson.fromJson(responseData, UserInfo.class);
                    }
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void postInformation(String OpenID,String AccessToken,String NickName,String CurrentDeviceAndroidID)
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{
                    LoginAccountInfo info = new LoginAccountInfo();
                    info.setOpenID(OpenID);
                    info.setAccessToken(AccessToken);
                    info.setNickName(NickName);
                    info.setCurrentDeviceAndroidID(CurrentDeviceAndroidID);
                    Gson gson = new Gson();
                    String result = gson.toJson(info,LoginAccountInfo.class);
                    RequestBody requestBody= FormBody.create(MediaType.parse("application/json"),result);
                    Request request=new Request.Builder()
                            .url(FullAddress)
                            .post(requestBody)
                            .build();
                    Response response=okHttpClient.newCall(request).execute();

                    if(response.isSuccessful())
                    {
                        String responseData=response.body().string();
                        LoginAccountResponse resp = gson.fromJson(responseData, LoginAccountResponse.class);
                        int statusCode=resp.getStatusCode();
                        long userID=resp.getUserID();
                      switch (statusCode)
                      {
                          case 0:
                              Context ctx = FindLostThingsApplication.getContext();
                              SharedPreferences.Editor editor = ctx.getSharedPreferences("userIDData",MODE_PRIVATE).edit();
                              editor.putLong("Snowflake ID",userID);
                              editor.apply();
                              break;
                          default:
                              break;
                      }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void getLostThingsCategory() //获取失物类别
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{
                    Request request=new Request.Builder()
                            .url(FullLostThingsCategory)
                            .build();
                    Response response=okHttpClient.newCall(request).execute();
                    String responseData=response.body().string();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getLostThingsCategoryPartition(int ID)//获取某类别下失物的细分类别
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{
                    String VariedUrl=FullLostThingsCategoryPartition+String.valueOf(ID);
                    Request request=new Request.Builder()
                            .url(VariedUrl)
                            .build();
                    Response response=okHttpClient.newCall(request).execute();
                    String responseData=response.body().string();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
