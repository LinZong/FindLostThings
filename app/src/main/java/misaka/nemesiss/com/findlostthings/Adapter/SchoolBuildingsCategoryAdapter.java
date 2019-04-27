package misaka.nemesiss.com.findlostthings.Adapter;

import android.content.Context;
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter;
import misaka.nemesiss.com.findlostthings.Model.MySchoolBuildings;

import java.util.List;

public class SchoolBuildingsCategoryAdapter extends MaterialSpinnerAdapter<MySchoolBuildings>
{
    public SchoolBuildingsCategoryAdapter(Context context, List<MySchoolBuildings> items)
    {
        super(context, items);
    }
}
