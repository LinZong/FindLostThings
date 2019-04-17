package misaka.nemesiss.com.findlostthings.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Services.User.APIDocs;
import misaka.nemesiss.com.findlostthings.Services.User.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Services.User.WaterfallThingsInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.modelmapper.TypeToken;

import java.util.List;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;

public class WaterfallThingsInfoTask extends CustomPostExecuteAsyncTask<WaterfallThingsInfo,Void, List<LostThingsInfo>>
{
    private OkHttpClient okHttpClient;
    String EncryptedAccessToken = null;
    Context ctx = FindLostThingsApplication.getContext();
    SharedPreferences preferences = ctx.getSharedPreferences("userIDData", MODE_PRIVATE);
    long SnowflakeID = preferences.getLong("Snowflake ID", 0);
    final String s1="EndItemId=";
    final String s2="&";
    final String s3="HaveFetchedItemCount=";
    final String s4="&Count=";
    String s5;
    public  WaterfallThingsInfoTask(TaskPostExecuteWrapper<List<LostThingsInfo>> DoInPostExecute) {
        super(DoInPostExecute);
        EncryptedAccessToken= APIDocs.encryptionAccessToken();
    }
    @Override
    protected List<LostThingsInfo> doInBackground(WaterfallThingsInfo... waterfallThingsInfos) {
        try {
            if(waterfallThingsInfos[0].getStatus()==0)
            {
                String s5=APIDocs.FullThingList+s1+waterfallThingsInfos[0].getEndItemId()+s2+s3+
                    waterfallThingsInfos[0].getHaveFetchedItemCount()+s4+waterfallThingsInfos[0].getCount();
            }
            else
            {
                s5=APIDocs.FullThingList+s3+ waterfallThingsInfos[0].getHaveFetchedItemCount()+s4+waterfallThingsInfos[0].getCount();
            }
            Request request = new Request.Builder()
                    .addHeader("actk", EncryptedAccessToken)
                    .addHeader("userid", String.valueOf(SnowflakeID))
                    .url(s5)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()){
                String responseData = response.body().string();
                Gson gson = new Gson();
                List<LostThingsInfo> lostThingsInfo = gson.fromJson(responseData, new TypeToken<List<LostThingsInfo>>(){}.getType());
                return lostThingsInfo;
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
