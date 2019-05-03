package misaka.nemesiss.com.findlostthings.Services.Thing;

import android.util.Log;
import android.util.SparseArray;
import androidx.annotation.Nullable;
import misaka.nemesiss.com.findlostthings.Model.LostThingsCategory;
import misaka.nemesiss.com.findlostthings.Model.SchoolInfo;
import misaka.nemesiss.com.findlostthings.Tasks.GetLostThingsCategoryAsyncTask;
import misaka.nemesiss.com.findlostthings.Tasks.GetSupportSchoolsTask;

public class ThingServices
{
    private SparseArray<SchoolInfo> Schools;
    private SparseArray<LostThingsCategory> ThingCategory;

    public ThingServices()
    {
        Schools = new SparseArray<>();
        ThingCategory = new SparseArray<>();

        ReloadSchoolList(null);
        ReloadThingCategory(null);
    }

    public void ReloadSchoolList(@Nullable Runnable callback)
    {
        new GetSupportSchoolsTask(TaskRet -> {
            if(TaskRet!=null && TaskRet.getStatusCode() == 0)
            {
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

    public SparseArray<SchoolInfo> getSchools()
    {
        return Schools;
    }

    public SparseArray<LostThingsCategory> getThingCategory()
    {
        return ThingCategory;
    }
}
