package misaka.nemesiss.com.findlostthings.Services.Thing;

import android.util.Log;
import android.util.SparseArray;
import androidx.annotation.Nullable;
import misaka.nemesiss.com.findlostthings.Model.LostThingDetail;
import misaka.nemesiss.com.findlostthings.Model.LostThingsCategory;
import misaka.nemesiss.com.findlostthings.Model.Response.LostThingsCategoryPartition;
import misaka.nemesiss.com.findlostthings.Model.SchoolInfo;
import misaka.nemesiss.com.findlostthings.Tasks.GetLostThingsCategoryAsyncTask;
import misaka.nemesiss.com.findlostthings.Tasks.GetLostThingsCategoryPartitionTask;
import misaka.nemesiss.com.findlostthings.Tasks.GetSupportSchoolsTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class ThingServices
{
    private SparseArray<SchoolInfo> Schools;
    private SparseArray<LostThingsCategory> ThingCategory;
    private SparseArray<SparseArray<LostThingDetail>> ThingDetails;
    private List<SchoolInfo> OriginalSchools;
    private List<LostThingsCategory> OriginalThingCategory;

    public ThingServices()
    {
        Schools = new SparseArray<>();
        ThingCategory = new SparseArray<>();
        ThingDetails = new SparseArray<>();
    }

    public void ReloadSchoolList(@Nullable Runnable callback)
    {
        new GetSupportSchoolsTask(TaskRet -> {
            if(TaskRet!=null && TaskRet.getStatusCode() == 0)
            {
                OriginalSchools = TaskRet.getSupportSchools();

                for (SchoolInfo sc : TaskRet.getSupportSchools())
                {
                    Schools.append(sc.getId(),sc);
                }
                Log.d("ThingServices","成功加载全部支持的学校列表.");
                if (callback != null)
                {
                    callback.run();
                }
            }
        }).execute();
    }

    public void ReloadThingCategory(@Nullable Runnable callback)
    {
        new GetLostThingsCategoryAsyncTask(TaskRet -> {
            if(TaskRet!=null && TaskRet.getStatusCode() == 0)
            {
                OriginalThingCategory = TaskRet.getCategoryList();

                for (LostThingsCategory c : TaskRet.getCategoryList())
                {
                    ThingCategory.append(c.getId(),c);
                }
                Log.d("ThingServices","成功加载全部的物品列表.");
                if (callback != null)
                {
                    callback.run();
                }
            }
        }).execute();
    }

    public void ReloadAllThingDetail(@Nullable Runnable callback)
    {
        new GetLostThingsCategoryPartitionTask(TaskRet -> {
            if(AppUtils.CommonResponseOK(TaskRet)) {
                List<LostThingDetail> detailList = TaskRet.getCategoryDetails();

                for (LostThingDetail d : detailList) {
                    int id = d.getCategoryId();
                    SparseArray<LostThingDetail> innerList = ThingDetails.get(id,null);
                    if(innerList == null) {
                        innerList = new SparseArray<>();
                        ThingDetails.put(id,innerList);
                    }
                    innerList.put(d.getId(),d);
                }
                if (callback != null) {
                    // 告知整个任务执行完成。
                    callback.run();
                }
            }
        }).execute();
    }

    public SparseArray<SchoolInfo> getSchools()
    {
        return Schools;
    }

    public SparseArray<LostThingsCategory> getThingCategory()
    {
        return ThingCategory;
    }

    public SparseArray<SparseArray<LostThingDetail>> getThingDetails() {
        return ThingDetails;
    }

    public List<LostThingsCategory> getOriginalThingCategory() {
        return OriginalThingCategory;
    }

    public List<SchoolInfo> getOriginalSchools() {
        return OriginalSchools;
    }
}
