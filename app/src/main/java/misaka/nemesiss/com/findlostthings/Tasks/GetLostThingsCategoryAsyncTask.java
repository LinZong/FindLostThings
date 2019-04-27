package misaka.nemesiss.com.findlostthings.Tasks;

import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.LostThingsCategory;
import misaka.nemesiss.com.findlostthings.Model.Response.LostThingsCategoryResponse;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GetLostThingsCategoryAsyncTask extends CustomPostExecuteAsyncTask<Void, Void, List<LostThingsCategory>> {

    private OkHttpClient okHttpClient;
    public GetLostThingsCategoryAsyncTask(TaskPostExecuteWrapper<List<LostThingsCategory>> DoInPostExecute) {
        super(DoInPostExecute);
    }

    @Override
    protected List<LostThingsCategory> doInBackground(Void... voids) {
        try {
            Request request = new Request.Builder()
                    .url(APIDocs.FullLostThingsCategory)
                    .build();
            Response response1 = okHttpClient.newCall(request).execute();
            if (response1.isSuccessful()){
                String responseData1 = response1.body().string();
                Gson gson = new Gson();
                LostThingsCategoryResponse resp = gson.fromJson(responseData1, LostThingsCategoryResponse.class);
                return resp.getCategoryList();
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