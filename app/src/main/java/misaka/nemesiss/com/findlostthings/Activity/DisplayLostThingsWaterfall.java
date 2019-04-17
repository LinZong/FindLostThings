package misaka.nemesiss.com.findlostthings.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.User.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Services.User.LostThingsInfoAdapter;
import misaka.nemesiss.com.findlostthings.Services.User.MySchoolBuildings;
import misaka.nemesiss.com.findlostthings.Services.User.WaterfallThingsInfo;
import misaka.nemesiss.com.findlostthings.Tasks.GetSchoolBuildingsTask;
import misaka.nemesiss.com.findlostthings.Tasks.WaterfallThingsInfoTask;

import java.util.ArrayList;
import java.util.List;

public class DisplayLostThingsWaterfall extends FindLostThingsActivity
{

    private List<LostThingsInfo> LostThingsInfoList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_lost_things_waterfall);
        initLostThingsInfo();
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LostThingsInfoAdapter adapter=new LostThingsInfoAdapter(LostThingsInfoList);
        recyclerView.setAdapter(adapter);
    }
    private void initLostThingsInfo()
    {
        WaterfallThingsInfo waterfallThingsInfo=new WaterfallThingsInfo();
        waterfallThingsInfo.setCount(10);
        waterfallThingsInfo.setHaveFetchedItemCount(0);
        waterfallThingsInfo.setEndItemId("[Guid(\"EB41C40D-C17E-4641-9524-BD543BC95042\")]");
            new WaterfallThingsInfoTask(TaskRet -> {
                for (LostThingsInfo sb :TaskRet )
                {
                   LostThingsInfoList.add(sb);
                }
            }).execute(waterfallThingsInfo);
    }
}
