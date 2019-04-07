package misaka.nemesiss.com.findlostthings.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class FindLostThingsActivity extends AppCompatActivity
{
    private static ArrayList<Activity> activities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activities.add(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        activities.remove(this);
    }

    public static ArrayList<Activity> GetAllActivities()
    {
        return activities;
    }

    public static void FinishAllActivities()
    {
        if(activities!=null && activities.size()>0)
        {
            for(Activity activity : activities)
            {
                if(!activity.isFinishing())
                {
                    activity.finish();
                }
            }
        }
    }
}
