package misaka.nemesiss.com.findlostthings.Tasks;

import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Response.LostThingsCategoryPartition;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

public class GetLostThingsCategoryPartitionTask extends CustomPostExecuteAsyncTask<Integer, Void, LostThingsCategoryPartition>{
    private OkHttpClient okHttpClient;
    public GetLostThingsCategoryPartitionTask(TaskPostExecuteWrapper<LostThingsCategoryPartition> DoInPostExecute) {
        super(DoInPostExecute);
    }

    @Override
    protected LostThingsCategoryPartition doInBackground(Integer... IDs) {
        try {
            String VariedUrl =(APIDocs.FullLostThingsCategoryPartition + IDs[0]);
            Request request = new Request.Builder()
                    .url(VariedUrl)
                    .build();
            Response response2 = okHttpClient.newCall(request).execute();
            if (response2.isSuccessful()){
                String responseData2 = response2.body().string();
                Gson gson = new Gson();
                LostThingsCategoryPartition lostThingsCategoryPartition = gson.fromJson(responseData2, misaka.nemesiss.com.findlostthings.Model.Response.LostThingsCategoryPartition.class);
                return  lostThingsCategoryPartition;
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