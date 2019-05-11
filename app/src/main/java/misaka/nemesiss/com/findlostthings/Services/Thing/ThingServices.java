package misaka.nemesiss.com.findlostthings.Services.Thing;

import android.util.Log;
import android.util.SparseArray;
import androidx.annotation.Nullable;
import misaka.nemesiss.com.findlostthings.Activity.SplashActivity;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.LostThingDetail;
import misaka.nemesiss.com.findlostthings.Model.LostThingsCategory;
import misaka.nemesiss.com.findlostthings.Model.SchoolInfo;
import misaka.nemesiss.com.findlostthings.Tasks.GetLostThingsCategoryAsyncTask;
import misaka.nemesiss.com.findlostthings.Tasks.GetLostThingsCategoryPartitionTask;
import misaka.nemesiss.com.findlostthings.Tasks.GetSupportSchoolsTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import misaka.nemesiss.com.findlostthings.Utils.EventProxy;
import rx.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.List;

public class ThingServices
{
    private SparseArray<SchoolInfo> Schools;
    private SparseArray<LostThingsCategory> ThingCategory;
    private SparseArray<SparseArray<LostThingDetail>> ThingDetails;
    private BehaviorSubject<List<SchoolInfo>> OriginalSchools;
    private BehaviorSubject<List<LostThingsCategory>> OriginalThingCategory;


    public ThingServices()
    {
        Schools = new SparseArray<>();
        ThingCategory = new SparseArray<>();
        ThingDetails = new SparseArray<>();

        OriginalSchools = BehaviorSubject.create(new ArrayList<>());
        OriginalThingCategory = BehaviorSubject.create(new ArrayList<>());

        FindLostThingsApplication.GetCurrentNetworkStatusObservable()
                .subscribe(result -> {
                    if(result && SplashActivity.GotoMainActivityEvent != null) {
                        if(OriginalSchools.getValue().isEmpty()) {
                            ReloadSchoolList(() -> {
                                SplashActivity.GotoMainActivityEvent.tryemit("get_school_name", EventProxy.EventStatus.Finish,"GetSchoolNameFinish");
                            });
                        }
                        if(OriginalThingCategory.getValue().isEmpty()) {
                            ReloadThingCategory(() -> {
                                SplashActivity.GotoMainActivityEvent.tryemit("get_thing_category", EventProxy.EventStatus.Finish,"GetThingCategoryFinish");
                            });
                            ReloadAllThingDetail(() -> {
                                SplashActivity.GotoMainActivityEvent.tryemit("get_thing_detail", EventProxy.EventStatus.Finish,"GetThingDetailFinish");
                            });
                        }
                    }
                });
    }

    public void ReloadSchoolList(@Nullable Runnable callback)
    {
        new GetSupportSchoolsTask(TaskRet -> {
            if(TaskRet!=null && TaskRet.getStatusCode() == 0)
            {
                OriginalSchools.onNext(TaskRet.getSupportSchools());
                Schools.clear();
                for (SchoolInfo sc : TaskRet.getSupportSchools())
                {
                    Schools.append(sc.getId(),sc);
                }
                Log.d("ThingServices","成功加载全部支持的学校列表.");
                //SplashActivity.GotoMainActivityEvent.tryemit("get_school_name", EventProxy.EventStatus.Finish,"GetSchoolNameFinish");
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
                OriginalThingCategory.onNext(TaskRet.getCategoryList());
                for (LostThingsCategory c : TaskRet.getCategoryList())
                {
                    ThingCategory.append(c.getId(),c);
                }
                Log.d("ThingServices","成功加载全部的物品列表.");
                //SplashActivity.GotoMainActivityEvent.tryemit("get_thing_category", EventProxy.EventStatus.Finish,"GetThingCategoryFinish");
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
                //SplashActivity.GotoMainActivityEvent.tryemit("get_thing_detail", EventProxy.EventStatus.Finish,"GetThingDetailFinish");
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
        return OriginalThingCategory.getValue();
    }

    public List<SchoolInfo> getOriginalSchools() {
        return OriginalSchools.getValue();
    }

    public BehaviorSubject<List<SchoolInfo>> getOriginalSchoolsObservable() {
        return OriginalSchools;
    }

    public BehaviorSubject<List<LostThingsCategory>> getThingCategoryObservable() {
        return OriginalThingCategory;
    }
}
